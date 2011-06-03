/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

class VariableStack implements Nodeable {
	private Stack<Map<String, ScriptValue_Variable>> nestedStacks = new Stack<Map<String, ScriptValue_Variable>>();

	public VariableStack() {
		this.nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
	}

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		this.nestedStacks.peek().put(name, variable);
	}

	public synchronized void advanceNestedStack() {
		assert Debugger.openNode("Stack Advancements and Retreats", "Advancing Nested Stack (Nested stack size before advance: " + this.nestedStacks.size() + ")");
		assert Debugger.addNode(this);
		this.nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
		assert Debugger.closeNode();
	}

	public synchronized ScriptValue_Variable getVariableFromStack(String name) {
		for (Map<String, ScriptValue_Variable> map : this.nestedStacks) {
			if (map.containsKey(name)) {
				return map.get(name);
			}
		}
		return null;
	}

	@Override
	public synchronized void nodificate() {
		assert Debugger.openNode("Variable Stack");
		assert Debugger.openNode("Nested Stacks (" + this.nestedStacks.size() + " stack(s))");
		for (Map<String, ScriptValue_Variable> map : this.nestedStacks) {
			assert Debugger.openNode("Stack (" + map.size() + " variable(s))");
			assert Debugger.addNode(map);
			assert Debugger.closeNode();
		}
		assert Debugger.closeNode();
		assert Debugger.closeNode();
	}

	public synchronized void retreatNestedStack() {
		assert this.nestedStacks.size() > 0;
		assert Debugger.openNode("Stack Advancements and Retreats", "Retreating Nested Stack (Nested stack size before retreat: " + this.nestedStacks.size() + ")");
		assert Debugger.addNode(this);
		this.nestedStacks.pop();
		assert Debugger.closeNode();
	}
}