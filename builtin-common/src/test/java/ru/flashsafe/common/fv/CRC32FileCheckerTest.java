package ru.flashsafe.common.fv;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ru.flashsafe.common.fv.CRC32FileChecker;

public class CRC32FileCheckerTest {

    private static final String PATH_TO_SAMPLE_FILE = "/ru/flashsafe/common/fv/Root.htm";

    private static final String EXPECTED_CRC32_VALUE = "7a651ac9";

    private CRC32FileChecker fileChecker;

    @Before
    public void before() {
        fileChecker = new CRC32FileChecker();
    }
    
    @Test
    public void checkFile() throws IOException {
        File file = new File(getClass().getResource(PATH_TO_SAMPLE_FILE).getFile());
        boolean actualValue = fileChecker.check(file, EXPECTED_CRC32_VALUE);
        assertTrue(actualValue);
    }

    @Test
    public void checkFileStream() throws IOException {
        File file = new File(getClass().getResource(PATH_TO_SAMPLE_FILE).getFile());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            boolean actualValue = fileChecker.check(fileInputStream, EXPECTED_CRC32_VALUE);
            assertTrue(actualValue);
        }
    }
    
}
