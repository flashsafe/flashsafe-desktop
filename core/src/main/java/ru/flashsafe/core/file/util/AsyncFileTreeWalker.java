package ru.flashsafe.core.file.util;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import static java.util.Objects.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class AsyncFileTreeWalker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncFileTreeWalker.class);
    
    private final Path start;

    private final FileVisitor<Path> visitor;

    private final FileOperationStatusComposite operationStatus;

    public AsyncFileTreeWalker(Path start, FileVisitor<Path> visitor, FileOperationStatusComposite operationStatus) {
        this.start = requireNonNull(start);
        this.visitor = requireNonNull(visitor);
        this.operationStatus = requireNonNull(operationStatus);
    }

    @Override
    public void run() {
        try {
            operationStatus.setTotalBytesToProcess(FileUtils.countSizeForPath(start));
            operationStatus.setState(OperationState.IN_PROGRESS);
            Files.walkFileTree(start, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
            operationStatus.setResult(OperationResult.SUCCESS);
        } catch (IOException e) {
            operationStatus.setResult(OperationResult.ERROR);
            LOGGER.warn("Error while walking file tree with root " + start, e);
        } finally {
            operationStatus.setState(OperationState.FINISHED);
        }
    }
}
