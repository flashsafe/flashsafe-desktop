package ru.flashsafe.application;

import java.nio.file.Paths;

public class ApplicationInfo {
    
    private static final String CURRENT_DIRECTORY_SYMBOL = ".";

    private static final String RUN_DIRECTORY;
    
    public static final String APPLICATIONS_DIRECTORY = "apps";

    static {
        RUN_DIRECTORY = Paths.get(CURRENT_DIRECTORY_SYMBOL).toAbsolutePath().normalize().toString();
    }

    private ApplicationInfo() {
    }

    public static String getRunDirectory() {
        return RUN_DIRECTORY;
    }

}
