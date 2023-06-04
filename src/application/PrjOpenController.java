package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PrjOpenController implements Initializable, RefreshScene {
	
	@FXML
    private AnchorPane aPane;

    @FXML
    private ListView<String> lvProjects;
    
    private JpGlobal jg = JpGlobal.getInstance();

	@FXML
    void doBtnClose(ActionEvent event) {
		jg.addStatus("canceled");
		jg.projectOpen = null;
		Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void doBtnOpen(ActionEvent event) {
    	
    	String selected = lvProjects.getSelectionModel().getSelectedItem();
    	if (selected == null || selected.isBlank() == true)
    		return;
    	
    	String[] arr = selected.split(" - ");
    	
    	jg.projectOpen = arr[0].trim();
    	
    	jg.addStatus(jg.projectOpen);
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	ObservableList<String> lvList = FXCollections.observableArrayList();
		Object[] prjs = jg.sysIni.getSectionKeys("Projects");
		
		for (Object o : prjs) {
			String prj = (String)o;
			String d = jg.sysIni.getString("Projects", prj);
			
			lvList.add(prj + " - " + d);
		}
		
		lvProjects.setItems(lvList);
		
		lvProjects.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					doBtnOpen(null);
				}
			}
		});
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
