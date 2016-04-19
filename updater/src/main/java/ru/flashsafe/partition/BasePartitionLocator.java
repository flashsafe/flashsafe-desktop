package ru.flashsafe.partition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.samuelcampos.usbdrivedectector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedectector.USBStorageDevice;
import ru.flashsafe.application.ApplicationInfo;
import ru.flashsafe.application.FlashSafeDataUtilities;
import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.partition.PartitionDetectionException.Type;

public abstract class BasePartitionLocator implements PartitionLocator {

    private final USBDeviceDetectorManager usbDriveDetector = new USBDeviceDetectorManager();
    
    private String systemPartitionRootPath;
    
    public BasePartitionLocator() {
    }
    
    @Override
    public Partition lookup(Partition.Type partitionType) throws PartitionDetectionException {
        if (Partition.Type.USER_DATA == partitionType) {
            return lookupDataPartition();
        } else if (Partition.Type.FLASHSAFE_SYSTEM == partitionType) {
            return lookupSystemPartition();
        }
        throw new IllegalArgumentException("Unexpected partition type: " + partitionType);
    }

    protected final synchronized Partition lookupSystemPartition() throws PartitionDetectionException {
        if (systemPartitionRootPath == null) {
            String runDirectory = ApplicationInfo.getRunDirectory();
            systemPartitionRootPath = findRootPathForRunDirectory(runDirectory);
        }
        return new FlashsafeSystemPartition(systemPartitionRootPath);
    }

    protected Partition lookupDataPartition() throws PartitionDetectionException {
        List<USBStorageDevice> storageDevices = usbDriveDetector.getRemovableDevices();
        Map<Path, String> pathToIdentifier = new HashMap<>();
        List<Path> availableRemovableDrives = new ArrayList<>();
        for (USBStorageDevice storageDevice : storageDevices) {
            Path dataPartitionRoot = storageDevice.getRootDirectory().toPath();
            availableRemovableDrives.add(dataPartitionRoot);
            if (FlashSafeDataUtilities.partitonIdentifierFileExists(dataPartitionRoot)) {
                String identifier = FlashSafeDataUtilities.readPartitonIdentifier(dataPartitionRoot);
                if (verifyPartition(identifier, dataPartitionRoot)) {
                    pathToIdentifier.put(dataPartitionRoot, identifier);
                }
            }
        }
        if (pathToIdentifier.isEmpty()) {
            Path availableRemovableDrive = null;
            if (availableRemovableDrives.size() == 1) {
                availableRemovableDrive = availableRemovableDrives.get(0);
            }
            throw new PartitionDetectionException(Type.NO_PARTITIONS, availableRemovableDrive);
        } else if (pathToIdentifier.size() > 1) {
            throw new PartitionDetectionException(Type.TOO_MANY_PARTITIONS);
        }
        Path partitionRootPath = pathToIdentifier.keySet().iterator().next();
        String partitionRootPathString = partitionRootPath.toAbsolutePath().normalize().toString();
        return new FlashsafeDataPartition(partitionRootPathString);
    }
    
    @Override
    public Partition createDataPartition(Path path) throws PartitionCreationException {
        try {
            Path dataDirectory = FlashSafeDataUtilities.createDataDirectory(path);
            //TODO write partition identifier
            FlashSafeDataUtilities.writePartitonIdentifier(path, "FIXED-IDENTIFIER");
            return new FlashsafeDataPartition(path.toAbsolutePath().normalize().toString());
        } catch (IOException e) {
            throw new PartitionCreationException(e);
        }
    }
    
    /**
     * 
     * @param identifier
     * @param dataPartitionRoot
     * @return true if the partition passed verification, false otherwise
     */
    protected abstract boolean verifyPartition(String identifier, Path dataPartitionRoot);

    private static String findRootPathForRunDirectory(String runDirectory) {
        File[] availableFilesystemRoots = File.listRoots();
        try {
            for (File currentRoot : availableFilesystemRoots) {
                if (runDirectory.startsWith(currentRoot.getCanonicalPath())) {
                    return currentRoot.getAbsolutePath();
                }
            }
            // TODO fix message
            throw new RuntimeException("Can not find root path for " + runDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Error while looking root path for " + runDirectory);
        }
    }

}
