package edu.uiuc.aadl.synch.propspec

import org.osate.aadl2.instance.SystemInstance
import org.eclipse.core.resources.IFile

class DefaultPropSpec {
	def static deGenerate(SystemInstance model, IFile modelFile) '''
		name: Çmodel.nameÈ;
		
		-- an AADL implementation
		model: Çmodel.systemImplementation.getQualifiedName()È;
		
		-- a path for the corresponding instance model
		instance: "ÇmodelFile.fullPathÈ";
		
		-- requirements and formulas below
	'''
}