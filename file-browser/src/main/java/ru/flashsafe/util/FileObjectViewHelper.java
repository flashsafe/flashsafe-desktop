package ru.flashsafe.util;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import ru.flashsafe.FileController;
import ru.flashsafe.IconUtil;
import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.view.CreatePathPane;
import ru.flashsafe.view.EnterPincodePane;
import ru.flashsafe.view.MainPane;

public class FileObjectViewHelper {

    private static final int KILOBYTE = 1024;
    
    private final ResourceBundle resourceBundle;
    
    private FileController fileController;
    
    public FileObjectViewHelper(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    
    public void setFileController(FileController _fileController) {
        this.fileController = _fileController;
    }
    
    public Label createLabelFor(FileObject fileObject) {
        Label label = new Label(fileObject.getName());
        label.setFont(new Font("Ubuntu Condensed", 14));
        label.setTextFill(Paint.valueOf("#DDD"));
        label.setTextAlignment(TextAlignment.CENTER);
        try {
            Tooltip tooltip = createTooltipFor(fileObject);
            label.setTooltip(tooltip);
            ImageView icon = createIcon(fileObject);
            label.setGraphic(icon);
            ContextMenu menu = createContextMenuFor(fileObject);
	        label.setContextMenu(menu);
	        /*if(fileObject.getType() == FileObjectType.DIRECTORY) {
	        	label.setOnDragOver((event) -> {
	            	if(event.getDragboard().hasString()) {
	                    event.acceptTransferModes(TransferMode.MOVE);
	                }
	                event.consume();
	            });
	            
	        	label.setOnDragOver((event) -> {
	            	Dragboard db = event.getDragboard();
	                if (db.hasString()) {
	                	try {
	                		((MainSceneController) fileController).fileManager.move(db.getString(), fileObject.getAbsolutePath());
	                	} catch(FileOperationException e) {
	                		((MainSceneController) fileController).LOGGER.error("Error on move object " + db.getString(), e);
	                	}
	                }
	                event.setDropCompleted(true);
	                event.consume();
	            });
	        } else {
	        	label.setOnDragDetected(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
		                Dragboard db = label.startDragAndDrop(TransferMode.MOVE);
		                ClipboardContent content = new ClipboardContent();
		                content.putString(fileObject.getAbsolutePath());
		                db.setContent(content);
		                event.consume();
					}});
	        }*/
            return label;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getTypeDescriptionFor(FileObject fileObject) {
        String description;
        if (fileObject.getType() == FileObjectType.FILE) {
            description = resourceBundle.getString("file");
        } else {
            description = resourceBundle.getString("folder");
        }
        return description;
    }
    
    public String getSizeDescriptionFor(FileObject fileObject) {
        if (fileObject.getType() == FileObjectType.DIRECTORY) {
            return "";
        }
        try {
            String description = String.valueOf(fileObject.getSize() / KILOBYTE);
            return description + " " + resourceBundle.getString("kilobyte");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ImageView createIcon(FileObject fileObject) {
        ImageView icon; 
        if (fileObject.getType() == FileObjectType.DIRECTORY) {
            icon = new ImageView(IconUtil.getFolderIcon((Directory)fileObject));
        } else {
            icon = new ImageView(IconUtil.getFileIcon(fileObject.getName()));
        }
        icon.setFitHeight(24);
        icon.setFitWidth(24);
        return icon;
    }
    
    private Tooltip createTooltipFor(FileObject fileObject) throws IOException {
        StringBuilder tooltipString = new StringBuilder();
        tooltipString.append(resourceBundle.getString("name")).append(": ").append(fileObject.getName()).append(System.lineSeparator())
                .append(resourceBundle.getString("type")).append(": ").append(fileObject.getType()).append(System.lineSeparator());
        if (fileObject.getType() == FileObjectType.FILE) {
            tooltipString.append(resourceBundle.getString("file_format")).append(": ").append(((File)fileObject).getFileFormat())
                    .append(System.lineSeparator());
        }
        tooltipString.append(resourceBundle.getString("size")).append(": ").append(getSizeDescriptionFor(fileObject)).append(System.lineSeparator());
        if (fileObject.getType() == FileObjectType.DIRECTORY) {
            tooltipString.append(resourceBundle.getString("number_of_files")).append(": ").append(((Directory)fileObject).getCount())
                    .append(System.lineSeparator());
        }
        /*
        tooltipString.append(resourceBundle.getString("creation_date")).append(": ")
                .append(new Date(fsObject.create_time * 1000).toLocaleString()).append(System.lineSeparator())
                .append(resourceBundle.getString("last_update")).append(": ")
                .append(new Date(fsObject.update_time * 1000).toLocaleString()).append(System.lineSeparator());*/
        return new Tooltip(tooltipString.toString());
    }
    
    public ContextMenu createContextMenuFor(FileObject fileObject) {
    	ContextMenu menu = new ContextMenu();
    	MenuItem item = new MenuItem("Copy");
        menu.getItems().add(item);
        item = new MenuItem("Move");
        menu.getItems().add(item);
        item = new MenuItem("Delete");
        item.setOnAction(event -> ((MainSceneController) fileController).delete(fileObject.getAbsolutePath()));
        menu.getItems().add(item);
        item = new MenuItem("Rename");
        menu.getItems().add(item);
    	item = new MenuItem("Create folder");
        item.setOnAction(event -> ((MainSceneController) fileController).showPathDialog());
        menu.getItems().add(item);
    	item = new MenuItem("Refresh");
        item.setOnAction(event -> ((MainSceneController) fileController).refresh());
        menu.getItems().add(item);
        Menu mitem = new Menu("Sorted by");
        menu.getItems().add(mitem);
        RadioMenuItem ritem = new RadioMenuItem("Name");
        ritem.setSelected(((MainSceneController) fileController).files.getSortOrder().contains(((MainSceneController) fileController).files.getColumns().get(0)));
        ritem.setOnAction(event -> ((MainSceneController) fileController).sortedByName());
        mitem.getItems().add(ritem);
        ritem = new RadioMenuItem("Creation date");
        ritem.setSelected(((MainSceneController) fileController).files.getSortOrder().contains(((MainSceneController) fileController).files.getColumns().get(1)));
        ritem.setOnAction(event -> ((MainSceneController) fileController).sortedByCreationDate());
        mitem.getItems().add(ritem);
        ritem = new RadioMenuItem("Type");
        ritem.setSelected(((MainSceneController) fileController).files.getSortOrder().contains(((MainSceneController) fileController).files.getColumns().get(2)));
        ritem.setOnAction(event -> ((MainSceneController) fileController).sortedByType());
        mitem.getItems().add(ritem);
        ritem = new RadioMenuItem("Size");
        ritem.setSelected(((MainSceneController) fileController).files.getSortOrder().contains(((MainSceneController) fileController).files.getColumns().get(3)));
        ritem.setOnAction(event -> ((MainSceneController) fileController).sortedBySize());
        mitem.getItems().add(ritem);
        mitem.getItems().add(new SeparatorMenuItem());
        ritem = new RadioMenuItem("Ascending");
        ritem.setSelected(((MainSceneController) fileController).files.getSortOrder().get(0).getSortType() == SortType.ASCENDING);
        ritem.setOnAction(event -> ((MainSceneController) fileController).ascSorted());
        mitem.getItems().add(ritem);
        ritem = new RadioMenuItem("Descending");
        ritem.setSelected(((MainSceneController) fileController).files.getSortOrder().get(0).getSortType() == SortType.DESCENDING);
        ritem.setOnAction(event -> ((MainSceneController) fileController).descSorted());
        mitem.getItems().add(ritem);
        if(fileObject.getType() == FileObjectType.DIRECTORY) {
            item = new MenuItem(resourceBundle.getString("open_in_new_window"));
            item.setOnAction(event -> openInNewWindow(fileObject));
            menu.getItems().add(item);
        }
        item = new MenuItem(resourceBundle.getString("download"));
	    item.setOnAction(event -> download(fileObject));
        menu.getItems().add(item);
        
        ((MainSceneController) fileController).files.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<FileObject>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends FileObject> change) {
				// TODO Auto-generated method stub
				menu.getItems().remove(menu.getItems().size() - 1);
				ObservableList<? extends FileObject> selected = change.getList();
				if(selected.size() > 1 && selected.contains(fileObject)) {
					MenuItem item = new MenuItem();
				    item.setText(/*resourceBundle.getString("download")*/"Download selected files");
				    item.setOnAction(event -> downloadAll(selected));
			        menu.getItems().add(item);
				} else {
					MenuItem item = new MenuItem();
				    item.setText(resourceBundle.getString("download"));
				    item.setOnAction(event -> download(fileObject));
			        menu.getItems().add(item);
				}
			}
			
		});
        
    	return menu;
    }
    
    @SuppressWarnings("unused")
	private void delete(FileObject fileObject) {
        try {
            /*if(fileObject.getType() == FileObjectType.FILE) {*/
                ((MainSceneController) fileController).fileManager.delete(fileObject.getAbsolutePath());
            /*} else { // directory
                deleteDirectory(fileObject);
            }*/
        } catch(/*FileOperation*/Exception ex) {
            ((MainSceneController) fileController).LOGGER.error("Error on delete directory " + fileObject.getAbsolutePath(), ex);
        }
    }
    
    /*private void deleteDirectory(FileObject fileObject) {
        try {
            List<FileObject> childs = ((MainSceneController) fileController).fileManager.list(fileObject.getAbsolutePath());
            for(FileObject child : childs) {
                if(child.getType() == FileObjectType.FILE) {
                    ((MainSceneController) fileController).fileManager.delete(child.getAbsolutePath());
                } else {
                    deleteDirectory(child);
                }
            }
            //((MainSceneController) fileController).fileManager.delete(fileObject.getAbsolutePath());
        } catch(FileOperationException ex) {
            ((MainSceneController) fileController).LOGGER.error("Error on delete directory " + fileObject.getAbsolutePath(), ex);
        }
    }*/
    
    private void download(FileObject fileObject) {
    	/*Task<Void> task = new Task<Void>() {
    		@Override
    		public Void call() {*/
    			final java.io.File[] targetPath = new java.io.File[1];
		        /*Platform.runLater(() -> {*/
		        	DirectoryChooser chooser = new DirectoryChooser();
		        	chooser.setTitle(resourceBundle.getString("choose_directory"));
			        targetPath[0] = chooser.showDialog(((MainSceneController)fileController).getWindow());
		        /*});*/
		        while(targetPath[0] == null) {}
		        if(targetPath[0] != null) { // If path was choosed
		            if(fileObject.getType() == FileObjectType.FILE) {
		        		try { new java.io.File(targetPath[0].getAbsolutePath() + "/" + fileObject.getName()).createNewFile(); } catch(IOException e) { e.printStackTrace(); }
		        		fileController.download(fileObject.getAbsolutePath(), new java.io.File(targetPath[0].getAbsolutePath() + "/" + fileObject.getName()));
		            } else { // directory
		            	new java.io.File(targetPath[0].getAbsolutePath() + "/" + fileObject.getName()).mkdir();
		            	new FolderDownloadOperation(fileController, fileObject, targetPath[0]).common();
		            }
		        }
		        /*return null;
    		}
    	};
    	new Thread(task).start();*/
    }
    
    private void downloadAll(ObservableList<? extends FileObject> fileObjects) {
    	/*Task<Void> task = new Task<Void>() {
    		@Override
    		public Void call() {*/
    			DirectoryChooser chooser = new DirectoryChooser();
    	        chooser.setTitle(resourceBundle.getString("choose_directory"));
    	        java.io.File targetPath = chooser.showDialog(((MainSceneController)fileController).getWindow());
    	        if(targetPath != null) { // If path was choosed
    	        	for(FileObject fileObject : fileObjects) {
    	        		if(fileObject.getType() == FileObjectType.FILE) {
    	            		try { new java.io.File(targetPath.getAbsolutePath() + "/" + fileObject.getName()).createNewFile(); } catch(IOException e) { e.printStackTrace(); }
    	            		fileController.download(fileObject.getAbsolutePath(), new java.io.File(targetPath.getAbsolutePath() + "/" + fileObject.getName()));
    	                } else { // directory
    	                	new java.io.File(targetPath.getAbsolutePath() + "/" + fileObject.getName()).mkdir();
    	                	new FolderDownloadOperation(fileController, fileObject, targetPath).common();
    	                }
    	        	}
    	        }
    			/*return null;
    		}
    	};
    	new Thread(task).start();*/
    }
    
    private void openInNewWindow(FileObject fileObject) {
    	try {
	    	Stage stage = new Stage();
	    	stage.setTitle("Flashsafe");
	        stage.setMinWidth(975);
	        stage.setMinHeight(650);
	        stage.initStyle(StageStyle.TRANSPARENT);
	        stage.getIcons().add(new Image(getClass().getResource("/img/logo.png").toExternalForm()));
	        FXMLLoader fxmlLoader = new FXMLLoader();
	        fxmlLoader.setLocation(getClass().getResource("/oldfs.fxml"));
	        fxmlLoader.setResources(((MainSceneController) fileController).resourceBundle);
	        fxmlLoader.setController(new MainSceneController(((MainSceneController) fileController).resourceBundle, fileObject.getAbsolutePath(), stage));
	        Parent root = fxmlLoader.load();
	        Scene scene = new Scene(root, Color.TRANSPARENT);
	        stage.setScene(scene);
	        stage.show();
    	} catch(IOException e) {
    		((MainSceneController) fileController).LOGGER.error("Error on open directory " + fileObject.getAbsolutePath() + " in new window", e);
    	}
    }
}
