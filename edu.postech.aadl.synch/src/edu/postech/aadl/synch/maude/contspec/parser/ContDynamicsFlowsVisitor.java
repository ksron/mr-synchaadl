package edu.postech.aadl.synch.maude.contspec.parser;

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
import org.osate.ba.aadlba.BinaryAddingOperator;
import org.osate.ba.aadlba.BinaryNumericOperator;
import org.osate.ba.aadlba.DataSubcomponentHolder;
import org.osate.ba.aadlba.Factor;
import org.osate.ba.aadlba.MultiplyingOperator;
import org.osate.ba.aadlba.Relation;
import org.osate.ba.aadlba.SimpleExpression;
import org.osate.ba.aadlba.Term;
import org.osate.ba.aadlba.Value;
import org.osate.ba.aadlba.ValueConstant;
import org.osate.ba.aadlba.ValueExpression;
import org.osate.ba.aadlba.ValueVariable;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;
import org.osate.ba.utils.visitor.IBAVisitable;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import edu.postech.aadl.synch.maude.contspec.ContFunc;
import edu.postech.aadl.synch.maude.contspec.ContSpec;
import edu.postech.aadl.synch.maude.contspec.ContSpecItem;
import edu.postech.aadl.synch.maude.contspec.ODE;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser.ContFuncContext;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser.Factor_expressionContext;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser.Factor_operatorContext;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser.ODEContext;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser.Term_expressionContext;
import edu.postech.aadl.synch.maude.contspec.parser.ContDynamicsParser.Term_operatorContext;

public class ContDynamicsFlowsVisitor extends ContDynamicsBaseVisitor<IBAVisitable> {

	private ContSpec spec;
	private ComponentInstance ci;
	private AadlBaFactory aadlbaFactory;
	private Aadl2Factory aadl2Factory;

	public ContSpec visit(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx, ComponentInstance ci) {
		spec = new ContSpec();
		aadlbaFactory = new AadlBaFactoryImpl();
		aadl2Factory = new Aadl2FactoryImpl();
		this.ci = ci;
		visitContinuousdynamics(ctx);
		return spec;
	}

	@Override
	public IBAVisitable visitContinuousdynamics(@NotNull ContDynamicsParser.ContinuousdynamicsContext ctx) {
		for (ContDynamicsParser.AssignmentContext assign : ctx.assignment()) {
			visitAssignment(assign);
		}
		return null;
	}

	@Override
	public IBAVisitable visitAssignment(@NotNull ContDynamicsParser.AssignmentContext ctx) {
		ContSpecItem item = null;
		SimpleExpression expr = visitSimple_expression(ctx.simple_expression());
		if (ctx.target() instanceof ODEContext) {
			ValueVariable var = visitODE((ODEContext) ctx.target());
			item = new ODE(var, expr);
		} else {
			ValueVariable var = visitContFunc((ContFuncContext) ctx.target());
			ValueVariable param = visitContFuncParam((ContFuncContext) ctx.target());
			item = new ContFunc(var, param, expr);
		}
		spec.addItem(item);
		return null;
	}

	@Override
	public ValueVariable visitODE(@NotNull ContDynamicsParser.ODEContext ctx) {
		ValueVariable vv = aadlbaFactory.createDataSubcomponentHolder();
		DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
		dsc.setName(ctx.value_variable().getText());
		((DataSubcomponentHolder) vv).setDataSubcomponent(dsc);
		return vv;
	}

	@Override
	public ValueVariable visitContFunc(@NotNull ContDynamicsParser.ContFuncContext ctx) {
		ValueVariable vv = aadlbaFactory.createDataSubcomponentHolder();
		DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
		dsc.setName(ctx.value_variable(0).getText());
		((DataSubcomponentHolder) vv).setDataSubcomponent(dsc);
		return vv;
	}

	public ValueVariable visitContFuncParam(@NotNull ContDynamicsParser.ContFuncContext ctx) {
		ValueVariable vv = aadlbaFactory.createBehaviorVariableHolder();
		BehaviorVariable bv = aadlbaFactory.createBehaviorVariable();
		bv.setName(ctx.value_variable(1).getText());
		((BehaviorVariableHolder) vv).setVariable(bv);
		return vv;
	}

	@Override
	public SimpleExpression visitSimple_expression(@NotNull ContDynamicsParser.Simple_expressionContext ctx) {
		SimpleExpression expr = aadlbaFactory.createSimpleExpression();
		for (Term_operatorContext op : ctx.term_operator()) {
			switch (op.getText()) {
			case "+":
				expr.getBinaryAddingOperators().add(BinaryAddingOperator.PLUS);
				break;
			case "-":
				expr.getBinaryAddingOperators().add(BinaryAddingOperator.MINUS);
				break;
			default:
				expr.getBinaryAddingOperators().add(BinaryAddingOperator.NONE);
				break;
			}
		}
		for (Term_expressionContext term : ctx.term_expression()) {
			expr.getTerms().add(visitTerm_expression(term));
		}
		return expr;
	}

	@Override
	public Term visitTerm_expression(@NotNull ContDynamicsParser.Term_expressionContext ctx) {
		Term term = aadlbaFactory.createTerm();
		for (Factor_operatorContext op : ctx.factor_operator()) {
			switch (op.getText()) {
			case "*":
				term.getMultiplyingOperators().add(MultiplyingOperator.MULTIPLY);
				break;
			case "/":
				term.getMultiplyingOperators().add(MultiplyingOperator.DIVIDE);
				break;
			default:
				term.getMultiplyingOperators().add(MultiplyingOperator.NONE);
				break;
			}
		}
		for (Factor_expressionContext factor : ctx.factor_expression()) {
			term.getFactors().add(visitFactor_expression(factor));
		}
		return term;
	}

	@Override
	public Factor visitFactor_expression(@NotNull ContDynamicsParser.Factor_expressionContext ctx) {
		Factor factor = aadlbaFactory.createFactor();
		factor.setFirstValue(visitValue_expression(ctx.value_expression(0)));
		if (ctx.value_operator() != null) {
			switch (ctx.value_operator().getText()) {
			case "**":
				factor.setBinaryNumericOperator(BinaryNumericOperator.MULTIPLY_MULTIPLY);
				break;
			default:
				factor.setBinaryNumericOperator(BinaryNumericOperator.NONE);
				break;
			}
		}
		if (ctx.value_expression(1) != null) {
			factor.setSecondValue(visitValue_expression(ctx.value_expression(1)));
		}
		return factor;
	}


	@Override
	public Value visitValue_expression(@NotNull ContDynamicsParser.Value_expressionContext ctx) {
		if (ctx.value_variable() != null) {
			return visitValue_variable(ctx.value_variable());
		} else if (ctx.value_constant() != null) {
			return visitValue_constant(ctx.value_constant());
		} else {
			ValueExpression expr = aadlbaFactory.createValueExpression();
			Relation rel = aadlbaFactory.createRelation();
			rel.setFirstExpression(visitSimple_expression(ctx.simple_expression()));
			expr.getRelations().add(rel);
			return expr;
		}
	}

	@Override
	public ValueConstant visitValue_constant(@NotNull ContDynamicsParser.Value_constantContext ctx) {
		AadlBaFactory aadlbaFactory = new AadlBaFactoryImpl();
		if (ctx.getText().matches("([0-9]+)(\\.[0-9]+)?")) {
			BehaviorRealLiteral brl = aadlbaFactory.createBehaviorRealLiteral();
			brl.setValue(ctx.getText());
			return brl;
		} else {
			BehaviorPropertyConstant cspr = aadlbaFactory.createBehaviorPropertyConstant();
			String[] prop = ctx.getText().split("::");
			cspr.setProperty(GetProperties.lookupPropertyConstant(ci, prop[0], prop[1]));
			return cspr;
		}
	}

	@Override
	public ValueVariable visitValue_variable(@NotNull ContDynamicsParser.Value_variableContext ctx) {
		AadlBaFactory aadlbaFactory = new AadlBaFactoryImpl();
		Aadl2Factory aadl2Factory = new Aadl2FactoryImpl();
		ValueVariable vv = null;
		if (checkHasDataSubCompHolder(ci, ctx.getText())) {
			vv = aadlbaFactory.createDataSubcomponentHolder();
			DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
			if(ctx.zero != null) {
				dsc.setName(ctx.getText().substring(0, ctx.getText().indexOf("(0)")));
			} else {
				dsc.setName(ctx.getText());
			}
			((DataSubcomponentHolder) vv).setDataSubcomponent(dsc);
		} else {
			vv = aadlbaFactory.createBehaviorVariableHolder();
			BehaviorVariable bv = aadlbaFactory.createBehaviorVariable();
			bv.setName(ctx.getText());
			((BehaviorVariableHolder) vv).setVariable(bv);
		}
		return vv;
	}

	private boolean checkHasDataSubCompHolder(ComponentInstance ci, String var) {
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
