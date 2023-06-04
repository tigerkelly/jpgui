package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class SceneNavController implements Initializable {

    @FXML
    private StackPane sceneHolder;
    
    public void setSceneNav(Node node) {
        sceneHolder.getChildren().setAll(node);
    }

	public void initialize(URL arg0, ResourceBundle arg1) {

	}
}
