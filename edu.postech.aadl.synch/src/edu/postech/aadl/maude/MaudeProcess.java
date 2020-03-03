package edu.postech.aadl.maude;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Property;

public class MaudeProcess {
	private String maudeExecPath = null;
	private String maudeLibDir = null;
	private String maudeOptions = null;
	private IFile pspcFile = null;
	private String aadlMaudePath = null;
	private String propertyMaudePath = null;

	public void runMaude(IPath path, Property prop) {
		try {
			ProcessBuilder builder = new ProcessBuilder(compileCommandOption().split(" "));
			setProcessEnv(builder.environment());
			Process process = builder.start();
			ResultGenerator result = new ResultGenerator(process, path, pspcFile, prop);
			result.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String compileCommandOption() {
		System.out.println(
				"Maude Command: " + maudeExecPath + " " + maudeOptions + " " + propertyMaudePath);
		return maudeExecPath + " " + maudeOptions + " " + propertyMaudePath;
	}

	private void setProcessEnv(Map<String, String> map) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac")) {
			map.put("DYLD_LIBRARY_PATH", "DYLD_LIBRARY_PATH:" + maudeLibDir);
		} else if (os.contains("unix") || os.contains("linux")) {
			map.put("LD_LIBRARY_PATH", "DYLD_LIBRARY_PATH:" + maudeLibDir);
		} else {
			System.out.println("Doesn't support os");
		}
	}

	public void setMaudeExecPath(String path) {
		this.maudeExecPath = path;
	}

	public void setMaudeLibDirPath(String libDir) {
		this.maudeLibDir = libDir;
	}

	public void setOption(String options) {
		maudeOptions = options;
	}

	public void setAADLInstanceMaude(String path) {
		this.aadlMaudePath = path;
	}

	public void setPropertyMaudePath(IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		this.propertyMaudePath = maudeSearchFile.getLocation().toFile().getPath();
	}

	public void setPspcFile(IFile pspcFile) {
		this.pspcFile = pspcFile;
	}
}
