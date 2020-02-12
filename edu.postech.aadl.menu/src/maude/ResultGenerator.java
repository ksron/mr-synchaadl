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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.postech.aadl.utils.IOUtils;
import edu.postech.maude.view.views.HybridSynchAADLView;
import edu.postech.maude.view.views.MaudeResult;

public class ResultGenerator extends Thread {

	private Display display;
	private HybridSynchAADLView view;
	private IFile maudeResultFile;
	private Process process;
	private String propId;
	private String location;
	private Boolean inv;

	public ResultGenerator(Process process, IPath path, String propId, boolean inv) {
		this.process = process;
		this.maudeResultFile = IOUtils.getFile(path);
		this.location = path.toString();
		this.propId = propId;
		this.inv = inv;

		this.view = (HybridSynchAADLView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView("edu.postech.maude.view.views.MaudeConsoleView");
		this.display = view.workbench.getDisplay();
	}

	@Override
	public void run() {
		display.asyncExec(() -> view.clear());

		try {
			String result = createMaudeResultFile(maudeResultFile);
			String simplifiedResult = getMaudeSimpleResult(result, inv);
			String elapsedTime = getMaudeElapsedTime(result);

			display.asyncExec(() -> view.updateData(new MaudeResult(propId, simplifiedResult, location, elapsedTime)));
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

	public String getMaudeSimpleResult(String rm, boolean req) {
		if (req) {
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
