package application;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class YesNoOKController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private AnchorPane aPane;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Label lblTitle;
    
    @FXML
    private TextArea taMessage;
    
    @FXML
    private ImageView icon;
    
    private ButtonInfo action = null;
    private List<ButtonInfo> dialogButtons = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dialogButtons = new ArrayList<ButtonInfo>();
		
		taMessage.setStyle("-fx-font-size:16;");
	}
	
	public void setTitle(String title) {
		lblTitle.setText(title);
	}
	
	public void setImage(Image img) {
		icon.setImage(img);
	}
	
	public void setMessage(String msg) {
		taMessage.setText(msg);
	}
	
	public void clearButtons() {
		dialogButtons.clear();
	}
	
	public void addButtons(ButtonInfo[] buttons) {
		
		for (ButtonInfo bi : buttons) {
			dialogButtons.add(bi);
		}
		
		for (ButtonInfo bi : dialogButtons) {
			Button b = new Button(bi.getText());
			b.setPrefWidth(115.0);
			b.setPrefHeight(30.0);
			b.setStyle("-fx-font-size:18;");
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
	            public void handle(ActionEvent event) {
					Button b = (Button)event.getSource();
					for (ButtonInfo bi : dialogButtons) {
						if (bi.getText().equalsIgnoreCase(b.getText()) == true) {
							action = bi;
							break;
						}
					}
					
					Stage stage = (Stage) b.getScene().getWindow();
			        stage.close();
	            }
			});
			flowPane.getChildren().add(b);
			if (bi.isDefaultButton())
				b.requestFocus();
		}
	}
	
	public ButtonInfo getAction() {
		return action;
	}
}
