public class ScriptValue_String implements ScriptValue_Abstract,ScriptConvertible,Nodeable{
	private String m_value;
	private final ScriptEnvironment m_environment;
	public ScriptValue_String(ScriptEnvironment env,String string){
		m_environment=env;
		m_value=string;
	}
	public String getStringValue(){return m_value;}
	// Abstract-value implementation
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public ScriptValueType getType(){return ScriptValueType.STRING;}
	public boolean isConvertibleTo(ScriptValueType type){return getType().equals(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{
		assert Debugger.addNode("Type Casting","Casting ("+getType()+" to "+type+")");
		if(getType().equals(type)){return this;}
		throw new Exception_Nodeable_ClassCast(ref,this,type);
	}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return this;}
	public ScriptValue_Abstract setValue(Referenced ref,ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Value Assignments","Setting String Value");
		assert Debugger.addSnapNode("Former value",this);
		m_value=((ScriptValue_String)value.castToType(ref,getType())).getStringValue();
		assert Debugger.closeNode("New value",this);
		return this;
	}
	public boolean valuesEqual(Referenced ref,ScriptValue_Abstract rhs)throws Exception_Nodeable{
		return getStringValue().equals(((ScriptValue_String)rhs.castToType(ref,getType())).getStringValue());
	}
	public int valuesCompare(Referenced ref,ScriptValue_Abstract rhs)throws Exception_Nodeable{
		return getStringValue().compareTo(((ScriptValue_String)rhs.castToType(ref,getType())).getStringValue());
	}
	// Interface implementations
	public Object convert(){return new String(m_value);}
	public boolean nodificate(){
		assert Debugger.openNode("String Script-Value ("+getStringValue().length()+" character(s): "+getStringValue());
		assert Debugger.addSnapNode("Reference",super.toString());
		assert Debugger.closeNode();
		return true;
	}
}
