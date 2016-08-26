package ru.flashsafe;

import com.trolltech.qt.QUiForm;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.AlignmentFlag;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QVBoxLayout;
import com.trolltech.qt.gui.QWidget;

public class SettingsWindow implements QUiForm<FramelessWindow> {
    private QWidget centralWidget;
    private QLabel mainSettingsLabel;
    private QLabel aboutSettingsLabel;
    private QLabel aboutLabel;
    
    private FramelessWindow window;
    
    public SettingsWindow() {}

    @Override
    public void setupUi(FramelessWindow window) {
        this.window = window;
        window.setObjectName("settingsWindow");
        window.setFixedSize(400, 300);
        window.setWindowIcon(new QIcon(new QPixmap("classpath:img/logo.png")));
        window.setIconSize(new QSize(32, 32));
        window.setWindowIconText("Settings");
        window.setWindowTitle("Settings");
        //window.overrideWindowFlags(Qt.WindowType.Dialog, Qt.WindowType.FramelessWindowHint);
        window.setContentsMargins(0, 0, 0, 0);
        
        String stylesheet = ResourcesUtil.loadQSS("flashsafe");
        window.setStyleSheet(stylesheet);
        
        QTimer timer = new QTimer(window);
        timer.setInterval(5000);
        timer.timeout.connect(this, "timer()");
        timer.start();
        
        centralWidget = new QWidget();
        centralWidget.setObjectName("settingsWidget");
        
        QHBoxLayout hlayout = new QHBoxLayout();
        hlayout.setObjectName("settingsHLayout");
        hlayout.setContentsMargins(0, 0, 0, 0);
        QVBoxLayout vlayout = new QVBoxLayout();
        vlayout.setObjectName("settingsVLayout");
        vlayout.setContentsMargins(0, 0, 0, 0);
        
        mainSettingsLabel = new QLabel("Main");
        mainSettingsLabel.setObjectName("mainSettinsLabel");
        mainSettingsLabel.setAlignment(AlignmentFlag.AlignHCenter, AlignmentFlag.AlignTop);
        vlayout.addWidget(mainSettingsLabel);
        
        aboutSettingsLabel = new QLabel("About");
        aboutSettingsLabel.setObjectName("aboutSettinsLabel");
        aboutSettingsLabel.setAlignment(AlignmentFlag.AlignHCenter, AlignmentFlag.AlignTop);
        vlayout.addWidget(aboutSettingsLabel);
        
        hlayout.addLayout(vlayout);
        
        aboutLabel = new QLabel("Flashsafe - secure and infinite cloud-storage.");
        aboutLabel.setObjectName("aboutLabel");
        aboutLabel.setAlignment(AlignmentFlag.AlignHCenter, AlignmentFlag.AlignTop);
        hlayout.addWidget(aboutLabel);
        
        centralWidget.setLayout(hlayout);
        
        window.setCentralWidget(centralWidget);
    }
    
    protected void timer() {
        window.setStyleSheet(ResourcesUtil.loadQSS("flashsafe"));
    }
    
}
