/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.values.ScriptValue_Variable;

class VariableStack implements Nodeable {
	private Deque<Map<String, ScriptValue_Variable>> nestedStacks = new ArrayDeque<Map<String, ScriptValue_Variable>>();

	public VariableStack() {
		this.nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
	}

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		this.nestedStacks.peek().put(name, variable);
	}

	public synchronized void advanceNestedStack() {
		assert Logs.openNode("Stack Advancements and Retreats", "Advancing Nested Stack (Nested stack size before advance: " + this.nestedStacks.size() + ")");
		assert Logs.addNode(this);
		this.nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
		assert Logs.closeNode();
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
		assert Logs.openNode("Variable Stack");
		assert Logs.openNode("Nested Stacks (" + this.nestedStacks.size() + " stack(s))");
		for (Map<String, ScriptValue_Variable> map : this.nestedStacks) {
			assert Logs.openNode("Stack (" + map.size() + " variable(s))");
			assert Logs.addNode(map);
			assert Logs.closeNode();
		}
		assert Logs.closeNode();
		assert Logs.closeNode();
	}

	public synchronized void retreatNestedStack() {
		assert this.nestedStacks.size() > 0;
		assert Logs.openNode("Stack Advancements and Retreats", "Retreating Nested Stack (Nested stack size before retreat: " + this.nestedStacks.size() + ")");
		assert Logs.addNode(this);
		this.nestedStacks.pop();
		assert Logs.closeNode();
	}
}