package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ScriptEditController implements Initializable, RefreshScene {

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblScriptName;

    @FXML
    private TextArea taScript;
    
    private JpGlobal jg = JpGlobal.getInstance();
    private File sf = null;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnSave(ActionEvent event) {
    	if (sf != null) {
    		try {
				FileWriter w = new FileWriter(sf.getAbsolutePath(), false);
				
				w.write(taScript.getText());
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
    
    public void setScript(File scriptFile) {
    	lblScriptName.setText(scriptFile.getName());
    	
//    	System.out.println(scriptFile.getAbsolutePath());
    	this.sf = scriptFile;
  
		try {
			FileInputStream fis = new FileInputStream(scriptFile);
			byte[] data = new byte[(int) scriptFile.length()];
	    	fis.read(data);
	    	fis.close();
	    	String str = new String(data, "UTF-8");
	    	
	    	taScript.setText(str);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public Stage getStage() {
    	return (Stage)aPane.getScene().getWindow();
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
