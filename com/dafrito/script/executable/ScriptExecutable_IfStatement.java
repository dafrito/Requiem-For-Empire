package com.dafrito.script.executable;

import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.Returnable;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Boolean;

public class ScriptExecutable_IfStatement extends ScriptElement implements ScriptExecutable,Returnable{
	private ScriptValue_Abstract m_testingValue;
	private List<ScriptExecutable>m_expressions;
	private ScriptExecutable_IfStatement m_elseStatement;
	private boolean m_shouldReturn=false;
	private ScriptValue_Abstract m_returnValue;
	public ScriptExecutable_IfStatement(Referenced ref,ScriptValue_Abstract test,List<ScriptExecutable>list){
		super(ref);
		this.m_testingValue=test;
		this.m_expressions=list;
	}
	public void setElseStatement(ScriptExecutable_IfStatement statement){
		if(this.m_elseStatement!=null){this.m_elseStatement.setElseStatement(statement);
		}else{this.m_elseStatement=statement;}
	}
	// Returnable implementation
	public boolean shouldReturn(){return this.m_shouldReturn;}
	public ScriptValue_Abstract getReturnValue()throws Exception_Nodeable{
		if(this.m_returnValue!=null){this.m_returnValue=this.m_returnValue.getValue();}
		return this.m_returnValue;
	}
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable{
		assert LegacyDebugger.open("If-Statement Executions","Executing If-Statements");
		if(((ScriptValue_Boolean)this.m_testingValue.getValue()).getBooleanValue()){
			getEnvironment().advanceNestedStack();
			for(ScriptExecutable exec:this.m_expressions){
				exec.execute();
				if(exec instanceof Returnable&&((Returnable)exec).shouldReturn()){
					this.m_returnValue=((Returnable)exec).getReturnValue();
					this.m_shouldReturn=true;
					assert LegacyDebugger.close();
					return null;
				}
			}
			getEnvironment().retreatNestedStack();
		}else{
			if(this.m_elseStatement!=null){this.m_elseStatement.execute();}
		}
		assert LegacyDebugger.close();
		return null;
	}
	// Nodeable implementation
	@Override
    public boolean nodificate(){
		assert LegacyDebugger.open("Script If-Statement");
		assert LegacyDebugger.addSnapNode("Testing Expression",this.m_testingValue);
		assert LegacyDebugger.addSnapNode("Expressions",this.m_expressions);
		assert LegacyDebugger.addSnapNode("Else statement",this.m_elseStatement);
		assert LegacyDebugger.close();
		return true;
	}
}
