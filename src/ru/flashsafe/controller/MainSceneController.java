/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.flashsafe.Main;
import ru.flashsafe.http.HttpAPI;
import ru.flashsafe.model.FSObject;

/**
 * FXML Controller class
 *
 * @author alex_xpert
 */
public class MainSceneController implements Initializable {
    private static final Logger log = LogManager.getLogger(MainSceneController.class);
    private final Image cloud_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/cloud_enabled.png"));
    private final Image upload_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/upload_enabled.png"));
    private final Image download_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/download_enabled.png"));
    private final Image create_path_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/create_folder_enabled.png"));
    private final Image folderIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder.png"));
    private final Image folderBlackIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder_black.png"));
    private final Image lockIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock.png"));
    private final Image lockBlackIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock_black.png"));
    private final Image dividerIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/divider.png"));
    private final Image fileIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/file.png"));
    
    private boolean menu_opened = false;
    private FSObject[] content;
    private FSObject current_element;
    private int current_path_id = 0;
    private String current_pincode = "";
    private ArrayList<Integer> parent_path = new ArrayList();
    private boolean back = false;
    
    @FXML
    private AnchorPane myFilesPane;
    @FXML
    private Label upload_button;   
    @FXML
    private Label download_button;
    @FXML
    private Label create_path_button;
    @FXML
    private Label back_button;
    @FXML
    private Label settings;
    @FXML
    private Label filename;
    @FXML
    private Label filetype;
    @FXML
    private Label filesize;
    @FXML
    private Label cloud;
    @FXML
    private Pane menu;
    @FXML
    private Pane network;
    @FXML
    private Label status;
    @FXML
    private Pane pincode_dialog;
    @FXML
    private TextField pincode_textfield;
    @FXML
    private Button pincode_submit;
    @FXML
    private VBox files1;
    @FXML
    private VBox files2;
    @FXML
    private TextField current_path;
    @FXML
    TitledPane mf;
    @FXML
    private Pane pathname_dialog;
    @FXML
    private TextField pathname_textfield;
    @FXML
    private Button pathname_submit;
    @FXML
    private ProgressBar progress;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        int auth = HttpAPI.getInstance().auth();
                        if (auth == 0) {
                            network.setVisible(true);
                        } else {
                            while (cloud_enabled.isBackgroundLoading() || upload_enabled.isBackgroundLoading()
                                    || download_enabled.isBackgroundLoading() || create_path_enabled.isBackgroundLoading()) {}
                            cloud.setGraphic(new ImageView(cloud_enabled));
                            upload_button.setGraphic(new ImageView(upload_enabled));
                            download_button.setGraphic(new ImageView(download_enabled));
                            create_path_button.setGraphic(new ImageView(create_path_enabled));
                            content = HttpAPI.getInstance().getContent();
                            while (dividerIcon.isBackgroundLoading()) {}
                            TreeItem<String> root_item = new TreeItem("", new ImageView(dividerIcon));
                            root_item.setExpanded(true);
                            TreeView myFilesTree = new TreeView(root_item);
                            myFilesPane.getChildren().add(myFilesTree);
                            mf.setExpanded(true);
                            while (folderIcon.isBackgroundLoading() || lockIcon.isBackgroundLoading()
                                    || folderBlackIcon.isBackgroundLoading() || fileIcon.isBackgroundLoading()
                                    || lockBlackIcon.isBackgroundLoading()) {}
                            for (int i=0;i<content.length;i++) {
                                FSObject fso = content[i];
                                if (fso.type.equals("dir")) {
                                    TreeItem<String> treeitem = new TreeItem(fso.name, new ImageView(fso.pincode ? lockIcon : folderIcon));
                                    root_item.getChildren().add(treeitem);
                                }
                                Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon));
                                label.setFont(new Font("Ubuntu Condensed", 18));
                                label.setTextFill(Paint.valueOf("#7C7C7C"));
                                label.setId(String.valueOf(fso.id));
                                label.setPrefWidth(340);
                                label.setOnMouseClicked(getOnElementClick(label));
                                (i % 2 == 0 ? files1 : files2).getChildren().add(label);
                            }
                            current_path.setText("/");
                        }
                    }

                });
            }
            
        }).start();
    }    
    
    private EventHandler<MouseEvent> getOnElementClick(Label label) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    for(FSObject fso : content) {
                        if(fso.id == Integer.parseInt(label.getId())) {
                            current_element = fso;
                        }
                    }
                    switch(event.getClickCount()) {
                        case 1:
                            for(int i=0;i<files1.getChildren().size();i++) {
                                files1.getChildren().get(i).setStyle("-fx-background-color: #FFFFFF");
                            }
                            for(int i=0;i<files2.getChildren().size();i++) {
                                files2.getChildren().get(i).setStyle("-fx-background-color: #FFFFFF");
                            }
                            label.setStyle("-fx-background-color: #59DAFF");
                            filename.setText(current_element.name);
                            filetype.setText(current_element.type.equals("file") ? current_element.format : "папка");
                            filesize.setText(String.valueOf(current_element.size / 1024) + " КБ");
                            break;
                        case 2:
                            filename.setText("");
                            filetype.setText("");
                            filesize.setText("");
                            if(current_element.type.equals("dir")) {
                                if(current_element.pincode) {
                                    pincode_dialog.setVisible(true);
                                } else {
                                    content = HttpAPI.getInstance().getContent(current_element.id, "");
                                    files1.getChildren().clear();
                                    files2.getChildren().clear();
                                    if(content != null) {
                                        for (int i=0;i<content.length;i++) {
                                            FSObject fso = content[i];
                                            Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon));
                                            label.setFont(new Font("Ubuntu Condensed", 18));
                                            label.setTextFill(Paint.valueOf("#7C7C7C"));
                                            label.setId(String.valueOf(fso.id));
                                            label.setPrefWidth(340);
                                            label.setOnMouseClicked(getOnElementClick(label));
                                            (i % 2 == 0 ? files1 : files2).getChildren().add(label);
                                        }
                                    }
                                    parent_path.add(current_path_id);
                                    current_path.setText(current_path.getText() + (current_path.getText().equals("/") ? "" : "/") + current_element.name);
                                    current_path_id = current_element.id;
                                    current_pincode = "";
                                }
                            }
                            break;
                    }
                }
            }
        };
    }
    
    public void onPincodeSubmit() {
        pincode_dialog.setVisible(false);
        if(!pincode_textfield.getText().isEmpty()) {
            if(back) {
                back = false;
                content = HttpAPI.getInstance().getContent(parent_path.isEmpty() ? 0 : parent_path.get(parent_path.size() - 1), pincode_textfield.getText());
                for (int i=0;i<content.length;i++) {
                    FSObject fso = content[i];
                    Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon));
                    label.setFont(new Font("Ubuntu Condensed", 18));
                    label.setTextFill(Paint.valueOf("#7C7C7C"));
                    label.setId(String.valueOf(fso.id));
                    label.setPrefWidth(340);
                    label.setOnMouseClicked(getOnElementClick(label));
                    (i % 2 == 0 ? files1 : files2).getChildren().add(label);
                }
                String[] paths = current_path.getText().split("/");
                String cp = "";
                for(int i = 1;i<paths.length-1;i++) {
                    cp += "/" + paths[i];
                }
                current_path.setText(cp);
                if(!parent_path.isEmpty()) {
                    current_path_id = parent_path.get(parent_path.size() - 1);
                    parent_path.remove(parent_path.size() -1);
                } else {
                    current_path_id = 0;
                }
                current_pincode = pincode_textfield.getText();
                pincode_textfield.setText("");
            } else {
                content = HttpAPI.getInstance().getContent(current_element.id, pincode_textfield.getText());
                files1.getChildren().clear();
                files2.getChildren().clear();
                if(content != null) {
                    for (int i=0;i<content.length;i++) {
                        FSObject fso = content[i];
                        Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon));
                        label.setFont(new Font("Ubuntu Condensed", 18));
                        label.setTextFill(Paint.valueOf("#7C7C7C"));
                        label.setId(String.valueOf(fso.id));
                        label.setPrefWidth(340);
                        label.setOnMouseClicked(getOnElementClick(label));
                        (i % 2 == 0 ? files1 : files2).getChildren().add(label);
                    }
                    parent_path.add(current_path_id);
                    current_path.setText(current_path.getText() + (current_path.getText().equals("/") ? "" : "/") + current_element.name);
                    current_path_id = current_element.id;
                    current_pincode = pincode_textfield.getText();
                    pincode_textfield.setText("");
                } else {
                    pincode_textfield.setText("");
                    pincode_dialog.setVisible(true);
                }
            }
        }
    }
    
    public void onCreatePathClick() {
        pathname_dialog.setVisible(true);
    }
    
    public void onPathnameSubmit() {
        pathname_dialog.setVisible(false);
        if(!pathname_textfield.getText().isEmpty()) {
            int id = HttpAPI.getInstance().createPath(current_path_id, current_pincode, pathname_textfield.getText());
            FSObject path = new FSObject();
            path.id = id;
            path.count = 0;
            path.create_time = System.currentTimeMillis();
            path.update_time = System.currentTimeMillis();
            path.name = pathname_textfield.getText();
            path.pincode = false;
            path.type = "dir";
            path.size = 0;
            Label label = new Label(path.name, new ImageView(folderBlackIcon));
            label.setFont(new Font("Ubuntu Condensed", 18));
            label.setTextFill(Paint.valueOf("#7C7C7C"));
            label.setId(String.valueOf(path.id));
            label.setPrefWidth(340);
            label.setOnMouseClicked(getOnElementClick(label));
            (files1.getChildren().size() ==  files2.getChildren().size() ? files1 : files2).getChildren().add(label);
            FSObject[] new_content = new FSObject[content.length + 1];
            for(int i=0;i<content.length;i++) {
                new_content[i] = content[i];
            }
            new_content[content.length] = path;
            content = new_content;
        } else {
            pathname_dialog.setVisible(true);
        }
    }
    
    public void onUploadFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выгрузить файл");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt", "*.rtf", "*.doc", "*.docx"),
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.tiff", "*.ico"),
                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac", "*.wma", "*.amr"),
                new ExtensionFilter("Video Files", "*.wmv", "*.mp4", "*.avi", "*.mov", "*.flv", "*.3gp", "*.3gpp"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(Main._stage);
        if (selectedFile != null) {
            int id = HttpAPI.getInstance().uploadFile(current_path_id, current_pincode, -1, selectedFile);
            FSObject f = new FSObject();
            f.id = id;
            f.format = selectedFile.getName().split("\\.")[selectedFile.getName().split("\\.").length - 1].toUpperCase();
            f.create_time = System.currentTimeMillis();
            f.update_time = System.currentTimeMillis();
            f.name = selectedFile.getName();
            f.pincode = false;
            f.type = "file";
            f.size = selectedFile.length();
            Label label = new Label(f.name, new ImageView(fileIcon));
            label.setFont(new Font("Ubuntu Condensed", 18));
            label.setTextFill(Paint.valueOf("#7C7C7C"));
            label.setId(String.valueOf(f.id));
            label.setPrefWidth(340);
            label.setOnMouseClicked(getOnElementClick(label));
            (files1.getChildren().size() ==  files2.getChildren().size() ? files1 : files2).getChildren().add(label);
            FSObject[] new_content = new FSObject[content.length + 1];
            for(int i=0;i<content.length;i++) {
                new_content[i] = content[i];
            }
            new_content[content.length] = f;
            content = new_content;
        }
    }
    
    public void back() {
        content = HttpAPI.getInstance().getContent(parent_path.isEmpty() ? 0 : parent_path.get(parent_path.size() - 1), "");
        files1.getChildren().clear();
        files2.getChildren().clear();
        if(content != null) {
            for (int i=0;i<content.length;i++) {
                FSObject fso = content[i];
                Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon));
                label.setFont(new Font("Ubuntu Condensed", 18));
                label.setTextFill(Paint.valueOf("#7C7C7C"));
                label.setId(String.valueOf(fso.id));
                label.setPrefWidth(340);
                label.setOnMouseClicked(getOnElementClick(label));
                (i % 2 == 0 ? files1 : files2).getChildren().add(label);
            }
            String[] paths = current_path.getText().split("/");
            String cp = "";
            for(int i = 1;i<paths.length-1;i++) {
                cp += "/" + paths[i];
            }
            current_path.setText(cp);
            if(!parent_path.isEmpty()) {
                current_path_id = parent_path.get(parent_path.size() - 1);
                parent_path.remove(parent_path.size() -1);
            } else {
                current_path_id = 0;
            }
            current_pincode = "";
        } else {
            back = true;
            pincode_dialog.setVisible(true);
        }
    }
    
    public void toggleSettings() {
        if(menu_opened) {
            settings.setStyle("-fx-background-color: transparent");
            settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/settings.png"))));
            menu.setVisible(false);
            menu_opened = false;
        } else {
            settings.setStyle("-fx-background-color: #F3F3F3");
            settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/settings_opened.png"))));
            menu.setVisible(true);
            menu_opened = true;
        }
    }
}
