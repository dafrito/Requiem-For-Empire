/**
 * 
 */
package com.dafrito.rfe.script.parsing.tokens;

import com.bluespot.parsing.TokenVisitor;
import com.dafrito.rfe.script.parsing.ScriptGroup;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.parsing.ScriptOperatorType;

/**
 * @author Aaron Faanes
 * 
 */
public interface RiffTokenVisitor extends TokenVisitor {

	public void enterContext(Cursor cursor);

	public void visitGroup(ScriptGroup group);

	public void visitKeyword(ScriptKeywordType keyword);

	public void visitOperator(ScriptOperatorType operator);

	public void exitContext(Cursor cursor);
}
