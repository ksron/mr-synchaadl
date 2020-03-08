package edu.postech.aadl.maude;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
	private IPath propMaudePath = null;

	private MaudeProcessListener listener = null;

	public MaudeProcess(Maude mu, IPath propPath) {
		maude = mu;
		propMaudePath = propPath;
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
			maude.setResultString(getSimplifiedResult(pResult));
			maude.setElapsedTime(getElapsedTime(pResult));
			writeResultFile(compileResultFile(pResult), maude.getLocationIPath());

			listener.update(maude);
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private String compileCommandOption() {
		return maudeExecPath + " " + maudeOptions + " "
				+ IOUtils.getFile(propMaudePath).getLocation().toFile().getPath();
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

	private String compileResultFile(String result) {
		StringBuffer sbResult = new StringBuffer();
		sbResult.append(System.getProperty("line.separator"));
		Pattern time = Pattern.compile("Time\\[[0-9]+\\]");
		Matcher matcher = time.matcher(result);
		while (matcher.find()) {
			sbResult.append(matcher.group());
			result = result.substring(matcher.start());
			Map<String, String> map = getSMTValueMap(result);
			sbResult.append(getModel(result, map));
		}
		return sbResult.toString();
	}

	private Map<String, String> getSMTValueMap(String result) {
		Map<String, String> map = new HashMap<String, String>();
		Pattern start = Pattern.compile("SMT\\[.*\\]");
		Matcher matcher = start.matcher(result);
		if (matcher.find()) {
			result.substring(matcher.start() + 5);
			for (String elem : result.split(",")) {
				String param = elem.split("|->")[0];
				String value = elem.split("|->")[1];
				map.put(param, value);
			}
		}
		return map;
	}

	private String getModel(String result, Map<String, String> map) {
		String ret = "";
		Pattern start = Pattern.compile("Model\\[.*\\]");
		Matcher matcher = start.matcher(result);
		if (matcher.find()) {
			ret = result.substring(matcher.start());
			for (String param : map.keySet()) {
				if (ret.contains(param)) {
					ret.replace(param, map.get(param));
				}
			}
		}
		return ret;
	}

	private void writeResultFile(String result, IPath file) throws CoreException {
		IOUtils.setFileContent(new ByteArrayInputStream(result.getBytes()), IOUtils.getFile(file));
	}

	private String getSimplifiedResult(String rm) {
		if (maude.isInvProperty()) {
			if (rm.contains("counterexample(failure)")) {
				return "No counterexample found";
			} else if (rm.contains("result CounterExample:")) {
				return "Counterexample found";
			} else {
				return "Error occured!";
			}
		} else {
			if (rm.contains("counterexample(failure)")) {
				return "UnReachable";
			} else if (rm.contains("result CounterExample:")) {
				return "Reachable";
			} else {
				return "Error occured!";
			}
		}
	}

	private String getElapsedTime(String rm) {
		Pattern p = Pattern.compile("[0-9]+ms cpu");
		Matcher m = p.matcher(rm);
		String elapsedTime = "..";
		if (m.find()) {
			elapsedTime = m.group().split(" ")[0];
		}
		return elapsedTime;
	}
}
