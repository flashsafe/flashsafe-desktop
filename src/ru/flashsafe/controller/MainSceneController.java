/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.swing.ImageIcon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.flashsafe.Main;
import ru.flashsafe.http.HttpAPI;
import ru.flashsafe.http.UploadProgressListener;
import ru.flashsafe.model.FSObject;
import ru.flashsafe.token.FlashSafeToken;
import ru.flashsafe.token.event.BaseEventHandler;
import ru.flashsafe.token.exception.TokenServiceInitializationException;
import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
import ru.flashsafe.token.service.impl.RemoteEmulatorTokenService;

/**
 * FXML Controller class
 * @author alex_xpert
 */
public class MainSceneController implements Initializable, UploadProgressListener {
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
    private final Image arrowIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/arrow.png"));
    
    private final ArrayList<FSObject> PARENT_PATH = new ArrayList<>();
    private final HashMap<Integer, ArrayList<FSObject>> CHILDRENS = new HashMap<>();
    private FSObject cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
    private FSObject current_element = cur_path;
    private FSObject[] content;
    private boolean back = false;
    private boolean menu_opened = false;
    private int path;
    private String pincode = "";
    private ArrayList<ListView> lists = new ArrayList<>();
    private SystemTray tray;
    private TrayIcon ticon;
    private final ArrayList<FSObject> FORWARD_PATH = new ArrayList<>();
    public static RemoteEmulatorTokenService rets;
    private boolean run = false;
    private TreeView current_tv = null;
    private final ArrayList<TreeView> TREE_VIEW = new ArrayList<>();
    private final ArrayList<TreeView> TREE_VIEW_FORWARD = new ArrayList<>();
    
    @FXML
    private AnchorPane window, myFilesPane;
    @FXML
    private Label upload_button, download_button, create_path_button;
    @FXML
    private Label back_button, forward_button, settings;
    @FXML
    private Label filename, filetype, filesize;
    @FXML
    private Label cloud;
    @FXML
    private Pane menu, network;
    @FXML
    private Label status;
    @FXML
    private Pane pincode_dialog;
    @FXML
    private TextField pincode_textfield;
    @FXML
    private Button pincode_submit;
    @FXML
    private VBox files1, files2;
    @FXML
    private HBox current_path;
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
        Thread t = new Thread(new Runnable() {
            
            @Override
            public void run() {
//                while (cloud_enabled.isBackgroundLoading() || upload_enabled.isBackgroundLoading()
//                        || download_enabled.isBackgroundLoading() || create_path_enabled.isBackgroundLoading()
//                        || folderIcon.isBackgroundLoading() || lockIcon.isBackgroundLoading()
//                        || folderBlackIcon.isBackgroundLoading() || fileIcon.isBackgroundLoading()
//                        || lockBlackIcon.isBackgroundLoading() || dividerIcon.isBackgroundLoading()) {}
                
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() { 
                        if (auth()) {
                            cloud.setGraphic(new ImageView(cloud_enabled));
                            upload_button.setGraphic(new ImageView(upload_enabled));
                            download_button.setGraphic(new ImageView(download_enabled));
                            create_path_button.setGraphic(new ImageView(create_path_enabled));
                            window.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                                @Override
                                public void handle(MouseEvent event) {
                                    for(ListView l : lists) {
                                        if(!l.isFocused()) {
                                            l.setVisible(false);
                                        }
                                    }
                                    if(!files1.isFocused() && !files2.isFocused()) {
                                        for(int i=0;i<files1.getChildren().size();i++) {
                                            files1.getChildren().get(i).setStyle("-fx-background-color: #FFFFFF");
                                        }
                                        for(int i=0;i<files2.getChildren().size();i++) {
                                            files2.getChildren().get(i).setStyle("-fx-background-color: #FFFFFF");
                                        }
                                        filename.setText("");
                                        filetype.setText("");
                                        filesize.setText("");
                                    }
                                }
                            });
                            files1.setOnDragOver(new EventHandler<DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    if (event.getGestureSource() != files1 &&
                                            event.getDragboard().hasFiles()) {
                                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                                    }
                                    event.consume();
                                }
                            });
                            files1.setOnDragDropped(new EventHandler <DragEvent>() {
                                @Override
                                public void handle(DragEvent event) {
                                    Dragboard db = event.getDragboard();
                                    if (db.hasFiles()) {
                                        final File f = db.getFiles().get(0);
                                        Thread t = new Thread(new Runnable() {
                                            public void run() {
                                                Platform.runLater(new Runnable() {
                                                    public void run() {
                                                        uploadFile(f);
                                                    }
                                                });
                                            }
                                        });
                                        t.setDaemon(true);
                                        t.start();
                                    }
                                    event.setDropCompleted(true);
                                    event.consume();
                                }
                            });
                            
                            loadContent(cur_path.id, "", current_tv);
                            try {
                                if(SystemTray.isSupported()) {
                                    tray = SystemTray.getSystemTray();
                                    ticon = new TrayIcon(new ImageIcon(getClass().getResource("/ru/flashsafe/img/logo1.png")).getImage(), "FlashSafe");
                                    tray.add(ticon);
                                    ticon.displayMessage("Соединение установлено", "Успешно установлено соединение с облаком. Ваша флешка готова к работе.", TrayIcon.MessageType.INFO);
                                }
                            } catch(AWTException e) {
                                log.error(e);
                            }
                        } else {
                            network.setVisible(true);
                        }
                    }

                });
            }
            
        });
        t.setDaemon(true);
        try {
            rets = RemoteEmulatorTokenService.getTokenService();
            rets.setCodeGenerationStrategy(new FixedValueGenerationStrategy("open123458"));
            rets.subscribeToEvents("1", new BaseEventHandler() {

                @Override
                protected void onDetach(FlashSafeToken flashSafeToken) {
                    
                }

                @Override
                protected void onAttach(FlashSafeToken flashSafeToken) {
                    if(!run) {
                        run = true;
                        t.start();
                    }
                }
            });
            
        } catch(TokenServiceInitializationException e) {
            log.error(e);
        }
        
        HttpAPI.getInstance().addListener(this);
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
                            if(current_element.type.equals("dir")) {
                                filename.setText("");
                                filetype.setText("");
                                filesize.setText("");
                                if(current_element.pincode) {
                                    path = current_element.id;
                                    pincode_dialog.setVisible(true);
                                } else {
                                    clearContent();
                                    loadContent(current_element.id, "", current_tv);
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
                loadContent(PARENT_PATH.isEmpty() ? 0 : PARENT_PATH.get(PARENT_PATH.size() - 1).id, pincode_textfield.getText(), current_tv);
            } else {
                clearContent();
                loadContent(path, pincode_textfield.getText(), current_tv);
            }
        }
    }
    
    public void onCreatePathClick() {
        pathname_dialog.setVisible(true);
    }
    
    public void onPathnameSubmit() {
        pathname_dialog.setVisible(false);
        if(!pathname_textfield.getText().isEmpty()) {
            int id = HttpAPI.getInstance().createPath(cur_path.id, pincode, pathname_textfield.getText());
            FSObject path = new FSObject(id, "dir", pathname_textfield.getText(), "", 0, false, 0, System.currentTimeMillis(), System.currentTimeMillis());
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
            uploadFile(selectedFile);
        }
    }
    
    private void uploadFile(File selectedFile) {
        progress.setProgress(0);
        progress.setVisible(true);
        int id = HttpAPI.getInstance().uploadFile(cur_path.id, pincode, -1, selectedFile);
        progress.setVisible(false);
        FSObject f = new FSObject(id, "file", selectedFile.getName(),
                selectedFile.getName().split("\\.")[selectedFile.getName().split("\\.").length - 1].toLowerCase(),
                selectedFile.length(), false, 0, System.currentTimeMillis(), System.currentTimeMillis());
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
    
    @Override
    public void onUpdateProgress(double percent) {
        progress.setProgress(percent);
    }
    
    public void back() {
        back = true;
        FORWARD_PATH.add(cur_path);
        clearContent();
        loadContent(PARENT_PATH.isEmpty() ? 0 : PARENT_PATH.get(PARENT_PATH.size() - 1).id, "", current_tv);
    }
    
    public void forward() {
        if(!FORWARD_PATH.isEmpty()) {
            clearContent();
            loadContent(FORWARD_PATH.get(FORWARD_PATH.size() - 1).id, "", current_tv);
            FORWARD_PATH.remove(FORWARD_PATH.size() - 1);
        }
    }
    
    private boolean auth() {
        return HttpAPI.getInstance().auth();
    }
    
    private void clearContent() {
        files1.getChildren().clear();
        files2.getChildren().clear();
    }
    
    private int loadContent(int path_id, String pin, TreeView tv) {
        Object[] answer = HttpAPI.getInstance().getContent(path_id, pin);
        int code = (int) answer[1];
        switch(code) {
            case 0:
                content = (FSObject[]) answer[0];
                ArrayList<FSObject> path_childrens = new ArrayList<>();
                TreeItem<String> root_item = new TreeItem("", new ImageView(dividerIcon));
                root_item.setExpanded(false);
                if(tv == null) {
                    myFilesPane.getChildren().add(new TreeView(root_item));
                } else {
                    tv.getRoot().getChildren().add(root_item);
                }
                mf.setExpanded(true);
                for (int i=0;i<content.length;i++) {
                    FSObject fso = content[i];
                    Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon));
                    label.setFont(new Font("Ubuntu Condensed", 18));
                    label.setTextFill(Paint.valueOf("#7C7C7C"));
                    label.setId(String.valueOf(fso.id));
                    label.setPrefWidth(340);
                    label.setOnMouseClicked(getOnElementClick(label));
                    (i % 2 == 0 ? files1 : files2).getChildren().add(label);
                    if(fso.type.equals("dir")) {
                        path_childrens.add(fso);
                        if(!back) {
                            TreeItem<String> item = new TreeItem(fso.name, new ImageView(fso.pincode ? lockIcon : folderIcon));
                            item.setExpanded(false);
                            TreeView treeView = new TreeView(item);
                            treeView.setId(String.valueOf(fso.id));
                            treeView.setOnMouseClicked(getTreeViewItemClickListener(fso.id));
                            if(tv == null) {
                                myFilesPane.getChildren().add(treeView);
                            } else {
                                tv.getRoot().getChildren().add(treeView);
                            }
                        } else {
                            
                        }
                    }
                }
                CHILDRENS.put(path_id, path_childrens);
                if(!pin.equals("")) {
                    pincode = pincode_textfield.getText();
                    pincode_textfield.setText("");
                }
                if(back) {
                    if(current_path.getChildren().size() > 1) {
                        current_path.getChildren().remove(current_path.getChildren().size() - 1);
                        current_path.getChildren().remove(current_path.getChildren().size() - 1);
                    }
                    if(!PARENT_PATH.isEmpty() && PARENT_PATH.size() > 1) {
                        PARENT_PATH.remove(PARENT_PATH.size() - 1);
                        cur_path = PARENT_PATH.get(PARENT_PATH.size() - 1);
                    } else {
                        cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
                    }
                    back = false;
                } else {
                    Label cb = new Label();
                    cb.setPadding(new Insets(0, 0, 0, 5));
                    cb.setTextFill(Paint.valueOf("#F3F3F3"));
                    Font font = Font.loadFont(getClass().getResourceAsStream("/ru/flashsafe/font/ubuntu_regular.ttf"), 18);
                    cb.setFont(font);
                    cb.setId(String.valueOf(path_id));
                    cb.setText(current_element.name);
                    cb.setPrefHeight(30);
                    Label arrow = new Label();
                    arrow.setGraphic(new ImageView(arrowIcon));
                    arrow.setPrefHeight(30);
                    current_path.getChildren().add(cb);
                    current_path.getChildren().add(arrow);
                    cb.setOnMouseEntered(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            arrow.fireEvent(event);
                        }

                    });
                    cb.setOnMouseExited(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            arrow.fireEvent(event);
                        }

                    });
                    arrow.setOnMouseClicked(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            ListView list = new ListView();
                            double width = 0;
                            for(FSObject fso : CHILDRENS.get(path_id)) {
                                Label l = new Label();
                                l.setTextFill(Paint.valueOf("#7C7C7C"));
                                Font font = Font.loadFont(getClass().getResourceAsStream("/ru/flashsafe/font/ubuntu_regular.ttf"), 18);
                                l.setFont(font);
                                l.setId(String.valueOf(fso.id));
                                l.setText(fso.name);
                                l.setMaxHeight(30);
                                l.setOnMouseClicked(new EventHandler<MouseEvent>() {

                                    @Override
                                    public void handle(MouseEvent event) {
                                        current_element = fso;
                                        clearContent();
                                        int pos = current_path.getChildren().indexOf(fso);
                                        if(current_path.getChildren().size() > pos + 1) {
                                            for(int i=pos+1;i<current_path.getChildren().size();i++) {
                                                current_path.getChildren().remove(i);
                                            }
                                        }
                                        loadContent(fso.id, "", current_tv);
                                    }
                                });
                                list.getItems().add(l);
                                if(fso.name.length() > width) {
                                    width = fso.name.length();
                                }
                            }
                            window.getChildren().add(list);
                            list.setLayoutX(cb.getLayoutX() + 220);
                            list.setLayoutY(cb.getLayoutY() + 53);
                            list.setPrefWidth(width * font.getSize() * 0.525);
                            list.setPrefHeight(list.getItems().size() * 27.25);
                            lists.add(list);
                            list.setVisible(true);
                        }
                    });
                    PARENT_PATH.add(cur_path);
                    cur_path = current_element;
                }
                break;
            case 1:
                CHILDRENS.put(path_id, new ArrayList());
                if(!pin.equals("")) {
                    pincode = pincode_textfield.getText();
                    pincode_textfield.setText("");
                }
                if(back) {
                    if(current_path.getChildren().size() > 1) {
                        current_path.getChildren().remove(current_path.getChildren().size() - 1);
                        current_path.getChildren().remove(current_path.getChildren().size() - 1);
                    }
                    if(!PARENT_PATH.isEmpty() && PARENT_PATH.size() > 1) {
                        PARENT_PATH.remove(PARENT_PATH.size() - 1);
                        cur_path = PARENT_PATH.get(PARENT_PATH.size() - 1);
                    } else {
                        cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
                    }
                    back = false;
                } else {
                    Label cb = new Label();
                    cb.setPadding(new Insets(0, 0, 0, 5));
                    cb.setTextFill(Paint.valueOf("#F3F3F3"));
                    Font font = Font.loadFont(getClass().getResourceAsStream("/ru/flashsafe/font/ubuntu_regular.ttf"), 18);
                    cb.setFont(font);
                    cb.setId(String.valueOf(path_id));
                    cb.setText(current_element.name);
                    cb.setPrefHeight(30);
                    Label arrow = new Label();
                    arrow.setGraphic(new ImageView(arrowIcon));
                    arrow.setPrefHeight(30);
                    current_path.getChildren().add(cb);
                    current_path.getChildren().add(arrow);
                    cb.setOnMouseEntered(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            arrow.fireEvent(event);
                        }

                    });
                    cb.setOnMouseExited(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            arrow.fireEvent(event);
                        }

                    });
                    arrow.setOnMouseClicked(new EventHandler() {

                        @Override
                        public void handle(Event event) {
                            ListView list = new ListView();
                            double width = 0;
                            for(FSObject fso : CHILDRENS.get(path_id)) {
                                Label l = new Label();
                                l.setTextFill(Paint.valueOf("#7C7C7C"));
                                Font font = Font.loadFont(getClass().getResourceAsStream("/ru/flashsafe/font/ubuntu_regular.ttf"), 18);
                                l.setFont(font);
                                l.setId(String.valueOf(fso.id));
                                l.setText(fso.name);
                                l.setMaxHeight(30);
                                l.setOnMouseClicked(new EventHandler<MouseEvent>() {

                                    @Override
                                    public void handle(MouseEvent event) {
                                        current_element = fso;
                                        clearContent();
                                        int pos = current_path.getChildren().indexOf(fso);
                                        if(current_path.getChildren().size() > pos + 1) {
                                            for(int i=pos+1;i<current_path.getChildren().size();i++) {
                                                current_path.getChildren().remove(i);
                                            }
                                        }
                                        loadContent(fso.id, "", current_tv);
                                    }
                                });
                                list.getItems().add(l);
                                if(fso.name.length() > width) {
                                    width = fso.name.length();
                                }
                            }
                            window.getChildren().add(list);
                            list.setLayoutX(cb.getLayoutX() + 220);
                            list.setLayoutY(cb.getLayoutY() + 53);
                            list.setPrefWidth(width * font.getSize() * 0.525);
                            list.setPrefHeight(list.getItems().size() * 27.25);
                            lists.add(list);
                            list.setVisible(true);
                        }
                    });
                    PARENT_PATH.add(cur_path);
                    cur_path = current_element;
                }
                break;
            case 2:
                status.setText("Oops!");
                network.setVisible(true);
                break;
            case 3:
                path = path_id;
                pincode_dialog.setVisible(true);
                break;
            case 4:
                status.setText("Oops!");
                network.setVisible(true);
                break;
        }
        return code;
    }
    
    private EventHandler<MouseEvent> getTreeViewItemClickListener(int path_id) {
        return new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY) {
                    loadContent(path_id, "", current_tv);
                }
            }
        };
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
