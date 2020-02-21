package edu.postech.aadl.synch.maude.template

import org.osate.aadl2.ComponentCategory
import org.osate.aadl2.instance.ComponentInstance
import edu.postech.aadl.utils.PropertyUtil
import org.osate.aadl2.PropertyAssociation
import org.osate.xtext.aadl2.properties.util.PropertyUtils
import org.osate.aadl2.ListValue
import org.osate.aadl2.StringLiteral
import org.osate.aadl2.instance.FeatureInstance

class RtmAadlSetting {

	public static val SEMANTICS_PATH = "semantics";
	
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
		PropertyUtil::isEnvironment(o)
	}
	
	static def isCorrectParam(ComponentInstance ci){
		for(PropertyAssociation pa : ci.ownedPropertyAssociations){
			if(pa.property.name.equals(PropertyUtil::INITIAL_VALUE)){
				var value = ((PropertyUtils::getSimplePropertyValue(ci, pa.property) as ListValue).ownedListElements.get(0) as StringLiteral).value;
				if (value.matches("[^0-9]+") && !value.equals("param")){
					return false
				}
			}
		}
		return true
	}
	
	static def isParam(ComponentInstance ci){
		for(PropertyAssociation pa : ci.ownedPropertyAssociations){
			if(pa.property.name.equals(PropertyUtil::INITIAL_VALUE)){
				if(((PropertyUtils::getSimplePropertyValue(ci, pa.property) as ListValue).ownedListElements.get(0) as StringLiteral).value.equals("param")){
					return true
				}
			}
		}
		return false
	}
	
	static def featClass(FeatureInstance fi){
		var o = fi.containingComponentInstance
		o.isEnv ? "Env" : "Data"
	}
	
	static def compClass(ComponentInstance o) {
		switch o.category.value {
			case ComponentCategory::SYSTEM_VALUE:		o.isEnv ? "Env" : "System" 
			case ComponentCategory::PROCESS_VALUE:		"Process"
			case ComponentCategory::THREAD_VALUE:		"Thread"
			case ComponentCategory::DATA_VALUE:			"Data"
			case ComponentCategory::THREAD_GROUP_VALUE:	"ThreadGroup"
		}
	}	
}