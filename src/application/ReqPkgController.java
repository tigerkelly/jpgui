package application;

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

public class ReqPkgController implements Initializable, RefreshScene {

	@FXML
    private AnchorPane aPane;
	
	@FXML
    private Button btnClose;

    @FXML
    private Label lblFor;

    @FXML
    private TextArea taText;

    @FXML
    void doBtnClose(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
	    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win") == true) {
			lblFor.setText("for Windows.");
			taText.setText("For the Windows OS you need the following installed.\n");
			taText.appendText("The WiX Toolset 3.0 or greater.\n");
			taText.appendText("From the following URL. ");
			taText.appendText("https://wixtoolset.org\n");
			taText.appendText("The Java JDK 8.0 or greater version of java.\n");
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			lblFor.setText("for Unix like.");
			taText.setText("For Unix like OSes you will need the following installed.\n");
			taText.appendText("On Linix: RPM and DEB\n");
			taText.appendText("On Red Hat Linux: rpm-build package.\n");
			taText.appendText("On Ubuntu: fakeroot package.\n");
			taText.appendText("The Java JDK 8.0 or greater version of java.\n");
		} else if (os.contains("mac") == true) {
			lblFor.setText("for Apple.");
			lblFor.setText("For Apple MAC you will need the following installed.\n\n");
			taText.appendText("The Xcode command line tools when the --mac-sign and/or the -icon options are used.\n");
			taText.appendText("The Java JDK 8.0 or greater version of java.\n");
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
