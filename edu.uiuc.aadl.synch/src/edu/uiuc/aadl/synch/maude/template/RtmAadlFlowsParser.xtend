package edu.uiuc.aadl.synch.maude.template

import org.osate.aadl2.instance.ComponentInstance
import org.osate.aadl2.PropertyAssociation
import edu.uiuc.aadl.utils.PropertyUtil
import org.osate.aadl2.ModalPropertyValue
import org.osate.aadl2.StringLiteral
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager
import com.google.common.collect.SetMultimap
import org.osate.aadl2.NamedElement
import org.osate.xtext.aadl2.properties.util.GetProperties
import java.util.ArrayList
import org.eclipse.emf.common.util.ECollections
import java.util.Comparator

class RtmAadlFlowsParser extends RtmAadlIdentifier{
	
	
	new(AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		super(errMgr, opTable)
	}
	
	public def isAndGetContinuousDynamics(ComponentInstance o){
		for(PropertyAssociation pa : o.ownedPropertyAssociations){
			if(pa.property.qualifiedName().contains(PropertyUtil::CD)){
				return pa.ownedValues
			}
		}
	}
	
	public def compileContinuousDynamics(ModalPropertyValue mpv, ComponentInstance o){
		var mode = ""
		for(String modes : mpv.inModes.get(0).toString.split("#")){
			if(modes.contains(o.name)){
				mode = modes.split("\\.").last
			}
		}
		val expression = (mpv.ownedValue as StringLiteral).value
		
		return "((" + mode + ")" + "[" + expression.split(";").map[if(it.trim.length>1) it.trim.compileCDParsing(o)].filterNull.join(" ; ", "empty") + "])"
	}
	
	private def compileCDParsing(String value, ComponentInstance ne){	
		val componentId = value.split(" ").get(0).substring(0, value.indexOf('('))
		val varId = value.split(" ").get(0).substring(value.indexOf('(')+1, value.indexOf(')'))
		varId.id("VarId")
		componentId.id("VarId")
		
		val expression = value.substring(value.indexOf('=') + 1).trim
		return  "(" + componentId + "(" +varId + ")" + " = " + expression.compileExpressionInitial(componentId).
																		compileExpressionPropertyConstant(ne).
																			compileExpressionVarId(varId).
																			compileExpressionSubComponent(ne).
																				compileExpressionConstant.
																					compileExpressionMinusValue.normalizeSpace + ") "
	}
	
	private def compileExpressionPropertyConstant(String expression, NamedElement ne) {
		var result = " "
		for(String token : expression.split(" ")){
			if(token.contains("::")){
				result += token.openParenthesis + "[["+ GetProperties::lookupPropertyConstant(ne, token.trimBrackets.split("::").get(0), token.trimBrackets.split("::").get(1)).constantValue + "]] " + token.closeParenthesis;
				println("Property Token : " + token)
				println("Property Constant : "+result)
			}
			else{
				result += " " +  token + " "
			}
		}
		result
	}
	
	
	
	private def compileExpressionConstant(String expression){
		var result = " "
		for(String token : expression.split(" ")){
			if(token.trimBrackets.matches("(\\d+)(\\.\\d+)?")){
				result += token.replaceAll(token.trimBrackets, "[["+token.trimBrackets+"]] ")
			}
			else {
				result += " " + token + " "
			}
		}
		result 
	}
	
	private def compileExpressionVarId(String expression, String varId) {
		var result = " "
		for(String token : expression.split(" ")){
			if(token.trimBrackets.equals(varId)){
				result += token.replaceAll(token.trimBrackets, "v[" + token.trimBrackets +"] ")
			} else {
				result += " "+token + " "
			}
		}
		result
		
	}
	
	private def compileExpressionSubComponent(String expression, ComponentInstance o){
		val ciNames = new ArrayList<String>();
		var result = ""
		var temp = true
		ECollections.sort(o.componentInstances, new Comparator<ComponentInstance>(){
			override compare(ComponentInstance o1, ComponentInstance o2) {
				if(o1.name.toString.length < o2.name.toString.length){
					return 1;
				}
				return -1;
			}
		})
		
		for(ComponentInstance ci : o.componentInstances){
			ciNames.add(ci.name)
		}
		
		for(String token : expression.split(" ")){
			temp = true
			for(String name : ciNames){
				if(token.contains(name) && temp){
					result += token.replaceAll(name, "c["+name+"] ")
					temp = false
				}
			}
			if(temp){
				result += " " + token + " "
			}
		}
		result
	}
	
	private def compileExpressionInitial(String expression, String componentId) {
		expression.replaceAll("\\(0\\)", "")
	}
	
	private def compileExpressionMinusValue(String expression){
		var result = ""
		for(String token : expression.split(" ")){
			if(token.contains("-") && token.length > 1){
				result += token.replaceAll("-", "minus(") + ") "
			} else {
				result += " " + token + " "
			}
		}
		result 
	}
	
	private def trimBrackets(String str){
		str.replaceAll("\\(", "").replaceAll("\\)", "")
	}
	
	private def normalizeSpace(String str){
		str.replaceAll("( )+", " ")
	}
	
	private def openParenthesis(String token) {
		var k = 0;
		var result = ""
		for(var i = 0; i < token.length; i++){
			if(token.charAt(i)=="(".charAt(0)){
				k+=1;
			}
		}
		for(var i = 0; i < k; i++){
			result += "("
		}
		println("Debug1 : " + k)
		println("Debug2 : " + result)
		result
	}
	
	private def closeParenthesis(String token) {
				var k = 0;
		var result = ""
		for(var i = 0; i < token.length; i++){
			if(token.charAt(i)==")".charAt(0)){
				k++;
			}
		}
		for(var i = 0; i < k; i++){
			result += ")"
		}
		result
	}
}