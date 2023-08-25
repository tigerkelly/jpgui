package application;
	
import java.io.IOException;

import com.rkw.IniFile;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {
	private Pane mainPane = null;
	private JpGlobal jg = JpGlobal.getInstance();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/jpgui_icon.png")));
			primaryStage.setScene(createScene(loadMainPane()));
			primaryStage.setMinWidth(750);
			primaryStage.setMinHeight(800);
			primaryStage.setOnCloseRequest((e) -> { 
				Object[] objs = jg.prjList.keySet().toArray();

				for (Object o : objs) {
					String s = (String) o;
					IniFile ini = jg.prjList.get(s);
					if (ini.getChangedFlag() == true) {
						ButtonType bt = jg.yesNoCancelAlert(mainPane, "Quit JpGui", 
								"You have unsaved changes.\n" +
								"Select 'No' to abort all changes and exit.\n" +
								"select 'Yes' to close dialog, then use\n" +
								"Menu File -> 'Save Project' or 'Save all Projects'.\n", null);
//						System.out.println(bt);
						if (bt.getButtonData() == ButtonData.YES) {
							e.consume();
							return;
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() {
//		System.out.println("*** JpGui is Ending. ***");

		SceneInfo si = jg.sceneNav.fxmls.get(jg.scenePeek());
		if (si != null && si.controller instanceof RefreshScene) {
			RefreshScene c = (RefreshScene) si.controller;
			c.leaveScene();
		}
	}

	/**
	 * Loads the main fxml layout. Sets up the vista switching VistaNavigator. Loads
	 * the first vista into the fxml layout.
	 *
	 * @return the loaded pane.
	 * @throws IOException if the pane could not be loaded.
	 */
//    @SuppressWarnings("resource")
	private Pane loadMainPane() throws IOException {
//		System.out.println("*** JpGui is Starting. ***");

		FXMLLoader loader = new FXMLLoader();

		mainPane = (Pane) loader.load(getClass().getResourceAsStream(SceneNav.MAIN)); // SceneNav

		SceneNavController mainController = loader.getController();

		jg.sceneNav.setMainController(mainController);
		jg.sceneNav.loadScene(SceneNav.JPGUI);

		return mainPane;
	}

	/**
	 * Creates the main application scene.
	 *
	 * @param mainPane the main application layout.
	 *
	 * @return the created scene.
	 */
	private Scene createScene(Pane mainPane) {
		Scene scene = new Scene(mainPane);

//		scene.getStylesheets().setAll(getClass().getResource("application.css").toExternalForm());

		return scene;
	}
}
