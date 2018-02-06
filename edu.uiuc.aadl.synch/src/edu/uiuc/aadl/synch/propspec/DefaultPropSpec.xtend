package edu.uiuc.aadl.synch.propspec

import org.osate.aadl2.instance.SystemInstance
import org.eclipse.core.resources.IFile

class DefaultPropSpec {
	def static deGenerate(SystemInstance model, IFile modelFile) '''
		name: «model.name»;
		
		-- an AADL implementation
		model: «model.getQualifiedName»;
		
		-- a path for the corresponding instance model
		instance: "«modelFile.fullPath»";
		
		-- requirements and formulas below
	'''
}