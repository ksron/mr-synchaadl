package edu.postech.xtext.maude;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;

import edu.postech.aadl.utils.IOUtils;




public class MaudeRunner {
	private String maudeDirectory = null;
	private String Name = null;
	private StringBuilder Options = null;

	private String ModeFilePath = null;
	private String TargetPath = null;
	private String TestFilePath = null;

	private static int count = 0;

	private Process process;

	public String runMaude(IPath path) {
		String result = null;
		if (!checkParameters()) {
			System.out.println("Maude Incomplete Build!!");
			return null;
		}
		try {
			ProcessBuilder builder = new ProcessBuilder(compileCommandOption().split(" "));
			IFile maudeResult = IOUtils.getFile(path);

			// builder.redirectOutput(output);
			this.process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = resultMaudeParse(sb.toString());
			IOUtils.setFileContent(new ByteArrayInputStream(result.getBytes()), maudeResult);

		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String resultMaudeParse(String rm) {
		if (rm.indexOf("No solution") != -1) {
			return rm.substring(rm.indexOf("No solution"));
		} else if (rm.indexOf("Solution 1") != -1) {
			return rm.substring(rm.indexOf("Solution 1"));
		} else {
			return "Error Occured!";
		}
	}

	public String DebugCompileCommand() {
		return compileCommandOption();
	}

	private boolean checkParameters() {
		if (maudeDirectory == null) {
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

	public void makeMaudeFile(String txt, IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(txt.getBytes()), maudeSearchFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		this.TestFilePath = maudeSearchFile.getLocation().toFile().getPath();
	}


	private String compileCommandOption() {
		return this.maudeDirectory + "/" + this.Name + Options.toString() + " "
				+ this.ModeFilePath + " " + this.TargetPath + " " + this.TestFilePath;

	}

	public void setTargetMaude(String path) {
		this.TargetPath = path;
	}

	public void setName(String name) {
		this.Name = name;
	}


	public void setMaudeDirectory(String directory) {
		this.maudeDirectory = directory;
	}

	public void setMode(String modeFilePath) {
		this.ModeFilePath = modeFilePath;
	}

	public void setOption(EList<String> options) {
		this.Options = new StringBuilder();

		for (String option : options) {
			if(option.contains("maude")) {
				this.Options.append(" " + this.maudeDirectory + "/" + option);
			} else {
				this.Options.append(" " + option);
			}
		}
	}
}
