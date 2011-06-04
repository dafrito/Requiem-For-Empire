package com.dafrito.rfe.script.operations;

import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptElement;
import com.dafrito.rfe.script.values.Returnable;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValue_Boolean;

public class ScriptExecutable_IfStatement extends ScriptElement implements ScriptExecutable, Returnable, Nodeable {
	private ScriptValue testingValue;
	private List<ScriptExecutable> expressions;
	private ScriptExecutable_IfStatement elseStatement;
	private boolean shouldReturn = false;
	private ScriptValue returnValue;

	public ScriptExecutable_IfStatement(Referenced ref, ScriptValue test, List<ScriptExecutable> list) {
		super(ref);
		this.testingValue = test;
		this.expressions = list;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute() throws Exception_Nodeable {
		assert Debugger.openNode("If-Statement Executions", "Executing If-Statements");
		if (((ScriptValue_Boolean) this.testingValue.getValue()).getBooleanValue()) {
			this.getEnvironment().advanceNestedStack();
			for (ScriptExecutable exec : this.expressions) {
				exec.execute();
				if (exec instanceof Returnable && ((Returnable) exec).shouldReturn()) {
					this.returnValue = ((Returnable) exec).getReturnValue();
					this.shouldReturn = true;
					assert Debugger.closeNode();
					return null;
				}
			}
			this.getEnvironment().retreatNestedStack();
		} else {
			if (this.elseStatement != null) {
				this.elseStatement.execute();
			}
		}
		assert Debugger.closeNode();
		return null;
	}

	@Override
	public ScriptValue getReturnValue() throws Exception_Nodeable {
		if (this.returnValue != null) {
			this.returnValue = this.returnValue.getValue();
		}
		return this.returnValue;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Script If-Statement");
		assert Debugger.addSnapNode("Testing Expression", this.testingValue);
		assert Debugger.addSnapNode("Expressions", this.expressions);
		assert Debugger.addSnapNode("Else statement", this.elseStatement);
		assert Debugger.closeNode();
	}

	public void setElseStatement(ScriptExecutable_IfStatement statement) {
		if (this.elseStatement != null) {
			this.elseStatement.setElseStatement(statement);
		} else {
			this.elseStatement = statement;
		}
	}

	// Returnable implementation
	@Override
	public boolean shouldReturn() {
		return this.shouldReturn;
	}
}
