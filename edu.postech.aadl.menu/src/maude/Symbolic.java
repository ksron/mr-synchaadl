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
import edu.postech.maude.view.views.MaudeConsoleView;
import edu.postech.maude.view.views.MaudeResult;

public class Symbolic extends Maude {
	private String maudeDirectory = null;
	private String maudeExecPath = null;
	private StringBuilder Options = null;
	private String userCommand = null;
	private String TargetPath = null;
	private String TestFilePath = null;
	private static int count = 0;
	private Process process;
	private MaudeConsoleView viewer;
	private boolean req = false;

	class MaudeRunner extends Thread {
		private String[] commandOptions;
		private Process process;
		private String nickName;
		private IPath path;
		private String location;
		private String result;
		private String simpleResult;
		private String elapsedTime;
		private MaudeConsoleView view;
		private Display display;
		private boolean req = false;

		public MaudeRunner(String[] commandOptions, IPath path, String nickName, boolean requirement) {
			this.commandOptions = commandOptions;
			this.path = path;
			this.location = path.toString();
			this.nickName = nickName;
			this.view = (MaudeConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView("edu.postech.maude.view.views.MaudeConsoleView");
			;
			this.display = view.workbench.getDisplay();
			this.req = requirement;
		}

		@Override
		public void run() {
			System.out.println("MaudeRunner Thread start!");
			MaudeResult initialMaudeResult = view.initialData(nickName);
			try {
				ProcessBuilder builder = new ProcessBuilder(commandOptions);
				IFile maudeResultFile = IOUtils.getFile(path);
				// builder.redirectError(Redirect.INHERIT);
				// builder.redirectOutput(Redirect.INHERIT);
				process = builder.start();
				result = createMaudeResultFile(maudeResultFile);
				simpleResult = getMaudeSimpleResult(result, req);
				elapsedTime = getMaudeElapsedTime(result);
			} catch (IOException | CoreException e) {
				System.out.println("Terminated!");
				result = "Terminated";
				location = "...";
				elapsedTime = "...";
			}
			display.asyncExec(() -> view.refreshData(initialMaudeResult,
					new MaudeResult(nickName, simpleResult, location, elapsedTime)));
		}

		private String createMaudeResultFile(IFile file) throws IOException, CoreException {
			String result = "< Analysis Command > \n\n" + userCommand + "\n\n" + "< Result >\n";
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			System.out.println("Error buffer");
			StringBuffer esb = new StringBuffer();
			String eline;
			while ((eline = error.readLine()) != null) {
				System.out.println("Error : " + eline);
				esb.append(eline + "\n");
			}

			System.out.println("Output buffer");
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				System.out.println("Output : " + line);
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
					return "Counter-Example Not Found";
				} else if (rm.indexOf("Solution 1") != -1) {
					return "Counter-Example Found";
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

	@Override
	public void runMaude(IPath path, String nickName) {
		if (!checkParameters()) {
			System.out.println("Maude Incomplete Build!!");
			return;
		}
		MaudeRunner mrt = new MaudeRunner(compileCommandOption().split(" "), path, nickName, req);
		mrt.run();
	}

	@Override
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

	@Override
	public String DebugCompileCommand() {
		return compileCommandOption();
	}


	@Override
	public void setTestFilePath(IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		this.TestFilePath = maudeSearchFile.getLocation().toFile().getPath();
	}

	private String compileCommandOption() {
		return maudeExecPath + Options.toString() + " " + this.TargetPath + " "
				+ this.TestFilePath;
	}

	@Override
	public void setTargetMaude(String path) {
		this.TargetPath = path;
	}

	@Override
	public void setMaudeExecPath(String path) {
		this.maudeExecPath = path;
	}

	@Override
	public void setMaudeDirPath(String directory) {
		this.maudeDirectory = directory;
	}

	@Override
	public void setUserFormula(String userCommand) {
		this.userCommand = userCommand;
	}

	@Override
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