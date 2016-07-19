package ru.flashsafe.view;

import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.util.FontUtil;
import ru.flashsafe.util.FontUtil.FontType;

/**
 * Create Path Pane
 * @author Alexander Krysin
 */
public class CreatePathPane extends Pane {
	public Pane pathname_dialog = this;
	public TextField pathname_textfield = new TextField();
	public Button pathname_submit = new Button();

	private ResourceBundle resourceBundle;

	@SuppressWarnings("unused")
	private MainSceneController controller;

	public CreatePathPane(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
		getStylesheets().add(getClass().getResource("/css/mainscene.css").toExternalForm());
		setId("CreatePathDialog");
		//setLayoutX(288.0);
		//setLayoutY(263.0);
		setPrefHeight(200.0);
		setPrefWidth(325.0);
		getChildren().add(getTitleLabel());
		getChildren().add(getPane());
	}

	public void setController(MainSceneController controller) {
		this.controller = controller;
		pathname_submit.setOnAction(event -> controller.onPathnameSubmit());
		pathname_textfield.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> val, String oldval, String newval) {
				System.out.println("VAL: " + val + "\nOLDVAL: " + oldval + "\nNEWVAL: " + newval);
				if(oldval.equals("")) pathname_submit.setStyle("-fx-effect: dropshadow(three-pass-box, #6CD0F3, 2, 5,0, 0);");
				if(newval.equals("")) pathname_submit.setStyle("-fx-effect: null;");
			}
		});
		pathname_textfield.setOnAction(event -> controller.onPathnameSubmit());
		pathname_textfield.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				controller.hidePathDialog();
			}
		});
	}

	private Label getTitleLabel() {
		Label label = new Label();
		label.setId("CreatePathTitle");
		label.setAlignment(Pos.CENTER);
		label.setContentDisplay(ContentDisplay.CENTER);
		label.setPrefHeight(20.0);
		label.setPrefWidth(325.0);
		label.setText(resourceBundle.getString("folder_name"));
		label.setTextFill(Paint.valueOf("#7c7c7c"));
		label.setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
		return label;
	}

	private Pane getPane() {
		Pane pane = new Pane();
		pane.setId("CreatePathPane");
		pane.setLayoutY(20.0);
		pane.setPrefHeight(180.0);
		pane.setPrefWidth(325.0);
		pathname_textfield.setId("CreatePathTextfield");
		pathname_textfield.setLayoutX(40.0);
		pathname_textfield.setLayoutY(25.0);
		pathname_textfield.setPrefHeight(40.0);
		pathname_textfield.setPrefWidth(245.0);
		pathname_textfield.setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
		pane.getChildren().add(pathname_textfield);
		pathname_submit.setId("CreatePathSubmit");
		pathname_submit.setLayoutX(40.0);
		pathname_submit.setLayoutY(75.0);
		pathname_submit.setMnemonicParsing(false);
		pathname_submit.setPrefHeight(40.0);
		pathname_submit.setPrefWidth(245.0);
		pathname_submit.setText("OK");
		pathname_submit.setTextFill(Paint.valueOf("#f3f3f3"));
		pathname_submit.setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
		pane.getChildren().add(pathname_submit);
		return pane;
	}
}
