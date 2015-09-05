/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.awt.Desktop;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
import ru.flashsafe.util.FontUtil;
import ru.flashsafe.util.FontUtil.FontType;
import ru.flashsafe.util.ResizeHandler;

/**
 * FXML Controller class
 * 
 * @author alex_xpert
 */
public class MainSceneController implements Initializable, UploadProgressListener {

    private static final Logger logger = LogManager.getLogger(MainSceneController.class);
    private final Image folderBlackIcon = new Image(getClass().getResourceAsStream("/img/fs/folder_empty.png"));
    private final Image lockBlackIcon = new Image(getClass().getResourceAsStream("/img/fs/folder_lock.png"));
    private final Image folderFull = new Image(getClass().getResourceAsStream("/img/fs/folder.png"));

    private final List<FSObject> PARENT_PATH = new ArrayList<>();
    private final Map<Integer, List<FSObject>> CHILDRENS = new HashMap<>();
    private FSObject cur_path = new FSObject(0, "dir", "/", "", 0, false, 0, 0, 0);
    private FSObject current_element = cur_path;
    private FSObject[] content;
    private boolean back = false;
    private int path;
    private String pincode = "";
    // public static RemoteEmulatorTokenService rets;
    private TreeView current_tv = null;

    private double windowXPosition;
    private double windowYPosition;

    private String pin = "";

    private ObservableList<TableRow> currentDirectoryEntries = FXCollections.observableArrayList();

    private ResourceBundle resourceBundle;
    
    private int view_type = 6;
    
    private FSObject[] temp_content;

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
    @FXML
    private AnchorPane files_area;
    @FXML
    private GridPane gfiles;
    @FXML
    private ListView lfiles;
    @FXML
    private ScrollPane scroll_pane;
    @FXML
    private TextField search_field;

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
                    gfiles.setOnDragDetected(event -> {
                        Dragboard db = files.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        List<File> list = new ArrayList<>();
                        list.add(new File("./mime.mime"));
                        content.putFiles(list);
                        db.setContent(content);
                        event.consume();

                    });
                    gfiles.setOnDragOver(event -> {
                        if (event.getGestureSource() != files && event.getDragboard().hasFiles()) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        }
                        event.consume();
                    });
                    gfiles.setOnDragDropped(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasFiles()) {
                            final File f = db.getFiles().get(0);
                            uploadFile(f);
                        }
                        event.setDropCompleted(true);
                        event.consume();

                    });
                    
                    lfiles.setOnDragDetected(event -> {
                        Dragboard db = files.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        List<File> list = new ArrayList<>();
                        list.add(new File("./mime.mime"));
                        content.putFiles(list);
                        db.setContent(content);
                        event.consume();

                    });
                    lfiles.setOnDragOver(event -> {
                        if (event.getGestureSource() != files && event.getDragboard().hasFiles()) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        }
                        event.consume();
                    });
                    lfiles.setOnDragDropped(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasFiles()) {
                            final File f = db.getFiles().get(0);
                            uploadFile(f);
                        }
                        event.setDropCompleted(true);
                        event.consume();

                    });
                    
                    files.setOnDragDetected(event -> {
                        Dragboard db = files.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        List<File> list = new ArrayList<>();
                        list.add(new File("./mime.mime"));
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
                        Dragboard db = event.getDragboard();
                        if (db.hasFiles()) {
                            final File f = db.getFiles().get(0);
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

                    files.setItems(currentDirectoryEntries);

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
                            display_menu.setVisible(false);
                            break;
                        case 2:
                            Main._stage.setMaximized(!Main._stage.isMaximized());
                            break;
                        }

                    });
                    
                    KeyCombination backCombination = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);
                    KeyCombination createFolderCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN);
                    Main._stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                        if (backCombination.match(event)) {
                            back();
                        } else if(createFolderCombination.match(event)) {
                            onCreatePathClick();
                        }
                    });
                    
                    settings.setOnMouseClicked(event -> {
                        settings_pane.setVisible(true);

                    });
                    settings_close.setOnMouseClicked(event -> settings_pane.setVisible(false));

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
                    display_list.getItems().get(0).setOnMouseClicked(event -> { changeToXLargeView(); });
                    display_list.getItems().get(1).setOnMouseClicked(event -> { changeToLargeView(); });
                    display_list.getItems().get(2).setOnMouseClicked(event -> { changeToMediumView(); });
                    display_list.getItems().get(3).setOnMouseClicked(event -> { changeToSmallView(); });
                    display_list.getItems().get(4).setOnMouseClicked(event -> { changeToTileView(); });
                    display_list.getItems().get(5).setOnMouseClicked(event -> { changeToListView(); });
                    display_list.getItems().get(6).setOnMouseClicked(event -> { changeToTableView(); });
                    display_slider.setMin(0);
                    display_slider.setMax(6);
                    display_slider.setValue(0);
                    display_slider.setMajorTickUnit(1);
                    display_slider.setMinorTickCount(0);
                    display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/itable.png"))));
                    display_choice.setOnAction(event -> display_menu.setVisible(!display_menu.isVisible()));
                    display_choice.setStyle("-fx-background-color: transparent");
                    
                    Font myriadPro = Font.loadFont(getClass().getResourceAsStream("/font/myriadpro_regular.ttf"), 20);
                    Label[] categories = { myfiles, docs, pictures, sounds, videos, loads, contacts };
                    for (Label l : categories) {
                        l.setFont(myriadPro);
                        l.setOnMousePressed(getOnCategoryClickListener(l));
                    }
                    myfiles.getStyleClass().remove(0);
                    myfiles.getStyleClass().add("category1");
                    
                    pincode_textfield.setOnKeyTyped(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent event) {
                            pin += event.getCharacter();
                        }
                    });
                    
                    gfiles.getColumnConstraints().clear();
                    gfiles.getRowConstraints().clear();
                    
                    search_field.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            List tmp = new ArrayList<FSObject>();
                            for(FSObject fso : content) {
                                if(fso.name.contains(newValue)) {
                                    tmp.add(fso);
                                }
                            }
                            temp_content = new FSObject[tmp.size()];
                            tmp.toArray(temp_content);
                            switch(view_type) {
                                case 0:
                                    changeToGridView(temp_content, 96, false);
                                    break;
                                case 1:
                                    changeToGridView(temp_content, 64, false);
                                    break;
                                case 2:
                                    changeToGridView(temp_content, 48, false);
                                    break;
                                case 3:
                                    changeToGridView(temp_content, 24, false);
                                    break;
                                case 4:
                                    changeToGridView(temp_content, 48, true);
                                    break;
                                case 5:
                                    changeToListView1(temp_content);
                                    break;
                                case 6:
                                    changeToTableView1(temp_content);
                                    break;
                            }
                        }
                    });
                    display_slider.valueProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                            int num = (int) (newValue.doubleValue() * 10);
//                            switch(num) {
//                                case 0:
//                                    if(view_type != 0) changeToXLargeView();
//                                    break;
//                                case 1:
//                                    if(view_type != 1) changeToLargeView();
//                                    break;
//                                case 2:
//                                    if(view_type != 2) changeToMediumView();
//                                    break;
//                                case 3:
//                                    if(view_type != 3) changeToSmallView();
//                                    break;
//                                case 4:
//                                    if(view_type != 4) changeToTileView();
//                                    break;
//                                case 5:
//                                    if(view_type != 5) changeToListView();
//                                    break;
//                                case 6:
//                                    if(view_type != 6) changeToTableView();
//                                    break;
//                            }
                        }
                     });
                //TODO
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
    
    private void buildSelectViewControl() {
        String[] dlitems = { "xlarge", "large", "medium", "small", "tile", "list", "table" };
        String[] dlinames = { "Огромные значки", "Большие значки", "Обычные значки", "Маленькие значки", "Плитка",
                "Список", "Таблица" };
        for (int i = 0; i < dlinames.length; i++) {
            Label itemLabel = new Label();
            itemLabel.setText(dlinames[i]);
            itemLabel.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/" + dlitems[i] + ".png"))));
            itemLabel.setStyle("-fx-text-fill: #353F4B ; -fx-font-size: 14px");
            display_list.getItems().add(itemLabel);
        }
        display_slider.setMin(0.0);
        display_slider.setMax(0.6);
        display_slider.setValue(0.0);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/table.png"))));
        display_choice.setOnAction(event -> display_menu.setVisible(!display_menu.isVisible()));
    }

    /**
     * Получаем иконку файла
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
        Font leftMenuFont = FontUtil.instance().font(FontType.LEFT_MENU);
        Label[] leftMenuCategories = { myfiles, docs, pictures, sounds, videos, loads, contacts };
        for (Label categoryLabel : leftMenuCategories) {
            categoryLabel.setFont(leftMenuFont);
            categoryLabel.setOnMousePressed(getOnCategoryClickListener(categoryLabel));
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

    public void refresh() {
        clearContent();
        loadContent(cur_path.id, "", current_tv);
    }
    
    public void exit() {
        Main._stage.close();
        Platform.exit();
    }

    private EventHandler<MouseEvent> getOnCategoryClickListener(Label source) {
        return event -> {
            Label[] categories = { myfiles, docs, pictures, sounds, videos, loads, contacts };
            for (Label category : categories) {
                removeSelection(category);
            }
            applySelection(source);
        };
    }

    // FIXME - rename styles
    private static void applySelection(Label label) {
        label.getStyleClass().remove("category");
        label.getStyleClass().add("category1");
    }

    // FIXME - rename styles
    private static void removeSelection(Label label) {
        label.getStyleClass().remove("category1");
        label.getStyleClass().add("category");
    }

    private EventHandler<MouseEvent> getOnSettingsCategoryClickListener(Label source) {
        return event -> {
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
        };
    }

    public void backspace() {
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
            ImageView icon = new ImageView(folderBlackIcon);
            Label label = new Label(path.name);
            label.setFont(new Font("Ubuntu Condensed", 18));
            label.setTextFill(Paint.valueOf("#7C7C7C"));
            label.setId(String.valueOf(path.id));
            label.setPrefWidth(340);
            Tooltip tooltip = createTooltipFor(path);
            label.setTooltip(tooltip);
            HBox hbox;
            VBox vbox;
            int col = 0, row = 0;
            if(view_type < 5) { // Вычисляем ячейку
                col = gfiles.getChildren().size() % gfiles.getColumnConstraints().size();
                row = col == 0 ? gfiles.getRowConstraints().size() : gfiles.getRowConstraints().size() - 1;
            }
            switch(view_type) {
                case 0: // XLarge
                    icon.setFitHeight(96);
                    icon.setFitWidth(96);
                    vbox = new VBox();
                    vbox.getChildren().add(icon);
                    vbox.getChildren().add(label);
                    vbox.setOnMouseClicked(getOnElementClick(label));
                    gfiles.add(vbox, col, row);
                    break;
                case 1: // Large
                    icon.setFitHeight(64);
                    icon.setFitWidth(64);
                    vbox = new VBox();
                    vbox.getChildren().add(icon);
                    vbox.getChildren().add(label);
                    vbox.setOnMouseClicked(getOnElementClick(label));
                    gfiles.add(vbox, col, row);
                    break;
                case 2: // Medium
                    icon.setFitHeight(48);
                    icon.setFitWidth(48);
                    vbox = new VBox();
                    vbox.getChildren().add(icon);
                    vbox.getChildren().add(label);
                    vbox.setOnMouseClicked(getOnElementClick(label));
                    gfiles.add(vbox, col, row);
                    col++;
                    break;
                case 3: // Small
                    icon.setFitHeight(24);
                    icon.setFitWidth(24);
                    vbox = new VBox();
                    vbox.getChildren().add(icon);
                    vbox.getChildren().add(label);
                    vbox.setOnMouseClicked(getOnElementClick(label));
                    gfiles.add(vbox, col, row);
                    break;
                case 4: // Tile
                    icon.setFitHeight(48);
                    icon.setFitWidth(48);
                    label.setPadding(new Insets(15, 0, 0, 5));
                    hbox = new HBox();
                    hbox.getChildren().add(icon);
                    hbox.getChildren().add(label);
                    hbox.setOnMouseClicked(getOnElementClick(label));
                    gfiles.add(hbox, col, row);
                    break;
                case 5: // List
                    icon.setFitHeight(24);
                    icon.setFitWidth(24);
                    label.setOnMouseClicked(getOnElementClick(label));
                    label.setGraphic(icon);
                    lfiles.getItems().add(label);
                    break;
                case 6: // Table
                    icon.setFitHeight(24);
                    icon.setFitWidth(24);
                    label.setOnMouseClicked(getOnElementClick(label));
                    label.setGraphic(icon);
                    currentDirectoryEntries.add(TableRow.fromFSObject(path, label, resourceBundle));
                    break;
            }
            FSObject[] new_content = new FSObject[content.length + 1];
            for (int i = 0; i < content.length; i++) {
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
                ImageView icon = new ImageView(getFileIcon(f.name));
                Label label = new Label(f.name);
                label.setMaxHeight(24);
                label.setFont(new Font("Ubuntu Condensed", 18));
                label.setTextFill(Paint.valueOf("#7C7C7C"));
                label.setId(String.valueOf(f.id));
                Tooltip tooltip = createTooltipFor(f);
                label.setTooltip(tooltip);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        HBox hbox;
                        VBox vbox;
                        int col = 0, row = 0;
                        if(view_type < 5) { // Вычисляем ячейку
                            col = gfiles.getChildren().size() % gfiles.getColumnConstraints().size();
                            row = col == 0 ? gfiles.getRowConstraints().size() : gfiles.getRowConstraints().size() - 1;
                        }
                        switch(view_type) {
                            case 0: // XLarge
                                icon.setFitHeight(96);
                                icon.setFitWidth(96);
                                vbox = new VBox();
                                vbox.getChildren().add(icon);
                                vbox.getChildren().add(label);
                                vbox.setOnMouseClicked(getOnElementClick(label));
                                gfiles.add(vbox, col, row);
                                break;
                            case 1: // Large
                                icon.setFitHeight(64);
                                icon.setFitWidth(64);
                                vbox = new VBox();
                                vbox.getChildren().add(icon);
                                vbox.getChildren().add(label);
                                vbox.setOnMouseClicked(getOnElementClick(label));
                                gfiles.add(vbox, col, row);
                                break;
                            case 2: // Medium
                                icon.setFitHeight(48);
                                icon.setFitWidth(48);
                                vbox = new VBox();
                                vbox.getChildren().add(icon);
                                vbox.getChildren().add(label);
                                vbox.setOnMouseClicked(getOnElementClick(label));
                                gfiles.add(vbox, col, row);
                                col++;
                                break;
                            case 3: // Small
                                icon.setFitHeight(24);
                                icon.setFitWidth(24);
                                vbox = new VBox();
                                vbox.getChildren().add(icon);
                                vbox.getChildren().add(label);
                                vbox.setOnMouseClicked(getOnElementClick(label));
                                gfiles.add(vbox, col, row);
                                break;
                            case 4: // Tile
                                icon.setFitHeight(48);
                                icon.setFitWidth(48);
                                label.setPadding(new Insets(15, 0, 0, 5));
                                hbox = new HBox();
                                hbox.getChildren().add(icon);
                                hbox.getChildren().add(label);
                                hbox.setOnMouseClicked(getOnElementClick(label));
                                gfiles.add(hbox, col, row);
                                break;
                            case 5: // List
                                icon.setFitHeight(24);
                                icon.setFitWidth(24);
                                label.setGraphic(icon);
                                label.setOnMouseClicked(getOnElementClick(label));
                                lfiles.getItems().add(label);
                                break;
                            case 6: // Table
                                icon.setFitHeight(24);
                                icon.setFitWidth(24);
                                label.setGraphic(icon);
                                label.setOnMouseClicked(getOnElementClick(label));
                                currentDirectoryEntries.add(TableRow.fromFSObject(f, label, resourceBundle));
                                break;
                        }
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
        clearContent();
        loadContent(PARENT_PATH.isEmpty() ? 0 : PARENT_PATH.get(PARENT_PATH.size() - 1).id, "", current_tv);
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
                .append(System.lineSeparator());
        if ("file".equals(fsObject.type)) {
            tooltipString.append(resourceBundle.getString("file_format")).append(": ").append(fsObject.format)
                    .append(System.lineSeparator());
        }
        tooltipString
                .append(resourceBundle.getString("size"))
                .append(": ")
                .append(String.valueOf(fsObject.size / 1024))
                .append(" КБ")
                .append(System.lineSeparator());
        if ("dir".equals(fsObject.type)) {
            tooltipString.append(resourceBundle.getString("number_of_files")).append(": ").append(fsObject.count)
                    .append(System.lineSeparator());
        }
        tooltipString.append(resourceBundle.getString("creation_date")).append(": ")
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
            int columns_count = 0;
            switch(view_type) {
                case 0: // XLarge
                    columns_count = (int) ((gfiles.getWidth() - 10) / (96 + 10));
                    break;
                case 1: // Large
                    columns_count = (int) ((gfiles.getWidth() - 10) / (64 + 10));
                    break;
                case 2: // Medium
                    columns_count = (int) ((gfiles.getWidth() - 10) / (48 + 10));
                    break;
                case 3: // Small
                    columns_count = (int) ((gfiles.getWidth() - 10) / (24 + 10));
                    break;
                case 4: // Tile
                    columns_count = (int) ((gfiles.getWidth() - 10) / 150);
                    break;
            }
            int col = 0, row = 0;
            for (int i = 0; i < content.length; i++) {
                FSObject fso = content[i];
                ImageView icon = new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : fso.count > 0 ? folderFull
                        : folderBlackIcon : /* fileIcon */getFileIcon(fso.name));
                Label label = new Label(fso.name);
                label.setFont(new Font("Ubuntu Condensed", 14));
                label.setTextFill(Paint.valueOf("#000"));
                label.setId(String.valueOf(fso.id));
                Tooltip tooltip = createTooltipFor(fso);
                label.setTooltip(tooltip);
                HBox hbox;
                VBox vbox;
                switch(view_type) {
                    case 0: // XLarge
                        icon.setFitHeight(96);
                        icon.setFitWidth(96);
                        vbox = new VBox();
                        vbox.getChildren().add(icon);
                        vbox.getChildren().add(label);
                        vbox.setOnMouseClicked(getOnElementClick(label));
                        gfiles.add(vbox, col, row);
                        col++;
                        if(col == columns_count) {
                            col = 0;
                            row++;
                        }
                        break;
                    case 1: // Large
                        icon.setFitHeight(64);
                        icon.setFitWidth(64);
                        vbox = new VBox();
                        vbox.getChildren().add(icon);
                        vbox.getChildren().add(label);
                        vbox.setOnMouseClicked(getOnElementClick(label));
                        gfiles.add(vbox, col, row);
                        col++;
                        if(col == columns_count) {
                            col = 0;
                            row++;
                        }
                        break;
                    case 2: // Medium
                        icon.setFitHeight(48);
                        icon.setFitWidth(48);
                        vbox = new VBox();
                        vbox.getChildren().add(icon);
                        vbox.getChildren().add(label);
                        vbox.setOnMouseClicked(getOnElementClick(label));
                        gfiles.add(vbox, col, row);
                        col++;
                        if(col == columns_count) {
                            col = 0;
                            row++;
                        }
                        break;
                    case 3: // Small
                        icon.setFitHeight(24);
                        icon.setFitWidth(24);
                        vbox = new VBox();
                        vbox.getChildren().add(icon);
                        vbox.getChildren().add(label);
                        vbox.setOnMouseClicked(getOnElementClick(label));
                        gfiles.add(vbox, col, row);
                        col++;
                        if(col == columns_count) {
                            col = 0;
                            row++;
                        }
                        break;
                    case 4: // Tile
                        icon.setFitHeight(48);
                        icon.setFitWidth(48);
                        label.setPadding(new Insets(15, 0, 0, 5));
                        hbox = new HBox();
                        hbox.getChildren().add(icon);
                        hbox.getChildren().add(label);
                        hbox.setOnMouseClicked(getOnElementClick(label));
                        gfiles.add(hbox, col, row);
                        col++;
                        if(col == columns_count) {
                            col = 0;
                            row++;
                        }
                        break;
                    case 5: // List
                        icon.setFitHeight(24);
                        icon.setFitWidth(24);
                        label.setGraphic(icon);
                        label.setOnMouseClicked(getOnElementClick(label));
                        lfiles.getItems().add(label);
                        break;
                    case 6: // Table
                        icon.setFitHeight(24);
                        icon.setFitWidth(24);
                        label.setGraphic(icon);
                        label.setOnMouseClicked(getOnElementClick(label));
                        currentDirectoryEntries.add(TableRow.fromFSObject(fso, label, resourceBundle));
                        break;
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

    public void changeToGridView(FSObject[] content, int size, boolean tile) {
        files.setVisible(false);
        lfiles.setVisible(false);
        gfiles.getChildren().clear();
        gfiles.setHgap(10);
        gfiles.setVgap(10);
        gfiles.setPadding(new Insets(10));
        int csize = tile ? 150 : size;
        int columns_count = (int) ((gfiles.getWidth() - 10) / (csize + 10));
        int col = 0, row = 0;
        for(FSObject fso : content) {
            ImageView icon = new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : fso.count > 0 ? folderFull
                    : folderBlackIcon : getFileIcon(fso.name));
            icon.setFitHeight(size);
            icon.setFitWidth(size);
            Label name = new Label(fso.name);
            name.setId(String.valueOf(fso.id));
            name.setOnMouseClicked(getOnElementClick(name));
            name.setTooltip(createTooltipFor(fso));
            if(tile) {
                name.setPadding(new Insets(15, 0, 0, 5));
                HBox box = new HBox();
                box.getChildren().add(icon);
                box.getChildren().add(name);
                gfiles.add(box, col, row);
            } else {
                name.setMaxWidth(size);
                VBox box = new VBox();
                box.getChildren().add(icon);
                box.getChildren().add(name);
                gfiles.add(box, col, row);
            }
            col++;
            if(col == columns_count) {
                col = 0;
                row++;
            }
        }
        scroll_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gfiles.setVisible(true);
    }
    
    public void changeToXLargeView() {
        changeToGridView(content, 96, false);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/ixlarge.png"))));
        display_slider.setValue(6);
        display_menu.setVisible(false);
        view_type = 0;
    }
    
    public void changeToLargeView() {
        changeToGridView(content, 64, false);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/ilarge.png"))));
        display_slider.setValue(5);
        display_menu.setVisible(false);
        view_type = 1;
    }
    
    public void changeToMediumView() {
        changeToGridView(content, 48, false);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/imedium.png"))));
        display_slider.setValue(4);
        display_menu.setVisible(false);
        view_type = 2;
    }
    
    public void changeToSmallView() {
        changeToGridView(content, 24, true);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/ismall.png"))));
        display_slider.setValue(3);
        display_menu.setVisible(false);
        view_type = 3;
    }
    
    public void changeToTileView() {
        changeToGridView(content, 48, true);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/itile.png"))));
        display_slider.setValue(2);
        display_menu.setVisible(false);
        view_type = 4;
    }
    
    public void changeToListView() {
        changeToListView1(content);
    }
    
    private void changeToListView1(FSObject[] content) {
        gfiles.setVisible(false);
        files.setVisible(false);
        lfiles.getItems().clear();
        for(FSObject fso : content) {
            ImageView icon = new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : fso.count > 0 ? folderFull
                    : folderBlackIcon : getFileIcon(fso.name));
            icon.setFitHeight(24);
            icon.setFitWidth(24);
            Label name = new Label(fso.name, icon);
            name.setId(String.valueOf(fso.id));
            name.setOnMouseClicked(getOnElementClick(name));
            name.setTooltip(createTooltipFor(fso));
            lfiles.getItems().add(name);
        }
        scroll_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        lfiles.setVisible(true);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/ilist.png"))));
        display_slider.setValue(0.1);
        display_menu.setVisible(false);
        view_type = 5;
    }
    
    public void changeToTableView() {
        changeToTableView1(content);
    }
    
    private void changeToTableView1(FSObject[] content) {
        gfiles.setVisible(false);
        lfiles.setVisible(false);
        files.getItems().clear();
        currentDirectoryEntries.clear();
        for(FSObject fso : content) {
            ImageView icon = new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : fso.count > 0 ? folderFull
                    : folderBlackIcon : getFileIcon(fso.name));
            icon.setFitHeight(24);
            icon.setFitWidth(24);
            Label name = new Label(fso.name, icon);
            name.setId(String.valueOf(fso.id));
            name.setOnMouseClicked(getOnElementClick(name));
            name.setTooltip(createTooltipFor(fso));
            currentDirectoryEntries.add(TableRow.fromFSObject(fso, name, resourceBundle));
        }
        files.setItems(currentDirectoryEntries);
        scroll_pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll_pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        files.setVisible(true);
        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/itable.png"))));
        display_slider.setValue(0.0);
        display_menu.setVisible(false);
        view_type = 6;
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
          
