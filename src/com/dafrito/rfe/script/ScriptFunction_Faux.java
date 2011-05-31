package com.dafrito.rfe.script;

import java.util.Collection;
import java.util.List;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.proxies.FauxTemplate;

public class ScriptFunction_Faux extends ScriptFunction implements ScriptFunction_Abstract, Nodeable {
	private FauxTemplate fauxTemplate;
	private ScriptTemplate_Abstract object;
	private String name;

	public ScriptFunction_Faux(FauxTemplate template, String name, ScriptValueType type, List<ScriptValue> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) {
		super(type, params, permission, isAbstract, isStatic);
		this.fauxTemplate = template;
		this.name = name;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
		throw new Exception_InternalError("Invalid call in faux function");
	}

	@Override
	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
		throw new Exception_InternalError("Invalid call in faux function");
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue> params) throws Exception_Nodeable {
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
		assert Debugger.openNode("Faux Script-Function (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")");
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
