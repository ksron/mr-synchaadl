package synchaadlmenu.handlers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import edu.postech.aadl.utils.IOUtils;
import edu.postech.maude.view.views.MaudeConsoleView;
import edu.postech.maude.view.views.MaudeResult;

public class Maude {
	private String maudeDirectory = null;
	private String maudeExecPath = null;
	private StringBuilder Options = null;
	private String userCommand = null;
	private String ModeFilePath = null;
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
				long start = System.currentTimeMillis();
				ProcessBuilder builder = new ProcessBuilder(commandOptions);
				IFile maudeResult = IOUtils.getFile(path);
				// builder.redirectError(Redirect.INHERIT);
				// builder.redirectOutput(Redirect.INHERIT);
				process = builder.start();
				// process.waitFor();
				long end = System.currentTimeMillis();
				elapsedTime = Double.toString((end - start)) + " ms";
				result = createMaudeResultFile(maudeResult);
				result = resultMaude(result, req);
			} catch (IOException | CoreException e) {
				System.out.println("Terminated!");
				result = "Terminated";
				location = "...";
				elapsedTime = "...";
			}
			System.out.println("MaudeRunner Thread end!");
			System.out.println("NickName : " + nickName);
			System.out.println("Result : " + result);
			System.out.println("Location : " + location);
			System.out.println("elapsedTime : " + elapsedTime);

			display.asyncExec(() -> view.refreshData(initialMaudeResult,
					new MaudeResult(nickName, result, location, elapsedTime)));
		}

		private String createMaudeResultFile(IFile file) throws IOException, CoreException {
			String result = "< Analysis Command > \n\n" + userCommand + "\n\n" + "< Result >\n";
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			System.out.println("Error buffer");
			StringBuffer esb = new StringBuffer();
			String eline;
			while ((eline = error.readLine()) != null) {
				// System.out.println("Error : " + eline);
				esb.append(eline + "\n");
			}

			System.out.println("Output buffer");
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				System.out.println("Output : " + line);
			}
			result += getSolutionString(sb.toString());
			IOUtils.setFileContent(new ByteArrayInputStream(result.getBytes()), file);
			return result;
		}

		private String getSolutionString(String rm) {
			if (rm.indexOf("No solution") != -1) {
				return rm.substring(rm.indexOf("No solution"));
			} else if (rm.indexOf("Solution 1") != -1) {
				return rm.substring(rm.indexOf("Solution 1"));
			} else {
				return "Error Occured!";
			}
		}

		private String resultMaude(String rm, boolean req) {
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
	}

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

	public void writeSearchMaudeFile(String txt, IPath path) {
		IFile maudeSearchFile = IOUtils.getFile(path);
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(txt.getBytes()), maudeSearchFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		this.TestFilePath = maudeSearchFile.getLocation().toFile().getPath();
	}

	private String compileCommandOption() {
		return maudeExecPath + Options.toString() + " "
				+ this.ModeFilePath + " " + this.TargetPath + " " + this.TestFilePath;
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

	public void setMode(String modeFilePath) {
		this.ModeFilePath = modeFilePath;
	}

	public void setUserFormula(String userCommand) {
		this.userCommand = userCommand;
	}

	public void setOption(String options) {
		this.Options = new StringBuilder();

		for (String option : options.split(" ")) {
			if(option.contains("maude")) {
				this.Options.append(" " + this.maudeDirectory + "/" + option);
			} else {
				this.Options.append(" " + option);
			}
		}
	}
}
