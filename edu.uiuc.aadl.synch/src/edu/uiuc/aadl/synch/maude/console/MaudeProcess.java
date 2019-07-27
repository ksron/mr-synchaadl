package edu.uiuc.aadl.synch.maude.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MaudeProcess {
    private Runtime runTime;
    private Integer pidMaude;
    private Process process;

    private String commandString[];

    private String pathMaudeBin = "";
    private String logFile = "";

    private MaudeProcess() {
        runTime = Runtime.getRuntime();
    }

    public MaudeProcess(String[] commandString) {
        this();
        setCommandString(commandString);
    }

    private void setCommandString(String[] commandString) {
        this.commandString = new String[commandString.length];
        System.arraycopy(commandString, 0, this.commandString, 0, commandString.length);
    }

    public void runMaude() throws IOException, InterruptedException {
        this.process = this.runTime.exec(this.commandString);
        printStream();
    }

    private void printStream() throws InterruptedException, IOException {
        this.process.waitFor();
        try (InputStream psout = process.getInputStream()) {
            copy(psout, System.out);
        }
    }

    private void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
    }

}