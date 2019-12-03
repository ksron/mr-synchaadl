
package edu.postech.aadl.synch.maude.template

import edu.postech.aadl.xtext.propspec.propSpec.BinaryExpression
import edu.postech.aadl.xtext.propspec.propSpec.BinaryFormula
import edu.postech.aadl.xtext.propspec.propSpec.PropRef
import edu.postech.aadl.xtext.propspec.propSpec.StateProp
import edu.postech.aadl.xtext.propspec.propSpec.Top
import edu.postech.aadl.xtext.propspec.propSpec.UnaryExpression
import edu.postech.aadl.xtext.propspec.propSpec.UnaryFormula
import edu.postech.aadl.xtext.propspec.propSpec.Value
import edu.postech.aadl.xtext.propspec.propSpec.ValueProp
import org.osate.aadl2.ContainedNamedElement
import org.osate.aadl2.ContainmentPathElement
import org.osate.aadl2.PropertyValue

import static extension edu.postech.aadl.synch.maude.template.RtmAadlIdentifier.*
import edu.postech.aadl.xtext.propspec.propSpec.Prop
import org.osate.aadl2.NamedElement
import edu.postech.aadl.xtext.propspec.propSpec.Reachability
import edu.postech.aadl.xtext.propspec.propSpec.Invariant
import edu.postech.aadl.xtext.propspec.propSpec.SYMBOLIC
import edu.postech.aadl.xtext.propspec.propSpec.Mode
import edu.postech.aadl.xtext.propspec.propSpec.RANDOM
import edu.postech.aadl.xtext.propspec.propSpec.DISTRIBUTED
import edu.postech.aadl.xtext.propspec.propSpec.MODELCHECK

class RtmPropSpec {
	
	static def compileReachabilityCommand(Top top, Reachability reach, Mode mode)'''
	mod TEST-«top.name.toUpperCase» is
	  including «top.name.toUpperCase»-MODEL-SYMBOLIC .
	  including SPECIFICATION-LANGUAGE-SEMANTICS .
	
	  op initState : -> Object .
	  eq initState = initialize(initial) .
	  
    endm
    
    «top.compileByMode(reach, mode)»
    
	quit
	'''
	
	static def compileByMode(Top top, Reachability reach ,Mode mode)'''
	«IF mode == null»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
		  
	  eq @m@ = ['TEST-«top.name.toUpperCase»] . 
	  eq MFlag = true .
	endm
	
	search 
	      {eval(«top.name.escape+" . "+(reach.initCond as  Prop).path.compilePath» | «(reach.initCond as ValueProp).expression.compileExp», initState) ||
		   initState,0,«reach.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object,T:Time,«reach.bound»} 
		such that
		  check-sat(B:BoolExp and eval(«top.name.escape+" . "+(reach.goalCond as Prop).path.compilePath» | «(reach.goalCond as ValueProp).expression.compileExp», OBJ:Object)) .
	«ELSEIF mode instanceof RANDOM»
	search 
		{{initState, «(mode as RANDOM).seed»}, 0, «reach.bound»} =>+
	 	{{OBJ:Object, N:Nat}, T:Time, «reach.bound»} such that check-true(eval(«top.name.escape+" . "+(reach.goalCond as Prop).path.compilePath» | «(reach.goalCond as ValueProp).expression.compileExp», OBJ:Object)) .
	«ELSEIF mode instanceof DISTRIBUTED»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  
	  var TS : Set{InterTiming} .
	  eq timing(TS) = interTimes(TS, «(mode as DISTRIBUTED).degree») .
	endm
	
	search 
		{initState , 0 , «reach.bound»} =>* 
		{none , T:Time , «reach.bound»} .
	«ELSEIF mode instanceof MODELCHECK»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  
	  eq @m@ = ['TEST-«top.name.toUpperCase»] . 
	  «IF (mode as MODELCHECK).getOption.equals("-merge")»eq MFlag = true .«ELSE»eq MFlag = false .«ENDIF»
	endm
	
	mod TEST-«top.name.toUpperCase»-MODEL-CHECK is
		including TEST-«top.name.toUpperCase»-MODE .
	    including MODEL-CHECKER .
	    including LTL-SIMPLIFIER .
	    
        op modelState : -> State .
        subsort Configuration < State .
        eq modelState = initState .
    
        op initFormula : -> Prop .
        op goalFormula : -> Prop .
    
        var C : Configuration .
    
        eq C |= initFormula = check-sat(eval(«top.name.escape+" . "+(reach.initCond as  Prop).path.compilePath» | «(reach.initCond as ValueProp).expression.compileExp», C)) .
        eq C |= goalFormula = check-sat(eval(«top.name.escape+" . "+(reach.goalCond as  Prop).path.compilePath» | «(reach.goalCond as ValueProp).expression.compileExp», C)) .
    endm
    
    red modelCheck(modelState, initFormula -> <> goalFormula) .
	«ELSEIF mode instanceof SYMBOLIC»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  
	  eq @m@ = ['TEST-«top.name.toUpperCase»] . 
	  «IF (mode as SYMBOLIC).getOption.equals("-merge")»eq MFlag = true .«ELSE»eq MFlag = false .«ENDIF»
	endm
	
	search 
	      {eval(«top.name.escape+" . "+(reach.initCond as  Prop).path.compilePath» | «(reach.initCond as ValueProp).expression.compileExp», initState) ||
		   initState,0,«reach.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object,T:Time,«reach.bound»} 
		such that
		  check-sat(B:BoolExp and eval(«top.name.escape+" . "+(reach.goalCond as Prop).path.compilePath» | «(reach.goalCond as ValueProp).expression.compileExp», OBJ:Object)) .
	«ENDIF»
	'''
	
	static def getReachabilityCommand(Top top, Reachability reach)'''
	reachability : 
	(«top.name.escape+" . "+(reach.initCond as Prop).path.compilePath» | «(reach.initCond as ValueProp).expression.compileExpForUser»)
	==>
	(«top.name.escape+" . "+(reach.goalCond as Prop).path.compilePath» | «(reach.goalCond as ValueProp).expression.compileExpForUser»)
	'''
	
	// invariant
	static def compileRequirementCommand(Top top, Invariant inv, Mode mode)'''
	mod TEST-«top.name.toUpperCase» is
	  including «top.name.toUpperCase»-MODEL-SYMBOLIC .
	  including SPECIFICATION-LANGUAGE-SEMANTICS .
	
	  op initState : -> Object .
	  eq initState = initialize(initial) .
	  
    endm
    
    «top.compileByMode(inv, mode)»
    
	quit
	'''
	
	static def compileByMode(Top top, Invariant inv ,Mode mode)'''
	«IF mode == null»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
		  
	  eq @m@ = ['TEST-«top.name.toUpperCase»] . 
	  eq MFlag = true .
	endm
	
	search 
	      {eval(«top.name.escape+" . "+(inv.initCond as  Prop).path.compilePath» | «(inv.initCond as ValueProp).expression.compileExp», initState) ||
		   initState,0,«inv.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object,T:Time,«inv.bound»} 
		such that
		  check-sat(B:BoolExp and eval(«top.name.escape+" . "+(inv.goalCond as Prop).path.compilePath» | not ( «(inv.goalCond as ValueProp).expression.compileExp» ), OBJ:Object)) .
	«ELSEIF mode instanceof RANDOM»
	search 
		{{initState, «(mode as RANDOM).seed»}, 0, «inv.bound»} =>+
	 	{{OBJ:Object, N:Nat}, T:Time, «inv.bound»} such that check-true(eval(«top.name.escape+" . "+(inv.goalCond as Prop).path.compilePath» | not ( «(inv.goalCond as ValueProp).expression.compileExp» ), OBJ:Object)) .
	«ELSEIF mode instanceof DISTRIBUTED»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  
	  var TS : Set{InterTiming} .
	  eq timing(TS) = interTimes(TS, «(mode as DISTRIBUTED).degree») .
	endm
	
	search 
		{initState , 0 , «inv.bound»} =>* 
		{none , T:Time , «inv.bound»} .
	«ELSEIF mode instanceof MODELCHECK»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  
	  eq @m@ = ['TEST-«top.name.toUpperCase»] . 
	  «IF (mode as MODELCHECK).getOption.equals("-merge")»eq MFlag = true .«ELSE»eq MFlag = false .«ENDIF»
	endm
	
	mod TEST-«top.name.toUpperCase»-MODEL-CHECK is
		including TEST-«top.name.toUpperCase»-MODE .
	    including MODEL-CHECKER .
	    including LTL-SIMPLIFIER .
	    
        op modelState : -> State .
        subsort Configuration < State .
        eq modelState = initState .
    
        op initFormula : -> Prop .
        op goalFormula : -> Prop .
    
        var C : Configuration .
    
        eq C |= initFormula = check-sat(eval(«top.name.escape+" . "+(inv.initCond as  Prop).path.compilePath» | «(inv.initCond as ValueProp).expression.compileExp», C)) .
        eq C |= goalFormula = check-sat(eval(«top.name.escape+" . "+(inv.goalCond as  Prop).path.compilePath» | «(inv.goalCond as ValueProp).expression.compileExp», C)) .
    endm
    
    red modelCheck(modelState, initFormula -> [] goalFormula) .
	«ELSEIF mode instanceof SYMBOLIC»
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  
	  eq @m@ = ['TEST-«top.name.toUpperCase»] . 
	  «IF (mode as SYMBOLIC).getOption.equals("-merge")»eq MFlag = true .«ELSE»eq MFlag = false .«ENDIF»
	endm
	
	search 
	      {eval(«top.name.escape+" . "+(inv.initCond as  Prop).path.compilePath» | «(inv.initCond as ValueProp).expression.compileExp», initState) ||
		   initState,0,«inv.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object,T:Time,«inv.bound»} 
		such that
		  check-sat(B:BoolExp and eval(«top.name.escape+" . "+(inv.goalCond as Prop).path.compilePath» | not( «(inv.goalCond as ValueProp).expression.compileExp» ), OBJ:Object)) .
	«ENDIF»
	'''
	
	static def getRequirementCommand(Top top, Invariant inv)'''
	invariant : 
	(«top.name.escape+" . "+(inv.initCond as Prop).path.compilePath» | «(inv.initCond as ValueProp).expression.compileExp»)
	==>
	(«top.name.escape+" . "+(inv.goalCond as Prop).path.compilePath» | «(inv.goalCond as ValueProp).expression.compileExp»)
	'''
	
	/**
	 *  translate LTL formulas
	 */
	 
	 static def compileRewCommand(int bound) '''
	 	rew {initial} .
	 '''
	 
	private static def dispatch CharSequence compileFormula(BinaryFormula f) 
	'''(«f.left.compileFormula» «f.op» «f.right.compileFormula»)'''
			
	
	private static def dispatch CharSequence compileFormula(UnaryFormula f) {
		if (f.child == null) f.op else '''(«f.op» «f.child.compileFormula»)'''
	}
			
	// private static def dispatch CharSequence compileFormula(PropRef pr) 
	// '''«pr.def.name»'''
	
	
	private static def dispatch CharSequence compileFormula(StateProp prop) 
	'''«prop.path.compilePath» @ «prop.state»'''
	
	
	private static def dispatch CharSequence compileFormula(ValueProp prop) 
	'''«prop.path.compilePath» | «prop.expression.compileExp»'''
	 
	/*
	 * get Raw user reachability / requirement
	 */ 
	 
	private static def dispatch CharSequence compileExpForUser(BinaryExpression e) '''
		«e.left.compileExpForUser» «e.op» «e.right.compileExpForUser»'''
		
		
	private static def dispatch CharSequence compileExpForUser(UnaryExpression e) '''
		«e.op.translateUnaryOpForUser»«e.child.compileExpForUser»'''
	
	
	private static def dispatch CharSequence compileExpForUser(Value e) {
		val v = e.value
		switch v {
			PropertyValue:			'''«RtmAadlProperty::compilePropertyValue(v)»'''
			ContainmentPathElement:	'''«v.namedElement.name.escape»'''
		}
	}
	
	private static def translateUnaryOpForUser(String op) {
		switch op {
			case "+":	"+"
			case "-":	"-"
			default:	op 
		}
	}
	
	  
	/**
	 *  translate BA expressions
	 */
	private static def dispatch CharSequence compileExp(BinaryExpression e) '''
		(«e.left.compileExp» «e.op» «e.right.compileExp»)'''
		
		
	private static def dispatch CharSequence compileExp(UnaryExpression e) '''
		«e.op.translateUnaryOp»(«e.child.compileExp»)'''
	
	
	private static def dispatch CharSequence compileExp(Value e) {
		val v = e.value
		switch v {
			PropertyValue:			'''[[«RtmAadlProperty::compilePropertyValue(v)»]]'''
			ContainmentPathElement:	'''c[«v.namedElement.name.escape»]'''
		}
	}
	
	private static def translateUnaryOp(String op) {
		switch op {
			case "+":	"plus"
			case "-":	"minus"
			default:	op
		}
	}
	
	// an component path
	private static def CharSequence compilePath(ContainedNamedElement path) {
		path.containmentPathElements.map[namedElement.name.escape].join(' . ')
	}
}