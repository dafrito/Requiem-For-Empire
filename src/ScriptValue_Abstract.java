public interface ScriptValue_Abstract{
	// ScriptValue_Abstract implementation
	public ScriptEnvironment getEnvironment();
	public ScriptValueType getType();
	public boolean isConvertibleTo(ScriptValueType type);
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable;
	public ScriptValue_Abstract getValue()throws Exception_Nodeable;
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable;
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable;
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable;
}

