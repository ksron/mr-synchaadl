package edu.uiuc.aadl.synch.maude.template

import com.google.common.collect.SetMultimap
import edu.uiuc.aadl.utils.PropertyUtil
import org.osate.aadl2.BooleanLiteral
import org.osate.aadl2.IntegerLiteral
import org.osate.aadl2.NamedElement
import org.osate.aadl2.Property
import org.osate.aadl2.PropertyValue
import org.osate.aadl2.RealLiteral
import org.osate.aadl2.StringLiteral
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager
import org.osate.xtext.aadl2.properties.util.PropertyUtils

class RtmAadlProperty extends RtmAadlIdentifier {	

	new(AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		super(errMgr, opTable)
	}
	
	def compilePropertyValue(Property pr, NamedElement ne) {
		switch (pr.eContainer as NamedElement).name {
			case PropertyUtil::SYNCHAADL:	pr.compileSynchAadlPropertyValue(ne)
			default:						pr.compileDefaultPropertyValue(ne)
		}
	}
	
	def compileSynchAadlPropertyValue(Property pr, NamedElement ne) {
		//TODO: check fast/slow adaptor types for multirate connections..
		switch pr.name {
			case PropertyUtil::INPUT_ADAPTOR:	(PropertyUtils::getSimplePropertyValue(ne,pr) as StringLiteral).value
			default:							pr.compileDefaultPropertyValue(ne)
		}
	}
	
	
	def compileDefaultPropertyValue(Property pr, NamedElement ne) {
		val pe = PropertyUtils::getSimplePropertyValue(ne,pr)
		if (pe instanceof PropertyValue)	
			compilePropertyValue(pe as PropertyValue)
	}
	
	
	/**************************************************************************************************************
	 * Property expressions (only basic values)
	 */
	 
	
	static def dispatch CharSequence compilePropertyValue(BooleanLiteral be) {
		if (be.isValue()) "true" else "false"
	}
	
	
	static def dispatch CharSequence compilePropertyValue(StringLiteral sv) { 
		'"' + sv.value + '"'
	}
	
	
	static def dispatch CharSequence compilePropertyValue(IntegerLiteral iv) { 
		translateUnit(Long::toString(iv.value), iv.unit?.name)
	}
	
	
	static def dispatch CharSequence compilePropertyValue(RealLiteral rv) {
		translateUnit(Double::toString(rv.value), rv.unit?.name)
	}
	
	static def dispatch CharSequence compilePropertyValue(PropertyValue pv) {
		null
	}
	
	static private def CharSequence translateUnit(String num, String unit) {
		switch unit?.toLowerCase() {
			case null:	return num
			case "ms":	return num
			case "sec":	return num
			default:	return num + " " + unit
		}
	}
	
}