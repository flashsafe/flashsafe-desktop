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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ru.flashsafe.http.HttpAPI;
import ru.flashsafe.model.FSObject;

/**
 * FXML Controller class
 *
 * @author alex_xpert
 */
public class MainSceneController implements Initializable {
    private final ImageView cloud_enabled = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/cloud_enabled.png"))
    );
    private final ImageView folderIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder.png"))
    );
    private final ImageView folderBlackIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/folder_black.png"))
    );
    private final ImageView lockIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock.png"))
    );
    private final ImageView lockBlackIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/lock_black.png"))
    );
    private final ImageView dividerIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/divider.png"))
    );
    private final ImageView fileIcon = new ImageView(
            new Image(getClass().getResourceAsStream("/ru/flashsafe/img/file.png"))
    );
    
    private boolean menu_opened = false;
    
    @FXML
    private AnchorPane myFilesPane;
    @FXML
    private Label upload_button;   
    @FXML
    private Label download_button;
    @FXML
    private Label create_path_button;
    @FXML
    private Label back_button;
    @FXML
    private Label settings;
    @FXML
    private Label filename;
    @FXML
    private Label filetype;
    @FXML
    private Label filesize;
    @FXML
    private Label cloud;
    @FXML
    private Pane menu;
    @FXML
    private Pane network;
    @FXML
    private Label status;
    @FXML
    private Pane pincode_dialog;
    @FXML
    private TextField pincode_textfield;
    @FXML
    private Button pincode_submit;
    @FXML
    private VBox files1;
    @FXML
    private VBox files2;
    @FXML
    private TextField current_path;
    @FXML
    TitledPane mf;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int auth = HttpAPI.auth();
        if (auth == 0) {
            network.setVisible(true);
        } else {
            while (cloud_enabled.getImage().isBackgroundLoading()) {}
            cloud.setGraphic(cloud_enabled);
            FSObject[] content = HttpAPI.getContent();
            while (dividerIcon.getImage().isBackgroundLoading()) {}
            TreeItem<String> root_item = new TreeItem("", dividerIcon);
            root_item.setExpanded(true);
            TreeView myFilesTree = new TreeView(root_item);
            myFilesPane.getChildren().add(myFilesTree);
            mf.setExpanded(true);
            while (folderIcon.getImage().isBackgroundLoading() || lockIcon.getImage().isBackgroundLoading()
                    || folderBlackIcon.getImage().isBackgroundLoading() || fileIcon.getImage().isBackgroundLoading()
                    || lockBlackIcon.getImage().isBackgroundLoading()) {}
            for (int i=0;i<content.length;i++) {
                FSObject fso = content[i];
                if (fso.type.equals("dir")) {
                    TreeItem<String> treeitem = new TreeItem(fso.name, fso.pincode ? lockIcon : folderIcon);
                    root_item.getChildren().add(treeitem);
                }
                Label label = new Label(fso.name, fso.type.equals("dir") ? fso.pincode ? lockBlackIcon : folderBlackIcon : fileIcon);
                (i % 2 == 0 ? files1 : files2).getChildren().add(label);
            }
            current_path.setText("/");
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
