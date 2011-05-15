public class ScriptExecutable_AutoMathematicator extends ScriptElement implements ScriptExecutable,ScriptValue_Abstract,Nodeable{
	private ScriptValue_Abstract m_value;
	private ScriptOperatorType m_operator;
	private boolean m_isPost;
	public ScriptExecutable_AutoMathematicator(Referenced ref,ScriptValue_Abstract value,ScriptOperatorType operator,boolean isPost){
		super(ref);
		m_value=value;
		m_operator=operator;
		m_isPost=isPost;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("Auto-Mathematicator Executions","Executing Auto-Mathematicator");
		ScriptValue_Abstract returning;
		if(m_operator==ScriptOperatorType.INCREMENT){
			returning=((ScriptValue_Numeric)m_value.getValue()).setNumericValue(((ScriptValue_Numeric)m_value.getValue()).increment(this));
		}else{
			returning=((ScriptValue_Numeric)m_value.getValue()).setNumericValue(((ScriptValue_Numeric)m_value.getValue()).decrement(this));
		}
		assert Debugger.closeNode();
		return returning;
	}
	// ScriptValue_Abstract implementation
	public ScriptValueType getType(){return m_value.getType();}
	public boolean isConvertibleTo(ScriptValueType type){return m_value.isConvertibleTo(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return m_value=m_value.castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{
		assert m_value.getValue() instanceof ScriptValue_Numeric:"Should be a ScriptValue_Numeric: " + m_value.getValue();
		ScriptValue_Numeric value=new ScriptValue_Numeric(getEnvironment(),((ScriptValue_Numeric)m_value.getValue()).getNumericValue());
		ScriptValue_Numeric otherValue=(ScriptValue_Numeric)execute().getValue();
		if(m_isPost){return value;
		}else{return otherValue;}
	}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getValue().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		if(m_operator==ScriptOperatorType.INCREMENT){
			if(m_isPost){
				assert Debugger.openNode("Auto-Mathematicators","Auto-Mathematicator(Post-Incrementing)");
			}else{
				assert Debugger.openNode("Auto-Mathematicators","Auto-Mathematicator(Pre-Incrementing)");
			}
		}else{
			if(m_isPost){
				assert Debugger.openNode("Auto-Mathematicators","Auto-Mathematicator(Post-Decrementing)");
			}else{
				assert Debugger.openNode("Auto-Mathematicators","Auto-Mathematicator(Pre-Decrementing)");
			}
		}
		assert Debugger.addSnapNode("Value",m_value);
		assert Debugger.closeNode();
		return true;
	}
}
