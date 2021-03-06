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
import org.osate.aadl2.PropertyAssociation
import org.osate.aadl2.ModalPropertyValue
import org.osate.aadl2.ArrayRange
import org.osate.aadl2.impl.RangeValueImpl
import java.text.NumberFormat
import org.osate.aadl2.impl.IntegerLiteralImpl
import org.osate.aadl2.RangeValue
import org.osate.aadl2.NumberValue
import org.osate.aadl2.ListValue

class RtmAadlProperty extends RtmAadlIdentifier {	

	new(AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		super(errMgr, opTable)
	}
	
	def compilePropertyValue(Property pr, NamedElement ne) {
		//println(ne)
		switch (pr.eContainer as NamedElement).name {
			case PropertyUtil::SYNCHAADL:	pr.compileSynchAadlPropertyValue(ne)
			case PropertyUtil::DATA_MODEL:	pr.compileDataModelPropertyValue(ne)
			default:						pr.compileDefaultPropertyValue(ne)
		}
	}
	
	def compileDataModelPropertyValue(Property pr, NamedElement ne){
		switch pr.name {
			case PropertyUtil::INITIAL_VALUE: 			((PropertyUtils::getSimplePropertyValue(ne,pr) as ListValue).ownedListElements.get(0) as StringLiteral).value
		}
	}
	
	def compileSynchAadlPropertyValue(Property pr, NamedElement ne) {
		//TODO: check fast/slow adaptor types for multirate connections..
		switch pr.name {
			case PropertyUtil::INPUT_ADAPTOR:	(PropertyUtils::getSimplePropertyValue(ne,pr) as StringLiteral).value
			case PropertyUtil::SAMPLING_TIME:	compileRangePropertyValue(pr, ne)
			case PropertyUtil::RESPONSE_TIME: 	compileRangePropertyValue(pr, ne)
			default:							pr.compileDefaultPropertyValue(ne)
		}
	}
	
	def compileRangePropertyValue(Property pr, NamedElement ne) {
		var range = ""

		val minimum = (ne.getSimplePropertyValue(pr) as RangeValue).minimum
		val maximum = (ne.getSimplePropertyValue(pr) as RangeValue).maximum
		
		if(minimum instanceof IntegerLiteral)
			range += (minimum as IntegerLiteral).value + " .. "
		else
			range += (minimum as RealLiteral).value + " .. "
			
		if(maximum instanceof IntegerLiteral)
			range += (maximum as IntegerLiteral).value
		else
			range += (maximum as RealLiteral).value

		return range
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