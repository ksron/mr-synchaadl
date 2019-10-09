package edu.postech.aadl.synch.maude.template

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

import static extension edu.postech.aadl.synch.maude.template.RtmAadlSetting.*
import static extension edu.postech.aadl.utils.PropertyUtil.*
import static extension org.osate.xtext.aadl2.properties.util.GetProperties.*
import org.osate.ba.aadlba.BehaviorAnnex
import org.osate.aadl2.DefaultAnnexSubclause
import java.util.ArrayList
import java.util.List
import org.osate.aadl2.instance.ModeInstance
import org.osate.aadl2.instance.ModeTransitionInstance
import org.eclipse.emf.common.util.EList
import edu.postech.aadl.utils.PropertyUtil
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
import org.eclipse.emf.common.util.ECollections
import java.util.Comparator
import org.antlr.v4.runtime.ANTLRInputStream
import edu.postech.antlr.parser.FlowsLexer
import org.antlr.v4.runtime.CommonTokenStream
import edu.postech.antlr.parser.FlowsParser
import edu.postech.antlr.firstPath.FlowsVisitor
import edu.postech.antlr.secondPath.ContinuousFunction
import org.osate.ba.aadlba.BehaviorActionSequence

class RtmAadlModel extends RtmAadlIdentifier {

	private val RtmAadlBehaviorLanguage bc;
	private val RtmAadlProperty pc;
	private val IProgressMonitor monitor;
	private val SetMultimap<ComponentInstance, ConnectionReference> conxTable = HashMultimap::create() ;


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
	
	private def putNotDuplicate(SetMultimap<ComponentInstance, ConnectionReference> smm, ComponentInstance ci, ConnectionReference cr){
		var check1 = true
		var check2 = true
		
		for(ConnectionReference param : smm.get(ci)){
			if(param.connection.source.context != null && cr.connection.source.context != null && param.connection.source.context.name.equals(cr.connection.source.context.name)){
				if(param.connection.source.connectionEnd.name.equals(cr.connection.source.connectionEnd.name)){
					check1 = false
				}
			}
			if(param.connection.source.context == null && cr.connection.source.context == null){
				if(param.connection.source.connectionEnd.name.equals(cr.connection.source.connectionEnd.name)){
					check1 = false
				}
			}
		}
		
		for(ConnectionReference param : smm.get(ci)){
			if(param.connection.destination.context != null && cr.connection.destination.context != null && param.connection.destination.context.name.equals(cr.connection.destination.context.name)){
				if(param.connection.destination.connectionEnd.name.equals(cr.connection.destination.connectionEnd.name)){
					check2 = false
				}
			}
			if(param.connection.destination.context == null && cr.connection.destination.context == null){
				if(param.connection.destination.connectionEnd.name.equals(cr.connection.destination.connectionEnd.name)){
					check2 = false
				}
			}
		}
		
		if(check1 || check2){
			smm.put(ci, cr)
		}
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

			
			o.connectionInstances.forEach[connectionReferences.forEach[conxTable.putNotDuplicate(context, it)]]	

			'''
			< «o.id("ComponentId")» : «IF o.isEnv»Env«ELSE»«o.compClass»«ENDIF» |
				«IF o.isEnv»
				features : (
					«o.featureInstances.map[compileEnvFeature(o)].filterNull.join('\n', "none")»),
				«ELSE»
				features : (
					«o.featureInstances.map[compileDataFeature].filterNull.join('\n', "none")»),
				«ENDIF»
				subcomponents : (
					«o.componentInstances.filter[isSync].map[compileComponent].filterNull.join('\n',"none")»),
				«IF o.isData»
				value : null(«IF o.subcomponent.subcomponentType.name.contains("Boolean")»Boolean«ELSE»Real«ENDIF»),
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
				«IF o.isEnv»
				currMode : (
					«o.modeInstances.compileCurrentMode»
					),
				jumps : (
					«o.modeTransitionInstances.map[compileJumps].filterNull.join(' ;\n', 'empty')»
					),
				flows : (
					«isAndGetContinuousDynamics(o).map[compileContinuousDynamics(it, o)].filterNull.join(" ;\n", "empty")»
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
				«IF o.isEnv»
				connections : (
					«conxTable.get(o).map[compileEnvConnection(o)].filterNull.join(' ;\n', "empty")») 
				«ELSE»
				connections : (
					«conxTable.get(o).map[compileDataConnection].filterNull.join(' ;\n', "empty")»)
				«ENDIF»
				> ''' => [
				monitor.worked(1)
			]	
		}
	}
	
	// Compile Features
	private def compileEnvFeature(FeatureInstance o, ComponentInstance ci) {
		val f = o.feature
		switch f {
			Port: '''
			< «f.id("FeatureId")» : Env«f.direction.compileDirection(o)»Port | 
				content : «f.category.compileOutFeature(o)»,
				«IF f.direction.outgoing»
				target : «compileOutTarget(f.id("FeatureId"), ci.id("ComponentId"))»,
				«ELSE»
				target : «compileInTarget(f.id("FeatureId"), ci.id("ComponentId"))»,
				«ENDIF»
				
				properties : «o.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")»,
				envCache : «f.category.compileOutFeature(o)» >'''
			default:
				null => [o.check(false, "Unsupported feature: " + o.category.getName() + " " + o.name)]
		}
	}
	
	private def compileOutTarget(String featureId, String componentId){
		for(ConnectionReference cr : conxTable.values){
			if(cr.connection.source.context!=null && cr.connection.destination.context!=null){
				if(cr.connection.source.context.name.equals(componentId) && cr.connection.source.connectionEnd.name.equals(featureId)){
					return cr.connection.destination.context.name
				}
			}
		}
		return "none"
	}
	
	private def compileInTarget(String featureId, String componentId){
		for(ConnectionReference cr : conxTable.values){
			if(cr.connection.source.context!=null && cr.connection.destination.context!=null){
				if(cr.connection.destination.context.name.equals(componentId) && cr.connection.destination.connectionEnd.name.equals(featureId)){
					return cr.connection.source.context.name
				}
			}
		}
		return "none"
	}

	private def compileDataFeature(FeatureInstance o) {
		val f = o.feature
		switch f {
			Port: '''
			< «f.id("FeatureId")» : Data«f.direction.compileDirection(o)»Port | 
				content : «f.category.compileOutFeature(o)» ,
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
			case EVENT_DATA_PORT : return "null(Real)"
			case EVENT_PORT : return "null(Unit)"
			default : return "null(Real)"
		}
	}

	private def compileDirection(DirectionType type, FeatureInstance o) {
		o.check(! (type.incoming && type.outgoing), "'in out' features are not supported")
		'''«IF type.incoming»In«ENDIF»«IF type.outgoing»Out«ENDIF»'''
	}
	
	
	// Compile Variables
	private def compileVariables(BehaviorVariable bv){
		bv.id("VarId")
		'''( «bv.name» : Real )'''
		
	}
	
	// Compile VarGen
	private def compileVarGenName(ComponentInstance o){
		if(o.getContainingComponentInstance == null)
			return o.id("ComponentId")
		return compileVarGenName(o.getContainingComponentInstance) +"."+ o.id("ComponentId")
	}
	
	// Compile Current Mode
	private def compileCurrentMode(EList<ModeInstance> mi){
		mi.forEach[ element | id(element.name, "Location")]
		for(ModeInstance value : mi){
			if(value.initial==true){
				return value.name
			}
		}
		if(mi.isEmpty){
			return "@@default@loc@@"
		}
		return mi.get(0).name
	}
	
	// Compile Jumps
	private def compileJumps(ModeTransitionInstance mti){
		val src = mti.name.split("_").get(0)
		val dest = mti.name.split("_").get(mti.name.split("_").length-1)
		val guard = mti.name.substring(src.length+1, mti.name.length-dest.length-1)
		'''(«mti.source.name» -[ («mti.modeTransition.ownedTriggers.map[it.triggerPort.name.escape].filterNull.join(" , ", "[[true]]")») ]-> «mti.destination.name»)'''
	}
	// Compile Flows
	public def isAndGetContinuousDynamics(ComponentInstance o){
		for(PropertyAssociation pa : o.ownedPropertyAssociations){
			if(pa.property.qualifiedName().contains(PropertyUtil::CD)){
				return pa.ownedValues
			}
		}
	}
	
	public def compileContinuousDynamics(ModalPropertyValue mpv, ComponentInstance o){		
		var mode = ""
		if(!mpv.inModes.isEmpty){
			for(String modes : mpv.inModes.get(0).toString.split("#")){
				if(modes.contains(o.name)){
					mode = modes.split("\\.").last
				}
			}
		} else {
			mode = "@@default@loc@@"
		}

		var expression = (mpv.ownedValue as StringLiteral).value
		
		return "((" + mode + ")" + "[" + expression.antlrParsing(o)+"])"
		
		//return "((" + mode + ")" + "[" + expression.split(";").map[if(it.trim.length>1) it.trim.compileCDParsing(o)].filterNull.join(" ; ", "empty") + "])"
	}
	
	private def antlrParsing(String expression, ComponentInstance ci){
		println("Parsing..")
		
		var stream = new ANTLRInputStream(expression)
		var lexer = new FlowsLexer(stream)
		var tokens = new CommonTokenStream(lexer)
		var parser = new FlowsParser(tokens)
		
		println("Result : " + parser.getBuildParseTree) 
		
        val bhaSeq = new FlowsVisitor().setComponentInstance(ci).visitContinuousdynamics(parser.continuousdynamics()) as BehaviorActionSequence;
		val cf = new ContinuousFunction(bhaSeq, this.bc)
		cf.parse
		cf.setVarId(this);
		
		cf.getMaude
		//""
	}
	
	// Compile Sampling/Response
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
				if(ci.id("ComponentId").equals(target) && targetInstanceContains(targetInstances, ci)){
					targetInstances.add(ci)
				}
			}
		}
		//println(targetInstances);
		return targetInstances
	}
	
	private def targetInstanceContains(ArrayList<ComponentInstance> tis, ComponentInstance target){
		for(ComponentInstance ci : tis){
			if(ci.name.toString.equals(target.name) && ci.ownedPropertyAssociations.size() == target.ownedPropertyAssociations.size()){
				return false
			}
		}
		return true
	}
	
	private def compileSamplingTime(ComponentInstance o) {
		var value = "(" + o.id("ComponentId") + " : ("
		//println(value)
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.contains(PropertyUtil::SAMPLING_TIME)){
				return value += "rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(0)+"),rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(2)+")))"
			}
		}
		return null;
	}
	
	private def compileResponseTime(ComponentInstance o) {
		var value = "(" + o.id("ComponentId") + " : ("
		//println(value)
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			//println("PropertyAssociation : " + p.property.name)
			if(p.property.name.equals(PropertyUtil::RESPONSE_TIME)){
				return value += "rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(0)+"),rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(2)+")))"
			}
		}
		return null;
	}

	
	// Connection
	private def CharSequence compileDataConnection(ConnectionReference o) {
		val c = o.connection => [
			o.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type")
		]
		'''(«c.source.compileConnectionEndName(o)» --> «c.destination.compileConnectionEndName(o)»)'''
	}
	
	private def CharSequence compileEnvConnection(ConnectionReference o, ComponentInstance ci) {
		val c = o.connection => [
			o.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type")
		]
		
		'''(«c.source.compileConnectionEndName(o)»«IF ci.isSubcomponentData(c.source.connectionEnd.name.escape)» ==> «ELSE» =>> «ENDIF»«c.destination.compileConnectionEndName(o)»)'''
	}

	private def compileConnectionEndName(ConnectedElement end, ConnectionReference o) {
		switch end {
			ConnectedElement: '''«IF end.context != null»«end.context.name.escape» .. «ENDIF»«end.connectionEnd.name.escape»'''
			default:
				null => [o.check(false, "Unsupported connection end")]
		}
	}
	
	
	// Compile Property
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


	// Utility
	private def compileInitialValue(NamedElement ne, String none) {
		val iv = ne.dataInitialValue?.ownedListElements
		if(! iv.nullOrEmpty) "[[" + (iv.get(0) as StringLiteral).value + "]]" else none // TODO: type checking
	}
}
