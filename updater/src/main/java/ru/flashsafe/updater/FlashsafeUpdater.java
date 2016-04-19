package ru.flashsafe.updater;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.updater.controller.MainController;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FlashsafeUpdater extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlashsafeUpdater.class);
    
    private static Locale currentLocale;
    
    private static ResourceBundle messageBundle;
    
    private final Injector injector;
    
    private Label status;

    private ProgressBar progress;
    
    private Stage stage;
    
    public FlashsafeUpdater() {
        injector = Guice.createInjector(new UpdaterModule());
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        MainController mainController = injector.getInstance(MainController.class);
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/updater.fxml"));
        Pane pane = (Pane) loader.load();
        ((Label) pane.getChildren().get(2)).setFont(Font.loadFont(getClass().getResourceAsStream("/font/Lekton-Regular.ttf"), 30));
        status = (Label) pane.getChildren().get(3);
        status.setFont(Font.loadFont(getClass().getResourceAsStream("/font/Jura-Regular.ttf"), 18));
        progress = (ProgressBar) pane.getChildren().get(0);
        primaryStage.setScene(new Scene(pane));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        primaryStage.setTitle("Flashsafe");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                String newVersionMessage = messageBundle.getString("new_version_available");
                Platform.runLater(() -> status.setText(newVersionMessage));
                mainController.runFileBrowser();
                Platform.runLater(() -> stage.close());
                return null;
            }
        };
        new Thread(task).start();
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        initLocaleAndMessageBundle();
        launch(args);
    }
    
    private static void initLocaleAndMessageBundle() {
        currentLocale = Locale.getDefault();
        try {
            messageBundle = ResourceBundle.getBundle("messages.interface", currentLocale);
        } catch (NullPointerException | MissingResourceException e) {
            LOGGER.warn("Unable to load resource bundle for locale:" + currentLocale + " . Locale switched to English", e);
            currentLocale = Locale.ENGLISH;
            messageBundle = ResourceBundle.getBundle("bundles.interface", Locale.ENGLISH);
        }
    }

}
