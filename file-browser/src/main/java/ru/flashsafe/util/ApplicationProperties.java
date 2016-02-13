package ru.flashsafe.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationProperties.class);
    
    private static final Properties APPLICATION_PROPERTIES = new Properties();
    
    static {
        try {
            APPLICATION_PROPERTIES.loadFromXML(new FileInputStream("./flashsafe.xml"));
        } catch (IOException e) {
            LOGGER.warn("Error while reading properties from file", e);
            APPLICATION_PROPERTIES.setProperty("id", "1");
            APPLICATION_PROPERTIES.setProperty("secret", "open123458");
            APPLICATION_PROPERTIES.setProperty("language", Locale.getDefault().getLanguage());
            try {
                APPLICATION_PROPERTIES.storeToXML(new FileOutputStream("./flashsafe.xml"), "Flashsafe Properties", "UTF-8");
            } catch(IOException ex) {
                LOGGER.error("Error while writing properties to file", e);
            }
        }
    }
    
    private ApplicationProperties() {
    }
    
    public static String secret() {
        return APPLICATION_PROPERTIES.getProperty("secret");
    }
    
    public static String userId() {
        return APPLICATION_PROPERTIES.getProperty("id");
    }
    
    public static String languageTag() {
        return APPLICATION_PROPERTIES.getProperty("language");
    }
    
}
