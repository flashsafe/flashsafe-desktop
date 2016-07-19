package ru.flashsafe.view;

import java.util.ResourceBundle;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;

import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.util.FontUtil;
import ru.flashsafe.util.FontUtil.FontType;

/**
 * Enter Pincode Pane
 * @author Alexander Krysin
 */
public class EnterPincodePane extends Pane {
	public Pane pincode_dialog = this;
	public PasswordField pincode_textfield = new PasswordField();
	public Button pincode_submit = new Button();
	public Button backspace = new Button();
	public Button one = new Button(), two = new Button(), three = new Button(), four = new Button(),
			five = new Button(), six = new Button(), seven = new Button(), eight = new Button(),
			nine = new Button(), zero = new Button();

	private ResourceBundle resourceBundle;

	@SuppressWarnings("unused")
	private MainSceneController controller;

	public EnterPincodePane(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
		getStylesheets().add(getClass().getResource("/css/mainscene.css").toExternalForm());
		setId("PincodeDialog");
		//setLayoutX(288.0);
		//setLayoutY(150.0);
		setPrefHeight(340.0);
		setPrefWidth(300.0);
		getChildren().add(getTitleLabel());
		getChildren().add(getPane());
	}

	public void setController(MainSceneController controller) {
		this.controller = controller;
		pincode_submit.setOnAction(event -> controller.onPincodeSubmit());
		backspace.setOnAction(event -> controller.backspace());
	}

	private Label getTitleLabel() {
		Label label = new Label();
		label.setId("PincodeTitle");
		label.setAlignment(Pos.CENTER);
		label.setContentDisplay(ContentDisplay.CENTER);
		label.setPrefHeight(20.0);
		label.setPrefWidth(300.0);
		label.setText(resourceBundle.getString("enter_pin_code"));
		label.setTextFill(Paint.valueOf("#7c7c7c"));
		label.setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
		return label;
	}

	private Pane getPane() {
		Pane pane = new Pane();
		pane.setId("PincodePane");
		pane.setLayoutY(20.0);
		pane.setPrefHeight(320.0);
		pane.setPrefWidth(300.0);
		pincode_textfield.setId("PincodeTextfield");
		pincode_textfield.setLayoutX(28.0);
		pincode_textfield.setLayoutY(15.0);
		pincode_textfield.setPrefHeight(40.0);
		pincode_textfield.setPrefWidth(245.0);
		pincode_textfield.setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
		pane.getChildren().add(pincode_textfield);
		Button[] buttons = {pincode_submit, one, two, three, four, five, six, seven, eight, nine, backspace, zero};
		String[] texts = {"OK", "1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0"};
		double[] layout_x = {198.0, 28.0, 113.0, 198.0, 28.0, 113.0, 198.0, 28.0, 113.0, 198.0, 28.0, 113.0};
		double[] layout_y = {255.0, 75.0, 75.0, 75.0, 135.0, 135.0, 135.0, 195.0, 195.0, 195.0, 255.0, 255.0};
		String[] ids = {"PincodeSubmit", "PincodeSubmit", "PincodeSubmit", "PincodeSubmit", "PincodeSubmit", "PincodeSubmit",
				"PincodeSubmit", "PincodeSubmit", "PincodeSubmit", "PincodeSubmit", "PincodeBackspace", "PincodeSubmit"};
		for(int i=0;i<12;i++) {
			buttons[i].setId(ids[i]);
			buttons[i].setLayoutX(layout_x[i]);
			buttons[i].setLayoutY(layout_y[i]);
			buttons[i].setMnemonicParsing(false);
			buttons[i].setPrefHeight(50.0);
			buttons[i].setPrefWidth(75.0);
			buttons[i].setText(texts[i]);
			buttons[i].setTextFill(Paint.valueOf("#f3f3f3"));
			buttons[i].setFont(FontUtil.instance().font(FontType.FILE_TABLE_CONTENT));
			pane.getChildren().add(buttons[i]);
		}
		return pane;
	}
}
