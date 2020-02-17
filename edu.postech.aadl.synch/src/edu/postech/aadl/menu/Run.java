package edu.postech.aadl.menu;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class Run extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ConstraintChecker cc = new ConstraintChecker();
		cc.execute(event);
		if (cc.isError()) {
			return null;
		}

		CodeGeneration cg = new CodeGeneration();
		cg.execute(event);
		if (cg.isError()) {
			return null;
		}

		SymbolicAnalysis sa = new SymbolicAnalysis();
		sa.execute(event);
		return null;
	}

}
