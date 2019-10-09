package edu.postech.aadl.synch.maude.template

import org.osate.aadl2.ComponentCategory
import org.osate.aadl2.instance.ComponentInstance

class RtmAadlSetting {

	public static val SEMANTICS_PATH = "rtmaude";
	public static val SEMANTICS_FILE = "interpreter-symbolic.maude";
	public static val ANALYSIS_FILE  = "mr-sync-aadl-analysis.maude"; // should be revised. 
	
	static def isSync(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:			true 
			case ComponentCategory::PROCESS_VALUE:			true
			case ComponentCategory::THREAD_VALUE:			true
			case ComponentCategory::DATA_VALUE:				true
			case ComponentCategory::THREAD_GROUP_VALUE:		true
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
	
	static def isSubcomponentData(ComponentInstance o, String name){
		for(ComponentInstance ci : o.componentInstances){
			if(ci.name.toString.equals(name)){
				return true
			}
		}
		return false
	}
	
	static def isEnv(ComponentInstance o) {
		for(var i = 0; i < o.ownedPropertyAssociations.size; i++){
			if(o.ownedPropertyAssociations.get(i).getProperty().name.equals("isEnvironment") &&
				o.ownedPropertyAssociations.get(i).getProperty().inherit)
				return true;
		}
		return false;	
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