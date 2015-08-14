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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperationStatus;
import ru.flashsafe.core.file.FileOperationType;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.file.util.AsyncFileTreeWalker;

/**
 * 
 * @author Andrew
 * 
 */
//TOD add logging
public class LocalFileManager implements FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileManager.class);
    
    // TODO return the usage of configuration registry (or properties)
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public LocalFileManager() {
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            
//            @Override
//            public void run() {
//                executor.shutdownNow();
//                try {
//                    executor.awaitTermination(500, TimeUnit.MILLISECONDS);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//            
//        });
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
    public FileOperationStatus copy(String fromPath, String toPath) throws FileOperationException {
        Path from = Paths.get(fromPath);
        Path to;
        if (Files.isRegularFile(from)) {
            to = Paths.get(toPath + from.getFileName());
        } else {
            to = Paths.get(toPath);
        }
        FileOperationStatusComposite operationStatus = new FileOperationStatusComposite(FileOperationType.COPY);
        executorService.execute(new AsyncFileTreeWalker(from, new CopyDirectoryVisitor(from, to, operationStatus),
                operationStatus));
        return operationStatus;
    }

    @Override
    public FileOperationStatus move(String fromPath, String toPath) throws FileOperationException {
        Path from = Paths.get(fromPath);
        Path to;
        if (Files.isRegularFile(from)) {
            to = Paths.get(toPath + from.getFileName());
        } else {
            to = Paths.get(toPath);
        }
        FileOperationStatusComposite operationStatus = new FileOperationStatusComposite(FileOperationType.MOVE);
        executorService.execute(new AsyncFileTreeWalker(from, new MoveDirectoryVisitor(from, to, operationStatus),
                operationStatus));
        return operationStatus;
    }

    @Override
    public FileOperationStatus delete(String path) throws FileOperationException {
        Path pathToDelete = Paths.get(path);
        FileOperationStatusComposite operationStatus = new FileOperationStatusComposite(FileOperationType.DELETE);
        executorService.execute(new AsyncFileTreeWalker(pathToDelete, new DeleteDirectoryVisitor(operationStatus), operationStatus));
        return operationStatus;
    }

}