/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author alex_xpert
 */
public class MainSceneController implements Initializable {
    private final Node folderIcon = new ImageView(
        new Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder.png"))
    );
    private final Node lockIcon = new ImageView(
        new Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock.png"))
    );
    private final Node dividerIcon = new ImageView(
        new Image(getClass().getResourceAsStream("/ru/flashsafe/img/divider.png"))
    );
    
    private boolean menu_opened = false;
    
    @FXML
    private AnchorPane myFilesPane;
    @FXML
    private Label settings;
    @FXML
    private Pane menu;
    @FXML
    private VBox files1;
    @FXML
    private VBox files2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        TreeItem<String> rootItem = new TreeItem<String> ("Отпуск", folderIcon);
        rootItem.setExpanded(true);
        rootItem.getChildren().add(new TreeItem<String> ("", dividerIcon));
        rootItem.getChildren().add(new TreeItem<String> ("Девчонки", lockIcon));
        TreeView myFilesTree = new TreeView<String>(rootItem);
        myFilesPane.getChildren().add(myFilesTree);
        
        String[] images = {"Катюша", "Катюша 2", "Катюша 3", "Катюша 4", "Катюша 5", "Настя", "Ира 4 размер", "IMG_0047", "IMG_0048",
                "Анька", "Ксюша бар", "бухие", "PHOTO472", "PHOTO 476", "PHOTO 477", "PHOTO 478", "PHOTO 479", "PHOTO 480", "alisa"};
        for(int i=0;i<19;i++) {
            Label label = new Label(images[i], new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/image.png"))));
            if(i % 2 == 0) files1.getChildren().add(label); else files2.getChildren().add(label);
        }
    }    
    
    public void toggleSettings() {
        if(menu_opened) {
            settings.setStyle("-fx-background-color: transparent");
            settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/settings.png"))));
            menu.setVisible(false);
            menu_opened = false;
        } else {
            settings.setStyle("-fx-background-color: #F3F3F3");
            settings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/ru/flashsafe/img/settings_opened.png"))));
            menu.setVisible(true);
            menu_opened = true;
        }
    }
}
