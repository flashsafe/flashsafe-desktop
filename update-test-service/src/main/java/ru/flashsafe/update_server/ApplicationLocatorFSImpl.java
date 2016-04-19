package ru.flashsafe.update_server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.Algorithms;
import ru.flashsafe.common.fv.FileChecker;
import ru.flashsafe.common.fv.FileCheckers;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;
import ru.flashsafe.update_server.application.ApplicationEntry;

@Component
public class ApplicationLocatorFSImpl implements ApplicationLocator {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLocatorFSImpl.class);

    private final Map<String, ApplicationEntry> idToApplication = new ConcurrentHashMap<>();

    private final String CURRENT_PATH = ".";

    private Path currentDirectory;

    @PostConstruct
    public void init() {
        currentDirectory = Paths.get(CURRENT_PATH);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            stream.forEach(path -> {
                if (Files.isRegularFile(path) && path.toString().endsWith(".zip")) {
                    ApplicationEntry newEntry = buildApplicationEntry(path);
                    if (newEntry != null) {
                        String applicationId = newEntry.getApplicationId();
                        ApplicationEntry previousEntry = idToApplication.putIfAbsent(applicationId, newEntry);
                        if (previousEntry == null) {
                            logger.info("Application {} was registered.", applicationId);
                        } else {
                            previousEntry.registerVersion(newEntry.getApplication(), path.toString(), newEntry.getChecksum());
                            logger.info("Application {} was updated.", applicationId);
                        }
                    }
                }
            });
        } catch (IOException e) {
            logger.warn("Error while scanning for applications", e);
        }
    }

    @Override
    public Application findApplication(String id) {
        ApplicationEntry appEntry = idToApplication.get(id);
        return appEntry.getApplication();
    }

    @Override
    public Set<Application> findAllApplication() {
        Set<Application> allApplications = new HashSet<>();
        idToApplication.values().forEach(applicationEntry -> allApplications.add(applicationEntry.getApplication()));
        return allApplications;
    }

    @Override
    public InputStream getApplicationPackageStream(String id) throws IOException {
        ApplicationEntry appEntry = idToApplication.get(id);
        if (appEntry != null) {
            return new FileInputStream(appEntry.getResourceId());
        }
        return null;
    }
    
    @Override
    public InputStream getApplicationPackageStream(String id, Version version) throws IOException {
        ApplicationEntry appEntry = idToApplication.get(id);
        if (appEntry != null) {
            return new FileInputStream(appEntry.getResourceId(version));
        }
        return null;
    }

    @Override
    public Checksum checksum(String id, Version version) {
        ApplicationEntry application = idToApplication.get(id);
        return application.getChecksum(version);
    }

    @Override
    public Checksum checksum(String id) {
        ApplicationEntry application = idToApplication.get(id);
        return checksum(id, application.getLatestVersion());
    }

    private static ApplicationEntry buildApplicationEntry(Path path) {
        String appId = null;
        Version applicationVersion = null;
        Checksum appChecksum = null;
        try (ZipFile zipFile = new ZipFile(path.toFile())) {
            ZipEntry applicationDescriptorEntry = zipFile.getEntry("application.ad");
            if (applicationDescriptorEntry != null) {
                try (InputStream appIn = zipFile.getInputStream(applicationDescriptorEntry)) {
                    Properties prop = new Properties();
                    prop.loadFromXML(appIn);
                    appId = prop.getProperty("id");
                    if (appId != null) {
                        applicationVersion = buildVersionFromName(path.getFileName().toString());
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Error while processing application entry " + path.getFileName().toString() + ". Skipped.", e);
        }
        if (appId != null && applicationVersion != null) {
            try {
                appChecksum = calculateChecksum(path);
                return new ApplicationEntry(new Application(appId, "", applicationVersion), path.toString(), appChecksum);
            } catch (IOException e) {
                logger.warn("Error while calculation checksum for application entry " + path.getFileName().toString()
                        + ". Skipped.", e);
            }
        }
        return null;
    }
    
    private static Checksum calculateChecksum(Path filePath) throws IOException {
        FileChecker fileChecker = FileCheckers.checkerFor(Algorithms.MD5);
        String checksumString = fileChecker.checksum(filePath.toFile());
        return new Checksum(Algorithms.MD5, checksumString);
    }
    
    private static Version buildVersionFromName(String applicationName) {
        String versionString = applicationName.substring(applicationName.lastIndexOf("-") + 1, applicationName.lastIndexOf(".zip"));
        String[] vesrionElements = versionString.split("\\.", 3);
        if (vesrionElements.length > 2) {
            return new Version(Integer.valueOf(vesrionElements[0]), Integer.valueOf(vesrionElements[1]),
                    Integer.valueOf(vesrionElements[2]));
        } else {
            return new Version(Integer.valueOf(vesrionElements[0]), Integer.valueOf(vesrionElements[1]));
        }
    }

}
