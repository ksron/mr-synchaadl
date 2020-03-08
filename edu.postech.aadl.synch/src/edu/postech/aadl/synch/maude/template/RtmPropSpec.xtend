
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
import edu.postech.aadl.xtext.propspec.propSpec.ScopedExpression
import org.eclipse.emf.common.util.EList
import edu.postech.aadl.xtext.propspec.propSpec.Proposition
import edu.postech.aadl.xtext.propspec.propSpec.PropRef
import edu.postech.aadl.xtext.propspec.propSpec.SYMBOLIC
import edu.postech.aadl.xtext.propspec.propSpec.MODELCHECK
import edu.postech.aadl.xtext.propspec.propSpec.RANDOM
import edu.postech.aadl.xtext.propspec.propSpec.DISTRIBUTED
import org.osate.ba.utils.AadlBaUtils
import org.osate.aadl2.NamedElement
import org.osate.aadl2.DataPort
import org.osate.aadl2.DataSubcomponent
import org.osate.ba.aadlba.BehaviorVariable
import org.osate.ba.aadlba.ParameterHolder

class RtmPropSpec {

	static def compilePropertyCommand(Top top, Property prop, Mode mode, String maudeDirPath)'''
	«top.compileLoadFiles(mode, maudeDirPath)»
	mod TEST-«top.name.toUpperCase» is
	  including «top.name.toUpperCase»-MODEL-SYMBOLIC .
	  including «mode.compileModelTransition» .
	  including SPECIFICATION-LANGUAGE-SEMANTICS .
  	  including COUNTEREXAMPLE-INTERFACE .
  	  including META-LEVEL .
	  including CONVERSION .
	
	  op initState : -> Object .
	  eq initState = initialize(collapse(initial)) .
	  
	«top.compileProp»
  	«top.compileMode»
	endm
		  
	«top.compileVerboseProperty(prop, mode)»
	quit
	'''
	
	static def compileLoadFiles(Top top, Mode mode, String maudeDirPath)'''
	load «maudeDirPath»/prelude.maude
	«IF mode == null || (mode != null && mode instanceof SYMBOLIC)»
	load «maudeDirPath»/smt.maude
	«ENDIF»
	load semantics/«mode.compileInterpreter».maude
	load «top.name».maude
	'''
	
	static def compileInterpreter(Mode mode){
		switch (mode){
			DISTRIBUTED:		'''interpreter-distributed'''
			RANDOM:				'''interpreter-random'''
			SYMBOLIC:			'''interpreter-«mode.name»«mode.option»'''
			default:			'''interpreter-symbolic2-merge'''
		}
	}
	
	static def compileModelTransition(Mode mode){
		switch (mode){
			RANDOM:			'''RANDOM-SIMULATION'''
			default:		'''MODEL-TRANSITION-SYSTEM'''
		}
	}
	
	static def compileProp(Top top)'''
	«FOR Proposition prop : top.getProposition»
	    op «prop.name» : -> PropSpec .
		eq «prop.name» = «prop.expression.compileExp» .
	«ENDFOR»
	'''
	
	static def compileMode(Top top){
		val mode = top.mode;
		if(mode == null || (mode != null && mode instanceof SYMBOLIC && (mode as SYMBOLIC).option != null)){
			'''eq @m@ = ['«top.name.toUpperCase»-MODEL-SYMBOLIC] .'''
		}else if(mode instanceof DISTRIBUTED){
			'''eq #sample = «mode.sample» . eq #response = «mode.response» .'''
		}
	}
	
	static def compileVerboseProperty(Top top, Property prop, Mode mode){
		var verbose = ""
		if(mode!= null && mode.verbose!=null){
			verbose += "set show timing off .\n"
			verbose += "set show stats off .\n"
			verbose += "set show command off .\n"
			verbose += "red \"time-bound: \" + " + "string(" + prop.bound + ", 10) .\n"
			if(mode instanceof DISTRIBUTED){
				verbose += "red \"sample: \" + " + "string(" + mode.sample + ", 10) .\n"
				verbose += "red \"response: \" + " + "string(" + mode.response + ", 10) .\n"
			}
			verbose += "set show timing on .\n"
			verbose += "set show stats on .\n"
			verbose += "set show command on .\n\n"
			
			verbose += "set verbose on .\n"
			verbose += "set print attribute on .\n"
			verbose += top.compileProperty(prop, mode)
			verbose += "set verbose off .\n"
			verbose += "set print attribute off .\n\n"
		} else {
			verbose += top.compileProperty(prop, mode)
		}
		return verbose
	}
	
	static def dispatch compileProperty(Top top, Property prop, Void mode)'''
	red counterexample(metaSearchPath(upModule('TEST-«top.name.toUpperCase», false), 
			upTerm({«top.compileInitConst(prop)» || initState | 0 | «prop.bound»}), 
			upTerm({B:BoolExp || OBJ:Object | T:Time | «prop.bound»}),
			upTerm(check-sat(B:BoolExp and finalConst(OBJ:Object) and «top.compileGoalConst(prop)»)) = upTerm((true).Bool),
			'*,
			unbounded,
			1)) .
	'''
	
	static def dispatch compileProperty(Top top, Property prop, SYMBOLIC mode)'''
	red counterexample(metaSearchPath(upModule('TEST-«top.name.toUpperCase», false), 
			upTerm({«top.compileInitConst(prop)» || initState | 0 | «prop.bound»}), 
			upTerm({B:BoolExp || OBJ:Object | T:Time | «prop.bound»}),
			upTerm(check-sat(B:BoolExp and finalConst(OBJ:Object) and «top.compileGoalConst(prop)»)) = upTerm((true).Bool),
			'*,
			unbounded,
			1)) .
	'''

	static def dispatch compileProperty(Top top, Property prop, RANDOM mode)'''
	red repeat({{initState, 0} | 0 | «prop.bound»}, «top.compileGoalConst(prop)») .
	'''
	
	static def dispatch compileProperty(Top top, Property prop, DISTRIBUTED mode)'''
	search [1]
	      {initState | 0 | «prop.bound»} 
		=>*
		  {OBJ:Object | T:Time | «prop.bound»} 
		such that
		  check-true(«top.compileGoalConst(prop)») .
	'''
	

	
	static def compileInitConst(Top top, Property pr)'''
	eval(«top.name.escape» | «pr.initCond == null ? "[[true]]" : pr.initCond.compileExp», initState)
	'''
	
	static def compileGoalConst(Top top, Property pr){
		switch(pr){
			Invariant:			'''eval(«top.name.escape» | not(«pr.goalCond.compileExp»), OBJ:Object)'''
			Reachability:		'''eval(«top.name.escape» | «pr.goalCond.compileExp», OBJ:Object)'''
			default:			''''''
		}
	}

	/**
	 *  translate BA expressions
	 */
	private static def dispatch CharSequence compileExp(BinaryExpression e) '''
		(«e.left.compileExp» «e.op» «e.right.compileExp»)'''
		
	private static def dispatch CharSequence compileExp(UnaryExpression e) '''
		«e.op.translateUnaryOp»(«e.child.compileExp»)'''
	
	private static def dispatch CharSequence compileExp(ScopedExpression e)'''
		(«e.path.compilePath» | ( «e.expression.compileExp» ))'''

	private static def dispatch CharSequence compileExp(PropRef e)'''
		«e.def.name»'''

	private static def dispatch CharSequence compileExp(Value e) {
		var v = e.value
		switch v {
			ContainedNamedElement:		'''(«IF v.containmentPathElements.length > 1»«v.compilePath» | «ENDIF»«v.containmentPathElements.last.namedElement.compilePrefix» )'''
			default:					'''[[«RtmAadlProperty::compilePropertyValue(v as PropertyValue)»]]'''
		}
	}
	
	private static def compilePrefix(NamedElement ne){
		switch ne {
			DataPort:					'''f[«ne.name»]'''
			DataSubcomponent:			'''c[«ne.name»]'''
			BehaviorVariable:			'''v[«ne.name»]'''
			ParameterHolder:			'''p[«ne.name»]'''
			default:					null
		}
	}
	
	private static def translateUnaryOp(String op) {
		switch op {
			case "+":	"plus"
			case "-":	"minus"
			default:	op
		}
	}
	
	// an component path except component
	private static def CharSequence compilePath(ContainedNamedElement path) {
		path.containmentPathElements.subList(0, path.containmentPathElements.length-1).map[namedElement.name.escape].join(' . ')
	}
}