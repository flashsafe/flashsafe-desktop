package ru.flashsafe.perspective;

import java.awt.Desktop;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.FileController;
import ru.flashsafe.Main;
import ru.flashsafe.controller.DocController;
import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.util.ApplicationProperties;
import ru.flashsafe.util.FileObjectViewHelper;

public class TablePerspective implements Perspective {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TablePerspective.class);

    private final TableView<FileObject> tableView;

    private final ObservableList<FileObject> dataModel;

    private final FileController fileController;

    private final FileObjectViewHelper fileObjectViewHelper;
    
    private List<String> imageExtensions = Arrays.asList("jpg", "jpe", "jpeg", "png", "bmp", "tiff", "gif");
    
    private List<String> officeOnlineSupportExtensions = Arrays.asList("doc", "docx", "docm", "dotm", "dotx", "xls", "xlsx", "xlsb", "xlsm", "ppt", "pptx", "ppsx", "pps", "pptm", "potm", "ppam",
    		"potx", "ppsm");
    private List<String> googleDocsViewerSupportExtensions = Arrays.asList("odt", "pdf", "pages", "xps", "rtf", "xsw", "sdw", "txt", "html", "sdc", "sdd", "sxi");
    
    private int currentIndex;

    public TablePerspective(TableView<FileObject> table, ObservableList<FileObject> dataModel,
            FileObjectViewHelper fileObjectViewHelper, FileController fileController) {
        this.tableView = table;
        this.dataModel = dataModel;
        this.fileObjectViewHelper = fileObjectViewHelper;
        this.fileController = fileController;
        this.fileObjectViewHelper.setFileController(this.fileController);
        initPerspective();
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
	private void initPerspective() {
        // FIXME use column name not index
        TableColumn<FileObject, Label> nameColumn = (TableColumn<FileObject, Label>) tableView.getColumns().get(0);
        nameColumn.setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            return new ReadOnlyObjectWrapper<Label>(fileObjectViewHelper.createLabelFor(value));
        });
        nameColumn.setStyle("-fx-text-fill: #DDD;");
        nameColumn.setComparator(new Comparator<Label>() {

			@Override
			public int compare(Label arg0, Label arg1) {
				if(arg0.getContextMenu().getItems().size() == 8 ^ arg1.getContextMenu().getItems().size() == 8) {
					if(arg0.getContextMenu().getItems().size() == 8) {
						return 1;
					} else {
						return -1;
					}
				}
				int compare = 0;
				List<Character> alphabet = Arrays.asList('0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
						'а','б','в','г','д','е','ё','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ь','ы','ъ','э','ю','я');
				String one = arg0.getText().toLowerCase();
				String two = arg1.getText().toLowerCase();
				for(int i=0;i<(one.length() > two.length() ? two.length() : one.length());i++) {
					if(one.charAt(i) != two.charAt(i)) {
						if(alphabet.indexOf(one.charAt(i)) > alphabet.indexOf(two.charAt(i))) {
							compare = 1;
						} else {
							compare = -1;
						}
						break;
					}
				}
				return compare;
			}
		});

        TableColumn<FileObject, String> createDateColumn = (TableColumn<FileObject, String>) tableView.getColumns().get(1);
        createDateColumn.setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            long timeInMilliseconds = /*(value.getCreationTime() * 1000)*/0;
            // FIXME switch to Java8 API
                return new SimpleStringProperty(new Date(timeInMilliseconds).toLocaleString());
            });
        createDateColumn.setStyle("/*-fx-background-color: #2E3335;*/ -fx-text-fill: #DDD;");
        
        ((TableColumn<FileObject, String>) tableView.getColumns().get(2)).setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            return new SimpleStringProperty(fileObjectViewHelper.getTypeDescriptionFor(value));
        });

        ((TableColumn<FileObject, String>) tableView.getColumns().get(2)).setStyle("/*-fx-background-color: #2E3335;*/ -fx-text-fill: #DDD;");
        
        TableColumn<FileObject, String> sizeColumn = (TableColumn<FileObject, String>) tableView.getColumns().get(3);
        sizeColumn.setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            return new SimpleStringProperty(fileObjectViewHelper.getSizeDescriptionFor(value));
        });
        sizeColumn.setStyle("/*-fx-background-color: #2E3335;*/ -fx-text-fill: #DDD;");
        sizeColumn.setComparator(new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				if(arg0.equals("") || arg1.equals("")) {
					if(arg0.equals("") && arg1.equals("")) {
						return 0;
					} else if(arg0.length() < arg1.length()) {
						return -1;
					} else {
						return 1;
					}
				}
				int one = Integer.parseInt(arg0.replace(" KB", ""));
				int two = Integer.parseInt(arg1.replace(" KB", ""));
				if(one < two) {
					return -1;
				} else if(one > two) {
					return 1;
				} else {
					return 0;
				}
			}
		});
        
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                int clickCount = event.getClickCount();
                if (clickCount == 2) {
                    FileObject fileObject = tableView.getSelectionModel().getSelectedItem();
                    if (fileObject != null) {
                        if (fileObject.getType() == FileObjectType.DIRECTORY) {
                            Platform.runLater(() -> ((MainSceneController) fileController).browseFolder(fileObject.getHash()));
                        } else{
                            String ext = fileObject.getName().split("\\.")[fileObject.getName().split("\\.").length - 1].toLowerCase();
                            if(ext.equals("jpg") || ext.equals("jpe") || ext.equals("jpeg") || ext.equals("png") || ext.equals("bmp") || ext.equals("tiff") || ext.equals("gif")) {
                                openPicture(fileObject);
                            } else if(ext.equals("avi") || ext.equals("mp4") || ext.equals("3gp") || ext.equals("3gpp") || ext.equals("wmv") || ext.equals("mov") || ext.equals("flv")) {
                                
                            } else if(ext.equals("mp3") || ext.equals("aac") || ext.equals("wma") || ext.equals("wav") || ext.equals("midi") || ext.equals("amr")) {
                                openAudio(fileObject);
                            } else if(officeOnlineSupportExtensions.contains(ext)) {
                                openWithOfficeOnline(fileObject);
                            } else if(googleDocsViewerSupportExtensions.contains(ext)) {
                            	openWithGoogleDocsViewer(fileObject);
                            }
                            /*new Thread(() -> {
                                try {
                                    Platform.runLater(() -> Main._scene.setCursor(Cursor.WAIT));
                                    File f = new File(fileObject.getName());
                                    f.createNewFile();
                                    f.deleteOnExit();
                                    HttpURLConnection conn = (HttpURLConnection) new URL("https://flashsafe-alpha.azurewebsites.net/cloud/"
                                            + ApplicationProperties.userId()
                                            + fileObject.getAbsolutePath()
                                                    .replace("fls:/", "").replace(" ", "%20")).openConnection();
                                    conn.connect();
                                    InputStream in = conn.getInputStream();
                                    int b;
                                    OutputStream out = new FileOutputStream(f);
                                    while((b = in.read()) != -1) out.write((byte) b);
                                    in.close();
                                    conn.disconnect();
                                    out.flush();
                                    out.close();
                                    Platform.runLater(() -> Main._scene.setCursor(Cursor.DEFAULT));
                                    open(f);
                                } catch (IOException ex) {
                                    LOGGER.error("Error on open file " + fileObject.getName(), ex);
                                }
                                
                            }).start();*/
                        }
                    }
                }
            }
        });
        
        tableView.setRowFactory(tv -> {
            TableRow<FileObject> row = new TableRow<>();
            /*ContextMenu menu = new ContextMenu();
        	MenuItem item = new MenuItem();
            item.setText("Create folder");
            item.setOnAction(event -> ((MainSceneController) fileController).showPathDialog());
            menu.getItems().add(item);
            ((MainSceneController) fileController).name_column.setContextMenu(menu);*/
            //row.setContextMenu(row.getItem() == null ? null : fileObjectViewHelper.createContextMenuFor(row.getItem()));
            return row ;
        });
    }
    
    @SuppressWarnings("unused")
	private void open(File file) {
        openSystemSpecific(file.getPath());
        //openDESKTOP(file);
    }
    
    @SuppressWarnings("unused")
	private static boolean openDESKTOP(File file) {

        LOGGER.info("Trying to use Desktop.getDesktop().open() with " + file.toString());
        try {
            if (!Desktop.isDesktopSupported()) {
                LOGGER.error("Platform is not supported.");
                return false;
            }

            if (!Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                LOGGER.error("OPEN is not supported.");
                return false;
            }

            Desktop.getDesktop().open(file);

            return true;
        } catch (Throwable t) {
            LOGGER.error("Error using desktop open.", t);
            return false;
        }
    }
    
    private static boolean openSystemSpecific(String what) {

        EnumOS os = getOs();

        if (os.isLinux()) {
            if (runCommand("kde-open", "%s", what)) return true;
            if (runCommand("gnome-open", "%s", what)) return true;
            if (runCommand("xdg-open", "%s", what)) return true;
        }

        if (os.isMac()) {
            if (runCommand("open", "%s", what)) return true;
        }

        if (os.isWindows()) {
            if (runCommand("explorer", "%s", what)) return true;
        }

        return false;
    }
    
    private static boolean runCommand(String command, String args, String file) {

        LOGGER.info("Trying to exec:\n   cmd = " + command + "\n   args = " + args + "\n   %s = " + file);

        String[] parts = prepareCommand(command, args, file);

        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null) return false;

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    LOGGER.error("Process ended immediately.");
                    return false;
                } else {
                    LOGGER.error("Process crashed.");
                    return false;
                }
            } catch (IllegalThreadStateException itse) {
                LOGGER.error("Process is running.");
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Error running command.", e);
            return false;
        }
    }
    
    private static String[] prepareCommand(String command, String args, String file) {

        List<String> parts = new ArrayList<String>();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing

                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[parts.size()]);
    }
    
    public static enum EnumOS {
        linux, macos, solaris, unknown, windows;

        public boolean isLinux() {

            return this == linux || this == solaris;
        }


        public boolean isMac() {

            return this == macos;
        }


        public boolean isWindows() {

            return this == windows;
        }
    }
    
    public static EnumOS getOs() {

        String s = System.getProperty("os.name").toLowerCase();

        if (s.contains("win")) {
            return EnumOS.windows;
        }

        if (s.contains("mac")) {
            return EnumOS.macos;
        }

        if (s.contains("solaris")) {
            return EnumOS.solaris;
        }

        if (s.contains("sunos")) {
            return EnumOS.solaris;
        }

        if (s.contains("linux")) {
            return EnumOS.linux;
        }

        if (s.contains("unix")) {
            return EnumOS.linux;
        } else {
            return EnumOS.unknown;
        }
    }
    
    private void openPicture(FileObject fileObject) {
    	//Main._scene.setCursor(javafx.scene.Cursor.WAIT);
        Stage picStage = new Stage();
        picStage.initStyle(StageStyle.TRANSPARENT);
        picStage.setResizable(false);
        picStage.setFullScreen(true);
        picStage.setTitle("Flashsafe - " + fileObject.getHash());
        ImageView parent = new ImageView();
        parent.setVisible(false);
        
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	int width = gd.getDisplayMode().getWidth();
    	int height = gd.getDisplayMode().getHeight();
    	
    	final ProgressIndicator loading = new ProgressIndicator();
    	AnchorPane.setTopAnchor(loading, (height - loading.getHeight()) / 2);
        AnchorPane.setLeftAnchor(loading, (width - loading.getWidth()) / 2);
        loading.setProgress(-1);
        
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(parent);
        pane.getChildren().add(loading);
        
        ImageView close = new ImageView();
        close.setImage(new Image(getClass().getResourceAsStream("/img/close.png")));
        close.setFitWidth(24);
        close.setFitHeight(24);
        AnchorPane.setTopAnchor(close, 13.0);
        AnchorPane.setRightAnchor(close, 13.0);
        close.setOnMouseClicked((event) -> picStage.close());
        pane.getChildren().add(close);
        
        List<FileObject> pictures = tableView.getItems().filtered(new Predicate<FileObject>() {

			@Override
			public boolean test(FileObject fo) {
				String ext = fo.getName().split("\\.")[fo.getName().split("\\.").length - 1].toLowerCase();
				return imageExtensions.contains(ext);
			}
		});
        currentIndex = pictures.indexOf(fileObject);
        
        final Pane prev = new Pane();
        final Pane next = new Pane();
        
        prev.setPrefWidth(60);
        prev.setPrefHeight(300);
        AnchorPane.setTopAnchor(prev, (height - prev.getHeight()) / 3);
        AnchorPane.setLeftAnchor(prev, 5.0);
        Label parrow = new Label("<");
        parrow.setFont(Font.font("Tahoma", FontWeight.BOLD, 36));
        parrow.setTextFill(Color.WHITESMOKE);
        parrow.setLayoutX(20);
        parrow.setLayoutY(120.0);
        prev.getChildren().add(parrow);
        prev.setOnMouseEntered(event -> prev.setStyle("-fx-background-color: rgba(0,0,0,0.75);"));
        prev.setOnMouseExited(event -> prev.setStyle("-fx-background-color: transparent;"));
        if(currentIndex != 0) prev.setVisible(true); else prev.setVisible(false);
        prev.setOnMouseClicked((event) -> {
        	parent.setVisible(false);
            loading.setVisible(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
		        	if(currentIndex != 0) {
		        		try {
		        			currentIndex = currentIndex - 1;
		        			File iimg = new File(pictures.get(currentIndex).getName());
		                    iimg.createNewFile();
		                    iimg.deleteOnExit();
		                    FileOperation idownloadOperation = ((MainSceneController) fileController).fileManager.copy(pictures.get(currentIndex).getHash(), iimg.getAbsolutePath());
		                    while (idownloadOperation.getState() != OperationState.FINISHED) {
		                        Thread.sleep(200);
		                    }
		                    Image iimage = new Image(new FileInputStream(iimg));
		                    if(iimage.getWidth() > width - 50 || iimage.getHeight() > height - 50) {
		        	    		parent.setFitWidth(iimage.getWidth() / 2);
		        	    		parent.setFitHeight(iimage.getHeight() / 2);
		        	    	} else {
		        	    		parent.setFitWidth(iimage.getWidth());
		        	    		parent.setFitHeight(iimage.getHeight());
		        	    	}
		                    AnchorPane.setTopAnchor(parent, (height - parent.getFitHeight()) / 2);
		                    AnchorPane.setRightAnchor(parent, (width - parent.getFitWidth()) / 2);
		                    AnchorPane.setBottomAnchor(parent, (height - parent.getFitHeight()) / 2);
		                    AnchorPane.setLeftAnchor(parent, (width - parent.getFitWidth()) / 2);
		                    parent.setImage(new Image(new FileInputStream(iimg)));
		                    if(currentIndex == 0) prev.setVisible(false);
		                    if(!next.isVisible()) next.setVisible(true);
		        		} catch(FileOperationException | IOException | InterruptedException e) {
		        			LOGGER.error("Error on open picture " + fileObject.getHash(), e);
		        		}
		        	}
		        	loading.setVisible(false);
		        	parent.setVisible(true);
		        	return null;
		        }
		    };
		    new Thread(task).start();
        });
        
        next.setPrefWidth(60);
        next.setPrefHeight(300);
        AnchorPane.setTopAnchor(next, (height - next.getHeight()) / 3);
        AnchorPane.setRightAnchor(next, 5.0);
        Label narrow = new Label(">");
        narrow.setFont(Font.font("Tahoma", FontWeight.BOLD, 36));
        narrow.setTextFill(Color.WHITESMOKE);
        narrow.setLayoutX(20);
        narrow.setLayoutY(120.0);
        next.getChildren().add(narrow);
        next.setOnMouseEntered(event -> next.setStyle("-fx-background-color: rgba(0,0,0,0.75);"));
        next.setOnMouseExited(event -> next.setStyle("-fx-background-color: transparent;"));
        if(currentIndex != pictures.size() - 1) next.setVisible(true); else next.setVisible(false);
        next.setOnMouseClicked((event) -> {
        	parent.setVisible(false);
            loading.setVisible(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
		        	if(currentIndex < pictures.size() - 1) {
		        		try {
		        			currentIndex = currentIndex + 1;
		        			File iimg = new File(pictures.get(currentIndex).getName());
		                    iimg.createNewFile();
		                    iimg.deleteOnExit();
		                    FileOperation idownloadOperation = ((MainSceneController) fileController).fileManager.copy(pictures.get(currentIndex).getHash(), iimg.getAbsolutePath());
		                    while (idownloadOperation.getState() != OperationState.FINISHED) {
		                        Thread.sleep(200);
		                    }
		                    Image iimage = new Image(new FileInputStream(iimg));
		                    if(iimage.getWidth() > width - 50 || iimage.getHeight() > height - 50) {
		        	    		parent.setFitWidth(iimage.getWidth() / 2);
		        	    		parent.setFitHeight(iimage.getHeight() / 2);
		        	    	} else {
		        	    		parent.setFitWidth(iimage.getWidth());
		        	    		parent.setFitHeight(iimage.getHeight());
		        	    	}
		                    AnchorPane.setTopAnchor(parent, (height - parent.getFitHeight()) / 2);
		                    AnchorPane.setRightAnchor(parent, (width - parent.getFitWidth()) / 2);
		                    AnchorPane.setBottomAnchor(parent, (height - parent.getFitHeight()) / 2);
		                    AnchorPane.setLeftAnchor(parent, (width - parent.getFitWidth()) / 2);
		                    parent.setImage(new Image(new FileInputStream(iimg)));
		                    if(currentIndex == pictures.size() - 1) next.setVisible(false);
		                    if(!prev.isVisible()) prev.setVisible(true);
		        		} catch(FileOperationException | IOException | InterruptedException e) {
		        			LOGGER.error("Error on open picture " + fileObject.getHash(), e);
		        		}
		        	}
		        	loading.setVisible(false);
		        	parent.setVisible(true);
		        	return null;
                }
            };
            new Thread(task).start();
        });
        
        pane.getChildren().add(prev);
        pane.getChildren().add(next);
        
        pane.setVisible(true);
        pane.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
        
        Scene scene = new Scene(pane, Color.TRANSPARENT);
        scene.setOnKeyPressed((event) -> {
        	switch(event.getCode()) {
        	case LEFT:
        		parent.setVisible(false);
                loading.setVisible(true);
                Task<Void> ltask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
    		        	if(currentIndex != 0) {
    		        		try {
    		        			currentIndex = currentIndex - 1;
    		        			File iimg = new File(pictures.get(currentIndex).getName());
    		                    iimg.createNewFile();
    		                    iimg.deleteOnExit();
    		                    FileOperation idownloadOperation = ((MainSceneController) fileController).fileManager.copy(pictures.get(currentIndex).getHash(), iimg.getAbsolutePath());
    		                    while (idownloadOperation.getState() != OperationState.FINISHED) {
    		                        Thread.sleep(200);
    		                    }
    		                    Image iimage = new Image(new FileInputStream(iimg));
    		                    if(iimage.getWidth() > width - 50 || iimage.getHeight() > height - 50) {
    		        	    		parent.setFitWidth(iimage.getWidth() / 2);
    		        	    		parent.setFitHeight(iimage.getHeight() / 2);
    		        	    	} else {
    		        	    		parent.setFitWidth(iimage.getWidth());
    		        	    		parent.setFitHeight(iimage.getHeight());
    		        	    	}
    		                    AnchorPane.setTopAnchor(parent, (height - parent.getFitHeight()) / 2);
    		                    AnchorPane.setRightAnchor(parent, (width - parent.getFitWidth()) / 2);
    		                    AnchorPane.setBottomAnchor(parent, (height - parent.getFitHeight()) / 2);
    		                    AnchorPane.setLeftAnchor(parent, (width - parent.getFitWidth()) / 2);
    		                    parent.setImage(new Image(new FileInputStream(iimg)));
    		                    if(currentIndex == 0) prev.setVisible(false);
    		                    if(!next.isVisible()) next.setVisible(true);
    		        		} catch(FileOperationException | IOException | InterruptedException e) {
    		        			LOGGER.error("Error on open picture " + fileObject.getHash(), e);
    		        		}
    		        	}
    		        	loading.setVisible(false);
    		        	parent.setVisible(true);
    		        	return null;
    		        }
    		    };
    		    new Thread(ltask).start();
        		break;
        	case RIGHT:
        		parent.setVisible(false);
                loading.setVisible(true);
                Task<Void> rtask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
    		        	if(currentIndex < pictures.size() - 1) {
    		        		try {
    		        			currentIndex = currentIndex + 1;
    		        			File iimg = new File(pictures.get(currentIndex).getName());
    		                    iimg.createNewFile();
    		                    iimg.deleteOnExit();
    		                    FileOperation idownloadOperation = ((MainSceneController) fileController).fileManager.copy(pictures.get(currentIndex).getHash(), iimg.getAbsolutePath());
    		                    while (idownloadOperation.getState() != OperationState.FINISHED) {
    		                        Thread.sleep(200);
    		                    }
    		                    Image iimage = new Image(new FileInputStream(iimg));
    		                    if(iimage.getWidth() > width - 50 || iimage.getHeight() > height - 50) {
    		        	    		parent.setFitWidth(iimage.getWidth() / 2);
    		        	    		parent.setFitHeight(iimage.getHeight() / 2);
    		        	    	} else {
    		        	    		parent.setFitWidth(iimage.getWidth());
    		        	    		parent.setFitHeight(iimage.getHeight());
    		        	    	}
    		                    AnchorPane.setTopAnchor(parent, (height - parent.getFitHeight()) / 2);
    		                    AnchorPane.setRightAnchor(parent, (width - parent.getFitWidth()) / 2);
    		                    AnchorPane.setBottomAnchor(parent, (height - parent.getFitHeight()) / 2);
    		                    AnchorPane.setLeftAnchor(parent, (width - parent.getFitWidth()) / 2);
    		                    parent.setImage(new Image(new FileInputStream(iimg)));
    		                    if(currentIndex == pictures.size() - 1) next.setVisible(false);
    		                    if(!prev.isVisible()) prev.setVisible(true);
    		        		} catch(FileOperationException | IOException | InterruptedException e) {
    		        			LOGGER.error("Error on open picture " + fileObject.getHash(), e);
    		        		}
    		        	}
    		        	loading.setVisible(false);
    		        	parent.setVisible(true);
    		        	return null;
                    }
                };
                new Thread(rtask).start();
        		break;
        	}
        });
        picStage.setScene(scene);
        picStage.getIcons().add(new Image("/img/logo.png"));
        //Main._scene.setCursor(javafx.scene.Cursor.DEFAULT);
        picStage.show();
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	try {
	            	File img = new File(fileObject.getName());
		            img.createNewFile();
		            img.deleteOnExit();
		            
		            
		            FileOperation downloadOperation = ((MainSceneController) fileController).fileManager.copy(fileObject.getHash(), img.getAbsolutePath());
		            while (downloadOperation.getState() != OperationState.FINISHED) {
		                Thread.sleep(200);
		            }
		            
		            Image image = new Image(new FileInputStream(img));
		            
		            if(image.getWidth() > width - 50 || image.getHeight() > height - 50) {
			    		parent.setFitWidth(image.getWidth() / 2);
			    		parent.setFitHeight(image.getHeight() / 2);
			    	} else {
			    		parent.setFitWidth(image.getWidth());
			    		parent.setFitHeight(image.getHeight());
			    	}
		            
		            AnchorPane.setTopAnchor(parent, (height - parent.getFitHeight()) / 2);
		            AnchorPane.setRightAnchor(parent, (width - parent.getFitWidth()) / 2);
		            AnchorPane.setBottomAnchor(parent, (height - parent.getFitHeight()) / 2);
		            AnchorPane.setLeftAnchor(parent, (width - parent.getFitWidth()) / 2);
		            
		            parent.setImage(image);
		            loading.setVisible(false);
		            parent.setVisible(true);
            	} catch(IOException | InterruptedException | FileOperationException ex) {
                    LOGGER.error("Error on open picture " + fileObject.getHash(), ex);
                }
            	return null;
            }
        };
        new Thread(task).start();
    }

    private void openWithOfficeOnline(FileObject fileObject) {
        //Main._scene.setCursor(javafx.scene.Cursor.WAIT);
        Stage docStage = new Stage();
        docStage.setResizable(false);
        docStage.setTitle("Flashsafe - " + fileObject.getHash());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/doc.fxml"));
        try {
            String url = "https://view.officeapps.live.com/op/view.aspx?src=https://flashsafe-alpha.azurewebsites.net/cloud/" + ApplicationProperties.userId() + fileObject.getHash().replace("fls:/", "").replace(" ", "%20");
            loader.setController(new DocController(url));
            AnchorPane parent = loader.load();
            Scene scene = new Scene(parent);
            docStage.setScene(scene);
            docStage.getIcons().add(new Image("/img/logo.png"));
            docStage.initModality(Modality.WINDOW_MODAL);
            //docStage.initOwner(Main._stage);
            //Main._scene.setCursor(javafx.scene.Cursor.DEFAULT);
            docStage.show();
        } catch(IOException ex) {
            LOGGER.error("Error on open document " + fileObject.getHash(), ex);
        }
    }
    
    private void openWithGoogleDocsViewer(FileObject fileObject) {
        //Main._scene.setCursor(javafx.scene.Cursor.WAIT);
        Stage docStage = new Stage();
        docStage.setResizable(false);
        docStage.setTitle("Flashsafe - " + fileObject.getHash());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/doc.fxml"));
        try {
            String url = "https://docs.google.com/viewer?url=https://flashsafe-alpha.azurewebsites.net/cloud/" + ApplicationProperties.userId() + fileObject.getHash().replace("fls:/", "").replace(" ", "%20");
            loader.setController(new DocController(url));
            AnchorPane parent = loader.load();
            Scene scene = new Scene(parent);
            docStage.setScene(scene);
            docStage.getIcons().add(new Image("/img/logo.png"));
            docStage.initModality(Modality.WINDOW_MODAL);
            //docStage.initOwner(Main._stage);
            //Main._scene.setCursor(javafx.scene.Cursor.DEFAULT);
            docStage.show();
        } catch(IOException ex) {
            LOGGER.error("Error on open document " + fileObject.getHash(), ex);
        }
    }
    
    private void openAudio(FileObject fileObject) {
        String url = "http://flashsafe-alpha.azurewebsites.net/gettrack.php?uid=" + ApplicationProperties.userId() + "&file=" + fileObject.getHash().replace("fls:/", "").replace(" ", "%20");
        Media sound = new Media(url);
        MediaPlayer player;
        player = new MediaPlayer(sound);
        ((MainSceneController) fileController).player = player;
        ((MainSceneController) fileController).play.setOnAction((event) -> {
            player.play();
            ((MainSceneController) fileController).play.setVisible(false);
            ((MainSceneController) fileController).pause.setVisible(true);
        });
        ((MainSceneController) fileController).pause.setOnAction((event) -> {
            player.pause();
            ((MainSceneController) fileController).pause.setVisible(false);
            ((MainSceneController) fileController).play.setVisible(true);
        });
        ((MainSceneController) fileController).pause.setVisible(true);
        ((MainSceneController) fileController).buffered_progress.setVisible(true);
        ((MainSceneController) fileController).track.setVisible(true);
        ((MainSceneController) fileController).pprogress.setVisible(true);
        ((MainSceneController) fileController).play.setCursor(Cursor.HAND);
        ((MainSceneController) fileController).pause.setCursor(Cursor.HAND);
        ((MainSceneController) fileController).track.setCursor(Cursor.DEFAULT);
        ((MainSceneController) fileController).pprogress.setCursor(Cursor.DEFAULT);
        ((MainSceneController) fileController).pprogress.setOnMouseClicked((event) -> {
        	player.seek(Duration.seconds(((MainSceneController) fileController).pprogress.getValue()));
        });
        player.setOnStopped(() -> {
        	((MainSceneController) fileController).pause.setVisible(false);
            ((MainSceneController) fileController).play.setVisible(true);
        });
        player.setOnReady(() -> {
        	((MainSceneController) fileController).track.setText(player.getMedia().getMetadata().get("artist") + " - " + player.getMedia().getMetadata().get("title"));
            ((MainSceneController) fileController).pprogress.setMin(0.0);
            ((MainSceneController) fileController).pprogress.setMax(player.getTotalDuration().toSeconds());
            player.bufferProgressTimeProperty().addListener(new ChangeListener<Duration>() {
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					((MainSceneController) fileController).buffered_progress.setProgress(newValue.toSeconds() / player.getTotalDuration().toSeconds());
				}
			});
            player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    ((MainSceneController) fileController).pprogress.setValue(newValue.toSeconds());
                }
            });
            player.setOnEndOfMedia(() -> player.stop());
            System.out.println(((Image) sound.getMetadata().get("image")));
        	player.play();
        });
    }
    
    @Override
    public void switchOn() {
        SortedList<FileObject> sortedFiles = new SortedList<>(dataModel);
        sortedFiles.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(dataModel);
        tableView.setVisible(true);
    }

    @Override
    public void switchOff() {
        tableView.setVisible(false);
        tableView.setItems(null);
    }

    @Override
    public PerspectiveType getType() {
        return PerspectiveType.TABLE;
    }

}
