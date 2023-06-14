package application;

import javafx.scene.control.Button;

public class MyButton extends Button {

	private String myData;
	
	public MyButton() {
		super();
	}

	public String getMyData() {
		return myData;
	}

	public void setMyData(String myData) {
		this.myData = myData;
	}
}
