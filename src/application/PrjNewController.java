package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PrjNewController implements Initializable, RefreshScene {

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane aPane;
    
    @FXML
    private TextArea taProjectDesc;

    @FXML
    private TextField tfProjectName;
    
    private JpGlobal jg = JpGlobal.getInstance();
    
    @FXML
    void doBtnCancel(ActionEvent event) {
    	jg.addStatus("canceled");
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {
    	String prjName = tfProjectName.getText();
    	String prjDesc = taProjectDesc.getText();
    	
    	if (prjName.isBlank() == true)
    		return;
    	
    	if (jg.sysIni.keyExists("Projects", prjName) == true) {
    		jg.showAlert("Name Error", "That project name already exists.", AlertType.ERROR, false);
    		return;
    	}
    	
    	jg.sysIni.addValuePair("Projects", prjName, prjDesc);
    	
    	File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
    					"projects" + File.separator + prjName + ".ini");
    	
    	if (f.exists() == false) {
//    		try {
//				f.createNewFile();
				
				try {
					FileWriter myWriter = new FileWriter(f.getAbsolutePath(), false);
					myWriter.write("# Jpackage project builder INI file.\n\n[System]\n");
					myWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				jg.sysIni.removeValuePair("Projects", prjName);
//			}
    	}
    	
    	jg.sysIni.writeFile(true);
    	
    	jg.addStatus(prjName);
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
