package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import com.rkw.IniFile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class JpGuiController implements Initializable{

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

    @FXML
    private AnchorPane aPane;
    
    @FXML
    private HBox hbTitle;

    @FXML
    private Label lblTitle;
    
    @FXML
    private Label lblOs;
    
    @FXML
    private Label lblStatus;
    
    @FXML
    private Label lblVersion;

    @FXML
    private TabPane tabPane;
    
    @FXML
    private MenuItem mActionRun;

    @FXML
    private MenuItem mActionScript;

    @FXML
    private MenuItem mFileNewProject;
    
    @FXML
    private MenuItem mFileNewProjectWizard;

    @FXML
    private MenuItem mFileOpenProject;
    
    @FXML
    private MenuItem mFileDeleteProject;
    
    @FXML
    private MenuItem mFileExportProject;

    @FXML
    private MenuItem mFileImportProject;
    
    @FXML
    private MenuItem mFileSwitch;

    @FXML
    private MenuItem mFileQuit;

    @FXML
    private MenuItem mFileSaveAllProjects;

    @FXML
    private MenuItem mFileSaveProject;

    @FXML
    private MenuItem mHelpAbout;

    @FXML
    private MenuItem mHelpHelp;

    @FXML
    private MenuItem mHelpRequired;
    
    @FXML
    private MenuItem mScriptsEditPreRun;

    @FXML
    private MenuItem mScriptsEditPstRun;
    
    @FXML
    private MenuItem mScriptsEditMain;
    
    @FXML
    private MenuItem mSetOptions;
    
    @FXML
    private ComboBox<String> cbPlatform;

    @FXML
    private Menu menuPlatform;
    
    @FXML
    private Menu mOpenRecent;

    @FXML
    private Menu menuAction;

    @FXML
    private Menu menuFile;

    @FXML
    private Menu menuHelp;
    
    @FXML
    private Menu menuScripts;
    
    @FXML
    private Button btnQuit;
    
    @FXML
    private Button btnWorkspace;
    
    @FXML
    private Tooltip ttWorkspace;
    
    private JpGlobal jg = JpGlobal.getInstance();
    ObservableList<String> platforms = null;
    ObservableList<MenuItem> recents = null;
    Popup popup = null;
    DirectoryChooser dc = null;
    FileChooser fc = null;
    FileChooser zfc = null;
    int lastBtn = -1;
    String os = null;
    
    final int MAX_RECENTS = 8;
    
    @FXML
    void doSetOptions(ActionEvent event) {
    	jg.setStatus("Setting Options");
    	jg.centerScene(aPane, "Options.fxml", "Setting Options", null);
    }
    
    @FXML
    void doFileExportProject(ActionEvent event) {
    	jg.setStatus("Export Project");
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	
    	if (tab == null) {
    		jg.addStatus("No tab selected.");
    		return;
    	}
    	
    	String prjName = tab.getText();
    	String desc = jg.sysIni.getString("Projects", prjName);
    	String prjDir = jg.workDir.getAbsolutePath() + File.separator + prjName;
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
		File df = dc.showDialog(stage);
		if (df == null) {
			return;
		}
    	
		try {
			// Create file for project description.  Will be deleted on import.
			FileWriter fw = new FileWriter(jg.workDir.getAbsolutePath() + 
					File.separator + prjName + File.separator + "prj_desc.txt", false);
			fw.write(tab.getText() + "=" + desc + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String dirPath = df.getAbsolutePath() + File.separator + prjName + ".zip";
		
		try {
			jg.zipFile(prjName, prjDir, dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		jg.addStatus("Done...");
    }

    @FXML
    void doFileImportProject(ActionEvent event) {
    	jg.setStatus("Import Project");
    	Stage stage = (Stage) aPane.getScene().getWindow();
    	zfc.getExtensionFilters().addAll(
		     new FileChooser.ExtensionFilter("Zip Files", "*.zip"),
		     new FileChooser.ExtensionFilter("All Files", "*.*")
		);
    	File df = zfc.showOpenDialog(stage);
		if (df != null) {
//			System.out.println(df.getAbsolutePath());
			String t = df.getName();
			int x = t.lastIndexOf('.');
			String name = t.substring(0, x);
			jg.unzip(df.getAbsolutePath(), jg.workDir.getAbsolutePath() + File.separator + name);
			
			File f = new File(jg.workDir.getAbsolutePath() + File.separator + name + File.separator + "prj_desc.txt");
			if (f.exists() == false)
				return;
			
			String[] arr = null;
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String st = br.readLine();
				arr = st.split("=");
				
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (arr == null)
				return;
			
			jg.sysIni.addValuePair("Projects", arr[0], arr[1]);
			jg.sysIni.writeFile(true);
			
			jg.addStatus("Done...");
		} else {
			jg.addStatus("Canceled");
		}
    }
    
    @FXML
    void doFileSwitch(ActionEvent event) {
    	jg.centerScene(aPane, "Workspace.fxml", "Switch Workspace", null);
    	
    	if (jg.workspace != null) {
    		btnWorkspace.setText(jg.workspace.getName());
    		ttWorkspace.setText("Switch Workspace:\n" + jg.workspace.getAbsolutePath());
    	}
    }
    
    @FXML
    void doActionRun(ActionEvent event) {
    	
    	int os = 0;
    	switch(jg.platform) {
    	case "Win":
    		os = 1;
    		break;
    	case "Linux":
    		os = 2;
    		break;
    	case "Mac":
    		os = 3;
    		break;
		default:
			os = 0;
			break;
    	}
    	
    	if (jg.osType != os) {
    		jg.showAlert("User Error", "Can not run script, not on matching platform.", AlertType.ERROR, false);
    		return;
    	}
    	
    	jg.setStatus("Run Project");
    	File script = buildScript();
    	
    	String v = jg.currPrj.getString("Generic Options", jg.platform + " App Version");
    	if (v == null || v.isBlank() == true) {
    		jg.showAlert("User Error", "No 'App Version' given.", AlertType.ERROR, false);
    		return;
    	}
    	
    	String version = null;
    	
    	if (v.equals("%1") == true || v.equals("$1") == true) {
    		TextInputDialog td = new TextInputDialog("1.0.0");
    		td.setHeaderText("Package Version Number");
    		DialogPane dp = td.getDialogPane();
    		dp.setStyle("-fx-font-size: 14px; -fx-font-family: SanSerif;");
    		td.showAndWait();
    		
    		version = td.getResult();
    		
    		if (version == null || version.isBlank() == true)
    			return;
    	} else {
    		version = v;
    	}
    	
    	FXMLLoader loader = jg.loadScene(aPane, "RunOutput.fxml", "Run Output", null);
    	RunOutputController roc = (RunOutputController)loader.getController();
    	
    	if (jg.osType == 1) {
	    	String[] cmd = { "cmd.exe", "/C", script.getAbsolutePath(), version };
	    	
	    	roc.setCmd(cmd);
    	} else if (jg.osType == 2 || jg.osType == 3) {
    		String[] cmd = { script.getAbsolutePath(), version };
	    	
	    	roc.setCmd(cmd);
    	}
    	
    	Stage stage = (Stage)roc.getStage();
    	
    	stage.showAndWait();
    }

    @FXML
    void doActionScript(ActionEvent event) {
    	jg.setStatus("Build script");
    	buildScript();
    }

    @FXML
    void doFileQuit(ActionEvent event) {
    	Object[] objs = jg.prjList.keySet().toArray();

		for (Object o : objs) {
			String s = (String) o;
			IniFile ini = jg.prjList.get(s);
			if (ini.getChangedFlag() == true) {
				ButtonType bt = jg.yesNoCancelAlert(aPane, "Quit JpGui", 
						"You have unsaved changes.\n" +
						"Select 'No' to abort all changes and exit.\n" +
						"select 'Yes' to close dialog, then use\n" +
						"Menu File -> 'Save Project' or 'Save all Projects'.\n", null);
//				System.out.println(bt);
				if (bt.getButtonData() == ButtonData.YES) {
					return;
				}
			}
		}
		
    	jg.setStatus("Quiting program");
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    void doFileNewProject(ActionEvent event) {
    	jg.setStatus("Creating project");
    	jg.centerScene(aPane, "PrjNew.fxml", "New Project", null);
    }
    
    @FXML
    void doFileNewProjectWizard(ActionEvent event) {
    	jg.setStatus("Creating project with Wizard");
    	jg.centerScene(aPane, "PrjNewWizard.fxml", "New Project Wizard", null);
    }

    @FXML
    void doFileOpenProject(ActionEvent event) {
    	jg.setStatus("Opening project");
    	jg.centerScene(aPane, "PrjOpen.fxml", "Open Project", null);
    	
    	if (jg.projectOpen != null) {
    		
    		String recent = jg.sysIni.getString("RecentOpens", "projects");
    		if (recent != null) {
    			if (recent.contains(jg.projectOpen) == false)
    				recent = jg.projectOpen + "," + recent;
    		} else {
    			recent = jg.projectOpen;
    		}
    		
    		String[] arr = recent.split(",");
    		if (arr.length >= MAX_RECENTS) {
    			String rs = null;
    			for (int z = 0; z < MAX_RECENTS; z++) {
    				if (rs == null)
    					rs = arr[z];
    				else
    					rs += "," + arr[z];
    			}
    			
    			recent = rs;
    			
    			arr = recent.split(",");
    			recents.clear();
    			
    			for (String a : arr) {
    				MenuItem mi = new MenuItem(a);
    				mi.setOnAction((ee) -> {
    					MenuItem m = (MenuItem)ee.getSource();
    					jg.projectOpen = m.getText();
    					recentOpenProject();
    				});
    				recents.add(mi);
    			}
    		}
    		
    		jg.sysIni.addValuePair("RecentOpens", "projects", recent);
    		jg.sysIni.writeFile(true);
    		
    		ObservableList<Tab> tabs = tabPane.getTabs();
    		for (Tab tab : tabs) {
    			if (tab.getText().equalsIgnoreCase(jg.projectOpen) == true) {
    				tabPane.getSelectionModel().select(tab);
    				jg.showAlert("Dup Project", "You already have that project open.", AlertType.ERROR, false);
    				return;
    			}
    		}
    		
    		createPrjTab(jg.projectOpen);
    		
    		
    		
    		File f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_in");
    		File t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-in");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_out");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-out");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_in");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-in");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_out");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-out");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_in");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-in");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_out");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-out");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "_linux.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "-linux.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "_win.bat");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "-win.bat");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "_mac.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "-mac.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_prerun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-prerun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_prerun.bat");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-prerun.bat");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_prerun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-prerun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_postrun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-postrun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_postrun.bat");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-postrun.bat");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_postrun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-postrun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		setMenu(false);
    	}
    }
    
    @FXML
    void doFileSaveAllProjects(ActionEvent event) {
    	ObservableList<Tab> tabs = tabPane.getTabs();
    	
    	jg.setStatus("Saving projects");
    	for (Tab tab : tabs) {
    		saveProject(tab);
    	}
    }

    @FXML
    void doFileSaveProject(ActionEvent event) {
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	jg.setStatus("Save project");
    	saveProject(tab);
    }
    
    @FXML
    void doFileDeleteProject(ActionEvent event) {
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	if (tab == null) {
    		jg.setStatus("No tab selected.");
    		return;
    	}
    	
    	jg.setStatus("Deleting project");
    	ButtonType bt = jg.yesNoAlert("Delete Project", "Are you sure you want to delete selected project '" + tab.getText() +
    				"'?\n*** This can not be undone. ***", AlertType.WARNING);
//    	System.out.println(bt);
    	if (bt.getText().toLowerCase().equals("yes") ) {
    		File f = new File(jg.workspace.getAbsolutePath() + File.separator + tab.getText() + File.separator + tab.getText() + ".ini");
    		if (f.exists() == true)
    			f.delete();
    		
    		jg.sysIni.removeValuePair("Projects", tab.getText());
    		jg.sysIni.writeFile(true);
    		
    		f = new File(jg.workspace.getAbsolutePath() + File.separator + tab.getText());
    		if (f.exists() == true)
    			jg.deleteDir(f);
    		
    		tabPane.getTabs().remove(tab);
    		if (tabPane.getTabs().size() <= 0)
    			setMenu(true);
    		
    		jg.addStatus("Done...");
    	} else {
    		jg.addStatus("canceled");
    	}
    }

    @FXML
    void doHelpAbout(ActionEvent event) {
    	jg.centerScene(aPane, "About.fxml", "About JpGui", null);
    }

    @FXML
    void doHelpHelp(ActionEvent event) {
    	jg.centerScene(aPane, "Help.fxml", "Jpackage Help", null);
    }

    @FXML
    void doHelpRequired(ActionEvent event) {
    	jg.centerScene(aPane, "ReqPkg.fxml", "Required Packages", null);
    }
    
    @FXML
    void doPlatform(ActionEvent event) {
    	jg.platform = cbPlatform.getValue();
    	setFields();
    }
    
    @FXML
    void doScriptsEditPreRun(ActionEvent event) {
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	if (tab == null)
    		return;
    	
    	File f = new File(jg.workspace.getAbsolutePath() + 
				File.separator + tab.getText() + File.separator + jg.platform.toLowerCase() + "-prerun" + (jg.platform.equals("Win")? ".bat" : ".sh"));
    	
    	if (f.exists() == false) {
    		jg.showAlert("User Error", "Script not found.\nUse Action->Script to generate.", AlertType.ERROR, false);
    		return;
    	}
    	
    	jg.setStatus("Editing Script");
    	FXMLLoader loader = jg.loadScene(aPane, "ScriptEdit.fxml", "Edit Script", null);
    	ScriptEditController sec = (ScriptEditController)loader.getController();
    	
    	sec.setScript(f);
    	
    	Stage stage = (Stage)sec.getStage();
    	
    	stage.showAndWait();
    }

    @FXML
    void doScritpsEditPostRun(ActionEvent event) {
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	if (tab == null)
    		return;
    	
    	File f = new File(jg.workspace.getAbsolutePath() + 
				File.separator + tab.getText() + File.separator + jg.platform.toLowerCase() + "-postrun" + (jg.platform.equals("Win")? ".bat" : ".sh"));
    	
    	if (f.exists() == false) {
    		jg.showAlert("User Error", "Script not found.\nUse Action->Script to generate.", AlertType.ERROR, false);
    		return;
    	}
    	
    	jg.setStatus("Editing Script");
    	FXMLLoader loader = jg.loadScene(aPane, "ScriptEdit.fxml", "Edit Script", null);
    	ScriptEditController sec = (ScriptEditController)loader.getController();
    	
    	sec.setScript(f);
    	
    	Stage stage = (Stage)sec.getStage();
    	
    	stage.showAndWait();
    }
    
    @FXML
    void doScritpsEditMain(ActionEvent event) {
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	if (tab == null)
    		return;
    	
    	File f = new File(jg.workspace.getAbsolutePath() + 
				File.separator + tab.getText() + File.separator + tab.getText() + "-" + jg.platform.toLowerCase() + (jg.platform.equals("Win")? ".bat" : ".sh"));
    	
    	if (f.exists() == false) {
    		jg.showAlert("User Error", "Script not found.\nUse Action->Script to generate.", AlertType.ERROR, false);
    		return;
    	}
    	
    	jg.setStatus("Editing Script");
    	FXMLLoader loader = jg.loadScene(aPane, "ScriptEdit.fxml", "Edit Script", null);
    	ScriptEditController sec = (ScriptEditController)loader.getController();
    	
    	sec.setScript(f);
    	
    	Stage stage = (Stage)sec.getStage();
    	
    	stage.showAndWait();
    }

	    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		String os = System.getProperty("os.name").toLowerCase();
		
		platforms = FXCollections.observableArrayList();
		
		platforms.addAll("Win", "Linux", "Mac");
		
		cbPlatform.setItems(platforms);
		
		if (os.contains("win") == true) {
			cbPlatform.setValue("Win");
			jg.platform = "Win";
			lblOs.setText("OS = Windows");
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			cbPlatform.setValue("Linux");
			jg.platform = "Linux";
			lblOs.setText("OS = Linux");
		} else if (os.contains("mac") == true) {
			cbPlatform.setValue("Mac");
			jg.platform = "Mac";
			lblOs.setText("OS = Mac");
		}
		
		jg.status = lblStatus;
		
		popup = new Popup();
		TextArea ta = new TextArea("");
		ta.setPrefWidth(475);
		ta.setPrefHeight(125);
		ta.setStyle("-fx-font-size: 14px;");
		ta.setEditable(false);
		ta.setWrapText(true);
		
		lblVersion.setText(jg.appVersion);
		
		popup.getContent().add(ta);
		
		menuFile.setOnShowing((e)->{
			if (popup.isShowing() == true)
				popup.hide();
		});
		menuAction.setOnShowing((e)->{
			if (popup.isShowing() == true)
				popup.hide();
		});
		menuHelp.setOnShowing((e)->{
			if (popup.isShowing() == true)
				popup.hide();
		});
		
		lblTitle.setOnMouseClicked((e)->{
			if (popup.isShowing() == true)
				popup.hide();
		});
		
		hbTitle.setOnMouseClicked((e)->{
			if (popup.isShowing() == true)
				popup.hide();
		});
		
		fc = new FileChooser();
		zfc = new FileChooser();
		dc = new DirectoryChooser();
		
		if (jg.sysIni.sectionExists("RecentOpens") == false) {
			jg.sysIni.addSection("RecentOpens");
			jg.sysIni.writeFile(true);
		}
		
		recents = mOpenRecent.getItems();
		
		String rs = jg.sysIni.getString("RecentOpens", "projects");
		if (rs != null) {
			String[] arr = rs.split(",");
			for (String a : arr) {
				MenuItem mi = new MenuItem(a);
				mi.setOnAction((ee) -> {
					MenuItem m = (MenuItem)ee.getSource();
					jg.projectOpen = m.getText();
					recentOpenProject();
				});
				recents.add(mi);
			}
		}
		
		setMenu(true);
		
		// used to get workspace if not defined yet.
		aPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
			if (newScene != null) {
				if (jg.workspace == null) {
					FXMLLoader loader = jg.loadScene(aPane, "Workspace.fxml", "Select Workspace", null);
			    	WorkspaceController sec = (WorkspaceController)loader.getController();
			    	
			    	Stage stage = (Stage)sec.getStage();
			    	
			    	stage.showAndWait();
			    	
			    	if (jg.workspace != null) {
			    		if (jg.workspace.exists() == false)
			    			jg.workspace.mkdirs();
			    		
			    		jg.setupProject();
			    		
			    		int idx = 0;
			    		Object[] objs = jg.wsIni.getSectionKeys("Workspaces");
			    		if (objs != null) {
			    			for (Object o : objs) {
			    				int n = Integer.parseInt((String)o);
			    				if (n > idx)
			    					idx = n;
			    			}
			    		}
			    		
			    		idx++;
			    		
			    		jg.wsIni.addValuePair("Workspaces", idx + "", jg.workspace.getAbsolutePath());
			    		jg.wsIni.addValuePair("Current", "workspace", jg.workspace.getAbsolutePath());
			    		jg.wsIni.writeFile(true);
			    		
			    		btnWorkspace.setText(jg.workspace.getName());
			    		ttWorkspace.setText("Switch Workspace:\n" + jg.workspace.getAbsolutePath());
			    	} else {
			    		System.exit(1);
			    	}
				}
			}
		});
		
		if (jg.workspace != null) {
			btnWorkspace.setText(jg.workspace.getName());
			ttWorkspace.setText("Switch Workspace:\n" + jg.workspace.getAbsolutePath());
		}
	}
	
	private void createPrjTab(String prjName) {
		
		File pf = new File(jg.workspace.getAbsolutePath() + File.separator + 
						prjName + File.separator + prjName + ".ini");
		
		jg.currPrj = new IniFile(pf.getAbsolutePath());
		
		IniFile org = new IniFile(pf.getAbsolutePath());
		
		jg.orgList.put(prjName, org);
		
		if (jg.currPrj.sectionExists("JpGui Options") == false)
			jg.currPrj.addSection("JpGui Options");
		
		String os = jg.platform.toLowerCase();
		
		if (jg.osType == 1) {
			jg.currPrj.addValuePair("JpGui Options", "Win PreRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.bat");
			jg.currPrj.addValuePair("JpGui Options", "Win PostRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.bat");
			org.addValuePair("JpGui Options", "Win PreRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.bat");
			org.addValuePair("JpGui Options", "Win PostRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.bat");
		} else if (jg.osType == 2) {
			jg.currPrj.addValuePair("JpGui Options", "Linux PreRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.sh");
			jg.currPrj.addValuePair("JpGui Options", "Linux PostRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.sh");
			org.addValuePair("JpGui Options", "Linux PreRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.sh");
			org.addValuePair("JpGui Options", "Linux PostRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.sh");
		} else if (jg.osType == 3) {
			jg.currPrj.addValuePair("JpGui Options", "Mac PreRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.sh");
			jg.currPrj.addValuePair("JpGui Options", "Linux PostRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.sh");
			org.addValuePair("JpGui Options", "Mac PreRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.sh");
			org.addValuePair("JpGui Options", "Linux PostRun Script", 
					jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.sh");
		}
		
		jg.currPrj.writeFile(true);
		org.writeFile(true);
		
		jg.prjList.put(prjName, jg.currPrj);
		
		Tab tab = new Tab(prjName);
		
		tab.setOnSelectionChanged((e)->{
			if (popup.isShowing() == true)
				popup.hide();
			
			Tab t = (Tab)e.getSource();
			jg.currPrj = jg.prjList.get(t.getText());
		});
		
		tab.setOnClosed((e)->{
			if (popup.isShowing() == true)
				popup.hide();
			
			ObservableList<Tab> tabs = tabPane.getTabs();
			if (tabs.size() > 0) {
				jg.currPrj = jg.prjList.get(tabs.get(0).getText());
			} else {
				jg.currPrj = null;
			}
			
			if (tabPane.getTabs().size() <= 0)
				setMenu(true);
			else
				setMenu(false);
		});
		tab.setOnCloseRequest((e)->{
			if (popup.isShowing() == true)
				popup.hide();
			
			ObservableList<Tab> tabs = tabPane.getTabs();
			if (tabs.size() > 0) {
				jg.currPrj = jg.prjList.get(tabs.get(0).getText());
			} else {
				jg.currPrj = null;
			}
			
			if (tabPane.getTabs().size() <= 0)
				setMenu(true);
			else
				setMenu(false);
		});
		
		tabPane.setOnMouseClicked((e)->{
			if (popup.isShowing() == true)
				popup.hide();
		});
		
		loadTab(tab);
		
	}
	
	private void loadTab(Tab tab) {
		
//		String prjName = tab.getText();
		
		AnchorPane ap1 = new AnchorPane();
		ScrollPane sp = new ScrollPane();
		VBox vb1 = new VBox();
		Accordion accordion = new Accordion();
		
		sp.setFitToHeight(true);
		sp.setFitToWidth(true);
		
		tab.setContent(sp);
		sp.setContent(ap1);
		ap1.getChildren().add(vb1);
		vb1.getChildren().add(accordion);
		tabPane.getTabs().add(tab);
		
		tabPane.getSelectionModel().select(tab);
		
		VBox.setVgrow(sp, Priority.ALWAYS);
		VBox.setVgrow(vb1, Priority.ALWAYS);
		VBox.setVgrow(accordion, Priority.ALWAYS);
		
		AnchorPane.setTopAnchor(vb1, 0.0);
		AnchorPane.setBottomAnchor(vb1, 0.0);
		AnchorPane.setRightAnchor(vb1, 0.0);
		AnchorPane.setLeftAnchor(vb1, 0.0);
		
		String fn = "combined.txt";
		
		AnchorPane ap2 = null;
		TitledPane tp = null;
		VBox vb2 = new VBox();
		HBox hb = null;
		String hColor = "#ffff00;";
		
		try (InputStream in = getClass().getResourceAsStream("/resources/" + fn);
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
				
				MyButton sb = null;
				TextField tf = null;
				TextArea ta = null;
				CheckBox ckb = null;
				
				if (line.charAt(0) == '^') {
//					System.out.println(line);
					
					tp = new TitledPane();
					sp = new ScrollPane();
					ap2 = new AnchorPane();
					vb2 = new VBox();
					
					tp.setOnMouseClicked((e)->{
						if (popup.isShowing() == true)
							popup.hide();
					});
					
					tp.setContent(ap2);
					ap2.getChildren().add(vb2);
					accordion.getPanes().add(tp);
				
					ap2.setPrefHeight(400);
					ap2.setPrefWidth(600);
					AnchorPane.setTopAnchor(vb2, 0.0);
					AnchorPane.setBottomAnchor(vb2, 0.0);
					AnchorPane.setRightAnchor(vb2, 0.0);
					AnchorPane.setLeftAnchor(vb2, 0.0);
					
					tp.setText(line.substring(1));
					
					vb2.setAlignment(Pos.TOP_LEFT);
					vb2.setSpacing(4.0);
					tp.setContent(vb2);
					
					continue;
				}
				
				String[] arr = line.split(":");
				
				String prompt = arr[0].substring(1);
				
				Label lbl = new Label(prompt + ":");
				lbl.setUserData(prompt);
				lbl.setFont(jg.font1);
				lbl.setPrefWidth(175.0);
				hb = new HBox();
				hb.setSpacing(4.0);
				hb.setPadding(new Insets(4, 4, 4, 4));
				hb.setAlignment(Pos.CENTER_LEFT);
				
				if (line.charAt(0) == '&') {
					
					ckb = new CheckBox(prompt);
					ckb.setUserData(prompt);
					ckb.setFont(jg.font1);
					Region rg = new Region();
					HBox.setHgrow(rg, Priority.ALWAYS);
					final TitledPane titledPane = tp;
					final CheckBox fckb = ckb;
					ckb.selectedProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
//					    System.out.println("checkbox changed from " + oldValue + " to " + newValue);
					    Tab t = tabPane.getSelectionModel().getSelectedItem();
						if (t != null) {
							ObservableList<String> c = t.getStyleClass();
							if (c.contains("dirty") == false)
								t.getStyleClass().add("dirty");
							jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue.toString());
						}
					    titledPane.setStyle("-fx-text-fill: " + hColor);
					    HBox hb2 = (HBox)fckb.getParent();
					    Label lbl2 = (Label)hb2.getChildren().get(0);
					    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
					});
					
					hb.getChildren().addAll(lbl, ckb, rg);
					vb2.getChildren().add(hb);
				} else if (line.charAt(0) == '*') {
					
					tf = new TextField();
					tf.setFont(jg.font1);
					if (prompt.toLowerCase().equals("app version") == true)
						tf.setText("%1");
					final TitledPane titledPane = tp;
					final TextField ftf = tf;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
							HBox hb2 = (HBox)ftf.getParent();
						    Label lbl2 = (Label)hb2.getChildren().get(0);
						    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '-') {
					
					tf = new TextField();
					tf.setFont(jg.font1);
					final TitledPane titledPane = tp;
					final TextField ftf = tf;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
							HBox hb2 = (HBox)ftf.getParent();
						    Label lbl2 = (Label)hb2.getChildren().get(0);
						    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					sb = new MyButton();
					sb.setMyData("^");
					sb.setGraphic(new ImageView(jg.imgDir));
					sb.setStyle("-fx-font-size: 14px;");
					sb.setPrefWidth(30.0);
					sb.setOnAction((e) -> {
						Button b = (Button)e.getSource();
						HBox hb2 = (HBox)b.getParent();
						TextField textField = (TextField)hb2.getChildren().get(1);
						Stage stage = (Stage) aPane.getScene().getWindow();
						File df = dc.showDialog(stage);
						if (df != null) {
							textField.setText(df.getAbsolutePath());
						}
					});
					
					Tooltip tt = new Tooltip("Select a directory.");
					tt.setStyle("-fx-font-size: 14px;");
					sb.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '@') {	
					tf = new TextField();
					tf.setFont(jg.font1);
					final TitledPane titledPane = tp;
					final TextField ftf = tf;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
							HBox hb2 = (HBox)ftf.getParent();
						    Label lbl2 = (Label)hb2.getChildren().get(0);
						    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					sb = new MyButton();
					sb.setMyData("^");
					sb.setGraphic(new ImageView(jg.imgDir));
					sb.setStyle("-fx-font-size: 14px;");
					sb.setPrefWidth(30.0);
					sb.setOnAction((e) -> {
						Button b = (Button)e.getSource();
						HBox hb2 = (HBox)b.getParent();
						TextField textField = (TextField)hb2.getChildren().get(1);
						FXMLLoader loader = jg.loadScene(aPane, "SelectDirectories.fxml", "Select Directories", null);
						SelectDirectoriesController sdc = (SelectDirectoriesController)loader.getController();
						sdc.setData(textField.getText());
						
						Stage stage = sdc.getStage();
						
				    	stage.showAndWait();
				    	
				    	if (jg.dirsSelected != null) {
				    		textField.setText(jg.dirsSelected);
				    	}
					});
					
					Tooltip tt = new Tooltip("Select directories.");
					tt.setStyle("-fx-font-size: 14px;");
					sb.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '%') {	
					tf = new TextField();
					tf.setFont(jg.font1);
					final TitledPane titledPane = tp;
					final TextField ftf = tf;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
							HBox hb2 = (HBox)ftf.getParent();
						    Label lbl2 = (Label)hb2.getChildren().get(0);
						    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					sb = new MyButton();
					sb.setMyData("^");
					sb.setGraphic(new ImageView(jg.imgDir));
					sb.setStyle("-fx-font-size: 14px;");
					sb.setPrefWidth(30.0);
					sb.setOnAction((e) -> {
						Button b = (Button)e.getSource();
						HBox hb2 = (HBox)b.getParent();
						TextField textField = (TextField)hb2.getChildren().get(1);
						FXMLLoader loader = jg.loadScene(aPane, "FilesOrDirs.fxml", "Select Files & Directories", null);
						FilesOrDirsController fdc = (FilesOrDirsController)loader.getController();
						fdc.setData(textField.getText());
						
						Stage stage = fdc.getStage();
						
				    	stage.showAndWait();
				    	
				    	if (jg.dirsSelected != null) {
				    		textField.setText(jg.dirsSelected);
				    	}
					});
					
					Tooltip tt = new Tooltip("Select directories.");
					tt.setStyle("-fx-font-size: 14px;");
					sb.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '+') {
					
					tf = new TextField();
					tf.setFont(jg.font1);
					final TitledPane titledPane = tp;
					final TextField ftf = tf;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
							HBox hb2 = (HBox)ftf.getParent();
						    Label lbl2 = (Label)hb2.getChildren().get(0);
						    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					sb = new MyButton();
					sb.setMyData("^");
					sb.setGraphic(new ImageView(jg.imgFile));
					sb.setFont(jg.font1);
					sb.setPrefWidth(30.0);
					sb.setOnAction((e) -> {
						Button b = (Button)e.getSource();
						HBox hb2 = (HBox)b.getParent();
						TextField textField = (TextField)hb2.getChildren().get(1);
						Stage stage = (Stage) aPane.getScene().getWindow();
						File df = fc.showOpenDialog(stage);
						if (df != null) {
							textField.setText(df.getAbsolutePath());
						}
					});
					
					Tooltip tt = new Tooltip("Select a file.");
					tt.setFont(jg.font1);
					sb.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '!') {
					ta = new TextArea();
					ta.setPrefWidth(250.0);
					ta.setPrefHeight(250.0);
					ta.setFont(jg.font1);
					final TitledPane titledPane = tp;
					final TextArea fta = ta;
					ta.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), jg.platform + " " + (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
							HBox hb2 = (HBox)fta.getParent();
						    Label lbl2 = (Label)hb2.getChildren().get(0);
						    lbl2.setStyle("-fx-text-fill: blueviolet; -fx-font-weight: bold;");
						}
					});
					
					hb.setAlignment(Pos.TOP_LEFT);
					HBox.setHgrow(ta, Priority.ALWAYS);
					
					Tooltip tt = new Tooltip("One option per line.");
					tt.setFont(jg.font1);
					
					ta.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, ta);
					vb2.getChildren().add(hb);
					
					ta.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				}
				
				
				
				MyButton sm = null;
				if (arr[0].substring(1).equalsIgnoreCase("add modules") == true) {
					sm = new MyButton();
					sm.setMyData("@");
					sm.setGraphic(new ImageView(jg.imgList));
					sm.setStyle("-fx-font-size: 14px;");
					sm.setPrefWidth(30.0);
					sm.setOnAction((e) -> {
						Button b = (Button)e.getSource();
						HBox hb2 = (HBox)b.getParent();
						TextField textField = (TextField)hb2.getChildren().get(1);
						FXMLLoader loader = jg.loadScene(aPane, "AddModules.fxml", "Select Modules", null);
						AddModulesController amc = (AddModulesController)loader.getController();
						amc.setData(textField.getText());
						Stage stage = (Stage)amc.getStage();
				    	
				    	stage.showAndWait();
				    	
				    	if (jg.modulesSelected != null) {
				    		textField.setText(jg.modulesSelected);
				    	}
					});
				}
				
				// Add reset button.
				
				MyButton rs = new MyButton();
				rs.setMyData("r");
				rs.setPrefSize(18.0, 18.0);
				rs.setGraphic(new ImageView(jg.imgUndo));
				rs.setFont(jg.font1);
				Tooltip tt2 = new Tooltip("Reset field.");
				tt2.setFont(jg.font1);
				rs.setTooltip(tt2);
				
				final TitledPane tp2 = tp;
				
				rs.setOnMouseClicked((e) -> {
					Button b = (Button)e.getSource();
					HBox hb2 = (HBox)b.getParent();
					if (hb2.getChildren().get(1) instanceof TextField) {
						TextField tf2 = (TextField) hb2.getChildren().get(1);
						tf2.setText((String)tf2.getUserData());
						jg.currPrj.addValuePair(tp2.getText(), jg.platform + " " + (String)lbl.getUserData(), (String)tf2.getUserData());
						Label lbl2 = (Label)hb2.getChildren().get(0);
						lbl2.setStyle("-fx-text-fill: black;");
					}
					if (hb2.getChildren().get(1) instanceof CheckBox) {
						CheckBox ckb2 = (CheckBox) hb2.getChildren().get(1);
						ckb2.setSelected((Boolean)ckb2.getUserData());
						jg.currPrj.addValuePair(tp2.getText(), jg.platform + " " + (String)lbl.getUserData(), ckb2.getUserData().toString());
						Label lbl2 = (Label)hb2.getChildren().get(0);
						lbl2.setStyle("-fx-text-fill: black;");
					}
						
				});
				
				// Add Help button.
				
				MyButton qb = new MyButton();
				qb.setMyData("?");
				qb.setGraphic(new ImageView(jg.imgHelp));
				qb.setFont(jg.font1);
				qb.setOnAction((e) -> {
					TextArea textArea = (TextArea)popup.getContent().get(0);
					textArea.setStyle("-fx-font-size: 18px; -fx-font-family: SanSerif;");
					textArea.setPrefWidth(500.0);
					textArea.setPrefHeight(225.0);
					
					Button bh = (Button)e.getSource();
					String help = (String)bh.getUserData();
					Stage s = (Stage)bh.getScene().getWindow();
					
					textArea.setText(help);
					
					if (popup.isShowing() == false)
						popup.show(s);
					else {
						popup.hide();
						popup.show(s);
					}
				});
				
				if (sb != null)
					hb.getChildren().addAll(rs, sb, qb);
				else {
					if (sm != null)
						hb.getChildren().addAll(rs, sm, qb);
					else
						hb.getChildren().addAll(rs, qb);
				}
				
				String txt = arr[1];
				String line2 = null;
				while ((line2 = reader.readLine()) != null) {
					line2 = line2.trim();
					if (line2.charAt(0) == ';')
						break;
					txt += "\n" +  line2;
				}
				
				qb.setUserData(txt);
			}
			reader.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setFields();
	}
 	
	private void loadFields(String prjName) {
		
		IniFile ini = jg.prjList.get(prjName);
		
		if (ini == null) {
			return;
		}
		
		ObservableList<Tab> tabs = tabPane.getTabs();
		
		AnchorPane ap = null;
		ScrollPane sp = null;
		for (Tab tab : tabs) {
			if (tab.getText().equalsIgnoreCase(prjName) == true) {
				sp = (ScrollPane)tab.getContent();
				ap = (AnchorPane)sp.getContent();
				break;
			}
		}
		
		if (ap == null)
			return;
		
		jg.loadFlag = true;
	
		VBox vb = (VBox)ap.getChildren().get(0);
		Accordion a = (Accordion)vb.getChildren().get(0);
		
		ObservableList<TitledPane> panes = a.getPanes();
		for (TitledPane p : panes) {
			String sec = p.getText();
			
			VBox vb2 = (VBox)p.getContent();
			
			TextField tf = null;
			TextArea ta = null;
			CheckBox ckb = null;
			
			ObservableList<Node> nodes = vb2.getChildren();
			for (Node n : nodes) {
				HBox hb = (HBox)n;
				
				ObservableList<Node> fields = hb.getChildren();
				
				Label lbl = (Label)fields.get(0);
				
				if (fields.get(1) instanceof TextField) {
					tf = (TextField)fields.get(1);
					String fld = (String) lbl.getUserData();
					String value = ini.getString(sec, jg.platform + " " + fld);
					tf.setText(value);
					tf.setUserData(value);
				} else if (fields.get(1) instanceof CheckBox) {
					ckb = (CheckBox)fields.get(1);
					boolean yesNo = ini.getBoolean(sec, jg.platform + " " + lbl.getUserData());
					ckb.setSelected(yesNo);
					ckb.setUserData(yesNo);
				} else if (fields.get(1) instanceof TextArea) {
					ta = (TextArea)fields.get(1);
					String fld = (String) lbl.getUserData();
					String value = ini.getString(sec, jg.platform + " " + fld);
					ta.setText("");
					if (value != null) {
						String[] arr = value.split(",");
						
						for (String s : arr) {
							ta.appendText(s + "\n");
						}
					}
					ta.setUserData(value);
				}
				
				if (fields.get(2) instanceof Button) {		// Undo button
					Button b = (Button)fields.get(2);
					String fld = (String) lbl.getUserData();
					UserDataInfo udi = new UserDataInfo();
					if (tf != null)
						udi.setTf(tf);
					if (ckb != null)
						udi.setCkb(ckb);
					String value = ini.getString(sec, jg.platform + " " + fld);
					udi.setData(value);
					b.setUserData(udi);
				}
			}
		}
		
		jg.loadFlag = false;
	}
	
	private void saveProject(Tab tab) {
		if (tab == null) {
			jg.addStatus("Save, No project tab selected.");
			return;
		}
		
		String prjName = tab.getText();
		
		jg.addStatus(prjName);
		
		IniFile ini = jg.prjList.get(prjName);
		
		if (ini == null)
			return;
		
//		System.out.println(ini.stringFile(null));
		
		tab.getStyleClass().remove("dirty");
		
		ScrollPane sp = (ScrollPane)tab.getContent();
		AnchorPane ap = (AnchorPane)sp.getContent();
		VBox vb = (VBox)ap.getChildren().get(0);
		Accordion a = (Accordion)vb.getChildren().get(0);
		
		ObservableList<TitledPane> panes = a.getPanes();
		
		for (TitledPane p : panes) {
			String sec = p.getText();
			if (ini.sectionExists(sec) == false)
				ini.addSection(sec);
			
			p.setStyle("-fx-text-fill: black;");
			
			VBox vb2 = (VBox)p.getContent();
			
			ObservableList<Node> nodes = vb2.getChildren();
			for (Node n : nodes) {
				HBox hb = (HBox)n;
				
				ObservableList<Node> fields = hb.getChildren();
				
				Label lbl = (Label)fields.get(0);
				if (fields.get(1) instanceof TextField) {
					TextField tf = (TextField)fields.get(1);
					String value = tf.getText();
					if (value == null || value.isBlank())
						value = "";
					ini.addValuePair(sec, jg.platform + " " + (String)lbl.getUserData(), value);
				} else if (fields.get(1) instanceof CheckBox) {
					CheckBox ckb = (CheckBox)fields.get(1);
					ini.addValuePair(sec, jg.platform + " " + (String)lbl.getUserData(), ckb.isSelected() + "");
				} else if (fields.get(1) instanceof TextArea) {
					TextArea ta = (TextArea)fields.get(1);
					String v = ta.getText();
					String value = null;
					if (v == null || v.isBlank())
						value = "";
					else {
						String[] arr = v.split("\n");
						for (String s : arr) {
							if (value == null)
								value = s;
							else
								value += "," + s;
						}
					}
					ini.addValuePair(sec, jg.platform + " " + (String)lbl.getUserData(), value);
				}
			}
		}
		
		ini.writeFile(true);
		
		jg.prjList.put(prjName, ini);
	}
	
	private File buildScript() {
		os = cbPlatform.getValue().toLowerCase();
    	
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	
    	if (tab == null) {
    		jg.addStatus("No tab selected.");
    		return null;
    	}
		
    	boolean isWin = false;
    	String fn = tab.getText();
    	String prjName = tab.getText();
    	
		if (os.contains("win") == true) {
			fn += "-win.bat";
			isWin = true;
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			fn += "-linux.sh";
			isWin = false;
		} else if (os.contains("mac") == true) {
			fn += "-mac.sh";
			isWin = false;
		}
		
		File f = new File(jg.workspace.getAbsolutePath() + 
					File.separator + prjName + File.separator + fn);
		
		IniFile ini = jg.prjList.get(tab.getText());
		if (ini == null)
			return null;
		
		if (ini.getChangedFlag() == true)
			ini.writeFile(true);
		
		try {
			ScrollPane sp = (ScrollPane)tab.getContent();
			AnchorPane ap = (AnchorPane)sp.getContent();
			VBox vb = (VBox)ap.getChildren().get(0);
			Accordion a = (Accordion)vb.getChildren().get(0);
			
			ObservableList<TitledPane> panes = a.getPanes();
			
			String dest = null;
			String appName = null;
			String pkgVersion = null;
			String appExt = null;
			boolean jpackageVerboseFlag = false;
			boolean javaVerboseFlag = false;
			boolean jpackageVerFlag = false;
			boolean consoleFlag = false;
			
			String cmdLine = "";
			
			for (TitledPane p : panes) {
				String paneName = p.getText().toLowerCase();
				VBox vb2 = (VBox)p.getContent();
				
				ObservableList<Node> nodes = vb2.getChildren();
				for (Node n : nodes) {
					HBox hb = (HBox)n;
					
					ObservableList<Node> fields = hb.getChildren();
				
					String[] arr = null;
					String opt = null;
					Button b = (Button)fields.get(fields.size() - 1);
					Label lbl = (Label)fields.get(0);
					String help = (String)b.getUserData();
					if (help != null) {
						arr = help.split("\\n");
						opt = arr[0].split(" ")[0];
						
						if (os.equals("win") == true) {
							if (opt.startsWith("--linux") == true || opt.startsWith("--mac") == true)
								continue;
						} else if (os.equals("linux") == true) {
							if (opt.startsWith("--win") == true || opt.startsWith("--mac") == true)
								continue;
						} else if (os.equals("max") == true) {
							if (opt.startsWith("--win") == true || opt.startsWith("--linux") == true)
								continue;
						}
					}
					
					if (fields.get(1) instanceof TextField) {
						TextField tf = (TextField)fields.get(1);
						String value = tf.getText();
						if (lbl.getText().toLowerCase().equals("destination:") == true) {
							dest = value;
						} else if (lbl.getText().toLowerCase().equals("app name:") == true) {
							appName = value;
						} else if (lbl.getText().toLowerCase().equals("app version:") == true) {
							pkgVersion = value;
						} else if (lbl.getText().toLowerCase().equals("jpackage path:") == true) {
							jg.jpackagePath = value;
						} else if (lbl.getText().toLowerCase().equals("package type:") == true) {
							if (value.charAt(0) == '.')
								appExt = value;
							else
								appExt = "." + value;
						}
						if (value != null && value.isBlank() == false) {
							if (paneName.equals("jpgui options") == true) {
								continue;
							}
							if (isWin == true)
								cmdLine += " ^\n" + opt + " \"" + value + "\"";
							else
								cmdLine += " \\\n" + opt + " \"" + value + "\"";
						}
					} else if (fields.get(1) instanceof CheckBox) {
						CheckBox ckb = (CheckBox)fields.get(1);
						boolean value = ckb.isSelected();
						if (lbl.getText().toLowerCase().equals("jpackage verbose:") == true) {
							jpackageVerboseFlag = value;
						} else if (lbl.getText().toLowerCase().equals("java verbose:") == true) {
							javaVerboseFlag = value;
						} else if (lbl.getText().toLowerCase().equals("jpackage version:") == true) {
							jpackageVerFlag = value;
						} else if (lbl.getText().toLowerCase().equals("win console:") == true) {
							consoleFlag = value;
						}
						if (value == true) {
							if (paneName.equals("jpgui options") == true) {
								continue;
							}
							if (isWin == true)
								cmdLine += " ^\n" + opt;
							else
								cmdLine += " \\\n" + opt;
						}
					} else if (fields.get(1) instanceof TextArea) {
						TextArea ta = (TextArea)fields.get(1);
						String value = ta.getText();
						String[] arr2 = value.split("\\n");
						
						if (lbl.getText().toLowerCase().equals("java options:") == true) {
							for (String v : arr2) {
								if (isWin == true) {
									cmdLine += " ^\n" + opt + " '" + v + "'";
								} else {
									cmdLine += " \\\n" + opt + " \"" + v + "\"";
								}
							}
						}
					}
				}
			}
			
			if (jg.jpackagePath != null) {
				if (jg.jpackagePath.matches(".*\\s.*") == true) {
					cmdLine = "\"" + jg.jpackagePath + "\"" + cmdLine;
				} else {
					cmdLine = jg.jpackagePath + cmdLine;
				}
			} else {
				if (jg.jpackagePath.matches(".*\\s.*") == true) {
					cmdLine = "\"" + jg.jpackagePath + "\"" + cmdLine;
				} else {
					cmdLine = jg.jpackagePath + cmdLine;
				}
			}
			
			if (javaVerboseFlag == true)
				cmdLine += " ^\n--java-options '-verbose'";
			
			if (jpackageVerboseFlag == true)
				cmdLine += " ^\n--verbose";
			
			if (pkgVersion == null || pkgVersion.isBlank() == true) {
				jg.showAlert("User Error", "No Application version given for option '--app-version'", AlertType.ERROR, false);
				return null;
			}
			
			if (appName == null || appName.isBlank() == true) {
				jg.showAlert("User Error", "No Application name given for option '--name'", AlertType.ERROR, false);
				return null;
			}
			
			if (dest == null || dest.isBlank() == true) {
				jg.showAlert("User Error", "No destination directory given for option '--dest'", AlertType.ERROR, false);
				return null;
			}
			
			FileWriter fw = new FileWriter(f.getAbsolutePath(), false);
			
			if (isWin == true) {
				fw.write("@echo off\nrem Batch file to execute a jpackage command line.\nrem Generated by the JpGui program.\n");
				fw.write("rem Changes made to this script will be lost if regenerated.\n\n");
				fw.write("set jpgui=%JpGui%\n\n");
				
				fw.write("if \"%~1\"==\"\" (\n");
				fw.write("    if \"%jpgui%\"==\"\" (\n");
				fw.write("        echo \u001B[91mERROR:\u001B[0m Must give a version number that is different from the currently installed version.\n");
				fw.write("    ) else (\n");
				fw.write("        echo ERROR: Must give a version number that is different from the currently installed version.\n");
				fw.write("    )\n");
				fw.write("    exit /b 1\n");
				fw.write(")\n\n");
				if (jg.rmAll == true) {
					fw.write("echo Deleting old " + appName + "-" + "*" + appExt + "\n");
					fw.write("del /F /Q \"" + dest + File.separator + appName + "-" + "*" + appExt + "\"\n\n");
				} else {
					fw.write("echo Deleting old " + appName + "-" + pkgVersion + appExt + "\n");
					fw.write("del /F /Q \"" + dest + File.separator + appName + "-" + pkgVersion + appExt + "\"\n\n");
				}
			} else {
				fw.write("#!/usr/bin/sh\n# Shell script to execute a jpackage command line.\n# Generated by the JpGui program.\n");
				fw.write("# Changes made to this script will be lost if regenerated.\n\n");
				fw.write("if [ $# -eq 0 ]; then\n");
				fw.write("    if [ \"$jpgui\" == \"\" ]; then\n");
				fw.write("        printf \033[91mERROR:\033[0m Must give a version number that is different from the currently installed version.\n");
				fw.write("    else\n");
				fw.write("        printf ERROR: Must give a version number that is different from the currently installed version.\n");
				fw.write("    exit 1\n");
				fw.write("fi\n\n");
				fw.write("jpgui=$JpGui\n\n");
				if (jg.rmAll == true) {
					fw.write("echo Deleting old " + appName.toLowerCase() + "_" + "*" + appExt + "\n");
					fw.write("rm -rf " + dest + "/" + appName.toLowerCase() + "_" + "*" + appExt + "\n\n");
				} else {
					fw.write("echo Deleting old " + appName.toLowerCase() + "_" + pkgVersion + appExt + "\n");
					fw.write("rm -rf " + dest + "/" + appName.toLowerCase() + "_" + pkgVersion + appExt + "\n\n");
				}
			}
			
			String preRun = jg.currPrj.getString("JpGui Options", jg.platform + " PreRun Script");
			String postRun = jg.currPrj.getString("JpGui Options", jg.platform + " PostRun Script");;
			
			if (preRun != null && preRun.isBlank() == false) {
				fw.write("echo Executing PreRun Script.\n");
				if (isWin == true) {
					fw.write("call \"" + preRun + "\"\n\n");
					fw.write("if %errorlevel% neq 0 (\n");
					fw.write("    if \"%jpgui%\"==\"\" (\n");
					fw.write("        echo \u001B[91mFAILED:\u001B[0m PostRun Script.\n");
					fw.write("    ) else (\n");
					fw.write("        echo FAILED: PostRun Script.\n");
					fw.write("    )\n");
					fw.write("    exit /b 1\n");
					fw.write(")\n\n");
					fw.write("if \"%jpgui%\"==\"\" (\n");
					fw.write("    echo \u001B[92mDONE:\u001B[0m Executing PreRun Script.\n");
					fw.write(") else (\n");
					fw.write("    echo DONE: Executing PreRun Script.\n");
					fw.write(")\n\n");
				} else {
					fw.write(preRun + "\n\n");
					fw.write("if [ $? -ne 0 ]; then\n");
					fw.write("    if [ \"$jpgui\" == \"\" ]; then\n");
					fw.write("        printf \"\033[91mFAILED:\033[0m PreRun Script.\n\"");
					fw.write("    else\n");
					fw.write("        printf \"FAILED: PreRun Script.\n\"");
					fw.write("    exit 1\n");
					fw.write("fi\n\n");
					fw.write("if [ $? -ne 0 ]; then\n");
					fw.write("    printf \"\033[92mDONE:\033[0m Executing PreRun Script.\n\n\"");
					fw.write("else\n");
					fw.write("    printf \"DONE: Executing PreRun Script.\n\"");
					fw.write("fi\n\n");
				}
			}
			
			if (jpackageVerFlag == true) {
				if (isWin == true) {
					fw.write("echo Jpackage: " + jg.jpackageVersion + "\n\n");
					fw.write("echo Creating Application Version: %1\n\n");
				} else {
					fw.write("echo Jpackage: " + jg.jpackageVersion + "\n\n");
					fw.write("echo Creating Application Version: $1\n\n");
				}
			}
			
			fw.write("echo Running jpackage command line.  This can take a few...\n");
			
			if (consoleFlag == true) {
				if (isWin) {
					fw.write("if \"%jpgui%\"==\"\" (\n");
					fw.write("    echo \u001B[93mWARNING:\u001B[0m The windows console flag is set.\n");
					fw.write(") else (\n");
					fw.write("    echo WARNING: The windows console flag is set.\n");
					fw.write(")\n");
				} else {
					fw.write("if [ \"$jpgui\"==\"\" ]; then\n");
					fw.write("    printf \"\033[93mWARNING:\033[0m The windows console flag is set.\n\"");
					fw.write("else\n");
					fw.write("    printf \"WARNING: The windows console flag is set.\n\"");
					fw.write("fi\n");
				}
			}
			if (javaVerboseFlag == true) {
				if (isWin) {
					fw.write("if \"%jpgui%\"==\"\" (\n");
					fw.write("    echo \u001B[93mWARNING:\u001B[0m The Java verbose flag is set.\n");
					fw.write(") else (\n");
					fw.write("    echo WARNING: The Java verbose flag is set.\n");
					fw.write(")\n");
				} else {
					fw.write("if [ \"$jpgui\"==\"\" ]; then\n");
					fw.write("    printf \"\033[93mWARNING:\033[0m The Java verbose flag is set.\n\"");
					fw.write("else\n");
					fw.write("    printf \"WARNING: The Java verbose flag is set.\n\"");
					fw.write("fi\n");
				}
			}
			if (jpackageVerboseFlag == true) {
				if (isWin) {
					fw.write("if \"%jpgui%\"==\"\" (\n");
					fw.write("    echo \u001B[93mWARNING:\u001B[0m The Jpackage verbose flag is set.\n");
					fw.write(") else (\n");
					fw.write("    echo WARNING: The Jpackage verbose flag is set.\n");
					fw.write(")\n");
				} else {
					fw.write("if [ \"$jpgui\"==\"\" ]; then\n");
					fw.write("    printf \"\033[93mWARNING:\033[0m The Jpackage verbose flag is set.\n\"");
					fw.write("else\n");
					fw.write("    printf \"\033[93mWARNING:\033[0m The Jpackage verbose flag is set.\n\"");
					fw.write("fi\n");
				}
			}
			
			fw.write(cmdLine + "\n\n");
			
			if (isWin == true) {
				fw.write("if %errorlevel% neq 0 (\n");
				fw.write("    if \"%jpgui%\"==\"\" (\n");
				fw.write("        echo \u001B[91mFAILED:\u001B[0m Jpackage execution. ***\n");
				fw.write("    ) else (\n");
				fw.write("        echo FAILED: Jpackage execution. ***\n");
				fw.write("    )\n");
				fw.write("    exit /b 1\n");
				fw.write(")\n\n");
				fw.write("if \"%jpgui%\"==\"\" (\n");
				fw.write("    echo \u001B[92mDONE:\u001B[0m Executing jpackage command line.\n\n");
				fw.write(") else (\n");
				fw.write("    echo DONE: Executing jpackage command line.\n\n");
				fw.write(")\n");
			} else {
				fw.write("if [ $? -ne 0 ]; then\n");
				fw.write("    if [ \"$jpgui\"==\"\" ]; then\n");
				fw.write("        printf \"\033[91mFAILED:\033[0m Jpackage execution. ***\n\"");
				fw.write("    else\n");
				fw.write("        printf \"\033[91mFAILED:\033[0m Jpackage execution. ***\n\"");
				fw.write("    fi\n");
				fw.write("    exit 1\n");
				fw.write("fi\n\n");
				fw.write("if [ \"$jpgui\"==\"\" ]; then\n");
				fw.write("    printf \"\033[92mDONE:\033[0m Executing " + prjName + ".sh\n\"");
				fw.write("else\n");
				fw.write("    printf \"DONE: Executing " + prjName + ".sh\n\"");
				fw.write("fi\n\n");
					
			}
			
			if (postRun != null && postRun.isBlank() == false) {
				fw.write("echo Executing PostRun Script.\n");
				if (isWin == true) {
					fw.write("call \"" + postRun + "\"\n\n");
					fw.write("if %errorlevel% neq 0 (\n");
					fw.write("    if \"%jpgui%\"==\"\" (\n");
					fw.write("        echo \u001B[91mFAILED:\u001B[0m PostRun Script.\n");
					fw.write("    ) else (\n");
					fw.write("        echo FAILED: PostRun Script.\n");
					fw.write("    )\n");
					fw.write("    exit /b 1\n");
					fw.write(")\n\n");
					fw.write("if \"%jpgui%\"==\"\" (\n");
					fw.write("    echo \u001B[92mDONE:\u001B[0m Executing PostRun Script.\n\n");
					fw.write(") else (\n");
					fw.write("    echo DONE: Executing PostRun Script.\n");
					fw.write(")\n\n");
				} else {
					fw.write(postRun + "\n\n");
					fw.write("if [ $? -ne 0 ]; then\n");
					fw.write("    if \"$jpgui\"==\"\" ]; then\n");
					fw.write("        printf \"\033[91mFAILED:\033[0m PostRun Script.\n\"");
					fw.write("    else\n");
					fw.write("        printf \"FAILED: PostRun Script.\n\"");
					fw.write("    fi\n");
					fw.write("    exit 1\n");
					fw.write("fi\n");
					fw.write("if \"$jpgui\"==\"\" ]; then\n");
					fw.write("    printf \"\033[92mDONE:\033[0m Executing PostRun Script.\n\"");
					fw.write("else\n");
					fw.write("    printf \"DONE: Executing PostRun Script.\n\n\"");
					fw.write("fi\n\n");
					fw.write("exit 0\n");
				}
			}
			
			if (isWin) {
				fw.write("if \"%jpgui%\"==\"\" (\n");
				fw.write("    echo \u001B[97mFINISHED:\u001B[0m " + f.getName() + "\n");
				fw.write(") else (\n");
				fw.write("    echo FINISHED: " + f.getName() + "\n");
				fw.write(")\n");
			} else {
				fw.write("if \"$jpgui\"==\"\" ]; then\n");
				fw.write("    printf \"\033[97mFINISHED:\033[0m " + f.getName() + "\n\"");
				fw.write("else\n");
				fw.write("    printf \"FINISHED: " + f.getName() + "\n\"");
				fw.write("fi\n");
			}
			
			File prerun = new File(jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-prerun.bat");
	    	
			if (isWin == true) {
		    	if (prerun.exists() == false) {
		    		try {
						FileWriter w = new FileWriter(prerun.getAbsolutePath(), false);
						w.write("echo off\nrem PreRun script for project " + prjName + "\n\n");
						w.write("rem Place your code below this line.\n");
						w.write("rem Example: call :CP filename retcode\n");
						w.write("rem          if %retcode% neq 0 goto errAbort\n");
						w.write("rem Keep your code above this line.\n\n");
						w.write("exit /b 0\n\n");
						w.write(":errAbort\n");
						w.write("echo Abort batch file.\n");
						w.write("exit /b 1\n\n");
						
						w.write("rem Function to copy to input directory (jpackage --input).  Usage: call :CP filepath\n");
						w.write(":CP\n");
						w.write("set %~2=0\n");
						w.write("copy %1 " + jg.currPrj.getString("Application Image Options", jg.platform + " Input") + " >nul 2>&1\n");
						w.write("if %errorlevel% neq 0 (\n");
						w.write("    echo PreRun: Copy of %1 failed.\n");
						w.write("    set %~2=1\n");
						w.write(")\n");
						w.write("exit /b 0\n");
						w.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
			}
	    	
	    	if (isWin == false) {
		    	prerun = new File(jg.workspace.getAbsolutePath() + prjName + "/" + os + "-prerun.sh");
		    	
		    	if (prerun.exists() == false) {
		    		try {
						FileWriter w = new FileWriter(prerun.getAbsolutePath(), false);
						w.write("#! /usr/bin/sh\n# PreRun script for project " + prjName + "\n\n");
						
						w.write("# Function to copy to input directory (jpackage --input).  Usage: COPY filepath\n");
						w.write("COPY(){\n");
						w.write("cp $1 " + jg.currPrj.getString("Application Image Options", jg.platform + " Input") + " >/dev/null 2>&1\n");
						w.write("if [ $? -ne 0 ]; then\n");
						w.write("    echo PreRun: Copy of $1 failed.\n");
						w.write("    exit 1\n");
						w.write("fi\n}\n\n");
						w.write("# Place your code below this line.\n");
						w.write("# Keep your code above this line.\n\n");
						w.write("exit 0\n\n");
						w.close();
						prerun.setExecutable(true);
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
	    	}
	    	
	    	File postrun = new File(jg.workspace.getAbsolutePath() + File.separator + prjName + File.separator + os + "-postrun.bat");
	    	
	    	if (isWin == true) {
		    	if (postrun.exists() == false) {
		    		try {
						FileWriter w = new FileWriter(postrun.getAbsolutePath(), false);
						w.write("echo off\nrem PostRun script for project " + prjName + "\n\n");
						w.write("rem Place your code below this line.\n");
						w.write("rem Example: call :REMOVE filename retcode\n");
						w.write("rem          if %retcode% neq 0 goto errAbort\n");
						w.write("rem Keep your code above this line.\n\n");
						w.write("exit /b 0\n\n");
						
						w.write("rem Function to delete files.  Usage: call :REMOVE filepath\n");
						w.write(":REMOVE\n");
						w.write("del %1 >nul 2>&1\n");
						w.write("if %errorlevel% neq 0 (\n");
						w.write("    echo PostRun: Delete of %1 failed.\n");
						w.write("    exit /b 1\n");
						w.write(")\n");
						w.write("exit /b 0\n");
						w.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
	    	}
	    	
	    	if (isWin == false) {
		    	postrun = new File(jg.workspace.getAbsolutePath() + prjName + "/" + os + "-postrun.sh");
		    	
		    	if (postrun.exists() == false) {
		    		try {
						FileWriter w = new FileWriter(postrun.getAbsolutePath(), false);
						w.write("#! /usr/bin/sh\n# PostRun script for project " + prjName + "\n\n");
						
						w.write("# Function to delete files.  Usage: REMOVE filepath\n");
						w.write("REMOVE(){\n");
						w.write("rm -rf $1 >/dev/null 2>&1\n");
						w.write("if [ $? -ne 0 ]; then\n");
						w.write("    echo PostRun: Delete of %1 failed.\n");
						w.write("    exit 1\n");
						w.write("fi\n}\n\n");
						w.write("# Place your code below this line.\n");
						w.write("# Keep your code above this line.\n\n");
						w.write("exit 0\n\n");
						w.close();
						postrun.setExecutable(true);
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
	    	}
			
			if (isWin == true)
				jg.addStatus("Batch file created.");
			else
				jg.addStatus("Shell script created.");
			
			fw.close();
			if (isWin == false)
				f.setExecutable(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return f;
	}
	
	private void setMenu(boolean flag) {
		if (flag == true) {
			mActionRun.setDisable(true);
			mActionScript.setDisable(true);
		    mFileNewProject.setDisable(false);
		    mFileOpenProject.setDisable(false);
		    mFileImportProject.setDisable(false);
		    mFileExportProject.setDisable(true);
		    mFileQuit.setDisable(false);
		    mFileSaveAllProjects.setDisable(true);
		    mFileDeleteProject.setDisable(true);
		    mFileSaveProject.setDisable(true);
		    mHelpAbout.setDisable(false);
		    mScriptsEditPreRun.setDisable(true);
		    mScriptsEditPstRun.setDisable(true);
		    mScriptsEditMain.setDisable(true);
		} else {
			mActionRun.setDisable(false);
			mActionScript.setDisable(false);
		    mFileNewProject.setDisable(false);
		    mFileOpenProject.setDisable(false);
		    mFileImportProject.setDisable(false);
		    mFileExportProject.setDisable(false);
		    mFileQuit.setDisable(false);
		    mFileSaveAllProjects.setDisable(false);
		    mFileDeleteProject.setDisable(false);
		    mFileSaveProject.setDisable(false);
		    mHelpAbout.setDisable(false);
		    mScriptsEditPreRun.setDisable(false);
		    mScriptsEditPstRun.setDisable(false);
		    mScriptsEditMain.setDisable(false);
		}
	}
	
	private void setFields() {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		if (tab == null)
			return;
		
		String prjName = tab.getText();
		String os = cbPlatform.getValue().toLowerCase();
		
		ScrollPane sp = (ScrollPane)tab.getContent();
		AnchorPane ap = (AnchorPane)sp.getContent();
		VBox vb = (VBox)ap.getChildren().get(0);
		Accordion a = (Accordion)vb.getChildren().get(0);
		
		ObservableList<TitledPane> panes = a.getPanes();
		
		for (TitledPane p : panes) {
			
			VBox vb2 = (VBox)p.getContent();
			
			ObservableList<Node> nodes = vb2.getChildren();
			for (Node n : nodes) {
				HBox hb = (HBox)n;
				
				ObservableList<Node> fields = hb.getChildren();
				
				Label lb = null;
				TextField tf = null;
				CheckBox ckb = null;
				MyButton rs = null;
				MyButton sel = null;
				MyButton help = null;
				
				for (Node fld : fields) {
					if (fld instanceof Label) {
						lb = (Label)fld;
					} else if (fld instanceof TextField) {
						tf = (TextField)fld;
					} else if (fld instanceof CheckBox) {
						ckb = (CheckBox)fld;
					} else if (fld instanceof MyButton) {
						MyButton b = (MyButton)fld;
						if (b.getMyData().equals("?") == true)
							help = b;
						else if (b.getMyData().equals("r") == true) {
							rs = b;
						} else if (b.getMyData().equals("^") == true) {
							sel = b;
						}
					}
					
					if (help != null) {
						String h = (String)help.getUserData();
						if (os.equals("win") == true) {
							if (h.startsWith("--linux") == true || h.startsWith("--mac") == true) {
								lb.setDisable(true);
								if (tf != null)
									tf.setDisable(true);
								if (ckb != null)
									ckb.setDisable(true);
								if (rs != null)
									rs.setDisable(true);
								if (sel != null && jg.osType != 3)
									sel.setDisable(true);
								else if (sel != null)
									sel.setDisable(false);
							} else {
								lb.setDisable(false);
								if (tf != null)
									tf.setDisable(false);
								if (ckb != null)
									ckb.setDisable(false);
								if (rs != null)
									rs.setDisable(false);
								if (sel != null && jg.osType != 1)
									sel.setDisable(true);
								else if (sel != null)
									sel.setDisable(false);
							}
						} else if (os.equals("linux") == true) {
							if (h.startsWith("--win") == true || h.startsWith("--mac") == true) {
								lb.setDisable(true);
								if (tf != null)
									tf.setDisable(true);
								if (ckb != null)
									ckb.setDisable(true);
								if (rs != null)
									rs.setDisable(true);
								if (sel != null && jg.osType != 2)
									sel.setDisable(true);
								else if (sel != null)
									sel.setDisable(false);
							} else {
								lb.setDisable(false);
								if (tf != null)
									tf.setDisable(false);
								if (ckb != null)
									ckb.setDisable(false);
								if (rs != null)
									rs.setDisable(false);
								if (sel != null && jg.osType != 2)
									sel.setDisable(true);
								else if (sel != null)
									sel.setDisable(false);
							}
						} else if (os.equals("mac") == true) {
							if (h.startsWith("--win") == true || h.startsWith("--linux") == true) {
								lb.setDisable(true);
								if (tf != null)
									tf.setDisable(true);
								if (ckb != null)
									ckb.setDisable(true);
								if (rs != null)
									rs.setDisable(true);
								if (sel != null && jg.osType != 3)
									sel.setDisable(true);
								else if (sel != null)
									sel.setDisable(false);
							} else {
								lb.setDisable(false);
								if (tf != null)
									tf.setDisable(false);
								if (ckb != null)
									ckb.setDisable(false);
								if (rs != null)
									rs.setDisable(false);
								if (sel != null && jg.osType != 3)
									sel.setDisable(true);
								else if (sel != null)
									sel.setDisable(false);
							}
						}
					}
				}
			}
		}
		
		loadFields(prjName);
	}
	
	private void recentOpenProject() {
    	
    	if (jg.projectOpen != null) {
    		
    		ObservableList<Tab> tabs = tabPane.getTabs();
    		for (Tab tab : tabs) {
    			if (tab.getText().equalsIgnoreCase(jg.projectOpen) == true) {
    				tabPane.getSelectionModel().select(tab);
    				jg.showAlert("Dup Project", "You already have that project open.", AlertType.ERROR, false);
    				return;
    			}
    		}
    		
    		createPrjTab(jg.projectOpen);
    		
    		
    		
    		File f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_in");
    		File t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-in");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_out");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-out");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_in");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-in");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_out");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-out");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_in");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-in");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_out");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-out");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "_linux.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "-linux.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "_win.bat");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "-win.bat");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "_mac.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + jg.projectOpen + "-mac.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_prerun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-prerun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_prerun.bat");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-prerun.bat");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_prerun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-prerun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux_postrun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "linux-postrun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win_postrun.bat");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "win-postrun.bat");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		f = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac_postrun.sh");
    		t = new File(jg.workDir.getAbsolutePath() + File.separator + jg.projectOpen + File.separator + "mac-postrun.sh");
    		if (f.exists() == true)
    			f.renameTo(t);
    		
    		setMenu(false);
    	}
    }
}
