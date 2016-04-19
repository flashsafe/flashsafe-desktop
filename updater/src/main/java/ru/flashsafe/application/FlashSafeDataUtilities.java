package ru.flashsafe.application;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FlashSafeDataUtilities {

    public static final String FLASH_SAFE_DATA_DIRECTORY = ".fls";
    
    public static final String PARTITION_ID_FILE_NAME = ".flsid";
    
    private static final String ATTRIBUTE_HIDDEN = "dos:hidden";
    
    public static boolean dataDirectoryExists(Path dataPartitionRoot) {
        requireNonNull(dataPartitionRoot);
        return directoryExists(dataPartitionRoot.resolve(FLASH_SAFE_DATA_DIRECTORY));
    }

    public static boolean partitonIdentifierFileExists(Path dataPartitionRoot) {
        requireNonNull(dataPartitionRoot);
        Path filePath = dataPartitionRoot.resolve(FLASH_SAFE_DATA_DIRECTORY).resolve(PARTITION_ID_FILE_NAME);
        return Files.exists(filePath);
    }

    public static Path createDataDirectory(Path dataPartitionRoot) throws IOException {
        requireNonNull(dataPartitionRoot);
        Path dataDirectoryPath = Files.createDirectory(dataPartitionRoot.resolve(FLASH_SAFE_DATA_DIRECTORY));
        // set hidden attribute for Windows
        Files.setAttribute(dataDirectoryPath, ATTRIBUTE_HIDDEN, true);
        return dataDirectoryPath;
    }
    
    public static String readPartitonIdentifier(Path dataPartitionRoot) {
        requireNonNull(dataPartitionRoot);
        Path dataDirectory = dataPartitionRoot.resolve(FLASH_SAFE_DATA_DIRECTORY);
        if (!directoryExists(dataDirectory)) {
            throw new IllegalStateException("Can not read partiton identifier from " + dataPartitionRoot
                    + ". Flashsafe data directory does not exist!");
        }
        Path identifierFile = dataDirectory.resolve(PARTITION_ID_FILE_NAME);
        if (!Files.exists(identifierFile)) {
            throw new IllegalStateException("Can not read partiton identifier from " + dataPartitionRoot
                    + ". ID file does not exist!");
        }
        return readIdentifier(identifierFile);
    }
    
    public static void writePartitonIdentifier(Path dataPartitionRoot, String identifier) {
        requireNonNull(dataPartitionRoot);
        requireNonNull(identifier);
        Path dataDirectory = dataPartitionRoot.resolve(FLASH_SAFE_DATA_DIRECTORY);
        if (!directoryExists(dataDirectory)) {
            throw new IllegalStateException("Can not read partiton identifier from " + dataPartitionRoot
                    + ". Flashsafe data directory does not exist!");
        }
        Path identifierFile = dataDirectory.resolve(PARTITION_ID_FILE_NAME);
        writeIdentifier(identifierFile, identifier);
    }
    
    public static boolean dataDirectoryExistsFor(Path rootPath) {
        return directoryExists(rootPath.resolve(FLASH_SAFE_DATA_DIRECTORY));
    }
    
    private static boolean directoryExists(Path directoryPath) {
        return Files.exists(directoryPath) && Files.isDirectory(directoryPath);
    }

    private static void writeIdentifier(Path filePath, String identifier) {
        try {
            Files.deleteIfExists(filePath);
            Files.write(filePath, Collections.singletonList(identifier), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Error while  writing id to file " + filePath);
        }
    }

    private static String readIdentifier(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath, Charset.forName("UTF-8"));
            if (lines.isEmpty() || lines.size() > 1) {
                throw new IllegalStateException("Incorrect file format! ID file " + filePath + "!");
            }
            return lines.iterator().next();
        } catch (IOException e) {
            throw new RuntimeException("Error while reading id from file " + filePath);
        }
    }
}
