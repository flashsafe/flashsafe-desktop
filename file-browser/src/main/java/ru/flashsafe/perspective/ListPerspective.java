package ru.flashsafe.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import ru.flashsafe.FileController;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.util.FileObjectViewHelper;

public class ListPerspective implements Perspective {

    private final ListView<FileObject> listView;

    private final ObservableList<FileObject> dataModel;

    private final FileController fileController;
    
    private final FileObjectViewHelper fileObjectViewHelper;

    public ListPerspective(ListView<FileObject> listView, ObservableList<FileObject> dataModel, FileObjectViewHelper fileObjectViewHelper, FileController fileController) {
        this.listView = listView;
        this.dataModel = dataModel;
        this.fileObjectViewHelper = fileObjectViewHelper;
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
                FileObject row = listView.getSelectionModel().getSelectedItem();
                if (row.getType() == FileObjectType.DIRECTORY) {
                    fileController.loadContent(row.getAbsolutePath());
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
        listView.setCellFactory(new Callback<ListView<FileObject>, ListCell<FileObject>>() {
            @Override
            public ListCell<FileObject> call(ListView<FileObject> listView) {
                return new FileCell(fileObjectViewHelper);
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

    private static class FileCell extends ListCell<FileObject> {
        
        private final FileObjectViewHelper fileObjectViewHelper;
        
        public FileCell(FileObjectViewHelper fileObjectViewHelper) {
            this.fileObjectViewHelper = fileObjectViewHelper;
        }
        
        @Override
        public void updateItem(FileObject item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (item != null) {
                setGraphic(fileObjectViewHelper.createLabelFor(item));
            }
        }
    }

}
