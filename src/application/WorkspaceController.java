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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class WorkspaceController implements Initializable {

	private JpGlobal jg = JpGlobal.getInstance();
	
    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnPath;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> cbPath;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	jg.workspace = null;
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnPath(ActionEvent event) {
    	DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        
        Stage stage = (Stage)aPane.getScene().getWindow();
        
        File sd = dc.showDialog(stage);
        
        if (sd != null) {
        	jg.workspace = sd;
        	cbPath.setValue(sd.getAbsolutePath());
        }
    }

    @FXML
    void soBtnSave(ActionEvent event) {
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	
    	String p = cbPath.getValue();
    	if (p == null || p.isBlank() == true)
    		return;
    	
    	jg.workspace = new File(p);
    	
    	if (jg.workspace.exists() == false) {
    		jg.workspace.mkdirs();
    	}
    	
    	Object[] objs = jg.wsIni.getSectionKeys("Workspaces");
		int idx = 0;
		if (objs != null) {
			// test if path already exist in list.
			boolean flag = false;
			for (Object o : objs) {
				p = jg.wsIni.getString("Workspaces", o);
				if (p.equals(jg.workspace.getAbsolutePath()) == true) {
					flag = true;
					break;
				}
			}
			
			if (flag == false) {
    			for (Object o : objs) {
    				int n = Integer.parseInt((String)o);
    				if (n > idx)
    					idx = n;
    			}
    			idx++;
    			
    			jg.wsIni.addValuePair("Workspaces", idx + "", jg.workspace.getAbsolutePath());
			}
		}
		
    	jg.setupProject();
    	
    	jg.wsIni.addValuePair("Current", "workspace", jg.workspace.getAbsolutePath());
		jg.wsIni.writeFile(true);
    	
    	stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String p = jg.wsIni.getString("Current", "workspace");
		if (p == null || p.isBlank() == true)
			cbPath.setValue(System.getProperty("user.home") + File.separator + "JpGui-ws");
		else
			cbPath.setValue(p);
		
		ObservableList<String> lst = FXCollections.observableArrayList();
		
		Object[] objs = jg.wsIni.getSectionKeys("Workspaces");
		if (objs != null) {
			for (Object o : objs) {
				p = jg.wsIni.getString("Workspaces", o);
				lst.add(p);
			}
		}
		
		cbPath.setItems(lst);
	}
	
	public Stage getStage() {
		return (Stage)aPane.getScene().getWindow();
	}

}