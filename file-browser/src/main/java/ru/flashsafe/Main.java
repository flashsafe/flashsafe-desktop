package ru.flashsafe;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.util.ResizeHelper;
import ru.flashsafe.util.SystemTrayUtil;

/**
 * Main class of a FlashSafe Desktop Client
 * 
 * @author alex_xpert
 */
public class Main extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static Locale currentLocale;

    private static ResourceBundle currentResourceBundle;

    public static Stage _stage;

    public static Scene _scene;

    public static ExecutorService es = Executors.newFixedThreadPool(3);

    @Override
    public void start(Stage stage) throws Exception {
        FlashSafeApplication.run();
        es.submit(() -> {
            Platform.runLater(() -> {
                _stage = stage;
                stage.setTitle("Flashsafe");
                stage.setResizable(true);
                stage.setMinWidth(975);
                stage.setMinHeight(650);
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainScene.fxml"));
                    fxmlLoader.setResources(currentResourceBundle);
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root);
                    _scene = scene;
                    stage.setScene(scene);
                    ResizeHelper.addResizeListener(stage);
                } catch (Exception e) {
                    LOGGER.error("Error while building main window", e);
                    e.printStackTrace();
                }
                stage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            });
        });
        SystemTrayUtil.addToSystemTray(currentResourceBundle.getString("connection_established"),
                currentResourceBundle.getString("flashsafe_ready_to_use"));
    }

    @Override
    public void stop() throws Exception {
        FlashSafeApplication.stop();
        SystemTrayUtil.removeFromSystemTray();
        es.shutdown();
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        currentLocale = Locale.getDefault();
        currentResourceBundle = ResourceBundle.getBundle("bundles.interface", currentLocale);
        launch(args);
    }

}
