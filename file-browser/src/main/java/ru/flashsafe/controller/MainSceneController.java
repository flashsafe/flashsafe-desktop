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
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.FileController;
import ru.flashsafe.Main;
import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.FlashSafeSystem;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.event.FileObjectSecurityEvent;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult.ResultType;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.model.FSObject;
import ru.flashsafe.perspective.ListPerspective;
import ru.flashsafe.perspective.Perspective;
import ru.flashsafe.perspective.PerspectiveManager;
import ru.flashsafe.perspective.PerspectiveType;
import ru.flashsafe.perspective.TablePerspective;
import ru.flashsafe.util.FileObjectViewHelper;
import ru.flashsafe.util.FontUtil;
import ru.flashsafe.util.FontUtil.FontType;
import ru.flashsafe.util.HistoryObject;
import ru.flashsafe.util.WaitForEvent;

/**
 * FXML Controller class
 * 
 * @author alex_xpert
 */
public class MainSceneController implements Initializable, FileController, FileObjectSecurityHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainSceneController.class);

    private FSObject[] content;

    private double windowXPosition;

    private double windowYPosition;

    private String pin = "";

    private ResourceBundle resourceBundle;

    private PerspectiveType currentPerspective = PerspectiveType.TABLE;

    private WaitForEvent pincodeEnteredEvent = new WaitForEvent();

    private PerspectiveManager perspectiveManager;

    private FlashSafeSystem flashSafeSystem;

    private FileManager fileManager;

    private final ObservableList<FileObject> currentFolderEntries = FXCollections.observableArrayList();

    private HistoryObject<String> historyObject = new HistoryObject<>();

    private String currentFolder = FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX;

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
    private TableView<FileObject> files;
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
    private ListView<FileObject> lfiles;
    @FXML
    private ScrollPane scroll_pane;
    @FXML
    private TextField search_field;

    private class AddHandlersTask implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    files.setPlaceholder(new Label(""));
                    pathname_textfield.setOnKeyPressed(event -> {
                        KeyCode keyCode = event.getCode();
                        if (keyCode == KeyCode.ESCAPE) {
                            hidePathDialog();
                        }
                    });

                    backspace.setOnMouseClicked(event -> backspace());

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
                                LOGGER.error("Unable to open FlashSafe website", e);
                            }
                        }

                    });

                    attachWindowDragControlToElement(flashsafe);
                    flashsafe.setCursor(Cursor.MOVE);
                    attachWindowDragControlToElement(topPane);

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

                    KeyCombination backwardCombination = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);
                    KeyCombination forwardCombination = new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);
                    KeyCombination createFolderCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN);
                    Main._stage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
                        if (backwardCombination.match(event)) {
                            navigateBackward();
                        } else if (forwardCombination.match(event)) {
                            navigateForward();
                        } else if (createFolderCombination.match(event)) {
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

                    Font myriadPro = Font.loadFont(getClass().getResourceAsStream("/font/myriadpro_regular.ttf"), 20);
                    Label[] categories = { myfiles, docs, pictures, sounds, videos, loads, contacts };
                    for (Label l : categories) {
                        l.setFont(myriadPro);
                        l.setOnMousePressed(getOnCategoryClickListener(l));
                    }

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
                            List<FSObject> tmp = new ArrayList<>();
                            for (FSObject fso : content) {
                                if (fso.name.contains(newValue)) {
                                    tmp.add(fso);
                                }
                            }
                            // temp_content = new FSObject[tmp.size()];
                            // tmp.toArray(temp_content);
                            // switch (currentPerspective) {
                            // case ICONS_X_LARGE:
                            // changeToGridView(temp_content, 96, false);
                            // break;
                            // case ICONS_LARGE:
                            // changeToGridView(temp_content, 64, false);
                            // break;
                            // case ICONS_MEDIUM:
                            // changeToGridView(temp_content, 48, false);
                            // break;
                            // case ICONS_SMALL:
                            // changeToGridView(temp_content, 24, false);
                            // break;
                            // case TILE:
                            // changeToGridView(temp_content, 48, true);
                            // break;
                            // case LIST:
                            // changeToListView1(temp_content);
                            // break;
                            // case TABLE:
                            // changeToTableView1(temp_content);
                            // break;
                            // }
                        }
                    });
                    buildSelectViewControl();
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
        String[] dlitems = { /* "xlarge", "large", "medium", "small", "tile", */"list", "table" };
        String[] dlinames = { /*
                               * "Огромные значки", "Большие значки",
                               * "Обычные значки", "Маленькие значки", "Плитка",
                               */resourceBundle.getString("perspective.list"), resourceBundle.getString("perspective.table") };
        for (int i = 0; i < dlinames.length; i++) {
            Label perspectiveLabel = new Label();
            perspectiveLabel.setText(dlinames[i]);
            perspectiveLabel.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/" + dlitems[i] + ".png"))));
            perspectiveLabel.setStyle("-fx-text-fill: #353F4B ; -fx-font-size: 14px");
            display_list.getItems().add(perspectiveLabel);
        }
        display_list.getItems().get(0).setOnMouseClicked(event -> {
            display_slider.setValue(1);
            switchPerspectiveTo(PerspectiveType.LIST);
            display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/ilist.png"))));
            display_menu.setVisible(false);
        });
        display_list.getItems().get(1).setOnMouseClicked(event -> {
            display_slider.setValue(0);
            switchPerspectiveTo(PerspectiveType.TABLE);
            display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/itable.png"))));
            display_menu.setVisible(false);
        });

        display_slider.setMajorTickUnit(1);
        display_slider.setMinorTickCount(0);
        display_slider.setSnapToTicks(true);

        display_slider.setMin(0);
        display_slider.setMax(1);
        display_slider.setValue(0);

        display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/itable.png"))));
        display_choice.setOnAction(event -> display_menu.setVisible(!display_menu.isVisible()));
        display_choice.setStyle("-fx-background-color: transparent");

        display_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // if (display_slider.isValueChanging()) {
                // // FIXME apply skipped value
                // return;
                // }
                int num = newValue.intValue();
                switch (num) {
                case 0:
                    if (currentPerspective != PerspectiveType.TABLE)
                        switchPerspectiveTo(PerspectiveType.TABLE);
                    display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/ilist.png"))));
                    display_menu.setVisible(false);
                    break;
                case 1:
                    if (currentPerspective != PerspectiveType.LIST)
                        switchPerspectiveTo(PerspectiveType.LIST);
                    display_choice.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/img/itable.png"))));
                    display_menu.setVisible(false);
                    break;
                }
            }
        });
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

        flashSafeSystem = FlashSafeApplication.flashSafeSystem();
        fileManager = flashSafeSystem.fileManager();
        flashSafeSystem.fileManagementService().registerFileObjectSecurityHandler(this);

        perspectiveManager = new PerspectiveManager(getPerspectives());
        perspectiveManager.switchTo(PerspectiveType.TABLE);

        Main.es.submit(new AddHandlersTask());
        browseFolder(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX);
    }

    private List<Perspective> getPerspectives() {
        FileObjectViewHelper fileObjectViewHelper = new FileObjectViewHelper(resourceBundle);

        List<Perspective> perspectives = new ArrayList<>();
        TablePerspective tablePerspective = new TablePerspective(files, currentFolderEntries, fileObjectViewHelper, this);
        perspectives.add(tablePerspective);

        ListPerspective listPerspective = new ListPerspective(lfiles, currentFolderEntries, fileObjectViewHelper, this);
        perspectives.add(listPerspective);
        return perspectives;
    }

    public void refresh() {
        listFolder(currentFolder);
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

    private void showPathDialog() {
        pathname_dialog.setVisible(true);
        pathname_textfield.requestFocus();
    }

    private void hidePathDialog() {
        pathname_dialog.setVisible(false);
        pathname_textfield.setText("");
    }

    public void onCreatePathClick() {
        showPathDialog();
    }

    public void onPathnameSubmit() {
        String folderName = pathname_textfield.getText();
        hidePathDialog();
        if (folderName.isEmpty()) {
            return;
        }
        try {
            fileManager.createDirectory(currentFolder + "/" + folderName);
            refresh();
        } catch (FileOperationException e) {
            LOGGER.warn("Error while creating a new folder", e);
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
            // uploadFile(selectedFile);
        }
    }

    public synchronized void navigateBackward() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (historyObject.hasPrevious()) {
                    String previousLocation = historyObject.previous();
                    if (listFolder(previousLocation)) {
                        currentFolder = previousLocation;
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public synchronized void navigateForward() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (historyObject.hasNext()) {
                    String previousLocation = historyObject.next();
                    if (listFolder(previousLocation)) {
                        currentFolder = previousLocation;
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public synchronized void browseFolder(String folderPath) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (listFolder(folderPath)) {
                    currentFolder = folderPath;
                    historyObject.addObject(currentFolder);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private boolean listFolder(String path) {
        try {
            Platform.runLater(() -> {
                Main._scene.setCursor(Cursor.WAIT);
            });
            List<FileObject> folderEntries = fileManager.list(path);
            Platform.runLater(() -> {
                currentFolderEntries.clear();
                currentFolderEntries.addAll(folderEntries);
            });
            return true;
        } catch (FileOperationException e) {
            LOGGER.warn("Error while executing list", e);
            return false;
        } finally {
            Platform.runLater(() -> {
                Main._scene.setCursor(Cursor.DEFAULT);
            });
        }
    }

    public void onPincodeSubmit() {
        pincodeEnteredEvent.happened();
    }

    @Override
    public FileObjectSecurityEventResult handle(FileObjectSecurityEvent event) {
        Platform.runLater(() -> pincode_dialog.setVisible(true));
        pincodeEnteredEvent.waitEvent();

        ResultType result = ResultType.CANCEL;
        String codeValue = "";
        if (!pin.isEmpty()) {
            result = ResultType.CONTINUE;
            codeValue = pin;
        }
        pin = "";
        Platform.runLater(() -> {
            pincode_dialog.setVisible(false);
            pincode_textfield.setText("");
        });
        return new FileObjectSecurityEventResult(result, codeValue);
    }

    @Override
    public void upload(File file) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    FileOperation uploadOperation = fileManager.copy(file.getAbsolutePath(), currentFolder);
                    progress.setVisible(true);
                    while (uploadOperation.getState() != OperationState.FINISHED) {
                        updateProgress(uploadOperation.getProgress(), 100);
                        Thread.sleep(200);
                    }
                    progress.setVisible(false);
                    // FIXME dirty hack - should add loaded objects to
                    // fileOperation
                    Platform.runLater(() -> refresh());
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while uploading file " + file.getAbsolutePath(), e);
                }
                return null;
            }
        };
        progress.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    @Override
    public synchronized void loadContent(String path) {
        browseFolder(path);
    }

    private synchronized void switchPerspectiveTo(PerspectiveType perspective) {
        perspectiveManager.switchTo(perspective);
        currentPerspective = perspective;
    }
}
