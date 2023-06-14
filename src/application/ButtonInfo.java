package application;

public class ButtonInfo {
	private String text;
	private int reValue;
	private boolean defaultButton;
	
	public ButtonInfo(String text, int reValue, boolean defaultButton) {
		super();
		this.text = text;
		this.reValue = reValue;
		this.defaultButton = defaultButton;
	}
	
	public ButtonInfo() {
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getReValue() {
		return reValue;
	}

	public void setReValue(int reValue) {
		this.reValue = reValue;
	}

	public boolean isDefaultButton() {
		return defaultButton;
	}

	public void setDefaultButton(boolean defaultButton) {
		this.defaultButton = defaultButton;
	}

	@Override
	public String toString() {
		return "ButtonInfo [text=" + text + ", reValue=" + reValue + ", defaultButton=" + defaultButton + "]";
	}
}
