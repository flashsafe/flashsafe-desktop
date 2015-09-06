package ru.flashsafe.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import ru.flashsafe.FileController;
import ru.flashsafe.controller.MainSceneController.FileCell;
import ru.flashsafe.controller.MainSceneController.TableRow;

public class ListPerspective implements Perspective {

    private final ListView<TableRow> listView;
    
    private final ObservableList<TableRow> dataModel;
    
    private final FileController fileController;
    
    public ListPerspective(ListView<TableRow> listView, ObservableList<TableRow> dataModel, FileController fileController) {
        this.listView = listView;
        this.dataModel = dataModel;
        this.fileController = fileController;
        initPerspective();
    }
    
    private void initPerspective() {
        listView.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            int clickCount = event.getClickCount();
            if (clickCount == 2) {
                Object targer = event.getTarget();
                if (targer.getClass() == Label.class) {
                    Label fileObjectLabel = (Label) targer;
                    System.out.println(fileObjectLabel.getId());
                    fileController.loadContent(Integer.valueOf(fileObjectLabel.getId()));
                }
            }
        });
        
        listView.setOnDragDetected(event -> {
            Dragboard db = listView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            List<File> list = new ArrayList<>();
            list.add(new File("./mime.mime"));
            content.putFiles(list);
            db.setContent(content);
            event.consume();

        });
        listView.setOnDragOver(event -> {
            if (event.getGestureSource() != listView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        listView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                final File f = db.getFiles().get(0);
                fileController.upload(f);
            }
            event.setDropCompleted(true);
            event.consume();

        });
        
        listView.setCellFactory(new Callback<ListView<TableRow>, ListCell<TableRow>>() {

            @Override
            public ListCell<TableRow> call(ListView<TableRow> listView) {
                return new FileCell();
            }
        });
    }
    
    @Override
    public void switchOn() {
        listView.setItems(dataModel);
        listView.setVisible(true);
    }

    @Override
    public void switchOff() {
        listView.setVisible(false);
        listView.setItems(null);
    }

    @Override
    public PerspectiveType getType() {
        return PerspectiveType.LIST;
    }

}
