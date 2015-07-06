package ru.flashsafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of a FlashSafe Desktop Client
 * @author alex_xpert
 */
public class Main extends Application {
    private static final Logger log = LogManager.getLogger(Main.class);
    public static Stage _stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        _stage = stage;
        
        Parent root = FXMLLoader.load(getClass().getResource("fxml/MainScene.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.getIcons().add(new Image(getClass().getResource("img/logo.png").toExternalForm()));
        stage.setTitle("FlashSafe");
        stage.setResizable(false);
        stage.setWidth(900);
        stage.setHeight(675);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
