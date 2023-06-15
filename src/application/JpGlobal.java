package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.rkw.IniFile;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JpGlobal {

	private static JpGlobal singleton = null;
	
	private JpGlobal() {
		initGlobals();
	}
	
	private void initGlobals() {
		appVersion = "1.1.2";
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win") == true) {
			osType = 1;
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			osType = 2;
		} else if (os.contains("mac") == true) {
			osType = 3;
		}
		
		String d = System.getProperty("user.home");
		
		workDir = new File(d + File.separator + "JpGui" + File.separator + "projects");
		
		if (workDir.exists() == false) {
			workDir.mkdirs();
		}
		
		File f = new File(workDir.getAbsolutePath() + File.separator + "jpgui.ini");
		if (f.exists() == false) {
			try {
				f.createNewFile();
				
				try {
					FileWriter myWriter = new FileWriter(f.getAbsolutePath());
					myWriter.write("# Jpackage project builder INI file.\n\n[System]\n");
					myWriter.write("\tjpackage = jpackage\n");
					myWriter.write("\twinBaseDir =\n");
					myWriter.write("\tlinuxBaseDir =\n");
					myWriter.write("\tmacBaseDir =\n");
						
					myWriter.write("[Projects]\n\n");
					myWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		sysIni = new IniFile(f.getAbsolutePath());
		
		jpackagePath = sysIni.getString("System", "jpackage");
		if (jpackagePath == null || jpackagePath.isBlank() == true)
			jpackagePath = "jpackage";
		
		String winBase = sysIni.getString("System", "Win basedir");
		if (winBase != null)
			winBaseDir = new File(winBase);
		
		String linuxBase = sysIni.getString("System", "Linux basedir");
		if (linuxBase != null)
			linuxBaseDir = new File(linuxBase);
		
		String macBase = sysIni.getString("System", "Mac basedir");
		if (macBase != null)
			macBaseDir = new File(macBase);
		
		prjList = new HashMap<String, IniFile>();
		orgList = new HashMap<String, IniFile>();
		
		prjList.clear();
		orgList.clear();
		
		sceneNav = new SceneNav();
		
		String cmd = jpackagePath + " --version";
		ProcessRet pr = runProcess(cmd.split(" "));
		jpackageVersion = pr.getOutput();
	}
	
	public String appVersion = null;
	public Map<String, IniFile> prjList = null;
	public Map<String, IniFile> orgList = null;
	
	public boolean loadFlag = false;
	public boolean leaveProgram = false;
	
	Alert alert = null;
	public IniFile sysIni = null;
	public IniFile currPrj = null;
	public SceneNav sceneNav = null;
	public File workDir = null;
	public File winBaseDir = null;
	public File linuxBaseDir = null;
	public File macBaseDir = null;
	public int osType = 0;
	
	public String projectOpen = null;
	
	public String jpackageVersion = null;
	public String jpackagePath = null;
	public String platform = null;
	
	public Label status = null;
	
	public static JpGlobal getInstance() {
		// return SingletonHolder.singleton;
		if (singleton == null) {
			synchronized (JpGlobal.class) {
				singleton = new JpGlobal();
			}
		}
		return singleton;
	}
	
	public String scenePeek() {
		if (sceneNav.sceneQue == null || sceneNav.sceneQue.isEmpty())
			return SceneNav.JPGUI;
		else
			return sceneNav.sceneQue.peek();
	}

	public void guiRestart(String msg) {
		String errMsg = String.format("A GUI error occurred.\r\nError loading %s\r\n\r\nRestarting GUI.", msg);
		showAlert("GUI Error", errMsg, AlertType.CONFIRMATION, false);
		System.exit(1);
	}

	public void loadSceneNav(String fxml) {
		if (sceneNav.loadScene(fxml) == true) {
			guiRestart(fxml);
		}
	}
	
	public void loadPcb() {
		
	}
	
	public void closeAlert() {
		if (alert != null) {
			alert.close();
			alert = null;
		}
	}
	
	public void addStatus(String msg) {
		if (status == null)
			return;
		String current = status.getText();
		status.setText(current + ", " + msg);
	}
	
	public void setStatus(String msg) {
		if (status == null)
			return;
		status.setText(msg);
	}
	
	public ButtonType yesNoAlert(String title, String msg, AlertType alertType) {
		ButtonType yes = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType no = new ButtonType("No", ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(alertType, msg, yes, no);
		alert.getDialogPane().setPrefWidth(500.0);
		alert.setTitle(title);
		alert.setHeaderText(null);
		
		for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(bt);
			button.setStyle("-fx-font-size: 16px;");
			button.setPrefWidth(100.0);
		}
		
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get();
	}

	public ButtonType showAlert(String title, String msg, AlertType alertType, boolean yesNo) {
		alert = new Alert(alertType);
		alert.getDialogPane().setPrefWidth(500.0);
		for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(bt);
			if (yesNo == true) {
				if (button.getText().equals("Cancel"))
					button.setText("No");
				else if (button.getText().equals("OK"))
					button.setText("Yes");
			}
			button.setStyle("-fx-font-size: 16px;");
			button.setPrefWidth(100.0);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);

		alert.setContentText(msg);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		ButtonType bt = alert.showAndWait().get();

		alert = null;

		return bt;
	}
	
	public void showOutput(String title, String msg, AlertType alertType, boolean yesNo) {
		alert = new Alert(alertType);
		alert.getDialogPane().setPrefWidth(750.0);
		alert.getDialogPane().setPrefHeight(450.0);
		for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
			Button button = (Button) alert.getDialogPane().lookupButton(bt);
			if (yesNo == true) {
				if (button.getText().equals("Cancel"))
					button.setText("No");
				else if (button.getText().equals("OK"))
					button.setText("Yes");
			}
			button.setStyle("-fx-font-size: 16px;");
			button.setPrefWidth(100.0);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);
	
		TextArea txt = new TextArea(msg);
		txt.setStyle("-fx-font-size: 16px;");
		txt.setWrapText(true);

		alert.getDialogPane().setContent(txt);
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("myDialogs.css").toExternalForm());
		dialogPane.getStyleClass().add("myDialog");

		alert.showAndWait().get();

		alert = null;
	}

	public void Msg(String msg) {
		System.out.println(msg);
	}
	
	public boolean copyFile(File in, File out) {
		
		try {
	        FileInputStream fis  = new FileInputStream(in);
	        FileOutputStream fos = new FileOutputStream(out);
	        byte[] buf = new byte[4096];
	        int i = 0;
	        while((i=fis.read(buf))!=-1) {
	            fos.write(buf, 0, i);
	        }
	        fis.close();
	        fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		
		return false;
    }
	
	public void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            deleteDir(f);
	        }
	    }
	    file.delete();
	}
	
	public boolean isDigit(String s) {
		boolean tf = true;
		
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i)) == false) {
				tf = false;
				break;
			}
		}
		
		return tf;
	}
	
	public void centerScene(Node node, String fxml, String title, String data) {
		FXMLLoader loader = null;
		try {
			Stage stage = new Stage();
			stage.setTitle(title);

			loader = new FXMLLoader(getClass().getResource(fxml));

			stage.initModality(Modality.APPLICATION_MODAL);

			stage.setScene(new Scene(loader.load()));
			stage.hide();

			Stage ps = (Stage) node.getScene().getWindow();

			ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
				double stageWidth = newValue.doubleValue();
				stage.setX(ps.getX() + ps.getWidth() / 2 - stageWidth / 2);
			};
			ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
				double stageHeight = newValue.doubleValue();
				stage.setY(ps.getY() + ps.getHeight() / 2 - stageHeight / 2);
			};

			stage.widthProperty().addListener(widthListener);
			stage.heightProperty().addListener(heightListener);

			// Once the window is visible, remove the listeners
			stage.setOnShown(e2 -> {
				stage.widthProperty().removeListener(widthListener);
				stage.heightProperty().removeListener(heightListener);
			});

			stage.showAndWait();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public ButtonInfo centerDialog(Node node, String title, String msg, Image icon, ButtonInfo[] buttons) {
		FXMLLoader loader = null;
		YesNoOKController yno = null;
		try {
			Stage stage = new Stage();
			stage.setTitle(title);

			loader = new FXMLLoader(getClass().getResource("YesNoOK.fxml"));

			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.UTILITY);
			stage.setAlwaysOnTop(true);

			stage.setScene(new Scene(loader.load()));
			stage.hide();

			Stage ps = (Stage) node.getScene().getWindow();
			
			yno = (YesNoOKController)loader.getController();
			if (title != null)
				yno.setTitle(title);
			if (msg != null)
				yno.setMessage(msg);
			if (icon != null)
				yno.setImage(icon);
			if (buttons != null)
				yno.addButtons(buttons);
			
//			for (ButtonInfo bi : buttons) {
//				yno.addButton(bi.getText(), bi.getReValue(), bi.isDefaultButton());
//			}

			ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
				double stageWidth = newValue.doubleValue();
				stage.setX(ps.getX() + ps.getWidth() / 2 - stageWidth / 2);
			};
			ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
				double stageHeight = newValue.doubleValue();
				stage.setY(ps.getY() + ps.getHeight() / 2 - stageHeight / 2);
			};

			stage.widthProperty().addListener(widthListener);
			stage.heightProperty().addListener(heightListener);

			// Once the window is visible, remove the listeners
			stage.setOnShown(e2 -> {
				stage.widthProperty().removeListener(widthListener);
				stage.heightProperty().removeListener(heightListener);
			});

			stage.showAndWait();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return yno.getAction();
	}
	
	public ProcessRet runProcess(String[] args) {
    	Process p = null;
    	ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream(true);
		pb.command(args);

		try {
			p = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

//		@SuppressWarnings("resource")
		StreamGobbler inGobbler = new StreamGobbler(p.getInputStream(), true);
		inGobbler.start();

		int ev = 0;
		try {
			ev = p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return new ProcessRet(ev, inGobbler.getOutput());
    }
}
