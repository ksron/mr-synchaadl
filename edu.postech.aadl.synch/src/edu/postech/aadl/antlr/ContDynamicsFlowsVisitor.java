package edu.postech.aadl.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.impl.Aadl2FactoryImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.ba.aadlba.AadlBaFactory;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;

import edu.postech.aadl.antlr.ContDynamicsParser.ContFuncContext;
import edu.postech.aadl.antlr.ContDynamicsParser.ODEContext;
import edu.postech.aadl.antlr.model.Constant;
import edu.postech.aadl.antlr.model.ContDynamics;
import edu.postech.aadl.antlr.model.ContDynamicsElem;
import edu.postech.aadl.antlr.model.ContDynamicsItem;
import edu.postech.aadl.antlr.model.ContFunc;
import edu.postech.aadl.antlr.model.ContFuncTarget;
import edu.postech.aadl.antlr.model.Divide;
import edu.postech.aadl.antlr.model.Factor;
import edu.postech.aadl.antlr.model.FactorOperator;
import edu.postech.aadl.antlr.model.Minus;
import edu.postech.aadl.antlr.model.Multiply;
import edu.postech.aadl.antlr.model.ODE;
import edu.postech.aadl.antlr.model.ODETarget;
import edu.postech.aadl.antlr.model.Plus;
import edu.postech.aadl.antlr.model.Power;
import edu.postech.aadl.antlr.model.SimpleExpression;
import edu.postech.aadl.antlr.model.Target;
import edu.postech.aadl.antlr.model.Term;
import edu.postech.aadl.antlr.model.TermOperator;
import edu.postech.aadl.antlr.model.UnaryOperator;
import edu.postech.aadl.antlr.model.Value;
import edu.postech.aadl.antlr.model.ValueOperator;
import edu.postech.aadl.antlr.model.Variable;

public class ContDynamicsFlowsVisitor extends ContDynamicsBaseVisitor<ContDynamicsElem> {

	private AadlBaFactory aadlbaFactory;
	private Aadl2Factory aadl2Factory;

	public ContDynamicsFlowsVisitor(ComponentInstance ci) {
		aadlbaFactory = new AadlBaFactoryImpl();
		aadl2Factory = new Aadl2FactoryImpl();
	}

	@Override
	public ContDynamics visitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		ContDynamics cdObj = new ContDynamics();
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
		SimpleExpression expr = null;

		ContDynamicsParser.TargetContext targetCtx = ctx.target();
		if (targetCtx instanceof ContDynamicsParser.ContFuncContext) {
			item = new ContFunc();
			target = visitContFunc((ContFuncContext) targetCtx);
		} else if (targetCtx instanceof ContDynamicsParser.ODEContext) {
			item = new ODE();
			target = visitODE((ODEContext) targetCtx);
		}
		item.setTarget(target);

		expr = visitSimple_expression(ctx.simple_expression());
		item.setExpr(expr);
		return item;
	}

	@Override
	public ContFuncTarget visitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx) {
		ContFuncTarget target = new ContFuncTarget(visitValue_variable(ctx.value_variable(0)),
				visitValue_variable(ctx.value_variable(1)));
		return target;
	}

	@Override
	public ODETarget visitODE(@NotNull ContDynamicsParser.ODEContext ctx) {
		ODETarget target = new ODETarget(visitValue_variable(ctx.value_variable()));
		return target;
	}

	@Override
	public SimpleExpression visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx) {
		SimpleExpression expr = new SimpleExpression();

		UnaryOperator unaryOp = visitUnary_operator(ctx.unary_operator());
		expr.setUnaryOp(unaryOp);

		Term firstTerm = visitTerm(ctx.term(0));
		expr.addTerm(firstTerm);

		for (int i = 1; i < ctx.term().size(); i++) {
			Term term = visitTerm(ctx.term(i));
			expr.addTerm(term);
			TermOperator termOp = visitTerm_operator(ctx.term_operator(i - 1));
			expr.addTermOp(termOp);
		}
		return expr;
	}

	@Override
	public UnaryOperator visitUnary_operator(@NotNull ContDynamicsParser.Unary_operatorContext ctx) {
		UnaryOperator op = null;
		if (ctx.PLUS() != null) {
			op = new Plus(ctx.PLUS().getSymbol().getText());
		} else if (ctx.MINUS() != null) {
			op = new Minus(ctx.MINUS().getSymbol().getText());
		}
		return op;
	}

	@Override
	public Term visitTerm(@NotNull ContDynamicsParser.TermContext ctx) {
		Term term = new Term();

		Factor firstFact = visitFactor(ctx.factor(0));
		term.addFact(firstFact);

		for (int i = 1; i < ctx.factor().size(); i++) {
			Factor fact = visitFactor(ctx.factor(i));
			term.addFact(fact);
			FactorOperator factOp = visitFactor_operator(ctx.factor_operator(i - 1));
			term.addFactOp(factOp);
		}
		return term;
	}

	@Override
	public TermOperator visitTerm_operator(@NotNull ContDynamicsParser.Term_operatorContext ctx) {
		TermOperator op = null;
		if (ctx.PLUS() != null) {
			op = new Plus(ctx.PLUS().getSymbol().getText());
		} else if (ctx.MINUS() != null) {
			op = new Minus(ctx.MINUS().getSymbol().getText());
		}
		return op;
	}

	@Override
	public FactorOperator visitFactor_operator(@NotNull ContDynamicsParser.Factor_operatorContext ctx) {
		FactorOperator op = null;
		if (ctx.MUL() != null) {
			op = new Multiply(ctx.MUL().getSymbol().getText());
		} else if (ctx.DIVIDE() != null) {
			op = new Divide(ctx.DIVIDE().getSymbol().getText());
		}
		return op;
	}

	@Override
	public Factor visitFactor(@NotNull ContDynamicsParser.FactorContext ctx) {
		Factor fact = new Factor();

		Value left = visitValue(ctx.value(0));
		fact.setLeftVal(left);

		if (ctx.value_operator() != null) {
			ValueOperator op = visitValue_operator(ctx.value_operator());
			fact.setValOp(op);
		}

		if (ctx.value().size() > 1) {
			Value right = visitValue(ctx.value(1));
			fact.setRightVal(right);
		}

		return fact;
	}

	@Override
	public ValueOperator visitValue_operator(@NotNull ContDynamicsParser.Value_operatorContext ctx) {
		ValueOperator op = null;
		if (ctx.POWER() != null) {
			op = new Power(ctx.POWER().getSymbol().getText());
		}
		return op;
	}

	@Override
	public Value visitValue(@NotNull ContDynamicsParser.ValueContext ctx) {
		Value value = null;
		if (ctx.value_constant() != null) {
			value = new Value(visitValue_constant(ctx.value_constant()));
		} else if (ctx.value_variable() != null) {
			value = new Value(visitValue_variable(ctx.value_variable()));
		} else if (ctx.simple_expression() != null) {
			value = new Value(visitSimple_expression(ctx.simple_expression()));
		}
		return value;
	}

	@Override
	public Constant visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx) {
		Constant var = new Constant(ctx.getText());
		return var;
	}



	@Override
	public Variable visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx) {
		Variable var = new Variable(ctx.getText());
		return var;
	}
}
