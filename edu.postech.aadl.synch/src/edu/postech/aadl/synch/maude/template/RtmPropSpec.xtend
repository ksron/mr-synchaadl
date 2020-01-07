
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
	  including «mode.compileModelTrans» .
	  including SPECIFICATION-LANGUAGE-SEMANTICS .
	  including CONVERSION .
	
	  op initState : -> Object .
	  eq initState = initialize(collapse(initial)) .
	  
	«top.compileProp»
  	«top.compileMode»
	endm
		  
	«top.compileRequirement(prop, mode)»
	quit
	'''
	
	// For experiment in ubuntu, maudeDirPath variable isn't used.
	static def compileLoadFiles(Top top, Mode mode, String maudeDirPath)'''
	load /home/jaehun/maude-z3-ubuntu/prelude.maude
	«IF mode == null || (mode != null && mode instanceof SYMBOLIC)»
	load /home/jaehun/maude-z3-ubuntu/smt.maude
	load /home/jaehun/maude-z3-ubuntu/smtCheck.maude
	«ENDIF»
	load ./semantics/«mode.compileInterpreter»
	load «top.name».maude
	'''
	
	static def compileRequirement(Top top, Property prop, Mode mode){
		var verbose = ""
		if(mode.verbose!=null){
			verbose += "set show timing off .\n"
			verbose += "set show stats off .\n"
			verbose += "set show command off .\n"
			if(prop != null){
				verbose += "red \"time-bound: \" + " + "string(" + prop.bound + ", 10) .\n"
			}
			if(mode != null && mode instanceof DISTRIBUTED){
				verbose += "red \"sample: \" + " + "string(" + (mode as DISTRIBUTED).sample + ", 10) .\n"
				verbose += "red \"response: \" + " + "string(" + (mode as DISTRIBUTED).response + ", 10) .\n"
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
	
	static def compileModelTrans(Mode mode){
		if(mode != null && mode instanceof RANDOM){
			return "RANDOM-SIMULATION"
		}
		return "MODEL-TRANSITION-SYSTEM"
	}
	
	static def compileProperty(Top top, Property prop, Mode mode){
		if (prop instanceof Reachability){
			if(mode == null){
				top.compileProperty(prop as Reachability)
			}else if(mode instanceof SYMBOLIC){
				top.compileProperty(prop as Reachability, mode as SYMBOLIC)
			} else if(mode instanceof RANDOM){
				top.compileProperty(prop as Reachability, mode as RANDOM)
			} else if(mode instanceof DISTRIBUTED){
				top.compileProperty(prop as Reachability, mode as DISTRIBUTED)
			}
		}else{
			if(mode == null){
				top.compileProperty(prop as Invariant)
			}else if(mode instanceof SYMBOLIC){
				top.compileProperty(prop as Invariant, mode as SYMBOLIC)
			} else if(mode instanceof RANDOM){
				top.compileProperty(prop as Invariant, mode as RANDOM)
			} else if(mode instanceof DISTRIBUTED){
				top.compileProperty(prop as Invariant, mode as DISTRIBUTED)
			}
		}
	}
	
	static def compileInterpreter(Mode mode){
		if(mode == null){
			return "interpreter-symbolic.maude"
		} else if (mode instanceof RANDOM){
			return "interpreter-random.maude"
		} else if (mode instanceof DISTRIBUTED){
			return "interpreter-distributed.maude"
		} else if (mode instanceof SYMBOLIC && (mode as SYMBOLIC).option != null){
			return "interpreter-symbolic-merge.maude"
		} else {
			return "interpreter-symbolic.maude"
		}
	}
	
	static def compileMode(Top top){
		val mode = top.mode;
		if((mode instanceof SYMBOLIC && (mode as SYMBOLIC).option != null) ||
			(mode instanceof MODELCHECK && (mode as MODELCHECK).option != null)){
			'''eq @m@ = ['«top.name.toUpperCase»-MODEL-SYMBOLIC] .'''
		} else if(mode instanceof DISTRIBUTED){
		'''  eq #sample = «mode.sample» .«"\n"»eq #response = «mode.response» .'''
		} else{
			''''''
		}
	}
	
	static def compileProp(Top top){
		var result=""
		for(Proposition prop : top.getProposition){
			result += "op " + prop.name + " : -> PropSpec ." + "\n"
			result += "eq " + prop.name + " = " + top.name.escape + " | " + prop.expression.compileExp + " ." + "\n"
			result += "\n"
		}
		return result
	}
	
	static def compileProperty(Top top, Reachability reach)'''
	search [1]
	      {«top.compileInitConst(reach)» ||
		   initState | 0 | «reach.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object | T:Time | «reach.bound»} 
		such that
		  check-sat(B:BoolExp and «top.compileGoalConst(reach)») .
	'''
	
	static def compileProperty(Top top, Invariant inv)'''
	search [1]
	      {«top.compileInitConst(inv)» ||
		   initState | 0 | «inv.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object | T:Time | «inv.bound»} 
		such that
		  check-sat(B:BoolExp and not(«top.compileGoalConst(inv)»)) .
	'''
	
	static def compileProperty(Top top, Reachability reach, SYMBOLIC mode)'''
	search [1]
	      {«top.compileInitConst(reach)» ||
		   initState | 0 | «reach.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object | T:Time | «reach.bound»} 
		such that
		  check-sat(B:BoolExp and «top.compileGoalConst(reach)») .
	'''
	
	static def compileProperty(Top top, Invariant inv, SYMBOLIC mode)'''
	search [1]
	      {«top.compileInitConst(inv)» ||
		   initState | 0 | «inv.bound»} 
		=>*
		  {B:BoolExp || OBJ:Object | T:Time | «inv.bound»} 
		such that
		  check-sat(B:BoolExp and not(«top.compileGoalConst(inv)»)) .
	'''

	static def compileProperty(Top top, Reachability reach, RANDOM mode)'''
	red repeat({{initState, 0} | 0 | «reach.bound»}, «top.compileGoalConst(reach)») .
	'''
	
	static def compileProperty(Top top, Invariant inv, RANDOM mode)'''
	red repeat({{initState, 0} | 0 | «inv.bound»}, not(«top.compileGoalConst(inv)»)) .
	'''
	
	static def compileProperty(Top top, Reachability reach, DISTRIBUTED mode)'''
	search [1]
	      {initState | 0 | «reach.bound»} 
		=>*
		  {OBJ:Object | T:Time | «reach.bound»} 
		such that
		  check-true(«top.compileGoalConst(reach)») .
	'''

	static def compileProperty(Top top, Invariant inv, DISTRIBUTED mode)'''
	search [1]
	      {initState | 0 | «inv.bound»} 
		=>*
		  {OBJ:Object | T:Time | «inv.bound»} 
		such that
		  check-true(not(«top.compileGoalConst(inv)»)) .
	'''
	
	static def compileInitConst(Top top, Property pr){
		var const = ""
		if(pr instanceof Reachability){
			if((pr as Reachability).initCond != null)
				const = (pr as Reachability).initCond.compileExp.toString
			else
				const = "[[true]]"
		} else if (pr instanceof Invariant){
			if((pr as Invariant).initCond != null)
				const = (pr as Invariant).initCond.compileExp.toString
			else
				const = "[[true]]"
		}
		top.compileConst(const, false)
	}
	
	static def compileGoalConst(Top top, Property pr){
		var const = ""
		if(pr instanceof Reachability){
			const = (pr as Reachability).goalCond.compileExp.toString
		} else if (pr instanceof Invariant){
			const = (pr as Invariant).goalCond.compileExp.toString
		}
		top.compileConst(const, true)
	}
	
	static def compileConst(Top top, String const, Boolean goal){
		if(top.mode instanceof RANDOM){
			if(const.equals("[[true]]")){
				return '''[[true]]'''
			}else if(const.equals("[[false]]")){
				return '''[[false]]'''
			}else{
				return top.name.escape+" | "+ const
			}
		}
		if (!goal){
			if(const.equals("[[true]]")){
				return '''eval([[true]], initState)'''				
			}else if(const.equals("[[false]]")){
				return '''eval([[false]], initState)'''	
			}else{
				return '''eval(«top.name.escape+" | "+ const», initState)'''
			}
		} else if (goal){
			if(const.equals("[[true]]")){
				return '''eval([[true]], OBJ:Object)'''
			}else if(const.equals("[[false]]")){
				return '''eval([[false]], initState)'''	
			}else{
				return '''eval(«top.name.escape+" | "+ const», OBJ:Object)'''
			}
		}
	}
	
	static def getReachabilityCommand(Top top, Reachability reach)'''
	reachability : 
	(«top.name.escape+" | "+ reach.initCond.compileExp»)
	==>
	(«top.name.escape+" | "+ reach.goalCond.compileExp»)
	'''
	
	static def getInvariantCommand(Top top, Invariant inv)'''
	invariant : 
	(«top.name.escape+" | "+ inv.initCond.compileExp»)
	==>
	(«top.name.escape+" | not("+ inv.goalCond.compileExp»))
	'''

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
		val v = e.value
		
		if(v instanceof ContainedNamedElement){
			var lastElement = (v as ContainedNamedElement).containmentPathElements.last.namedElement
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
				return '''«lastElement.compilePrefix»'''
			} else {
				return '''( «path» | «lastElement.compilePrefix» )'''
			}
		}else{
			return '''[[«RtmAadlProperty::compilePropertyValue(v as PropertyValue)»]]'''
		}
	}
	
	private static def compilePrefix(NamedElement ne){
		if(ne instanceof DataPort){
			'''f[«ne.name»]'''
		}else if(ne instanceof DataSubcomponent){
			'''c[«ne.name»]'''
		}else if (ne instanceof BehaviorVariable){
			'''v[«ne.name»]'''
		}else if (ne instanceof ParameterHolder){
			'''p[«ne.name»]'''
		}else{
			'''ErrorPrefix'''
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