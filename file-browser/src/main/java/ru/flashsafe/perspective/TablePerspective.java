package ru.flashsafe.perspective;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import ru.flashsafe.FileController;
import ru.flashsafe.controller.MainSceneController.TableRow;

public class TablePerspective<T> implements Perspective {

    private final TableView<T> tableView;
    
    private final ObservableList<T> dataModel;
    
    private final FileController fileController;
    
    public TablePerspective(TableView<T> table, ObservableList<T> dataModel, FileController fileController) {
        this.tableView = table;
        this.dataModel = dataModel;
        this.fileController = fileController;
        initPerspective();
    }
    
    private void initPerspective() {
        // FIXME use column name not index
        TableColumn<TableRow, Label> nameColumn = (TableColumn<TableRow, Label>) tableView.getColumns().get(0);
        nameColumn.setCellValueFactory(new PropertyValueFactory<TableRow, Label>("name"));

        TableColumn<TableRow, String> createDateColumn = (TableColumn<TableRow, String>) tableView.getColumns().get(1);
        createDateColumn.setCellValueFactory(new PropertyValueFactory<TableRow, String>("createDate"));

        ((TableColumn) tableView.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<TableRow, String>("type"));

        TableColumn<TableRow, String> sizeColumn = (TableColumn<TableRow, String>) tableView.getColumns().get(3);
        sizeColumn.setCellValueFactory(new PropertyValueFactory<TableRow, String>("size"));
        
        tableView.setOnMouseClicked(event -> {
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
        SortedList<T> sortedFiles = new SortedList<>(dataModel);
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
