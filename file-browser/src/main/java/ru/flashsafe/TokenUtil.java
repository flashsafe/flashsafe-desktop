/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe;

import org.pkcs11.jacknji11.CE;
import org.pkcs11.jacknji11.CKA;
import org.pkcs11.jacknji11.CKRException;
import static org.pkcs11.jacknji11.CK_SESSION_INFO.CKF_RW_SESSION;
import static org.pkcs11.jacknji11.CK_SESSION_INFO.CKF_SERIAL_SESSION;

/**
 *
 * @author aless
 */
public class TokenUtil {
        public static void PKCS11Initialize() {
        try {
            CE.Initialize();
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static long[] getSlotList() {
        long[] slotList = null;
        try {
            slotList = CE.GetSlotList(true);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
        return slotList;
    }
    
    public static long openSession(long slotId) {
        long sessionId = 0;
        try {
            sessionId = CE.OpenSession(slotId, CKF_SERIAL_SESSION | CKF_RW_SESSION, null, null);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
        return sessionId;
    }
    
    public static void login(long sessionId, String pin) {
        try {
            CE.LoginSO(sessionId, pin);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static void logout(long sessionId) {
        try {
            CE.Logout(sessionId);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static void closeSession(long slotId) {
        try {
            CE.CloseSession(slotId);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static void findObjectsInit(long sessionId, CKA... templ) {
        try {
            CE.FindObjectsInit(sessionId, templ);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static long[] findObjects(long sessionId, int maxCount) {
        long[] objects = null;
        try {
            objects = CE.FindObjects(sessionId, maxCount);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
        return objects;
    }
    
    public static void findObjectsFinal(long sessionId) {
        try {
            CE.FindObjectsFinal(sessionId);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static CKA getAttributeValue(long sessionId, long objectId, long cka) {
        CKA value = null;
        try {
            value = CE.GetAttributeValue(sessionId, objectId, cka);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
        return value;
    }
    
    public static void setAttributeValue(long sessionId, long objectId, CKA... templ) {
        try {
            CE.SetAttributeValue(sessionId, objectId, templ);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static void closeAllSessions(long slotId) {
        try {
            CE.CloseAllSessions(slotId);
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
    
    public static void PKCS11Finalize() {
        try {
            CE.Finalize();
        } catch(CKRException ex) {
            System.out.println("ERROR: " + ex.getCKR() + " " + ex.getMessage());
        }
    }
}
