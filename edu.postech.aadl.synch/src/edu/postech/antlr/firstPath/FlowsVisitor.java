package edu.postech.antlr.firstPath;

import java.util.Iterator;

import org.antlr.v4.runtime.misc.NotNull;
import org.eclipse.emf.common.util.ECollections;
import org.osate.aadl2.Aadl2Factory;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.impl.Aadl2FactoryImpl;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.ba.aadlba.AadlBaFactory;
import org.osate.ba.aadlba.AssignmentAction;
import org.osate.ba.aadlba.BehaviorAction;
import org.osate.ba.aadlba.BehaviorActionSequence;
import org.osate.ba.aadlba.BehaviorIntegerLiteral;
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
import org.osate.ba.aadlba.Target;
import org.osate.ba.aadlba.Term;
import org.osate.ba.aadlba.UnaryAddingOperator;
import org.osate.ba.aadlba.Value;
import org.osate.ba.aadlba.ValueExpression;
import org.osate.ba.aadlba.ValueVariable;
import org.osate.ba.aadlba.impl.AadlBaFactoryImpl;
import org.osate.ba.utils.visitor.IBAVisitable;
import org.osate.xtext.aadl2.properties.util.GetProperties;

import edu.postech.antlr.parser.FlowsBaseVisitor;
import edu.postech.antlr.parser.FlowsParser;
import edu.postech.antlr.parser.FlowsParser.Binary_adding_operatorContext;
import edu.postech.antlr.parser.FlowsParser.FactorContext;
import edu.postech.antlr.parser.FlowsParser.Multiplying_operatorContext;
import edu.postech.antlr.parser.FlowsParser.RelationContext;
import edu.postech.antlr.parser.FlowsParser.TermContext;

public class FlowsVisitor extends FlowsBaseVisitor<IBAVisitable> {

	private AadlBaFactory aadlbaFactory;
	private Aadl2Factory aadl2Factory;
	private ComponentInstance ci;

	public FlowsVisitor() {
		aadlbaFactory = new AadlBaFactoryImpl();
		aadl2Factory = new Aadl2FactoryImpl();
    }

	public FlowsVisitor setComponentInstance(ComponentInstance ci) {
		this.ci = ci;
		return this;
	}

	@Override
	public IBAVisitable visitContinuousdynamics(@NotNull FlowsParser.ContinuousdynamicsContext ctx) {
		System.out.println("Continuousdynamics : " + ctx.getText());
		BehaviorActionSequence bas = aadlbaFactory.createBehaviorActionSequence();
		for (FlowsParser.AssignmentContext assignment : ctx.assignment()) {
			bas.getActions().add((BehaviorAction) visitAssignment(assignment));
		}
		return bas;
	}

	@Override
	public IBAVisitable visitAssignment(@NotNull FlowsParser.AssignmentContext ctx) {
		System.out.println("Assignment: " + ctx.getText());
		AssignmentAction aa = aadlbaFactory.createAssignmentAction();
		aa.setTarget((Target) visitTarget(ctx.target()));
		aa.setValueExpression((ValueExpression) visitValue_expression(ctx.value_expression()));
		return aa;
	}

	@Override
	public IBAVisitable visitTarget(@NotNull FlowsParser.TargetContext ctx) {
		System.out.println("Target : " + ctx.getText());
		DataSubcomponentHolder dscHolder = aadlbaFactory.createDataSubcomponentHolder();
		DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
		dsc.setName(ctx.getText());
		dscHolder.setDataSubcomponent(dsc);

		return dscHolder;
	}

	@Override
	public IBAVisitable visitValue_expression(@NotNull FlowsParser.Value_expressionContext ctx) {
		System.out.println("Value_expression : " + ctx.getText());
		ValueExpression ve = aadlbaFactory.createValueExpression();
		for (RelationContext relation : ctx.relation()) {
			ve.getRelations().add((Relation) visitRelation(relation));
		}
		return ve;
	}

	@Override
	public IBAVisitable visitRelation(@NotNull FlowsParser.RelationContext ctx) {
		System.out.println("Continuousdynamics : " + ctx.getText());
		Relation re = aadlbaFactory.createRelation();
		re.setFirstExpression((SimpleExpression) visitSimple_expression(ctx.simple_expression(0)));

		return re;
	}

	@Override
	public IBAVisitable visitSimple_expression(@NotNull FlowsParser.Simple_expressionContext ctx) {
		System.out.println("Continuousdynamics : " + ctx.getText());
		SimpleExpression se = aadlbaFactory.createSimpleExpression();
		if (ctx.unary_adding_operator() != null) {
			switch (ctx.unary_adding_operator().getText().charAt(0)) {
			case '-':
				se.setUnaryAddingOperator(UnaryAddingOperator.MINUS);
				break;
			case '+':
				se.setUnaryAddingOperator(UnaryAddingOperator.PLUS);
				break;
			}
		}

		Iterator<TermContext> itTerm = ctx.term().iterator();
		Iterator<Binary_adding_operatorContext> itOperator = ctx.binary_adding_operator().iterator();
		se.getTerms().add((Term) visitTerm(itTerm.next()));

		while (itTerm.hasNext() && itOperator.hasNext()) {
			switch (itOperator.next().getText().charAt(0)) {
			case '-':
				se.getBinaryAddingOperators().add(BinaryAddingOperator.MINUS);
				break;
			case '+':
				se.getBinaryAddingOperators().add(BinaryAddingOperator.PLUS);
				break;
			}
			se.getTerms().add((Term) visitTerm(itTerm.next()));
		}
		return se;
	}

	@Override
	public IBAVisitable visitTerm(@NotNull FlowsParser.TermContext ctx) {
		System.out.println("Term : " + ctx.getText());
		Term te = aadlbaFactory.createTerm();
		Iterator<FactorContext> itFactor = ctx.factor().iterator();
		Iterator<Multiplying_operatorContext> itOperator = ctx.multiplying_operator().iterator();
		te.getFactors().add((Factor) visitFactor(itFactor.next()));

		while (itFactor.hasNext() && itOperator.hasNext()) {
			switch (itOperator.next().getText().charAt(0)) {
			case '*':
				te.getMultiplyingOperators().add(MultiplyingOperator.MULTIPLY);
				break;
			case '/':
				te.getMultiplyingOperators().add(MultiplyingOperator.DIVIDE);
				break;
			}
			te.getFactors().add((Factor) visitFactor(itFactor.next()));
		}
		return te;
	}

	@Override
	public IBAVisitable visitFactor(@NotNull FlowsParser.FactorContext ctx) {
		System.out.println("Factor : " + ctx.getText());
		Factor fa = aadlbaFactory.createFactor();
		fa.setFirstValue((Value) visitValue(ctx.value(0)));
		if (ctx.binary_numeric_operator() != null) {
			fa.setBinaryNumericOperator(BinaryNumericOperator.MULTIPLY_MULTIPLY);
			fa.setSecondValue((Value) visitValue(ctx.value(1)));
		}

		return fa;
	}

	@Override
	public IBAVisitable visitValue(@NotNull FlowsParser.ValueContext ctx) {
		System.out.println("Value : " + ctx.getText());
		if (ctx.LPAREN() != null && ctx.RPAREN() != null) {
			return visitValue_expression(ctx.value_expression());
		}
		return visitChildren(ctx);

	}

	@Override
	public IBAVisitable visitValue_constant(@NotNull FlowsParser.Value_constantContext ctx) {
		System.out.println("Value_constant : " + ctx.getText());
		if (ctx.getText().matches("([0-9]*)\\.([0-9]*)")) {
			System.out.println("Real Number");
			BehaviorRealLiteral brl = aadlbaFactory.createBehaviorRealLiteral();
			brl.setValue(ctx.getText());
			return brl;
		}
		else if (ctx.getText().matches("([0-9]+)")) {
			System.out.println("Integer Number");
			BehaviorRealLiteral brl2 = aadlbaFactory.createBehaviorRealLiteral();
			brl2.setValue(ctx.getText());
			return brl2;
		}
		else if (ctx.getText().matches("([a-zA-Z0-9]*)::([a-zA-Z0-9]*)")) {
			BehaviorPropertyConstant cspr = aadlbaFactory.createBehaviorPropertyConstant();
			String[] constant = ctx.getText().split("::");
			cspr.setProperty(GetProperties.lookupPropertyConstant(ci, constant[0], constant[1]));
			return cspr;
		} else {
			BehaviorIntegerLiteral bil = aadlbaFactory.createBehaviorIntegerLiteral();
			bil.setValue(ctx.getText());
			return bil;
		}
	}

	@Override
	public IBAVisitable visitValue_variable(@NotNull FlowsParser.Value_variableContext ctx) {
		System.out.println("Value_variable : " + ctx.getText());
		ValueVariable vv = null;
		if (checkDataSubcomponentHolder(ctx.getText())) {
			vv = aadlbaFactory.createDataSubcomponentHolder();
			DataSubcomponent dsc = aadl2Factory.createDataSubcomponent();
			if (ctx.getText().contains("(0)")) {
				dsc.setName(ctx.getText().substring(0, ctx.getText().length() - 3));
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

	private boolean checkDataSubcomponentHolder(String var) {
		ECollections.sort(ci.getComponentInstances(), (o1, o2) -> {
			if(o1.getName().toString().length() < o2.getName().toString().length()){
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
