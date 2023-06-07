package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OptionsController implements Initializable, RefreshScene {

	@FXML
	private AnchorPane aPane;
	
	@FXML
    private Button btnCacnel;

    @FXML
    private Button btnSave;
    
    @FXML
    private Button btnSelect;

    @FXML
    private TextField tfJpackagePath;
    
    private JpGlobal jg = JpGlobal.getInstance();

    @FXML
    void doBtnCancel(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void doBtnSelect(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
		File fs = fc.showOpenDialog(stage);
		if (fs != null) {
			tfJpackagePath.setText(fs.getAbsolutePath());
		}
    }
    
    @FXML
    void doBtnSave(ActionEvent event) {
    	String path = tfJpackagePath.getText();
    	if (path == null || path.isBlank() == true) {
    		jg.sysIni.addValuePair("System", "path", "jpackage");
    	} else {
    		jg.sysIni.addValuePair("System", "path", path);
    	}
    	
    	jg.jpackagePath = path;
    	
    	jg.sysIni.writeFile(true);
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
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
