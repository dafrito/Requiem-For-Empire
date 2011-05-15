import java.util.List;
import java.util.Collection;
public class ScriptExecutable_ParseFunction extends ScriptElement implements ScriptFunction_Abstract,ScriptExecutable,Nodeable{
	private boolean m_isStatic,m_isAbstract;
	private ScriptGroup m_body;
	private ScriptValue_Abstract m_template;
	private String m_name;
	private ScriptValueType m_returnType;
	private List<ScriptValue_Abstract>m_parameters;
	private ScriptKeywordType m_permission;
	public ScriptExecutable_ParseFunction(Referenced ref,ScriptValueType returnType,ScriptValue_Abstract object,String name,List<ScriptValue_Abstract>paramList,ScriptKeywordType permission,boolean isStatic,boolean isAbstract,ScriptGroup body){
		super(ref);
		m_name=name;
		m_template=object;
		m_returnType=returnType;
		m_parameters=paramList;
		m_permission=permission;
		m_isStatic=isStatic;
		m_isAbstract=isAbstract;
		m_body=body;
	}
	public String getName(){return m_name;}
	public ScriptGroup getBody(){return m_body;}
	public boolean isStatic(){return m_isStatic;}
	// ScriptFunction implementation
	public boolean isAbstract(){return m_isAbstract;}
	public ScriptKeywordType getPermission(){return m_permission;}
	public ScriptValueType getReturnType(){return m_returnType;}
	public ScriptValue_Abstract getReturnValue(){throw new Exception_InternalError(getEnvironment(),"Invalid call in unparsed function");}
	public void setReturnValue(Referenced element,ScriptValue_Abstract value){throw new Exception_InternalError(getEnvironment(),"Invalid call in unparsed function");}
	public List<ScriptValue_Abstract>getParameters(){return m_parameters;}
	public void execute(Referenced ref,List<ScriptValue_Abstract>valuesGiven)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in unparsed function");}
	public void addExpression(ScriptExecutable exp)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in unparsed function");}
	public void addExpressions(Collection<ScriptExecutable>list)throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in unparsed function");}
	public boolean areParametersConvertible(List<ScriptValue_Abstract>list){return ScriptFunction.areParametersConvertible(getParameters(),list);}
	public boolean areParametersEqual(List<ScriptValue_Abstract>list){return ScriptFunction.areParametersEqual(getParameters(),list);}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{throw new Exception_InternalError(getEnvironment(),"Invalid call in unparsed function");}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Unparsed Script-Function ("+ScriptFunction.getDisplayableFunctionName(m_name)+")");
		assert super.nodificate();
		assert Debugger.addNode("Static: "+m_isStatic);
		assert Debugger.addSnapNode("Body",m_body);
		assert Debugger.closeNode();
		return true;
	}
}
