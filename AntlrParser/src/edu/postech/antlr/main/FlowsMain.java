package edu.postech.antlr.main;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import edu.postech.antlr.formula.FlowsData;
import edu.postech.antlr.formula.FlowsVisitor;
import edu.postech.antlr.parser.FlowsLexer;
import edu.postech.antlr.parser.FlowsParser;

public class FlowsMain {
    public static void main(String[] args){
		ANTLRInputStream cd = new ANTLRInputStream("x(t) = ThermostatSpec::K * (ThermostatSpec::h1 - x(0)) * t;");
        FlowsLexer lexer = new FlowsLexer(cd);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FlowsParser parser = new FlowsParser(tokens);

        System.out.println("Build Parse Tree : " + parser.getBuildParseTree());

        FlowsData flowsData = new FlowsData();
		flowsData.addConstant("ThermostatSpec::K", "0.1");
		flowsData.addConstant("ThermostatSpec::h1", "0.2");
        flowsData.addInitialValue("x", "0.3");

        String answer = new FlowsVisitor().setFlowsData(flowsData).visitFormula(parser.formula());
        System.out.println(answer);

    }
}
