package edu.postech.aadl.maude;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.synch.view.DisplayView;
import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.Property;

public class ResultGenerator extends Thread {
	private IFile maudeResultFile;
	private Process process;
	private Property prop;
	private IFile pspcFile;
	private IPath location;

	public ResultGenerator(Process process, IPath path, IFile pspcFile, Property prop) {
		this.process = process;
		this.maudeResultFile = IOUtils.getFile(path);
		this.location = path;
		this.pspcFile = pspcFile;
		this.prop = prop;
	}

	@Override
	public void run() {
		Maude initial = new Maude(pspcFile, prop, "Running", location, "");
		initial.setProcess(process);

		DisplayView.initDataView(initial);
		try {
			String result = getProcessResult();

			writeResultFile(result, maudeResultFile);
			String simplifiedResult = getSimplifiedResult(result);
			String elapsedTime = getElapsedTime(result);
			if (initial.getResultString().equals("Terminated")) {
				return;
			}
			DisplayView.updateDataView(initial,
					new Maude(pspcFile, prop, simplifiedResult, location, elapsedTime));
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}

	private String getProcessResult() throws IOException, CoreException {
		String result = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		result += sb.toString();
		return result;
	}

	private void writeResultFile(String result, IFile file) throws CoreException {
		IOUtils.setFileContent(new ByteArrayInputStream(result.getBytes()), file);
	}

	public String getSimplifiedResult(String rm) {
		if (prop instanceof Invariant) {
			if (rm.contains("No solution")) {
				return "No counterexample found";
			} else if (rm.contains("Solution 1")) {
				return "No counterexample found";
			} else {
				return "Error occured!";
			}
		} else {
			if (rm.contains("No solution")) {
				return "UnReachable";
			} else if (rm.contains("Solution 1")) {
				return "Reachable";
			} else {
				return "Error occured!";
			}
		}
	}

	public String getElapsedTime(String rm) {
		Pattern p = Pattern.compile("[0-9]+ms cpu");
		Matcher m = p.matcher(rm);
		String elapsedTime = "..";
		if (m.find()) {
			elapsedTime = m.group().split(" ")[0];
		}
		return elapsedTime;
	}
}
