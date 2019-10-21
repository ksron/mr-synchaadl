package edu.postech.xtext.maude;

public class MaudeResult {

	public String result;
	public String nickname;
	public String location;
	public String elapsedTime;

	public MaudeResult(String nickname, String result, String name, String elapsedTime) {
		this.result = result;
		this.nickname = nickname;
		this.location = name;
		this.elapsedTime = elapsedTime;
	}
}
