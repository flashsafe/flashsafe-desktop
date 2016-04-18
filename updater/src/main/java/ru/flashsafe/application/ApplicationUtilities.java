package ru.flashsafe.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.inject.Inject;

import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.partition.PartitionService;

public class ApplicationUtilities {
    
    private static final String ATTRIBUTE_HIDDEN = "dos:hidden";
    
    private final PartitionService partitionService;

    @Inject
    ApplicationUtilities(PartitionService partitionService) {
        this.partitionService = partitionService;
    }
    
    public boolean applicationsDirectoryExists() {
        Path applicationsDirectory = getApplicationsDirectoryPath();
        return Files.exists(applicationsDirectory) && Files.isDirectory(applicationsDirectory);
    }

    public Path createApplicationsDirectory() throws IOException {
        Path applicationsDirectory = getApplicationsDirectoryPath();
        if (Files.exists(applicationsDirectory) && Files.isDirectory(applicationsDirectory)) {
            // TODO throw an exception
        }
        Path applicationsDirectoryPath = Files.createDirectory(applicationsDirectory);
        // set hidden attribute for Windows
        Files.setAttribute(applicationsDirectory, ATTRIBUTE_HIDDEN, true);
        return applicationsDirectoryPath;
    }

    private Path getApplicationsDirectoryPath() {
        Partition dataPartition = partitionService.lookupDataPartition();
        Path partitionRoot = dataPartition.getPartitionPath();
        return partitionRoot.resolve(FlashSafeDataUtilities.FLASH_SAFE_DATA_DIRECTORY);
    }
    
}
