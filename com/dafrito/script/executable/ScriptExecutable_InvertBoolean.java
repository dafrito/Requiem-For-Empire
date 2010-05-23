package com.dafrito.script.executable;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Boolean;


public class ScriptExecutable_InvertBoolean extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract{
	private ScriptExecutable m_value;
	public ScriptExecutable_InvertBoolean(Referenced ref,ScriptExecutable value){
		super(ref);
		this.m_value=value;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{return new ScriptValue_Boolean(getEnvironment(),!((ScriptValue_Boolean)this.m_value.execute()).getBooleanValue());}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return ScriptValueType.BOOLEAN;}
	public boolean isConvertibleTo(ScriptValueType type){return getType().equals(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getValue().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return execute();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getValue().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Nodeable implementation
	@Override
    public boolean nodificate(){
		assert LegacyDebugger.open("Boolean Inverter");
		assert super.nodificate();
		assert LegacyDebugger.addSnapNode("Value",this.m_value);
		assert LegacyDebugger.close();
		return true;
	}
}
