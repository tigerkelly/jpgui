package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelpController implements Initializable, RefreshScene {

	@FXML
    private AnchorPane aPane;

    @FXML
    private Accordion accordion;

    @FXML
    private Button btnClose;
    
    private JpGlobal jg = JpGlobal.getInstance();

    @FXML
    void doBtnClose(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }

    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		accordion.setStyle("-fx-font-size: 14px;");
		
		String os = System.getProperty("os.name").toLowerCase();
		
		String fn = null;
		if (os.contains("win") == true) {
			fn = "windows_" + jg.jpackageVersion.replaceAll("\\.", "_") + ".txt";
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			fn = "linux_" + jg.jpackageVersion.replaceAll("\\.", "_") + ".txt";
		} else if (os.contains("mac") == true) {
			fn = "apple_" + jg.jpackageVersion.replaceAll("\\.", "_") + ".txt";
		}
		
		AnchorPane ap2 = null;
		TitledPane tp = null;
		ScrollPane scroll = null;
		VBox vb2 = new VBox();
		HBox hb1 = null;
		HBox hb2 = null;
		
		Insets inset1 = new Insets(0, 0, 0, 4);
		Insets inset2 = new Insets(0, 0, 0, 32);
		
		try (InputStream in = getClass().getResourceAsStream("/resources/" + fn);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		    
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isBlank() == true) {
					continue;
				}
				
				if (line.charAt(0) == '^') {
					tp = new TitledPane();
					ap2 = new AnchorPane();
					scroll = new ScrollPane();
					vb2 = new VBox();
					
					scroll.setPrefHeight(accordion.getHeight());
				    scroll.prefWidth(accordion.getWidth());
				    
				    scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
				    scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
				    
				    scroll.setContent(vb2);
				
					ap2.getChildren().add(vb2);
					accordion.getPanes().add(tp);
				
					AnchorPane.setTopAnchor(vb2, 0.0);
					AnchorPane.setBottomAnchor(vb2, 0.0);
					AnchorPane.setRightAnchor(vb2, 0.0);
					AnchorPane.setLeftAnchor(vb2, 0.0);
					
					tp.setText(line.substring(1));
					
					vb2.setAlignment(Pos.TOP_LEFT);
					vb2.setSpacing(4.0);
					tp.setContent(scroll);
					
				} else if (line.charAt(0) == '&') {
//						System.out.println(line);
					Text t = new Text();
					t.setStyle("-fx-font-size: 16px; -fx-font-family: Monospace;");
					String[] arr = line.split(":");
					Label lbl = new Label(arr[0].substring(1) + ":");
					lbl.setUserData(arr[0].substring(1));
					lbl.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");

					hb1 = new HBox();
					hb1.setSpacing(4.0);
					hb1.setPadding(inset1);
					hb1.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb2 = new HBox();
					hb2.setSpacing(4.0);
					hb2.setPadding(inset2);
					hb2.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb1.getChildren().add(lbl);
					hb2.getChildren().add(t);
					vb2.getChildren().addAll(hb1, hb2);
					
					String txt = arr[1];
					String line2 = null;
					while ((line2 = reader.readLine()) != null) {
						line2 = line2.trim();
						if (line2.charAt(0) == ';')
							break;
						txt += "\n" + line2;
					}
					t.setText(txt);
				} else if (line.charAt(0) == '*') {
					Text t = new Text();
					t.setStyle("-fx-font-size:16px; -fx-font-family: Monospace;");
					String[] arr = line.split(":");
					Label lbl = new Label(arr[0].substring(1) + ":");
					lbl.setUserData(arr[0].substring(1));
					lbl.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
					hb1 = new HBox();
					hb1.setSpacing(4.0);
					hb1.setPadding(inset1);
					hb1.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb2 = new HBox();
					hb2.setSpacing(4.0);
					hb2.setPadding(inset2);
					hb2.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb1.getChildren().add(lbl);
					hb2.getChildren().add(t);
					vb2.getChildren().addAll(hb1, hb2);
					
					String txt = arr[1];
					String line2 = null;
					while ((line2 = reader.readLine()) != null) {
						line2 = line2.trim();
						if (line2.charAt(0) == ';')
							break;
						txt += "\n" +  line2;
					}
					t.setText(txt);
				} else if (line.charAt(0) == '-') {
//						System.out.println(line);
					Text t = new Text();
					t.setStyle("-fx-font-size:16px; -fx-font-family: Monospace;");
					String[] arr = line.split(":");
					Label lbl = new Label(arr[0].substring(1) + ":");
					lbl.setUserData(arr[0].substring(1));
					lbl.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
					hb1 = new HBox();
					hb1.setSpacing(4.0);
					hb1.setPadding(inset1);
					hb1.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb2 = new HBox();
					hb2.setSpacing(4.0);
					hb2.setPadding(inset2);
					hb2.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb1.getChildren().add(lbl);
					hb2.getChildren().add(t);
					vb2.getChildren().addAll(hb1, hb2);
					
					String txt = arr[1];
					String line2 = null;
					while ((line2 = reader.readLine()) != null) {
						line2 = line2.trim();
						if (line2.charAt(0) == ';')
							break;
						txt += "\n" +  line2;
					}
					t.setText(txt);
				} else if (line.charAt(0) == '+') {
//						System.out.println(line);
					Text t = new Text();
					t.setStyle("-fx-font-size:16px; -fx-font-family: Monospace;");
					String[] arr = line.split(":");
					Label lbl = new Label(arr[0].substring(1) + ":");
					lbl.setUserData(arr[0].substring(1));
					lbl.setStyle("-fx-font-size:16px; -fx-font-weight: bold;");
					hb1 = new HBox();
					hb1.setSpacing(4.0);
					hb1.setPadding(inset1);
					hb1.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb2 = new HBox();
					hb2.setSpacing(4.0);
					hb2.setPadding(inset2);
					hb2.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(t, Priority.ALWAYS);
					
					hb1.getChildren().add(lbl);
					hb2.getChildren().add(t);
					vb2.getChildren().addAll(hb1, hb2);
					
					String txt = arr[1];
					String line2 = null;
					while ((line2 = reader.readLine()) != null) {
						line2 = line2.trim();
						if (line2.charAt(0) == ';')
							break;
						txt += "\n" +  line2;
					}
					t.setText(txt);
				}
			}
			reader.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void refreshScene() {
		
	}

	@Override
	public void leaveScene() {
		
	}

	@Override
	public void clickIt(String text) {
		
	}
}
