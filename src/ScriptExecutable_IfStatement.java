import java.util.*;
public class ScriptExecutable_IfStatement extends ScriptElement implements ScriptExecutable,Returnable{
	private ScriptValue_Abstract m_testingValue;
	private List<ScriptExecutable>m_expressions;
	private ScriptExecutable_IfStatement m_elseStatement;
	private boolean m_shouldReturn=false;
	private ScriptValue_Abstract m_returnValue;
	public ScriptExecutable_IfStatement(Referenced ref,ScriptValue_Abstract test,List<ScriptExecutable>list){
		super(ref);
		m_testingValue=test;
		m_expressions=list;
	}
	public void setElseStatement(ScriptExecutable_IfStatement statement){
		if(m_elseStatement!=null){m_elseStatement.setElseStatement(statement);
		}else{m_elseStatement=statement;}
	}
	// Returnable implementation
	public boolean shouldReturn(){return m_shouldReturn;}
	public ScriptValue_Abstract getReturnValue()throws Exception_Nodeable{
		if(m_returnValue!=null){m_returnValue=m_returnValue.getValue();}
		return m_returnValue;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("If-Statement Executions","Executing If-Statements");
		if(((ScriptValue_Boolean)m_testingValue.getValue()).getBooleanValue()){
			getEnvironment().advanceNestedStack();
			for(ScriptExecutable exec:m_expressions){
				exec.execute();
				if(exec instanceof Returnable&&((Returnable)exec).shouldReturn()){
					m_returnValue=((Returnable)exec).getReturnValue();
					m_shouldReturn=true;
					assert Debugger.closeNode();
					return null;
				}
			}
			getEnvironment().retreatNestedStack();
		}else{
			if(m_elseStatement!=null){m_elseStatement.execute();}
		}
		assert Debugger.closeNode();
		return null;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Script If-Statement");
		assert Debugger.addSnapNode("Testing Expression",m_testingValue);
		assert Debugger.addSnapNode("Expressions",m_expressions);
		assert Debugger.addSnapNode("Else statement",m_elseStatement);
		assert Debugger.closeNode();
		return true;
	}
}
