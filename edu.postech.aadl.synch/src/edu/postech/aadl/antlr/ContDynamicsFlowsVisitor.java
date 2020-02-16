package edu.postech.aadl.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.eclipse.emf.common.util.ECollections;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.impl.Aadl2FactoryImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.ba.aadlba.AadlBaFactory;
import org.osate.ba.aadlba.BehaviorPropertyConstant;
import org.osate.ba.aadlba.BehaviorRealLiteral;
import org.osate.ba.aadlba.BehaviorVariable;
import org.osate.ba.aadlba.BehaviorVariableHolder;
import org.osate.ba.aadlba.DataSubcomponentHolder;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import edu.postech.aadl.antlr.ContDynamicsParser.ContFuncContext;
import edu.postech.aadl.antlr.ContDynamicsParser.ODEContext;
import edu.postech.aadl.antlr.model.CDExpression;
import edu.postech.aadl.antlr.model.Constant;
import edu.postech.aadl.antlr.model.ContDynamics;
import edu.postech.aadl.antlr.model.ContDynamicsElem;
import edu.postech.aadl.antlr.model.ContDynamicsItem;
import edu.postech.aadl.antlr.model.FactorCDExpression;
import edu.postech.aadl.antlr.model.Operator;
import edu.postech.aadl.antlr.model.SimpleCDExpression;
import edu.postech.aadl.antlr.model.Target;
import edu.postech.aadl.antlr.model.TermCDExpression;
import edu.postech.aadl.antlr.model.ValueCDExpression;
import edu.postech.aadl.antlr.model.Variable;
import edu.postech.aadl.antlr.model.impl.ConstantImpl;
import edu.postech.aadl.antlr.model.impl.ContDynamicsImpl;
import edu.postech.aadl.antlr.model.impl.ContFuncImpl;
import edu.postech.aadl.antlr.model.impl.FactorCDExpressionImpl;
import edu.postech.aadl.antlr.model.impl.ODEImpl;
import edu.postech.aadl.antlr.model.impl.OperatorImpl;
import edu.postech.aadl.antlr.model.impl.TargetImpl;
import edu.postech.aadl.antlr.model.impl.TermCDExpressionImpl;
import edu.postech.aadl.antlr.model.impl.TokenCDExpressionImpl;
import edu.postech.aadl.antlr.model.impl.ValueCDExpressionImpl;
import edu.postech.aadl.antlr.model.impl.VariableImpl;

public class ContDynamicsFlowsVisitor extends ContDynamicsBaseVisitor<ContDynamicsElem> {
	private ComponentInstance ci;
	private AadlBaFactory aadlbaFactory;
	private Aadl2Factory aadl2Factory;

	public ContDynamics visit(ComponentInstance ci, @NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		this.ci = ci;
		aadlbaFactory = new AadlBaFactoryImpl();
		aadl2Factory = new Aadl2FactoryImpl();
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
		CDExpression expr = null;

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
	public SimpleCDExpression visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx) {
		SimpleCDExpression expr = new TokenCDExpressionImpl();

		TermCDExpression termExpr = visitTerm_expression(ctx.term_expression(0));
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
	public TermCDExpression visitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx) {
		TermCDExpression term = new TermCDExpressionImpl();

		FactorCDExpression factExpr = visitFactor_expression(ctx.factor_expression(0));
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
	public FactorCDExpression visitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx) {
		FactorCDExpression fact = new FactorCDExpressionImpl();

		ValueCDExpression left = visitValue_expression(ctx.value_expression(0));
		fact.addExpr(left);

		if (ctx.value_operator() != null) {
			Operator op = visitValue_operator(ctx.value_operator());
			fact.addOp(op);
		}

		if (ctx.value_expression().size() > 1) {
			ValueCDExpression right = visitValue_expression(ctx.value_expression(1));
			fact.addExpr(right);
		}

		return fact;
	}

	@Override
	public Operator visitValue_operator(@NotNull ContDynamicsParser.Value_operatorContext ctx) {
		return new OperatorImpl(ctx.getText());
	}

	@Override
	public ValueCDExpression visitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx) {
		ValueCDExpression expr = new ValueCDExpressionImpl();
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
	public Constant visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx) {
		Constant constant = new ConstantImpl();
		if (ctx.getText().matches("([0-9]+)(\\.[0-9]+)?")) {
			BehaviorRealLiteral brl = aadlbaFactory.createBehaviorRealLiteral();
			brl.setValue(ctx.getText());
			constant.setValue(brl);
		} else {
			BehaviorPropertyConstant cspr = aadlbaFactory.createBehaviorPropertyConstant();
			String[] prop = ctx.getText().split("::");
			cspr.setProperty(GetProperties.lookupPropertyConstant(ci, prop[0], prop[1]));
			constant.setValue(cspr);
		}
		return constant;
	}



	@Override
	public Variable visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx) {
		Variable var = new VariableImpl();
		if (checkHasDataSubCompHolder(ctx.getText())) {
			DataSubcomponentHolder dsch = aadlbaFactory.createDataSubcomponentHolder();
			DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
			dsc.setName(ctx.var.getText());
			dsch.setDataSubcomponent(dsc);
			var.setValue(dsch);
		} else {
			BehaviorVariableHolder bvh = aadlbaFactory.createBehaviorVariableHolder();
			BehaviorVariable bv = aadlbaFactory.createBehaviorVariable();
			bv.setName(ctx.getText());
			bvh.setVariable(bv);
			var.setValue(bvh);
		}
		return var;
	}

	private boolean checkHasDataSubCompHolder(String var) {
		ECollections.sort(ci.getComponentInstances(), (o1, o2) -> {
			if (o1.getName().toString().length() < o2.getName().toString().length()) {
				return 1;
			}
			return -1;
		});
		for (ComponentInstance o : ci.getComponentInstances()) {
			if (var.contains(o.getName())) {
				return true;
			}
		}
		return false;
	}
}
