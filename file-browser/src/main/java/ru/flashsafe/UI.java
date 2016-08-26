package ru.flashsafe;

import java.util.List;

import com.trolltech.qt.QUiForm;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.WindowType;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QFileIconProvider.IconType;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.concurrent.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.FlashSafeSystem;
import ru.flashsafe.core.file.Directory;

import ru.flashsafe.core.file.FileManager;
import ru.flashsafe.core.file.FileObject;
import static ru.flashsafe.core.file.FileObjectType.DIRECTORY;
import ru.flashsafe.core.file.FileOperation;
import ru.flashsafe.core.file.event.FileObjectSecurityEvent;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult;
import ru.flashsafe.core.file.event.FileObjectSecurityEventResult.ResultType;
import ru.flashsafe.core.file.event.FileObjectSecurityHandler;
import ru.flashsafe.core.file.exception.FileOperationException;
import ru.flashsafe.core.old.storage.FlashSafeStorageFileObject;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.util.HistoryObject;

/**
 * @author Alexander Krysin
 *
 */
public class UI implements QUiForm<FramelessWindow>, FileController, FileObjectSecurityHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UI.class);
    
    private QWidget centralwidget;
    private QFrame leftFrame;
    private QWidget verticalLayoutWidget;
    private QVBoxLayout verticalLayout;
    private QHBoxLayout horizontalLayout;
    private QLabel logo;
    private QPushButton settingsButton;
    private QTreeWidget tree;
    private QCommandLinkButton addFolderButton, trashButton;
    private QPushButton closeButton, minimizeButton, maximizeButton;
    private QPushButton backwardButton, forwardButton;
    private QPushButton tableButton, gridButton;
    private QPushButton filterButton;
    private QLineEdit searchField;
    private QLabel searchIcon;
    private QDragAndDropTableWidget table;
    private QProgressBar contentProgress;
    
    private final FlashSafeSystem flashSafeSystem;
    public final FileManager fileManager;
    private static final ObservableList<FileObject> currentFolderEntries = FXCollections.observableArrayList();
    private static final HistoryObject<String> historyObject = new HistoryObject<>();
    public String currentFolder = "";
    
    private FramelessWindow window;
    private static final FramelessWindow loadsWindow = new FramelessWindow(new QSize(350, 250));
    private static final FramelessWindow createPathWindow = new FramelessWindow(new QSize(250, 100));
    private static final FramelessWindow settingsWindow = new FramelessWindow(new QSize(400, 300));
    
    private static final LoadsWindow loadsUI = new LoadsWindow();
    private final CreatePathWindow createPathUI = new CreatePathWindow(this);
    private static final SettingsWindow settingsUI = new SettingsWindow();
    
    private static final HashMap<String, QTreeWidgetItem> FOLDERS_TREE = new HashMap<>();
    
    private static final QSystemTrayIcon TRAY_ICON = new QSystemTrayIcon();

    public UI() {
        super();
        flashSafeSystem = FlashSafeApplication.flashSafeSystem();
        fileManager = flashSafeSystem.fileManager();
        flashSafeSystem.fileManagementService().registerFileObjectSecurityHandler(this);
        loadsUI.setupUi(loadsWindow);
        createPathUI.setupUi(createPathWindow);
        settingsUI.setupUi(settingsWindow);
    }
    
    public void setupUi(FramelessWindow window) {
        this.window = window;
        window.setObjectName("window");
        
        String stylesheet = ResourcesUtil.loadQSS("flashsafe");
        window.setStyleSheet(stylesheet);
        
        TRAY_ICON.setIcon(new QIcon("classpath:img/logo.png"));
        TRAY_ICON.show();
        
        QTimer timer = new QTimer(window);
        timer.setInterval(5000);
        timer.timeout.connect(this, "timer()");
        timer.start();
        
        window.setWindowIcon(new QIcon("classpath:img/logo.png"));
        window.setIconSize(new QSize(32, 32));
        
        window.setWindowFlags(WindowType.FramelessWindowHint);
        
        centralwidget = new QWidget(window);
        centralwidget.setObjectName("centralwidget");
        
        leftFrame = new QFrame(centralwidget);
        leftFrame.setObjectName("leftFrame");
        leftFrame.setGeometry(new QRect(0, 0, 200, 750));
        leftFrame.setMinimumSize(new QSize(200, 750));
        leftFrame.setBaseSize(new QSize(200, 750));
        
        verticalLayoutWidget = new QWidget(leftFrame);
        verticalLayoutWidget.setObjectName("verticalLayoutWidget");
        verticalLayoutWidget.setGeometry(new QRect(0, 0, 200, 750));
        
        verticalLayout = new QVBoxLayout(verticalLayoutWidget);
        verticalLayout.setObjectName("verticalLayout");
        
        horizontalLayout = new QHBoxLayout();
        horizontalLayout.setSpacing(6);
        horizontalLayout.setMargin(10);
        horizontalLayout.setObjectName("horizontalLayout");
        
        logo = new QLabel(verticalLayoutWidget);
        logo.setObjectName("logo");
        logo.setMinimumSize(new QSize(140, 40));
        logo.setMaximumSize(new QSize(16777215, 40));
        logo.setBaseSize(new QSize(140, 40));
        
        QFont font = new QFont();
        font.setPointSize(11);
        
        logo.setFont(font);

        horizontalLayout.addWidget(logo);

        settingsButton = new QPushButton(verticalLayoutWidget);
        settingsButton.setObjectName("settingsButton");
        settingsButton.setGeometry(100, 0, 32, 32);
        settingsButton.setMinimumSize(new QSize(32, 32));
        settingsButton.setMaximumSize(new QSize(32, 32));
        settingsButton.setBaseSize(new QSize(32, 32));
        settingsButton.setIcon(new QIcon("classpath:img/sttngs.png"));
        settingsButton.setIconSize(new QSize(32, 32));
        settingsButton.clicked.connect(this, "showSettingsWindow()");

        horizontalLayout.addWidget(settingsButton);

        verticalLayout.addLayout(horizontalLayout);

        tree = new QTreeWidget(verticalLayoutWidget);
        tree.setObjectName("tree");
        tree.setMinimumWidth(200);
        tree.header().hide();
        tree.setColumnCount(1);
        
        verticalLayout.addWidget(tree);

        addFolderButton = new QCommandLinkButton(verticalLayoutWidget);
        addFolderButton.setObjectName("addFolderButton");
        addFolderButton.setMinimumSize(new QSize(0, 40));
        addFolderButton.setMaximumSize(new QSize(16777215, 40));
        addFolderButton.setBaseSize(new QSize(180, 40));
        
        QFont font1 = new QFont();
        font1.setFamily("Segoe UI");
        font1.setPointSize(14);
        
        addFolderButton.setFont(font1);
        addFolderButton.setIcon(new QIcon("classpath:img/add_folder.png"));
        addFolderButton.setIconSize(new QSize(24, 24));
        addFolderButton.clicked.connect(this, "showCreatePathWindow()");
        

        verticalLayout.addWidget(addFolderButton);

        trashButton = new QCommandLinkButton(verticalLayoutWidget);
        trashButton.setObjectName("trashButton");
        trashButton.setEnabled(true);
        trashButton.setMinimumSize(new QSize(180, 50));
        trashButton.setMaximumSize(new QSize(16777215, 50));
        trashButton.setBaseSize(new QSize(180, 50));
        
        QFont font2 = new QFont();
        font2.setFamily("Segoe UI");
        font2.setPointSize(14);
        
        trashButton.setFont(font2);
        trashButton.setIcon(new QIcon("classpath:img/trash.png"));
        trashButton.setIconSize(new QSize(24, 24));
        
        verticalLayout.addWidget(trashButton);
        
        closeButton = new QPushButton(centralwidget);
        closeButton.setObjectName("closeButton");
        closeButton.setGeometry(new QRect(976, 10, 14, 14));
        closeButton.setMinimumSize(new QSize(14, 14));
        closeButton.setMaximumSize(new QSize(14, 14));
        closeButton.setBaseSize(new QSize(14, 14));
        closeButton.setIcon(new QIcon("classpath:img/close_no_shape.png"));
        closeButton.setIconSize(new QSize(14, 14));
        closeButton.clicked.connect(this, "exit()");
        
        minimizeButton = new QPushButton(centralwidget);
        minimizeButton.setObjectName("minimizeButton");
        minimizeButton.setGeometry(new QRect(952, 10, 14, 14));
        minimizeButton.setMinimumSize(new QSize(14, 14));
        minimizeButton.setMaximumSize(new QSize(14, 14));
        minimizeButton.setBaseSize(new QSize(14, 14));
        minimizeButton.setIcon(new QIcon("classpath:img/minimize_no_shape.png"));
        minimizeButton.setIconSize(new QSize(14, 14));
        minimizeButton.clicked.connect(this, "minimize()");
        
        maximizeButton = new QPushButton(centralwidget);
        maximizeButton.setObjectName("maximizeButton");
        maximizeButton.setGeometry(new QRect(928, 10, 14, 14));
        maximizeButton.setMinimumSize(new QSize(14, 14));
        maximizeButton.setMaximumSize(new QSize(14, 14));
        maximizeButton.setBaseSize(new QSize(14, 14));
        maximizeButton.setIcon(new QIcon("classpath:img/maximize_no_shape.png"));
        maximizeButton.setIconSize(new QSize(14, 14));
        maximizeButton.clicked.connect(this, "maximize()");
        
        backwardButton = new QPushButton(centralwidget);
        backwardButton.setObjectName("backwardButton");
        backwardButton.setGeometry(new QRect(210, 20, 25, 25));
        backwardButton.setMinimumSize(new QSize(25, 25));
        backwardButton.setMaximumSize(new QSize(25, 25));
        backwardButton.setBaseSize(new QSize(25, 25));
        backwardButton.setIcon(new QIcon("classpath:img/backward_disabled.png"));
        backwardButton.setIconSize(new QSize(25, 25));
        backwardButton.clicked.connect(this, "navigateBackward()");
        
        forwardButton = new QPushButton(centralwidget);
        forwardButton.setObjectName("forwardButton");
        forwardButton.setGeometry(new QRect(245, 20, 25, 25));
        forwardButton.setMinimumSize(new QSize(25, 25));
        forwardButton.setMaximumSize(new QSize(25, 25));
        forwardButton.setBaseSize(new QSize(25, 25));
        forwardButton.setIcon(new QIcon("classpath:img/forward_disabled.png"));
        forwardButton.setIconSize(new QSize(25, 25));
        forwardButton.clicked.connect(this, "navigateForward()");
        
        tableButton = new QPushButton(centralwidget);
        tableButton.setObjectName("tableButton");
        tableButton.setGeometry(new QRect(290, 20, 25, 25));
        tableButton.setMinimumSize(new QSize(25, 25));
        tableButton.setMaximumSize(new QSize(25, 25));
        tableButton.setBaseSize(new QSize(25, 25));
        tableButton.setIcon(new QIcon("classpath:img/list_active.png"));
        tableButton.setIconSize(new QSize(25, 25));
        
        gridButton = new QPushButton(centralwidget);
        gridButton.setObjectName("gridButton");
        gridButton.setGeometry(new QRect(325, 20, 25, 25));
        gridButton.setMinimumSize(new QSize(25, 25));
        gridButton.setMaximumSize(new QSize(25, 25));
        gridButton.setBaseSize(new QSize(25, 25));
        gridButton.setIcon(new QIcon("classpath:img/table_inactive.png"));
        gridButton.setIconSize(new QSize(25, 25));
        
        filterButton = new QPushButton(centralwidget);
        filterButton.setObjectName("filterButton");
        filterButton.setGeometry(new QRect(370, 20, 25, 25));
        filterButton.setMinimumSize(new QSize(25, 25));
        filterButton.setMaximumSize(new QSize(25, 25));
        filterButton.setBaseSize(new QSize(25, 25));
        filterButton.setIcon(new QIcon("classpath:img/filter_inactive.png"));
        filterButton.setIconSize(new QSize(25, 25));
        
        searchField = new QLineEdit(centralwidget);
        searchField.setObjectName("searchField");
        searchField.setGeometry(new QRect(889, 88, 100, 22));
        
        QFont font3 = new QFont();
        font3.setPointSize(11);
        
        searchField.setFont(font3);
        searchField.setMaxLength(255);
        searchField.setFrame(true);
        
        searchIcon = new QLabel(centralwidget);
        searchIcon.setObjectName("searchIcon");
        searchIcon.setGeometry(new QRect(869, 90, 20, 20));
        searchIcon.setPixmap(new QPixmap("classpath:img/search.png").scaled(14, 14));
        
        table = new QDragAndDropTableWidget(centralwidget, this);
        table.setObjectName("table");
        table.setGeometry(new QRect(210, 120, 780, 620));
        table.setMinimumSize(new QSize(780, 620));
        table.verticalHeader().hide();
        table.setColumnCount(3);
        table.setHorizontalHeaderLabels(Arrays.asList("Name", "Type", "Size"));
        table.horizontalHeader().setMinimumWidth(780);
        table.horizontalHeader().setResizeMode(QHeaderView.ResizeMode.Interactive);
        table.setSortingEnabled(true);
        table.sortItems(0, Qt.SortOrder.AscendingOrder);
        table.horizontalHeader().setSortIndicatorShown(false);
        table.setDragDropOverwriteMode(false);
        table.setDropIndicatorShown(true);
        table.setAcceptDrops(true);
        table.setDragEnabled(true);
        table.setDragDropMode(QAbstractItemView.DragDropMode.DragDrop);
        table.setSelectionMode(QAbstractItemView.SelectionMode.MultiSelection);
        
        contentProgress = new QProgressBar(centralwidget);
        contentProgress.setObjectName("contentProgress");
        contentProgress.setGeometry(new QRect(210, 115, 780, 2));
        contentProgress.setMinimum(0);
        contentProgress.setMaximum(0);
        contentProgress.setValue(0);
        contentProgress.setTextVisible(false);
        
        window.setCentralWidget(centralwidget);
        
        retranslateUi(window);
        
        window.connectSlotsByName();
        
        loadTree();
        browseFolder(currentFolder);
    }

    void retranslateUi(FramelessWindow window) {
        window.setWindowTitle(com.trolltech.qt.core.QCoreApplication.translate("window", "Flashsafe", null));
        logo.setText(com.trolltech.qt.core.QCoreApplication.translate("window", "MY FLASHSAFE", null));
        settingsButton.setText("");
        addFolderButton.setText(com.trolltech.qt.core.QCoreApplication.translate("window", "Add new folder", null));
        trashButton.setText(com.trolltech.qt.core.QCoreApplication.translate("window", "Trash", null));
        closeButton.setText("");
        minimizeButton.setText("");
        maximizeButton.setText("");
        backwardButton.setText("");
        forwardButton.setText("");
        tableButton.setText("");
        gridButton.setText("");
        filterButton.setText("");
        searchField.setPlaceholderText(com.trolltech.qt.core.QCoreApplication.translate("window", "Search...", null));
        searchIcon.setText("");
    }
    
    protected void exit() {
        window.close();
        TRAY_ICON.dispose();
        FlashSafeApplication.stop();
        Main.es.shutdown();
        Main.app.dispose();
    }
    
    protected void minimize() {
        if(!window.isMinimized()) {
            window.showMinimized();
        } else {
            window.showNormal();
        }
    }
    
    protected void maximize() {
        if(!window.isMaximized()) {
            window.showMaximized();
        } else {
            window.showNormal();
        }
    }
    
    protected void timer() {
        window.setStyleSheet(ResourcesUtil.loadQSS("flashsafe"));
    }
    
    private void showCreatePathWindow() {
        if(createPathWindow.isHidden()) createPathWindow.show();
    }
    
    private void showSettingsWindow() {
        if(settingsWindow.isHidden()) settingsWindow.show();
    }
    
    @Override
    public void upload(File fileObject, String toPath) {}

    @Override
    public void download(String fromPath, File toFile) {}

    @Override
    public void loadContent(String path) {
        browseFolder(path);
    }

    @Override
    public void move(String fromPath, String toPath) {}

    @Override
    public void copy(String fromPath, String toPath) {}

    @Override
    public void rename(String fileObjectHash, String name) {}

    public void refresh() {
	listFolder(currentFolder);
    }
    
    @Override
    public void delete(String path) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    fileManager.delete(path);
                    QApplication.invokeLater(() -> {
                        refresh();
                    });
                } catch (FileOperationException e) {
                    LOGGER.warn("Error while delete object " + path, e);
                }
                return null;
            }
	};
	new Thread(task).start();
    }
    
    public synchronized void navigateBackward() {
        if (historyObject.hasPrevious()) {
            String previousLocation = historyObject.previous();
            if (listFolder(previousLocation)) {
                currentFolder = previousLocation;
                QApplication.invokeLater(() -> {
                    //breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/"));
                    backwardButton.setIcon(new QIcon("classpath:img/backward_disabled.png"));
                    forwardButton.setIcon(new QIcon("classpath:img/forward_enabled.png"));
                });
            }
        }
    }

    public synchronized void navigateForward() {
        if (historyObject.hasNext()) {
            String previousLocation = historyObject.next();
            if (listFolder(previousLocation)) {
                currentFolder = previousLocation;
                QApplication.invokeLater(() -> {
                    //breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/"));
                    forwardButton.setIcon(new QIcon("classpath:img/forward_disabled.png"));
                    backwardButton.setIcon(new QIcon("classpath:img/backward_enabled.png"));
                });
            }
        }
    }

    public void createDirectory(String folderName) {
	if (folderName.isEmpty()) {
            return;
	}
	try {
            fileManager.createDirectory(currentFolder, folderName);
            refresh();
	} catch (FileOperationException e) {
            LOGGER.warn("Error while creating a new folder", e);
	}
    }
    
    public synchronized void browseTrash() {
	//folders_count = 0;
	//Task<Void> task = new Task<Void>() {
            //@Override
            //protected Void call() throws Exception {
                //if (trashList()) {
                    //currentFolder = "fls://Trash";
                    //historyObject.addObject(currentFolder);
                    //back.setImage(new Image(getClass().getResourceAsStream("/img/backward_enabled.png")));
                    //Platform.runLater(() -> breadcrumbs.setText(currentFolder.replace("fls:/", "Flashsafe:/")));
                //}
                //return null;
            //}
	//};
	//new Thread(task).start();
    }
    
    @Override
    public String getCurrentLocation() {
        return currentFolder;
    }

    @Override
    public FileObjectSecurityEventResult handle(FileObjectSecurityEvent event) {
        return new FileObjectSecurityEventResult(ResultType.CONTINUE, "");
    }
    
    public final synchronized void browseFolder(String folderPath) {
        contentProgress.setMaximum(0);
        if (listFolder(folderPath)) {
            currentFolder = folderPath;
            historyObject.addObject(currentFolder);
        }
    }

    private boolean listFolder(String path) {
        try {
            List<FileObject> folderEntries = fileManager.list(path);
            currentFolderEntries.clear();
            currentFolderEntries.addAll(folderEntries);
            if(currentFolderEntries.size() == 0) {
                table.setRowCount(table.height() / table.horizontalHeader().height());
            }
            table.setEntries(currentFolderEntries);
            contentProgress.setMaximum(1);
            contentProgress.setValue(1);
            return true;
        } catch (FileOperationException e) {
            LOGGER.warn("Error while executing list", e);
            return false;
        }
    }
    
    public void loadTree() {
        try {
            List<FlashSafeStorageFileObject> dirs = fileManager.getTree();
            dirs.forEach(dir -> {
                if(dir.getParentHash() == null || dir.getParentHash().equals("")) {
                    QTreeWidgetItem item = new QTreeWidgetItem();
                    item.setIcon(0, new QIcon("classpath:img/folder_black1.png"));
                    item.setText(0, dir.getName());
                    tree.addTopLevelItem(item);
                    FOLDERS_TREE.put(dir.getHash(), item);
                }
            });
            dirs.forEach(dir -> {
                if(dir.getParentHash() != null && !dir.getParentHash().equals("")) {
                    QTreeWidgetItem item = new QTreeWidgetItem();
                    item.setIcon(0, new QIcon("classpath:img/folder_black1.png"));
                    item.setText(0, dir.getName());
                    FOLDERS_TREE.get(dir.getParentHash()).addChild(item);
                    FOLDERS_TREE.put(dir.getHash(), item);
                }
            });
        } catch (FileOperationException ex) {
            LOGGER.error("Error on load tree", ex);
        }
    }
    
    private long calcLength(File[] files) {
	long length = 0;
	for(File file : files) {
            if(file.isFile()) {
                length += file.length();
            } else {
                length += calcLength(file.listFiles());
            }
	}
	return length;
    }
    
    @Override
    public void upload(QFile fileObject, String toPath) {
        //Task<Void> task = new Task<Void>() {
            //@Override
            //protected Void call() throws Exception {
                try {
                    long length = fileObject.size();
                    FileOperation uploadOperation = length != 0 ? fileManager.copy(new QFileInfo(fileObject).absoluteFilePath(), toPath) : null;
                    //if(length == 0) fileManager.createFile(toPath + "/" + fileObject.fileName());
                    QWidget[] loadWidget = new QWidget[1];
                    QProgressBar[] bar = new QProgressBar[1];
                    QListWidgetItem[] items = new QListWidgetItem[1];
                    //QApplication.invokeLater(() -> {
                        loadWidget[0] = createLoadWidget(uploadOperation, "Uploading...");
                        bar[0] = (QProgressBar) loadWidget[0].children().get(4);
                        items[0] = loadsUI.addLoad(loadWidget[0]);
                        if(length == 0) bar[0].setValue(100);
                        if(loadsWindow.isHidden()) loadsWindow.show();
                    //});
                    while (uploadOperation.getState() != OperationState.FINISHED) {
                        /*QApplication.invokeLater(() -> */bar[0].setValue(uploadOperation.getProgress()/*)*/);
                        Thread.sleep(200);
                    }
                    //QApplication.invokeLater(() -> {
                        ((QLabel) loadWidget[0].children().get(3)).setText("Upload finished.");
                        //loadWidget[0].children().remove(5);
                        items[0].setSizeHint(new QSize(348, 36));
                        TRAY_ICON.showMessage("Upload finished", "File " + fileObject.fileName() + " was upload to Your Flashsafe.", QSystemTrayIcon.MessageIcon.Information, 5000);
                    //});
                    refresh();
                } catch (FileOperationException | InterruptedException e) {
                    LOGGER.warn("Error while uploading file " + new QFileInfo(fileObject).absoluteFilePath(), e);
                }
                //return null;
            //}
        //};
        //new Thread(task).start();
    }

    @Override
    public void download(String fromPath, QFile toFile) {
    	//Task<Void> task = new Task<Void>() {
            //@Override
            //protected Void call() throws Exception {
                try {
                    FileOperation downloadOperation = fileManager.copy(fromPath, new QFileInfo(toFile).absoluteFilePath());
                    QWidget[] loadWidget = new QWidget[1];
                    QProgressBar[] bar = new QProgressBar[1];
                    QListWidgetItem[] items = new QListWidgetItem[1];
                    //QApplication.invokeLater(() -> {
                        loadWidget[0] = createLoadWidget(downloadOperation, "Downloading...");
                        bar[0] = (QProgressBar) loadWidget[0].children().get(4);
                        items[0] = loadsUI.addLoad(loadWidget[0]);
                        if(loadsWindow.isHidden()) loadsWindow.show();
                    //});
                    while (downloadOperation.getState() != OperationState.FINISHED) {
                        /*QApplication.invokeLater(() -> */bar[0].setValue(downloadOperation.getProgress()/*)*/);
                        Thread.sleep(200);  
                    }
                    //QApplication.invokeLater(() -> {
                        ((QLabel) loadWidget[0].children().get(3)).setText("Download finished.");
                        //loadWidget[0].children().remove(5);
                        items[0].setSizeHint(new QSize(348, 36));
                        TRAY_ICON.showMessage("Download finished", "File " + toFile.fileName() + " was download to Your PC.", QSystemTrayIcon.MessageIcon.Information, 5000);
                    //});
                } catch (FileOperationException | InterruptedException e) {
                    LOGGER.warn("Error while downloading file " + new QFileInfo(toFile).absoluteFilePath(), e);
                }
                //return null;
            //}
        //};
        //new Thread(task).start();
    }
    
    private QWidget createLoadWidget(FileOperation operation, String process) {
        QVBoxLayout layout = new QVBoxLayout();
        layout.setContentsMargins(0, 0, 0, 0);
        QHBoxLayout hlayout = new QHBoxLayout();
        hlayout.setContentsMargins(0, 0, 0, 0);
        QLabel icon = new QLabel();
        icon.setObjectName("loadIcon");
        QFileInfo fileInfo = new QFileInfo(operation.getFileObjectName());
        QFileIconProvider iconProvider = new QFileIconProvider();
        QIcon licon = fileInfo.isDir() ? iconProvider.icon(IconType.Folder)
                : iconProvider.icon(fileInfo);
        icon.setPixmap(licon.pixmap(32, 32));
        icon.setMaximumSize(new QSize(32, 32));
        hlayout.addWidget(icon);
        QVBoxLayout vlayout = new QVBoxLayout();
        vlayout.setContentsMargins(0, 0, 0, 0);
        QFont font = new QFont("Tahoma", 12);
        font.setBold(true);
        QLabel name = new QLabel(/*operation.getFileObjectName()*/"somefile");
        name.setObjectName("loadName");
        name.setFont(font);
        vlayout.addWidget(name);
        QLabel info = new QLabel(process);
        info.setObjectName("loadInfo");
        font.setBold(false);
        info.setFont(font);
        vlayout.addWidget(info);
        hlayout.addLayout(vlayout);
        layout.addLayout(hlayout);
        QProgressBar progress = new QProgressBar();
        progress.setObjectName("loadProgress");
        progress.setTextVisible(false);
        progress.setMaximum(100);
        layout.addWidget(progress);
        QWidget loadWidget = new QWidget();
        loadWidget.setLayout(layout);
        return loadWidget;
    }
    
}
