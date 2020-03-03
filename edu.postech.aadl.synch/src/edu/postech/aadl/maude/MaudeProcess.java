package edu.postech.aadl.maude;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import edu.postech.aadl.maude.preferences.MaudePrefPage;
import edu.postech.aadl.utils.IOUtils;

public class MaudeProcess extends Thread {
	private Maude maude;
	private String maudeExecPath = null;
	private String maudeLibDir = null;
	private String maudeOptions = null;
	private IPath propertyMaudePath = null;

	private MaudeProcessListener listener = null;

	public MaudeProcess(Maude mu, IPath propPath) {
		maude = mu;
		propertyMaudePath = propPath;
		getMaudePrefrences();
	}

	public void setListener(MaudeProcessListener listener) {
		this.listener = listener;
	}

	private void getMaudePrefrences() {
		IPreferenceStore pref = new ScopedPreferenceStore(InstanceScope.INSTANCE, "edu.postech.maude.preferences.page");
		maudeExecPath = pref.getString(MaudePrefPage.MAUDE);
		maudeOptions = pref.getString(MaudePrefPage.MAUDE_OPTS);
		maudeLibDir = pref.getString(MaudePrefPage.MAUDE_LIB_DIR);
	}

	@Override
	public void run() {
		ProcessBuilder builder = new ProcessBuilder(compileCommandOption().split(" "));
		setProcessEnv(builder.environment());
		try {
			Process process = builder.start();
			maude.setProcess(process);
			process.waitFor();

			String pResult = getProcessResult(process);
			writeResultFile(pResult, maude.getLocationIPath().toFile());
			maude.setResultString(getSimplifiedResult(pResult));
			maude.setElapsedTime(getElapsedTime(pResult));

			listener.update(maude);
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String compileCommandOption() {
		return maudeExecPath + " " + maudeOptions + " "
				+ IOUtils.getFile(propertyMaudePath).getLocation().toFile().getPath();
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

	private String getProcessResult(Process process) throws IOException, CoreException {
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

	private void writeResultFile(String result, File file) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);
		IOUtils.setFileContent(new ByteArrayInputStream(result.getBytes()), ifile);
	}

	public String getSimplifiedResult(String rm) {
		if (maude.isInvProperty()) {
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
