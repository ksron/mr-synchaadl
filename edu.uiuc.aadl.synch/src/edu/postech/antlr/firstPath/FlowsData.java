package edu.postech.antlr.firstPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowsData{
    Map<String, String> constants;
	List<String> componentData;

    public FlowsData(){
            constants = new HashMap<String, String>();
		componentData = new ArrayList<String>();
    }

    public void addConstant(String key, String value){
        constants.put(key, value);
    }

	public void addComponentData(String key) {
		componentData.add(key);
    }

    public String getConstant(String key){
        return constants.get(key);
    }

	public boolean checkComponentData(String key) {
		return componentData.contains(key);
    }
}
