package ru.flashsafe.core.file.util;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

import ru.flashsafe.core.file.impl.FileOperationStatusComposite;
import ru.flashsafe.core.operation.OperationState;

public class AsyncFileTreeWalker implements Runnable {

    private final Path start;

    private final FileVisitor<Path> visitor;

    private final FileOperationStatusComposite operationStatus;

    public AsyncFileTreeWalker(Path start, FileVisitor<Path> visitor, FileOperationStatusComposite operationStatus) {
        this.start = start;
        this.visitor = visitor;
        this.operationStatus = operationStatus;
    }

    @Override
    public void run() {
        try {
            operationStatus.setTotalBytesToProcess(FileUtils.countSizeForPath(start));
            operationStatus.setState(OperationState.IN_PROGRESS);
            Files.walkFileTree(start, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, visitor);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO mark operation as failed
        } finally {
            operationStatus.setState(OperationState.FINISHED);
        }
    }
}