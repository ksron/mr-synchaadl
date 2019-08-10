package edu.uiuc.aadl.synch.propspec

import org.osate.aadl2.instance.SystemInstance
import org.eclipse.core.resources.IFile
import java.util.Arrays
import java.util.List

class DefaultPropSpec {
	def static deGenerate(SystemInstance model, IFile modelFile) '''
		name: «model.name»;
		
		-- an AADL implementation
		model: «model.name.escape»;
		
		
		-- a path for the corresponding instance model
		instance: "«modelFile.fullPath»";
		
		-- symbolic / distributed / random mode
		mode: "symbolic";
		
		-- search condition : initialCondition ==> FinalCondition in time INTEGER
		search: (drone1.drone.droneProc.droneThread | abs(currY) < 1.0 ) ==> (drone1.drone.droneProc.droneThread | abs(currY) < 1.0 ) in time 10 ;
	'''
	
	def static escape(String name) {
		var List<String> namespace = Arrays.asList(name.split('_'))
		'''«namespace.get(0)»::«namespace.get(0)».«namespace.get(1)»'''
	}
}