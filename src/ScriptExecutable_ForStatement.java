import java.util.*;
public class ScriptExecutable_ForStatement extends ScriptElement implements ScriptExecutable,Returnable,Nodeable{
	private ScriptExecutable m_initializer,m_tester,m_repeater;
	private boolean m_shouldReturn=false;
	private ScriptValue_Abstract m_returnValue;
	private List<ScriptExecutable>m_expressions;
	public ScriptExecutable_ForStatement(ScriptExecutable initializer,ScriptExecutable tester,ScriptExecutable repeater,List<ScriptExecutable>expressions){
		super((Referenced)initializer);
		m_initializer=initializer;
		m_tester=tester;
		m_repeater=repeater;
		m_expressions=expressions;
	}
	// Returnable implementation
	public boolean shouldReturn(){return m_shouldReturn;}
	public ScriptValue_Abstract getReturnValue()throws Exception_Nodeable{
		if(m_returnValue!=null){m_returnValue=m_returnValue.getValue();}
		return m_returnValue;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert Debugger.openNode("For-Statement Executions","Executing For-Statement");
		getEnvironment().advanceNestedStack();
		assert Debugger.openNode("Initializing");
		m_initializer.execute();
		assert Debugger.closeNode();
		while(((ScriptValue_Boolean)m_tester.execute().getValue()).getBooleanValue()){
			assert Debugger.openNode("Looping","Looping iteration");
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
			assert Debugger.closeNode();
			assert Debugger.openNode("Executing repeater");
			m_repeater.execute();
			assert Debugger.closeNode();
		}
		getEnvironment().retreatNestedStack();
		assert Debugger.closeNode();
		return null;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Script For-Statement");
		assert Debugger.addSnapNode("Initializer",m_initializer);
		assert Debugger.addSnapNode("Boolean expression",m_tester);
		assert Debugger.addSnapNode("Repeating expression",m_repeater);
		assert Debugger.addSnapNode("Expressions",m_expressions);
		assert Debugger.closeNode();
		return true;
	}
}
