package edu.postech.maude.view.views;

public class MaudeResult {

	public String result;
	public String propId;
	public String location;
	public String elapsedTime;

	public MaudeResult(String propId, String result, String name, String elapsedTime) {
		this.result = result;
		this.propId = propId;
		this.location = name;
		this.elapsedTime = elapsedTime;
	}
}
