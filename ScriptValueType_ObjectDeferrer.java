public class ScriptValueType_ObjectDeferrer extends ScriptValueType{
	private ScriptValue_Abstract m_template;
	private String m_name;
	public ScriptValueType_ObjectDeferrer(ScriptValue_Abstract template,String name){this(template.getEnvironment(),template,name);}
	public ScriptValueType_ObjectDeferrer(ScriptEnvironment env,ScriptValue_Abstract template,String name){
		super(env);
		assert name!=null;
		m_template=template;
		m_name=name;
	}
	public ScriptValueType getBaseType()throws Exception_Nodeable{
		if(m_template!=null){
			return getEnvironment().getTemplate(m_template.getType()).getVariable(m_name).getType();
		}else{
			return getEnvironment().retrieveVariable(m_name).getType();
		}
	}
}

