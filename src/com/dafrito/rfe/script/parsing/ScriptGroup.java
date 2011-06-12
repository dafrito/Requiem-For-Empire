package com.dafrito.rfe.script.parsing;

import java.util.List;

import com.dafrito.rfe.gui.debug.CommonString;
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;

public class ScriptGroup extends ScriptElement implements Nodeable {
	protected List<Object> elements;
	private CharacterGroup type;

	public ScriptGroup(Referenced ref, List<Object> elements, CharacterGroup type) {
		super(ref);
		this.elements = elements;
		this.type = type;
	}

	public List<Object> getElements() {
		return this.elements;
	}

	public CharacterGroup getType() {
		return this.type;
	}

	@Override
	public void nodificate() {
		switch (this.type) {
		case CURLY_BRACES:
			assert Debugger.openNode(CommonString.SCRIPTGROUPCURLY);
			break;
		case PARENTHESES:
			assert Debugger.openNode(CommonString.SCRIPTGROUPPARENTHETICAL);
			break;
		default:
			throw new AssertionError("Unsupported group");
		}
		assert Debugger.addSnapNode(CommonString.ELEMENTS, this.elements);
		assert Debugger.closeNode();
	}

	public void setElements(List<Object> list) {
		this.elements = list;
	}
}
