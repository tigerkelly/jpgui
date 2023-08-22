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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

public class PrjNewWizardController implements Initializable, RefreshScene {

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane aPane;
    
    @FXML
    private TextArea taProjectDesc;
    
    @FXML
    private VBox vbox;

    @FXML
    private TextField tfProjectName;
    
    private JpGlobal jg = JpGlobal.getInstance();
    private Popup popup = null;
    private DirectoryChooser dc = null;
    private FileChooser fc = null;
//    private TextField tfMainJar = null;
    
    private TextField packageType = null;
    private TextField appVersion = null;
    private TextField copyright = null;
    private TextField description = null;
    private TextField appName = null;
    private TextField vendor = null;
    private TextField addModules = null;
    private TextField destination = null;
    private TextField input = null;
    private TextField icon = null;
    private TextField modulePath = null;
    private TextField mainClass = null;
    private TextField mainJar = null;
    private CheckBox shortcut = null;
    
    @FXML
    void doBtnCancel(ActionEvent event) {
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
    	
    	File f1 = new File(jg.workDir.getAbsolutePath() + File.separator + prjName);
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
    	
    	File iWin = new File(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "win-in");
    	if (iWin.exists() == false)
    		iWin.mkdirs();
    	
    	File keepDir = new File(iWin.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File oWin = new File(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "win-out");
    	if (oWin.exists() == false)
    		oWin.mkdirs();
    	
    	keepDir = new File(oWin.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File iLinux = new File(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "linux-in");
    	if (iLinux.exists() == false)
    		iLinux.mkdirs();
    	
    	keepDir = new File(iLinux.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File oLinux = new File(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "linux-out");
    	if (oLinux.exists() == false)
    		oLinux.mkdirs();
    	
    	keepDir = new File(oLinux.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File iMac = new File(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "mac-in");
    	if (iMac.exists() == false)
    		iMac.mkdirs();
    	
    	keepDir = new File(iMac.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	File oMac = new File(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "mac-out");
    	if (oMac.exists() == false)
    		oMac.mkdirs();
    	
    	keepDir = new File(oMac.getAbsolutePath() + File.separator + ".keepdir");
    	try {
			keepDir.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	// Add the values to the ini file.
    	
    	IniFile prjIni = new IniFile(prjIniFile.getAbsolutePath());
    	
    	String os = "Unknown ";
    	if (jg.osType == 1)
    		os = "Win ";
    	else if (jg.osType == 2)
    		os = "Linux ";
    	else if (jg.osType == 3)
    		os = "Mac ";
    	
    	prjIni.addSection("JpGui Options");
    	prjIni.addSection("Generic Options");
    	prjIni.addSection("Runtime Image Options");
    	prjIni.addSection("Application Image Options");
    	prjIni.addSection("Application Launcher(s) Options");
    	prjIni.addSection("Platform Dependent Launcher Options");
    	prjIni.addSection("Application Package Options");
    	prjIni.addSection("Platform Dependent Package Options");
    	
    	if (packageType != null)
    		prjIni.addValuePair("Generic Options", os + "Package Type", packageType.getText());
    	else
    		System.out.println("packagetype");
    	if (appVersion != null)
    		prjIni.addValuePair("Generic Options", os + "App Version", appVersion.getText());
    	else
    		System.out.println("addversino");
    	if (copyright != null)
    		prjIni.addValuePair("Generic Options", os + "Copyright", copyright.getText());
    	else
    		System.out.println("copyrioght");
    	if (description != null)
    		prjIni.addValuePair("Generic Options", os + "Description", description.getText());
    	else
    		System.out.println("desc");
    	if (appName != null)
    		prjIni.addValuePair("Generic Options", os + "App Name", appName.getText());
    	else
    		System.out.println("appanem");
    	if (vendor != null)
    		prjIni.addValuePair("Generic Options", os + "Vendor", vendor.getText());
    	else
    		System.out.println("vendor");
    	if (addModules != null)
    		prjIni.addValuePair("Runtime Image Options", os + "Add Modules", addModules.getText());
    	else
    		System.out.println("addmodules");
    	if (destination != null)
    		prjIni.addValuePair("Generic Options", os + "Destination", destination.getText());
    	else
    		System.out.println("destination");
    	if (input != null)
    		prjIni.addValuePair("Application Image Options", os + "Input", input.getText());
    	else
    		System.out.println("input");
    	if (icon != null)
    		prjIni.addValuePair("Generic Options", os + "Icon", icon.getText());
    	else
    		System.out.println("icon");
    	if (modulePath != null)
    		prjIni.addValuePair("Runtime Image Options", os + "Module Path", modulePath.getText());
    	else
    		System.out.println("modulepath");
    	if (mainClass != null)
    		prjIni.addValuePair("Runtime Image Options", os + "Main Class", mainClass.getText());
    	else
    		System.out.println("mainclass");
    	if (mainJar != null)
    		prjIni.addValuePair("Runtime Image Options", os + "Main Jar", mainJar.getText());
    	else
    		System.out.println("mainjar");
    	if (shortcut != null)
    		prjIni.addValuePair("Platform Dependent Package Options", os + "Shortcut", shortcut.isSelected() + "");
    	else
    		System.out.println("shortcurt");
    	
    	if (jg.osType == 1)
    		prjIni.addValuePair("Generic Options", os + "Destination", oWin.getAbsolutePath());
    	else if (jg.osType == 2)
    		prjIni.addValuePair("Generic Options", os + "Destination", oLinux.getAbsolutePath());
    	else if (jg.osType == 3)
    		prjIni.addValuePair("Generic Options", os + "Destination", oMac.getAbsolutePath());
    	
    	if (jg.osType == 1)
    		prjIni.addValuePair("Application Image Options", os + "Input", iWin.getAbsolutePath());
    	else if (jg.osType == 1)
    		prjIni.addValuePair("Application Image Options", os + "Input", iLinux.getAbsolutePath());
    	else if (jg.osType == 1)
    		prjIni.addValuePair("Application Image Options", os + "Input", iMac.getAbsolutePath());
    	
    	prjIni.addValuePair("JpGui Options", os + "Jpackage Version", "true");
    	
    	prjIni.writeFile(true);
    	
    	jg.sysIni.writeFile(true);
    	
    	Stage stage = (Stage) aPane.getScene().getWindow();
        stage.close();
    }
	    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		TextFormatter<?> formatter = new TextFormatter<>((TextFormatter.Change change) -> {
		    String text = change.getText();

		    // if text was added, fix the text to fit the requirements
		    if (text.isEmpty() == false) {
		        String newText = text.replace(" ", "");

		        int carretPos = change.getCaretPosition() - text.length() + newText.length();
		        change.setText(newText);

		        // fix carret position based on difference in originally added text and fixed text
		        change.selectRange(carretPos, carretPos);
		    }
		    return change;
		});
		
		tfProjectName.setText("ProjectName");
		taProjectDesc.setText("Description.");
		
		tfProjectName.setTextFormatter(formatter);
		
		tfProjectName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				if (mainJar != null) {
		        	mainJar.setText(newValue.toLowerCase() + ".jar");
		        }
			}
		});
		
		tfProjectName.setOnMouseClicked((e)-> {
			if (popup.isShowing() == true)
				popup.hide();
		});
		
		taProjectDesc.setOnMouseClicked((e)-> {
			if (popup.isShowing() == true)
				popup.hide();
		});
		
		fc = new FileChooser();
		dc = new DirectoryChooser();
		
		popup = new Popup();
		TextArea ta = new TextArea("");
		ta.setPrefWidth(475);
		ta.setPrefHeight(125);
		ta.setStyle("-fx-font-size: 14px;");
		ta.setEditable(false);
		ta.setWrapText(true);
		
		String prjName = tfProjectName.getText();
		
		File f1 = new File(jg.workDir.getAbsolutePath() + File.separator + prjName);
    	if (f1.exists() == false) {
    		f1.mkdirs();
    	}
    	File prjIniFile = new File(f1.getAbsolutePath() + File.separator + prjName + ".ini");
		
		IniFile prjIni = new IniFile(prjIniFile.getAbsolutePath());
		
		popup.getContent().add(ta);
		
		HBox hb = null;
		
		try (InputStream in = getClass().getResourceAsStream("/resources/wizard.txt");
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
				
				String[] arr = line.split(":");
				String prompt = arr[0].substring(1);
				
				Label lbl = new Label(prompt + ":");
				lbl.setUserData(prompt);
				lbl.setFont(jg.font1);
				lbl.setPrefWidth(150.0);
				hb = new HBox();
				hb.setSpacing(4.0);
				hb.setPadding(new Insets(4, 4, 4, 4));
				hb.setAlignment(Pos.CENTER_LEFT);
				
				if (line.charAt(0) == '&') {
					if (lbl.getText().equals("Shortcut:") == true && jg.osType != 3) {
						ckb = new CheckBox(prompt);
						ckb.setUserData(prompt);
						ckb.setFont(jg.font1);
						Region rg = new Region();
						HBox.setHgrow(rg, Priority.ALWAYS);
						
						if (lbl.getText().equals("Shortcut:") == true) {
							ckb.setSelected(true);
							shortcut = ckb;
						}
						
						hb.getChildren().addAll(lbl, ckb, rg);
						vbox.getChildren().add(hb);
					}
				} else if (line.charAt(0) == '*') {
					tf = new TextField();
					tf.setFont(jg.font1);
					
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					if (lbl.getText().equals("Package Type:") == true) {
						if (jg.osType == 1)
							tf.setText("exe");
						else if (jg.osType == 2)
							tf.setText("deb");
						else if (jg.osType == 3)
							tf.setText("pkg");
						packageType = tf;
					} else if (lbl.getText().equals("App Version:") == true) {
						tf.setText("%1");
						appVersion = tf;
					} else if (lbl.getText().equals("Copyright:") == true) {
						tf.setText("Copyright by me");
						copyright = tf;
					} else if (lbl.getText().equals("Description:") == true) {
						tf.setText("**Put a single line description here.**");
						description = tf;
					} else if (lbl.getText().equals("Add Modules:") == true) {
						tf.setText("javafx.controls,javafx.fxml");
						addModules = tf;
					} else if (lbl.getText().equals("App Name:") == true) {
						appName = tf;
					} else if (lbl.getText().equals("Vendor:") == true) {
						vendor = tf;
					} else if (lbl.getText().equals("Main Class:") == true) {
						mainClass = tf;
					} else if (lbl.getText().equals("Main Jar:") == true) {
						mainJar = tf;
					}
					
					hb.getChildren().addAll(lbl, tf);
					vbox.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '-') {
					tf = new TextField();
					tf.setFont(jg.font1);
					
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					if (lbl.getText().equals("Input:") == true) {
						if (jg.osType == 1)
							tf.setText(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator	+ "win-in");
						else if (jg.osType == 2)
							tf.setText(jg.workDir.getAbsolutePath().replaceAll("\\", "/") + "/" + prjName + "/"	+ "linux-in");
						else if (jg.osType == 3)
							tf.setText(jg.workDir.getAbsolutePath().replaceAll("\\", "/") + "/" + prjName + "/"	+ "mac-in");
						input = tf;
					} else if (lbl.getText().equals("Destination:") == true) {
						if (jg.osType == 1)
				    		tf.setText(jg.workDir.getAbsolutePath() + File.separator + prjName + File.separator + "win-out");
				    	else if (jg.osType == 2)
				    		tf.setText(jg.workDir.getAbsolutePath().replaceAll("\\", "/") + "/" + prjName + "/"+ "linux-out");
				    	else if (jg.osType == 3)
				    		tf.setText(jg.workDir.getAbsolutePath().replaceAll("\\", "/") + "/" + prjName + "/"+ "mac-out");
						destination = tf;
					}
					
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
					vbox.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '@') {
					tf = new TextField();
					tf.setFont(jg.font1);

					if (lbl.getText().equals("Module Path:") == true) {
						String mp = jg.sysIni.getString("System", "modulepath");
						if (mp != null)
							tf.setText(mp);
						modulePath = tf;
					}
					
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
					vbox.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '%') {
					tf = new TextField();
					tf.setFont(jg.font1);
					
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
					vbox.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				} else if (line.charAt(0) == '+') {
					tf = new TextField();
					tf.setFont(jg.font1);
					
					HBox.setHgrow(tf, Priority.ALWAYS);
					
					if (lbl.getText().equals("Icon:") == true) {
						icon = tf;
					}
					
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
					vbox.getChildren().add(hb);
					
					tf.setOnMouseClicked((e)-> {
						if (popup.isShowing() == true)
							popup.hide();
					});
				}
				
				// Add reset button.
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
				    		addModules = textField;
				    	}
					});
				}
				
				// Add Help button.
				MyButton qb = new MyButton();
				qb.setMyData("?");
				qb.setGraphic(new ImageView(jg.imgHelp));
				qb.setFont(jg.font1);
				qb.setOnAction((e) -> {
					TextArea ta2 = (TextArea)popup.getContent().get(0);
					ta2.setPrefWidth(500.0);
					ta2.setPrefHeight(225.0);
					
					Button bh = (Button)e.getSource();
					String help = (String)bh.getUserData();
					Stage s = (Stage)bh.getScene().getWindow();
					
					ta2.setText(help);
					
					if (popup.isShowing() == false)
						popup.show(s);
					else {
						popup.hide();
						popup.show(s);
					}
				});
				
				if (sb != null)
					hb.getChildren().addAll(sb, qb);
				else {
					if (sm != null)
						hb.getChildren().addAll(sm, qb);
					else
						hb.getChildren().addAll(qb);
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
			
		}  catch (IOException e) {
			e.printStackTrace();
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
