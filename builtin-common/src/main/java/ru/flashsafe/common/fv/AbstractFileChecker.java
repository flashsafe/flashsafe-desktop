package ru.flashsafe.common.fv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public abstract class AbstractFileChecker implements FileChecker {

    private final Algorithms algorithm;

    AbstractFileChecker(Algorithms algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public boolean check(File file, String checksum) throws IOException {
        String actualChecksum = checksum(file);
        return checksum.equals(actualChecksum);
    }
    
    @Override
    public String checksum(File file) throws IOException {
        HashCode hashCode = Files.hash(file, getHashFunction());
        return convertToStringValue(hashCode);
    }

    protected boolean checkInputStream(InputStream inputStream, String checksum) throws IOException {
        try (HashingInputStream hashingInputStream = new HashingInputStream(getHashFunction(), inputStream)) {
            ByteStreams.copy(hashingInputStream, ByteStreams.nullOutputStream());
            HashCode hashCode = hashingInputStream.hash();
            return checksum.equals(convertToStringValue(hashCode));
        }
    }

    protected abstract HashFunction getHashFunction();

    protected abstract String convertToStringValue(HashCode hashCode);

    @Override
    public final Algorithms getAlgorithm() {
        return algorithm;
    }

}
