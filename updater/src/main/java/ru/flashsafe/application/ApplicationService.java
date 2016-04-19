package ru.flashsafe.application;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.application.ApplicationDescriptor;
import ru.flashsafe.common.application.InstalledApplication;
import ru.flashsafe.common.fv.FileChecker;
import ru.flashsafe.common.fv.FileCheckers;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.ssl.SSLService;
import ru.flashsafe.common.util.ArchiveUtils;
import ru.flashsafe.common.version.Version;
import ru.flashsafe.file_loader.DownloadUtil;
import ru.flashsafe.partition.PartitionDetectionException;
import ru.flashsafe.partition.PartitionDetectionException.Type;
import ru.flashsafe.partition.PartitionService;
import ru.flashsafe.updater.UpdateSources;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);

    private final Map<String, InstalledApplication> nameToApplicationMap = new HashMap<>();

    private boolean init = false;

    private final PartitionService partitionService;

    private final ApplicationUtilities applicationUtilities;

    private final RemoteApplicationService remoteApplicationService;

    private final SSLService sslService;

    @Inject
    ApplicationService(PartitionService partitionService, ApplicationUtilities applicationUtilities,
            RemoteApplicationService remoteApplicationService, SSLService sslService) {
        this.partitionService = partitionService;
        this.applicationUtilities = applicationUtilities;
        this.remoteApplicationService = remoteApplicationService;
        this.sslService = sslService;
    }

    public synchronized List<InstalledApplication> getAllInstalledApplications() {
        if (!init) {
            findInstalledApplications();
            init = true;
        }
        return new ArrayList<>(nameToApplicationMap.values());
    }

    public synchronized InstalledApplication getInstalledApplication(String applicationId) {
        requireNonNull(applicationId);
        if (!init) {
            findInstalledApplications();
            init = true;
        }
        InstalledApplication application = nameToApplicationMap.get(applicationId);
        if (application == null) {
            throw new IllegalStateException("Can not find application " + applicationId);
        }
        return application;
    }

    public synchronized Application getApplication(String applicationId) {
        requireNonNull(applicationId);
        return remoteApplicationService.findApplication(applicationId);
    }

    public boolean install(Application application) {
        requireNonNull(application);
        try {
            LOGGER.debug("Creating temp directory for application archive");
            Path pathToApplicationArch = Files.createTempDirectory("fls_apps");
            LOGGER.debug("Temp directory {} was created", pathToApplicationArch);
            Application returnedApp = remoteApplicationService.findApplication(application.getId());
            LOGGER.debug("Downloading application {} to {}", application.getId(), pathToApplicationArch);
            Path applicationArch = downloadApplication(returnedApp, pathToApplicationArch);
            boolean verified = verifyApplicationArchive(applicationArch, returnedApp.getId(), returnedApp.getLatestVersion());
            LOGGER.debug("Verification process finished with the following result: {}", verified);
            LOGGER.debug("Installing application {}", application.getId());
            installToDataPartition(application, applicationArch);
            return true;
        } catch (IOException e) {
            LOGGER.warn("Error while installing application " + application.getName(), e);
            throw new IllegalArgumentException(e);
        }
    }

    // FIXME change to prepare a new version then delete an old one
    public boolean update(InstalledApplication application) {
        requireNonNull(application);
        Path applicationsDirectory = applicationUtilities.getApplicationsDirectoryPath();
        Path applicationRootDirectory = applicationsDirectory.resolve(application.getId());
        LOGGER.debug("Removing the previous version of {}", application.getId());
        FileUtils.deleteQuietly(applicationRootDirectory.toFile());
        return install(application);
    }

    public boolean repair(InstalledApplication application) {
        requireNonNull(application);
        return false;
    }

    public boolean uninstall(InstalledApplication application) {
        requireNonNull(application);
        return false;
    }

    public Process run(InstalledApplication application) throws InterruptedException, ExecutionException {
        requireNonNull(application);
        Path appRootPath = application.getApplicationRootPath();
        ApplicationDescriptor appDescriptor = application.getApplicationDescriptor();
        String parentDirectory = appDescriptor.getDirectoryName();
        Path appFile = appRootPath.resolve(parentDirectory).resolve(appDescriptor.getLaunchInformation().getFileName());
        String[] runArguments = {};
        if (appDescriptor.getLaunchInformation().isUseBuiltinJRE()) {
            runArguments = new String[] { ApplicationInfo.BUILTIN_JRE_PATH, "-jar", appFile.toAbsolutePath().toString() };
        } else {
            runArguments = new String[] { appFile.toAbsolutePath().toString() };
        }
        final String[] applicationArgs = runArguments;
        ExecutorService applicationExecutor = Executors.newSingleThreadExecutor();
        Future<Process> result = applicationExecutor.submit(() -> {
            try {
                Process process = new ProcessBuilder(applicationArgs).start();
                return process;
            } catch (IOException e) {
                LOGGER.error("Error while running " + application.getName(), e);
                throw new IllegalStateException("Error while running application " + application.getName(), e);
            }
        });
        Process processResult = result.get();
        applicationExecutor.shutdownNow();
        return processResult;
    }

    Path downloadApplication(Application application, Path pathToApplicationArch) {
        DownloadUtil downloadUtil = new DownloadUtil(sslService);
        // FIXME use source resolver to switch FLASH_SO/FLASH_RU
        return downloadUtil.download(UpdateSources.FLASH_SO.updateSourceURL + "/application/" + application.getId() + "/package",
                pathToApplicationArch);
    }

    boolean installToDataPartition(Application application, Path applicationArch) {
        try (ZipFile zipFile = new ZipFile(applicationArch.toFile())) {
            ZipEntry applicationDescriptorEntry = zipFile.getEntry(ApplicationDescriptor.DESCRIPTOR_FILE_NAME);
            if (applicationDescriptorEntry != null) {
                try (InputStream appIn = zipFile.getInputStream(applicationDescriptorEntry)) {
                    Properties prop = new Properties();
                    prop.loadFromXML(appIn);
                    String appId = prop.getProperty(ApplicationDescriptor.SYSTEM_NAME_PROPERTY);
                    if (appId != null) {
                        Path applicationsDirectory = applicationUtilities.getApplicationsDirectoryPath();
                        Path applicationRootDirectory = applicationsDirectory.resolve(appId);
                        Files.createDirectories(applicationRootDirectory);
                        ArchiveUtils.unzip(applicationArch, applicationRootDirectory);
                        registerApplication(applicationRootDirectory);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.warn("Error while installing application " + application.getName(), e);
            return false;
        }
    }

    boolean verifyApplicationArchive(Path applicationArch, String applicationId, Version applicationVersion) {
        Checksum expectedChecksum = remoteApplicationService.getChecksum(applicationId, applicationVersion);
        FileChecker fileChecker = FileCheckers.checkerFor(expectedChecksum.getAlgorithm());
        try {
            String actualChecksum = fileChecker.checksum(applicationArch.toFile());
            return actualChecksum.equals(expectedChecksum.getChecksumValue());
        } catch (IOException e) {
            LOGGER.warn("Error while calculating checksum for application " + applicationId, e);
            return false;
        }
    }

    void findInstalledApplications() {
        try {
            Path applicationsDirectory = applicationUtilities.getApplicationsDirectoryPath();
            if (!applicationUtilities.applicationsDirectoryExists()) {
                applicationsDirectory = applicationUtilities.createApplicationsDirectory();
            }
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(applicationsDirectory,
                    new DirectoriesOnlyFilter())) {
                directoryStream.forEach(applicationPath -> registerApplication(applicationPath));
            }
        } catch (PartitionDetectionException e) {
            if (e.getErrorType() == Type.NO_PARTITIONS && e.getAvailableDriveForDataPartition() != null) {
                partitionService.configureDataPartition(e.getAvailableDriveForDataPartition());
            } else {
                throw new IllegalAccessError("Can not get installed applications. Data partition is unavailable!");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while looking for installed applications", e);
        }
    }

    void registerApplication(Path applicationRootDirectory) {
        Path applicationDescriptorFile = applicationRootDirectory.resolve(ApplicationDescriptor.DESCRIPTOR_FILE_NAME);
        if (!Files.exists(applicationDescriptorFile)) {
            return;
        }
        ApplicationDescriptor applicationDescriptor = ApplicationDescriptor.readFrom(applicationDescriptorFile);
        Version appVersion = Version.fromString(applicationDescriptor.getVersion());
        InstalledApplication application = new InstalledApplication(applicationDescriptor.getSystemName(), appVersion,
                applicationRootDirectory, applicationDescriptor);
        nameToApplicationMap.put(applicationDescriptor.getSystemName(), application);
    }

    private static final class DirectoriesOnlyFilter implements DirectoryStream.Filter<Path> {

        @Override
        public boolean accept(Path path) throws IOException {
            return Files.isDirectory(path);
        }
    }
}
