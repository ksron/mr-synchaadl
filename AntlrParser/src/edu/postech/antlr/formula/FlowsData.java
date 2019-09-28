package edu.postech.antlr.formula;

import java.util.HashMap;
import java.util.Map;

public class FlowsData{
    Map<String, String> constants;
    Map<String, String> initialValues;

    public FlowsData(){
            constants = new HashMap<String, String>();
            initialValues = new HashMap<String, String>();
    }

    public void addConstant(String key, String value){
        constants.put(key, value);
    }

    public void addInitialValue(String key, String value){
        initialValues.put(key, value);
    }

    public String getConstant(String key){
        return constants.get(key);
    }

    public String getInitialValue(String key){
        return initialValues.get(key);
    }
}
