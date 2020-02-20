package edu.postech.aadl.synch.maude.parse;

import org.antlr.v4.runtime.misc.NotNull;

import edu.postech.aadl.synch.maude.parse.ContDynamicsParser.AssignmentContext;
import edu.postech.aadl.synch.maude.parse.ContDynamicsParser.ContFuncContext;
import edu.postech.aadl.synch.maude.parse.ContDynamicsParser.ODEContext;
import edu.postech.aadl.synch.maude.parse.model.Constant;
import edu.postech.aadl.synch.maude.parse.model.ContDynamics;
import edu.postech.aadl.synch.maude.parse.model.ContDynamicsItem;
import edu.postech.aadl.synch.maude.parse.model.ContFunc;
import edu.postech.aadl.synch.maude.parse.model.Expression;
import edu.postech.aadl.synch.maude.parse.model.FactorExpression;
import edu.postech.aadl.synch.maude.parse.model.ODE;
import edu.postech.aadl.synch.maude.parse.model.SimpleExpression;
import edu.postech.aadl.synch.maude.parse.model.TermExpression;
import edu.postech.aadl.synch.maude.parse.model.Variable;

public class ContDynamicsFlowsVisitor extends ContDynamicsBaseVisitor<Expression> {

	private ContDynamics cd;

	public ContDynamics getContDynamics() {
		return cd;
	}

	@Override
	public Expression visitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		cd = new ContDynamics();
		for (AssignmentContext ac : ctx.assignment()) {
			visitAssignment(ac);
		}
		return null;
	}

	@Override
	public Expression visitAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx) {
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
	public Expression visitODE(@NotNull ContDynamicsParser.ODEContext ctx) {
		return new Variable(ctx.value_variable().getText());
	}

	@Override
	public Expression visitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx) {
		return new Variable(ctx.value_variable(0).getText(), ctx.value_variable(1).getText());
	}

	@Override
	public Expression visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx) {
		SimpleExpression expr = new SimpleExpression();
		expr.setLeftExpression(visitTerm_expression(ctx.term_expression()));
		if (ctx.simple_expression() != null && ctx.term_operator() != null) {
			expr.setRightExpression(visitSimple_expression(ctx.simple_expression()));
			expr.setOperation(ctx.term_operator().getText());
		}
		return expr;
	}

	@Override
	public Expression visitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx) {
		TermExpression expr = new TermExpression();
		expr.setLeftExpression(visitFactor_expression(ctx.factor_expression()));
		if (ctx.term_expression() != null && ctx.factor_operator() != null) {
			expr.setRightExpression(visitTerm_expression(ctx.term_expression()));
			expr.setOperation(ctx.factor_operator().getText());
		}
		return expr;
	}

	@Override
	public Expression visitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx) {
		FactorExpression expr = new FactorExpression();
		expr.setLeftExpression(visitValue_expression(ctx.value_expression()));
		if (ctx.factor_expression() != null && ctx.value_operator() != null) {
			expr.setRightExpression(visitFactor_expression(ctx.factor_expression()));
			expr.setOperation(ctx.value_operator().getText());
		}
		return expr;
	}

	@Override
	public Expression visitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx) {
		Expression expr = visitChildren(ctx);
		if (ctx.unary_operator() != null) {
			expr.setUnaryOperation(ctx.unary_operator().getText());
		}
		return expr;
	}

	@Override
	public Expression visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx) {
		return new Constant(ctx.getText());
	}

	@Override
	public Expression visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx) {
		return new Variable(ctx.getText());
	}



}
