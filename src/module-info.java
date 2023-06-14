module jgui {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;
	requires java.base;
	requires java.desktop;
	requires IniFile;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base, java;
}
