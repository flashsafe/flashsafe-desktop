package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.file.util.AsyncFileTreeWalker;
import ru.flashsafe.core.file.util.CompositeFileOperation;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationResult;

import com.google.inject.Singleton;

/**
 * The implementation of {@link FileManager} which is used to work with local file system.
 * 
 * @author Andrew
 * 
 */
@Singleton
public class LocalFileManager implements FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileManager.class);
    
    private static final String FILE_SEPARATOR = java.io.File.separator;

    // TODO return the usage of configuration registry (or properties)
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public LocalFileManager() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                LOGGER.debug("Local file manager is shutting down");
                executorService.shutdownNow();
                try {
                    executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    LOGGER.error("Shutdown process finished with an error", e);
                }
            }
        });
    }

    @Override
    public List<FileObject> list(String path) throws FileOperationException {
        List<FileObject> fileObjects = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path currentPath : directoryStream) {
                // TODO fix creating file
                FileObject fileObject = Files.isDirectory(currentPath) ? new LocalDirectory(currentPath) : new LocalFile(
                        currentPath);
                fileObjects.add(fileObject);
            }
            return Collections.unmodifiableList(fileObjects);
        } catch (IOException e) {
            LOGGER.warn("Error while listing directory " + path, e);
            throw new FileOperationException(e);
        }
    }

    @Override
    public File createFile(String path) throws FileOperationException {
        try {
            Path newFile = Files.createFile(Paths.get(path));
            return new LocalFile(newFile);
        } catch (IOException e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public Directory createDirectory(String path) throws FileOperationException {
        try {
            Path newDirectory = Files.createDirectory(Paths.get(path));
            return new LocalDirectory(newDirectory);
        } catch (IOException e) {
            LOGGER.warn("Error while creating directory " + path, e);
            throw new FileOperationException(e);
        }
    }

    @Override
    public FileOperation copy(String fromPath, String toPath) throws FileOperationException {
        Path from = Paths.get(fromPath);
        Path to = Paths.get(sanitizePath(toPath) + FILE_SEPARATOR + from.getFileName());

        FileOperationInfo operationInfo = new FileOperationInfo(fromPath, toPath, from.getFileName().toString());
        CompositeFileOperation fileOperation = new CompositeFileOperation(OperationIDGenerator.nextId(), FileOperationType.COPY,
                operationInfo);

        Future<OperationResult> operationFuture = executorService.submit(new AsyncFileTreeWalker(from, new CopyDirectoryVisitor(
                from, to, fileOperation), fileOperation));
        fileOperation.setOperationFuture(operationFuture);
        return fileOperation;
    }

    @Override
    public FileOperation move(String fromPath, String toPath) throws FileOperationException {
        Path from = Paths.get(fromPath);
        Path to = Paths.get(sanitizePath(toPath) + FILE_SEPARATOR + from.getFileName());

        FileOperationInfo operationInfo = new FileOperationInfo(fromPath, toPath, from.getFileName().toString());
        CompositeFileOperation fileOperation = new CompositeFileOperation(OperationIDGenerator.nextId(), FileOperationType.MOVE,
                operationInfo);

        Future<OperationResult> operationFuture = executorService.submit(new AsyncFileTreeWalker(from, new MoveDirectoryVisitor(
                from, to, fileOperation), fileOperation));
        fileOperation.setOperationFuture(operationFuture);
        return fileOperation;
    }

    @Override
    public FileOperation delete(String path) throws FileOperationException {
        Path pathToDelete = Paths.get(path);
        
        FileOperationInfo operationInfo = new FileOperationInfo(pathToDelete.toString(), null, pathToDelete.getFileName()
                .toString());
        CompositeFileOperation fileOperation = new CompositeFileOperation(OperationIDGenerator.nextId(),
                FileOperationType.DELETE, operationInfo);
        
        Future<OperationResult> operationFuture = executorService.submit(new AsyncFileTreeWalker(pathToDelete,
                new DeleteDirectoryVisitor(fileOperation), fileOperation));
        fileOperation.setOperationFuture(operationFuture);
        return fileOperation;
    }
    
    private static String sanitizePath(String path) {
        return path.replaceFirst(FILE_SEPARATOR + "$", "");
    }

}