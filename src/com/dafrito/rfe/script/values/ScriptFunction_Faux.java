package com.dafrito.rfe.script.values;

import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.proxies.FauxTemplate;

public class ScriptFunction_Faux extends RiffScriptFunction implements ScriptFunction, Nodeable {
	private FauxTemplate fauxTemplate;
	private ScriptTemplate_Abstract object;
	private String name;

	public ScriptFunction_Faux(FauxTemplate template, String name, ScriptValueType type, List<ScriptValue> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) {
		super(type, params, permission, isAbstract, isStatic);
		this.fauxTemplate = template;
		this.name = name;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws ScriptException {
		throw new UnsupportedOperationException("Invalid call in faux function");
	}

	@Override
	public void addExpressions(List<ScriptExecutable> list) throws ScriptException {
		throw new UnsupportedOperationException("Invalid call in faux function");
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> params) throws ScriptException {
		if (this.name.equals("")) {
			this.setReturnValue(ref, this.fauxTemplate.execute(ref, this.name, params, null));
		} else {
			if (this.object == null) {
				this.object = this.fauxTemplate;
			}
			this.setReturnValue(ref, this.fauxTemplate.execute(ref, this.name, params, this.object));
		}
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Faux Script-Function (" + RiffScriptFunction.getDisplayableFunctionName(this.name) + ")");
		super.nodificate();
		assert Debugger.addNode("Faux Template Type: " + this.fauxTemplate.getType());
		assert Debugger.closeNode();
	}

	public void setFauxTemplate(ScriptTemplate_Abstract template) {
		assert Debugger.openNode("Faux Function Referenced-Template Changes", "Changing Faux-Function Object");
		assert Debugger.addNode(this);
		assert Debugger.addSnapNode("New object", template);
		this.fauxTemplate = (FauxTemplate) template;
		assert Debugger.closeNode();
	}

	public void setTemplate(ScriptTemplate_Abstract template) {
		assert Debugger.openNode("Faux Function Object Changes", "Changing Object");
		assert Debugger.addNode(this);
		assert Debugger.addSnapNode("New object", template);
		this.object = template;
		assert Debugger.closeNode();
	}
}
