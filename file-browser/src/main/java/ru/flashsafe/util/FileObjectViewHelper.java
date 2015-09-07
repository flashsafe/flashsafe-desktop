package ru.flashsafe.util;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import ru.flashsafe.IconUtil;
import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileObject;
import ru.flashsafe.core.file.FileObjectType;

public class FileObjectViewHelper {

    private static final int KILOBYTE = 1024;
    
    private final ResourceBundle resourceBundle;
    
    public FileObjectViewHelper(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    
    public Label createLabelFor(FileObject fileObject) {
        Label label = new Label(fileObject.getName());
        label.setFont(new Font("Ubuntu Condensed", 14));
        label.setTextFill(Paint.valueOf("#000"));
        try {
            Tooltip tooltip = createTooltipFor(fileObject);
            label.setTooltip(tooltip);
            ImageView icon = createIcon(fileObject);
            label.setGraphic(icon);
            return label;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getTypeDescriptionFor(FileObject fileObject) {
        String description;
        if (fileObject.getType() == FileObjectType.FILE) {
            description = resourceBundle.getString("file");
        } else {
            description = resourceBundle.getString("folder");
        }
        return description;
    }
    
    public String getSizeDescriptionFor(FileObject fileObject) {
        if (fileObject.getType() == FileObjectType.DIRECTORY) {
            return "";
        }
        try {
            String description = String.valueOf(fileObject.getSize() / KILOBYTE);
            return description + " " + resourceBundle.getString("kilobyte");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ImageView createIcon(FileObject fileObject) {
        ImageView icon; 
        if (fileObject.getType() == FileObjectType.DIRECTORY) {
            icon = new ImageView(IconUtil.getFolderIcon((Directory)fileObject));
        } else {
            icon = new ImageView(IconUtil.getFileIcon(fileObject.getName()));
        }
        icon.setFitHeight(24);
        icon.setFitWidth(24);
        return icon;
    }
    
    private Tooltip createTooltipFor(FileObject fileObject) throws IOException {
        StringBuilder tooltipString = new StringBuilder();
        tooltipString.append(resourceBundle.getString("name")).append(": ").append(fileObject.getName()).append(System.lineSeparator())
                .append(resourceBundle.getString("type")).append(": ").append(fileObject.getType()).append(System.lineSeparator());
        if (fileObject.getType() == FileObjectType.FILE) {
            tooltipString.append(resourceBundle.getString("file_format")).append(": ").append(((File)fileObject).getFileFormat())
                    .append(System.lineSeparator());
        }
        tooltipString.append(resourceBundle.getString("size")).append(": ").append(getSizeDescriptionFor(fileObject)).append(System.lineSeparator());
        if (fileObject.getType() == FileObjectType.DIRECTORY) {
            tooltipString.append(resourceBundle.getString("number_of_files")).append(": ").append(((Directory)fileObject).getCount())
                    .append(System.lineSeparator());
        }
        /*
        tooltipString.append(resourceBundle.getString("creation_date")).append(": ")
                .append(new Date(fsObject.create_time * 1000).toLocaleString()).append(System.lineSeparator())
                .append(resourceBundle.getString("last_update")).append(": ")
                .append(new Date(fsObject.update_time * 1000).toLocaleString()).append(System.lineSeparator());*/
        return new Tooltip(tooltipString.toString());
    }
    
}