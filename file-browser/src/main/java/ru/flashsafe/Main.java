package ru.flashsafe;

import com.sun.javafx.util.Utils;
import static org.pkcs11.jacknji11.CK_SESSION_INFO.CKF_RW_SESSION;
import static org.pkcs11.jacknji11.CK_SESSION_INFO.CKF_SERIAL_SESSION;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.pkcs11.jacknji11.CE;
import org.pkcs11.jacknji11.CKR;
import org.pkcs11.jacknji11.CKRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.FlashSafeConfiguration;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.util.ApplicationProperties;
import ru.flashsafe.util.SystemTrayUtil;
import ru.flashsafe.view.CreatePathPane;
import ru.flashsafe.view.EnterPincodePane;
import ru.flashsafe.view.MainPane;

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
    
    //public static FXMLLoader fxmlLoader = new FXMLLoader();
    
    private static long session;

    //private static String PIN = "00000000";
    
    private boolean tokenInit = false;
    
    // Fix transparent windows on Linux OS
    static {
        System.setProperty("javafx.allowTransparentStage", String.valueOf(true));
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        //if(Utils.isWindows() && !tokenInit) {
            //initToken(stage);
        //} else {
            FlashSafeConfiguration configuration = createConfiguration();
            FlashSafeApplication.setConfiguration(configuration);
            FlashSafeApplication.run();
            es.submit(() -> {
                Platform.runLater(() -> {
                    _stage = stage;
                    stage.setTitle("Flashsafe");
                    stage.setMinWidth(975);
                    stage.setMinHeight(650);
                    if(!Utils.isUnix()) {
                        stage.initStyle(StageStyle.TRANSPARENT);
                    }
                    stage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
                    try {
                        //fxmlLoader.setLocation(getClass().getResource("/fxml/MainScene.fxml"));
                        //fxmlLoader.setResources(currentResourceBundle);
                        //Parent root = fxmlLoader.load();
                        CreatePathPane pathnameDialog = new CreatePathPane(currentResourceBundle);
                        EnterPincodePane pincodeDialog = new EnterPincodePane(currentResourceBundle);
                        MainPane mainPane = new MainPane(currentResourceBundle, FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX, stage, pathnameDialog, pincodeDialog);
                        Scene scene = new Scene(/*root*/mainPane, Color.TRANSPARENT);
                        _scene = scene;
                        stage.setScene(scene);
                        //ResizeHelper.addResizeListener(stage);
                    } catch (Exception e) {
                        LOGGER.error("Error while building main window", e);
                        e.printStackTrace();
                    }
                    stage.show();
                });
            });
        //}
        
//        SystemTrayUtil.addToSystemTray(currentResourceBundle.getString("connection_established"),
//                currentResourceBundle.getString("flashsafe_ready_to_use"));
    }
    
    private static FlashSafeConfiguration createConfiguration() {
        return FlashSafeConfiguration.builder().registerUserId(ApplicationProperties.userId())
                .registerSecret(ApplicationProperties.secret()).build();
    }

    @Override
    public void stop() throws Exception {
        FlashSafeApplication.stop();
        SystemTrayUtil.removeFromSystemTray();
        es.shutdown();
    }

    private void initToken(Stage __stage) {
        try {
            //System.loadLibrary("jcPKCS11");
            //CE.Initialize();
            TokenUtil.PKCS11Initialize();
            long[] slotList = TokenUtil.getSlotList();
            TokenUtil.closeAllSessions(slotList[0]);
            //session = CE.OpenSession(0, CKF_SERIAL_SESSION | CKF_RW_SESSION, null, null);
            session = TokenUtil.openSession(slotList[0]);
            Stage stage = new Stage();
            _stage = stage;
            if(!Utils.isUnix()) {
                stage.initStyle(StageStyle.TRANSPARENT);
            }
            stage.setTitle("Flashsafe Token");
            stage.setMinWidth(260);
            stage.setMinHeight(130);
            stage.getIcons().add(new Image(Class.forName("ru.flashsafe.Main").getResource("/img/logo.png").toExternalForm()));
            stage.setResizable(false);
            final VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(5);
            vbox.setPadding(new Insets(5));
            Label label = new Label(currentResourceBundle.getString("enter_pin_code"));
            label.setTextFill(Color.valueOf("#ECEFF4"));
            PasswordField passwordField = new PasswordField();
            Button button = new Button("OK");
            final Label error = new Label(currentResourceBundle.getString("incorrect_pin_code"));
            error.setTextFill(Color.RED);
            button.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    try {
                        CE.LoginUser(session, passwordField.getText());
                        stage.close();
                        tokenInit = true;
                        try {
                            start(__stage);
                        } catch(Exception ex) {
                            LOGGER.error("Error while building main window", ex);
                        }
                        return;
                    } catch(CKRException ex) {
                        if(ex.getCKR() == CKR.PIN_INCORRECT) {
                            if(!vbox.getChildren().contains(error)) {
                                vbox.getChildren().add(1, error);
                            }
                        }
                    }
                }
            });
            vbox.getChildren().add(label);
            vbox.getChildren().add(passwordField);
            vbox.getChildren().add(button);
            vbox.setStyle("-fx-background-color: #353F4B;");
            Scene scene;
            if(!Utils.isUnix()) {
                scene = new Scene(vbox, Color.TRANSPARENT);
            } else {
                scene = new Scene(vbox);
            }
            stage.setScene(scene);
            stage.show();
        } catch(ClassNotFoundException ex) {
            LOGGER.error("Error while building main window", ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        String languageValue = ApplicationProperties.languageTag();
        currentLocale = Locale.forLanguageTag(languageValue);
        try {
            currentResourceBundle = ResourceBundle.getBundle("bundles.interface", currentLocale);
        } catch (NullPointerException | MissingResourceException e) {
            LOGGER.warn("Unable to load resource bundle for locale:" + currentLocale + " . The application will use ENGLISH", e);
            currentLocale = Locale.ENGLISH;
            currentResourceBundle = ResourceBundle.getBundle("bundles.interface", Locale.ENGLISH);
        }
        launch(args);
    }

}
