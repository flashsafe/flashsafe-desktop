package ru.flashsafe.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import ru.flashsafe.FileController;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.util.FileObjectViewHelper;

public class TablePerspective implements Perspective {

    private final TableView<FileObject> tableView;
    
    private final ObservableList<FileObject> dataModel;
    
    private final FileController fileController;
    
    private final FileObjectViewHelper fileObjectViewHelper;
    
    public TablePerspective(TableView<FileObject> table, ObservableList<FileObject> dataModel,
            FileObjectViewHelper fileObjectViewHelper, FileController fileController) {
        this.tableView = table;
        this.dataModel = dataModel;
        this.fileObjectViewHelper = fileObjectViewHelper;
        this.fileController = fileController;
        initPerspective();
    }
    
    private void initPerspective() {
        // FIXME use column name not index
        TableColumn<FileObject, Label> nameColumn = (TableColumn<FileObject, Label>) tableView.getColumns().get(0);
        nameColumn.setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            return new ReadOnlyObjectWrapper<Label>(fileObjectViewHelper.createLabelFor(value));
        });

        TableColumn<FileObject, String> createDateColumn = (TableColumn<FileObject, String>) tableView.getColumns().get(1);
        createDateColumn.setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            long timeInMilliseconds = (value.getCreationTime() * 1000);
            //FIXME switch to Java8 API
            return new SimpleStringProperty(new Date(timeInMilliseconds).toLocaleString());
        });

        ((TableColumn<FileObject, String>) tableView.getColumns().get(2)).setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            return new SimpleStringProperty(fileObjectViewHelper.getTypeDescriptionFor(value));
        });

        TableColumn<FileObject, String> sizeColumn = (TableColumn<FileObject, String>) tableView.getColumns().get(3);
        sizeColumn.setCellValueFactory(cellData -> {
            FileObject value = cellData.getValue();
            return new SimpleStringProperty(fileObjectViewHelper.getSizeDescriptionFor(value));
        });
        
        tableView.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            int clickCount = event.getClickCount();
            if (clickCount == 2) {
                FileObject row = tableView.getSelectionModel().getSelectedItem();
                if (row != null && row.getType() == FileObjectType.DIRECTORY) {
                    fileController.loadContent(row.getAbsolutePath());
                }
            }
        });
        
        tableView.setOnDragDetected(event -> {
            Dragboard db = tableView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            List<File> list = new ArrayList<>();
            list.add(new File("./mime.mime"));
            content.putFiles(list);
            db.setContent(content);
            event.consume();

        });
        tableView.setOnDragOver(event -> {
            if (event.getGestureSource() != tableView && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        tableView.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                final File f = db.getFiles().get(0);
                fileController.upload(f);
            }
            event.setDropCompleted(true);
            event.consume();

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
