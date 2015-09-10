package ru.flashsafe.core.file.util;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;

/**
 * Provides an ability to walk thru File Tree in separate thread.
 * 
 * @author Andrew
 *
 */
// FIXME Runtime exceptions and threads - think about call Future.get() on
// operation
public class AsyncFileTreeWalker implements Callable<OperationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncFileTreeWalker.class);

    private final Path start;

    private final FileVisitor<Path> visitor;

    private final CompositeFileOperation operation;

    /**
     * 
     * @param start
     *            start point of TreeWalker execution
     * @param visitor
     *            visitor to use
     * @param operation
     *            operation
     */
    public AsyncFileTreeWalker(Path start, FileVisitor<Path> visitor, CompositeFileOperation operation) {
        this.start = requireNonNull(start);
        this.visitor = requireNonNull(visitor);
        this.operation = requireNonNull(operation);
        operation.setState(OperationState.PLANNED);
    }

    @Override
    public OperationResult call() throws Exception {
        try {
            operation.setState(OperationState.IN_PROGRESS);
            operation.setTotalBytes(FileUtils.countSizeForPath(start));
            Files.walkFileTree(start, Collections.emptySet(), Integer.MAX_VALUE, visitor);
            if (operation.getResult() == OperationResult.UNKNOWN) {
                operation.setResult(OperationResult.SUCCESS);
            }
            return operation.getResult();
        } catch (IOException e) {
            operation.setResult(OperationResult.ERROR);
            LOGGER.warn("Error while walking file tree with root " + start, e);
            return OperationResult.ERROR;
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected error while walking file tree with root " + start, e);
            return OperationResult.ERROR;
        } finally {
            operation.setState(OperationState.FINISHED);
        }
    }
}
