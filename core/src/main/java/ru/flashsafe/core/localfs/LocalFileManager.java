package ru.flashsafe.core.localfs;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;

/**
 * 
 * @author Andrew
 *
 */
public class LocalFileManager implements FileManager {

    @Override
    public List<FileObject> list(String path) {
        List<FileObject> fileObjects = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path currentPath : directoryStream) {
                FileObject fileObject = Files.isDirectory(currentPath) ? new LocalDirectory(currentPath) : new LocalFile(
                        currentPath);
                fileObjects.add(fileObject);
            }
        } catch (IOException ex) {
            // TODO fix
        }
        return Collections.unmodifiableList(fileObjects);
    }

    @Override
    public File createFile(String path) {
        try {
            Path newFile = Files.createFile(Paths.get(path));
            return new LocalFile(newFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Directory createDirectory(String path) {
        try {
            Path newDirectory = Files.createDirectory(Paths.get(path));
            return new LocalDirectory(newDirectory);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void copy(String fromPath, String toPath) {
        try {
            Files.walkFileTree(Paths.get(fromPath), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new CopyDirectoryVisitor(Paths.get(fromPath), Paths.get(toPath)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void move(String fromPath, String toPath) {
        try {
            Files.walkFileTree(Paths.get(fromPath), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new MoveDirectoryVisitor(Paths.get(fromPath), Paths.get(toPath)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public void delete(String path) {
        try {
            Files.walkFileTree(Paths.get(path), new DeleteDirectoryVisitor());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}