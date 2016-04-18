package ru.flashsafe.common.fv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.flashsafe.common.fv.MD5FileChecker;

public class MD5FileCheckerTest {

    private static final String PATH_TO_SAMPLE_FILE = "/ru/flashsafe/common/fv/Root.htm";

    private static final String EXPECTED_MD5_VALUE = "c56bfc4ce1f5bf31290d4dd1e28319b0";

    private MD5FileChecker fileChecker;

    @Before
    public void before() {
        fileChecker = new MD5FileChecker();
    }
    
    @Test
    public void checkFile() throws IOException {
        File file = new File(getClass().getResource(PATH_TO_SAMPLE_FILE).getFile());
        boolean actualValue = fileChecker.check(file, EXPECTED_MD5_VALUE);
        assertTrue(actualValue);
    }

    @Test
    public void checkFileStream() throws IOException {
        File file = new File(getClass().getResource(PATH_TO_SAMPLE_FILE).getFile());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            boolean actualValue = fileChecker.check(fileInputStream, EXPECTED_MD5_VALUE);
            assertTrue(actualValue);
        }
    }

}
