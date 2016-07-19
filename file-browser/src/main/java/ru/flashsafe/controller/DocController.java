package ru.flashsafe.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class DocController {

    @FXML // fx:id="web"
    private WebView web; // Value injected by FXMLLoader
    
    private String url;
    
    public DocController(String url) {
        this.url = url;
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert web != null : "fx:id=\"web\" was not injected: check your FXML file 'doc.fxml'.";
        web.getEngine().load(url);
    }
}
