/**
 * 
 */
package com.dafrito.rfe.script.parsing.tokens;

import java.util.ArrayDeque;
import java.util.Deque;

import com.dafrito.rfe.script.parsing.ScriptGroup;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.parsing.ScriptOperatorType;

/**
 * A {@link RiffTokenVisitor} that does nothing.
 * 
 * @author Aaron Faanes
 * 
 */
public class AbstractRiffTokenVisitor implements RiffTokenVisitor {

	private final Deque<Cursor> cursors = new ArrayDeque<Cursor>();

	protected Cursor getCursor() {
		if (this.cursors.isEmpty()) {
			return NoopCursor.instance();
		}
		return this.cursors.peekLast();
	}

	@Override
	public void enterContext(Cursor cursor) {
		this.cursors.addLast(cursor);
	}

	@Override
	public void visitUnparsed(String unparsed) {
		// Do nothing
	}

	@Override
	public void visitGroup(ScriptGroup group) {
		// Do nothing
	}

	@Override
	public void visitKeyword(ScriptKeywordType keyword) {
		// Do nothing
	}

	@Override
	public void visitOperator(ScriptOperatorType operator) {
		// Do nothing
	}

	@Override
	public void exitContext(Cursor cursor) {
		this.cursors.removeLast();
	}
}
