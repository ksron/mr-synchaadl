package edu.uiuc.aadl.synch.maude.template

import org.osate.aadl2.ComponentCategory
import org.osate.aadl2.instance.ComponentInstance

class RtmAadlSetting {

	public static val SEMANTICS_PATH = "rtmaude";
	public static val SEMANTICS_FILE = "mr-sync-aadl-interpreter.maude";
	public static val ANALYSIS_FILE  = "mr-sync-aadl-analysis.maude";
	
	static def isSync(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:			true 
			case ComponentCategory::PROCESS_VALUE:			true
			case ComponentCategory::THREAD_VALUE:			true
			case ComponentCategory::DATA_VALUE:				true
			case ComponentCategory::THREAD_GROUP_VALUE:		true
			//case ComponentCategory::SUBPROGRAM_VALUE:			true
			//case ComponentCategory::SUBPROGRAM_GROUP_VALUE:	true
			default: false
		}
	}
	
	static def periodic(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:		true 
			case ComponentCategory::PROCESS_VALUE:		true
			case ComponentCategory::THREAD_VALUE:		true
			case ComponentCategory::THREAD_GROUP_VALUE:	true
			default: false
		}
	}
	
	static def behavioral(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::THREAD_VALUE:		true 
			//case ComponentCategory::SUBPROGRAM_VALUE:	true
			default: false
		}
	}
	
	static def isData(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::DATA_VALUE:			true
			default: false
		}
	}
	
	static def compClass(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:		"System" 
			case ComponentCategory::PROCESS_VALUE:		"Process"
			case ComponentCategory::THREAD_VALUE:		"Thread"
			case ComponentCategory::DATA_VALUE:			"Data"
			case ComponentCategory::THREAD_GROUP_VALUE:	"ThreadGroup"
		}
	}	
}