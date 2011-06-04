package com.dafrito.rfe.script.parsing;

import java.util.List;

import com.dafrito.rfe.gui.debug.CommonString;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;

public class ScriptGroup extends ScriptElement implements Nodeable {
	public enum GroupType {
		curly, parenthetical
	}

	protected List<Object> elements;
	private GroupType type;

	public ScriptGroup(Referenced ref, List<Object> elements, GroupType type) {
		super(ref);
		this.elements = elements;
		this.type = type;
	}

	public List<Object> getElements() {
		return this.elements;
	}

	public GroupType getType() {
		return this.type;
	}

	@Override
	public void nodificate() {
		switch (this.type) {
		case curly:
			assert Debugger.openNode(CommonString.SCRIPTGROUPCURLY);
			break;
		case parenthetical:
			assert Debugger.openNode(CommonString.SCRIPTGROUPPARENTHETICAL);
			break;
		default:
			throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.addSnapNode(CommonString.ELEMENTS, this.elements);
		assert Debugger.closeNode();
	}

	public void setElements(List<Object> list) {
		this.elements = list;
	}
}
