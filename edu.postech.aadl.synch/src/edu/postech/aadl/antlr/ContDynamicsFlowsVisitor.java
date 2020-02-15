package edu.postech.aadl.antlr;

import org.antlr.v4.runtime.misc.NotNull;

import edu.postech.aadl.antlr.ContDynamicsParser.ContFuncContext;
import edu.postech.aadl.antlr.ContDynamicsParser.ODEContext;
import edu.postech.aadl.antlr.model.ContDynamics;
import edu.postech.aadl.antlr.model.ContDynamicsElem;
import edu.postech.aadl.antlr.model.ContDynamicsItem;
import edu.postech.aadl.antlr.model.Expression;
import edu.postech.aadl.antlr.model.FactorExpression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.TokenExpression;
import edu.postech.aadl.antlr.model.Target;
import edu.postech.aadl.antlr.model.TermExpression;
import edu.postech.aadl.antlr.model.ValueExpression;
import edu.postech.aadl.antlr.model.Variable;
import edu.postech.aadl.antlr.model.impl.ConstantImpl;
import edu.postech.aadl.antlr.model.impl.ContDynamicsImpl;
import edu.postech.aadl.antlr.model.impl.ContFuncImpl;
import edu.postech.aadl.antlr.model.impl.FactorExpressionImpl;
import edu.postech.aadl.antlr.model.impl.ODEImpl;
import edu.postech.aadl.antlr.model.impl.OperatorImpl;
import edu.postech.aadl.antlr.model.impl.TokenExpressionImpl;
import edu.postech.aadl.antlr.model.impl.TargetImpl;
import edu.postech.aadl.antlr.model.impl.TermExpressionImpl;
import edu.postech.aadl.antlr.model.impl.ValueExpressionImpl;
import edu.postech.aadl.antlr.model.impl.VariableImpl;

public class ContDynamicsFlowsVisitor extends ContDynamicsBaseVisitor<ContDynamicsElem> {

	public ContDynamics visit(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		return visitContinuousdynamics(ctx);
	}

	@Override
	public ContDynamics visitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		ContDynamics cdObj = new ContDynamicsImpl();
		for (ContDynamicsParser.AssignmentContext assignCtx : ctx.assignment()) {
			ContDynamicsItem cdItem = visitAssignment(assignCtx);
			cdObj.addItem(cdItem);
		}
		return cdObj;
	}

	@Override
	public ContDynamicsItem visitAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx) {
		ContDynamicsItem item = null;
		Target target = null;
		Expression expr = null;

		ContDynamicsParser.TargetContext targetCtx = ctx.target();
		if (targetCtx instanceof ContDynamicsParser.ContFuncContext) {
			item = new ContFuncImpl();
			target = visitContFunc((ContFuncContext) targetCtx);
		} else if (targetCtx instanceof ContDynamicsParser.ODEContext) {
			item = new ODEImpl();
			target = visitODE((ODEContext) targetCtx);
		}
		item.setTarget(target);

		expr = visitSimple_expression(ctx.simple_expression());
		item.setExpression(expr);
		return item;
	}

	@Override
	public Target visitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx) {
		Target target = new TargetImpl(visitValue_variable(ctx.value_variable(0)));
		target.setParam(visitValue_variable(ctx.value_variable(1)));
		return target;
	}

	@Override
	public Target visitODE(@NotNull ContDynamicsParser.ODEContext ctx) {
		Target target = new TargetImpl(visitValue_variable(ctx.value_variable()));
		return target;
	}

	@Override
	public TokenExpression visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx) {
		TokenExpression expr = new TokenExpressionImpl();

		TermExpression termExpr = visitTerm_expression(ctx.term_expression(0));
		expr.addExpr(termExpr);

		for (int i = 1; i < ctx.term_expression().size(); i++) {
			termExpr = visitTerm_expression(ctx.term_expression(i));
			expr.addExpr(termExpr);
			Operator termOp = visitTerm_operator(ctx.term_operator(i - 1));
			expr.addOp(termOp);
		}
		return expr;
	}

	@Override
	public Operator visitTerm_operator(@NotNull ContDynamicsParser.Term_operatorContext ctx) {
		return new OperatorImpl(ctx.getText());
	}


	@Override
	public TermExpression visitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx) {
		TermExpression term = new TermExpressionImpl();

		FactorExpression factExpr = visitFactor_expression(ctx.factor_expression(0));
		term.addExpr(factExpr);

		for (int i = 1; i < ctx.factor_expression().size(); i++) {
			factExpr = visitFactor_expression(ctx.factor_expression(i));
			term.addExpr(factExpr);
			Operator factOp = visitFactor_operator(ctx.factor_operator(i - 1));
			term.addOp(factOp);
		}
		return term;
	}

	@Override
	public Operator visitFactor_operator(@NotNull ContDynamicsParser.Factor_operatorContext ctx) {
		return new OperatorImpl(ctx.getText());
	}

	@Override
	public FactorExpression visitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx) {
		FactorExpression fact = new FactorExpressionImpl();

		ValueExpression left = visitValue_expression(ctx.value_expression(0));
		fact.addExpr(left);

		if (ctx.value_operator() != null) {
			Operator op = visitValue_operator(ctx.value_operator());
			fact.addOp(op);
		}

		if (ctx.value_expression().size() > 1) {
			ValueExpression right = visitValue_expression(ctx.value_expression(1));
			fact.addExpr(right);
		}

		return fact;
	}

	@Override
	public Operator visitValue_operator(@NotNull ContDynamicsParser.Value_operatorContext ctx) {
		return new OperatorImpl(ctx.getText());
	}

	@Override
	public ValueExpression visitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx) {
		ValueExpression expr = new ValueExpressionImpl();
		if (ctx.unary_operator() != null) {
			expr.addOp(visitUnary_operator(ctx.unary_operator()));
		}
		if (ctx.value_constant() != null) {
			expr.addExpr(visitValue_constant(ctx.value_constant()));
		} else if (ctx.value_variable() != null) {
			expr.addExpr(visitValue_variable(ctx.value_variable()));
		} else if (ctx.simple_expression() != null) {
			expr.addExpr(visitSimple_expression(ctx.simple_expression()));
		}
		return expr;
	}

	@Override
	public Operator visitUnary_operator(@NotNull ContDynamicsParser.Unary_operatorContext ctx) {
		return new OperatorImpl(ctx.getText());
	}

	@Override
	public ConstantImpl visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx) {
		return new ConstantImpl(ctx.getText());
	}



	@Override
	public Variable visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx) {
		return new VariableImpl(ctx.getText());
	}
}
