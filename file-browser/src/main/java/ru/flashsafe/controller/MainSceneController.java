/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.awt.Desktop;
import java.awt.Dimension;
//import ch.randelshofer.quaqua.osx.OSXFile;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
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
import javafx.scene.input.DragEvent;
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

import ru.flashsafe.Main;
import ru.flashsafe.http.HttpAPI;
import ru.flashsafe.http.UploadProgressListener;
import ru.flashsafe.model.FSObject;
//import ru.flashsafe.token.FlashSafeToken;
//import ru.flashsafe.token.event.BaseEventHandler;
//import ru.flashsafe.token.exception.TokenServiceInitializationException;
//import ru.flashsafe.token.generator.FixedValueGenerationStrategy;
//import ru.flashsafe.token.service.impl.RemoteEmulatorTokenService;

/**
 * FXML Controller class
 * @author alex_xpert
 */
public class MainSceneController implements Initializable, UploadProgressListener {
    private static final Logger log = LogManager.getLogger(MainSceneController.class);

    //private final Image cloud_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/cloud_enabled.png"));
    //private final Image upload_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/upload_enabled.png"));
    //private final Image download_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/download_enabled.png"));
    //private final Image create_path_enabled = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/create_folder_enabled.png"));
    //private final Image folderIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder.png"));
    private final Image folderBlackIcon = new Image(getClass().getResourceAsStream("/img/fs/folder_empty.png"), 24, 24, false, false);
    //private final Image lockIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock.png"));
    private final Image lockBlackIcon = new Image(getClass().getResourceAsStream("/img/fs/folder_lock.png"), 24, 24, false, false);
    //private final Image dividerIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/divider.png"));
    //private final Image fileIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/file.png"));
    //private final Image arrowIcon = new Image(getClass().getResourceAsStream("/ru/flashsafe/img/arrow.png"));
    private final Image folderFull = new Image(getClass().getResourceAsStream("/img/fs/folder.png"), 24, 24, false, false);

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
    //public static RemoteEmulatorTokenService rets;
    private boolean run = false;
    private TreeView current_tv = null;
    private final ArrayList<TreeView> TREE_VIEW = new ArrayList<>();
    private final ArrayList<TreeView> TREE_VIEW_FORWARD = new ArrayList<>();
    final int[] x = new int[1];
    final int[] y = new int[1];
    private String pin = "";

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
    private TableView<TableRow> files;
    @FXML
    private Pane pathname_dialog;
    @FXML
    private TextField pathname_textfield;
//    @FXML
//    private Button pathname_submit;
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

    private class AddHandlersTask implements Callable {

        @Override
        public Object call() throws Exception {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
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
                        ResizeListener listener = new ResizeListener();
                    	window.setOnMouseMoved(listener);
                    	window.setOnMousePressed(listener);
                    	window.setOnMouseDragged(listener);
                    	topPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent event) {
                                                                switch(event.getClickCount()) {
                                                                    case 1:
                                                                        display_menu.setVisible(!display_menu.isVisible());
                                                                        break;
                                                                    case 2:
                                                                        Main._stage.setFullScreen(!Main._stage.isFullScreen());
                                                                        break;
                                                                }
							}

                    	});
                    	KeyCombination backCombination = new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_DOWN);
                    	Main._stage.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>(){

							@Override
							public void handle(KeyEvent event) {
								if(backCombination.match(event)) {
									back();
								}
							}

                    	});
                    	refresh.setOnMouseClicked(new EventHandler<MouseEvent>(){

							@Override
							public void handle(MouseEvent event) {
								clearContent();
								loadContent(cur_path.id, "", current_tv);
							}

                    	});
                    	settings.setOnMouseClicked(new EventHandler<MouseEvent>(){

							@Override
							public void handle(MouseEvent event) {
								settings_pane.setVisible(true);
							}

                    	});
                    	settings_close.setOnMouseClicked(new EventHandler<MouseEvent>(){

							@Override
							public void handle(MouseEvent event) {
								settings_pane.setVisible(false);
							}

                    	});
                    	Label[] settings_categories = {rendering, caching, hardware, software};
                    	for(Label l : settings_categories) {
                    		l.setOnMousePressed(getOnSettingsCategoryClickListener(l));
                    	}
                    	rendering.setStyle("-fx-text-fill: #555555;");
                    	rendering.getStyleClass().remove("category");
                		rendering.getStyleClass().add("category2");
                		link.setOnAction(new EventHandler<ActionEvent>() {

							@Override
							public void handle(ActionEvent event) {
								if (Desktop.isDesktopSupported()) {
									try {
								        Desktop.getDesktop().browse(new URI(link.getText()));
								    } catch (IOException | URISyntaxException e) {
								    	log.error(e);
								    }
								}
							}

                		});
                		flashsafe.setOnMouseDragged(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                Main._stage.setX(Main._stage.getX() + event.getX()-x[0]);
                                Main._stage.setY(Main._stage.getY() + event.getY()-y[0]);

                            }

                        });
                		flashsafe.setOnMouseMoved(new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent event) {
								x[0] = (int) (event.getX()-1);
	                            y[0] = (int) (event.getY()-1);
							}

                		});
                		flashsafe.setCursor(Cursor.MOVE);
                                String[] dlitems = {"xlarge", "large", "medium", "small", "tile", "list", "table"};
                                String[] dlinames = {"Огромные значки", "Большие значки", "Обычные значки", "Маленькие значки", "Плитка", "Список", "Таблица"};
                                for(int i=0;i<7;i++) {
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
                                display_choice.setOnAction(new EventHandler<ActionEvent>() {

                                    @Override
                                    public void handle(ActionEvent event) {
                                        display_menu.setVisible(!display_menu.isVisible());
                                    }
                                });
                	}

            });
            return null;
        }

    }


    /**
     * Получаем иконку файла, размером 16x16
     * @param filename
     * @return
     */
    private Image getFileIcon(String filename) {
        Image image = null;
        List<String> archives = Arrays.asList("rar", "zip", "gz", "bz", "7z", "bz2", "tar", "deb", "rpm");
        List<String> documents = Arrays.asList("txt", "rtf", "doc", "xls", "ppt", "docx", "xlsx", "pptx", "odt", "odp", "ods", "odg");
        List<String> pictures = Arrays.asList("jpeg", "jpe", "jpg", "png", "gif", "tiff", "tif", "bmp", "wlmp", "svg", "eps", "ico", "icns");
        List<String> sounds = Arrays.asList("mp1", "mp2", "mp3", "wma", "wav", "amr", "aac", "midi", "ogg");
        List<String> videos = Arrays.asList("3gp", "3gpp", "avi", "flv", "mkv", "mov", "qt", "vob", "wmv");
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        if(archives.contains(ext)) {
        	image = new Image(getClass().getResourceAsStream("/img/fs/archive.png"), 24, 24, false, false);
        } else if(documents.contains(ext)) {
        	image = new Image(getClass().getResourceAsStream("/img/fs/document.png"), 24, 24, false, false);
        } else if(pictures.contains(ext)) {
        	image = new Image(getClass().getResourceAsStream("/img/fs/picture.png"), 24, 24, false, false);
        } else if(sounds.contains(ext)) {
        	image = new Image(getClass().getResourceAsStream("/img/fs/music.png"), 24, 24, false, false);
        } else if(videos.contains(ext)) {
        	image = new Image(getClass().getResourceAsStream("/img/fs/video.png"), 24, 24, false, false);
        } else {
        	image = new Image(getClass().getResourceAsStream("/img/fs/binary.png"), 24, 24, false, false);
        }
        return image;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	Font myriadPro = Font.loadFont(getClass().getResourceAsStream("/font/myriadpro_regular.ttf"), 20);
    	Label[] categories = {myfiles, docs, pictures, sounds, videos, loads, contacts};
    	for(Label l : categories) {
    		l.setFont(myriadPro);
    		l.setOnMousePressed(getOnCategoryClickListener(l));
    	}
    	myfiles.getStyleClass().remove(0);
		myfiles.getStyleClass().add("category1");
        HttpAPI.getInstance().addListener(this);
        Future connect = Main.es.submit(new ConnectToCloudTask());
        while(!connect.isDone()) {}
        try {
            if ((boolean) connect.get()) {
                Main.es.submit(new AddHandlersTask());
                loadContent(cur_path.id, "", current_tv);
            }
        } catch(InterruptedException | ExecutionException e) {
            log.error(e);
        }
    }

    public void exit() {
    	Main._stage.close();
    }

    private EventHandler<MouseEvent> getOnCategoryClickListener(Label source) {
    	return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Label[] categories = {myfiles, docs, pictures, sounds, videos, loads, contacts};
				for(Label l : categories) {
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
				Label[] categories = {rendering, caching, hardware, software};
				for(Label l : categories) {
					l.setStyle("-fx-text-fill: #ECEFF4;");
					l.getStyleClass().remove("category2");
					l.getStyleClass().add("category");
				}
				source.setStyle("-fx-text-fill: #555555;");
				source.getStyleClass().remove("category");
				source.getStyleClass().add("category2");
				if(source.equals(software)) {
					software_pane.setVisible(true);
				} else {
					software_pane.setVisible(false);
				}
			}

    	};
    }


    private void backspace() {
        if(!pincode_textfield.getText().isEmpty()) {
            pincode_textfield.setText(pincode_textfield.getText().substring(0, pincode_textfield.getText().length() - 1));
        }
    }

    private void pincodeEnter(String num) {
    	pin += num;
        pincode_textfield.setText(pincode_textfield.getText() + "*");
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
                            break;
                        case 2:
                            if(current_element.type.equals("dir")) {
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
        if(!pin.isEmpty()) {
            if(back) {
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
        if(!pathname_textfield.getText().isEmpty()) {
            int id = HttpAPI.getInstance().createPath(cur_path.id, pincode, pathname_textfield.getText());
            FSObject path = new FSObject(id, "dir", pathname_textfield.getText(), "", 0, false, 0, System.currentTimeMillis(), System.currentTimeMillis());
            Label label = new Label(path.name, new ImageView(folderBlackIcon));
            label.setFont(new Font("Ubuntu Condensed", 18));
            label.setTextFill(Paint.valueOf("#7C7C7C"));
            label.setId(String.valueOf(path.id));
            label.setPrefWidth(340);
            label.setOnMouseClicked(getOnElementClick(label));
            Tooltip tooltip = new Tooltip("Имя: " + path.name + "\nТип: " + path.type + "\n"
                    + (path.type.equals("file") ? "Формат: " + path.format + "\n" : "")
                    + "Размер: " +  String.valueOf(path.size / 1024) + "КБ\n"
                    + (path.type.equals("dir") ? "Файлов: " + path.count + "\n" : "")
                    + "Дата создания: " + new Date(path.create_time * 1000).toLocaleString() + "\n"
                    + "Последнее обновление: " + new Date(path.update_time * 1000).toLocaleString());
            label.setTooltip(tooltip);
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
                Label label = new Label(f.name, new ImageView(getFileIcon(f.name)));
                label.setFont(new Font("Ubuntu Condensed", 18));
                label.setTextFill(Paint.valueOf("#7C7C7C"));
                label.setId(String.valueOf(f.id));
                label.setPrefWidth(340);
                label.setOnMouseClicked(getOnElementClick(label));
                Tooltip tooltip = new Tooltip("Имя: " + f.name + "\nТип: " + f.type + "\n"
                        + (f.type.equals("file") ? "Формат: " + f.format + "\n" : "")
                        + "Размер: " +  String.valueOf(f.size / 1024) + "КБ\n"
                        + (f.type.equals("dir") ? "Файлов: " + f.count + "\n" : "")
                        + "Дата создания: " + new Date(f.create_time * 1000).toLocaleString() + "\n"
                        + "Последнее обновление: " + new Date(f.update_time * 1000).toLocaleString());
                label.setTooltip(tooltip);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        files.getItems().add(new TableRow(f.type.equals("file") ? "Файл" : "Папка", label, String.valueOf(f.size / 1024) + "КБ", new Date(f.create_time * 1000).toLocaleString()));
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
                ((TableColumn) files.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("type"));
                ((TableColumn) files.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<TableRow, Label>("name"));
                ((TableColumn) files.getColumns().get(1)).setComparator(new Comparator<Label>() {
                    @Override
                    public int compare(Label p1, Label p2) {
                        return java.text.Collator.getInstance().compare(p1.getText(), p2.getText());
                    }
                });
                ((TableColumn) files.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("size"));
                ((TableColumn) files.getColumns().get(2)).setComparator(new Comparator<String>() {
                    @Override
                    public int compare(String p1, String p2) {
                        int one = Integer.parseInt(p1.replace("КБ", ""));
                        int two = Integer.parseInt(p2.replace("КБ", ""));
                        return one == two ? 0 : one < two ? -1 : 1;
                    }
                });
                ((TableColumn) files.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("createDate"));
                ((TableColumn) files.getColumns().get(3)).setComparator(new Comparator<String>() {
                    @Override
                    public int compare(String p1, String p2) {
                        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                        Date d1 = null, d2 = null;
                        try {
                            d1 = df.parse(p1);
                            d2 = df.parse(p2);
                        } catch(ParseException pe) {
                            pe.printStackTrace();
                        }
                        return d1.compareTo(d2);
                    }
                });
                for (int i=0;i<content.length;i++) {
                    FSObject fso = content[i];
                    Label label = new Label(fso.name, new ImageView(fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : fso.count > 0 ? folderFull : folderBlackIcon :/*fileIcon*/getFileIcon(fso.name)));
                    label.setFont(new Font("Ubuntu Condensed", 14));
                    label.setTextFill(Paint.valueOf("#000"));
                    label.setId(String.valueOf(fso.id));
                    label.setPrefWidth(340);
                    label.setOnMouseClicked(getOnElementClick(label));
                    Tooltip tooltip = new Tooltip("Имя: " + fso.name + "\nТип: " + fso.type + "\n"
                            + (fso.type.equals("file") ? "Формат: " + fso.format + "\n" : "")
                            + "Размер: " +  String.valueOf(fso.size / 1024) + "КБ\n"
                            + (fso.type.equals("dir") ? "Файлов: " + fso.count + "\n" : "")
                            + "Дата создания: " + new Date(fso.create_time * 1000).toLocaleString() + "\n"
                            + "Последнее обновление: " + new Date(fso.update_time * 1000).toLocaleString());
                    label.setTooltip(tooltip);
                    files.getItems().add(new TableRow(fso.type.equals("file") ? "Файл" : "Папка", label, String.valueOf(fso.size / 1024) + "КБ", new Date(fso.create_time * 1000).toLocaleString()));
                    if(fso.type.equals("dir")) {
                        path_childrens.add(fso);
                    }
                }
                CHILDRENS.put(path_id, path_childrens);
                if(!pin.equals("")) {
                    pincode = pincode_textfield.getText();
                    pincode_textfield.setText("");
                }
                if(back) {
                    if(!PARENT_PATH.isEmpty() && PARENT_PATH.size() > 1) {
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
                CHILDRENS.put(path_id, new ArrayList());
                if(!pin.equals("")) {
                    pincode = pincode_textfield.getText();
                    pincode_textfield.setText("");
                }
                if(back) {
                    if(!PARENT_PATH.isEmpty() && PARENT_PATH.size() > 1) {
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
                if(event.getButton() == MouseButton.PRIMARY) {
                    loadContent(path_id, "", current_tv);
                }
            }
        };
    }

    public void toggleSettings() {
        /*if(menu_opened) {
            settings.setStyle("-fx-background-color: transparent");
            settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/sttngs.png"))));
            menu.setVisible(false);
            menu_opened = false;
        } else {
            settings.setStyle("-fx-background-color: #F3F3F3");
            settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/sttngs.png"))));
            menu.setVisible(true);
            menu_opened = true;
        }*/
    }

    public class TableRow {
        private SimpleStringProperty type;
        private Label name;
        private SimpleStringProperty size;
        private SimpleStringProperty createDate;

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

    class ResizeListener implements EventHandler<MouseEvent>{
    	  double dx;
    	  double dy;
    	  double deltaX;
    	  double deltaY;
    	  double border = 10;
    	  boolean moveH;
    	  boolean moveV;
    	  boolean resizeH = false;
    	  boolean resizeV = false;
    	  Dimension minSize = new Dimension((int) Main._scene.getWidth(), (int) Main._scene.getHeight());

    	  @Override
    	  public void handle(MouseEvent t) {
    	    if(MouseEvent.MOUSE_MOVED.equals(t.getEventType())){
    	      if(t.getX() < border && t.getY() < border){
    	        Main._scene.setCursor(Cursor.NW_RESIZE);
    	        resizeH = true;
    	        resizeV = true;
    	        moveH = true;
    	        moveV = true;
    	      }
    	      else if(t.getX() < border && t.getY() > Main._scene.getHeight() -border){
    	    	Main._scene.setCursor(Cursor.SW_RESIZE);
    	        resizeH = true;
    	        resizeV = true;
    	        moveH = true;
    	        moveV = false;
    	      }
    	      else if(t.getX() > Main._scene.getWidth() -border && t.getY() < border){
    	    	Main._scene.setCursor(Cursor.NE_RESIZE);
    	        resizeH = true;
    	        resizeV = true;
    	        moveH = false;
    	        moveV = true;
    	      }
    	      else if(t.getX() > Main._scene.getWidth() -border && t.getY() > Main._scene.getHeight() -border){
    	    	Main._scene.setCursor(Cursor.SE_RESIZE);
    	        resizeH = true;
    	        resizeV = true;
    	        moveH = false;
    	        moveV = false;
    	      }
    	      else if(t.getX() < border || t.getX() > Main._scene.getWidth() -border){
    	    	Main._scene.setCursor(Cursor.E_RESIZE);
    	        resizeH = true;
    	        resizeV = false;
    	        moveH = (t.getX() < border);
    	        moveV = false;
    	      }
    	      else if(t.getY() < border || t.getY() > Main._scene.getHeight() -border){
    	    	Main._scene.setCursor(Cursor.N_RESIZE);
    	        resizeH = false;
    	        resizeV = true;
    	        moveH = false;
    	        moveV = (t.getY() < border);
    	      }
    	      else{
    	    	Main._scene.setCursor(Cursor.DEFAULT);
    	        resizeH = false;
    	        resizeV = false;
    	        moveH = false;
    	        moveV = false;
    	      }
    	    }
    	    else if(MouseEvent.MOUSE_PRESSED.equals(t.getEventType())){
    	      dx = Main._stage.getWidth() - t.getX();
    	      dy = Main._stage.getHeight() - t.getY();
    	    }
    	    else if(MouseEvent.MOUSE_DRAGGED.equals(t.getEventType())){
    	      if(resizeH){
    	        if(Main._stage.getWidth() <= minSize.width){
    	          if(moveH){
    	            deltaX = Main._stage.getX()-t.getScreenX();
    	            if(t.getX() < 0){// if new > old, it's permitted
    	            	Main._stage.setWidth(deltaX+Main._stage.getWidth());
    	            	Main._stage.setX(t.getScreenX());
    	            }
    	          }
    	          else{
    	            if(t.getX()+dx - Main._stage.getWidth() > 0){
    	            	Main._stage.setWidth(t.getX()+dx);
    	            }
    	          }
    	        }
    	        else if(Main._stage.getWidth() > minSize.width){
    	          if(moveH){
    	            deltaX = Main._stage.getX()-t.getScreenX();
    	            Main._stage.setWidth(deltaX+Main._stage.getWidth());
    	            Main._stage.setX(t.getScreenX());
    	          }
    	          else{
    	        	Main._stage.setWidth(t.getX()+dx);
    	          }
    	        }
    	      }
    	      if(resizeV){
      	        if(Main._stage.getHeight() <= minSize.height){
      	          if(moveV){
      	            deltaY = Main._stage.getY()-t.getScreenY();
      	            if(t.getY() < 0){// if new > old, it's permitted
      	            	Main._stage.setHeight(deltaY+Main._stage.getHeight());
      	            	Main._stage.setY(t.getScreenY());
      	            }
      	          }
      	          else{
      	            if(t.getY()+dy - Main._stage.getHeight() > 0){
      	            	Main._stage.setHeight(t.getY()+dy);
      	            }
      	          }
      	        }
      	        else if(Main._stage.getHeight() > minSize.height){
      	          if(moveV){
      	            deltaY = Main._stage.getY()-t.getScreenY();
      	            Main._stage.setHeight(deltaY+Main._stage.getHeight());
      	            Main._stage.setY(t.getScreenY());
      	          }
      	          else{
      	        	Main._stage.setHeight(t.getY()+dy);
      	          }
      	        }
      	      }
    	    }
    	  }
    	}
}
