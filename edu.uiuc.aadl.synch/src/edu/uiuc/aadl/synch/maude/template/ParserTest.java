package edu.uiuc.aadl.synch.maude.template;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import edu.postech.antlr.parser.FlowsLexer;
import edu.postech.antlr.parser.FlowsParser;

public class ParserTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ANTLRInputStream stream = new ANTLRInputStream("x(t) = (-dotx * t) + x(0)");
		FlowsLexer lexer = new FlowsLexer(stream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FlowsParser parser = new FlowsParser(tokens);

		System.out.println(parser.getBuildParseTree());
	}

}
