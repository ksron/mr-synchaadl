package edu.postech.aadl.synch.maude.template

import com.google.common.collect.SetMultimap
import org.osate.aadl2.Element
import org.osate.aadl2.NamedElement
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager
import java.util.List
import java.util.ArrayList
import java.util.Set

class RtmAadlIdentifier {
	
	private val AnalysisErrorReporterManager errMgr;
	private val SetMultimap<String, String> opTable;
	
	new (AnalysisErrorReporterManager errMgr, SetMultimap<String, String> opTable) {
		this.errMgr = errMgr;
		this.opTable = opTable;
	}
	
	def generateIds() '''
		«FOR key: opTable.keySet»
		ops «opTable.get(key).join(" ")» : -> «key» [ctor] .
		«ENDFOR»
	'''
	
	def generateLocations() '''
		«FOR key: opTable.keySet»
		«IF key.equals("Location")»
		«locEncode(opTable.get(key))»
		«ENDIF»
		«ENDFOR»
	'''
	
	def locEncode(Set<String> names){
		var idx = 0
		var encode = ""
		for(String name : names){
			encode += "eq " + name + " = loc(real(" + (idx++) + ")) .\n"
		}
		encode += "eq @@default@loc@@ = loc(real(" + (idx++) + ")) .\n"
		return encode
	}
	
	def check(Element obj, boolean cond, String msg) {
		cond => [if (!cond) errMgr.error(obj, msg)];
	}
	
	def id(String id, String sort) {
		id.escape => [opTable.put(sort, it)]
	}
	
	def id(NamedElement ne, String sort) {
		ne.name.id(sort)
	}
	
	def qualifiedId(NamedElement ne, String sort) {
		ne.getQualifiedName().id(sort)
	}
	
	/*
	 *  static auxiliary methods  
	 */
	
	static def escape(String name) {
		return name.replaceAll("_", "");
	}
	
	static def path(String path, (String)=>String esp) {
		path.split('\\.').map[esp.apply(it)].join(' . ')
	}
	
	static def join(Iterable<?> iterable, CharSequence separator, CharSequence empty) {
		if (iterable.nullOrEmpty)
			empty
		else
			iterable.join(separator)
			
	}
	
	public def isVarId(String vid){
		for(String v : opTable.get("VarId")){
			if(vid.equals(v))
				return true
		}
		return false
	}
}