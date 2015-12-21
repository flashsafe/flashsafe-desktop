package ru.flashsafe.view;

import java.io.File;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import ru.flashsafe.util.FontUtil;

public class CopyFilePane extends AnchorPane {
    private Label descr;
    private ProgressBar progress;
    private Label time_remaining;
    private Button cancel;
    
    private ResourceBundle resourceBundle;
    
    private String src;
    private String dest;
    
    private long start_time;
    
    public CopyFilePane(String src, String dest, ResourceBundle resourceBundle) {
        this.src = src;
        this.dest = dest;
        this.resourceBundle = resourceBundle;
        getStylesheets().add(getClass().getResource("/css/fileuploadpane.css").toExternalForm());
        getStylesheets().add("http://flash.so/flashsafe/fileuploadpane.css");
        setPrefHeight(220.0);
        setPrefWidth(420.0);
        Pane pane = new Pane(); 
        pane.setStyle("-fx-background-color: #353F4B;");
        pane.setPrefHeight(200.0);
        pane.setPrefWidth(350.0);
        AnchorPane pane1 = new AnchorPane(); 
        pane1.setStyle("-fx-background-color: transparent;");
        pane1.setPrefHeight(200.0);
        pane1.setMaxWidth(385.0);
        pane1.getChildren().add(getTitleLabel());
        pane1.getChildren().add(getDescriptionLabel());
        pane1.getChildren().add(getProgressBar());
        pane1.getChildren().add(getTimeRemainingLabel());
        pane1.getChildren().add(getCancelButton());
        pane.getChildren().add(pane1);
        getChildren().add(pane);
        start_time = System.currentTimeMillis();
    }
    
    public ProgressBar getBar() {
        return progress;
    }
    
    public void updateTimeRemaining(double progress) {
        time_remaining.setText(resourceBundle.getString("time_remaining") + ": " + calculateTimeRemaining(progress));
    }
    
    public Button getCancel() {
        return cancel;
    }
    
    private String calculateTimeRemaining(double progress) {
        String t = "-";
        if(progress > 0) {
            long totalBytes = new File(src).length();
            long processedBytes = (long) (totalBytes * progress);
            long speed = processedBytes / ((System.currentTimeMillis() - start_time) / 1000);
            if(speed > 0) {
                long time = (totalBytes - processedBytes) / speed;
                long h = 0, m = 0, s = 0;
                if(time >= 3600) {
                    h = time / 3600;
                    m = (time % 3600) / 60;
                    s = time % 60;
                } else if(time >= 60) {
                    m = time  / 60;
                    s = time % 60;
                } else {
                    s = time;
                }
                t = (h == 0 ? "" : h + " " + (h == 1 ? resourceBundle.getString("hour") : resourceBundle.getString("hours"))) + " "
                        + (m == 0 ? "" : m + " " + resourceBundle.getString("minutes")) + " "
                        + (s == 0 ? "" : s + " " + resourceBundle.getString("seconds"));
            }
        }
        return t;
    }
    
    private Label getTitleLabel() {
        Label titleLabel = new Label();
        titleLabel.setLayoutX(10.0);
        titleLabel.setLayoutY(10.0);
        titleLabel.setText(resourceBundle.getString("copy"));
        titleLabel.setTextFill(Paint.valueOf("#ECEFF4"));
        AnchorPane.setLeftAnchor(titleLabel, 10.0);
        AnchorPane.setTopAnchor(titleLabel, 10.0);
        titleLabel.setFont(FontUtil.instance().font(FontUtil.FontType.FILE_TABLE_CONTENT));
        return titleLabel;
    }
   
    private Label getDescriptionLabel() {
        descr = new Label();
        descr.setId("filename");
        descr.setLayoutY(35.0);
        descr.setText(resourceBundle.getString("from") + " " + cropString(20, src) + "... " + resourceBundle.getString("to") + " " + cropString(20, dest) + "...");
        descr.setTextFill(Paint.valueOf("#ECEFF4"));
        AnchorPane.setLeftAnchor(descr, 10.0);
        descr.setFont(FontUtil.instance().font(FontUtil.FontType.FILE_TABLE_CONTENT));
        return descr;
    }
    
    private String cropString(int length, String str) {
        return str.length() > length ? str.substring(0, length) : str;
    }
    
    private ProgressBar getProgressBar() {
        progress = new ProgressBar();
        progress.setId("progress");
        progress.setLayoutY(75.0);
        progress.setPrefHeight(30.0);
        progress.setPrefWidth(325.0);
        progress.setProgress(0.0);
        AnchorPane.setLeftAnchor(progress, 10.0);
        AnchorPane.setRightAnchor(progress, 10.0);
//        progress.progressProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                updateTimeRemaining(newValue.doubleValue());
//            }
//        });
        return progress;
    }
    
    private Label getTimeRemainingLabel() {
        time_remaining = new Label();
        time_remaining.setLayoutY(115.0);
        time_remaining.setText(resourceBundle.getString("time_remaining") + ": ");
        time_remaining.setTextFill(Paint.valueOf("#ECEFF4"));
        AnchorPane.setLeftAnchor(time_remaining, 10.0);
        time_remaining.setFont(FontUtil.instance().font(FontUtil.FontType.FILE_TABLE_CONTENT));
        return time_remaining;
    }
    
    private Button getCancelButton() {
        cancel = new Button();
        cancel.setId("cancel");
        cancel.setCancelButton(true);
        cancel.setText(resourceBundle.getString("cancel"));
        cancel.setTextFill(Paint.valueOf("#5f5f5f"));
        AnchorPane.setBottomAnchor(cancel, 10.0);
        AnchorPane.setRightAnchor(cancel, 10.0);
        return cancel;
    }
}

