package ru.flashsafe;

import com.trolltech.qt.core.*;
import com.trolltech.qt.core.Qt.WindowType;
import com.trolltech.qt.gui.*;
import java.io.IOException;
import java.net.InetAddress;
import javafx.concurrent.Task;
import org.pkcs11.jacknji11.CE;
import org.pkcs11.jacknji11.CKR;
import org.pkcs11.jacknji11.CKRException;
import ru.flashsafe.core.FlashSafeApplication;
import ru.flashsafe.core.FlashSafeConfiguration;
import ru.flashsafe.util.ApplicationProperties;

public class SplashUI implements com.trolltech.qt.QUiForm<QDialog>
{
    public QLabel label;
    public QProgressBar progressBar;
    public QLabel label_2;
    
    private QDialog self;
    
    private static long session;
    private static String PIN = "12345678";
    private boolean tokenInit = false;
    
    private UI ui;
    private FramelessWindow window;

    public SplashUI() { super(); }

    public void setupUi(QDialog Splash) {
        self = Splash;
        Splash.setObjectName("Splash");
        Splash.setWindowIcon(new QIcon("classpath:img/logo.png"));
        Splash.resize(new QSize(400, 300).expandedTo(Splash.minimumSizeHint()));
        Splash.setStyleSheet("background: #2E3335;");
        Splash.setWindowFlags(WindowType.FramelessWindowHint);
        label = new QLabel(Splash);
        label.setObjectName("label");
        label.setGeometry(new QRect(5, 0, 390, 260));
        label.setPixmap(new QPixmap("classpath:img/logos.png").scaled(200, 100));
        label.setAlignment(com.trolltech.qt.core.Qt.AlignmentFlag.createQFlags(com.trolltech.qt.core.Qt.AlignmentFlag.AlignHCenter,com.trolltech.qt.core.Qt.AlignmentFlag.AlignVCenter));
        progressBar = new QProgressBar(Splash);
        progressBar.setObjectName("progressBar");
        progressBar.setGeometry(new QRect(10, 280, 380, 10));
        progressBar.setValue(0);
        progressBar.setTextVisible(false);
        progressBar.setMaximum(100);
        progressBar.setStyleSheet("QProgressBar#progressBar::chunk {background: qlineargradient(x1:0,y1:0.5,x2:1,y2:0.5,stop: 0.0 rgb(91,212,248),stop: 0.5 rgb(41,168,198),stop: 1.0 rgb(107, 227, 238));}");
        label_2 = new QLabel(Splash);
        label_2.setObjectName("label_2");
        label_2.setGeometry(new QRect(10, 260, 380, 16));
        label_2.setStyleSheet("color: #EEEEEE;");
        label_2.setText("Starting...");
        retranslateUi(Splash);

        Splash.connectSlotsByName();
    }

    void retranslateUi(QDialog Splash){
        Splash.setWindowTitle(com.trolltech.qt.core.QCoreApplication.translate("Splash", "Flashsafe", null));
        label.setText("");
    }
    
    private static FlashSafeConfiguration createConfiguration() {
        return FlashSafeConfiguration.builder().registerUserId(ApplicationProperties.userId())
                .registerSecret(ApplicationProperties.secret()).build();
    }
    
    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    QApplication.invokeLater(() -> label_2.setText("Initialize hardware..."));
                    TokenUtil.PKCS11Initialize();
                    long[] slotList = TokenUtil.getSlotList();
                    if(slotList.length == 0) {
                        QApplication.invokeLater(() -> label_2.setStyleSheet("color: red;"));
                        QApplication.invokeLater(() -> label_2.setText("Device not find! Exit in 5s..."));
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> label_2.setText("Device not find! Exit in 4s..."));
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> label_2.setText("Device not find! Exit in 3s..."));
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> label_2.setText("Device not find! Exit in 2s..."));
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> label_2.setText("Device not find! Exit in 1s..."));
                        Thread.sleep(1000);
                        TokenUtil.PKCS11Finalize();
                        QApplication.invokeLater(() -> self.close());
                    }
                    TokenUtil.closeAllSessions(slotList[0]);
                    Thread.sleep(1000);
                    QApplication.invokeLater(() -> progressBar.setValue(20));
                    QApplication.invokeLater(() -> label_2.setText("Connect to hardware..."));
                    session = TokenUtil.openSession(slotList[0]);
                    TokenUtil.login(session, PIN);
                    tokenInit = true;
                    Thread.sleep(1000);
                    QApplication.invokeLater(() -> progressBar.setValue(40));
                    QApplication.invokeLater(() -> label_2.setText("Checking Internet connection..."));
                    // Step 1, check internet connection
                    InetAddress add = InetAddress.getByName("api.flash.so");
                    if(add.isReachable(3000)) {
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> progressBar.setValue(60));
                        QApplication.invokeLater(() -> label_2.setText("Loading UI..."));
                        // Step 2, load UI
                        FlashSafeConfiguration configuration = createConfiguration();
                        FlashSafeApplication.setConfiguration(configuration);
                        FlashSafeApplication.run();
                        QApplication.invokeLater(() -> {
                            ui = new UI();
                            window = new FramelessWindow(new QSize(1000, 750));
                            ui.setupUi(window);
                            QApplication.instance().installEventFilter(window);
                        });
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> progressBar.setValue(80));
                        QApplication.invokeLater(() -> label_2.setText("Connecting..."));
                        // Step 3, load files
                        //QApplication.invokeLater(() -> ui.browseFolder(ui.currentFolder));
                        //QApplication.invokeLater(() -> ui.loadTree());
                        Thread.sleep(1000);
                        QApplication.invokeLater(() -> progressBar.setValue(100));
                        QApplication.invokeLater(() -> label_2.setText("Done!"));
                        // Step 4, show window
                        QApplication.invokeLater(() -> window.show());
                        QApplication.invokeLater(() -> self.dispose());
                    } else {
                        QApplication.invokeLater(() -> label_2.setStyleSheet("color: red;"));
                        QApplication.invokeLater(() -> label_2.setText("Error! Check Your Internet connection."));
                    }

                } catch(InterruptedException e) {
                    QApplication.invokeLater(() -> label_2.setStyleSheet("color: red;"));
                    QApplication.invokeLater(() -> label_2.setText("Unknown error!"));
                } catch(CKRException e) {
                    QApplication.invokeLater(() -> label_2.setStyleSheet("color: red;"));
                    switch((int) e.getCKR()) {
                        case PKCS11Constants.DEVICE_REMOVED:
                            QApplication.invokeLater(() -> label_2.setText("Error! Device was removed."));
                            break;
                        case PKCS11Constants.FUNCTION_FAILED:
                            QApplication.invokeLater(() -> label_2.setText("Error! Hardware authentication failed."));
                            break;
                        case PKCS11Constants.PIN_INCORRECT:
                            QApplication.invokeLater(() -> label_2.setText("Error! Incorrect PIN!"));
                            break;
                        case PKCS11Constants.TOKEN_NOT_PRESENT:
                            QApplication.invokeLater(() -> label_2.setText("Error! Check Device not present."));
                            break;
                        default:
                            QApplication.invokeLater(() -> label_2.setText("Error! " + e.getLocalizedMessage()));
                            break;
                    }
                } catch(IOException e) {
                    QApplication.invokeLater(() -> label_2.setStyleSheet("color: red;"));
                    QApplication.invokeLater(() -> label_2.setText("Error! Check Your Internet connection."));
                }
            }
        }).start();
    }

}
