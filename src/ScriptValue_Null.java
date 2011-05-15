public class ScriptValue_Null extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract,Nodeable{
	public ScriptValue_Null(Referenced ref){
		super(ref);
	}
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		return this;
	}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return null;}
	public boolean isConvertibleTo(ScriptValueType type){return true;}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return this;}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return this;}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{throw new Exception_InternalError("Set Value");}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return(rhs==null||rhs.getValue()==null||rhs.getValue() instanceof ScriptValue_Null);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{throw new Exception_Nodeable_IncomparableObjects(ref,this,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.addNode("Null Script-Value");
		return true;
	}
}
