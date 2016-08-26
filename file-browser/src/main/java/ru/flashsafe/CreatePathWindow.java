package ru.flashsafe;

import com.trolltech.qt.QUiForm;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt.WindowType;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashsafe.core.file.exception.FileOperationException;


public class CreatePathWindow implements QUiForm<FramelessWindow>{
    public Logger LOGGER = LoggerFactory.getLogger(CreatePathWindow.class);
    
    private QWidget centralWidget;
    private QLabel titleLabel;
    private QLineEdit pathNameEdit;
    private QPushButton acceptButton;
    
    private FramelessWindow window;
    
    private FileController fileController;
    
    public CreatePathWindow(FileController fileController) {
        this.fileController = fileController;
    }

    @Override
    public void setupUi(FramelessWindow window) {
        this.window = window;
        window.setObjectName("createPathWindow");
        window.setFixedSize(250, 100);
        window.setWindowIcon(new QIcon(new QPixmap("classpath:img/logo.png")));
        window.setIconSize(new QSize(32, 32));
        window.setWindowIconText("Create folder");
        window.setWindowTitle("Create folder");
        //window.overrideWindowFlags(WindowType.Dialog, WindowType.FramelessWindowHint);
        
        String stylesheet = ResourcesUtil.loadQSS("flashsafe");
        window.setStyleSheet(stylesheet);
        
        QTimer timer = new QTimer(window);
        timer.setInterval(5000);
        timer.timeout.connect(this, "timer()");
        timer.start();
        
        centralWidget = new QWidget();
        centralWidget.setObjectName("createPathWidget");
        centralWidget.setMinimumSize(new QSize(250, 100));
        centralWidget.setMaximumSize(new QSize(250, 100));
        centralWidget.setBaseSize(250, 100);
        
        QVBoxLayout layout = new QVBoxLayout();
        
        titleLabel = new QLabel("Enter new folder name:");
        titleLabel.setObjectName("creatFolderTitle");
        
        layout.addWidget(titleLabel);
        
        pathNameEdit = new QLineEdit();
        pathNameEdit.setObjectName("pathNameEdit");
        
        layout.addWidget(pathNameEdit);
        
        acceptButton = new QPushButton("OK");
        acceptButton.setObjectName("acceptPathNameButton");
        acceptButton.clicked.connect(this, "onPathnameSubmit()");
        
        layout.addWidget(acceptButton);
        
        centralWidget.setLayout(layout);
        
        window.setCentralWidget(centralWidget);
    }
    
    protected void timer() {
        window.setStyleSheet(ResourcesUtil.loadQSS("flashsafe"));
    }
    
    public void onPathnameSubmit() {
	String folderName = pathNameEdit.text();
	window.hide();
	if (folderName.isEmpty()) {
            return;
	}
	try {
            ((UI) fileController).fileManager.createDirectory(((UI) fileController).getCurrentLocation(), folderName);
            ((UI) fileController).refresh();
	} catch (FileOperationException e) {
            LOGGER.warn("Error while creating a new folder", e);
	}
	pathNameEdit.setText("");
    }
}
