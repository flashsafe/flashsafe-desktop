package ru.flashsafe.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeRegistry;
import ru.flashsafe.core.FlashSafeSystem;
import ru.flashsafe.core.file.FileManagementService;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileUtility;

import com.google.inject.Inject;

public class DefaultFlashSafeSystem implements FlashSafeSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFlashSafeSystem.class);

    private final FileManagementService fileManagementService;

    private final FileUtility fileUtility;
    
    @Inject
    public DefaultFlashSafeSystem(FileManagementService fileManagementService, FileUtility fileUtility) {
        this.fileManagementService = fileManagementService;
        this.fileUtility = fileUtility;
        LOGGER.info("Using {} as an implementation of {}", DefaultFlashSafeSystem.class, FlashSafeSystem.class);
    }

    @Override
    public String storageAddress() {
        return FlashSafeRegistry.getStorageAddress();
    }

    @Override
    public FileManagementService fileManagementService() {
        return fileManagementService;
    }
    
    @Override
    public FileManager fileManager() {
        return fileManagementService.getFileManager();
    }

    @Override
    public FileUtility fileUtility() {
        return fileUtility;
    }

}
