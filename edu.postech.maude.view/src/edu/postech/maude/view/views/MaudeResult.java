package edu.postech.maude.view.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class MaudeResult {

	public String result;
	public IFile pspc;
	public String propId;
	public String location;
	public String elapsedTime;

	private Process process = null;

	public MaudeResult(IFile pspc, String propId, String result, String name, String elapsedTime) {
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

	public String getPspcFileName() {
		return pspc.getName();
	}

	public IPath getLocationIPath() {
		return new Path(location);
	}

	public Integer findPropIdLine() {
		try {
			InputStream is = pspc.getContents();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			int idx = 1;
			while ((line = br.readLine()) != null) {
				if (line.contains("[" + propId + "]")) {
					return new Integer(idx);
				}
				idx++;
			}
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}

		return 1;
	}
}
