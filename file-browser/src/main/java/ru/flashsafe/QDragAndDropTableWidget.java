package ru.flashsafe;

import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.DropAction;
import static com.trolltech.qt.core.Qt.ItemDataRole.DisplayRole;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QDragEnterEvent;
import com.trolltech.qt.gui.QDragMoveEvent;
import com.trolltech.qt.gui.QDropEvent;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QFileDialog.Option;
import com.trolltech.qt.gui.QHeaderView;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QWidget;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import javafx.collections.ObservableList;

import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;
import static ru.flashsafe.core.file.FileObjectType.FILE;

/**
 * @author Alexander Krysin
 *
 */
public class QDragAndDropTableWidget extends QTableWidget {
    private ObservableList<FileObject> currentFolderEntries;
    private final FileController fileController;
    private String upload = "";
    private String download = "";
    private String downloadName = "";
	
    public QDragAndDropTableWidget(QWidget widget, FileController fileController) {
        super(widget);
        this.fileController = fileController;
        setDefaultDropAction(DropAction.CopyAction);
    }
    
    public void setEntries(ObservableList<FileObject> currentFolderEntries) {
        this.currentFolderEntries = currentFolderEntries;
        setRowCount(currentFolderEntries.size());
        for(int i=0;i<currentFolderEntries.size();i++) {
            FileObject fileObject = currentFolderEntries.get(i);
            FileObjectType type = fileObject.getType();
            String iconUri = type == FILE ? 
                    IconUtil.getFileIconUri(fileObject.getName()) : 
                    IconUtil.getFolderIconUri((Directory) fileObject);
            QLabel label = new QLabel();
            label.setAcceptDrops(true);
            label.setTextFormat(Qt.TextFormat.RichText);
            label.setText("<img src=\"" + iconUri + "\" width=\"16\" height=\"16\" /> " + fileObject.getName());
            setCellWidget(i, 0, label);
            label = new QLabel();
            label.setAcceptDrops(true);
            label.setText(type == FILE ? "File" : "Folder");
            setCellWidget(i, 1, label);
            label = new QLabel();
            label.setAcceptDrops(true);
            String size = "";
            try {size = Long.toString(fileObject.getSize() / 1024) + "KB"; } catch(IOException e) { e.printStackTrace(); }
            label.setText(size);
            setCellWidget(i, 2, label);
            label = new QLabel();
            label.setAcceptDrops(true);
            label.setText(fileObject.getMimeType());
            setCellWidget(i, 3, label);
        }
        horizontalHeader().setResizeMode(QHeaderView.ResizeMode.Stretch);
        horizontalHeader().setMovable(true);
        horizontalHeader().resizeSections(QHeaderView.ResizeMode.Interactive);
        resizeColumnsToContents();
    }
    
    @Override
    protected void contextMenuEvent(QContextMenuEvent event) {
        QMenu menu = new QMenu(this);
        QModelIndex index = indexAt(event.pos());
        if(index != null) {
        FileObject fileObject = currentFolderEntries.get(index.row());
        download = fileObject.getHash();
        downloadName = fileObject.getName();
            QAction download_action = menu.addAction("Download", this, "download()");
            QAction delete_action = menu.addAction("Delete", this, "delete()");
        }
        QAction refresh_action = menu.addAction("Refresh", this, "refresh()");
        menu.popup(event.globalPos());
    }
    
    @Override
    protected void dragEnterEvent(QDragEnterEvent event) {
        event.setDropAction(DropAction.CopyAction);
        event.acceptProposedAction();
    }

    @Override
    protected void dragMoveEvent(QDragMoveEvent event) {
        event.acceptProposedAction();
    }
    
    @Override
    protected void dropEvent(QDropEvent event) {
        if(event.mimeData().hasUrls()) {
            for(int i=0;i<event.mimeData().urls().size();i++) {
                fileController.upload(new QFile(event.mimeData().urls().get(i).toLocalFile()), ((UI) fileController).getCurrentLocation());
            }
        }
        event.accept();
    }
    
    @Override
    protected void mousePressEvent(QMouseEvent event) {}
    
    @Override
    protected void mouseDoubleClickEvent(QMouseEvent event) {
        QModelIndex index = indexAt(event.pos());
        if(index != null) {
            FileObject fileObject = currentFolderEntries.get(index.row());
            if(fileObject.getType() == FileObjectType.DIRECTORY) {
                ((UI) fileController).browseFolder(fileObject.getHash());
            }
        }
    }
    
    private void download() {
        String dir = QFileDialog.getExistingDirectory(this, "Choose directory for download", "", Option.ShowDirsOnly);
        if(null != dir && !dir.equals("")) {
            ((UI) fileController).download(download, new QFile(dir + "/" + downloadName));
        }
    }
    
    private void delete() {
        ((UI) fileController).delete(upload);
    }
    
    private void refresh() {
        ((UI) fileController).refresh();
    }
    
}
