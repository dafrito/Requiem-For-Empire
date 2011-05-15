import java.util.List;
public class ScriptTemplate_Placeholder extends ScriptTemplate_Abstract implements ScriptValue_Abstract,Nodeable{
	private String m_name;
	public ScriptTemplate_Placeholder(ScriptEnvironment env,String name){
		super(env,null,null,null);
		m_name=name;
	}
	private ScriptTemplate_Abstract getTemplate(){
		try{
			ScriptTemplate_Abstract template=(ScriptTemplate_Abstract)getEnvironment().retrieveVariable(m_name).getValue();
			assert template!=null:"Template could not be retrieved ("+m_name+")";
			return template;
		}catch(Exception_Nodeable ex){throw new Exception_InternalError("Exception occurred while retrieving template: "+ex);}
	}
	// Abstract-value implementation
	public ScriptValueType getType(){return getTemplate().getType();}
	public boolean isConvertibleTo(ScriptValueType type){return getTemplate().isConvertibleTo(type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{return getTemplate().castToType(ref,type);}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return getTemplate().getValue();}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{return getTemplate().setValue(ref,value);}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getTemplate().valuesEqual(ref,rhs);}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return getTemplate().valuesCompare(ref,rhs);}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Template Placeholder ("+m_name+")");
		assert Debugger.addSnapNode("Referenced Template",getTemplate());
		assert Debugger.closeNode();
		return true;
	}
	// Abstract-template implementation
	public boolean isFullCreation(){return getTemplate().isFullCreation();}
	public void disableFullCreation(){getTemplate().disableFullCreation();}
	public boolean isConstructing()throws Exception_Nodeable{return getTemplate().isConstructing();}
	public void setConstructing(boolean constructing)throws Exception_Nodeable{getTemplate().setConstructing(constructing);}
	public boolean isObject(){return getTemplate().isObject();}
	public boolean isAbstract()throws Exception_Nodeable{return getTemplate().isAbstract();}
	public ScriptTemplate instantiateTemplate(){return getTemplate().instantiateTemplate();}
	public ScriptValue_Variable getStaticReference()throws Exception_Nodeable{return getTemplate().getStaticReference();}
	public ScriptTemplate_Abstract createObject(Referenced ref,ScriptTemplate_Abstract object)throws Exception_Nodeable{return getTemplate().createObject(ref,object);}
	public ScriptValue_Variable addVariable(Referenced ref,String name,ScriptValue_Variable value)throws Exception_Nodeable{return getTemplate().addVariable(ref,name,value);}
	public ScriptValue_Variable getVariable(String name)throws Exception_Nodeable{return getTemplate().getVariable(name);}
	public void addFunction(Referenced ref,String name,ScriptFunction_Abstract function)throws Exception_Nodeable{getTemplate().addFunction(ref,name,function);}
	public List<ScriptFunction_Abstract>getFunctions(){return getTemplate().getFunctions();}
	public ScriptFunction_Abstract getFunction(String name,List<ScriptValue_Abstract>params){return getTemplate().getFunction(name,params);}
	public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn){return getTemplate().getFunctionTemplate(fxn);}
	public void addTemplatePreconstructorExpression(ScriptExecutable exec)throws Exception_Nodeable{getTemplate().addTemplatePreconstructorExpression(exec);}
	public void addPreconstructorExpression(ScriptExecutable exec)throws Exception_Nodeable{getTemplate().addPreconstructorExpression(exec);}
	public void initialize()throws Exception_Nodeable{getTemplate().initialize();}
	public void initializeFunctions(Referenced ref)throws Exception_Nodeable{getTemplate().initializeFunctions(ref);}
	// Overloaded ScriptTemplate_Abstract fxns
	public ScriptTemplate_Abstract getExtendedClass(){return getTemplate().getExtendedClass();}
	public List<ScriptValueType>getInterfaces(){return getTemplate().getInterfaces();}
}
