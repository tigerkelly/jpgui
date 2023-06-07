package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ReqPkgController implements Initializable, RefreshScene {

	@FXML
    private AnchorPane aPane;
	
	@FXML
    private Button btnClose;

    @FXML
    private TextArea taText;

    @FXML
    void doBtnClose(ActionEvent event) {
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
	    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
			taText.setText("Windows:\n");
			taText.appendText("  For the Windows OS you need the following installed.\n");
			taText.appendText("  The WiX Toolset 3.0 or greater.\n");
			taText.appendText("  From the following URL. ");
			taText.appendText("    https://wixtoolset.org\n");
			taText.appendText("  The Java JDK 8.0 or greater version of java.\n\n");

			taText.appendText("Linux:\n");
			taText.appendText("  For Unix like OSes you will need the following installed.\n");
			taText.appendText("  On Linix: RPM and DEB\n");
			taText.appendText("  On Red Hat Linux: rpm-build package.\n");
			taText.appendText("  On Ubuntu: fakeroot package.\n");
			taText.appendText("  The Java JDK 8.0 or greater version of java.\n\n");
			
			taText.appendText("MAC:\n");
			taText.appendText("  For Apple MAC you will need the following installed.\n\n");
			taText.appendText("  The Xcode command line tools when the --mac-sign and/or the -icon options are used.\n");
			taText.appendText("  The Java JDK 8.0 or greater version of java.\n\n");
			
			taText.appendText("For all:\n");
			taText.appendText("For all platforms thier are basic options that are required.\n");
			taText.appendText("  Package Type:--type\n");
			taText.appendText("  App Version:--app-version\n");
			taText.appendText("  App Name:--name\n");
			taText.appendText("  Destination:--dest\n");
			taText.appendText("  Package Type:--type\n");
			taText.appendText("  Runtime Image:--runtime-image\n");
			taText.appendText("  Input:--input\n");
			taText.appendText("  Main Class:--main-class\n");
			taText.appendText("  Main Jar:--main-jar or Module:--module\n");
			taText.appendText("There are probably more that are system dependent.\n\n");
			
			taText.appendText("The versions used for jpackage are:\n");
			taText.appendText("  Windows jpackage 19.0.2\n");
			taText.appendText("  Linux jpackage 20.0.1\n");
			taText.appendText("  MAC jpackage 20.0.1\n\n");
			
			taText.selectHome();
			taText.deselect();
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
