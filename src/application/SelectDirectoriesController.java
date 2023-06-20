package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SelectDirectoriesController implements Initializable, RefreshScene {

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private ListView<String> lvDirs;
    
    private JpGlobal jg = JpGlobal.getInstance();
    private ObservableList<String> dirs = null;

    @FXML
    void doBtnAdd(ActionEvent event) {
    	DirectoryChooser dc = new DirectoryChooser();
    	Stage stage = (Stage) aPane.getScene().getWindow();
		File df = dc.showDialog(stage);
		if (df != null) {
			if (dirs.contains(df.getAbsolutePath()) == false)
				dirs.add(df.getAbsolutePath());
		}
		
//		lvDirs.setItems(dirs);
    }
    
    @FXML
    void doBtnDelete(ActionEvent event) {
    	String item = lvDirs.getSelectionModel().getSelectedItem();
    	if (item == null)
    		return;
    	
    	dirs.remove(item);
    }

    @FXML
    void doBtnCancel(ActionEvent event) {
    	jg.dirsSelected = null;
    	Stage stage = (Stage) aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {
    	jg.dirsSelected = null;
    	for (String s : dirs) {
    		if (jg.dirsSelected == null)
    			jg.dirsSelected = s;
    		else
    			jg.dirsSelected += ";" + s;
    	}
    	Stage stage = (Stage) aPane.getScene().getWindow();
    	stage.close();
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dirs = FXCollections.observableArrayList();
		
		lvDirs.setItems(dirs);
	}
	
	public Stage getStage() {
    	return (Stage)aPane.getScene().getWindow();
    }
	
	public void setData(String txt) {
		String[] arr = txt.split(";");
		for (String s : arr)
			dirs.add(s);
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