package ru.flashsafe.view;

import java.util.ResourceBundle;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import ru.flashsafe.util.FontUtil;
import ru.flashsafe.util.FontUtil.FontType;

/**
 * Settings pane
 * @author Alexander Krysin
 */
public class SettingsPane extends Pane {
	public Pane settings_pane = this;
	public Label rendering = new Label(), caching = new Label(), hardware = new Label(),
			software = new Label();
	public Label settings_close = new Label();
	public Pane software_pane = new Pane();
	public Hyperlink link = new Hyperlink();

	private ResourceBundle resourceBundle;

	public SettingsPane(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
		getStylesheets().add(getClass().getResource("/css/mainscene.css").toExternalForm());
		setPrefHeight(500.0);
		setPrefWidth(600.0);
		setStyle("-fx-background-color: #ECEFF4;");
		AnchorPane.setLeftAnchor(this, 200.0);
		AnchorPane.setTopAnchor(this, 75.0);
		getChildren().add(getContentPane());
		getChildren().add(getCloseLabel());
	}

	private Pane getContentPane() {
		Pane pane = new Pane();
		pane.setPrefHeight(500.0);
		setPrefWidth(180.0);
		setStyle("-fx-background-color: #353F4B;");
		Label[] labels = {rendering, caching, hardware, software};
		String[] texts = {"view", "caching", "about_device", "about"};
		double layout_y = 0.0;
		for(int i=0;i<4;i++) {
			labels[i].setLayoutY(layout_y);
			labels[i].setPrefHeight(50.0);
			labels[i].setPrefWidth(180.0);
			labels[i].setStyle("-fx-text-fill: #ECEFF4;");
			labels[i].getStyleClass().add("category");
			labels[i].setText(resourceBundle.getString(texts[i]));
			pane.getChildren().add(labels[i]);
			layout_y += 50.0;
		}
		pane.getChildren().add(getSoftwarePane());
		return pane;
	}

	private Pane getSoftwarePane() {
		software_pane.getStylesheets().add(getClass().getResource("/css/mainscene.css").toExternalForm());
		software_pane.setLayoutX(180.0);
		software_pane.setPrefHeight(500.0);
		software_pane.setPrefWidth(420.0);
		software_pane.setVisible(false);
		ImageView logo = new ImageView();
		logo.setFitHeight(150.0);
		logo.setFitWidth(200.0);
		logo.setLayoutX(135.0);
		logo.setLayoutY(14.0);
		logo.setPickOnBounds(true);
		logo.setPreserveRatio(true);
		logo.setImage(new Image(getClass().getResourceAsStream("/img/logo.png")));
		software_pane.getChildren().add(logo);
		Label label = new Label();
		label.setAlignment(Pos.CENTER);
		label.setLayoutX(147.0);
		label.setLayoutY(149.0);
		label.setStyle("-fx-padding: 0;");
		label.getStyleClass().add("category");
		label.setText("Flashsafe 1.0.0");
		software_pane.getChildren().add(label);
		link.setAlignment(Pos.CENTER);
		link.setContentDisplay(ContentDisplay.CENTER);
		link.setLayoutX(126.0);
		link.setLayoutY(466.0);
		link.setText("http://flashsafe.ru");
		link.setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
		software_pane.getChildren().add(link);
		return software_pane;
	}

	private Label getCloseLabel() {
		settings_close.setAlignment(Pos.CENTER);
		settings_close.setContentDisplay(ContentDisplay.CENTER);
		settings_close.setLayoutX(575.0);
		settings_close.setPrefHeight(25.0);
		settings_close.setPrefWidth(25.0);
		settings_close.setStyle("-fx-background-color: #353F4B;");
		settings_close.setText("X");
		settings_close.setTextFill(Paint.valueOf("#eceff4"));
		return settings_close;
	}
}
