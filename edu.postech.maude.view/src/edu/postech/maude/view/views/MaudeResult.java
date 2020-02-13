package edu.postech.maude.view.views;

public class MaudeResult {

	public String result;
	public String pspc;
	public String propId;
	public String location;
	public String elapsedTime;

	private Process process = null;

	public MaudeResult(String pspc, String propId, String result, String name, String elapsedTime) {
		this.pspc = pspc;
		this.result = result;
		this.propId = propId;
		this.location = name;
		this.elapsedTime = elapsedTime;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void killProcess() {
		if (process != null) {
			process.destroy();
		}
	}

	public boolean checkProcess() {
		return process.isAlive();
	}
}
