package edu.postech.aadl.synch.maude.contspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.ba.aadlba.BehaviorVariableHolder;
import org.osate.ba.aadlba.Factor;
import org.osate.ba.aadlba.SimpleExpression;
import org.osate.ba.aadlba.Term;
import org.osate.ba.aadlba.Value;

import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsErrorListener;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsFlowsVisitor;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsLexer;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser;

public class ContSpec {
	private List<ContSpecItem> cdItems;

	public ContSpec() {
		cdItems = new ArrayList<ContSpecItem>();
	}

	public void addItem(ContSpecItem item) {
		cdItems.add(item);
	}

	public List<ContSpecItem> getItems() {
		return Collections.unmodifiableList(cdItems);
	}

	public static ContSpec parse(String cont, ComponentInstance ci) {
		ANTLRInputStream stream = new ANTLRInputStream(cont);
		ContDynamicsLexer lexer = new ContDynamicsLexer(stream);
		lexer.addErrorListener(ContDynamicsErrorListener.INSTANCE);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ContDynamicsParser parser = new ContDynamicsParser(tokens);
		parser.addErrorListener(ContDynamicsErrorListener.INSTANCE);

		ContDynamicsFlowsVisitor visitor = new ContDynamicsFlowsVisitor();
		ContSpec result = null;
		try{
			result = visitor.visit(parser.continuousdynamics(), ci);
		}catch(ParseCancellationException e){
			e.printStackTrace(); // TODO: show error dialog to user
		}
		return result;
	}

	public boolean findValueVariable(String name) {
		List<String> names = new ArrayList<String>();
		for (ContSpecItem item : cdItems) {
			getValueVariableNames(item.getExpression(), names);
		}
		return names.contains(name);
	}

	public boolean isValidParameter() {
		boolean ret = true;
		for (ContSpecItem item : cdItems) {
			if (item instanceof ContFunc) {
				List<String> names = new ArrayList<String>();
				getValueVariableNames(item.getExpression(), names);
				ret &= names.contains(
						((BehaviorVariableHolder) ((ContFunc) item).getParam()).getBehaviorVariable().getName());
			}
		}
		return ret;
	}

	private void getValueVariableNames(SimpleExpression sExpr, List<String> name) {
		for (Term term : sExpr.getTerms()) {
			for (Factor factor : term.getFactors()) {
				Value first = factor.getFirstValue();
				Value second = factor.getSecondValue();
				if (first != null && first instanceof BehaviorVariableHolder) {
					name.add(((BehaviorVariableHolder) first).getBehaviorVariable().getName());
				}
				if (second != null && second instanceof BehaviorVariableHolder) {
					name.add(((BehaviorVariableHolder) second).getBehaviorVariable().getName());
				}
				if (first != null && first instanceof SimpleExpression) {
					getValueVariableNames((SimpleExpression) first, name);
				}
				if (second != null && second instanceof SimpleExpression) {
					getValueVariableNames((SimpleExpression) second, name);
				}
			}
		}
	}
}
