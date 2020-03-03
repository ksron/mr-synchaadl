package edu.postech.aadl.maude;

import java.io.ByteArrayInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import edu.postech.aadl.utils.IOUtils;

public class MaudeCSV {
	public StringBuffer col;

	public MaudeCSV() {
		col = new StringBuffer();
	}


	public void doGenerate(IPath path) {
		try {
			IOUtils.setFileContent(new ByteArrayInputStream(col.toString().getBytes()), IOUtils.getFile(path));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void addColumn(Maude mr) {
		col.append(mr.getPspcFileName() + "," + mr.getPropId() + "," + mr.getResultString() + ","
				+ mr.getLocationString() + "," + mr.getElapsedTimeString() + "\n");
	}

	public void setCategory() {
		col.append("PSPC File,Property Id,Result,Location,ElapsedTime\n");
	}
}
