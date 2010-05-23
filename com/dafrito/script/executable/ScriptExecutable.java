package com.dafrito.script.executable;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValue_Abstract;

public interface ScriptExecutable{
	// ScriptExecutable implementation
	public ScriptValue_Abstract execute()throws Exception_Nodeable;
	public ScriptElement getDebugReference();
}
