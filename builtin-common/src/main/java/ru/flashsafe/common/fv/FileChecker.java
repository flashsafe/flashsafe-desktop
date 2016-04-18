package ru.flashsafe.common.fv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public interface FileChecker {

    boolean check(File file, String checksum) throws IOException;
    
    boolean check(FileInputStream fileInputStream, String checksum) throws IOException;
    
    Map<String, Boolean> check(Collection<Path> files, Map<String, String> fileToChecksum);
    
    Algorithms getAlgorithm(); 
    
}
