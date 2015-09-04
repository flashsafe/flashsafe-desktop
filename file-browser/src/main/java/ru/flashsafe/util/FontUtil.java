package ru.flashsafe.util;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.text.Font;

public class FontUtil {
    
    public enum FontType {
        
        LEFT_MENU,
        
        FILE_TABLE_CONTENT
        
    }
    
    private static final Map<FontType , Font> fonts = new HashMap<>();
    
    private static final FontUtil INSTANCE = new FontUtil();
    
    private FontUtil() {
        init();
    }
    
    private void init() {
        fonts.put(FontType.LEFT_MENU, Font.loadFont(getClass().getResourceAsStream("/font/myriadpro_regular.ttf"), 20));
        fonts.put(FontType.FILE_TABLE_CONTENT, new Font("Ubuntu Condensed", 14));
    }
    
    public static FontUtil instance() {
        return INSTANCE;
    }
    
    public Font font(FontType fontType) {
        return fonts.get(fontType);
    }
    
}
