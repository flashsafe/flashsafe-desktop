package ru.flashsafe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.image.Image;

public class IconUtil {

    private static final Map<String, Image> extensionToIconMap = new HashMap<>();

    private static final String UNKNOWN_EXTENSION = "UNKNOWN_EXTENSION";
    
    public enum IconType {

        ARCHIVE,

        DOCUMENT,

        PICTURE,

        MUSIC,

        VIDEO,

        BINARY

    }

    static {
        init();
    }

    private IconUtil() {
    }

    public static Image getFileIcon(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        Image fileIcon = extensionToIconMap.get(extension);
        return fileIcon != null ? fileIcon : extensionToIconMap.get(UNKNOWN_EXTENSION);
    }

    private static void init() {
        Map<IconType, Image> typeToImageMap = loadIcons();
        Map<IconType, List<String>> typeToExtentionMap = getExtensions();
        mapExtensionsToIcons(typeToExtentionMap, typeToImageMap);
    }

    private static void mapExtensionsToIcons(Map<IconType, List<String>> typeToExtentionMap, Map<IconType, Image> typeToImageMap) {
        for (Entry<IconType, List<String>> typeToExtention : typeToExtentionMap.entrySet()) {
            for (String extension : typeToExtention.getValue()) {
                extensionToIconMap.put(extension, typeToImageMap.get(typeToExtention.getKey()));
            }
        }
        extensionToIconMap.put(UNKNOWN_EXTENSION, typeToImageMap.get(IconType.BINARY));
    }

    private static Map<IconType, List<String>> getExtensions() {
        Map<IconType, List<String>> typeToImageMap = new HashMap<>();
        typeToImageMap.put(IconType.ARCHIVE, Arrays.asList("rar", "zip", "gz", "bz", "7z", "bz2", "tar", "deb", "rpm"));
        typeToImageMap.put(IconType.DOCUMENT,
                Arrays.asList("txt", "rtf", "doc", "xls", "ppt", "docx", "xlsx", "pptx", "odt", "odp", "ods", "odg"));
        typeToImageMap.put(IconType.PICTURE,
                Arrays.asList("jpeg", "jpe", "jpg", "png", "gif", "tiff", "tif", "bmp", "wlmp", "svg", "eps", "ico", "icns"));
        typeToImageMap.put(IconType.MUSIC, Arrays.asList("mp1", "mp2", "mp3", "wma", "wav", "amr", "aac", "midi", "ogg"));
        typeToImageMap.put(IconType.VIDEO, Arrays.asList("3gp", "3gpp", "avi", "flv", "mkv", "mov", "qt", "vob", "wmv"));
        return typeToImageMap;
    }

    private static Map<IconType, Image> loadIcons() {
        Map<IconType, Image> typeToImage = new HashMap<>();
        typeToImage.put(IconType.ARCHIVE, new Image(IconUtil.class.getResourceAsStream("/img/fs/archive.png"), 24, 24, false,
                false));
        typeToImage.put(IconType.DOCUMENT, new Image(IconUtil.class.getResourceAsStream("/img/fs/document.png"), 24, 24, false,
                false));
        typeToImage.put(IconType.PICTURE, new Image(IconUtil.class.getResourceAsStream("/img/fs/picture.png"), 24, 24, false,
                false));
        typeToImage.put(IconType.MUSIC, new Image(IconUtil.class.getResourceAsStream("/img/fs/music.png"), 24, 24, false, false));
        typeToImage.put(IconType.VIDEO, new Image(IconUtil.class.getResourceAsStream("/img/fs/video.png"), 24, 24, false, false));
        typeToImage.put(IconType.BINARY,
                new Image(IconUtil.class.getResourceAsStream("/img/fs/binary.png"), 24, 24, false, false));
        return typeToImage;
    }

}
