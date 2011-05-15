import java.util.List;
import java.util.Collection;
public class ScriptFunction_Faux extends ScriptFunction implements ScriptFunction_Abstract,Nodeable{
	private FauxTemplate m_fauxTemplate;
	private ScriptTemplate_Abstract m_object;
	private String m_name;
	public ScriptFunction_Faux(FauxTemplate template,String name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract,boolean isStatic){
		super(type,params,permission,isAbstract,isStatic);
		m_fauxTemplate=template;
		m_name=name;
	}
	public void setFauxTemplate(ScriptTemplate_Abstract template){
		assert Debugger.openNode("Faux Function Referenced-Template Changes","Changing Faux-Function Object");
		assert Debugger.addNode(this);
		assert Debugger.addSnapNode("New object",template);
		m_fauxTemplate=(FauxTemplate)template;
		assert Debugger.closeNode();
	}
	public void setTemplate(ScriptTemplate_Abstract template){
		assert Debugger.openNode("Faux Function Object Changes","Changing Object");
		assert Debugger.addNode(this);
		assert Debugger.addSnapNode("New object",template);
		m_object=template;
		assert Debugger.closeNode();
	}
	public void execute(Referenced ref, List<ScriptValue_Abstract>params)throws Exception_Nodeable{
		if(m_name.equals("")){setReturnValue(ref,m_fauxTemplate.execute(ref,m_name,params,null));
		}else{
			if(m_object==null){m_object=m_fauxTemplate;}
			setReturnValue(ref,m_fauxTemplate.execute(ref,m_name,params,m_object));
		}
	}
	public void addExpression(ScriptExecutable exp)throws Exception_Nodeable{throw new Exception_InternalError("Invalid call in faux function");}
	public void addExpressions(Collection<ScriptExecutable>list)throws Exception_Nodeable{throw new Exception_InternalError("Invalid call in faux function");}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Faux Script-Function ("+ScriptFunction.getDisplayableFunctionName(m_name)+")");
		assert super.nodificate();
		assert Debugger.addNode("Faux Template Type: " + m_fauxTemplate.getType());
		assert Debugger.closeNode();
		return true;
	}
}
