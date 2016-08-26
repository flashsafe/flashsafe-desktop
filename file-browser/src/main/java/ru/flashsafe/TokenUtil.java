package ru.flashsafe;

import org.pkcs11.jacknji11.CE;
import org.pkcs11.jacknji11.CKA;
import static org.pkcs11.jacknji11.CK_SESSION_INFO.CKF_RW_SESSION;
import static org.pkcs11.jacknji11.CK_SESSION_INFO.CKF_SERIAL_SESSION;

/**
 *
 * @author aless
 */
public class TokenUtil {
    
    static {
        System.load("F:/flashsafe-desktop/file-browser/eps2003csp11.dll");
    }
    
    public static void PKCS11Initialize() {
        CE.Initialize();
    }
    
    public static long[] getSlotList() {
        return CE.GetSlotList(true);
    }
    
    public static long openSession(long slotId) {
        return CE.OpenSession(slotId, CKF_SERIAL_SESSION | CKF_RW_SESSION, null, null);
    }
    
    public static void login(long sessionId, String pin) {
        CE.LoginUser(sessionId, pin);
    }
    
    public static void logout(long sessionId) {
        CE.Logout(sessionId);
    }
    
    public static void closeSession(long slotId) {
        CE.CloseSession(slotId);
    }
    
    public static void findObjectsInit(long sessionId, CKA... templ) {
        CE.FindObjectsInit(sessionId, templ);
    }
    
    public static long[] findObjects(long sessionId, int maxCount) {
        return CE.FindObjects(sessionId, maxCount);
    }
    
    public static void findObjectsFinal(long sessionId) {
        CE.FindObjectsFinal(sessionId);
    }
    
    public static CKA getAttributeValue(long sessionId, long objectId, long cka) {
        return CE.GetAttributeValue(sessionId, objectId, cka);
    }
    
    public static void setAttributeValue(long sessionId, long objectId, CKA... templ) {
        CE.SetAttributeValue(sessionId, objectId, templ);
    }
    
    public static void closeAllSessions(long slotId) {
        CE.CloseAllSessions(slotId);
    }
    
    public static void PKCS11Finalize() {
        CE.Finalize();
    }
}
