package edu.postech.aadl.maude;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.Property;

public class Maude {

	private String result;
	private IFile pspc;
	private Property prop;
	private IPath path;
	private String elapsedTime;

	private Process process = null;

	public Maude(IFile pspc, Property prop, String result, IPath path, String elapsedTime) {
		this.pspc = pspc;
		this.result = result;
		this.prop = prop;
		this.path = path;
		this.elapsedTime = elapsedTime;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void setElapsedTime(String time) {
		elapsedTime = time;
	}

	public void killProcess() {
		if (process != null) {
			process.destroy();
		}
	}

	public IFile getPSPCFile() {
		return pspc;
	}

	public Property getProp() {
		return prop;
	}

	public boolean checkProcess() {
		return process.isAlive();
	}

	public void setResultString(String result) {
		this.result = result;
	}

	public String getResultString() {
		return result;
	}

	public String getPropId() {
		return prop.getName();
	}

	public String getPspcFileName() {
		return pspc.getName();
	}

	public String getLocationString() {
		return path.toString();
	}

	public IPath getLocationIPath() {
		return path;
	}

	public Integer findPropIdLine() {
		try {
			InputStream is = pspc.getContents();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			int idx = 1;
			while ((line = br.readLine()) != null) {
				if (line.contains("[" + prop.getName() + "]")) {
					return new Integer(idx);
				}
				idx++;
			}
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}

		return 1;
	}

	public String getElapsedTimeString() {
		return elapsedTime;
	}

	public boolean isInvProperty() {
		return prop instanceof Invariant;
	}
}
