package edu.postech.xtext.maude;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import org.eclipse.emf.common.util.EList;


public class MaudeRunner {
	private String BaseDirectory = null;
	private String Name = null;
	private StringBuilder Options = null;

	private String ModeFilePath = null;
	private String TargetPath = null;
	private String TestFilePath = null;

	private Process process;

	public void runMaude() {
		if (!checkParameters()) {
			System.out.println("Maude Build Faled!!");
			return;
		}
		try {
			ProcessBuilder builder = new ProcessBuilder(compileCommandOption().split(" "));
			builder.redirectError(Redirect.INHERIT);
			builder.redirectOutput(Redirect.INHERIT);
			this.process = builder.start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String DebugCompileCommand() {
		return compileCommandOption();
	}

	private boolean checkParameters() {
		if (BaseDirectory == null) {
			return false;
		}
		if (Name == null) {
			return false;
		}
		if (Options == null) {
			return false;
		}
		if (TargetPath == null) {
			return false;
		}
		if (TestFilePath == null) {
			return false;
		}
		return true;
	}

	public void makeMaudeFile(String txt, int idx) {
		File file = new File("MaudeTest_" + idx + ".maude");
		FileWriter fw;
		try {
			fw = new FileWriter(file, false);
			fw.write(txt);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.TestFilePath = file.getPath();
	}


	private String compileCommandOption() {
		return this.BaseDirectory + "/" + this.Name + Options.toString() + " "
				+ this.ModeFilePath + " " + this.TargetPath + " " + this.TestFilePath;

	}

	public void setTargetMaude(String path) {
		this.TargetPath = path;
	}

	public void setName(String name) {
		this.Name = name;
	}


	public void setDirectory(String directory) {
		this.BaseDirectory = directory;
	}

	public void setMode(String modeFilePath) {
		this.ModeFilePath = modeFilePath;
	}


	public void setOption(EList<String> options) {
		this.Options = new StringBuilder();

		for (String option : options) {
			if(option.contains("maude")) {
				this.Options.append(" " + this.BaseDirectory + "/" + option);
			} else {
				this.Options.append(" " + option);
			}
		}
	}

}
