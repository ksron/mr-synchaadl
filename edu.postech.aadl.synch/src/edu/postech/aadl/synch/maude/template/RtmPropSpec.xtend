
package edu.postech.aadl.synch.maude.template

import edu.postech.aadl.xtext.propspec.propSpec.BinaryExpression
import edu.postech.aadl.xtext.propspec.propSpec.Top
import edu.postech.aadl.xtext.propspec.propSpec.Property
import edu.postech.aadl.xtext.propspec.propSpec.UnaryExpression
import edu.postech.aadl.xtext.propspec.propSpec.Value
import org.osate.aadl2.ContainedNamedElement
import org.osate.aadl2.ContainmentPathElement
import org.osate.aadl2.PropertyValue

import static extension edu.postech.aadl.synch.maude.template.RtmAadlIdentifier.*
import edu.postech.aadl.xtext.propspec.propSpec.Reachability
import edu.postech.aadl.xtext.propspec.propSpec.Invariant
import edu.postech.aadl.xtext.propspec.propSpec.Mode
import edu.postech.aadl.xtext.propspec.propSpec.ScopedValue
import org.eclipse.emf.common.util.EList
import org.osate.aadl2.PropertyExpression

class RtmPropSpec {
	
	static def compilePropertyCommand(Top top, Property prop, Mode mode)'''
	mod TEST-«top.name.toUpperCase» is
	  including «top.name.toUpperCase»-MODEL-SYMBOLIC .
	  including MODEL-TRANSITION-SYSTEM .
	  including SPECIFICATION-LANGUAGE-SEMANTICS .
	
	  op initState : -> Object .
	  eq initState = initialize(initial) .
	  
	  op prop : -> AADLExp.
	  eq prop = 
	  
    endm
    
    «IF prop instanceof Reachability»«top.compileByMode((prop as Reachability), mode)»
    «ELSEIF prop instanceof Invariant»«top.compileByMode((prop as Invariant), mode)»
    «ELSE»[Error] Invalid Property type
    «ENDIF»
    
	quit
	'''
	
	
	static def compileByMode(Top top, Reachability reach, Mode mode)'''
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  eq @m@ = ['TEST-«top.name.toUpperCase»] .
	endm
	
	search 
	      {eval(«top.name.escape+" | "+ reach.initCond.compileValueExp», initState) ||
		   initState | 0 | «reach.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object | T:Time | «reach.bound»} 
		such that
		  check-sat(B:BoolExp and eval(«top.name.escape+" | "+ reach.initCond.compileValueExp», OBJ:Object)) .
	'''
	
	static def compileByMode(Top top, Invariant inv, Mode mode)'''
    mod TEST-«top.name.toUpperCase»-MODE is
    	including TEST-«top.name.toUpperCase» .
	  eq @m@ = ['TEST-«top.name.toUpperCase»] .
	endm
	
	search 
	      {eval(«top.name.escape+" | "+ inv.initCond.compileValueExp», initState) ||
		   initState | 0 | «inv.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object | T:Time | «inv.bound»} 
		such that
		  check-sat(B:BoolExp and eval(«top.name.escape+" | not("+ inv.initCond.compileValueExp»)), OBJ:Object)) .
	'''
	
	static def getReachabilityCommand(Top top, Reachability reach)'''
	reachability : 
	(«top.name.escape+" | "+ reach.initCond.compileValueExp»)
	==>
	(«top.name.escape+" | "+ reach.goalCond.compileValueExp»)
	'''
	
	static def getRequirementCommand(Top top, Invariant inv)'''
	invariant : 
	(«top.name.escape+" | "+ inv.initCond.compileValueExp»)
	==>
	(«top.name.escape+" | not("+ inv.goalCond.compileValueExp»)
	'''
	
	
	static def compileValueExp(PropertyExpression pe){
		if(pe instanceof ScopedValue){
			'''«(pe as ScopedValue).path.compilePath» | «(pe as ScopedValue).expression.compileExp»'''
		} else if (pe instanceof BinaryExpression) {
			'''«(pe as BinaryExpression).compileExp»'''
		} else if (pe instanceof UnaryExpression) {
			'''«(pe as UnaryExpression).compileExp»'''
		} else if (pe instanceof Value){
			'''«(pe as Value).compileExp»'''
		}
	}

	/**
	 *  translate BA expressions
	 */
	private static def dispatch CharSequence compileExp(BinaryExpression e) '''
		(«e.left.compileExp» «e.op» «e.right.compileExp»)'''
		
		
	private static def dispatch CharSequence compileExp(UnaryExpression e) '''
		«e.op.translateUnaryOp»(«e.child.compileExp»)'''
	
	private static def dispatch CharSequence compileExp(ScopedValue e)'''
		(«e.path.compilePath» | ( «e.expression.compileExp» ))
	'''

	private static def dispatch CharSequence compileExp(Value e) {
		val v = e.value
		
		if(v instanceof ContainedNamedElement){
			var lastElement = (v as ContainedNamedElement).containmentPathElements.last.namedElement.name
			var path = ""
			var EList<ContainmentPathElement> ecpe = (v as ContainedNamedElement).containmentPathElements;
			for(var i = 0; i < ecpe.length-1; i++){
				if(path.length==0){
					path += ecpe.get(i).namedElement.name
				} else {
					path += " . " + ecpe.get(i).namedElement.name
				}
			}
			if(path.length==0){
				return '''c[«lastElement»]'''
			} else {
				return '''( «path» | c[«lastElement»] )'''
			}
		}else{
			return '''[[«RtmAadlProperty::compilePropertyValue(v as PropertyValue)»]]'''
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