package edu.postech.aadl.synch.maude.parse;

import org.antlr.v4.runtime.misc.NotNull;
import org.osate.aadl2.instance.ComponentInstance;

import edu.postech.aadl.synch.maude.parse.ContDynamicsParser.AssignmentContext;
import edu.postech.aadl.synch.maude.parse.ContDynamicsParser.ContFuncContext;
import edu.postech.aadl.synch.maude.parse.ContDynamicsParser.ODEContext;
import edu.postech.aadl.synch.maude.parse.model.CDExpression;
import edu.postech.aadl.synch.maude.parse.model.Constant;
import edu.postech.aadl.synch.maude.parse.model.ContDynamics;
import edu.postech.aadl.synch.maude.parse.model.ContDynamicsItem;
import edu.postech.aadl.synch.maude.parse.model.ContFunc;
import edu.postech.aadl.synch.maude.parse.model.FactorCDExpression;
import edu.postech.aadl.synch.maude.parse.model.ODE;
import edu.postech.aadl.synch.maude.parse.model.SimpleCDExpression;
import edu.postech.aadl.synch.maude.parse.model.TermCDExpression;
import edu.postech.aadl.synch.maude.parse.model.Variable;

public class ContDynamicsFlowsVisitor extends ContDynamicsBaseVisitor<CDExpression> {

	private ContDynamics cd;
	private ComponentInstance ci;

	public ContDynamicsFlowsVisitor(ComponentInstance ci) {
		this.ci = ci;
	}

	public ContDynamics getContDynamics() {
		return cd;
	}

	@Override
	public CDExpression visitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		cd = new ContDynamics();
		for (AssignmentContext ac : ctx.assignment()) {
			visitAssignment(ac);
		}
		return null;
	}

	@Override
	public CDExpression visitAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx) {
		ContDynamicsItem item = null;
		if (ctx.target() instanceof ODEContext) {
			item = new ODE();
			item.setTarget((Variable) visitODE((ODEContext) ctx.target()));
		} else if (ctx.target() instanceof ContFuncContext) {
			item = new ContFunc();
			item.setTarget((Variable) visitContFunc((ContFuncContext) ctx.target()));
		}
		item.setExpression(visitSimple_expression(ctx.simple_expression()));
		cd.addItem(item);
		return null;
	}

	@Override
	public CDExpression visitODE(@NotNull ContDynamicsParser.ODEContext ctx) {
		return new Variable(ctx.value_variable().getText(), ci);
	}

	@Override
	public CDExpression visitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx) {
		return new Variable(ctx.value_variable(0).getText(), ctx.value_variable(1).getText(), ci);
	}

	@Override
	public CDExpression visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx) {
		SimpleCDExpression expr = new SimpleCDExpression();
		expr.setLeftExpression(visitTerm_expression(ctx.term_expression()));
		if (ctx.simple_expression() != null && ctx.term_operator() != null) {
			expr.setRightExpression(visitSimple_expression(ctx.simple_expression()));
			expr.setOperation(ctx.term_operator().getText());
		}
		return expr;
	}

	@Override
	public CDExpression visitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx) {
		TermCDExpression expr = new TermCDExpression();
		expr.setLeftExpression(visitFactor_expression(ctx.factor_expression()));
		if (ctx.term_expression() != null && ctx.factor_operator() != null) {
			expr.setRightExpression(visitTerm_expression(ctx.term_expression()));
			expr.setOperation(ctx.factor_operator().getText());
		}
		return expr;
	}

	@Override
	public CDExpression visitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx) {
		FactorCDExpression expr = new FactorCDExpression();
		expr.setLeftExpression(visitValue_expression(ctx.value_expression()));
		if (ctx.factor_expression() != null && ctx.value_operator() != null) {
			expr.setRightExpression(visitFactor_expression(ctx.factor_expression()));
			expr.setOperation(ctx.value_operator().getText());
		}
		return expr;
	}

	@Override
	public CDExpression visitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx) {
		CDExpression expr = visitChildren(ctx);
		if (ctx.unary_operator() != null) {
			expr.setUnaryOperation(ctx.unary_operator().getText());
		}
		return expr;
	}

	@Override
	public CDExpression visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx) {
		return new Constant(ctx.getText(), ci);
	}

	@Override
	public CDExpression visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx) {
		return new Variable(ctx.getText(), ci);
	}



}
