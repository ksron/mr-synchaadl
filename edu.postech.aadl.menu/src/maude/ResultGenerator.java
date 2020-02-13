package maude;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.utils.IOUtils;
import edu.postech.aadl.xtext.propspec.propSpec.Invariant;
import edu.postech.aadl.xtext.propspec.propSpec.Property;
import edu.postech.maude.view.views.DisplayView;
import edu.postech.maude.view.views.MaudeResult;

public class ResultGenerator extends Thread {
	private IFile maudeResultFile;
	private Process process;
	private Property prop;
	private IFile pspcFile;
	private IPath location;
	private boolean inv;

	public ResultGenerator(Process process, IPath path, IFile pspcFile, Property prop) {
		this.process = process;
		this.maudeResultFile = IOUtils.getFile(path);
		this.location = path;
		this.pspcFile = pspcFile;
		this.prop = prop;
		this.inv = prop instanceof Invariant;
	}

	@Override
	public void run() {
		MaudeResult initial = new MaudeResult(pspcFile, prop, "Running", location, "");
		initial.setProcess(process);

		DisplayView.initDataView(initial);
		try {
			String result = createMaudeResultFile(maudeResultFile);
			String simplifiedResult = getMaudeSimpleResult(result);
			String elapsedTime = getMaudeElapsedTime(result);
			if (initial.getResultString().equals("Terminated")) {
				return;
			}
			DisplayView.updateDataView(initial,
					new MaudeResult(pspcFile, prop, simplifiedResult, location, elapsedTime));
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		}
	}

	private String createMaudeResultFile(IFile file) throws IOException, CoreException {
		String result = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		result += getSolutionString(sb.toString());
		IOUtils.setFileContent(new ByteArrayInputStream(sb.toString().getBytes()), file);
		return result;
	}

	public String getSolutionString(String rm) {
		if (rm.indexOf("No solution") != -1) {
			return rm.substring(rm.indexOf("No solution"));
		} else if (rm.indexOf("Solution 1") != -1) {
			return rm.substring(rm.indexOf("Solution 1"));
		} else {
			return "Error Occured!";
		}
	}

	public String getMaudeSimpleResult(String rm) {
		if (inv) {
			if (rm.indexOf("No solution") != -1) {
				return "No counterexample found";
			} else if (rm.indexOf("Solution 1") != -1) {
				return "No counterexample found";
			} else {
				return "Error Occured!";
			}
		} else {
			if (rm.indexOf("No solution") != -1) {
				return "UnReachable";
			} else if (rm.indexOf("Solution 1") != -1) {
				return "Reachable";
			} else {
				return "Error Occured!";
			}
		}
	}

	public String getMaudeElapsedTime(String rm) {
		Pattern p = Pattern.compile("[0-9]+ms cpu");
		Matcher m = p.matcher(rm);
		String elapsedTime = "..";
		if (m.find()) {
			elapsedTime = m.group().split(" ")[0];
		}
		return elapsedTime;
	}
}
