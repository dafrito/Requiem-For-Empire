import java.util.List;
public class ScriptFunction_Constructor extends ScriptFunction{
	private ScriptEnvironment m_environment;
	public ScriptFunction_Constructor(ScriptValueType returnType,List<ScriptValue_Abstract>paramList,ScriptKeywordType permission){
		super(returnType,paramList,permission,false,true);
		m_environment=returnType.getEnvironment();
	}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public void execute(Referenced ref,List<ScriptValue_Abstract>valuesGiven)throws Exception_Nodeable{
		assert Debugger.openNode("Constructor Iterations","Constructor Expression Iteration");
		ScriptTemplate_Abstract object=getEnvironment().getTemplate(getReturnType()).createObject(ref,null);
		getEnvironment().advanceStack(object,this);
		super.execute(ref,valuesGiven);
		setReturnValue(ref,object);
		getEnvironment().retreatStack();
		assert Debugger.closeNode();
	}
	public boolean nodificate(){
		assert Debugger.openNode("Constructor-Function Script-Element");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
