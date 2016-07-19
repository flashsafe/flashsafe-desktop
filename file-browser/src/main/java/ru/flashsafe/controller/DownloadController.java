/**
 * 
 */
package ru.flashsafe.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author Alexander Krysin
 *
 */
public class DownloadController implements Initializable {
	@FXML
	private Label filename;
	@FXML
	private ImageView cancel_button;
	@FXML
	private ProgressBar progress;
	//@FXML
	//private Label remaining_time;
	@FXML
	private ImageView download_enabled;
	@FXML
	private ImageView upload_enabled;
	
	//private CopyFileController controller;
	//private FileOperation operation;
	//private AnchorPane root;
	
	public DownloadController(CopyFileController controller/*, FileOperation operation, AnchorPane root*/) {
		//this.controller = controller;
		//this.operation = operation;
		//this.root = root;
	}

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		//remaining_time.setText("");
	}
	
	public ImageView getCancelButton() {
		return cancel_button;
	}
	
	public ProgressBar getProgress() {
		return progress;
	}
	
	public void setFileName(String filename) {
		this.filename.setText(filename);
	}
	
	//public void setRemainingTime(String remainingTime) {
		//remaining_time.setText(remainingTime);
	//}
	
	public void enableDownloadIcon() {
		download_enabled.setImage(new Image(getClass().getResourceAsStream("/img/download_enabled.png")));
		download_enabled.setVisible(true);
		upload_enabled.setVisible(false);
	}
	
	public void disableDownloadIcon() {
		download_enabled.setImage(new Image(getClass().getResourceAsStream("/img/download_disabled.png")));
		download_enabled.setVisible(true);
		upload_enabled.setVisible(false);
	}
	
	public void enableUploadIcon() {
		upload_enabled.setImage(new Image(getClass().getResourceAsStream("/img/upload_enabled.png")));
		upload_enabled.setVisible(true);
		download_enabled.setVisible(false);
	}
	
	public void disableUploadIcon() {
		upload_enabled.setImage(new Image(getClass().getResourceAsStream("/img/upload_disabled.png")));
		upload_enabled.setVisible(true);
		download_enabled.setVisible(false);
	}

}
