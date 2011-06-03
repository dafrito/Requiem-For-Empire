/**
 * 
 */
package com.dafrito.rfe.script;

import java.util.Stack;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.values.ScriptFunction_Abstract;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue_Variable;

class ThreadStack implements Nodeable {
	private VariableTable variableTable = new VariableTable();
	private Stack<ScriptTemplate_Abstract> objectStack = new Stack<ScriptTemplate_Abstract>(); // Stack of called objects
	private Stack<ScriptFunction_Abstract> functionStack = new Stack<ScriptFunction_Abstract>(); // Stack of called functions

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		if (variable == null) {
			assert Debugger.openNode("Undefined Variable Stack Additions", "Adding Undefined Variable to the Stack (" + name + ")");
		} else {
			assert Debugger.openNode("Variable Stack Additions", "Adding Variable to the Stack (" + name + ")");
			assert Debugger.addNode(variable);
		}
		assert Debugger.addNode(this);
		this.variableTable.addVariable(name, variable);
		assert Debugger.closeNode();
	}

	public synchronized void advanceNestedStack() {
		this.variableTable.advanceNestedStack();
	}

	public synchronized void advanceStack(ScriptTemplate_Abstract template, ScriptFunction_Abstract fxn) throws Exception_Nodeable {
		assert Debugger.openNode("Stack Advancements and Retreats", "Advancing Stack (Stack size before advance: " + this.functionStack.size() + ")");
		assert (this.functionStack.size() == this.objectStack.size()) && (this.objectStack.size() == this.variableTable.getStacks().size()) : "Stacks unequal: Function-stack: " + this.functionStack.size() + " Object-stack: " + this.objectStack.size() + " Variable-stack: " + this.variableTable.getStacks().size();
		if (template != null) {
			assert Debugger.addSnapNode("Advancing object", template);
		}
		assert Debugger.addSnapNode("Advancing function", fxn);
		if (template == null) {
			template = this.getCurrentObject();
		}
		if (template != null) {
			this.objectStack.push((ScriptTemplate_Abstract) template.getValue());
		} else {
			this.objectStack.push(null);
		}
		this.functionStack.push(fxn);
		this.variableTable.advanceStack();
		assert Debugger.closeNode();
	}

	public synchronized ScriptFunction_Abstract getCurrentFunction() {
		if (this.functionStack.size() == 0) {
			throw new Exception_InternalError("No call stack");
		}
		return this.functionStack.peek();
	}

	public synchronized ScriptTemplate_Abstract getCurrentObject() {
		if (this.objectStack.size() == 0) {
			throw new Exception_InternalError("No call stack");
		}
		return this.objectStack.peek();
	}

	public ScriptValue_Variable getVariableFromStack(String name) {
		return this.variableTable.getVariableFromStack(name);
	}

	@Override
	public synchronized void nodificate() {
		assert Debugger.openNode("Thread Stack");
		assert Debugger.addNode(this.variableTable);
		assert Debugger.addSnapNode("Object stack (" + this.objectStack.size() + ")", this.objectStack);
		assert Debugger.addSnapNode("Function stack (" + this.functionStack.size() + ")", this.functionStack);
		assert Debugger.closeNode();
	}

	public synchronized void retreatNestedStack() {
		this.variableTable.retreatNestedStack();
	}

	public synchronized void retreatStack() {
		assert Debugger.openNode("Stack Advancements and Retreats", "Retreating Stack (Stack size before retreat: " + this.functionStack.size() + ")");
		assert (this.functionStack.size() == this.objectStack.size()) && (this.objectStack.size() == this.variableTable.getStacks().size()) : "Stacks unequal: Function-stack: " + this.functionStack.size() + " Object-stack: " + this.objectStack.size() + " Variable-stack: " + this.variableTable.getStacks().size();
		if (this.variableTable.getStacks().size() > 0) {
			this.variableTable.retreatStack();
		}
		if (this.objectStack.size() > 0) {
			this.objectStack.pop();
			if (this.objectStack.size() > 0) {
				assert Debugger.addSnapNode("New Current Object", this.objectStack.peek());
			}
		}
		if (this.functionStack.size() > 0) {
			this.functionStack.pop();
			if (this.functionStack.size() > 0) {
				assert Debugger.addSnapNode("New Current Function", this.functionStack.peek());
			}
		}
		assert Debugger.closeNode();
	}
}