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
    	setFields();
    }

	    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		String os = System.getProperty("os.name").toLowerCase();
		
		platforms = FXCollections.observableArrayList();
		
		platforms.addAll("Win", "Linux", "Mac");
		
		cbPlatform.setItems(platforms);
		
		
		if (os.contains("win") == true) {
			cbPlatform.setValue("Win");
		} else if (os.contains("nux") == true || 
				os.contains("nix") == true || 
				os.contains("aix") == true || 
				os.contains("sunos") == true) {
			cbPlatform.setValue("Linux");
		} else if (os.contains("mac") == true) {
			cbPlatform.setValue("Mac");
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
		
		String prjName = tab.getText();
		
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
		
		Font font1 = Font.font("SansSerif", 16.0);
		
		String os = cbPlatform.getValue();
		
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
				String opt = arr[1].split(" ")[0];
				
				Label lbl = new Label(arr[0].substring(1) + ":");
				lbl.setUserData(arr[0].substring(1));
				lbl.setFont(font1);
				lbl.setPrefWidth(150.0);
				hb = new HBox();
				hb.setSpacing(4.0);
				hb.setPadding(new Insets(4, 4, 4, 4));
				hb.setAlignment(Pos.CENTER_LEFT);
				
				if (line.charAt(0) == '&') {
					
					CheckBox ckb = new CheckBox(arr[0].substring(1));
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
						}
					    titledPane.setStyle("-fx-text-fill: " + hColor);
					});
					
					hb.getChildren().addAll(lbl, ckb);
					vb2.getChildren().add(hb);
					if (opt.startsWith("--win") == true) {
						if (os.equals("Win") == false) {
							lbl.setDisable(true);
							ckb.setDisable(true);
						}
					} else if (opt.startsWith("--linux") == true) {
						if (os.equals("Linux") == false) {
							lbl.setDisable(true);
							ckb.setDisable(true);
						}
					} else if (opt.startsWith("--mac") == true) {
						if (os.equals("Mac") == false) {
							lbl.setDisable(true);
							ckb.setDisable(true);
						}
					}
				} else if (line.charAt(0) == '*') {
					
					TextField tf = new TextField();
					tf.setFont(font1);
					final TitledPane titledPane = tp;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
//							System.out.println("textfield changed from " + oldValue + " to " + newValue);
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					hb.getChildren().addAll(lbl, tf);
					vb2.getChildren().add(hb);
					
					if (opt.startsWith("--win") == true) {
						if (os.equals("Win") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
						}
					} else if (opt.startsWith("--linux") == true) {
						if (os.equals("Linux") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
						}
					} else if (opt.startsWith("--mac") == true) {
						if (os.equals("Mac") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
						}
					}
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '-') {
					
					TextField tf = new TextField();
					tf.setFont(font1);
					final TitledPane titledPane = tp;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
//							System.out.println("textfield changed from " + oldValue + " to " + newValue);
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					Button b1 = new Button("^");
					b1.setStyle("-fx-font-size: 14px;");
					b1.setPrefWidth(30.0);
					b1.setOnAction((e) -> {
						Stage stage = (Stage) aPane.getScene().getWindow();
						File df = dc.showDialog(stage);
						if (df != null) {
							tf.setText(df.getAbsolutePath());
						}
					});
					
					Tooltip tt = new Tooltip("Select a directory.");
					tt.setStyle("-fx-font-size: 14px;");
					b1.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf, b1);
					vb2.getChildren().add(hb);
					
					if (opt.startsWith("--win") == true) {
						if (os.equals("Win") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
							b1.setDisable(true);
						}
					} else if (opt.startsWith("--linux") == true) {
						if (os.equals("Linux") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
							b1.setDisable(true);
						}
					} else if (opt.startsWith("--mac") == true) {
						if (os.equals("Mac") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
							b1.setDisable(true);
						}
					}
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '+') {
					
					TextField tf = new TextField();
					tf.setFont(font1);
					final TitledPane titledPane = tp;
					tf.textProperty().addListener((observable, oldValue, newValue) -> {
						if (jg.loadFlag == true)
							return;
						if (newValue != null) {
//							System.out.println("textfield changed from " + oldValue + " to " + newValue);
							Tab t = tabPane.getSelectionModel().getSelectedItem();
							if (t != null) {
								ObservableList<String> c = t.getStyleClass();
								if (c.contains("dirty") == false)
									t.getStyleClass().add("dirty");
							}
							titledPane.setStyle("-fx-text-fill: " + hColor);
						}
					});
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					Button b1 = new Button("^");
					b1.setFont(font1);
					b1.setPrefWidth(30.0);
					b1.setOnAction((e) -> {
						Stage stage = (Stage) aPane.getScene().getWindow();
						File fs = fc.showOpenDialog(stage);
						if (fs != null) {
							tf.setText(fs.getAbsolutePath());
						}
					});
					
					Tooltip tt = new Tooltip("Select a file.");
					tt.setFont(font1);
					b1.setTooltip(tt);
					
					hb.getChildren().addAll(lbl, tf, b1);
					vb2.getChildren().add(hb);
					
					if (opt.startsWith("--win") == true) {
						if (os.equals("Win") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
							b1.setDisable(true);
						}
					} else if (opt.startsWith("--linux") == true) {
						if (os.equals("Linux") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
							b1.setDisable(true);
						}
					} else if (opt.startsWith("--mac") == true) {
						if (os.equals("Mac") == false) {
							lbl.setDisable(true);
							tf.setDisable(true);
							b1.setDisable(true);
						}
					}
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				}
				
				// Add Help button.
				
				Button qb = new Button("?");
				qb.setFont(font1);
				qb.setOnAction((e) -> {
					TextArea ta = (TextArea)popup.getContent().get(0);
					
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
				
				hb.getChildren().add(qb);
				
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
		
		loadFields(prjName);
	}
 	
	private void loadFields(String prjName) {
		IniFile ini = jg.prjList.get(prjName);
		
		if (ini == null)
			return;
		
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
			
			ObservableList<Node> nodes = vb2.getChildren();
			for (Node n : nodes) {
				HBox hb = (HBox)n;
				
				ObservableList<Node> fields = hb.getChildren();
				
				Label lbl = (Label)fields.get(0);
				
				if (fields.get(1) instanceof TextField) {
					TextField tf = (TextField)fields.get(1);
					String fld = (String) lbl.getUserData();
					String value = ini.getString(sec, fld);
					tf.setText(value);
				} else if (fields.get(1) instanceof CheckBox) {
					CheckBox ckb = (CheckBox)fields.get(1);
					boolean yesNo = ini.getBoolean(sec, lbl.getUserData());
					ckb.setSelected(yesNo);
				}
			}
		}
		
		jg.loadFlag = false;
	}
	
	private void saveProject(Tab tab) {
		if (tab == null) {
			jg.addStatus("Save, no project tab selected.");
			return;
		}
		
		String prjName = tab.getText();
		
		jg.addStatus(prjName);
		
		IniFile ini = jg.prjList.get(prjName);
		
		if (ini == null)
			return;
		
		tab.getStyleClass().remove("dirty");
		
		ScrollPane sp = (ScrollPane)tab.getContent();
		AnchorPane ap = (AnchorPane)sp.getContent();
		VBox vb = (VBox)ap.getChildren().get(0);
		Accordion a = (Accordion)vb.getChildren().get(0);
		
		ObservableList<TitledPane> panes = a.getPanes();
		
		for (TitledPane p : panes) {
			String sec = p.getText();
			if (ini.sectionExists(sec) == true)
				ini.removeSection(sec);
			
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
					ini.addValuePair(sec, (String)lbl.getUserData(), value);
				} else if (fields.get(1) instanceof CheckBox) {
					CheckBox ckb = (CheckBox)fields.get(1);
					ini.addValuePair(sec, (String)lbl.getUserData(), ckb.isSelected() + "");
				}
			}
		}
		
		ini.writeFile(true);
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
    	String prjname = tab.getText();
    	
		if (os.equals("win") == true) {
			fn += ".bat";
			isWin = true;
		} else if (os.equals("linux") == true || os.equals("mac") == true) {
			fn += ".sh";
			isWin = false;
		}
		
		File f = new File(System.getProperty("user.home") + File.separator + "JpGui" + File.separator + "projects" + 
					File.separator + prjname + File.separator + fn);
		
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
			String preRun = null;
			String postRun = null;
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
						} else if (lbl.getText().toLowerCase().equals("win prerun:") == true ||
								lbl.getText().toLowerCase().equals("linux prerun:") == true ||
								lbl.getText().toLowerCase().equals("mac prerun:") == true) {
							preRun = value;
						} else if (lbl.getText().toLowerCase().equals("win postrun:") == true ||
								lbl.getText().toLowerCase().equals("linux postrun:") == true ||
								lbl.getText().toLowerCase().equals("mac postrun:") == true) {
							postRun = value;
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
				fw.write("@echo off\nrem Batch file to execute a jpackage command line.\nrem Generated by the JpGui program.\n\n");
				fw.write("echo Deleting " + appName + "-" + appVersion + ".exe\n");
				fw.write("del /F /Q \"" + dest + File.separator + appName + "-" + appVersion + ".exe\"\n");
				fw.write("if exist \"" + dest + File.separator + appName + "-" + appVersion + ".exe\" goto errdel\n\n");
				fw.write("goto next2\n\n");
				fw.write(":errdel\n");
				fw.write("echo Failed to delete file.\n");
				fw.write("exit /b 1\n\n");
				fw.write(":next2\n\n");
			} else {
				fw.write("#!/usr/sh\n# Shell script to execute a jpackage command line.\n# Generated by the JpGui program.\n\n");
				fw.write("echo Deleting " + appName + "-" + appVersion + "\n");
				fw.write("rm -rf " + dest + File.separator + appName + "-" + appVersion + "\n\n");
				fw.write("if [ -f \"" + dest + File.separator + appName + "-" + appVersion + "\" ]; then\n");
				fw.write("   echo Failed to delete file.\n");
				fw.write("   exit 1\n");
				fw.write("fi\n\n");
			}
			
			if (preRun != null && preRun.isBlank() == false) {
				fw.write("echo Executing PreRun Script.\n");
				if (isWin == true) {
					fw.write("call \"" + preRun + "\"\n\n");
					fw.write("if errorlevel 1 goto errmsg1\n\n");
					fw.write("echo Done executing PreRun Script.\n\n");
					fw.write("goto next1\n\n");
					
					fw.write(":errmsg1\n");
					fw.write("echo " + postRun + " FAILED.\n");
					fw.write("exit /b 1\n\n");
					
					fw.write(":next1\n\n");
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
			
			fw.write("echo Running jpackage command line.  This can take a few...\n");
			fw.write(cmdLine + "\n\n");
			
			if (postRun != null && postRun.isBlank() == false) {
				fw.write("echo Executing PostRun Script.\n");
				if (isWin == true) {
					fw.write("call \"" + postRun + "\"\n\n");
					fw.write("if errorlevel 1 goto errmsg2\n\n");
					fw.write("echo Done executing PostRun Script.\n\n");
					fw.write("goto finished\n\n");
					
					fw.write(":errmsg2\n");
					fw.write("echo " + postRun + " FAILED.\n");
					fw.write("exit /b 1\n\n");
				} else {
					fw.write(postRun + "\n\n");
					fw.write("if [ $? -ne 0 ]; then\n");
					fw.write("    echo post run script FAILED.\n");
					fw.write("    exit 1\n");
					fw.write("fi\n\n");
				}
			}
			
			if (isWin == true) {
				fw.write(":finished\nexit /b 0\n");
			} else {
				fw.write("exit 0\n");
			}
			
			if (isWin == true)
				jg.addStatus("Batch file created.");
			else
				jg.addStatus("Shell script created.");
			
			fw.close();
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
		}
	}
	
	private void setFields() {
		Tab tab = tabPane.getSelectionModel().getSelectedItem();
		if (tab == null)
			return;
		
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
				Button sel = null;
				Button help = null;
				
				for (Node fld : fields) {
					if (fld instanceof Label) {
						lb = (Label)fld;
					} else if (fld instanceof TextField) {
						tf = (TextField)fld;
					} else if (fld instanceof CheckBox) {
						ckb = (CheckBox)fld;
					} else if (fld instanceof Button) {
						Button b = (Button)fld;
						if (b.getUserData() != null)
							help = b;
						else
							sel = b;
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
								if (sel != null)
									sel.setDisable(true);
							} else {
								lb.setDisable(false);
								if (tf != null)
									tf.setDisable(false);
								if (ckb != null)
									ckb.setDisable(false);
								if (sel != null)
									sel.setDisable(false);
							}
						} else if (os.equals("linux") == true) {
							if (h.startsWith("--win") == true || h.startsWith("--mac") == true) {
								lb.setDisable(true);
								if (tf != null)
									tf.setDisable(true);
								if (ckb != null)
									ckb.setDisable(true);
								if (sel != null)
									sel.setDisable(true);
							} else {
								lb.setDisable(false);
								if (tf != null)
									tf.setDisable(false);
								if (ckb != null)
									ckb.setDisable(false);
								if (sel != null)
									sel.setDisable(false);
							}
						} else if (os.equals("mac") == true) {
							if (h.startsWith("--win") == true || h.startsWith("--linux") == true) {
								lb.setDisable(true);
								if (tf != null)
									tf.setDisable(true);
								if (ckb != null)
									ckb.setDisable(true);
								if (sel != null)
									sel.setDisable(true);
							} else {
								lb.setDisable(false);
								if (tf != null)
									tf.setDisable(false);
								if (ckb != null)
									ckb.setDisable(false);
								if (sel != null)
									sel.setDisable(false);
							}
						}
					}
				}
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
