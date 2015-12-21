package ru.flashsafe.view;

import java.util.ResourceBundle;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.util.FontUtil;
import ru.flashsafe.util.FontUtil.FontType;

/**
 * Main Pane of Flashsafe client
 * @author Alexander Krysin
 */
public class MainPane extends AnchorPane {
	public AnchorPane window = this;
	public ScrollPane scroll_pane = new ScrollPane();
	public AnchorPane files_area = new AnchorPane();
	public TableView<FileObject> files = new TableView<FileObject>();
	public GridPane gfiles = new GridPane();
	public ListView<FileObject> lfiles = new ListView<FileObject>();
	public Pane topPane = new Pane();
	public Label flashsafe = new Label();
	public Button display_choice = new Button();
	public HBox display_menu = new HBox();
    public Slider display_slider = new Slider();
    public ListView<Label> display_list = new ListView<Label>();
    public Label settings = new Label(), refresh = new Label(), exit = new Label();
    public ProgressBar progress = new ProgressBar();
    public Label myfiles = new Label(), docs = new Label(), pictures = new Label(), sounds = new Label(),
    		videos = new Label(), loads = new Label(), contacts = new Label();
    public TextField search_field = new TextField();

    public CreatePathPane pathname_dialog;
    public EnterPincodePane pincode_dialog;

    private ResourceBundle resourceBundle;

    private MainSceneController controller;

    public MainPane(ResourceBundle resourceBundle, String currentFolder, Stage stage, CreatePathPane pathnameDialog, EnterPincodePane pincodeDialog) {
    	this.resourceBundle = resourceBundle;
    	pathname_dialog = pathnameDialog;
    	pincode_dialog = pincodeDialog;
    	controller = new MainSceneController(this, resourceBundle, currentFolder, stage);
    	pathname_dialog.setController(controller);
    	pincode_dialog.setController(controller);
    	getStylesheets().add(getClass().getResource("/css/mainscene.css").toExternalForm());
        getStylesheets().add("http://flash.so/flashsafe/mainscene.css");
        setPrefWidth(975.0);
    	setPrefHeight(650.0);
    	getChildren().add(getMainPane());
    	controller.init();
    }

    private AnchorPane getMainPane() {
        AnchorPane mainPane = new AnchorPane();
        mainPane.setId("MainPane");
    	mainPane.setPrefWidth(975.0);
    	mainPane.setPrefHeight(650.0);
    	mainPane.getChildren().add(getContentPane());
    	mainPane.getChildren().add(getTopPane());
    	mainPane.getChildren().add(getLogo());
    	mainPane.getChildren().add(getDisplayChoice());
    	mainPane.getChildren().add(getDisplayMenu());
    	mainPane.getChildren().add(getSettingsLabel());
    	mainPane.getChildren().add(getRefreshLabel());
    	mainPane.getChildren().add(getExitLabel());
    	mainPane.getChildren().add(getProgressBar());
    	mainPane.getChildren().add(getCategoriesIconsPane());
    	mainPane.getChildren().add(getCategoriesNamesPane());
    	mainPane.getChildren().add(getSearchPane());
        AnchorPane.setBottomAnchor(mainPane, 10.0);
    	AnchorPane.setLeftAnchor(mainPane, 10.0);
    	AnchorPane.setRightAnchor(mainPane, 10.0);
    	AnchorPane.setTopAnchor(mainPane, 10.0);
        return mainPane;
    }
    
    private ScrollPane getContentPane() {
    	scroll_pane.setFitToHeight(true);
    	scroll_pane.setFitToWidth(true);
    	scroll_pane.setLayoutX(265.0);
    	scroll_pane.setLayoutY(55.0);
    	scroll_pane.setPrefHeight(595.0);
    	scroll_pane.setPrefWidth(710.0);
    	AnchorPane.setBottomAnchor(scroll_pane, 0.0);
    	AnchorPane.setLeftAnchor(scroll_pane, 265.0);
    	AnchorPane.setRightAnchor(scroll_pane, 0.0);
    	AnchorPane.setTopAnchor(scroll_pane, 55.0);
    	scroll_pane.setContent(getFilesPane());
    	return scroll_pane;
    }

    private AnchorPane getFilesPane() {
    	files_area.setPrefHeight(570.0);
    	files_area.setPrefWidth(748.0);
    	files_area.setStyle("-fx-background-color: #FFFFFF;");
    	files_area.getChildren().add(getFilesTableView());
    	files_area.getChildren().add(getFilesGridPane());
    	files_area.getChildren().add(getFilesListView());
    	return files_area;
    }

    private TableView<FileObject> getFilesTableView() {
    	files.setPrefHeight(657.0);
    	files.setPrefWidth(710.0);
    	files.setStyle("-fx-background-insets: 0; -fx-padding: 0;");
    	files.getStyleClass().add("files");
    	AnchorPane.setBottomAnchor(files, 5.0);
    	AnchorPane.setLeftAnchor(files, 0.0);
    	AnchorPane.setRightAnchor(files, 5.0);
    	AnchorPane.setTopAnchor(files, 0.0);
    	TableColumn<FileObject, Label> name = new TableColumn<FileObject, Label>();
    	name.setPrefWidth(319.0);
    	name.setText(resourceBundle.getString("name"));
    	files.getColumns().add(name);
    	TableColumn<FileObject, String> creation_date = new TableColumn<FileObject, String>();
    	creation_date.setMinWidth(0.0);
    	creation_date.setPrefWidth(141.0);
    	creation_date.setText(resourceBundle.getString("creation_date"));
    	files.getColumns().add(creation_date);
    	TableColumn<FileObject, String> type = new TableColumn<FileObject, String>();
    	type.setPrefWidth(90.0);
    	type.setText(resourceBundle.getString("type"));
    	files.getColumns().add(type);
    	TableColumn<FileObject, String> size = new TableColumn<FileObject, String>();
    	size.setMinWidth(0.0);
    	size.setPrefWidth(110.0);
    	size.setText(resourceBundle.getString("size"));
    	files.getColumns().add(size);
    	return files;
    }

    private GridPane getFilesGridPane() {
    	gfiles.setPrefHeight(589.0);
    	gfiles.setPrefWidth(702.0);
    	gfiles.setVisible(false);
    	AnchorPane.setBottomAnchor(gfiles, 5.0);
    	AnchorPane.setLeftAnchor(gfiles, 0.0);
    	AnchorPane.setRightAnchor(gfiles, 5.0);
    	AnchorPane.setTopAnchor(gfiles, 0.0);
    	return gfiles;
    }

    private ListView<FileObject> getFilesListView() {
    	lfiles.setPrefHeight(588.0);
    	lfiles.setPrefWidth(704.0);
    	lfiles.setVisible(false);
    	AnchorPane.setBottomAnchor(lfiles, 5.0);
    	AnchorPane.setLeftAnchor(lfiles, 0.0);
    	AnchorPane.setRightAnchor(lfiles, 5.0);
    	AnchorPane.setTopAnchor(lfiles, 0.0);
    	return lfiles;
    }

    private Pane getTopPane() {
    	topPane.setId("TopToolbar");
    	topPane.setMaxHeight(55.0);
    	topPane.setMinHeight(55.0);
    	topPane.setMinWidth(975.0);
    	AnchorPane.setLeftAnchor(topPane, 0.0);
    	AnchorPane.setRightAnchor(topPane, 0.0);
    	AnchorPane.setTopAnchor(topPane, 0.0);
    	return topPane;
    }

    private Label getLogo() {
    	flashsafe.setId("Flashsafe");
    	flashsafe.setPrefHeight(55.0);
    	flashsafe.setPrefWidth(55.0);
    	AnchorPane.setLeftAnchor(flashsafe, 0.0);
    	AnchorPane.setTopAnchor(flashsafe, 0.0);
    	return flashsafe;
    }

    private Button getDisplayChoice() {
    	display_choice.setId("DisplayChoice");
    	display_choice.setAlignment(Pos.CENTER);
    	display_choice.setContentDisplay(ContentDisplay.CENTER);
    	display_choice.setMnemonicParsing(false);
    	AnchorPane.setRightAnchor(display_choice, 90.0);
    	AnchorPane.setTopAnchor(display_choice, 15.0);
    	return display_choice;
    }

    private HBox getDisplayMenu() {
    	display_menu.setId("DisplayMenu");
    	display_menu.setAlignment(Pos.CENTER);
    	display_menu.setMaxHeight(60.0);
    	display_menu.setMaxWidth(195.0);
    	display_menu.setMinHeight(60.0);
    	display_menu.setMinWidth(195.0);
    	display_menu.setVisible(false);
    	AnchorPane.setRightAnchor(display_menu, 90.0);
    	AnchorPane.setTopAnchor(display_menu, 15.0);
    	display_slider.setId("DisplaySlider");
    	display_slider.setMaxHeight(50.0);
    	display_slider.setOrientation(Orientation.VERTICAL);
    	AnchorPane.setTopAnchor(display_slider, 5.0);
    	display_menu.getChildren().add(display_slider);
    	display_list.setId("DisplayList");
    	display_list.setPrefHeight(50.0);
    	display_list.setPrefWidth(200.0);
    	display_menu.getChildren().add(display_list);
    	return display_menu;
    }

    private Label getSettingsLabel() {
    	settings.setId("Settings");
    	settings.setAlignment(Pos.CENTER);
    	settings.setContentDisplay(ContentDisplay.CENTER);
    	settings.setPrefHeight(55.0);
    	settings.setPrefWidth(30.0);
    	settings.setTextAlignment(TextAlignment.CENTER);
    	AnchorPane.setRightAnchor(settings, 60.0);
    	AnchorPane.setTopAnchor(settings, 0.0);
    	return settings;
    }

    private Label getRefreshLabel() {
    	refresh.setId("Refresh");
    	refresh.setAlignment(Pos.CENTER);
    	refresh.setContentDisplay(ContentDisplay.CENTER);
    	refresh.setPrefHeight(55.0);
    	refresh.setPrefWidth(30.0);
    	refresh.setTextAlignment(TextAlignment.CENTER);
    	AnchorPane.setRightAnchor(refresh, 30.0);
    	AnchorPane.setTopAnchor(refresh, 0.0);
    	refresh.setOnMouseClicked(event -> controller.refresh());
    	return refresh;
    }

    private Label getExitLabel() {
    	exit.setId("Exit");
    	exit.setAlignment(Pos.CENTER);
    	exit.setContentDisplay(ContentDisplay.CENTER);
    	exit.setPrefHeight(55.0);
    	exit.setPrefWidth(30.0);
    	exit.setTextAlignment(TextAlignment.CENTER);
    	AnchorPane.setRightAnchor(exit, 0.0);
    	AnchorPane.setTopAnchor(exit, 0.0);
    	exit.setOnMouseClicked(event -> controller.exit());
    	return exit;
    }

    private ProgressBar getProgressBar() {
    	progress.setId("Progress");
    	progress.setLayoutX(375.0);
    	progress.setLayoutY(325.0);
    	progress.setPrefHeight(30.0);
    	progress.setPrefWidth(500.0);
    	progress.setProgress(0.0);
    	progress.setVisible(false);
    	return progress;
    }

    private Pane getCategoriesIconsPane() {
    	Pane pane = new Pane();
    	pane.setLayoutY(55.0);
    	pane.setMinHeight(595.0);
    	pane.setMinWidth(55.0);
    	pane.setStyle("-fx-background-color: #4CAED1; -fx-background-insets: 0;");
    	AnchorPane.setBottomAnchor(pane, 0.0);
    	AnchorPane.setLeftAnchor(pane, 0.0);
    	AnchorPane.setTopAnchor(pane, 55.0);
    	String[] pics = {"myfiles", "docs", "photos", "sounds", "videos", "loads", "contacts"};
    	double layout_y = 30.0;
    	for(int i=0;i<7;i++) {
    		ImageView icon = new ImageView();
    		icon.setFitHeight(25.0);
    		icon.setFitWidth(25.0);
    		icon.setLayoutX(15.0);
    		icon.setLayoutY(layout_y);
    		icon.setPickOnBounds(true);
    		icon.setPreserveRatio(true);
    		icon.setImage(new Image(getClass().getResourceAsStream("/img/icons/" + pics[i] + ".png")));
    		pane.getChildren().add(icon);
    		layout_y += 70.0;
    	}
    	return pane;
    }
    private Pane getCategoriesNamesPane() {
    	Pane pane = new Pane();
    	pane.setLayoutX(55.0);
    	pane.setLayoutY(55.0);
    	pane.setPrefHeight(595.0);
    	pane.setPrefWidth(210.0);
    	pane.setStyle("-fx-background-color: #ECEFF4;");
    	AnchorPane.setBottomAnchor(pane, 40.0);
    	AnchorPane.setLeftAnchor(pane, 55.0);
    	AnchorPane.setTopAnchor(pane, 55.0);
    	Label[] labels = {myfiles, docs, pictures, sounds, videos, loads, contacts};
    	String[] texts = {"my_files", "documents", "images", "music", "video", "downloads", "contacts"};
    	double[] layout_y = {0.0, 75.0, 150.0, 220.0, 290.0, 360.0, 430};
    	Font myriadPro = FontUtil.instance().font(FontType.LEFT_MENU);
    	for(int i=0;i<7;i++) {
    		labels[i].setLayoutY(layout_y[i]);
    		labels[i].setPrefHeight(95.0);
    		labels[i].setPrefWidth(210.0);
    		labels[i].getStyleClass().add("category");
    		labels[i].setText(resourceBundle.getString(texts[i]));
    		labels[i].setFont(myriadPro);
    		pane.getChildren().add(labels[i]);
    	}
    	return pane;
    }

    private AnchorPane getSearchPane() {
    	AnchorPane pane = new AnchorPane();
    	pane.setId("SearchPane");
    	pane.setPrefHeight(40.0);
    	pane.setPrefWidth(210.0);
    	AnchorPane.setBottomAnchor(pane, 0.0);
    	AnchorPane.setLeftAnchor(pane, 55.0);
    	ImageView icon = new ImageView();
    	icon.setFitHeight(20.0);
    	icon.setFitWidth(20.0);
    	icon.setLayoutX(180.0);
    	icon.setLayoutY(10.0);
    	icon.setPickOnBounds(true);
    	icon.setPreserveRatio(true);
    	pane.getChildren().add(icon);
    	search_field.setId("SearchField");
    	search_field.setLayoutX(14.0);
    	search_field.setLayoutY(8.0);
    	search_field.setPromptText(resourceBundle.getString("search"));
    	pane.getChildren().add(search_field);
    	return pane;
    }
}
