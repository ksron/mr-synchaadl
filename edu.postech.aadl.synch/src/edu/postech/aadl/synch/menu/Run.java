package edu.postech.aadl.synch.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class Run extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ConstraintChecker cc = new ConstraintChecker();
		cc.execute(event);
		if (cc.hasError()) {
			return null;
		}

		CodeGeneration cg = new CodeGeneration();
		cg.execute(event);
		if (cg.hasError()) {
			return null;
		}

		SymbolicAnalysis sa = new SymbolicAnalysis();
		sa.execute(event);
		return null;
	}

}
