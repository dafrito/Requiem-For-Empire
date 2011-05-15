public class ScriptExecutable_ReturnValue extends ScriptElement implements ScriptExecutable,Returnable,Nodeable{
	private ScriptValue_Abstract m_value;
	public ScriptExecutable_ReturnValue(Referenced ref,ScriptValue_Abstract value){
		super(ref);
		m_value=value;
	}
	// Returnabled implementation
	public boolean shouldReturn(){return true;}
	public ScriptValue_Abstract getReturnValue()throws Exception_Nodeable{return m_value.getValue();}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("Executing returnable script-value");
		ScriptValue_Abstract value=m_value.getValue();
		assert Debugger.closeNode();
		return value;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Returnable Script-Value");
		assert Debugger.addSnapNode("Returned Value",m_value);
		assert Debugger.closeNode();
		return true;
	}
}
