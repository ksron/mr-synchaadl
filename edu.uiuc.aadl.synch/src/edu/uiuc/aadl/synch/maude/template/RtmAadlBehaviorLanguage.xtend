package edu.uiuc.aadl.synch.maude.template

import com.google.common.collect.SetMultimap
import fr.tpt.aadl.annex.behavior.aadlba.AssignmentAction
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionBlock
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionCollection
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionSequence
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActionSet
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorActions
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorBooleanLiteral
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorIntegerLiteral
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorPropertyConstant
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorPropertyValue
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorRealLiteral
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorStringLiteral
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorTransition
import fr.tpt.aadl.annex.behavior.aadlba.BehaviorVariableHolder
import fr.tpt.aadl.annex.behavior.aadlba.DataPortHolder
import fr.tpt.aadl.annex.behavior.aadlba.DataSubcomponentHolder
import fr.tpt.aadl.annex.behavior.aadlba.DispatchCondition
import fr.tpt.aadl.annex.behavior.aadlba.ElseStatement
import fr.tpt.aadl.annex.behavior.aadlba.ExecuteCondition
import fr.tpt.aadl.annex.behavior.aadlba.ExecutionTimeoutCatch
import fr.tpt.aadl.annex.behavior.aadlba.Factor
import fr.tpt.aadl.annex.behavior.aadlba.IfStatement
import fr.tpt.aadl.annex.behavior.aadlba.IndexableElement
import fr.tpt.aadl.annex.behavior.aadlba.Otherwise
import fr.tpt.aadl.annex.behavior.aadlba.ParameterHolder
import fr.tpt.aadl.annex.behavior.aadlba.ParameterLabel
import fr.tpt.aadl.annex.behavior.aadlba.PortCountValue
import fr.tpt.aadl.annex.behavior.aadlba.PortFreshValue
import fr.tpt.aadl.annex.behavior.aadlba.Relation
import fr.tpt.aadl.annex.behavior.aadlba.SimpleExpression
import fr.tpt.aadl.annex.behavior.aadlba.SubprogramCallAction
import fr.tpt.aadl.annex.behavior.aadlba.Target
import fr.tpt.aadl.annex.behavior.aadlba.Term
import fr.tpt.aadl.annex.behavior.aadlba.UnaryAddingOperator
import fr.tpt.aadl.annex.behavior.aadlba.Value
import fr.tpt.aadl.annex.behavior.aadlba.ValueConstant
import fr.tpt.aadl.annex.behavior.aadlba.ValueExpression
import fr.tpt.aadl.annex.behavior.aadlba.ValueVariable
import fr.tpt.aadl.annex.behavior.aadlba.WhileOrDoUntilStatement
import org.osate.aadl2.DataPort
import org.osate.aadl2.PropertyValue
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager

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
	 	t.condition?.compileCondition ?: "[true]"
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
			elsif («a.logicalValueExpression.compileExpression»)
				«a.behaviorActions.compileAction»
			«a.elseStatement?.compileAction»'''
		else '''
			if («a.logicalValueExpression.compileExpression»)
				«a.behaviorActions.compileAction»
			«a.elseStatement?.compileAction»
			end if'''
	}
	
	private def dispatch CharSequence compileAction(ElseStatement a) '''
			else
				«a.behaviorActions.compileAction»'''
				
	private def dispatch CharSequence compileAction(WhileOrDoUntilStatement a) {
		if (a.doUntil) '''
			do
				«a.getBehaviorActions.compileAction»
			until («a.logicalValueExpression.compileExpression»)'''
		else '''
			while («a.logicalValueExpression.compileExpression») {
				«a.behaviorActions.compileAction»
			}'''
	}
	
	private def dispatch CharSequence compileAction(AssignmentAction a) '''
		({«a.target.compileTarget»} := «a.valueExpression.compileExpression»)'''
	
	private def dispatch CharSequence compileAction(SubprogramCallAction a) {
		a.check(a.dataAccess == null , "data access for subprogram not supported")
		'''(«a.subprogram.element.qualifiedId("ClassifierId")» !«IF a.setParameterLabels» («a.parameterLabels.map[compileParameter].filterNull.join(' , ')»)«ENDIF»)'''
	}
	
	private def dispatch CharSequence compileAction(BehaviorActions a) {
		a.check(false, "Unsupported action: " + a.class.name)
		null
	}
	
	
	private def compileTarget(Target t) {
		t.check(! (t instanceof IndexableElement) || !(t as IndexableElement).setArrayIndexes, "arrays not supported")
		switch t {
			BehaviorVariableHolder:	t.behaviorVariable.name.escape
			DataPortHolder:			t.dataPort.name.escape
			DataSubcomponentHolder:	t.dataSubcomponent.name.escape
			ParameterHolder:		t.parameter.name.escape
			default:				null => [t.check(false, "Unsupported action reference: " + t.class.name)]
		}
	}
	
	
	private def compileParameter(ParameterLabel p) {
		switch p {
			ValueExpression: 	compileExpression(p)
			Target:				'''[«compileTarget(p)»]'''
		}
		
	}
	
	
	/**************************************************************************************************************
	 * Behavior expressions
	 */
	 
	private def dispatch CharSequence compileExpression(ValueVariable e) {
		switch e {
			BehaviorVariableHolder:	'''[«e.behaviorVariable.name.escape»]'''
			DataPortHolder:			'''[«e.dataPort.name.escape»]'''
			DataSubcomponentHolder:	'''[«e.dataSubcomponent.name.escape»]'''
			ParameterHolder:		'''[«e.parameter.name.escape»]'''
			PortCountValue:			'''count(«e.port.name.escape»)'''	=> [e.check(e.port instanceof DataPort, "only data port supported")]
			PortFreshValue:			'''fresh(«e.port.name.escape»)'''	=> [e.check(e.port instanceof DataPort, "only data port supported")]
			default:				null => [e.check(false, "Unsupported expression reference: " + e.class.name)]
		}
	}


	private def dispatch CharSequence compileExpression(ValueConstant e) {
		switch e {
			BehaviorBooleanLiteral:		if (e.value) "[true]" else "[false]"
			BehaviorStringLiteral:		'''["«e.value»"]'''
			BehaviorRealLiteral:		'''[«e.value»]'''
			BehaviorIntegerLiteral:		'''[«e.value»]'''
			BehaviorPropertyValue: 		'''[«e.property.qualifiedName.escape»]'''
			BehaviorPropertyConstant:	e.compilePropertyConstant
			default:					null => [e.check(false, "Unsupported expression constant: " + e.class.name)]
		}
	}
	
	private def compilePropertyConstant(BehaviorPropertyConstant c) {
		val value = c.property.constantValue
		if (value instanceof PropertyValue)
			'''[«RtmAadlProperty.compilePropertyValue(value as PropertyValue)»]'''
		else
			null => [c.check(false, "Unsupported property constant: " + c.class.name)]
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
		if (e.secondExpression == null)
			e.firstExpression.compileExpression
		else
			'''(«e.firstExpression.compileExpression» «e.relationalOperator.literal» «e.secondExpression.compileExpression»)'''
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
			case UnaryAddingOperator.NONE_VALUE:	""
			case UnaryAddingOperator.PLUS_VALUE:	"plus"
			case UnaryAddingOperator.MINUS_VALUE:	"minus"
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