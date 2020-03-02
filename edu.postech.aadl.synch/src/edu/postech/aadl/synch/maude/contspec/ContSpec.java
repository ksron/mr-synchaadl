package edu.postech.aadl.synch.maude.contspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.osate.aadl2.instance.ComponentInstance;

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
}
