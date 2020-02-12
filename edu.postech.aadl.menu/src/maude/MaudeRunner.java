package maude;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;

class MaudeRunner extends Thread {
	private String[] commandOptions;
	private Process process;
	private String propId;
	private IPath path;
	private boolean inv = false;

	public MaudeRunner(String[] commandOptions, IPath path, String propId, boolean inv) {
		this.commandOptions = commandOptions;
		this.path = path;
		this.propId = propId;
		this.inv = inv;
	}

	@Override
	public void run() {
		try {
			System.out.println("MaudeRunner Thread start!");
			ProcessBuilder builder = new ProcessBuilder(commandOptions);
			// builder.redirectError(Redirect.INHERIT);
			// builder.redirectOutput(Redirect.INHERIT);
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ResultGenerator result = new ResultGenerator(process, this.path, propId, inv);
		result.start();
	}
}