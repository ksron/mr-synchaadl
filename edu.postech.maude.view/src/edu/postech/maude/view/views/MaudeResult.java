package edu.postech.maude.view.views;

public class MaudeResult {

	public String result;
	public String nickName;
	public String location;
	public String elapsedTime;

	public MaudeResult(String nickname, String result, String name, String elapsedTime) {
		this.result = result;
		this.nickName = nickname;
		this.location = name;
		this.elapsedTime = elapsedTime;
	}
}
