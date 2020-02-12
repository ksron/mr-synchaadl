package maude;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.utils.IOUtils;

public class Maude {
	private String maudeDirectory = null;
	private String maudeExecPath = null;
	private StringBuilder Options = null;
	private String TargetPath = null;
	private String TestFilePath = null;
	private boolean req = false;

	public void runMaude(IPath path, String nickName) {
		if (!checkParameters()) {
			System.out.println("Maude Incomplete Build!!");
			return;
		}
		MaudeRunner mrt = new MaudeRunner(compileCommandOption().split(" "), path, nickName, req);
		mrt.run();
	}


	public void setRequirement(boolean req) {
		this.req = req;
	}

	private boolean checkParameters() {
		if (maudeDirectory == null) {
			return false;
		}
		if (maudeExecPath == null) {
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


	public String DebugCompileCommand() {
		return compileCommandOption();
	}


	public void setTestFilePath(IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		this.TestFilePath = maudeSearchFile.getLocation().toFile().getPath();
	}

	private String compileCommandOption() {
		return maudeExecPath + Options.toString() + " " + this.TargetPath + " " + this.TestFilePath;
	}


	public void setTargetMaude(String path) {
		this.TargetPath = path;
	}


	public void setMaudeExecPath(String path) {
		this.maudeExecPath = path;
	}


	public void setMaudeDirPath(String directory) {
		this.maudeDirectory = directory;
	}

	public void setOption(String options) {
		this.Options = new StringBuilder();

		for (String option : options.split(" ")) {
			if (option.contains("maude")) {
				this.Options.append(" " + this.maudeDirectory + "/" + option);
			} else {
				this.Options.append(" " + option);
			}
		}
	}
}
