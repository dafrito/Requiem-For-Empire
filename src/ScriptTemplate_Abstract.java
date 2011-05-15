import java.util.*;
public abstract class ScriptTemplate_Abstract implements ScriptValue_Abstract,Nodeable{
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;
	private ScriptValueType m_extended;
	private List<ScriptValueType> m_interfaces;
	public ScriptTemplate_Abstract(ScriptEnvironment env,ScriptValueType type){
		m_environment=env;
		m_type=type;
	}
	public ScriptTemplate_Abstract(ScriptEnvironment env,ScriptValueType type,ScriptValueType extended,List<ScriptValueType>interfaces){
		m_environment=env;
		m_type=type;
		m_extended=extended;
		m_interfaces=interfaces;
	}
	public ScriptTemplate_Abstract getExtendedClass(){
		assert getEnvironment()!=null:"Environment is null: "+this;
		if(getEnvironment().getTemplate(getType())!=null&&getEnvironment().getTemplate(getType())!=this){
			return getEnvironment().getTemplate(getType()).getExtendedClass();
		}
		if(m_extended==null){
			return null;
		}
		assert getEnvironment().getTemplate(m_extended)!=null:"Extended class is null!";
		return getEnvironment().getTemplate(m_extended);
	}
	public List<ScriptValueType>getInterfaces(){
		if(getEnvironment().getTemplate(getType())!=null&&getEnvironment().getTemplate(getType())!=this){return getEnvironment().getTemplate(getType()).getInterfaces();}
		return Collections.unmodifiableList(m_interfaces);
	}
	// Abstract-value implementation
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public ScriptValueType getType(){return m_type;}
	public boolean isConvertibleTo(ScriptValueType type){
		if(getEnvironment().getTemplate(getType())!=null&&getEnvironment().getTemplate(getType())!=this){return getEnvironment().getTemplate(getType()).isConvertibleTo(type);}
		assert Debugger.openNode("Value Type Match Test","Checking for Type-Match ("+getType()+" to "+type+")");
		assert Debugger.addNode(this);
		if(getType().equals(type)){assert Debugger.closeNode("Direct match.");return true;}
		if(getInterfaces()!=null){
			for(ScriptValueType scriptInterface:getInterfaces()){
				if(ScriptValueType.isConvertibleTo(getEnvironment(),scriptInterface,type)){
					assert Debugger.closeNode("Interface type match.");
					return true;
				}
			}
		}
		assert Debugger.openNode("No type match, checking extended classes for match.");
		if(getExtendedClass()!=null&&getExtendedClass().isConvertibleTo(type)){
			assert Debugger.closeNode();
			assert Debugger.closeNode();
			return true;
		}
		assert Debugger.closeNode();
		assert Debugger.closeNode();
		return false;
	}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{
		if(isConvertibleTo(type)){return this;}
		throw new Exception_Nodeable_ClassCast(ref,this,type);
	}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return this;}
	public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{return(this==rhs);}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{throw new Exception_InternalError("Templates have no inherent value, and thus their value cannot be set directly.");}
	public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs)throws Exception_Nodeable{throw new Exception_InternalError("Templates have no inherent value, and thus cannot be compared.");}
	// Remaining unimplemented ScriptValue_Abstract functions
	public abstract boolean nodificate();
	// Abstract-template implementation
	public abstract boolean isFullCreation();
	public abstract void disableFullCreation();
	public abstract boolean isConstructing()throws Exception_Nodeable;
	public abstract void setConstructing(boolean constructing)throws Exception_Nodeable;
	public abstract ScriptTemplate instantiateTemplate();
	public abstract boolean isObject();
	public abstract boolean isAbstract()throws Exception_Nodeable;
	public abstract ScriptValue_Variable getStaticReference()throws Exception_Nodeable;
	public abstract ScriptTemplate_Abstract createObject(Referenced ref,ScriptTemplate_Abstract object)throws Exception_Nodeable;
	public abstract ScriptValue_Variable addVariable(Referenced ref,String name,ScriptValue_Variable value)throws Exception_Nodeable;
	public abstract ScriptValue_Variable getVariable(String name)throws Exception_Nodeable;
	public abstract void addFunction(Referenced ref,String name,ScriptFunction_Abstract function)throws Exception_Nodeable;
	public abstract List<ScriptFunction_Abstract>getFunctions();
	public abstract ScriptFunction_Abstract getFunction(String name,List<ScriptValue_Abstract>params);
	public abstract ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn);
	public abstract void addTemplatePreconstructorExpression(ScriptExecutable exec)throws Exception_Nodeable;
	public abstract void addPreconstructorExpression(ScriptExecutable exec)throws Exception_Nodeable;
	public abstract void initialize()throws Exception_Nodeable;
	public abstract void initializeFunctions(Referenced ref)throws Exception_Nodeable;
}
