package edu.postech.aadl.synch.maude.template

import com.google.common.collect.HashMultimap
import com.google.common.collect.SetMultimap
import edu.postech.aadl.synch.maude.parse.ContDynamicsFlowsVisitor
import edu.postech.aadl.synch.maude.parse.ContDynamicsLexer 
import edu.postech.aadl.synch.maude.parse.ContDynamicsParser
import edu.postech.aadl.utils.PropertyUtil
import java.util.HashSet
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.emf.common.util.EList
import org.osate.aadl2.ConnectedElement
import org.osate.aadl2.DefaultAnnexSubclause
import org.osate.aadl2.DirectionType
import org.osate.aadl2.ModalPropertyValue
import org.osate.aadl2.NamedElement
import org.osate.aadl2.ParameterConnection
import org.osate.aadl2.Port
import org.osate.aadl2.PortConnection
import org.osate.aadl2.PropertyAssociation
import org.osate.aadl2.StringLiteral
import org.osate.aadl2.SystemSubcomponent
import org.osate.aadl2.instance.ComponentInstance
import org.osate.aadl2.instance.ConnectionReference
import org.osate.aadl2.instance.FeatureInstance
import org.osate.aadl2.instance.ModeInstance
import org.osate.aadl2.instance.ModeTransitionInstance
import org.osate.aadl2.instance.SystemInstance
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager
import org.osate.ba.aadlba.BehaviorAnnex
import org.osate.ba.aadlba.BehaviorVariable

import static extension edu.postech.aadl.synch.maude.template.RtmAadlSetting.*
import static extension edu.postech.aadl.utils.PropertyUtil.*
import edu.postech.aadl.synch.maude.parse.ContDynamicsErrorListener
import org.antlr.v4.runtime.misc.ParseCancellationException

class RtmAadlModel extends RtmAadlIdentifier {

	val RtmAadlBehaviorLanguage bc;
	val RtmAadlProperty pc;
	val IProgressMonitor monitor;
	val HashMultimap<ComponentInstance, ConnectionReference> conxTable = HashMultimap::create() ;


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

			
			o.connectionInstances.forEach[connectionReferences.forEach[conxTable.put(context, it)]]	

			'''
			< «o.id("ComponentId")» : «o.compClass» |
				features : (
					«o.featureInstances.map[compileFeature].filterNull.join('\n', "none")»),
				subcomponents : (
					«o.componentInstances.filter[isSync].map[compileComponent].filterNull.join('\n',"none")»),
				«IF o.isData»	
				value : «o.compileValue»,
				«ENDIF»
				«IF o.behavioral && ! (behAnx === null)»
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
					«o.compileModalPropertyValue.map[compileFlows(o)].filterNull.join(" ;\n", "empty")»
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
				connections : (
					«conxTable.get(o).map[compileConnection(o)].filterNull.join(' ;\n', "empty")») 
				> ''' => [
				monitor.worked(1)
			]	
		}
	}
	
	private def compileTarget(FeatureInstance fi, ComponentInstance ci){
		if(fi.direction.incoming){
			for(ConnectionReference cr : conxTable.values){
				if(cr.connection.source.context!==null && cr.connection.destination.context!==null){
					if(cr.connection.destination.context.name.escape.equals(ci.name) && cr.connection.destination.connectionEnd.name.escape.equals(fi.name)){
						return cr.connection.source.context.name
					}
				}
			}
		} else {
			for(ConnectionReference cr : conxTable.values){
				if(cr.connection.source.context!==null && cr.connection.destination.context!==null){
					if(cr.connection.source.context.name.escape.equals(ci.name) && cr.connection.source.connectionEnd.name.escape.equals(fi.name)){
						return cr.connection.destination.context.name
					}
				}
			}
		}
		return "none"
	}

	private def compileFeature(FeatureInstance fi) {
		val f = fi.feature
		var co = fi.containingComponentInstance
		switch f {
			Port: '''
			< «f.id("FeatureId")» : «fi.compileFeatureClass» | 
				content : «fi.compileOutFeature» ,
				«IF !co.isEnv && f.direction.incoming»
					cache : null(Real),
				«ENDIF»
				«IF co.isEnv»
				target : «fi.compileTarget(co)»,
				envCache : «fi.compileOutFeature»,
				«ENDIF»
				properties : «fi.ownedPropertyAssociations.map[compilePropertyAssociation(fi)].filterNull.join(' ;\n', "none")» >'''
			default:
				null => [fi.check(false, "Unsupported feature: " + fi.category.getName() + " " + fi.name)]
		}
	}
	
	private def compileOutFeature(FeatureInstance fi){
		switch fi.category {
			case DATA_PORT: 	 	"null(Real)"
			case EVENT_DATA_PORT: 	"null(Real)"
			case EVENT_PORT:  		"null(Unit)"
			default :  				null => [fi.check(false, "Unsupported feature: " + fi.category.name() + " " + fi.name)]
		}
	}
	
	private def compileFeatureClass(FeatureInstance fi){
		'''«fi.featClass»«(fi.feature as Port).direction.compileDirection(fi)»Port'''
	}

	private def compileDirection(DirectionType type, FeatureInstance o) {
		o.check(! (type.incoming && type.outgoing), "'in out' features are not supported")
		'''«IF type.incoming»In«ENDIF»«IF type.outgoing»Out«ENDIF»'''
	}
	
	private def compileValue(ComponentInstance o){
		o.check( o.correctParam, "invalid initial value: " + o.name)
		if(o.isParam){
			'''param(«IF o.subcomponent.subcomponentType.name.contains("Boolean")»Boolean«ELSE»Real«ENDIF»)'''
		}else{
			'''null(«IF o.subcomponent.subcomponentType.name.contains("Boolean")»Boolean«ELSE»Real«ENDIF»)'''
		}
	}
	
	private def compileVariables(BehaviorVariable bv){
		'''( «bv.id("VarId")» : Real )'''
	}
	
	private def compileVarGenName(ComponentInstance o){
		if(o.getContainingComponentInstance === null)
			return o.name.escape
		return compileVarGenName(o.getContainingComponentInstance) +"."+ o.name.escape
	}
	
	private def compileCurrentMode(EList<ModeInstance> mi){
		mi.forEach[ element | element.name.id("Location")]
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
	
	private def compileJumps(ModeTransitionInstance mti){
		'''(«mti.source.name» -[ («mti.modeTransition.ownedTriggers.map[it.triggerPort.name.escape].filterNull.join(" , ", "[[true]]")») ]-> «mti.destination.name»)'''
	}
	
	private def compileModalPropertyValue(ComponentInstance o){
		for(PropertyAssociation pa : o.ownedPropertyAssociations){
			if(pa.property.qualifiedName().contains(PropertyUtil::CD)){
				return pa.ownedValues
			}
		}
	}
	
	private def compileFlows(ModalPropertyValue mpv, ComponentInstance o){
		var mode = o.compileMode(mpv)
		var expression = (mpv.ownedValue as StringLiteral).value
		
		return "((" + mode + ")" + "[" + expression.parse(o)+" ] )"
	}
	
	private def compileMode(ComponentInstance o, ModalPropertyValue mpv){
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
	}
	
	private def parse(String expression, ComponentInstance ci){
		var stream = new ANTLRInputStream(expression)
		var lexer = new ContDynamicsLexer(stream)
		lexer.removeErrorListeners()
		lexer.addErrorListener(ContDynamicsErrorListener.INSTANCE)
		var tokens = new CommonTokenStream(lexer)
		var parser = new ContDynamicsParser(tokens)
		parser.setBuildParseTree(true)
		parser.removeErrorListeners()
		parser.addErrorListener(ContDynamicsErrorListener.INSTANCE)
		var visitor = new ContDynamicsFlowsVisitor(ci)
		try{
			visitor.visitContinuousdynamics(parser.continuousdynamics)
		}catch(ParseCancellationException e){
			ci.check(false, e.toString)
			return ""
		}
		bc.compileContinuousDynamics(visitor.contDynamics)
	}
	
	private def compileTargetInstanceList(ComponentInstance o){
		val targetInstances = new HashSet<ComponentInstance>()
		for(ConnectionReference cr : conxTable.values){
			if(cr.connection.source.context !== null && cr.connection.destination.context !== null){
				var source = (cr.context as ComponentInstance).findSubcomponentInstance(cr.connection.source.context as SystemSubcomponent)
				if(source.equals(o)){
					targetInstances.add((cr.context as ComponentInstance).findSubcomponentInstance(cr.connection.destination.context as SystemSubcomponent))
				}
			}
		}
		return targetInstances
	}
	
	private def compileSamplingTime(ComponentInstance o) {
		var value = "(" + o.name + " : ("
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.contains(PropertyUtil::SAMPLING_TIME)){
				return value += "rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(0)+"),rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(2)+")))"
			}
		}
		return null;
	}
	
	private def compileResponseTime(ComponentInstance o) {
		var value = "(" + o.name + " : ("
		for(PropertyAssociation p : o.ownedPropertyAssociations){
			if(p.property.name.equals(PropertyUtil::RESPONSE_TIME)){
				return value += "rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(0)+"),rat("+pc.compilePropertyValue(p.property, o).toString.split(" ").get(2)+")))"
			}
		}
		return null;
	}

	private def CharSequence compileConnection(ConnectionReference cr, ComponentInstance ci){
		val c = cr.connection => [
			cr.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type")
		]
		if(ci.isEnv){
			'''(«c.source.compileConnectionEndName(cr)»«IF ci.isSubcomponentData(c.source.connectionEnd.name.escape)» ==> «ELSE» =>> «ENDIF»«c.destination.compileConnectionEndName(cr)»)'''
		} else {
			'''(«c.source.compileConnectionEndName(cr)» --> «c.destination.compileConnectionEndName(cr)»)'''
		}
	}

	private def compileConnectionEndName(ConnectedElement end, ConnectionReference o) {
		switch end {
			ConnectedElement: '''«IF end.context !== null»«end.context.name.escape» .. «ENDIF»«end.connectionEnd.name.escape»'''
			default:
				null => [o.check(false, "Unsupported connection end")]
		}
	}
	
	
	// Compile Property
	private def compilePropertyAssociation(PropertyAssociation p, NamedElement ne) {
		val value = pc.compilePropertyValue(p.property, ne)
		if (value !== null && !value.equals("param")) '''(«p.property.qualifiedName().escape» => {{«value»}})'''
	}


	private def compileInitialValue(NamedElement ne, String none) {
		val iv = ne.dataInitialValue?.ownedListElements
		if(! iv.nullOrEmpty) "[[" + (iv.get(0) as StringLiteral).value + "]]" else none // TODO: type checking
	}
}