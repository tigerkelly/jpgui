package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
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
    private TextField tfJpackage;
    
    @FXML
    private TextField tfPath;
    
    @FXML
    private TextArea taUserMods;
    
    @FXML
    private CheckBox chbRemoveAll;
    
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
			tfJpackage.setText(fs.getAbsolutePath());
		}
    }
    
    @FXML
    void doBtnSave(ActionEvent event) {
    	String path = tfJpackage.getText();
    	if (path == null || path.isBlank() == true) {
    		jg.sysIni.addValuePair("System", "jpackage", "jpackage");
    	} else {
    		jg.sysIni.addValuePair("System", "jpackage", path);
    	}
    	
    	String mp = tfPath.getText();
    	if (mp != null) {
    		jg.sysIni.addValuePair("System", "modulepath", mp);
    		jg.modulePath = mp;
    	} else {
    		jg.modulePath = null;
    	}
    	
    	boolean rmAll = chbRemoveAll.isSelected();
    	
    	jg.sysIni.addValuePair("System", "removeall", Boolean.toString(rmAll));
    	
    	int idx = 0;
    	Object[] objs = jg.sysIni.getSectionKeys("UserMods");
    	if (objs != null) {
    		for (Object o : objs) {
    			int n = Integer.parseInt((String)o);
    			if (n > idx)
    				idx = n;
    		}
    	}
    	
    	idx++;
    	
    	jg.sysIni.removeSection("UserMods");
    	jg.sysIni.addSection("UserMods");
    	
    	String txt = taUserMods.getText();
    	if (txt.isBlank() == false) {
    		String[] arr = txt.split("\n");
    		
    		for (String s : arr) {
    			jg.sysIni.addValuePair("UserMods", idx + "", s);
    			idx++;
    		}
    	}
    	
    	jg.jpackagePath = path;
    	
    	jg.sysIni.writeFile(true);
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    void doBtnPath(ActionEvent event) {
    	DirectoryChooser dc = new DirectoryChooser();
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
		File df = dc.showDialog(stage);
		String mp = jg.sysIni.getString("System", "modulepath");
		if (mp!= null)
			dc.setInitialDirectory(new File(mp));
		if (df != null) {
			String txt = tfPath.getText();
			if (txt.isBlank() == true)
				txt = df.getAbsolutePath();
			else
				txt += ";" + df.getAbsolutePath();
			tfPath.setText(txt);
		}
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String path = jg.sysIni.getString("System", "jpackage");
		if (path == null || path.isBlank() == true)
			tfJpackage.setText("jpackage");
		else
			tfJpackage.setText(path);
		
		String mp = jg.sysIni.getString("System", "modulepath");
		if (mp != null)
			tfPath.setText(mp);
		
		if (jg.sysIni.keyExists("System", "removeall") == true) {
			boolean rmAll = jg.sysIni.getBoolean("System", "removeall");
			chbRemoveAll.setSelected(rmAll);
		}
		
		taUserMods.setText("");
		Object[] objs = jg.sysIni.getSectionKeys("UserMods");
		if (objs != null) {
			for (Object o : objs) {
				String v = jg.sysIni.getString("UserMods", o);
				taUserMods.appendText(v + "\n");
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
