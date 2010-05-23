package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.gui.Canvas;
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

public class FauxTemplate_Interface extends FauxTemplate{
	public static final String INTERFACESTRING="Interface";
	private Canvas m_interface;
	public FauxTemplate_Interface(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,INTERFACESTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Interface(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
		this.m_interface=new Canvas(env);
	}
	public Canvas getInterface(){return this.m_interface;}
	// Define default constructor here
	@Override
    public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Interface(getEnvironment(),getType());}
	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
    public void initialize()throws Exception_Nodeable{
		assert LegacyDebugger.open("Faux Template Initializations","Initializing interface faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		disableFullCreation();
		getExtendedClass().initialize();
		List<ScriptValue_Abstract>fxnParams=FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING)));
		addFauxFunction("add",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getRoot",ScriptValueType.createType(getEnvironment(),FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert LegacyDebugger.close();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
    public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert LegacyDebugger.open("Faux Template Executions","Executing interface faux template function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Interface template=(FauxTemplate_Interface)rawTemplate;
		ScriptValue_Abstract returning;
		assert LegacyDebugger.addSnapNode("Template provided",template);
		assert LegacyDebugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Interface)createObject(ref,template);}
			assert LegacyDebugger.close();
			return template;
		}else if(name.equals("add")){
			template.getInterface().getRoot().add(Parser.getElement(params.get(0)));
			assert LegacyDebugger.close();
			return null;
		}else if(name.equals("getRoot")){
			returning=Parser.getRiffElement(template.getInterface().getRoot());
			assert LegacyDebugger.close();
			return returning;
		}
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert LegacyDebugger.close();
		return returning;
	}
	@Override
    public boolean nodificate(){
		assert LegacyDebugger.open("Interface Faux Template");
		assert super.nodificate();
		assert LegacyDebugger.addNode(this.m_interface);
		assert LegacyDebugger.close();
		return true;
	}
}
