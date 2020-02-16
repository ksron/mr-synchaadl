package edu.postech.aadl.synch.maude.template

import com.google.common.collect.SetMultimap
import edu.postech.aadl.antlr.model.ContDynamics
import edu.postech.aadl.antlr.model.ContDynamicsItem
import edu.postech.aadl.antlr.model.ContFunc
import edu.postech.aadl.antlr.model.FactorCDExpression
import edu.postech.aadl.antlr.model.SimpleCDExpression
import edu.postech.aadl.antlr.model.TermCDExpression
import org.osate.aadl2.DataPort
import org.osate.aadl2.Property
import org.osate.aadl2.PropertyValue
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager
import org.osate.ba.aadlba.AssignmentAction
import org.osate.ba.aadlba.BehaviorActionBlock
import org.osate.ba.aadlba.BehaviorActionCollection
import org.osate.ba.aadlba.BehaviorActionSequence
import org.osate.ba.aadlba.BehaviorActionSet
import org.osate.ba.aadlba.BehaviorActions
import org.osate.ba.aadlba.BehaviorBooleanLiteral
import org.osate.ba.aadlba.BehaviorIntegerLiteral
import org.osate.ba.aadlba.BehaviorPropertyConstant
import org.osate.ba.aadlba.BehaviorRealLiteral
import org.osate.ba.aadlba.BehaviorStringLiteral
import org.osate.ba.aadlba.BehaviorTransition
import org.osate.ba.aadlba.BehaviorVariableHolder
import org.osate.ba.aadlba.DataPortHolder
import org.osate.ba.aadlba.DataSubcomponentHolder
import org.osate.ba.aadlba.DispatchCondition
import org.osate.ba.aadlba.ElseStatement
import org.osate.ba.aadlba.ExecuteCondition
import org.osate.ba.aadlba.ExecutionTimeoutCatch
import org.osate.ba.aadlba.Factor
import org.osate.ba.aadlba.IfStatement
import org.osate.ba.aadlba.IndexableElement
import org.osate.ba.aadlba.Otherwise
import org.osate.ba.aadlba.ParameterHolder
import org.osate.ba.aadlba.ParameterLabel
import org.osate.ba.aadlba.PortCountValue
import org.osate.ba.aadlba.PortFreshValue
import org.osate.ba.aadlba.PortSendAction
import org.osate.ba.aadlba.PropertySetPropertyReference
import org.osate.ba.aadlba.Relation
import org.osate.ba.aadlba.SimpleExpression
import org.osate.ba.aadlba.SubprogramCallAction
import org.osate.ba.aadlba.Target
import org.osate.ba.aadlba.Term
import org.osate.ba.aadlba.UnaryAddingOperator
import org.osate.ba.aadlba.Value
import org.osate.ba.aadlba.ValueConstant
import org.osate.ba.aadlba.ValueExpression
import org.osate.ba.aadlba.ValueVariable
import org.osate.ba.aadlba.WhileOrDoUntilStatement
import edu.postech.aadl.antlr.model.ValueCDExpression
import edu.postech.aadl.antlr.model.Operator
import edu.postech.aadl.antlr.model.Variable
import edu.postech.aadl.antlr.model.Constant
import edu.postech.aadl.antlr.model.ODE

class RtmAadlBehaviorLanguage extends RtmAadlIdentifier {

	new(AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		super(errMgr, opTable)
	}
	
	
	def compileTransition(BehaviorTransition t) {
		t.check(t.priority < 0, "transition priorities are not supported" )
		'''(«t.sourceState.id("Location")» -[ «t.compileTransitionGuard» ]-> «t.destinationState.id("Location")» «t.compileTransitionAction»)''' 
	}
	
	/**************************************************************************************************************
	 * Behavior conditions
	 */
	 
	 private def compileTransitionGuard(BehaviorTransition t) {
	 	t.condition?.compileCondition ?: "[[true]]"
	}
	
	private def dispatch compileCondition(DispatchCondition dc) {
		dc.check(dc.dispatchTriggerCondition == null && ! dc.setFrozenPorts, "Unsupported dispatch conditions")
		"on dispatch"
	}
	
	private def dispatch compileCondition(ExecuteCondition cd) {
		switch cd {
			Otherwise:				"otherwise"
			ValueExpression:		compileExpression(cd)
			ExecutionTimeoutCatch:	null => [cd.check(false, "Unsupported execute conditions")]
		}
	}
	
	
	/**************************************************************************************************************
	 * Behavior actions
	 */
	 
	private def compileTransitionAction(BehaviorTransition t) {
		t.actionBlock?.compileAction ?: '''
		{
			skip
		}'''
	}
	
	private def dispatch CharSequence compileAction(BehaviorActionBlock a) {
		a.check(a.timeout == null, "timeout action not supported")
		'''
		{
			«a.content.compileAction»
		}'''
	}
		
	private def dispatch CharSequence compileAction(BehaviorActionCollection a) {
		val del = switch a { 
			BehaviorActionSequence: " ;\n"
			BehaviorActionSet: " &\n"
		}
		a.actions.map[compileAction].filterNull.join(del, "skip");
	}
	
	private def dispatch CharSequence compileAction(IfStatement a) {	
		if (a.elif) '''
			(elsif («a.logicalValueExpression.compileExpression»){
				«a.behaviorActions.compileAction»
			})
			«IF a.elseStatement instanceof IfStatement»«ELSE»)«ENDIF»
			«a.elseStatement?.compileAction»'''
		else '''
			if («a.logicalValueExpression.compileExpression»){
				«a.behaviorActions.compileAction»
			}
			«IF a.elseStatement instanceof IfStatement»(«ENDIF»
			«a.elseStatement?.compileAction»
			end if'''	
	}
	
	private def dispatch CharSequence compileAction(ElseStatement a) '''
			else {
				«a.behaviorActions.compileAction»
			}'''
				
	private def dispatch CharSequence compileAction(WhileOrDoUntilStatement a) {
		if (a.doUntil) '''
			do{
				«a.getBehaviorActions.compileAction»}
			until («a.logicalValueExpression.compileExpression»)'''
		else '''
			while («a.logicalValueExpression.compileExpression») {
				«a.behaviorActions.compileAction»
			}'''
	}
	
	private def dispatch CharSequence compileAction(AssignmentAction a) '''
		(«a.target.compileTarget» := «a.valueExpression.compileExpression»)'''
	
	private def dispatch CharSequence compileAction(SubprogramCallAction a) {
		// a.check(a.dataAccess == null , "data access for subprogram not supported")
		'''(«a.subprogram.element.qualifiedId("ClassifierId")» !«IF a.setParameterLabels» («a.parameterLabels.map[compileParameter].filterNull.join(' , ')»)«ENDIF»)'''
	}
	
	
	private def dispatch CharSequence compileAction(PortSendAction a){
		'''(«a.port.port.name.escape» !«IF a.valueExpression == null»«ELSE»(«a.valueExpression.compileExpression»)«ENDIF»)'''
	}
	
	
	private def dispatch CharSequence compileAction(BehaviorActions a) {
		a.check(false, "Unsupported action: " + a.class.name)
		null
	}
	
	
	private def compileTarget(Target t) {
		t.check(! (t instanceof IndexableElement) || !(t as IndexableElement).setArrayIndexes, "arrays not supported")
		switch t {
			BehaviorVariableHolder:	"v{" + t.behaviorVariable.name.escape +"}" 
			DataPortHolder:			"f{" + t.dataPort.name.escape + "}" 
			DataSubcomponentHolder:	"c{" + t.dataSubcomponent.name.escape + "}"
			ParameterHolder:		"p{" + t.parameter.name.escape + "}" 
			default:				null => [t.check(false, "Unsupported action reference: " + t.class.name)]
		}
	}
	
	
	private def compileParameter(ParameterLabel p) {
		switch p {
			ValueExpression: 	compileExpression(p)
			Target:				'''[[«compileTarget(p)»]]'''
		}
		
	}
	
	
	/**************************************************************************************************************
	 * Behavior expressions
	 */
	 
	private def dispatch CharSequence compileExpression(ValueVariable e) {
		switch e {
			BehaviorVariableHolder:	'''v[«e.behaviorVariable.name.escape»]'''
			DataPortHolder:			'''f[«e.dataPort.name.escape»]'''
			DataSubcomponentHolder:	'''c[«e.dataSubcomponent.name.escape»]'''
			ParameterHolder:		'''p[«e.parameter.name.escape»]'''
			PortCountValue:			'''count(«e.port.name.escape»)'''	=> [e.check(e.port instanceof DataPort, "only data port supported")]
			PortFreshValue:			'''fresh(«e.port.name.escape»)'''	=> [e.check(e.port instanceof DataPort, "only data port supported")]
			default:				null => [e.check(false, "Unsupported expression reference: " + e.class.name)]
		}
	}
	private def dispatch CharSequence compileExpression(ValueConstant e) {
		switch e {
			BehaviorBooleanLiteral:		if (e.isValue()) "[[true]]" else "[[false]]"
			BehaviorStringLiteral:		'''[["«e.value»"]]'''
			BehaviorRealLiteral:		'''[[«e.value»]]'''
			BehaviorIntegerLiteral:		'''[[«e.value»]]'''
			PropertySetPropertyReference: e.compilePropertySetPropertyReference
			BehaviorPropertyConstant:	e.compilePropertyConstant
			default:					null => [e.check(false, "Unsupported expression constant: " + e.class.name)]
		}
	}
	
	private def compilePropertySetPropertyReference(PropertySetPropertyReference c){
		val value = c.properties.get(0).property.element
		if(value instanceof Property){
			'''[[«value.getQualifiedName.escape»]]'''
		}

		else
			null => [c.check(false, "Unsupported property reference : " + c.class.name)]
	}
	
	
	private def compilePropertyConstant(BehaviorPropertyConstant c) {
		val value = c.property.constantValue
		if (value instanceof PropertyValue){
			'''[[«RtmAadlProperty::compilePropertyValue(value as PropertyValue)»]]'''
		}

		else
			null => [c.check(false, "Unsupported property constant: " + c.class.name)]
	}
	
	public def CharSequence compileCD(ContDynamics cd)'''«cd.getItems.map[compileCDItem].filterNull.join(" ; ")»'''
	
	private def CharSequence compileCDItem(ContDynamicsItem item)'''(«item.compileTarget» = «(item.expression as SimpleCDExpression).compileCDExpression»)'''
	
	private def CharSequence compileTarget(ContDynamicsItem item){
		if(item instanceof ContFunc){
			item.target.param.text.id("VarId")
		}
		switch item{
			ContFunc:		'''«item.target.variable.text»(«item.target.param.text»)'''
			ODE:			'''dt/d[«item.target.variable.text»]'''
			default:		''''''
		}
	}
	
	private def CharSequence compileCDExpression(SimpleCDExpression expr){
		var simpleExprStr = expr.opCount.countParenthesis + expr.firstExpression.compileCDExpression.toString
		if(expr.hasMultiExpr){
			for(var i = 0; i < expr.opCount; i++){
				simpleExprStr += " "+ expr.getOp(i).compileCDOperator+" "
				simpleExprStr += expr.getNextExpression(i).compileCDExpression +")"
			}
		}
		return simpleExprStr
	}
	
	private def CharSequence compileCDExpression(TermCDExpression expr){
		var termExprStr = expr.opCount.countParenthesis + expr.firstExpression.compileCDExpression.toString
		if(expr.hasMultiExpr){
			for(var i = 0; i < expr.opCount; i++){
				termExprStr += " "+expr.getOp(i).compileCDOperator+ " "
				termExprStr += expr.getNextExpression(i).compileCDExpression +")"
			}
		}
		return termExprStr
	}
	
	private def CharSequence compileCDExpression(FactorCDExpression expr){
		var factExprStr = expr.opCount.countParenthesis + expr.firstExpression.compileCDExpression.toString
		if(expr.hasMultiExpr){
			for(var i = 0; i < expr.opCount; i++){
				factExprStr += " "+expr.getOp(i).compileCDOperator+" "
				factExprStr += expr.getNextExpression(i).compileCDExpression +")"
			}
		}
		return factExprStr
	}
	
		private def CharSequence countParenthesis(int count){
		var p=""
		for(var i=0; i < count; i++){
			p+="("
		}
		return p
	}
	
	private def CharSequence compileCDExpression(ValueCDExpression expr){
		var valExprStr = ""
		if(expr.getOp(0) != null){
			valExprStr = expr.getOp(0).compileCDUnaryOperator.toString
			valExprStr += "("
		}
		var single = expr.firstExpression
		if(single instanceof SimpleCDExpression){
			valExprStr += " ( " + (single as SimpleCDExpression).compileCDExpression.toString + " ) "
		}else if(single instanceof Variable){
			valExprStr += (single as Variable).compileCDExpression.toString
		}else if(single instanceof Constant){
			valExprStr += (single as Constant).compileCDExpression.toString
		}
		if(expr.getOp(0)!= null){
			valExprStr += ")"
		}
		return valExprStr;
	}
	
	private def CharSequence compileCDExpression(Variable variable){
		switch variable.getValue{
			DataSubcomponentHolder: '''c[«variable.text»]'''
			BehaviorVariableHolder: '''v[«variable.text»]'''
			default:				'''[Unsupported]'''
		}
	}
	
	private def CharSequence compileCDExpression(Constant constant){
		switch constant.getValue {
			BehaviorRealLiteral:		'''[[«constant.text»]]'''
			BehaviorPropertyConstant:		(constant.getValue as BehaviorPropertyConstant).compilePropertyConstant
			default:						'''[[Unsupported]]'''
		}
	}
	
	private def CharSequence compileCDUnaryOperator(Operator op){
		switch op.value {
			case Operator::ADD:			"plus"
			case Operator::MINUS:		"minus"
			default:					""
		}
	}
	
	private def CharSequence compileCDOperator(Operator op){
		switch op.value {
			case Operator::ADD:			"+"
			case Operator::MINUS:		"-"
			case Operator::MULTIPLY:	"*"
			case Operator::DIVIDE:		"/"
			case Operator::POWER:		"**"
		}
	}
	
	private def dispatch CharSequence compileExpression(ValueExpression e) {
		val itRel = e.relations.iterator
		var result = itRel.next.compileExpression
		if (e.setLogicalOperators) {
			val itOp = e.logicalOperators.iterator
			while (itRel.hasNext)
				result = '''(«result» «itOp.next.literal» «itRel.next.compileExpression»)'''
		}
		return result
	}
	
	
	private def dispatch CharSequence compileExpression(Relation e) {
		if (e.secondExpression == null){
			e.firstExpression.compileExpression
		}
		else{
			'''(«e.firstExpression.compileExpression» «e.relationalOperator.literal» «e.secondExpression.compileExpression»)'''
		}
			
	}
	
	private def dispatch CharSequence compileExpression(SimpleExpression e) {
		val itTerm = e.terms.iterator 
		var result = itTerm.next.compileExpression
		if (e.setBinaryAddingOperators) {
			val itOp = e.binaryAddingOperators.iterator
			while (itTerm.hasNext)
				result = '''(«result» «itOp.next.literal» «itTerm.next.compileExpression»)'''
		}
		return if (e.setUnaryAddingOperator) '''«e.unaryAddingOperator.compileUnaryAddingOperator»(«result»)''' else result
	}
	
	private def compileUnaryAddingOperator(UnaryAddingOperator u) {
		switch u.value {
			case UnaryAddingOperator::NONE_VALUE:	""
			case UnaryAddingOperator::PLUS_VALUE:	"plus"
			case UnaryAddingOperator::MINUS_VALUE:	"minus"
		}
	}
	
	private def dispatch CharSequence compileExpression(Term e) {
		val itFact = e.factors.iterator
		var result = itFact.next.compileExpression
		if (e.setMultiplyingOperators) {
			val itOp = e.multiplyingOperators.iterator
			while (itFact.hasNext)
				result = '''(«result» «itOp.next.literal» «itFact.next.compileExpression»)'''
		}
		return result
	}
	private def dispatch CharSequence compileExpression(Factor e) {
		if (e.setUnaryNumericOperator) '''
			«e.unaryNumericOperator.literal»(«e.firstValue.compileExpression»)'''
		else if (e.setUnaryBooleanOperator) '''
			«e.unaryBooleanOperator.literal»(«e.firstValue.compileExpression»)'''
		else if (e.setBinaryNumericOperator) '''
			(«e.firstValue.compileExpression» «e.binaryNumericOperator.literal» «e.secondValue.compileExpression»)'''
		else
			e.firstValue.compileExpression
	}
	
	
	private def dispatch CharSequence compileExpression(Value e) {
		e.check(false, "Unsupported expression: " + e.class.name)
		null
	}
			
}