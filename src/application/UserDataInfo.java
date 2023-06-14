package application;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class UserDataInfo {

	private String data;
	private TextField tf;
	private CheckBox ckb;
	
	public UserDataInfo() {
		super();
	}
	
	public UserDataInfo(String data, TextField tf, CheckBox ckb) {
		super();
		this.data = data;
		this.tf = tf;
		this.ckb = ckb;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public TextField getTf() {
		return tf;
	}

	public void setTf(TextField tf) {
		this.tf = tf;
	}

	public CheckBox getCkb() {
		return ckb;
	}

	public void setCkb(CheckBox ckb) {
		this.ckb = ckb;
	}
}
