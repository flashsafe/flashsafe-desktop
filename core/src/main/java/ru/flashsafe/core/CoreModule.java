package ru.flashsafe.core;

import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.event.FlashSafeEventServiceImpl;
import ru.flashsafe.core.file.FileManagementService;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileUtility;
import ru.flashsafe.core.file.event.FileManagementEventHandlerProvider;
import ru.flashsafe.core.file.impl.DefaultFileUtility;
import ru.flashsafe.core.file.impl.FileManagementServiceImpl;
import ru.flashsafe.core.file.impl.UnifiedFileManager;
import ru.flashsafe.core.old.storage.DefaultFlashSafeStorageService;
import ru.flashsafe.core.storage.FlashSafeStorageService;

import com.google.inject.AbstractModule;

/**
 * Guice's configuration for module Core.
 * 
 * @author Andrew
 *
 */
public class CoreModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileManager.class).to(UnifiedFileManager.class);
        bind(FileUtility.class).to(DefaultFileUtility.class);
        bind(FlashSafeStorageService.class).to(DefaultFlashSafeStorageService.class);
        bind(FlashSafeEventService.class).to(FlashSafeEventServiceImpl.class);
        bind(FileManagementService.class).to(FileManagementServiceImpl.class);
        bind(FileManagementEventHandlerProvider.class).to(FileManagementServiceImpl.class);
    }

}
