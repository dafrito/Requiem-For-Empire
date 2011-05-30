package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Nodeable;

public class ScriptOperator extends ScriptElement implements Nodeable {
	public static boolean isSemicolon(ScriptOperator op) {
		return op.getType() == ScriptOperatorType.SEMICOLON;
	}

	private final ScriptOperatorType type;

	public ScriptOperator(ScriptLine line, ScriptOperatorType type) {
		super(line);
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ScriptOperatorType) {
			return this.getType().equals(o);
		}
		if (!(o instanceof ScriptOperator)) {
			return false;
		}
		return this.getType() == ((ScriptOperator) o).getType();
	}

	public String getName() {
		return this.getType().toString();
	}

	public ScriptOperatorType getType() {
		return this.type;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode(Debugger.getString(DebugString.SCRIPTOPERATOR) + this.getName());
		super.nodificate();
		assert Debugger.closeNode();
	}

	@Override
	public String toString() {
		return this.type.toString();
	}
}
