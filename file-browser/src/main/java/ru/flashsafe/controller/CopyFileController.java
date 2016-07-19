/**
 * 
 */
package ru.flashsafe.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Alexander Krysin
 *
 */
public class CopyFileController implements Initializable {
	@FXML
	private Pane topbar;
	@FXML
	private Label title_text;
	@FXML
	private Pane minimize_button;
	@FXML
	private Pane close_button;
	@FXML
	private ListView<Node> downloads_list;
	
	private Stage stage;
	
	private double windowXPosition;

    private double windowYPosition;
	
	public CopyFileController(Stage stage) {
		this.stage = stage;
	}

	/* (non-Javadoc)
	 * @see javafx.fxml.Initializable#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		//downloads_list.setStyle(".divider > .line {-fx-border-style: solid;-fx-border-width: 0 0 1 0;-fx-border-color: #515354;}");
		close_button.setOnMouseClicked((event) -> {
			stage.hide();
		});
		minimize_button.setOnMouseClicked((event) -> {
			stage.setIconified(true);
		});
		attachWindowDragControlToElement(topbar);
		downloads_list.setStyle(".list-cell {-fx-background-color: #7C7C7C;} .root {-fx-background-color: #7C7C7C;}");
	}
	
	private void attachWindowDragControlToElement(Node element) {
        element.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - windowXPosition);
            stage.setY(event.getScreenY() - windowYPosition);
        });
        element.setOnMousePressed(event -> {
            windowXPosition = event.getSceneX();
            windowYPosition = event.getSceneY();
        });
    }
	
	public void addDownload(AnchorPane download) {
		if(downloads_list.getItems().size() == 7) {
			for(Node node : downloads_list.getItems()) {
				downloads_list.setPrefWidth(downloads_list.getWidth() - 5.0);
				((AnchorPane) node).setPrefWidth(((AnchorPane) node).getPrefWidth() - 13.0);
			}
		}
		if(downloads_list.getItems().size() >= 7) {
			download.setPrefWidth(download.getPrefWidth() - 13.0);
		}
		downloads_list.getItems().add(download);
		downloads_list.scrollTo(downloads_list.getItems().size() - 1);
		stage.requestFocus();
	}
	
	public void deleteDownload(AnchorPane download) {
		downloads_list.getItems().remove(download);
		if(downloads_list.getItems().size() == 7) {
			for(Node node : downloads_list.getItems()) {
				downloads_list.setPrefWidth(downloads_list.getWidth() + 5.0);
				((AnchorPane) node).setPrefWidth(((AnchorPane) node).getPrefWidth() + 13.0);
			}
		}
		if(downloads_list.getItems().isEmpty()) {
			stage.close();
		}
	}

}
