/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author FlashSafe
 */
public class Main extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String HOMEPAGE = "https://flash.so";

    private static final String SOURCE = HOMEPAGE + "/update/";

    private static final String VERSION = "version";
    
    private static Locale currentLocale;

    private static ResourceBundle messageBundle;

    private String version;

    private String last_version;

    private URLConnection conn;

    private static Label status;

    private ProgressBar progress;

    private static final Properties props = new Properties();

    private static final File config = new File("./properties.xml");

    private static Stage stage;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/updater.fxml"));
        Pane pane = (Pane) loader.load();
        ((Label) pane.getChildren().get(2)).setFont(Font.loadFont(getClass().getResourceAsStream("/Lekton-Regular.ttf"), 30));
        status = (Label) pane.getChildren().get(3);
        status.setFont(Font.loadFont(getClass().getResourceAsStream("/Jura-Regular.ttf"), 18));
        progress = (ProgressBar) pane.getChildren().get(0);
        primaryStage.setScene(new Scene(pane));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        primaryStage.setTitle("Flashsafe");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        try (FileInputStream in = new FileInputStream(config)) {
            props.loadFromXML(in);
        }
        version = props.getProperty(VERSION);

        checkUpdates();
    }

    public void checkUpdates() throws IOException, URISyntaxException {
        UpdateInformation updateInformation = loadUpdateInformation(HOMEPAGE + "/update.json");
        String last_ver = updateInformation.getLastVersion();
        if (last_ver != null) {
            last_version = last_ver;
            if (version.equals(last_version)) { // update available!
                String statusMessage = buildStatusMessage(last_version);
                Main.status.setText(statusMessage);
                update(updateInformation);
            } else {
                startClient();
            }
        }
    }
    
    private static String buildStatusMessage(String versionTag) {
        StringBuilder messageString = new StringBuilder(messageBundle.getString("new_version_available"));
        messageString.append(": ").append(versionTag).append("! ").append(messageBundle.getString("updating")).append("...");
        return messageString.toString();
    }

    private void update(final UpdateInformation updateInformation) {
        List<String> deletedFiles = updateInformation.getDeletedFiles();
        deletedFiles.forEach(file -> deleteFile(file));

        List<String> deletedDirectories = updateInformation.getDeletedDirectories();
        deletedDirectories.forEach(directory -> deleteFolder(directory));

        String update_source = SOURCE + updateInformation.getLastUpdate() + "/";

        List<String> createdDirectories = updateInformation.getCreatedDirectories();
        createdDirectories.forEach(directory -> createFolder(directory));

        List<String> createdFiles = updateInformation.getCreatedFiles();
        createdFiles.forEach(fileName -> downloadFile(fileName, update_source + fileName));

        try (FileOutputStream configStream = new FileOutputStream(config)) {
            props.setProperty(VERSION, last_version);
            props.storeToXML(configStream, "Flashsafe properties", "UTF-8");
        } catch (IOException ex) {
            LOGGER.error("Error on save properties.", ex);
        }

        startClient();
    }

    private void startClient() {
        Thread daemon = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File[] libs = new File("lib").listFiles();
                    StringBuilder builder = new StringBuilder("lib/" + libs[0].getName());
                    for (int i = 1; i < libs.length; i++) {
                        builder.append(";lib/" + libs[i].getName());
                    }
                    Process start = Runtime.getRuntime().exec(
                            new String[] { "java", "-classpath", builder.toString(), "ru.flashsafe.Main" });
                } catch (IOException ex) {
                    LOGGER.error("Error on start client.", ex);
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();
        stage.close();
    }

    private static void deleteFile(String name) {
        File f = new File("./" + name);
        if (f.exists())
            f.delete();
    }

    private void downloadFile(String name, String url) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File f = new File(name);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            in = getInputStream(url);
            out = new FileOutputStream(f);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            out.flush();
        } catch (IOException ex) {
            LOGGER.error("Error on download file " + name, ex);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private static void deleteFolder(String name) {
        clearFolder(new File(name));
    }

    private static void clearFolder(File folder) {
        File[] files = folder.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            } else {
                clearFolder(f);
            }
        }
        folder.delete();
    }

    private static void createFolder(String name) {
        File folder = new File(name);
        if (!folder.exists()) {
            if (name.contains("/")) {
                folder.mkdirs();
            } else {
                folder.mkdir();
            }
        }
    }

    private static UpdateInformation loadUpdateInformation(String url) {
        URLConnection connection = null;
        InputStreamReader streamReader = null;
        try {
            connection = new URL(url).openConnection();
            streamReader = new InputStreamReader(connection.getInputStream(), Charsets.UTF_8);
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(streamReader, UpdateInformation.class);
        } catch (IOException e) {
            LOGGER.error("Error on update", e);
            throw new RuntimeException("Error on update", e);
        } finally {
            IOUtils.closeQuietly(streamReader);
            IOUtils.close(connection);
        }
    }

    private InputStream getInputStream(String url) {
        InputStream in = null;
        try {
            conn = new URL(url).openConnection();
            in = conn.getInputStream();
        } catch (IOException ex) {
            LOGGER.error("Error on update", ex);
        }
        return in;
    }

}
