/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import ch.randelshofer.quaqua.osx.OSXFile;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.flashsafe.Main;
import ru.flashsafe.http.HttpAPI;
import ru.flashsafe.http.UploadProgressListener;
import ru.flashsafe.model.FSObject;
//import ru.flashsafe.token.FlashSafeToken;
//import ru.flashsafe.token.event.BaseEventHandler;
//import ru.flashsafe.token.exception.TokenServiceInitializationException;
//import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
import ru.flashsafe.token.service.impl.RemoteEmulatorTokenService;

/**
 * FXML Controller class
 * @author alex_xpert
 */
public class MainSceneController implements Initializable, UploadProgressListener {
    //private static final Logger log = LogManager.getLogger(MainSceneController.class);
    
    private final Image cloud_enabled = new Image(getClass().getResourceAsStream("/img/cloud_enabled.png"));
    private final Image upload_enabled = new Image(getClass().getResourceAsStream("/img/upload_enabled.png"));
    private final Image download_enabled = new Image(getClass().getResourceAsStream("/img/download_enabled.png"));
    private final Image create_path_enabled = new Image(getClass().getResourceAsStream("/img/create_folder_enabled.png"));
    private final Image folderIcon = new Image(getClass().getResourceAsStream("/img/folder.png"));
    private final Image folderBlackIcon = new Image(getClass().getResourceAsStream("/img/folder_black1.png"));
    private final Image lockIcon = new Image(getClass().getResourceAsStream("/img/lock.png"));
    private final Image lockBlackIcon = new Image(getClass().getResourceAsStream("/img/lock_black1.png"));
    private final Image dividerIcon = new Image(getClass().getResourceAsStream("/img/divider.png"));
    private final Image fileIcon = new Image(getClass().getResourceAsStream("/img/file.png"));
    private final Image arrowIcon = new Image(getClass().getResourceAsStream("/img/arrow.png"));
    
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
//    @FXML
//    private Button pincode_submit;
    @FXML
    private Button backspace;
    @FXML
    private Button one;
    @FXML
    private Button two;
    @FXML
    private Button three;
    @FXML
    private Button four;
    @FXML
    private Button five;
    @FXML
    private Button six;
    @FXML
    private Button seven;
    @FXML
    private Button eight;
    @FXML
    private Button nine;
    @FXML
    private Button zero;
    @FXML
    private TableView files;
    @FXML
    private HBox current_path;
    @FXML
    TitledPane mf;
    @FXML
    private Pane pathname_dialog;
    @FXML
    private TextField pathname_textfield;
//    @FXML
//    private Button pathname_submit;
    @FXML
    private ProgressBar progress;
    
    private class ConnectToCloudTask implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
             return auth();
        }
        
    }
    
    private class AddHandlersTask implements Callable {

        @Override
        public Object call() throws Exception {
            Platform.runLater(new Runnable() {

                @Override
                public void run() { 
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
                                if(!files.isFocused()) {
                                    for(int i=0;i<files.getItems().size();i++) {
                                        files.setStyle("-fx-background-color: #FFFFFF");
                                    }
                                    filename.setText("");
                                    filetype.setText("");
                                    filesize.setText("");
                                }
                            }
                        });
                        files.setOnDragDetected(new EventHandler<MouseEvent>() {
                            public void handle(MouseEvent event) {
                                Dragboard db = files.startDragAndDrop(TransferMode.ANY);
                                ClipboardContent content = new ClipboardContent();
                                ArrayList<File> list = new ArrayList();
                                list.add(new File("./.mime"));
                                content.putFiles(list);
                                db.setContent(content);
                                event.consume();
                            }
                        });
                        files.setOnDragOver(new EventHandler<DragEvent>() {
                            @Override
                            public void handle(DragEvent event) {
                                if (event.getGestureSource() != files &&
                                        event.getDragboard().hasFiles()) {
                                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                                }
                                event.consume();
                            }
                        });
                        files.setOnDragDropped(new EventHandler <DragEvent>() {
                            @Override
                            public void handle(DragEvent event) {
                                Dragboard db = event.getDragboard();
                                if (db.hasFiles()) {
                                    final File f = db.getFiles().get(0);
                                    uploadFile(f);
                                }
                                event.setDropCompleted(true);
                                event.consume();
                            }
                        });

                }

            });
            backspace.setOnMouseClicked(new EventHandler() {

                @Override
                public void handle(Event event) {
                    backspace();
                }
            });
            one.setOnMouseClicked(getOnNumClick(one));
            two.setOnMouseClicked(getOnNumClick(two));
            three.setOnMouseClicked(getOnNumClick(three));
            four.setOnMouseClicked(getOnNumClick(four));
            five.setOnMouseClicked(getOnNumClick(five));
            six.setOnMouseClicked(getOnNumClick(six));
            seven.setOnMouseClicked(getOnNumClick(seven));
            eight.setOnMouseClicked(getOnNumClick(eight));
            nine.setOnMouseClicked(getOnNumClick(nine));
            zero.setOnMouseClicked(getOnNumClick(zero));
            return null;
        }
        
    }
    
    /**
     * Получаем системную иконку файла, размером 16x16
     * @param filename
     * @return 
     */
    private Image getFileIcon(String filename) {
        Image image = null;
        try {
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            File f = File.createTempFile("icon", "." + ext);
            BufferedImage bimage;
            String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("mac")) { // For Mac OS X
                bimage = OSXFile.getIconImage(f, 16);
            } else { // For Windows, Linux(?)
                FileSystemView view = FileSystemView.getFileSystemView();      
                Icon icon = view.getSystemIcon(f);
                bimage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                icon.paintIcon(null, bimage.getGraphics(), 0, 0);
            }
            f.delete();
            image = SwingFXUtils.toFXImage(bimage, null);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return image;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HttpAPI.getInstance().addListener(this);
        Main.es.submit(new AddHandlersTask());
        Future connect = Main.es.submit(new ConnectToCloudTask());
        while(!connect.isDone()) {}
        try {
            if ((boolean) connect.get()) {
                loadContent(cur_path.id, "", current_tv);
            } else {
                network.setVisible(true);
            }
        } catch(InterruptedException | ExecutionException e) {
            //log.error(e);
        }
    }
    
    private void backspace() {
        if(!pincode_textfield.getText().isEmpty()) {
            pincode_textfield.setText(pincode_textfield.getText().substring(0, pincode_textfield.getText().length() - 1));
        }
    }
    
    private void pincodeEnter(String num) {
        pincode_textfield.setText(pincode_textfield.getText() + num);
    }
    
    private void resetNums() {
        ArrayList<String> nums = new ArrayList();
        nums.add("1");
        nums.add("2");
        nums.add("3");
        nums.add("4");
        nums.add("5");
        nums.add("6");
        nums.add("7");
        nums.add("8");
        nums.add("9");
        nums.add("0");
        Button[] bnums = {one, two, three, four, five, six, seven, eight, nine, zero};
        for(Button b : bnums) {
            Random r = new Random();
            int rand = r.nextInt(nums.size());
            b.setText(nums.get(rand));
            nums.remove(rand);
        }
    }
    
    private EventHandler<MouseEvent> getOnNumClick(Button num) {
        return new EventHandler() {

            @Override
            public void handle(Event event) {
                pincodeEnter(num.getText());
                resetNums();
            }
            
        };
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
                            for(int i=0;i<files.getItems().size();i++) {
                                files.setStyle("-fx-background-color: #FFFFFF");
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
            files.getItems().add(new TableRow("dir", label, "0", new Date().toLocaleString()));
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progress.setProgress(0);
                        progress.setVisible(true);
                    }
                });
                int id = HttpAPI.getInstance().uploadFile(cur_path.id, pincode, -1, selectedFile);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisible(false);
                    }
                });
                FSObject f = new FSObject(id, "file", selectedFile.getName(),
                        selectedFile.getName().split("\\.")[selectedFile.getName().split("\\.").length - 1].toLowerCase(),
                        selectedFile.length(), false, 0, System.currentTimeMillis(), System.currentTimeMillis());
                Label label = new Label(f.name, new ImageView(fileIcon));
                label.setFont(new Font("Ubuntu Condensed", 18));
                label.setTextFill(Paint.valueOf("#7C7C7C"));
                label.setId(String.valueOf(f.id));
                label.setPrefWidth(340);
                label.setOnMouseClicked(getOnElementClick(label));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        files.getItems().add(label);
                    }
                });
                FSObject[] new_content = new FSObject[content.length + 1];
                for(int i=0;i<content.length;i++) {
                    new_content[i] = content[i];
                }
                new_content[content.length] = f;
                content = new_content;
            }
        });
        t.setDaemon(true);
        t.start();
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
        files.getItems().clear();
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
                ((TableColumn) files.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("type"));
                ((TableColumn) files.getColumns().get(1)).setCellValueFactory(new Callback<CellDataFeatures<TableRow, TableRow>, ObservableValue<TableRow>>() {
                    @Override
                    public ObservableValue<TableRow> call(CellDataFeatures<TableRow, TableRow> features) {
                        return new ReadOnlyObjectWrapper(features.getValue());
                    }
                  });
                  ((TableColumn) files.getColumns().get(1)).setComparator(new Comparator<TableRow>() {
                    @Override
                    public int compare(TableRow p1, TableRow p2) {
                      return p1.equals(p2) ? 1 : 0;
                    }
                  });
                ((TableColumn) files.getColumns().get(1)).setCellFactory(new Callback<TableColumn<TableRow, TableRow>, TableCell<TableRow, TableRow>>() {
                    @Override
                    public TableCell<TableRow, TableRow> call(TableColumn<TableRow, TableRow> labelCol) {
                      return new TableCell<TableRow, TableRow>() {
                        final Label buttonGraphic = new Label();
                        @Override
                        public void updateItem(final TableRow person, boolean empty) {
                          super.updateItem(person, empty);
                          if (person != null) {
                            buttonGraphic.setText(person.getName().getText());
                            buttonGraphic.setGraphic(person.getName().getGraphic());
                            setGraphic(buttonGraphic);
                          } else {
                            setGraphic(null);
                          }
                        }
                      };
                    }
                  });
                ((TableColumn) files.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("size"));
                ((TableColumn) files.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("createDate"));
                for (int i=0;i<content.length;i++) {
                    FSObject fso = content[i];
                    Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : /*fileIcon*/getFileIcon(fso.name)));
                    label.setFont(new Font("Ubuntu Condensed", 18));
                    label.setTextFill(Paint.valueOf("#7C7C7C"));
                    label.setId(String.valueOf(fso.id));
                    label.setPrefWidth(340);
                    label.setOnMouseClicked(getOnElementClick(label));
                    files.getItems().add(new TableRow(fso.type, label, String.valueOf(fso.size / 1024) + "КБ", new Date(fso.create_time * 1000).toLocaleString()));
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
    
    public class TableRow {
        public SimpleStringProperty type;
        public Label name;
        public SimpleStringProperty size;
        public SimpleStringProperty createDate;
        
        public TableRow(String _type, Label _name, String _size, String _create_date) {
            this.type = new SimpleStringProperty(_type);
            this.name = _name;
            this.size = new SimpleStringProperty(_size);
            this.createDate = new SimpleStringProperty(_create_date);
        }
        
        public String getType() {
            return type.get();
        }
        
        public Label getName() {
            return name;
        }
        
        public String getSize() {
            return size.get();
        }
        
        public String getCreateDate() {
            return createDate.get();
        }
    }
    
}
