package application;

import java.io.BufferedReader;
import java.io.File;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class JpGuiController implements Initializable, RefreshScene {

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
    private MenuItem mFileOpenProject;
    
    @FXML
    private MenuItem mFileDeleteProject;

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
    private Menu menuAction;

    @FXML
    private Menu menuFile;

    @FXML
    private Menu menuHelp;
    
    @FXML
    private Menu menuScripts;
    
    @FXML
    private Button btnQuit;
    
    private JpGlobal jg = JpGlobal.getInstance();
    ObservableList<String> platforms = null;
    Popup popup = null;
    DirectoryChooser dc = null;
    FileChooser fc = null;
    int lastBtn = -1;
    
    @FXML
    void doSetOptions(ActionEvent event) {
    	jg.setStatus("Setting Options");
    	jg.centerScene(aPane, "Options.fxml", "Setting Options", null);
    }
    
    @FXML
    void doActionRun(ActionEvent event) {
    	jg.setStatus("Run Project");
    	File script = buildScript();
    	
    	String v = jg.currPrj.getString("Generic Options", "App Version");
    	if (v == null) {
    		jg.showAlert("User Error", "No 'App Version' given.", AlertType.ERROR, false);
    		return;
    	}
    	
    	String[] cmd = { "cmd.exe", "/C", script.getAbsolutePath(), v };
    	
    	ProcessRet pr = jg.runProcess(cmd);
    	
    	jg.showOutput("Run Output", pr.getOutput(), AlertType.INFORMATION, false);
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
				ButtonInfo[] buttons = {
						new ButtonInfo("Yes", 1, false),
						new ButtonInfo("No", 2, false),
						new ButtonInfo("Cancel", 3, true),
					};
					ButtonInfo bi = jg.centerDialog(aPane, "Save Changes", 
							"You have unsaved changes.\n" +
							"To NOT save changes select 'No' else\n" +
							"select either 'Yes' or 'Cancel' and then used\n" +
							"Menu File -> 'Save Project' or 'Save all Projects'.\n" +
							"Save changes?", null, buttons);
					if (bi.getReValue() == 1) {
						return;
					} else if (bi.getReValue() == 2) {
					} else {
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
    void doFileOpenProject(ActionEvent event) {
    	jg.setStatus("Opening project");
    	jg.centerScene(aPane, "PrjOpen.fxml", "Open Project", null);
    	
    	if (jg.projectOpen != null) {
    		ObservableList<Tab> tabs = tabPane.getTabs();
    		for (Tab tab : tabs) {
    			if (tab.getText().equalsIgnoreCase(jg.projectOpen) == true) {
    				jg.showAlert("Dup Project", "You already have that project open.", AlertType.ERROR, false);
    				return;
    			}
    		}
    		createPrjTab(jg.projectOpen);
    		
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
    		File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + 
    					File.separator + "projects" + File.separator + tab.getText() + File.separator + tab.getText() + ".ini");
    		if (f.exists() == true)
    			f.delete();
    		
    		jg.sysIni.removeValuePair("Projects", tab.getText());
    		jg.sysIni.writeFile(true);
    		
    		f = new File(System.getProperty("user.home") + File.separator + "JpGui" + 
    					File.separator + "projects" + File.separator + tab.getText());
    		if (f.exists() == true)
    			jg.deleteDir(f);
    		
    		tabPane.getTabs().remove(tab);
    		if (tabPane.getTabs().size() <= 0)
    			setMenu(true);
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
    	
    	File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator + "projects" + 
				File.separator + tab.getText() + File.separator + jg.platform.toLowerCase() + "_prerun" + (jg.platform.equals("Win")? ".bat" : ".sh"));
    	
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
    	
    	File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator + "projects" + 
				File.separator + tab.getText() + File.separator + jg.platform.toLowerCase() + "_postrun" + (jg.platform.equals("Win")? ".bat" : ".sh"));
    	
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
    	
    	File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator + "projects" + 
				File.separator + tab.getText() + File.separator + tab.getText() + "_" + jg.platform.toLowerCase() + (jg.platform.equals("Win")? ".bat" : ".sh"));
    	
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
		dc = new DirectoryChooser();
		
		setMenu(true);
	}
	
	private void createPrjTab(String prjName) {
		
		File pf = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
						"projects" + File.separator + prjName + File.separator + prjName + ".ini");
		
		jg.currPrj = new IniFile(pf.getAbsolutePath());
		
		IniFile org = new IniFile(pf.getAbsolutePath());
		
		jg.orgList.put(prjName, org);
		
		if (jg.currPrj.sectionExists("JpGui Options") == false)
			jg.currPrj.addSection("JpGui Options");
		
		String os = jg.platform.toLowerCase();
		
		if (jg.osType == 1) {
			jg.currPrj.addValuePair("JpGui Options", "Win PreRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_prerun.bat");
			jg.currPrj.addValuePair("JpGui Options", "Win PostRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_postrun.bat");
			org.addValuePair("JpGui Options", "Win PreRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_prerun.bat");
			org.addValuePair("JpGui Options", "Win PostRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_postrun.bat");
		} else if (jg.osType == 2) {
			jg.currPrj.addValuePair("JpGui Options", "Linux PreRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_prerun.sh");
			jg.currPrj.addValuePair("JpGui Options", "Linux PostRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_postrun.sh");
			org.addValuePair("JpGui Options", "Linux PreRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_prerun.sh");
			org.addValuePair("JpGui Options", "Linux PostRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_postrun.sh");
		} else if (jg.osType == 3) {
			jg.currPrj.addValuePair("JpGui Options", "Mac PreRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_prerun.sh");
			jg.currPrj.addValuePair("JpGui Options", "Linux PostRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_postrun.sh");
			org.addValuePair("JpGui Options", "Mac PreRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_prerun.sh");
			org.addValuePair("JpGui Options", "Linux PostRun Script", 
					System.getProperty("user.home") + File.separator + "JpGui" + File.separator + 
					"projects" + File.separator + prjName + File.separator + os + "_postrun.sh");
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
		
		InputStream undoImg = getClass().getResourceAsStream("/images/undo.png");
		Image imgUndo = new Image(undoImg, 18, 18, false, false);
		InputStream dirImg = getClass().getResourceAsStream("/images/folder.png");
		Image imgDir = new Image(dirImg, 18, 18, false, false);
		InputStream fileImg = getClass().getResourceAsStream("/images/file_icon.png");
		Image imgFile = new Image(fileImg, 18, 18, false, false);
		InputStream helpImg = getClass().getResourceAsStream("/images/help_icon.png");
		Image imgHelp = new Image(helpImg, 18, 18, false, false);
		
		Font font1 = Font.font("SansSerif", 16.0);
		
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
//				String opt = arr[1].split(" ")[0];
				
				Label lbl = new Label(arr[0].substring(1) + ":");
				lbl.setUserData(arr[0].substring(1));
				lbl.setFont(font1);
				lbl.setPrefWidth(150.0);
				hb = new HBox();
				hb.setSpacing(4.0);
				hb.setPadding(new Insets(4, 4, 4, 4));
				hb.setAlignment(Pos.CENTER_LEFT);
				
				if (line.charAt(0) == '&') {
					
					ckb = new CheckBox(arr[0].substring(1));
					ckb.setUserData(arr[0].substring(1));
					ckb.setFont(font1);
					ckb.setPrefWidth(250);
					final TitledPane titledPane = tp;
					ckb.selectedProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
//					    System.out.println("checkbox changed from " + oldValue + " to " + newValue);
					    Tab t = tabPane.getSelectionModel().getSelectedItem();
						if (t != null) {
							ObservableList<String> c = t.getStyleClass();
							if (c.contains("dirty") == false)
								t.getStyleClass().add("dirty");
							jg.currPrj.addValuePair(titledPane.getText(), (String)lbl.getUserData(), newValue.toString());
						}
					    titledPane.setStyle("-fx-text-fill: " + hColor);
					});
					
					hb.getChildren().addAll(lbl, ckb);
					vb2.getChildren().add(hb);
				} else if (line.charAt(0) == '*') {
					
					tf = new TextField();
					tf.setFont(font1);
					final TitledPane titledPane = tp;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
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
					tf.setFont(font1);
					final TitledPane titledPane = tp;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					sb = new MyButton();
					sb.setMyData("^");
					sb.setGraphic(new ImageView(imgDir));
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
				} else if (line.charAt(0) == '+') {
					
					tf = new TextField();
					tf.setFont(font1);
					final TitledPane titledPane = tp;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
								jg.currPrj.addValuePair(titledPane.getText(), (String)lbl.getUserData(), newValue);
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					sb = new MyButton();
					sb.setMyData("^");
					sb.setGraphic(new ImageView(imgFile));
					sb.setFont(font1);
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
					tt.setFont(font1);
					sb.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				}
				
				// Add reset button.
				
				MyButton rs = new MyButton();
				rs.setMyData("r");
				rs.setPrefSize(18.0, 18.0);
				rs.setGraphic(new ImageView(imgUndo));
				rs.setFont(font1);
				Tooltip tt2 = new Tooltip("Reset field.");
				tt2.setFont(font1);
				rs.setTooltip(tt2);
				
				final TitledPane tp2 = tp;
				
				rs.setOnMouseClicked((e) -> {
					Button b = (Button)e.getSource();
					HBox hb2 = (HBox)b.getParent();
					if (hb2.getChildren().get(1) instanceof TextField) {
						TextField tf2 = (TextField) hb2.getChildren().get(1);
						tf2.setText((String)tf2.getUserData());
						jg.currPrj.addValuePair(tp2.getText(), jg.platform + " " + (String)lbl.getUserData(), (String)tf2.getUserData());
					}
					if (hb2.getChildren().get(1) instanceof CheckBox) {
						CheckBox ckb2 = (CheckBox) hb2.getChildren().get(1);
						ckb2.setSelected((Boolean)ckb2.getUserData());
						jg.currPrj.addValuePair(tp2.getText(), jg.platform + " " + (String)lbl.getUserData(), ckb2.getUserData().toString());
					}
						
				});
				
				// Add Help button.
				
				MyButton qb = new MyButton();
				qb.setMyData("?");
				qb.setGraphic(new ImageView(imgHelp));
				qb.setFont(font1);
				qb.setOnAction((e) -> {
					TextArea ta = (TextArea)popup.getContent().get(0);
					ta.setPrefWidth(500.0);
					ta.setPrefHeight(225.0);
					
					Button bh = (Button)e.getSource();
					String help = (String)bh.getUserData();
					Stage s = (Stage)bh.getScene().getWindow();
					
					ta.setText(help);
					
					if (popup.isShowing() == false)
						popup.show(s);
					else {
						popup.hide();
						popup.show(s);
					}
				});
				
				if (sb != null)
					hb.getChildren().addAll(rs, sb, qb);
				else
					hb.getChildren().addAll(rs, qb);
				
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
				}
			}
		}
		
		ini.writeFile(true);
		
		jg.prjList.put(prjName, ini);
	}
	
	private File buildScript() {
		String os = cbPlatform.getValue().toLowerCase();
    	
    	Tab tab = tabPane.getSelectionModel().getSelectedItem();
    	
    	if (tab == null) {
    		jg.addStatus("No tab selected.");
    		return null;
    	}
		
    	boolean isWin = false;
    	String fn = tab.getText();
    	String prjName = tab.getText();
    	
		if (os.contains("win") == true) {
			fn += "_win.bat";
			isWin = true;
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			fn += "_linux.sh";
			isWin = false;
		} else if (os.contains("mac") == true) {
			fn += "_mac.sh";
			isWin = false;
		}
		
		File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator + "projects" + 
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
			String appVersion = null;
			boolean verboseFlag = false;
			boolean jpackageVerFlag = false;
			
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
							appVersion = value;
						} else if (lbl.getText().toLowerCase().equals("jpackage path:") == true) {
							jg.jpackagePath = value;
						}
						if (value != null && value.isBlank() == false) {
							if (paneName.equals("jpgui options") == true) {
								continue;
							}
							if (lbl.getText().toLowerCase().equals("java options:") == true) {
								if (isWin == true)
									cmdLine += " ^\n" + opt + " '" + value + "'";
								else
									cmdLine += " \\\n" + opt + " \"" + value + "\"";
							} else {
								if (isWin == true)
									cmdLine += " ^\n" + opt + " \"" + value + "\"";
								else
									cmdLine += " \\\n" + opt + " \"" + value + "\"";
							}
						}
					} else if (fields.get(1) instanceof CheckBox) {
						CheckBox ckb = (CheckBox)fields.get(1);
						boolean value = ckb.isSelected();
						if (lbl.getText().toLowerCase().equals("verbose output:") == true) {
							verboseFlag = value;
						} else if (lbl.getText().toLowerCase().equals("jpackage version:") == true) {
							jpackageVerFlag = value;
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
			
			if (verboseFlag == true)
				cmdLine += " ^\n--verbose";
			
			if (appVersion == null || appVersion.isBlank() == true) {
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
				fw.write("if exist \"" + dest + File.separator + appName + "-" + appVersion + ".exe\" (\n");
				fw.write("    echo Deleting old " + appName + "-" + appVersion + ".exe\n");
				fw.write("    del /F /Q \"" + dest + File.separator + appName + "-" + appVersion + ".exe\"\n");
				fw.write("    if exist \"" + dest + File.separator + appName + "-" + appVersion + ".exe\" (\n");
				fw.write("        echo Delete of " + appName + "-" + appVersion + ".exe FAILED.\n");
				fw.write("        exit /b 1\n");
				fw.write("    )\n");
				fw.write(")\n\n");
			} else {
				fw.write("#!/usr/bin/sh\n# Shell script to execute a jpackage command line.\n# Generated by the JpGui program.\n");
				fw.write("# Changes made to this script will be lost if regenerated.\n\n");
				fw.write("echo Deleting old " + appName + "-" + appVersion + "\n");
				fw.write("rm -rf " + dest + File.separator + appName + "-" + appVersion + "\n\n");
				fw.write("if [ -f \"" + dest + File.separator + appName + "-" + appVersion + "\" ]; then\n");
				fw.write("   echo Failed to delete \""  + dest + File.separator + appName + "-" + appVersion + "\"\n");
				fw.write("   exit 1\n");
				fw.write("fi\n\n");
			}
			
			String preRun = jg.currPrj.getString("JpGui Options", jg.platform + " PreRun Script");
			String postRun = jg.currPrj.getString("JpGui Options", jg.platform + " PostRun Script");;
			
			if (preRun != null && preRun.isBlank() == false) {
				fw.write("echo Executing PreRun Script.\n");
				if (isWin == true) {
					fw.write("call \"" + preRun + "\"\n\n");
					fw.write("if %errorlevel% neq 0 (\n");
					fw.write("    echo PostRun Script FAILED.\n");
					fw.write("    exit /b 1\n");
					fw.write(")\n\n");
					fw.write("echo Done executing PreRun Script.\n\n");
				} else {
					fw.write(preRun + "\n\n");
					fw.write("if [ $? -ne 0 ]; then\n");
					fw.write("    echo pre run script FAILED.\n");
					fw.write("    exit 1\n");
					fw.write("fi\n\n");
				}
			}
			
			if (jpackageVerFlag == true) {
				fw.write("echo Jpackage: " + jg.jpackageVersion + "\n\n");
			}
			
			fw.write("echo Creating Application Version: " + appVersion + "\n\n");
			
			fw.write("echo Running jpackage command line.  This can take a few...\n");
			fw.write(cmdLine + "\n\n");
			
			if (isWin == true) {
				fw.write("if %errorlevel% neq 0 (\n");
				fw.write("    echo *** Jpackage failed. ***\n");
				fw.write("    exit /b 1\n");
				fw.write(")\n\n");
				fw.write("echo Finished executing jpackage command line.\n\n");
			} else {
				fw.write("if [ $? -ne 0 ]; then\n");
				fw.write("    echo *** Jpackage failed. ***\n");
				fw.write("    exit 1\n");
				fw.write("fi\n\n");
				fw.write("echo Finished executing " + prjName + ".sh\n\n");
			}
			
			if (postRun != null && postRun.isBlank() == false) {
				fw.write("echo Executing PostRun Script.\n");
				if (isWin == true) {
					fw.write("call \"" + postRun + "\"\n\n");
					fw.write("if %errorlevel% neq 0 (\n");
					fw.write("    echo PostRun Script FAILED.\n");
					fw.write("    exit /b 1\n");
					fw.write(")\n\n");
					fw.write("echo Done executing PostRun Script.\n\n");
				} else {
					fw.write(postRun + "\n\n");
					fw.write("if [ $? -ne 0 ]; then\n");
					fw.write("    echo post run script FAILED.\n");
					fw.write("    exit 1\n");
					fw.write("fi\n\nexit 0\n");
				}
			}
			
			File prerun = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
					"projects" + File.separator + prjName + File.separator + os + "_prerun.bat");
	    	
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
		    	prerun = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
						"projects" + File.separator + prjName + File.separator + os + "_prerun.sh");
		    	
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
	    	
	    	File postrun = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
					"projects" + File.separator + prjName + File.separator + os + "_postrun.bat");
	    	
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
		    	postrun = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator +
						"projects" + File.separator + prjName + File.separator + os + "_postrun.sh");
		    	
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
