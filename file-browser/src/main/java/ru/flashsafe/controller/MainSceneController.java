/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.awt.Desktop;
//import ch.randelshofer.quaqua.osx.OSXFile;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.flashsafe.IconUtil;
import ru.flashsafe.Main;
import ru.flashsafe.http.HttpAPI;
import ru.flashsafe.http.UploadProgressListener;
import ru.flashsafe.model.FSObject;
//import ru.flashsafe.token.FlashSafeToken;
//import ru.flashsafe.token.event.BaseEventHandler;
//import ru.flashsafe.token.exception.TokenServiceInitializationException;
//import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
//import ru.flashsafe.token.service.impl.RemoteEmulatorTokenService;
import ru.flashsafe.util.ResizeHandler;

/**
 * FXML Controller class
 * 
 * @author alex_xpert
 */
public class MainSceneController implements Initializable, UploadProgressListener {

    private static final Logger logger = LogManager.getLogger(MainSceneController.class);

    // private final Image cloud_enabled = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/cloud_enabled.png"));
    // private final Image upload_enabled = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/upload_enabled.png"));
    // private final Image download_enabled = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/download_enabled.png"));
    // private final Image create_path_enabled = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/create_folder_enabled.png"));
    // private final Image folderIcon = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder.png"));
    private final Image folderBlackIcon = new Image(getClass().getResourceAsStream("/img/fs/folder_empty.png"), 24, 24, false,
            false);
    // private final Image lockIcon = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock.png"));
    private final Image lockBlackIcon = new Image(getClass().getResourceAsStream("/img/fs/folder_lock.png"), 24, 24, false, false);
    // private final Image dividerIcon = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/divider.png"));
    // private final Image fileIcon = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/file.png"));
    // private final Image arrowIcon = new
    // Image(getClass().getResourceAsStream("/ru/flashsafe/img/arrow.png"));
    private final Image folderFull = new Image(getClass().getResourceAsStream("/img/fs/folder.png"), 24, 24, false, false);

    private final List<FSObject> PARENT_PATH = new ArrayList<>();
    private final Map<Integer, List<FSObject>> CHILDRENS = new HashMap<>();
    private FSObject cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
    private FSObject current_element = cur_path;
    private FSObject[] content;
    private boolean back = false;
    private boolean menu_opened = false;
    private int path;
    private String pincode = "";
    private List<ListView> lists = new ArrayList<>();
    private final List<FSObject> FORWARD_PATH = new ArrayList<>();
    // public static RemoteEmulatorTokenService rets;
    private boolean run = false;
    private TreeView current_tv = null;
    private final List<TreeView> TREE_VIEW = new ArrayList<>();
    private final List<TreeView> TREE_VIEW_FORWARD = new ArrayList<>();

    private double windowXPosition;
    private double windowYPosition;

    private String pin = "";

    private ObservableList<TableRow> currentDirectoryEntries = FXCollections.observableArrayList();

    private ResourceBundle resourceBundle;

    @FXML
    private Pane topPane;
    @FXML
    private AnchorPane window;
    @FXML
    private Label settings, refresh, exit;
    @FXML
    private Pane menu;
    @FXML
    private Pane pincode_dialog;
    @FXML
    private TextField pincode_textfield;
    // @FXML
    // private Button pincode_submit;
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
    private TableView<TableRow> files;
    @FXML
    private Pane pathname_dialog;
    @FXML
    private TextField pathname_textfield;
    // @FXML
    // private Button pathname_submit;
    @FXML
    private ProgressBar progress;
    @FXML
    private Label flashsafe, myfiles, docs, pictures, sounds, videos, loads, contacts;
    @FXML
    private Pane settings_pane, software_pane;
    @FXML
    private Label rendering, caching, hardware, software, settings_close;
    @FXML
    private Hyperlink link;
    @FXML
    private Button display_choice;
    @FXML
    private Slider display_slider;
    @FXML
    private ListView<Label> display_list;
    @FXML
    private HBox display_menu;

    private class ConnectToCloudTask implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            return auth();
        }

    }

    private class AddHandlersTask implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    files.setOnDragDetected(event -> {
                        Dragboard db = files.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        List<File> list = new ArrayList<>();
                        list.add(new File("./.mime"));
                        content.putFiles(list);
                        db.setContent(content);
                        event.consume();

                    });
                    files.setOnDragOver(event -> {
                        if (event.getGestureSource() != files && event.getDragboard().hasFiles()) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        }
                        event.consume();
                    });
                    files.setOnDragDropped(event -> {
                        Dragboard dragboard = event.getDragboard();
                        if (dragboard.hasFiles()) {
                            final File f = dragboard.getFiles().get(0);
                            uploadFile(f);
                        }
                        event.setDropCompleted(true);
                        event.consume();

                    });

                    // FIXME use column name not index
                    TableColumn<TableRow, Label> nameColumn = (TableColumn<TableRow, Label>) files.getColumns().get(0);
                    nameColumn.setCellValueFactory(new PropertyValueFactory<TableRow, Label>("name"));

                    TableColumn<TableRow, String> createDateColumn = (TableColumn<TableRow, String>) files.getColumns().get(1);
                    createDateColumn.setCellValueFactory(new PropertyValueFactory<TableRow, String>("createDate"));
                    
                    ((TableColumn) files.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TableRow, String>(
                            "type"));
                    
                    TableColumn<TableRow, String> sizeColumn = (TableColumn<TableRow, String>) files.getColumns().get(3);
                    sizeColumn.setCellValueFactory(new PropertyValueFactory<TableRow, String>("size"));
                    
                    SortedList<TableRow> sortedFiles = new SortedList<>(currentDirectoryEntries);
                    files.setItems(sortedFiles);
                    sortedFiles.comparatorProperty().bind(files.comparatorProperty());

                    backspace.setOnMouseClicked(event -> backspace());

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
                    ResizeHandler handler = new ResizeHandler(Main._scene, Main._stage);
                    window.setOnMouseMoved(handler);
                    window.setOnMousePressed(handler);
                    window.setOnMouseDragged(handler);
                    topPane.setOnMouseClicked(event -> {
                        switch (event.getClickCount()) {
                        case 1:
                            //FIXME
                            break;
                        case 2:
                            Main._stage.setMaximized(!Main._stage.isMaximized());
                            break;
                        }

                    });
                    KeyCombination backCombination = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
                    Main._stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                        if (backCombination.match(event)) {
                            back();
                        }

                    });
                    refresh.setOnMouseClicked(event -> {
                        clearContent();
                        loadContent(cur_path.id, "", current_tv);

                    });
                    settings.setOnMouseClicked(event -> {
                        settings_pane.setVisible(true);

                    });
                    settings_close.setOnMouseClicked(event -> settings_pane.setVisible(false));

                    Label[] settings_categories = { rendering, caching, hardware, software };
                    for (Label l : settings_categories) {
                        l.setOnMousePressed(getOnSettingsCategoryClickListener(l));
                    }
                    rendering.setStyle("-fx-text-fill: #555555;");
                    rendering.getStyleClass().remove("category");
                    rendering.getStyleClass().add("category2");
                    link.setOnAction(event -> {
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(new URI(link.getText()));
                            } catch (IOException | URISyntaxException e) {
                                logger.error(e);
                            }
                        }

                    });

                    attachWindowDragControlToElement(flashsafe);
                    flashsafe.setCursor(Cursor.MOVE);
                    attachWindowDragControlToElement(topPane);

                    String[] dlitems = { "xlarge", "large", "medium", "small", "tile", "list", "table" };
                    String[] dlinames = { "Огромные значки", "Большие значки", "Обычные значки", "Маленькие значки", "Плитка",
                            "Список", "Таблица" };
                    for (int i = 0; i < dlinames.length; i++) {
                        Label l = new Label();
                        l.setText(dlinames[i]);
                        l.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/" + dlitems[i] + ".png"))));
                        l.setStyle("-fx-text-fill: #353F4B ; -fx-font-size: 14px");
                        display_list.getItems().add(l);
                    }
                    display_slider.setMin(0.0);
                    display_slider.setMax(0.6);
                    display_slider.setValue(0.0);
                    display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/table.png"))));
                    display_choice.setOnAction(event -> display_menu.setVisible(!display_menu.isVisible()));
                }

            });
            return null;
        }
    }

    private void attachWindowDragControlToElement(Node element) {
        element.setOnMouseDragged(event -> {
            Main._stage.setX(event.getScreenX() - windowXPosition);
            Main._stage.setY(event.getScreenY() - windowYPosition);
        });
        element.setOnMousePressed(event -> {
            windowXPosition = event.getSceneX();
            windowYPosition = event.getSceneY();
        });
    }

    /**
     * Получаем иконку файла, размером 24x24
     * 
     * @param filename
     * @return
     */
    private static Image getFileIcon(String filename) {
        return IconUtil.getFileIcon(filename);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        Font myriadPro = Font.loadFont(getClass().getResourceAsStream("/font/myriadpro_regular.ttf"), 20);
        Label[] categories = { myfiles, docs, pictures, sounds, videos, loads, contacts };
        for (Label l : categories) {
            l.setFont(myriadPro);
            l.setOnMousePressed(getOnCategoryClickListener(l));
        }
        myfiles.getStyleClass().remove(0);
        myfiles.getStyleClass().add("category1");
        HttpAPI.getInstance().addListener(this);
        Future<Boolean> connect = Main.es.submit(new ConnectToCloudTask());
        while (!connect.isDone()) {
        }
        try {
            if ((boolean) connect.get()) {
                Main.es.submit(new AddHandlersTask());
                loadContent(cur_path.id, "", current_tv);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e);
        }
    }

    public void exit() {
        Main._stage.close();
    }

    private EventHandler<MouseEvent> getOnCategoryClickListener(Label source) {
        return new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Label[] categories = { myfiles, docs, pictures, sounds, videos, loads, contacts };
                for (Label l : categories) {
                    l.getStyleClass().remove("category1");
                    l.getStyleClass().add("category");
                }
                source.getStyleClass().remove("category");
                source.getStyleClass().add("category1");
            }

        };
    }

    private EventHandler<MouseEvent> getOnSettingsCategoryClickListener(Label source) {
        return new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                Label[] categories = { rendering, caching, hardware, software };
                for (Label l : categories) {
                    l.setStyle("-fx-text-fill: #ECEFF4;");
                    l.getStyleClass().remove("category2");
                    l.getStyleClass().add("category");
                }
                source.setStyle("-fx-text-fill: #555555;");
                source.getStyleClass().remove("category");
                source.getStyleClass().add("category2");
                if (source.equals(software)) {
                    software_pane.setVisible(true);
                } else {
                    software_pane.setVisible(false);
                }
            }

        };
    }

    private void backspace() {
        if (!pincode_textfield.getText().isEmpty()) {
            pincode_textfield.setText(pincode_textfield.getText().substring(0, pincode_textfield.getText().length() - 1));
        }
    }

    private void pincodeEnter(String num) {
        pin += num;
        pincode_textfield.setText(pincode_textfield.getText() + "*");
    }

    private void resetNums() {
        List<String> nums = new ArrayList<>();
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
        Button[] bnums = { one, two, three, four, five, six, seven, eight, nine, zero };
        for (Button b : bnums) {
            Random r = new Random();
            int rand = r.nextInt(nums.size());
            b.setText(nums.get(rand));
            nums.remove(rand);
        }
    }

    private EventHandler<MouseEvent> getOnNumClick(Button num) {
        return new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                pincodeEnter(num.getText());
                resetNums();
            }

        };
    }

    private EventHandler<MouseEvent> getOnElementClick(Label label) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    for (FSObject fso : content) {
                        if (fso.id == Integer.parseInt(label.getId())) {
                            current_element = fso;
                        }
                    }
                    switch (event.getClickCount()) {
                    case 1:
                        break;
                    case 2:
                        if (current_element.type.equals("dir")) {
                            if (current_element.pincode) {
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
        if (!pin.isEmpty()) {
            if (back) {
                loadContent(PARENT_PATH.isEmpty() ? 0 : PARENT_PATH.get(PARENT_PATH.size() - 1).id, pin, current_tv);
            } else {
                clearContent();
                loadContent(path, pin, current_tv);
            }
            pin = "";
            pincode_textfield.setText("");
        }
    }

    public void onCreatePathClick() {
        pathname_dialog.setVisible(true);
    }

    public void onPathnameSubmit() {
        pathname_dialog.setVisible(false);
        if (!pathname_textfield.getText().isEmpty()) {
            int id = HttpAPI.getInstance().createPath(cur_path.id, pincode, pathname_textfield.getText());
            FSObject path = new FSObject(id, "dir", pathname_textfield.getText(), "", 0, false, 0, System.currentTimeMillis(),
                    System.currentTimeMillis());
            Label label = new Label(path.name, new ImageView(folderBlackIcon));
            label.setFont(new Font("Ubuntu Condensed", 18));
            label.setTextFill(Paint.valueOf("#7C7C7C"));
            label.setId(String.valueOf(path.id));
            label.setPrefWidth(340);
            label.setOnMouseClicked(getOnElementClick(label));
            Tooltip tooltip = createTooltipFor(path);
            label.setTooltip(tooltip);
            currentDirectoryEntries.add(new TableRow("dir", label, "0", new Date().toLocaleString()));
            
            FSObject[] new_content = Arrays.copyOf(content, content.length + 1);
            new_content[content.length] = path;
            content = new_content;
        } else {
            pathname_dialog.setVisible(true);
        }
    }

    public void onUploadFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resourceBundle.getString("upload_file"));
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter(resourceBundle.getString("text_files"), "*.txt", "*.rtf", "*.doc", "*.docx"),
                new ExtensionFilter(resourceBundle.getString("image_files"), "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp",
                        "*.tiff", "*.ico"),
                new ExtensionFilter(resourceBundle.getString("audio_files"), "*.wav", "*.mp3", "*.aac", "*.wma", "*.amr"),
                new ExtensionFilter(resourceBundle.getString("video_files"), "*.wmv", "*.mp4", "*.avi", "*.mov", "*.flv",
                        "*.3gp", "*.3gpp"), new ExtensionFilter(resourceBundle.getString("all_types"), "*.*"));
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
                FSObject f = new FSObject(id, "file", selectedFile.getName(), selectedFile.getName().split("\\.")[selectedFile
                        .getName().split("\\.").length - 1].toLowerCase(), selectedFile.length(), false, 0,
                        System.currentTimeMillis(), System.currentTimeMillis());
                Label label = new Label(f.name, new ImageView(getFileIcon(f.name)));
                label.setFont(new Font("Ubuntu Condensed", 18));
                label.setTextFill(Paint.valueOf("#7C7C7C"));
                label.setId(String.valueOf(f.id));
                label.setPrefWidth(340);
                label.setOnMouseClicked(getOnElementClick(label));
                Tooltip tooltip = createTooltipFor(f);
                label.setTooltip(tooltip);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentDirectoryEntries.add(TableRow.fromFSObject(f, label, resourceBundle));
                    }
                });
                FSObject[] new_content = Arrays.copyOf(content, content.length + 1);
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
        if (!FORWARD_PATH.isEmpty()) {
            clearContent();
            loadContent(FORWARD_PATH.get(FORWARD_PATH.size() - 1).id, "", current_tv);
            FORWARD_PATH.remove(FORWARD_PATH.size() - 1);
        }
    }

    private boolean auth() {
        return HttpAPI.getInstance().auth();
    }

    private void clearContent() {
        currentDirectoryEntries.clear();
    }

    private Tooltip createTooltipFor(FSObject fsObject) {
        StringBuilder tooltipString = new StringBuilder();
        tooltipString
                .append(resourceBundle.getString("name"))
                .append(": ")
                .append(fsObject.name)
                .append(System.lineSeparator())
                .append(resourceBundle.getString("type"))
                .append(": ")
                .append(fsObject.type)
                .append(System.lineSeparator())
                .append("file".equals(fsObject.type) ? resourceBundle.getString("file_format") + ": " + fsObject.format : "")
                .append(System.lineSeparator())
                .append(resourceBundle.getString("size"))
                .append(": ")
                .append(String.valueOf(fsObject.size / 1024))
                .append(" КБ")
                .append(System.lineSeparator())
                .append("dir".equals(fsObject.type) ? resourceBundle.getString("number_of_files") + ": " + fsObject.count + ""
                        : "").append(System.lineSeparator()).append(resourceBundle.getString("creation_date")).append(": ")
                .append(new Date(fsObject.create_time * 1000).toLocaleString()).append(System.lineSeparator())
                .append(resourceBundle.getString("last_update")).append(": ")
                .append(new Date(fsObject.update_time * 1000).toLocaleString()).append(System.lineSeparator());
        return new Tooltip(tooltipString.toString());
    }

    private int loadContent(int path_id, String pin, TreeView tv) {
        Object[] answer = HttpAPI.getInstance().getContent(path_id, pin);
        int code = (int) answer[1];
        switch (code) {
        case 0:
            content = (FSObject[]) answer[0];
            List<FSObject> path_childrens = new ArrayList<>();
            for (FSObject currentFSObject : content) {
                ImageView icon = new ImageView(currentFSObject.type.equals("dir") ? currentFSObject.pincode ? lockBlackIcon
                        : currentFSObject.count > 0 ? folderFull : folderBlackIcon
                        : /* fileIcon */getFileIcon(currentFSObject.name));
                Label label = new Label(currentFSObject.name, icon);
                label.setFont(new Font("Ubuntu Condensed", 14));
                label.setTextFill(Paint.valueOf("#000"));
                label.setId(String.valueOf(currentFSObject.id));
                label.setPrefWidth(340);
                label.setOnMouseClicked(getOnElementClick(label));
                Tooltip tooltip = createTooltipFor(currentFSObject);
                label.setTooltip(tooltip);
                currentDirectoryEntries.add(TableRow.fromFSObject(currentFSObject, label, resourceBundle));
                if (currentFSObject.type.equals("dir")) {
                    path_childrens.add(currentFSObject);
                }
            }
            CHILDRENS.put(path_id, path_childrens);
            if (!pin.equals("")) {
                pincode = pincode_textfield.getText();
                pincode_textfield.setText("");
            }
            if (back) {
                if (!PARENT_PATH.isEmpty() && PARENT_PATH.size() > 1) {
                    PARENT_PATH.remove(PARENT_PATH.size() - 1);
                    cur_path = PARENT_PATH.get(PARENT_PATH.size() - 1);
                } else {
                    cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
                }
                back = false;
            } else {
                PARENT_PATH.add(cur_path);
                cur_path = current_element;
            }
            break;
        case 1:
            CHILDRENS.put(path_id, new ArrayList<>());
            if (!pin.equals("")) {
                pincode = pincode_textfield.getText();
                pincode_textfield.setText("");
            }
            if (back) {
                if (!PARENT_PATH.isEmpty() && PARENT_PATH.size() > 1) {
                    PARENT_PATH.remove(PARENT_PATH.size() - 1);
                    cur_path = PARENT_PATH.get(PARENT_PATH.size() - 1);
                } else {
                    cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
                }
                back = false;
            } else {
                PARENT_PATH.add(cur_path);
                cur_path = current_element;
            }
            break;
        case 2:
            break;
        case 3:
            path = path_id;
            pincode_dialog.setVisible(true);
            break;
        case 4:
            break;
        }
        return code;
    }

    private EventHandler<MouseEvent> getTreeViewItemClickListener(int path_id) {
        return new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    loadContent(path_id, "", current_tv);
                }
            }
        };
    }

    public void toggleSettings() {
        /*
         * if(menu_opened) {
         * settings.setStyle("-fx-background-color: transparent");
         * settings.setGraphic(new ImageView(new
         * Image(getClass().getResourceAsStream
         * ("/ru/flashsafe/img/sttngs.png")))); menu.setVisible(false);
         * menu_opened = false; } else {
         * settings.setStyle("-fx-background-color: #F3F3F3");
         * settings.setGraphic(new ImageView(new
         * Image(getClass().getResourceAsStream
         * ("/ru/flashsafe/img/sttngs.png")))); menu.setVisible(true);
         * menu_opened = true; }
         */
    }

    public static class TableRow {

        private SimpleStringProperty type;

        private Label name;

        private SimpleStringProperty size;

        private SimpleStringProperty createDate;

        private TableRow(String _type, Label _name, String _size, String _create_date) {
            this.type = new SimpleStringProperty(_type);
            this.name = _name;
            this.size = new SimpleStringProperty(_size);
            this.createDate = new SimpleStringProperty(_create_date);
        }

        public static TableRow fromFSObject(FSObject fsObject, Label label, ResourceBundle bundle) {
            return new TableRow(fsObject.type.equals("file") ? bundle.getString("file") : bundle.getString("folder"), label,
                    String.valueOf(fsObject.size / 1024) + " КБ", new Date(fsObject.create_time * 1000).toLocaleString());
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
