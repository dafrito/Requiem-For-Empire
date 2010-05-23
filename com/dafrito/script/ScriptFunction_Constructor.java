package com.dafrito.script;

import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptFunction_Constructor extends ScriptFunction{
	private ScriptEnvironment m_environment;
	public ScriptFunction_Constructor(ScriptValueType returnType,List<ScriptValue_Abstract>paramList,ScriptKeywordType permission){
		super(returnType,paramList,permission,false,true);
		this.m_environment=returnType.getEnvironment();
	}
	public ScriptEnvironment getEnvironment(){return this.m_environment;}
	@Override
    public void execute(Referenced ref,List<ScriptValue_Abstract>valuesGiven)throws Exception_Nodeable{
		assert LegacyDebugger.open("Constructor Iterations","Constructor Expression Iteration");
		ScriptTemplate_Abstract object=getEnvironment().getTemplate(getReturnType()).createObject(ref,null);
		getEnvironment().advanceStack(object,this);
		super.execute(ref,valuesGiven);
		setReturnValue(ref,object);
		getEnvironment().retreatStack();
		assert LegacyDebugger.close();
	}
	@Override
    public boolean nodificate(){
		assert LegacyDebugger.open("Constructor-Function Script-Element");
		assert super.nodificate();
		assert LegacyDebugger.close();
		return true;
	}
}
