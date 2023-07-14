package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.rkw.IniFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
    	
    	File f1 = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
    					"projects" + File.separator + prjName);
    	if (f1.exists() == false) {
    		f1.mkdirs();
    	}
    	File prjIniFile = new File(f1.getAbsolutePath() + File.separator + prjName + ".ini");
    	
    	if (prjIniFile.exists() == false) {	
			try {
				FileWriter myWriter = new FileWriter(prjIniFile.getAbsolutePath(), false);
				myWriter.write("# Jpackage project builder INI file.\n\n");
				myWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	File iWin = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
				"projects" + File.separator + prjName + File.separator + "win_in");
    	if (iWin.exists() == false)
    		iWin.mkdirs();
    	
    	File keepDir = new File(iWin.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File oWin = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
				"projects" + File.separator + prjName + File.separator + "win_out");
    	if (oWin.exists() == false)
    		oWin.mkdirs();
    	
    	keepDir = new File(oWin.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File iLinux = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
				"projects" + File.separator + prjName + File.separator + "linux_in");
    	if (iLinux.exists() == false)
    		iLinux.mkdirs();
    	
    	keepDir = new File(iLinux.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File oLinux = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
				"projects" + File.separator + prjName + File.separator + "linux_out");
    	if (oLinux.exists() == false)
    		oLinux.mkdirs();
    	
    	keepDir = new File(oLinux.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File iMac = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
				"projects" + File.separator + prjName + File.separator + "mac_in");
    	if (iMac.exists() == false)
    		iMac.mkdirs();
    	
    	keepDir = new File(iMac.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File oMac = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
				"projects" + File.separator + prjName + File.separator + "mac_out");
    	if (oMac.exists() == false)
    		oMac.mkdirs();
    	
    	keepDir = new File(oMac.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	IniFile prjIni = new IniFile(prjIniFile.getAbsolutePath());
    	
    	prjIni.addSection("JpGui Options");
    	prjIni.addSection("Generic Options");
    	prjIni.addSection("Runtime Image Options");
    	prjIni.addSection("Application Image Options");
    	prjIni.addSection("Application Launcher(s) Options");
    	prjIni.addSection("Platform Dependent Launcher Options");
    	prjIni.addSection("Application Package Options");
    	prjIni.addSection("Platform Dependent Package Options");
    	prjIni.writeFile(true);
    	
    	jg.sysIni.writeFile(true);
    	
    	jg.addStatus(prjName);
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
	    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		TextFormatter<?> formatter = new TextFormatter<>((TextFormatter.Change change) -> {
		    String text = change.getText();

		    // if text was added, fix the text to fit the requirements
		    if (!text.isEmpty()) {
		        String newText = text.replace(" ", "");

		        int carretPos = change.getCaretPosition() - text.length() + newText.length();
		        change.setText(newText);

		        // fix carret position based on difference in originally added text and fixed text
		        change.selectRange(carretPos, carretPos);
		    }
		    return change;
		});
		
		tfProjectName.setTextFormatter(formatter);
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
