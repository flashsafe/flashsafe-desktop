package ru.flashsafe.core.file.impl;

import java.util.Objects;

import ru.flashsafe.core.file.FileManagementService;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.event.FileManagementEventHandlerProvider;
import ru.flashsafe.core.file.event.FileObjectDuplicationEvent;
import ru.flashsafe.core.file.event.FileObjectDuplicationEventResult;
import ru.flashsafe.core.file.event.FileObjectDuplicationHandler;
import ru.flashsafe.core.file.event.FileObjectSecurityEvent;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class FileManagementServiceImpl implements FileManagementService, FileManagementEventHandlerProvider {

    private final FileManager fileManager;

    private FileObjectDuplicationHandler fileObjectDuplicationHandler;

    private FileObjectSecurityHandler fileObjectSecurityHandler;

    @Inject
    FileManagementServiceImpl(FileManager fileManager) {
        this.fileManager = fileManager;
        fileObjectDuplicationHandler = new DefaultFileObjectDuplicationHandler();
        fileObjectSecurityHandler = new DefaultFileObjectSecurityHandler();
    }

    @Override
    public FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public synchronized void registerFileObjectDuplicationHandler(FileObjectDuplicationHandler handler) {
        fileObjectDuplicationHandler = Objects.requireNonNull(handler);
    }

    @Override
    public synchronized void registerFileObjectSecurityHandler(FileObjectSecurityHandler handler) {
        fileObjectSecurityHandler = Objects.requireNonNull(handler);
    }

    @Override
    public synchronized FileObjectDuplicationHandler getFileObjectDuplicationHandler() {
        return fileObjectDuplicationHandler;
    }

    @Override
    public synchronized FileObjectSecurityHandler getFileObjectSecurityHandler() {
        return fileObjectSecurityHandler;
    }

    private static final class DefaultFileObjectDuplicationHandler implements FileObjectDuplicationHandler {

        @Override
        public FileObjectDuplicationEventResult handle(FileObjectDuplicationEvent event) {
            return new FileObjectDuplicationEventResult(FileObjectDuplicationEventResult.ResultType.COPY_AND_REWRITE, true);
        }
    }

    private static final class DefaultFileObjectSecurityHandler implements FileObjectSecurityHandler {

        @Override
        public FileObjectSecurityEventResult handle(FileObjectSecurityEvent event) {
            return new FileObjectSecurityEventResult(FileObjectSecurityEventResult.ResultType.CANCEL, null);
        }
    }

}
