/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.Properties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author FlashSafe
 */
public class Main extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(ru.flashsafe.update.Main.class);
    private static final JsonParser PARSER = new JsonParser();
    private static final String HOMEPAGE = "https://flash.so";
    private static final String SOURCE = HOMEPAGE + "/update/";
    private String version;
    private String last_version;
    private URLConnection conn;
    private static Label status;
    private ProgressBar progress;
    private static final Properties props = new Properties();
    private static final File config = new File("./properties.xml");
    private static Stage stage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
     
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/updater.fxml"));
        Pane pane = (Pane) loader.load();
        ((Label )pane.getChildren().get(2)).setFont(Font.loadFont(getClass().getResourceAsStream("/Lekton-Regular.ttf"), 30));
        status = (Label) pane.getChildren().get(3);
        status.setFont(Font.loadFont(getClass().getResourceAsStream("/Jura-Regular.ttf"), 18));
        progress = (ProgressBar) pane.getChildren().get(0);
        primaryStage.setScene(new Scene(pane));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
        primaryStage.setTitle("Flashsafe");
        primaryStage.show();
        
        FileInputStream in = new FileInputStream(config);
        props.loadFromXML(in);
        in.close();
        version = props.getProperty("version");
        
        checkUpdates();
    }
    
    public void checkUpdates() throws IOException, URISyntaxException {
        JsonObject last_ver = getJsonObject(HOMEPAGE + "/update.json");
        if(last_ver != null) {
            last_version = last_ver.get("last_version").getAsString();
            if(!version.equals(last_version)) { // update available!
                try {
                    Main.status.setText(new String("Доступна новая версия: ".getBytes("Windows-1251"), "UTF-8") + last_version + new String("! Обновление...".getBytes("Windows-1251"), "UTF-8"));
                    update();
                } catch(IOException ex) {
                    LOGGER.error("Error on update", ex);
                }
            } else {
                startClient();
            }
        }
    }
    
    private void update() {
        JsonObject update = getJsonObject(HOMEPAGE + "/update.json").getAsJsonObject();
        
        JsonArray created_files = update.get("created_files").getAsJsonArray();
        JsonArray created_dirs = update.get("created_dirs").getAsJsonArray();
        JsonArray deleted_files = update.get("deleted_files").getAsJsonArray();
        JsonArray deleted_dirs = update.get("deleted_dirs").getAsJsonArray();
        
        for(int i=0;i<deleted_files.size();i++) deleteFile(deleted_files.get(i).getAsString());
        for(int i=0;i<deleted_dirs.size();i++) deleteFolder(deleted_dirs.get(i).getAsString());
        
        String update_source = SOURCE + update.get("last_update").getAsString() + "/";
        
        for(int i=0;i<created_dirs.size();i++) {
            String dirname = created_dirs.get(i).getAsString();
            createFolder(dirname);
        }
        
        for(int i=0;i<created_files.size();i++) {
            String filename = created_files.get(i).getAsString();
            downloadFile(filename, update_source + filename);
        }
        try {
            props.setProperty("version", last_version);
            FileOutputStream out = new FileOutputStream(config);
            props.storeToXML(out, "Flashsafe properties", "UTF-8");
            out.close();
        } catch(IOException ex) {
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
                    for(int i=1;i<libs.length;i++) {
                        builder.append(";lib/" + libs[i].getName());
                    }
                    Process start = Runtime.getRuntime().exec(new String[] {"java", "-classpath", builder.toString(), "ru.flashsafe.Main"});
                } catch(IOException ex) {
                    LOGGER.error("Error on start client.", ex);
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();
        stage.close();
    }
    
    private void deleteFile(String name) {
        File f = new File("./" + name);
        if(f.exists()) f.delete();
    }
    
    private void downloadFile(String name, String url) {
        try {
            File f = new File(name);
            if(f.exists()) f.delete();
            f.createNewFile();
            InputStream in = getInputStream(url);
            OutputStream out = new FileOutputStream(f);
            int b;
            while((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
            out.flush();
            out.close();
        } catch(IOException ex) {
            LOGGER.error("Error on download file " + name, ex);
        }
    }
    
    private void deleteFolder(String name) {
        clearFolder(new File(name));
    }
    
    private void clearFolder(File folder) {
        File[] files = folder.listFiles();
        for(File f : files) {
            if(f.isFile()) {
                f.delete();
            } else {
                clearFolder(f);
            }
        }
        folder.delete();
    }
    
    private void createFolder(String name) {
        File folder = new File(name);
        if(!folder.exists()) {
            if(name.contains("/")) {
                folder.mkdirs();
            } else {
                folder.mkdir();
            }
        }
    }
    
    private JsonObject getJsonObject(String url) {
        JsonObject inf = null;
        try {
            InputStream in = getInputStream(url);
            if(in != null) {
                inf = PARSER.parse(new InputStreamReader(in, "UTF-8")).getAsJsonObject();
                in.close();
                conn = null;
            }
        } catch(IOException ex) {
            LOGGER.error("Error on update", ex);
        }
        return inf;
    }
    
    private InputStream getInputStream(String url) {
        InputStream in = null;
        try {
            conn = new URL(url).openConnection();
            in = conn.getInputStream();
        } catch(IOException ex) {
            LOGGER.error("Error on update", ex);
        }
        return in;
    }
    
}
