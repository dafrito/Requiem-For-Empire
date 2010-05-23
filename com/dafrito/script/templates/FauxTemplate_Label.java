package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.gui.InterfaceElement_Label;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.script.Parser;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_Label extends FauxTemplate_InterfaceElement{
	public static final String LABELSTRING="Label";
	public FauxTemplate_Label(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,LABELSTRING),ScriptValueType.createType(env,FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Label(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
		setElement(new InterfaceElement_Label(env,null,null,""));
	}
	public InterfaceElement_Label getLabel(){return (InterfaceElement_Label)getElement();}
	// Define default constructor here
	@Override
    public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Label(getEnvironment(),getType());}
	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
    public void initialize()throws Exception_Nodeable{
		assert LegacyDebugger.open("Faux Template Initializations","Initializing label faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addConstructor(getType(),fxnParams);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addConstructor(getType(),fxnParams);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams=FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addFauxFunction("setLabel",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=FauxTemplate.createEmptyParamList();
		addFauxFunction("getLabel",ScriptValueType.STRING,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert LegacyDebugger.close();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert LegacyDebugger.open("Faux Template Executions","Executing Label Faux Template Function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Label template=(FauxTemplate_Label)rawTemplate;
		ScriptValue_Abstract returning;
		assert LegacyDebugger.addSnapNode("Template provided",template);
		assert LegacyDebugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Label)createObject(ref,template);}
			String label="";
			if(params.size()>0){
				label=Parser.getString(params.get(params.size()-1));
				params.remove(params.size()-1);
			}
			template.getLabel().setString(label);
		}else if(name.equals("setString")){
			template.getLabel().setString(Parser.getString(params.get(0)));
			assert LegacyDebugger.close();
			return null;
		}else if(name.equals("getString")){
			returning=Parser.getRiffString(getEnvironment(),template.getLabel().getString());
			assert LegacyDebugger.close();
			return returning;
		}
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert LegacyDebugger.close();
		return returning;
	}
	// Nodeable and ScriptConvertible implementations
	@Override
    public boolean nodificate(){
		if(getLabel()!=null){assert LegacyDebugger.open("Label Faux Template ("+getLabel().getString()+")");
		}else{assert LegacyDebugger.open("Label Faux Template");}
		assert super.nodificate();
		assert LegacyDebugger.close();
		return true;
	}
}
