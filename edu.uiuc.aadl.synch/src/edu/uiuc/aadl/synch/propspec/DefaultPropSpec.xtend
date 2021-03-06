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
		
		-- requirements and formulas below
	'''
	
	def static escape(String name) {
		var List<String> namespace = Arrays.asList(name.split('_'))
		'''«namespace.get(0)»::«namespace.get(0)».«namespace.get(1)»'''
	}
}