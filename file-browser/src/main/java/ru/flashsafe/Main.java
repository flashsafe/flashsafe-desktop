package ru.flashsafe;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.ImageIcon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of a FlashSafe Desktop Client
 * @author alex_xpert
 */
public class Main extends Application {
    private static final Logger log = LogManager.getLogger(Main.class);
    public static Stage _stage;
    public static Scene _scene;
    private SystemTray tray;
    private TrayIcon ticon;
    public static ExecutorService es = Executors.newFixedThreadPool(3);

    @Override
    public void start(Stage stage) throws Exception {
        es.submit(new Runnable(){
                public void run(){
                        Platform.runLater(new Runnable() {
                                public void run() {
                                    _stage = stage;
                                    stage.setTitle("Flashsafe");
                                    stage.setResizable(true);
                                    stage.setMinWidth(975);
                                    stage.setMinHeight(650);
                                    try {
                                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));
                                        Scene scene = new Scene(root);
                                        _scene = scene;
                                        stage.setScene(scene);
                                    } catch(Exception e) {
                                        log.error(e);
                                        e.printStackTrace();
                                    }
                                    stage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
                                    stage.initStyle(StageStyle.TRANSPARENT);
                                    stage.show();
                                }
                        });
                }
        });

        try {
            if(SystemTray.isSupported()) {
                tray = SystemTray.getSystemTray();
                ticon = new TrayIcon(new ImageIcon(getClass().getResource("/img/logo1.png")).getImage(), "Flashsafe");
                tray.add(ticon);
                ticon.displayMessage("Соединение установлено", "Успешно установлено соединение с облаком. Ваша флешка готова к работе.", TrayIcon.MessageType.INFO);
            }
        } catch(AWTException e) {
            log.error(e);
        }
    }

    @Override
    public void stop() throws Exception {
        tray.remove(ticon);
        es.shutdown();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
