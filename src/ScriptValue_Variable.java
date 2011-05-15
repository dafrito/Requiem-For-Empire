public class ScriptValue_Variable implements ScriptValue_Abstract,Nodeable{
	private ScriptValue_Abstract m_value;
	private final ScriptKeywordType m_permission;
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;
	public ScriptValue_Variable(ScriptEnvironment env,ScriptValueType type,ScriptKeywordType permission)throws Exception_Nodeable{
		this(env,type,null,permission);
	}
	public ScriptValue_Variable(ScriptEnvironment env,ScriptValue_Abstract value,ScriptKeywordType permission)throws Exception_Nodeable{
		this(env,value.getType(),value,permission);
	}
	public ScriptValue_Variable(ScriptEnvironment env,ScriptValueType type,ScriptValue_Abstract value,ScriptKeywordType permission)throws Exception_Nodeable{
		m_environment=env;
		m_permission=permission;
		m_type=type;
		if(value==null){m_value=ScriptValue.createUninitializedObject(env,type);
		}else{m_value=value;}
	}
	public ScriptValue_Abstract setReference(Referenced ref,ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Reference Assignments","Setting Variable Reference");
		if(!ScriptValueType.isPrimitiveType(getType())){
			assert Debugger.addNode("Assigning reference");
			assert Debugger.addSnapNode("Variable",this);
			assert Debugger.openNode("Retrieving value");
			value=value.getValue();
			assert Debugger.closeNode("Value",value);
			if(value==null){m_value=null;
			}else{m_value=value.castToType(ref,getType());}
			assert Debugger.closeNode("Reference assignment operation completed",this);
			return m_value;
		}
		assert Debugger.openNode("Assigning value...");
		m_value=m_value.setValue(ref,value.castToType(ref,getType()));
		assert Debugger.closeNode();
		assert Debugger.closeNode("Value assignment operation completed",this);
		return m_value;
	}
	public ScriptKeywordType getPermission()throws Exception_Nodeable{return m_permission;}
	// Abstract-value implementation
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public ScriptValueType getType(){return m_type;}
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{
		if(isConvertibleTo(type)){return new ScriptValue_Variable(getEnvironment(),getType(),getValue(),getPermission());}
		throw new Exception_Nodeable_ClassCast(ref,this,type);
	}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{
		assert Debugger.openNode("Variable Value Retrievals","Retrieving Variable's Value");
		assert Debugger.addNode(this);
		ScriptValue_Abstract returning;
		if(m_value!=null){returning=m_value.getValue();
		}else{returning=null;}
		assert Debugger.addSnapNode("Value",returning);
		assert Debugger.closeNode();
		return returning;
	}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return setReference(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{
		if(m_value==null||m_value.getValue()==null||m_value.getValue() instanceof ScriptValue_Null){
			return(rhs==null||rhs instanceof ScriptValue_Null);
		}
		return getValue().valuesEqual(ref,rhs);
	}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getValue().valuesCompare(ref,rhs);}
	public boolean nodificate(){
		assert Debugger.openNode("Script Variable ("+getType()+")");
		if(m_value!=null){assert Debugger.addSnapNode("Referenced element ("+m_value.getType()+")",m_value);
		}else{assert Debugger.addNode(DebugString.REFERENCEDELEMENTNULL);}
		if(m_permission==null){Debugger.addNode(DebugString.PERMISSIONNULL);
		}else{
			switch(m_permission){
				case PRIVATE:
				assert Debugger.addNode(DebugString.PERMISSIONPRIVATE);
				break;
				case PROTECTED:
				Debugger.addNode(DebugString.PERMISSIONPROTECTED);
				case PUBLIC:
				Debugger.addNode(DebugString.PERMISSIONPUBLIC);
			}
		}
		assert Debugger.closeNode();
		return true;
	}
}
