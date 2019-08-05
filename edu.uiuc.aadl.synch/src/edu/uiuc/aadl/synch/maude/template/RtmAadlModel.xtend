package edu.uiuc.aadl.synch.maude.template

import com.google.common.collect.HashMultimap
import com.google.common.collect.SetMultimap
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.OperationCanceledException
// import org.osate.aadl2.AbstractConnectionEnd
import org.osate.aadl2.ConnectedElement
import org.osate.aadl2.DirectionType
import org.osate.aadl2.NamedElement
import org.osate.aadl2.Parameter
import org.osate.aadl2.ParameterConnection
import org.osate.aadl2.Port
import org.osate.aadl2.PortConnection
import org.osate.aadl2.PropertyAssociation
import org.osate.aadl2.StringLiteral
import org.osate.aadl2.instance.ComponentInstance
import org.osate.aadl2.instance.ConnectionReference
import org.osate.aadl2.instance.FeatureInstance
import org.osate.aadl2.instance.SystemInstance
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager

import static extension edu.uiuc.aadl.synch.maude.template.RtmAadlSetting.*
import static extension edu.uiuc.aadl.utils.PropertyUtil.*
import static extension org.osate.xtext.aadl2.properties.util.GetProperties.*
import org.osate.ba.aadlba.BehaviorAnnex
import org.osate.aadl2.DefaultAnnexSubclause
import java.util.ArrayList
import java.util.List
import org.osate.aadl2.instance.ModeInstance
import org.osate.aadl2.instance.ModeTransitionInstance
import org.eclipse.emf.common.util.EList
import edu.uiuc.aadl.utils.PropertyUtil
import org.osate.aadl2.instance.ConnectionInstance
import org.osate.aadl2.FlowSpecification
import org.osate.aadl2.instance.FlowSpecificationInstance
import org.osate.aadl2.instance.EndToEndFlowInstance
import org.osate.aadl2.NamedValue
import org.osate.xtext.aadl2.properties.util.GetProperties
import org.eclipse.emf.ecore.EObject
import org.osate.ba.aadlba.BehaviorVariable
import java.util.regex.Pattern
import org.osate.aadl2.PortCategory
import org.osate.aadl2.ListValue
import org.osate.aadl2.ModalPropertyValue

class RtmAadlModel extends RtmAadlIdentifier {

	private val RtmAadlBehaviorLanguage bc;
	private val RtmAadlProperty pc;
	private val IProgressMonitor monitor;
	private val HashMultimap<ComponentInstance, ConnectionReference> conxTable = HashMultimap::create() ;


	new(IProgressMonitor pm, AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		super(errMgr, opTable)
		this.monitor = pm;
		this.bc = new RtmAadlBehaviorLanguage(errMgr, opTable);
		this.pc = new RtmAadlProperty(errMgr, opTable);
	}

	def doGenerate(SystemInstance model) {
		val initialState = model.compileComponent
		'''
			mod «model.name.toUpperCase»-MODEL is
				including MODEL-TRANSITION-SYSTEM .
				
				--- AADL identifiers
				«generateIds»
				--- the initial state
				op initial : -> Object .
				eq initial = «initialState» .
			endm
			
			mod «model.name.toUpperCase»-MODEL-SYMBOLIC is
				including «model.name.toUpperCase»-MODEL .
				including BEHAVIOR-SYMBOLIC-LOCATION .
				
				«generateLocations»
			endm
		'''
	}

	private def CharSequence compileComponent(ComponentInstance o) {
		if (monitor.isCanceled()) // when canceled
			throw new OperationCanceledException
		else {
			val anxSub = o.componentClassifier.ownedAnnexSubclauses.filter(typeof(DefaultAnnexSubclause)) => [
				o.check((! o.behavioral) || ! it.empty,
					"No behavior annex definition in thread: " + o.category.getName() + " " + o.name)
			]

			val behAnx = if(o.behavioral && ! (anxSub.empty)) anxSub.get(0).parsedAnnexSubclause as BehaviorAnnex
			val isEnv = o.isEnv
			
			o.connectionInstances.forEach[connectionReferences.forEach[conxTable.put(context, it)]]		
			
			'''
			< «o.id("ComponentId")» : «IF isEnv»Env«ELSE»«o.compClass»«ENDIF» |
				«IF isEnv»
				features : (
					«o.featureInstances.map[compileEnvFeature(o)].filterNull.join('\n', "none")»),
				«ELSE»
				features : (
					«o.featureInstances.map[compileDataFeature].filterNull.join('\n', "none")»),
				«ENDIF»
				subcomponents : (
					«o.componentInstances.filter[isSync].map[compileComponent].filterNull.join('\n',"none")»),
				«IF o.isData»
					value : null(Real),
				«ENDIF»
				«IF o.behavioral && ! (behAnx == null)»
				currState : (
					«behAnx.states.filter[isInitial].get(0).id("Location")»),
				completeStates : (
					«behAnx.states.filter[isComplete].map[id("Location")].join(' ', "empty")»),
				variables : (
					«behAnx.variables.map[compileVariables].join(' ; ', "empty")»),
				transitions : (
					«behAnx.transitions.map[bc.compileTransition(it)].filterNull.join(' ;\n', "empty")»),
				loopBound : (
					«o.compileInitialValue("10")»),
				transBound : (
					«o.compileInitialValue("10")»),
				varGen : (
					< "«o.compileVarGenName»" >
					),
				«ENDIF»
				«IF isEnv»
				currMode : (
					«o.modeInstances.compileCurrentMode»
					),
				jumps : (
					«o.modeTransitionInstances.map[compileJumps].filterNull.join(' ;\n', 'none')»
					),
				flows : (
					«o.isAndGetContinuousDynamics.map[compileContinuousDynamics(o)].filterNull.join(" ;\n", "empty")»
					),
				sampling : (
					«o.compileTargetInstanceList.map[compileSamplingTime].filterNull.join(" ,\n", "empty")»
					),
				response : (
					«o.compileTargetInstanceList.map[compileResponseTime].filterNull.join(" ,\n", "empty")»
					),
				varGen : (
					< "«o.compileVarGenName»" >
					),
				«ENDIF»
				properties : (
					«o.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")»),
				«IF isEnv»
				connections : (
					«conxTable.get(o).map[compileEnvConnection].filterNull.join(' ;\n', "empty")») 
				«ELSE»
				connections : (
					«conxTable.get(o).map[compileDataConnection].filterNull.join(' ;\n', "empty")»)
				«ENDIF»
				> ''' => [
				monitor.worked(1)
			]	
		}
	}
	
	private def compileVariables(BehaviorVariable bv){
		bv.id("VarId")
		'''( «bv.name» : Real )'''
		
	}
	
	private def isAndGetContinuousDynamics(ComponentInstance o){
		for(PropertyAssociation pa : o.ownedPropertyAssociations){
			if(pa.property.qualifiedName().contains(PropertyUtil::CD)){
				return pa.ownedValues
			}
		}
	}
	
	private def compileContinuousDynamics(ModalPropertyValue mpv, ComponentInstance o){
		var mode = mpv.inModes.get(0).toString.split("\\.").get(1)
		val expression = (mpv.ownedValue as StringLiteral).value
		
		return "[" + mode + "]" + expression.split(";").map[if(it.trim.length>1) it.trim.compileCDParsing(o)]
	}
	
	private def compileCDParsing(String value, ComponentInstance ne){	
		val componentId = value.split(" ").get(0).substring(0, value.indexOf('('))
		val varId = value.split(" ").get(0).substring(value.indexOf('(')+1, value.indexOf(')'))
		varId.id("VarId")
		componentId.id("VarId")
		
		
		val expression = value.substring(value.indexOf('=') + 1).trim
		
		return  "(" + componentId + "(" +varId + ")" + " = " + expression.compileExpressionInitial(componentId).
																		compileExpressionPropertyConstant(ne).
																			compileExpressionVarId(varId).
																				compileExpressionConstant.
																					compileExpressionMinusValue + ")"
	}
	
	private def compileExpressionPropertyConstant(String expression, NamedElement ne) {
		var result = ""
		for(String token : expression.split(" ")){
			if(token.trimBrackets.contains("::")){
				result += "[["+ GetProperties::lookupPropertyConstant(ne, token.trimBrackets.split("::").get(0), token.trimBrackets.split("::").get(1)).constantValue + "]] "
			}
			else{
				result += token + " "
			}
		}
		result
	}
	
	private def compileExpressionConstant(String expression){
		var result = ""
		for(String token : expression.split(" ")){
			if(token.trimBrackets.matches("\\d+(\\.\\d+)?")){
				result += token.replaceAll(token.trimBrackets, "[["+token.trimBrackets+"]] ")
			}
			else {
				result += token + " "
			}
		}
		result 
	}
	
	private def compileExpressionVarId(String expression, String varId) {
		var result = ""
		for(String token : expression.split(" ")){
			if(token.trimBrackets.equals(varId)){
				result += token.replaceAll(token.trimBrackets, "v[" + token.trimBrackets +"] ")
			} else {
				result += token + " "
			}
		}
		result
	}
	
	private def compileExpressionInitial(String expression, String componentId) {
		expression.replaceAll(componentId+"\\(0\\)", "c["+componentId+"]")
	}
	
	private def compileExpressionMinusValue(String expression){
		var result = ""
		for(String token : expression.split(" ")){
			if(token.contains("-") && token.length > 1){
				result += token.replaceAll("-", "minus(") + ")"
			} else {
				result += token + " "
			}
		}
		result 
	}
	
	private def trimBrackets(String str){
		str.replaceAll("\\(", "").replaceAll("\\)", "")
	}
	
	private def isOperator(String op){
		switch op{
			case "+": 	return true
			case "-": 	return true
			case "/": 	return true
			case "*":	return true
			default:	return false
		}
	}
	
	private def compileVarGenName(ComponentInstance o){
		if(o.getContainingComponentInstance == null)
			return o.id("ComponentId")
		return compileVarGenName(o.getContainingComponentInstance) +"."+ o.id("ComponentId")
	}
	
	private def compileTargetInstanceList(ComponentInstance o){
		val targets = new ArrayList<String>()
		for(ConnectionReference cr : conxTable.values){
			if(cr.connection.source.context != null && cr.connection.destination.context != null){
				if(cr.connection.source.context.name.equals(o.id("ComponentId"))){
					targets.add(cr.connection.destination.context.name)
				}
			}
		}
		val targetInstances = new ArrayList<ComponentInstance>()
		for(ComponentInstance ci : conxTable.keySet){
			for(String target : targets){
				if(ci.id("ComponentId").equals(target)){
					targetInstances.add(ci)
				}
			}
		}
		return targetInstances
	}
	
	private def compileTarget(String featureId, String componentId){
		for(ConnectionReference cr : conxTable.values){
			if(cr.connection.source.context!=null && cr.connection.destination.context!=null){
				if(cr.connection.source.context.name.equals(componentId) && cr.connection.source.connectionEnd.name.equals(featureId)){
					return cr.connection.destination.context.name
				}
			}
		}
		return "none"
	}

	private def compileEnvFeature(FeatureInstance o, ComponentInstance ci) {
		val f = o.feature
		switch f {
			Port: '''
			< «f.id("FeatureId")» : Env«f.direction.compileDirection(o)»Port | 
				content : «IF(f.direction.incoming)»«o.compileInitialValue("null(Unit)")»«ELSE»«o.compileInitialValue("null(Real)")»«ENDIF»,
				«IF f.direction.outgoing»
					target : «compileTarget(f.id("FeatureId"), ci.id("ComponentId"))»,
				«ENDIF»
				properties : «o.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")»,
				envCache : «IF(f.direction.incoming)»«o.compileInitialValue("null(Unit)")»«ELSE»«o.compileInitialValue("null(Real)")»«ENDIF» >'''
			default:
				null => [o.check(false, "Unsupported feature: " + o.category.getName() + " " + o.name)]
		}

	}

	private def compileDataFeature(FeatureInstance o) {
		val f = o.feature
		switch f {
			Port: '''
			< «f.id("FeatureId")» : Data«f.direction.compileDirection(o)»Port | 
				content : «IF (f.direction.incoming)»null(Real)«ELSE»«f.category.compileOutFeature(o)»«ENDIF» ,
				«IF f.direction.incoming»
					cache : null(Real),
				«ENDIF»
				properties : «o.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")» >'''
			default:
				null => [o.check(false, "Unsupported feature: " + o.category.getName() + " " + o.name)]
		}

	}
	
	private def compileOutFeature(PortCategory cat, FeatureInstance o){
		val c = o.category
		switch c {
			case DATA_PORT : return "null(Real)"
			case EVENT_DATA_PORT : return "null(Unit)"
			case EVENT_PORT : return "null(Unit)"
			default : return "null(Real)"
		}
	}

	private def compileDirection(DirectionType type, FeatureInstance o) {
		o.check(! (type.incoming && type.outgoing), "'in out' features are not supported")
		'''«IF type.incoming»In«ENDIF»«IF type.outgoing»Out«ENDIF»'''
	}

	private def CharSequence compileDataConnection(ConnectionReference o) {
		// TODO: check input adaptors for multirate connecLtions
		val c = o.connection => [
			o.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type")
		]
		'''(«c.source.compileConnectionEndName(o)» --> «c.destination.compileConnectionEndName(o)»)'''
	}
	
	private def CharSequence compileEnvConnection(ConnectionReference o) {
		// TODO: check input adaptors for multirate connecLtions\
		
		val c = o.connection => [
			o.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type")
		]
		'''(«c.source.compileConnectionEndName(o)» ==> «c.destination.compileConnectionEndName(o)»)'''
	}

	private def compileConnectionEndName(ConnectedElement end, ConnectionReference o) {
		switch end {
			ConnectedElement: '''«IF end.context != null»«end.context.name.escape» .. «ENDIF»«end.connectionEnd.name.escape»'''
			default:
				null => [o.check(false, "Unsupported connection end")]
		}
	}
	
	private def compileSamplingTime(ComponentInstance o) {
		var value = "(" + o.id("ComponentId") + " : ("
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.equals(PropertyUtil::SAMPLING_TIME)){
				return value += "rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(0)+"),rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(2)+")))"
			}
		}
		return "empty"
	}
	
	private def compileResponseTime(ComponentInstance o) {
		var value = "(" + o.id("ComponentId") + " : ("
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.equals(PropertyUtil::RESPONSE_TIME)){
				return value += "rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(0)+"),rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(2)+")))"
			}
		}
		return "empty"
	}

	private def compilePropertyAssociation(PropertyAssociation p, NamedElement ne) {
		switch(p.property.name){
			case PropertyUtil::NONDETERMINISTIC: 	return null
			case PropertyUtil::ENVIRONMENT:			return null
			case PropertyUtil::CD:					return null
			case PropertyUtil::ODE:					return null
		}
		
		val value = pc.compilePropertyValue(p.property, ne)
		if (value != null) '''(«p.property.qualifiedName().escape» => {{«value»}})'''
	}

	private def compileInitialValue(NamedElement ne, String none) {
		val iv = ne.dataInitialValue?.ownedListElements
		if(! iv.nullOrEmpty) "[[" + (iv.get(0) as StringLiteral).value + "]]" else none // TODO: type checking
	}
	
	private def compileCurrentMode(EList<ModeInstance> mi){
		mi.forEach[ element | id(element.name, "Location")]
		for(ModeInstance value : mi){
			if(value.initial==true){
				return value.name
			}
		}
		return mi.get(0).name
	}
	
	private def compileJumps(ModeTransitionInstance mti){
		val src = mti.name.split("_").get(0)
		val dest = mti.name.split("_").get(mti.name.split("_").length-1)
		val guard = mti.name.substring(src.length+1, mti.name.length-dest.length-1)
		'''(«src» -[ «guard.escape» ]-> «dest»)'''
		
	}

}
