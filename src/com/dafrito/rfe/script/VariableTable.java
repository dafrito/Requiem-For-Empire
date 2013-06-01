/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.ArrayDeque;
import java.util.Deque;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.values.ScriptValue_Variable;

class VariableTable implements Nodeable {
	Deque<VariableStack> stacks = new ArrayDeque<VariableStack>();

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		this.stacks.peek().addVariable(name, variable);
	}

	public synchronized void advanceNestedStack() {
		this.stacks.peek().advanceNestedStack();
	}

	public synchronized void advanceStack() {
		this.stacks.push(new VariableStack());
	}

	public Deque<VariableStack> getStacks() {
		return this.stacks;
	}

	public ScriptValue_Variable getVariableFromStack(String name) {
		ScriptValue_Variable variable = this.stacks.peek().getVariableFromStack(name);
		return variable;
	}

	@Override
	public synchronized void nodificate() {
		assert Logs.openNode("Variable Table");
		assert Logs.addSnapNode("Variable Stacks (" + this.stacks.size() + " stack(s))", this.stacks);
		assert Logs.closeNode();
	}

	public synchronized void retreatNestedStack() {
		this.stacks.peek().retreatNestedStack();
	}

	public synchronized void retreatStack() {
		this.stacks.pop();
	}
}