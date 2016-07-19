/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author FlashSafe
 */
public class PicController {
    @FXML
    ImageView pic;
    File picture;

    public PicController(File pic) {
        this.picture = pic;
    }

    public void initialize() {
        try {
            pic.setImage(new Image(new FileInputStream(picture)));
        } catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
