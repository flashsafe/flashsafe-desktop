package ru.flashsafe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Alexander Krysin
 */
public class ResourcesUtil {
    
    public static String loadQSS(String name) {
        String qss = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("F:/flashsafe-desktop/file-browser/src/main/resources/css/" + name + ".qss")));
            String line;
            while((line = reader.readLine()) != null) {
                qss += line;
            }
        } catch(IOException e) {
            
        } finally {
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch(IOException e) {
            }
        }
        return qss;
    }
    
}
