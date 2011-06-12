package com.dafrito.rfe.script.parsing;

import java.util.List;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.parsing.tokens.RiffToken;
import com.dafrito.rfe.script.parsing.tokens.RiffTokenVisitor;

@Inspectable
public class ScriptGroup extends ScriptElement implements RiffToken {
	protected List<Object> elements;
	private CharacterGroup type;

	public ScriptGroup(Referenced ref, List<Object> elements, CharacterGroup type) {
		super(ref);
		this.elements = elements;
		this.type = type;
	}

	@Inspectable
	public CharacterGroup getType() {
		return this.type;
	}

	@Inspectable
	public List<Object> getElements() {
		return this.elements;
	}

	public void setElements(List<Object> list) {
		this.elements = list;
	}

	@Override
	public void accept(RiffTokenVisitor visitor) {
		visitor.visitGroup(this);
	}
}
