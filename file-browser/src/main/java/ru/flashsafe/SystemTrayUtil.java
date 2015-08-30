package ru.flashsafe;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemTrayUtil {

    private static final Logger logger = LogManager.getLogger(SystemTrayUtil.class);

    private static SystemTray tray;

    private static TrayIcon trayIcon;

    static {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
        }
    }

    private SystemTrayUtil() {
    }

    public static void addToSystemTray(String caption, String text) {
        if (tray != null) {
            try {
                trayIcon = new TrayIcon(new ImageIcon(SystemTrayUtil.class.getResource("/img/logo1.png")).getImage(), "Flashsafe");
                tray.add(trayIcon);
                trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                logger.warn("Error while adding FlashSafe to tray", e);
            }
        }
    }

    public static void removeFromSystemTray() {
        if (tray != null) {
            tray.remove(trayIcon);
        }
    }

}
