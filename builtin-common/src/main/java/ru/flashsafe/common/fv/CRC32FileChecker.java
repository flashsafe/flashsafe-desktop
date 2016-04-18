package ru.flashsafe.common.fv;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class CRC32FileChecker extends AbstractFileChecker implements FileChecker {

    CRC32FileChecker() {
        super(Algorithms.CRC32);
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
        long hashValue = hashCode.padToLong();
        return Long.toHexString(hashValue);
    }

    @Override
    protected HashFunction getHashFunction() {
        return Hashing.crc32();
    }

}
