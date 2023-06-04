package application;

public class ProcessRet {

	private int ev;		// exit value
	private String output;
	
	public ProcessRet(int ev, String output) {
		super();
		this.ev = ev;
		this.output = output;
	}

	public int getEv() {
		return ev;
	}

	public void setEv(int ev) {
		this.ev = ev;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	@Override
	public String toString() {
		return "ProcessRet [ev=" + ev + ", output=" + output + "]";
	}
	
}
