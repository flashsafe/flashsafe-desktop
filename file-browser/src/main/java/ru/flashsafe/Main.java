package ru.flashsafe;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashsafe.core.FlashSafeConfiguration;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.util.ApplicationProperties;

/**
 * Main class of a FlashSafe Desktop Client
 * @author Alexander Krysin
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static ExecutorService es = Executors.newFixedThreadPool(3);
    
    public static QApplication app;
    
    private static FlashSafeConfiguration createConfiguration() {
        return FlashSafeConfiguration.builder().registerUserId(ApplicationProperties.userId())
                .registerSecret(ApplicationProperties.secret()).build();
    }
    
    public static void main(String[] args) throws ClassNotFoundException, FileOperationException {
        app = new QApplication(args);
        
        QDialog splash = new QDialog();
        SplashUI splashUI = new SplashUI();
        splashUI.setupUi(splash);
        splash.show();
        splashUI.start();
        
        app.exec();
    }

}
