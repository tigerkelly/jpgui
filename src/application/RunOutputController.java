package application;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RunOutputController implements Initializable {

    @FXML
    private AnchorPane aPane;

    @FXML
    private Button btnClose;
    
    @FXML
    private Button btnRun;
    
    @FXML
    private TextArea taOutput;
    
    private ProgressBar pbRun;
    
    @FXML
    private HBox hbox;
    
//    private JpGlobal jg = JpGlobal.getInstance();
    private String[] cmd = null;

    @FXML
    void doBtnClose(ActionEvent event) {
    	Stage stage = (Stage)aPane.getScene().getWindow();
    	stage.close();
    }
    
    @FXML
    void doBtnRun(ActionEvent event) {
    	if (cmd != null) {
    		hbox.getChildren().remove(pbRun);
    		
    		pbRun = new ProgressBar();
        	
        	pbRun.prefWidthProperty().bind(hbox.widthProperty());
        	
        	hbox.getChildren().add(pbRun);
    		
    		btnRun.setDisable(true);
    		
    		Process p = null;
        	ProcessBuilder pb = new ProcessBuilder();
        	Map<String, String> env = pb.environment();
        	env.put("JpGui", "true");
        	
    		pb.redirectErrorStream(true);
    		pb.command(cmd);

    		try {
    			setButtons(true);
    			p = pb.start();
    			inheritIO(p.getInputStream(), taOutput);
    		} catch (IOException e1) {
    			e1.printStackTrace();
    		}
    	}
    }
    
    private void inheritIO(final InputStream src, final TextArea ta) {
        new Thread(new Runnable() {
            public void run() {
                Scanner sc = new Scanner(src);
                while (sc.hasNextLine()) {
                	String s = sc.nextLine();
                    ta.appendText(s + "\n");
                    if (s.contains("FINISHED:") == true || s.contains("FAILED:") == true) {
                    	setButtons(false);
                    }
                }
                sc.close();
                
                Platform.runLater(() -> {
                	 hbox.getChildren().remove(pbRun);
                });
            }
        }).start();
    }
    
    private void setButtons(boolean flag) {
//    	btnRun.setDisable(flag);
		btnClose.setDisable(flag);
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	
	}
    
    public Stage getStage() {
    	return (Stage)aPane.getScene().getWindow();
    }
    
    public void setCmd(String[] cmd) {
    	this.cmd = cmd;
    	
    	taOutput.setText("");
    	String line = null;
    	for (String s : cmd) {
    		if (line == null)
    			line = s;
    		else
    			line += " " + s;
    	}
    	line += "\n";
    	
    	taOutput.setText(line);
    }
}

