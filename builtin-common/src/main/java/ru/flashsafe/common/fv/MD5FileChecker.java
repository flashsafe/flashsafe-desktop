package ru.flashsafe.common.fv;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class MD5FileChecker extends AbstractFileChecker implements FileChecker {

    MD5FileChecker() {
        super(Algorithms.MD5);
    }
    
    @Override
    public boolean check(FileInputStream fileInputStream, String checksum) throws IOException {
        return checkInputStream(fileInputStream, checksum);
    }

    @Override
    public Map<String, Boolean> check(Collection<Path> files, Map<String, String> fileToChecksum) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected String convertToStringValue(HashCode hashCode) {
        return hashCode.toString();
    }

    @Override
    protected HashFunction getHashFunction() {
        return Hashing.md5();
    }

}
