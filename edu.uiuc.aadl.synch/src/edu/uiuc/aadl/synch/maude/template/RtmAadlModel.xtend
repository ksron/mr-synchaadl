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

class RtmAadlModel extends RtmAadlIdentifier {		
	
	private val RtmAadlBehaviorLanguage bc;
	private val RtmAadlProperty pc;
	private val IProgressMonitor monitor;
	private val HashMultimap<ComponentInstance,ConnectionReference> conxTable = HashMultimap::create() ;
	
	new(IProgressMonitor pm, AnalysisErrorReporterManager errMgr, SetMultimap<String,String> opTable) {
		super(errMgr, opTable)
		this.monitor = pm;
		this.bc  = new RtmAadlBehaviorLanguage(errMgr, opTable);
		this.pc  = new RtmAadlProperty(errMgr, opTable);
	}
	
	def doGenerate(SystemInstance model) {
		val initialState = model.compileComponent(model.periodinMS);
		
		'''
		(tomod «model.name.toUpperCase»-MODEL is
			including MODEL-TRANSITION-SYSTEM .
			
			--- AADL identifiers
			«generateIds»
					
			--- the initial state
			op initial : -> Object .
			eq initial = collapse(
				«initialState») .
		endtom)
		
		(set tick det .)
	'''
	}
	
	 
	private def CharSequence compileComponent(ComponentInstance o, double parentPeriod) {
		if (monitor.isCanceled())	// when canceled
			throw new OperationCanceledException
		else {
			val period = o.periodinMS => [ 
				o.check(! o.periodic || (it > 0.0 && parentPeriod % it == 0), "Invalid period: " + o.category.getName() + " " + o.name) ]
				
			val anxSub = o.componentClassifier.ownedAnnexSubclauses.filter(typeof(DefaultAnnexSubclause)) => [
				o.check((! o.behavioral) || ! it.empty, "No behavior annex definition in thread: " + o.category.getName() + " " + o.name) ]

			val behAnx = if(o.behavioral && ! (anxSub.empty))anxSub.get(0).parsedAnnexSubclause as BehaviorAnnex
			
			//val behAnx = o.componentClassifier.ownedAnnexSubclauses.filter(typeof(BehaviorAnnex)) => [
			//	o.check(! o.behavioral || ! it.empty, "No behavior annex definition in thread: " + o.category.getName() + " " + o.name) ]
				
			// update the connection table
			o.connectionInstances.forEach [connectionReferences.forEach[conxTable.put(context, it) ]]
				
			'''
			< «o.id("ComponentId")» : «o.compClass» |
				«IF o.periodic && period > 0»
				rate : «(parentPeriod / period).intValue»,
				«ENDIF»
				«IF o.isData»
				value : «o.compileInitialValue("bot")»,
				«ENDIF»
				features : (
					«o.featureInstances.map[compileFeature].filterNull.join('\n',"none")»),
				subcomponents : (
					«o.componentInstances.filter[isSync].map[compileComponent(period)].filterNull.join('\n',"none")»),
				properties : (
					«o.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")»),
				«IF o.behavioral && ! (behAnx == null)»
				currState : (
					«behAnx.states.filter[isInitial].get(0).id("Location")»),
				completeStates : (
					«behAnx.states.filter[isComplete].map[id("Location")].join(' ', "empty")»),
				variables : (
					«behAnx.variables.map[id("VarId")].join(' ; ', "empty")»),
				transitions : (
					«behAnx.transitions.map[bc.compileTransition(it)].filterNull.join(' ;\n', "empty")»),
				«ENDIF»
				connections : (
					«conxTable.get(o).map[compileConnection].filterNull.join(' ;\n', "empty")») >''' => [ monitor.worked(1) ]
		}
	}
	

	private def compileFeature(FeatureInstance o) {	
		//TODO: generate an error for a lack of initial value of a feedback ooutput
	 	val f = o.feature
	 	switch f {
	 		Parameter: 
		 		'''
		 		< «f.id("FeatureId")» : «f.direction.compileDirection(o)»Param | 
		 			content : bot,
		 			properties : «f.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")» >'''
	 		Port: 
		 		'''
		 		< «f.id("FeatureId")» : «f.direction.compileDirection(o)»Port | 
		 			content : «o.compileInitialValue("nil")»,
		 			«IF f.direction.incoming»
		 			cache : bot,
		 			«ENDIF»
		 			properties : «f.ownedPropertyAssociations.map[compilePropertyAssociation(o)].filterNull.join(' ;\n', "none")» >'''
	 		default: 
	 			null => [o.check(false, "Unsupported feature: " + o.category.getName() + " " + o.name)]
	 	}
	 	
	}
	
	
	private def compileDirection(DirectionType type, FeatureInstance o) {
		o.check(! (type.incoming && type.outgoing), "'in out' features are not supported")
		'''«IF type.incoming»In«ENDIF»«IF type.outgoing»Out«ENDIF»''' 
	}
	 
	
	private def CharSequence compileConnection(ConnectionReference o) {
		//TODO: check input adaptors for multirate connecLtions
		val c = o.connection => [ o.check(it instanceof PortConnection || it instanceof ParameterConnection, "Unsupported connection type") ]
		'''(«c.source.compileConnectionEndName(o)» --> «c.destination.compileConnectionEndName(o)»)'''
	}

	private def compileConnectionEndName(ConnectedElement end, ConnectionReference o) {
		switch end {
			ConnectedElement:	'''«IF end.context != null»«end.context.name.escape» .. «ENDIF»«end.connectionEnd.name.escape»'''
			default:			null => [ o.check(false, "Unsupported connection end") ]
		}
	}
	
	
	private def compilePropertyAssociation(PropertyAssociation p, NamedElement ne) {
		val value = pc.compilePropertyValue(p.property,ne)
		if (value != null)	'''(«p.property.qualifiedId("PropertyId")» => {«value»})'''
	}
	
	
	private def compileInitialValue(NamedElement ne, String none) {
		val iv = ne.dataInitialValue?.ownedListElements
		if (! iv.nullOrEmpty ) "[" + (iv.get(0) as StringLiteral).value + "]" else none		//TODO: type checking
	}
	
}