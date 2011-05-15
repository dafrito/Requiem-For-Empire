public class ScriptValueType_StringDeferrer extends ScriptValueType{
	private final String m_typeString;
	private Referenced m_reference;
	public ScriptValueType_StringDeferrer(ScriptEnvironment env,String string){
		super(env);
		assert env!=null:"Environment is null";
		assert string!=null:"String is null";
		m_typeString=string;
	}
	public ScriptValueType_StringDeferrer(Referenced ref,String string){
		super(ref.getEnvironment());
		assert ref.getEnvironment()!=null:"String is null";
		assert string!=null:"Environment is null";
		m_reference=ref;
		m_typeString=string;
	}
	public ScriptValueType getBaseType()throws Exception_Nodeable{
		ScriptValueType kw=getEnvironment().getType(m_typeString);
		if(kw==null){
			if(m_reference==null){
				throw new Exception_Nodeable_VariableTypeNotFound((ScriptEnvironment)null,m_typeString);
			}else{
				throw new Exception_Nodeable_VariableTypeNotFound(m_reference,m_typeString);
			}
		}
		return kw;
	}
}
