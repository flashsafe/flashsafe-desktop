package ru.flashsafe.perspective;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import ru.flashsafe.FileController;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.util.FileObjectViewHelper;

public class ListPerspective implements Perspective {

    private final ListView<FileObject> listView;

    private final ObservableList<FileObject> dataModel;

    private final FileController fileController;
    
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
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
