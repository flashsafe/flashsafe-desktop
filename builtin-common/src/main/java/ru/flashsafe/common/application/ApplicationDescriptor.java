package ru.flashsafe.common.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class ApplicationDescriptor {

    public static final String DESCRIPTOR_FILE_NAME = "application.ad";
    
    public static final String SYSTEM_NAME_PROPERTY = "id";

    private static final String DIRECTORY_NAME_PROPERTY = "directoryName";

    private static final String FILE_NAME_PROPERTY = "fileName";

    private static final String LAUNCH_ARGS_PROPERTY = "launchArgs";

    private static final String USE_BUILTIN_JRE_PROPERTY = "useBuiltinJRE";
    
    private static final String VERSION_PROPERTY = "version";
    
    private final String systemName;
    
    private final String directoryName;
    
    private final String version;
    
    private final LaunchInformation launchInformation; 
    
    public ApplicationDescriptor(String name, String version, String directoryName, LaunchInformation launchInformation) {
        systemName = name;
        this.version = version;
        this.directoryName = directoryName;
        this.launchInformation = launchInformation;
    }
    
    public String getSystemName() {
        return systemName;
    }

    public String getVersion() {
        return version;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public LaunchInformation getLaunchInformation() {
        return launchInformation;
    }
    
    public void saveTo(Path path) throws FileNotFoundException, IOException {
        Properties descriptorProperties = new Properties();
        descriptorProperties.put(SYSTEM_NAME_PROPERTY, systemName);
        descriptorProperties.put(VERSION_PROPERTY, version);
        descriptorProperties.put(DIRECTORY_NAME_PROPERTY, directoryName);
        descriptorProperties.put(FILE_NAME_PROPERTY, launchInformation.getFileName());
        descriptorProperties.put(LAUNCH_ARGS_PROPERTY, launchInformation.getLaunchArgs());
        descriptorProperties.put(USE_BUILTIN_JRE_PROPERTY, launchInformation.isUseBuiltinJRE());
        try (FileOutputStream descriptorFileStream = new FileOutputStream(path.toFile())) {
            descriptorProperties.storeToXML(descriptorFileStream, "");
        }
    }

    public static ApplicationDescriptor readFrom(Path pathToApplicationDescriptor) {
        Properties descriptorProperties = new Properties();
        try (FileInputStream descriptorFileStream = new FileInputStream(pathToApplicationDescriptor.toFile())) {
            descriptorProperties.loadFromXML(descriptorFileStream);
            String fileName = descriptorProperties.getProperty(FILE_NAME_PROPERTY);
            boolean useBuiltinJRE = Boolean.parseBoolean(descriptorProperties.getProperty(USE_BUILTIN_JRE_PROPERTY));
            String launchArgs = descriptorProperties.getProperty(LAUNCH_ARGS_PROPERTY);
            LaunchInformation launchInformation = new LaunchInformation(fileName, useBuiltinJRE, launchArgs);
            
            String systemName = descriptorProperties.getProperty(SYSTEM_NAME_PROPERTY);
            String version = descriptorProperties.getProperty(VERSION_PROPERTY);
            String directoryName = descriptorProperties.getProperty(DIRECTORY_NAME_PROPERTY);
            return new ApplicationDescriptor(systemName, version, directoryName, launchInformation);
        } catch (InvalidPropertiesFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static class LaunchInformation {

        private final boolean useBuiltinJRE;

        private final String fileName;
        
        private final String launchArgs;
        
        public LaunchInformation(String fileName, boolean useBuiltinJRE, String launchArgs) {
            this.fileName = fileName;
            this.useBuiltinJRE = useBuiltinJRE;
            this.launchArgs = launchArgs;
        }

        public boolean isUseBuiltinJRE() {
            return useBuiltinJRE;
        }

        public String getFileName() {
            return fileName;
        }

        public String getLaunchArgs() {
            return launchArgs;
        }
    }
}
