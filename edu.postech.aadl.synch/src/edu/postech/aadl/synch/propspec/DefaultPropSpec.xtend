package edu.postech.aadl.synch.propspec

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
		mode: symbolic;
		
		-- proposition [id] : AADL Boolean Expression 
		proposition [ prop ] : env1 | ( x >= 10 and x <= 25 ) ;
		
		-- reachability property [id] : initialCondition ==> FinalCondition in time INTEGER
		reachability [ reach ] : ( env1 | x >= 10 and x <= 25 ) ==> ( env1 | x >= 10 and x <= 25 ) in time 5 ;
		
		-- invariant property [id] : initialCondition ==> FinalCondition in time INTEGER
		invariant [ inv ] : ( env1 | x >= 10 and x <= 25 ) ==> ( env1 | x >= 10 and x <= 25 ) in time 10 ;
	'''
	
	def static escape(String name) {
		var List<String> namespace = Arrays.asList(name.split('_'))
		'''«namespace.get(0)»::«namespace.get(0)».«namespace.get(1)»'''
	}
}