/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.FileController;
import ru.flashsafe.Main;
import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.FlashSafeSystem;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.event.FileObjectSecurityEvent;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult.ResultType;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.perspective.ListPerspective;
import ru.flashsafe.perspective.Perspective;
import ru.flashsafe.perspective.PerspectiveManager;
import ru.flashsafe.perspective.PerspectiveType;
import ru.flashsafe.perspective.TablePerspective;
import ru.flashsafe.util.FileObjectViewHelper;
import ru.flashsafe.util.FolderDownloadOperation;
import ru.flashsafe.util.HistoryObject;
import ru.flashsafe.util.WaitForEvent;
import ru.flashsafe.view.CreatePathPane;

/**
 * FXML Controller class
 *
 * @author Alexander Krysin
 */
public class MainSceneController implements FileController, FileObjectSecurityHandler {
    public Logger LOGGER = LoggerFactory.getLogger(MainSceneController.class);

    private double windowXPosition;
    private double windowYPosition;

    private String pin = "";

    public ResourceBundle resourceBundle;
    @SuppressWarnings("unused")
	private PerspectiveType currentPerspective = PerspectiveType.TABLE;
    private WaitForEvent pincodeEnteredEvent = new WaitForEvent();
    private PerspectiveManager perspectiveManager;
    private FlashSafeSystem flashSafeSystem;
    public FileManager fileManager;
    private final ObservableList<FileObject> currentFolderEntries = FXCollections.observableArrayList();
    private HistoryObject<String> historyObject = new HistoryObject<>();
    private String currentFolder = FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX;
    
    private Stage createPathStage, enterPincodeStage;
    private Stage stage;
    
    @FXML
    public Pane topPane;
    @FXML
    public AnchorPane window;
    
    @FXML
    public Pane pincode_dialog;
    @FXML
    public TextField pincode_textfield;
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
    public TableView<FileObject> files;
    
    @FXML
    public Pane pathname_dialog;
    @FXML
    public TextField pathname_textfield;
    @FXML Button pathname_submit;
    
    @SuppressWarnings("unused")
	private Label flashsafe, myfiles, docs, pictures, sounds, videos, loads, contacts;
    
    @FXML
    public Pane settings_pane;
    @FXML
    public Pane software_pane;
    
    @FXML
    public AnchorPane files_area;
    @FXML
    public GridPane gfiles;
    @FXML
    public ListView<FileObject> lfiles;
    
    @FXML
    public ScrollPane scroll_pane;
    
    @FXML
    public TextField search_field;
    @FXML
    public Label breadcrumbs;
    
    @FXML
    private AnchorPane folder_one;
    @FXML
    private Pane folder_one_left;
    @FXML
    private ImageView folder_one_icon;
    @FXML
    private Label folder_one_name;
    @FXML
    private AnchorPane folder_two;
    @FXML
    private Pane folder_two_left;
    @FXML
    private ImageView folder_two_icon;
    @FXML
    private Label folder_two_name;
    @FXML
    private AnchorPane folder_three;
    @FXML
    private Pane folder_three_left;
    @FXML
    private ImageView folder_three_icon;
    @FXML
    private Label folder_three_name;
    @FXML
    private AnchorPane folder_four;
    @FXML
    private Pane folder_four_left;
    @FXML
    private ImageView folder_four_icon;
    @FXML
    private Label folder_four_name;
    @FXML
    private AnchorPane folder_five;
    @FXML
    private Pane folder_five_left;
    @FXML
    private ImageView folder_five_icon;
    @FXML
    private Label folder_five_name;
    
    private AnchorPane[] folders;
	private Pane[] lefts;
	private ImageView[] icons;
	private Label[] names;
	
	private volatile int folders_count;
	
	@FXML
	private AnchorPane add_folder;
    
    @FXML
    private Pane bosspane;
    
    @FXML
    public ProgressBar buffered_progress;
    public MediaPlayer player;
    @FXML
    public Button play;
    @FXML
    public Button pause;
    @FXML
    public Slider pprogress;
    @FXML
    public Label track;
    
    
    private Stage copyFileStage;    
    private CopyFileController controller;
    
    private AnchorPane download_pane;
    
    @FXML
    public TableColumn<FileObject, Label> name_column;
    @FXML
    public TableColumn<FileObject, String> creation_date_column;
    @FXML
    public TableColumn<FileObject, String> type_column;
    @FXML
    public TableColumn<FileObject, String> size_column;
    
    @FXML
    public Pane upload_hint;
    
    @FXML
    private Circle green_circle, yellow_circle, red_circle;
    @FXML
    private ImageView maximize_icon, minimize_icon, exit_icon;
    
    @FXML
    private ImageView back, forward;
    
    @FXML
    public RadioMenuItem sorted_name, sorted_creation_date, sorted_type, sorted_size, sorted_asc, sorted_desc;
    
    @FXML
    private Pane trash_pane, trash_left;

    public MainSceneController(ResourceBundle resourceBundle, String currentFolder, Stage stage) {
    	this.currentFolder = currentFolder;
    	this.stage = stage;        
        this.resourceBundle = resourceBundle;
    }

    public Window getWindow() {
        return this.stage;
    }
    
    private class AddHandlersTask implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    files.setPlaceholder(new Label(""));
                    files.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

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
                    
                    add_folder.setOnMouseClicked((event) -> onCreatePathClick());

                    attachWindowDragControlToElement(topPane);
                    
                    Button[] nums = {one, two, three, four, five, six, seven, eight, nine, zero};
                    for(Button num : nums) {
                    	num.setOnMouseClicked(getOnNumClick(num));
                    }

                    gfiles.getColumnConstraints().clear();
                    gfiles.getRowConstraints().clear();

                    search_field.textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if(newValue.isEmpty()) {
                                files.setItems(currentFolderEntries);
                            } else {
                                files.setItems(currentFolderEntries.filtered(new Predicate<FileObject>() {
                                    @Override
                                    public boolean test(FileObject t) {
                                        return t.getName().contains(newValue);
                                    }
                                }));
                            }
                        }
                    });
                }
            });
            return null;
        }
    }

    public void setMediaPlayer(MediaPlayer player) {
        this.player = player;
    }
    
    private void attachWindowDragControlToElement(Node element) {
        element.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - windowXPosition);
            stage.setY(event.getScreenY() - windowYPosition);
        });
        element.setOnMousePressed(event -> {
            windowXPosition = event.getSceneX();
            windowYPosition = event.getSceneY();
        });
    }

    public void onDragOver(DragEvent event) {
    	if (!getPerspectives().contains(event.getGestureSource()) && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            upload_hint.setVisible(true);;
        }
        event.consume();
    }
    
    public void onDragExited(DragEvent event) {
    	upload_hint.setVisible(false);
    	event.consume();
    }
    
    public void onDragDropped(DragEvent event) {
    	Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            for(File f : db.getFiles()) {
            	Task<Void> task = new Task<Void>() {
            		@Override
            		public Void call() {
            			upload(f, getCurrentLocation());
            			return null;
            		}
            	};
            	new Thread(task).start();
            }
        }
        event.setDropCompleted(true);
        event.consume();
    }
    
    
    /**
     * Initializes the controller class.
     */
    public void initialize() {
        flashSafeSystem = FlashSafeApplication.flashSafeSystem();
        fileManager = flashSafeSystem.fileManager();
        flashSafeSystem.fileManagementService().registerFileObjectSecurityHandler(this);

        perspectiveManager = new PerspectiveManager(getPerspectives());
        perspectiveManager.switchTo(PerspectiveType.TABLE);
        
        folders = new AnchorPane[] {folder_one, folder_two, folder_three, folder_four, folder_five};
    	lefts = new Pane[] {folder_one_left, folder_two_left, folder_three_left, folder_four_left, folder_five_left};
    	icons = new ImageView[] {folder_one_icon, folder_two_icon, folder_three_icon, folder_four_icon, folder_five_icon};
    	names = new Label[] {folder_one_name, folder_two_name, folder_three_name, folder_four_name, folder_five_name};
        
        bosspane.setOnDragOver(event -> {
            if (event.getGestureSource() != bosspane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        bosspane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                final File f = db.getFiles().get(0);
                upload(f, FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX);
            }
            event.setDropCompleted(true);
            event.consume();
        });

        Main.es.submit(new AddHandlersTask());
        
        browseFolder(currentFolder);
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

    private void deselectSortedOptions() {
    	RadioMenuItem[] sorted_items = {sorted_name, sorted_creation_date, sorted_type, sorted_size};
    	for(RadioMenuItem item : sorted_items) item.setSelected(false);
    	files.getSortOrder().clear();
    }
    
    public void sortedByName() {
    	deselectSortedOptions();
    	sorted_name.setSelected(true);
        files.getSortOrder().add(files.getColumns().get(0));
    }
    
    public void sortedByCreationDate() {
    	deselectSortedOptions();
    	sorted_creation_date.setSelected(true);
    	files.getSortOrder().add(files.getColumns().get(1));
    }

	public void sortedByType() {
		deselectSortedOptions();
		sorted_type.setSelected(true);
		files.getSortOrder().add(files.getColumns().get(2));
	}
	
	public void sortedBySize() {
		deselectSortedOptions();
		sorted_size.setSelected(true);
		files.getSortOrder().add(files.getColumns().get(3));
	}
	
	private void deselectSortedTypeOptions() {
    	RadioMenuItem[] sorted_items = {sorted_asc, sorted_desc};
    	for(RadioMenuItem item : sorted_items) item.setSelected(false);
    }
	
	public void ascSorted() {
		deselectSortedTypeOptions();
		sorted_asc.setSelected(true);
		for(TableColumn column : files.getColumns()) column.setSortType(TableColumn.SortType.ASCENDING);
	}
	
	public void descSorted() {
		deselectSortedTypeOptions();
		sorted_desc.setSelected(true);
		for(TableColumn column : files.getColumns()) column.setSortType(TableColumn.SortType.DESCENDING);
	}
    
    public void refresh() {
        listFolder(currentFolder);
    }
    
    public void showMaximizeIcon() {
    	green_circle.setOpacity(1.0);
    	maximize_icon.setVisible(true);
    }
    
    public void hideMaximizeIcon() {
    	maximize_icon.setVisible(false);
    	green_circle.setOpacity(0.75);
    }
    
    public void showMinimizeIcon() {
    	yellow_circle.setOpacity(1.0);
    	minimize_icon.setVisible(true);
    }
    
    public void hideMinimizeIcon() {
    	minimize_icon.setVisible(false);
    	yellow_circle.setOpacity(0.75);
    }
    
    public void showExitIcon() {
    	red_circle.setOpacity(1.0);
    	exit_icon.setVisible(true);
    }
    
    public void hideExitIcon() {
    	exit_icon.setVisible(false);
    	red_circle.setOpacity(0.75);
    }

    public void maximize() {
    	stage.setFullScreen(!stage.isFullScreen());
    }
    
    public void minimize() {
        stage.setIconified(true);
    }
    
    public void exit() {
        stage.close();
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

    public void showPathDialog() {
        if(createPathStage == null) {
            createPathStage = new Stage(StageStyle.TRANSPARENT);
            createPathStage.setWidth(325.0);
            createPathStage.setHeight(200.0);
            createPathStage.setResizable(false);
            createPathStage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
            Scene scene;
            pathname_dialog = new CreatePathPane(resourceBundle);
            ((CreatePathPane) pathname_dialog).setController(this);
            scene = new Scene(pathname_dialog, Color.TRANSPARENT);
            createPathStage.setScene(scene);
            createPathStage.setAlwaysOnTop(true);
            createPathStage.initModality(Modality.WINDOW_MODAL);
            createPathStage.initOwner(stage);
            createPathStage.setTitle("Create path");
            createPathStage.getIcons().add(stage.getIcons().get(0));
    	}
    	createPathStage.show();
        ((CreatePathPane) pathname_dialog).pathname_textfield.requestFocus();
    }

    public void hidePathDialog() {
        if(createPathStage != null) {
    		createPathStage.close();
    	}
        pathname_textfield.setText("");
        pathname_submit.setStyle("-fx-effect: null;");
    }

    public void onCreatePathClick() {
        showPathDialog();
    }

    public void onPathnameSubmit() {
        String folderName = ((CreatePathPane) pathname_dialog).pathname_textfield.getText();
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
        ((CreatePathPane) pathname_dialog).pathname_textfield.setText("");
    }

    public void createDirectory(String folderName) {
        if (folderName.isEmpty()) {
            return;
        }
        try {
            fileManager.createDirectory(currentFolder.replace("fls://", "").equals("") ? currentFolder + folderName : currentFolder + "/" + folderName);
            refresh();
        } catch (FileOperationException e) {
            LOGGER.warn("Error while creating a new folder", e);
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
                        Platform.runLater(() -> breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/")));
                        back.setImage(new Image(getClass().getResourceAsStream("/img/backward_disabled.png")));
                        forward.setImage(new Image(getClass().getResourceAsStream("/img/forward_enabled.png")));
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
                        Platform.runLater(() -> breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/")));
                        forward.setImage(new Image(getClass().getResourceAsStream("/img/forward_disabled.png")));
                        back.setImage(new Image(getClass().getResourceAsStream("/img/backward_enabled.png")));
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public synchronized void browseFolder(String folderPath) {
    	folders_count = 0;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (listFolder(folderPath)) {
                    currentFolder = folderPath;
                    historyObject.addObject(currentFolder);
                    if(!folderPath.equals(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX)) back.setImage(new Image(getClass().getResourceAsStream("/img/backward_enabled.png")));
                    Platform.runLater(() -> breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/")));
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    public synchronized void browseTrash() {
    	folders_count = 0;
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (trashList()) {
                    currentFolder = "fls://Trash";
                    historyObject.addObject(currentFolder);
                    back.setImage(new Image(getClass().getResourceAsStream("/img/backward_enabled.png")));
                    Platform.runLater(() -> breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/")));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void disableSidebarFolders() {
    	for(AnchorPane folder : folders) {
    		folder.setStyle("-fx-background-color: #252B2D;");
    		folder.setOnMouseClicked(null);
    	}
    	for(Pane left : lefts) left.setVisible(false);
    	for(ImageView icon : icons) icon.setVisible(false);
    	for(Label name : names) name.setVisible(false);
    }
    
    private synchronized int getFoldersCount() {
    	return folders_count;
    }
    
    private synchronized void incrementFoldersCount() {
    	folders_count++;
    }
    
    private boolean listFolder(String path) {
        try {
            Platform.runLater(() -> {
                Main._scene.setCursor(Cursor.WAIT);
                disableSidebarFolders();
            });
            List<FileObject> folderEntries = fileManager.list(path);
            if(!path.equals(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX)) {
	            for(FileObject fo : currentFolderEntries) {
	                if(fo.getType() == FileObjectType.DIRECTORY) {
	                	int fc = getFoldersCount();
	                	if(fc < 5) {
		                	Platform.runLater(() -> {
		                		if(fo.getName().equals(path.split("/")[path.split("/").length - 1])) {
		                			lefts[fc].setVisible(true);
		                			folders[fc].setStyle("-fx-background-color: #2D3335;");
		                		}
			                	icons[fc].setVisible(true);
			                	names[fc].setText(fo.getName());
			                	names[fc].setVisible(true);
			                    folders[fc].setOnMouseClicked((event) -> {
			                    	try {
			                    		for(AnchorPane folder : folders) folder.setStyle("-fx-background-color: #252B2D;");
			                    		for(Pane left : lefts) left.setVisible(false);
			                    		folders[fc].setStyle("-fx-background-color: #2D3335;");
			                    		lefts[fc].setVisible(true);
			                    		List<FileObject> entries = fileManager.list(fo.getAbsolutePath());
			                    		currentFolderEntries.clear();
			                            currentFolderEntries.addAll(entries);
			                    	} catch (FileOperationException e) {
			                    		LOGGER.warn("Error while executing list", e);
			                    	}
			                    });
		                    });
		                	incrementFoldersCount();
	                	}
	                }
	            }
            } else {
            	Platform.runLater(() -> {
            		folders[0].setStyle("-fx-background-color: #2D3335;");
            		lefts[0].setVisible(true);
                	icons[0].setVisible(true);
                	names[0].setText("/");
                	names[0].setVisible(true);
                    folders[0].setOnMouseClicked((event) -> browseFolder(FileManager.FLASH_SAFE_STORAGE_PATH_PREFIX));
                });
            }
            Platform.runLater(() -> {
                currentFolderEntries.clear();
                currentFolderEntries.addAll(folderEntries);
                files.getColumns().get(0).setSortType(TableColumn.SortType.ASCENDING);
                files.getSortOrder().add(files.getColumns().get(0));
                trash_pane.setStyle("-fx-background-color: #252B2D");
                trash_left.setVisible(false);
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
    
    private boolean trashList() {
        try {
            Platform.runLater(() -> {
                Main._scene.setCursor(Cursor.WAIT);
                disableSidebarFolders();
            });
            List<FileObject> folderEntries = fileManager.trashList();
            for(FileObject fo : currentFolderEntries) {
                if(fo.getType() == FileObjectType.DIRECTORY) {
                	int fc = getFoldersCount();
                	if(fc < 5) {
	                	Platform.runLater(() -> {
		                	icons[fc].setVisible(true);
		                	names[fc].setText(fo.getName());
		                	names[fc].setVisible(true);
		                    folders[fc].setOnMouseClicked((event) -> {
		                    	try {
		                    		for(AnchorPane folder : folders) folder.setStyle("-fx-background-color: #252B2D;");
		                    		for(Pane left : lefts) left.setVisible(false);
		                    		folders[fc].setStyle("-fx-background-color: #2D3335;");
		                    		lefts[fc].setVisible(true);
		                    		List<FileObject> entries = fileManager.list(fo.getAbsolutePath());
		                    		currentFolderEntries.clear();
		                            currentFolderEntries.addAll(entries);
		                    	} catch (FileOperationException e) {
		                    		LOGGER.warn("Error while executing list", e);
		                    	}
		                    });
	                    });
	                	incrementFoldersCount();
                	}
                }
            }
            Platform.runLater(() -> {
                currentFolderEntries.clear();
                currentFolderEntries.addAll(folderEntries);
                files.getColumns().get(0).setSortType(TableColumn.SortType.ASCENDING);
                files.getSortOrder().add(files.getColumns().get(0));
                trash_pane.setStyle("-fx-background-color: #2D3335");
                trash_left.setVisible(true);
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
        Platform.runLater(() -> {
        	if(enterPincodeStage == null) {
                    enterPincodeStage = new Stage(StageStyle.TRANSPARENT);
                    enterPincodeStage.setWidth(300.0);
                    enterPincodeStage.setHeight(340.0);
                    enterPincodeStage.setResizable(false);
                    enterPincodeStage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
                    Scene scene;
                    scene = new Scene(pincode_dialog, Color.TRANSPARENT);
                    enterPincodeStage.setScene(scene);
                    enterPincodeStage.initModality(Modality.WINDOW_MODAL);
                    enterPincodeStage.initOwner(stage);
                    enterPincodeStage.setTitle("Enter PIN-code");
                    enterPincodeStage.getIcons().add(stage.getIcons().get(0));
        	}
        	enterPincodeStage.show();
        });
        pincodeEnteredEvent.waitEvent();

        ResultType result = ResultType.CANCEL;
        String codeValue = "";
        if (!pin.isEmpty()) {
            result = ResultType.CONTINUE;
            codeValue = pin;
        }
        pin = "";
        Platform.runLater(() -> {
            if(enterPincodeStage != null) {
        		enterPincodeStage.close();
        	}
            pincode_textfield.setText("");
        });
        return new FileObjectSecurityEventResult(result, codeValue);
    }

    private void prepareCopyFileStage() {
    	if(copyFileStage == null) {
	        copyFileStage = new Stage();
	        copyFileStage.initStyle(StageStyle.TRANSPARENT);
	        copyFileStage.setWidth(510);
	        copyFileStage.setHeight(430);
	        copyFileStage.setResizable(false);
	        copyFileStage.setTitle("Copy file");
	        copyFileStage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
	        
	        FXMLLoader loader = new FXMLLoader();
	    	loader.setLocation(getClass().getResource("/copy.fxml"));
	    	loader.setResources(resourceBundle);
	    	controller = new CopyFileController(copyFileStage);
	    	loader.setController(controller);
	    	ScrollPane copyFilePane;
	    	try {
	    		copyFilePane = (ScrollPane) loader.load();
	    		Scene scene;
		        scene = new Scene(copyFilePane, Color.TRANSPARENT);
		        scene.getStylesheets().add(getClass().getResource("/css/loadpane.css").toExternalForm());
		        scene.getStylesheets().add(getClass().getResource("/css/scrolls.css").toExternalForm());
		        copyFileStage.setScene(scene);
	    	} catch(IOException e) {
	    		LOGGER.error("Error on create CopyFilePane: " + e.getMessage(), e);
	    	}
    	}
    	if(!copyFileStage.isShowing()) {
	    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	    	int width = gd.getDisplayMode().getWidth();
	    	int height = gd.getDisplayMode().getHeight();
	    	copyFileStage.setX(width - 510);
	    	copyFileStage.setY(height - 470);
	    	copyFileStage.show();
    	}
    }
    
    AnchorPane ap = null;
    
    public void uploadDir(File dir, FolderDownloadOperation fuo) {
    	prepareCopyFileStage();
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	FXMLLoader loader = new FXMLLoader();
    	    	loader.setLocation(getClass().getResource("/download.fxml"));
    	    	loader.setResources(resourceBundle);
    	    	DownloadController download_controller = new DownloadController(controller/*, uploadOperation, download_pane*/);
    	    	loader.setController(download_controller);
    	    	AnchorPane dp = null;
    	    	try {
    	    		dp = (AnchorPane) loader.load();
    	    	} catch(IOException e) {
    	    		LOGGER.error("Error on create CopyFilePane: " + e.getMessage(), e);
    	    	}
    	    	final AnchorPane ddpp = dp;
    	    	download_controller.setFileName(dir.getName());
    	    	download_controller.enableUploadIcon();
    	    	Platform.runLater(() -> {
    	    		controller.addDownload(ddpp);
	            	download_controller.getProgress().progressProperty().bind(this.progressProperty());
	            	AnchorPane cfpane = download_pane;
	            	ap = cfpane;
	            	download_controller.getCancelButton().setOnMouseClicked((event) -> {
	            		download_controller.getProgress().progressProperty().unbind();
	                    controller.deleteDownload(ddpp);
	                    cancel();
	                });
    	    	});
                while (fuo.getState() != OperationState.FINISHED) {
                    updateProgress(fuo.getTotalProgress(), 100);
                    Thread.sleep(200);
                }
                updateProgress(100, 100);
                // FIXME dirty hack - should add loaded objects to
                Platform.runLater(() -> {
                    download_controller.disableUploadIcon();
                    refresh();
                });
                return null;
            }
        };
        new Thread(task).start();
    }
    
    private long calcLength(File[] files) {
    	long length = 0;
    	for(File file : files) {
    		if(file.isFile()) {
    			length += file.length();
    		} else {
    			length += calcLength(file.listFiles());
    		}
    	}
    	return length;
    }
    
    @Override
    public void upload(File fileObject, String toPath) {
    	Platform.runLater(() -> prepareCopyFileStage());
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                	long length = fileObject.isFile() ? fileObject.length() : calcLength(fileObject.listFiles());
                	final FileOperation uploadOperation = (fileObject.isDirectory() || length != 0) ? fileManager.copy(fileObject.getAbsolutePath(), toPath) : null;
                	if(fileObject.isFile() && length == 0) fileManager.createFile(toPath + "/" + fileObject.getName());
                	DownloadController download_controller = new DownloadController(controller);
        	    	FXMLLoader loader = new FXMLLoader();
        	    	loader.setLocation(getClass().getResource("/download.fxml"));
        	    	loader.setResources(resourceBundle);
        	    	loader.setController(download_controller);
        	    	AnchorPane dp = null;
        	    	try {
        	    		dp = (AnchorPane) loader.load();
        	    	} catch(IOException e) {
        	    		LOGGER.error("Error on create CopyFilePane: " + e.getMessage(), e);
        	    	}
        	    	final AnchorPane ddpp = dp;
                	Platform.runLater(() -> {
            	    	controller.addDownload(ddpp);
                    	if(fileObject.isDirectory() || length != 0) download_controller.getProgress().progressProperty().bind(this.progressProperty());
                    	if(fileObject.isFile() && length == 0) {
                    		download_controller.getProgress().setProgress(1.0);
                    		download_controller.disableUploadIcon();
                    	}
                    	download_controller.getCancelButton().setOnMouseClicked((event) -> {
                    		if(fileObject.isDirectory() || length != 0) download_controller.getProgress().progressProperty().unbind();
                            if(uploadOperation != null) uploadOperation.stop();
                            controller.deleteDownload(ddpp);
                            cancel();
                        });
                    	download_controller.setFileName(fileObject.getName());
                    	if(fileObject.isDirectory() || length != 0) download_controller.enableUploadIcon();
                    });
                    while (uploadOperation.getState() != OperationState.FINISHED) {
                        updateProgress(uploadOperation.getProgress(), 100);
                        Thread.sleep(200);
                    }
                    // FIXME dirty hack - should add loaded objects to
                    Platform.runLater(() -> {
                        if(fileObject.isDirectory() || length != 0) download_controller.disableUploadIcon();
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while uploading file " + fileObject.getAbsolutePath(), e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public synchronized void loadContent(String path) {
        browseFolder(path);
    }

    @SuppressWarnings("unused")
	private synchronized void switchPerspectiveTo(PerspectiveType perspective) {
        perspectiveManager.switchTo(perspective);
        currentPerspective = perspective;
    }

    @Override
    public void download(String fromPath, File toFile) {
    	Platform.runLater(() -> prepareCopyFileStage());
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                	FileOperation downloadOperation = fileManager.copy(fromPath, toFile.getAbsolutePath());
                	DownloadController download_controller = new DownloadController(controller/*, downloadOperation, download_pane*/);
        	    	FXMLLoader loader = new FXMLLoader();
        	    	loader.setLocation(getClass().getResource("/download.fxml"));
        	    	loader.setResources(resourceBundle);
        	    	loader.setController(download_controller);
        	    	AnchorPane dp = null;
        	    	try {
        	    		dp = (AnchorPane) loader.load();
        	    	} catch(IOException e) {
        	    		LOGGER.error("Error on create CopyFilePane: " + e.getMessage(), e);
        	    	}
        	    	final AnchorPane ddpp = dp;
                	Platform.runLater(() -> {
            	    	controller.addDownload(ddpp);
                    	download_controller.getProgress().progressProperty().bind(this.progressProperty());
                    	download_controller.getCancelButton().setOnMouseClicked((event) -> {
                    		download_controller.getProgress().progressProperty().unbind();
                            downloadOperation.stop();
                            controller.deleteDownload(ddpp);
                            cancel();
                        });
                    	download_controller.setFileName(toFile.getName());
                    	download_controller.enableDownloadIcon();
                    });
                    while (downloadOperation.getState() != OperationState.FINISHED) {
                        updateProgress(downloadOperation.getProgress(), 100);
                        Thread.sleep(200);
                    }
                    // FIXME dirty hack - should add loaded objects to
                    Platform.runLater(() -> {
                        if(download_controller.getProgress().getProgress() < 1.0) {
                    		download_controller.getProgress().progressProperty().unbind();
                    		download_controller.getProgress().setProgress(1.0); // Fix for empty files
                    	}
                    	download_controller.disableDownloadIcon();
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while downloading file " + toFile.getAbsolutePath(), e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public void downloadDir(FileObject path, File targetPath, FolderDownloadOperation fuo) {
    	prepareCopyFileStage();
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	FXMLLoader loader = new FXMLLoader();
    	    	loader.setLocation(getClass().getResource("/download.fxml"));
    	    	loader.setResources(resourceBundle);
    	    	DownloadController download_controller = new DownloadController(controller/*, uploadOperation, download_pane*/);
    	    	loader.setController(download_controller);
    	    	AnchorPane dp = null;
    	    	try {
    	    		dp = (AnchorPane) loader.load();
    	    	} catch(IOException e) {
    	    		LOGGER.error("Error on create CopyFilePane: " + e.getMessage(), e);
    	    	}
    	    	final AnchorPane ddpp = dp;
    	    	download_controller.setFileName(path.getName());
    	    	download_controller.enableDownloadIcon();
    	    	Platform.runLater(() -> {
    	    		controller.addDownload(ddpp);
	            	download_controller.getProgress().progressProperty().bind(this.progressProperty());
	            	AnchorPane cfpane = download_pane;
	            	ap = cfpane;
	            	download_controller.getCancelButton().setOnMouseClicked((event) -> {
	            		download_controller.getProgress().progressProperty().unbind();
	                    controller.deleteDownload(ddpp);
	                    cancel();
	                });
    	    	});
                while (fuo.getState() != OperationState.FINISHED) {
                    updateProgress(fuo.getTotalProgress(), 100);
                    Thread.sleep(200);
                }
                updateProgress(100, 100);
                // FIXME dirty hack - should add loaded objects to
                Platform.runLater(() -> {
                    download_controller.disableDownloadIcon();
                    refresh();
                });
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @Override
    public void move(String fromPath, String toPath) {
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                	/*FileOperation moveOperation = */fileManager.move(fromPath, toPath);
                    /*while (moveOperation.getState() != OperationState.FINISHED) {
                        Thread.sleep(200);
                    }*/
                    // FIXME dirty hack - should add loaded objects to
                    Platform.runLater(() -> {
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while move object " + fromPath, e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @Override
    public void copy(String fromPath, String toPath) {
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                	/*FileOperation copyOperation = */fileManager.copy(fromPath, toPath);
                    /*while (copyOperation.getState() != OperationState.FINISHED) {
                        Thread.sleep(200);
                    }*/
                    // FIXME dirty hack - should add loaded objects to
                    Platform.runLater(() -> {
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while copy object " + fromPath, e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @Override
    public void rename(long fileObjectId, String name) {
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                	/*FileOperation renameOperation = */fileManager.rename(fileObjectId, name);
                    /*while (renameOperation.getState() != OperationState.FINISHED) {
                        Thread.sleep(200);
                    }*/
                    // FIXME dirty hack - should add loaded objects to
                    Platform.runLater(() -> {
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while rename object " + fileObjectId, e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @Override
    public void delete(String path) {
    	Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                	/*FileOperation deleteOperation = */fileManager.delete(path);
                    /*while (deleteOperation.getState() != OperationState.FINISHED) {
                        Thread.sleep(200);
                    }*/
                    // FIXME dirty hack - should add loaded objects to
                    Platform.runLater(() -> {
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while delete object " + path, e);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    
    @Override
    public String getCurrentLocation() {
        return currentFolder;
    }
}
