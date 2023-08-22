package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AddModulesController implements Initializable, RefreshScene {

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSelect;

    @FXML
    private ListView<CheckBox> lvModules;
    
    @FXML
    private ListView<CheckBox> lvUserMods;
    
    private JpGlobal jg = JpGlobal.getInstance();
    private ObservableList<CheckBox> mods = null;
    private ObservableList<CheckBox> userMods = null;

    @FXML
    void doBtnCancel(ActionEvent event) {
    	jg.modulesSelected = null;
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

    @FXML
    void doBtnSelect(ActionEvent event) {
    	jg.modulesSelected = null;
    	for (CheckBox ckb : mods) {
    		if (ckb.isSelected() == true) {
    			if (jg.modulesSelected == null)
    				jg.modulesSelected = ckb.getText();
    			else
    				jg.modulesSelected += "," + ckb.getText();
    		}
    	}
    	for (CheckBox ckb : userMods) {
    		if (ckb.isSelected() == true) {
    			if (jg.modulesSelected == null)
    				jg.modulesSelected = ckb.getText();
    			else
    				jg.modulesSelected += "," + ckb.getText();
    		}
    	}
//    	System.out.println(jg.modulesSelected);
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mods = FXCollections.observableArrayList();
		userMods = FXCollections.observableArrayList();
		try (InputStream in = getClass().getResourceAsStream("/resources/modules.txt");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		    
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isBlank() == true) {
					continue;
				}
				
				if (line.startsWith("##") == true) {		// skip comments
					continue;
				}
				
				CheckBox ckb = new CheckBox(line);
				mods.add(ckb);
			}
			reader.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Object[] objs = jg.sysIni.getSectionKeys("UserMods");
		if (objs != null) {
			for (Object o : objs) {
				String v = jg.sysIni.getString("UserMods", o);
				CheckBox ckb = new CheckBox(v);
				userMods.add(ckb);
			}
		}
		
		lvModules.setItems(mods);
		
		
		lvUserMods.setItems(userMods);
	}
	
	public Stage getStage() {
    	return (Stage)aPane.getScene().getWindow();
    }
	
	public void setData(String modules) {
		if (modules == null)
			return;
		
		String[] mm = modules.split(",");
		
		for (CheckBox ckb : mods) {
			for (String s : mm) {
				if (ckb.getText().equalsIgnoreCase(s) == true)
					ckb.setSelected(true);
			}
		}
		
		for (CheckBox ckb : userMods) {
			for (String s : mm) {
				if (ckb.getText().equalsIgnoreCase(s) == true)
					ckb.setSelected(true);
			}
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
