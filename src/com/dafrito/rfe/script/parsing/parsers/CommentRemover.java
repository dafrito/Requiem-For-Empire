/**
 * 
 */
package com.dafrito.rfe.script.parsing.parsers;

import java.util.Iterator;
import java.util.List;

import com.dafrito.rfe.script.parsing.ScriptLine;

public class CommentRemover {

	private static enum State {
		NORMAL, BLOCK_COMMENT
	}

	private CommentRemover.State state = State.NORMAL;

	private String removeSingleLineParagraphs(String string) {
		int beginParagraph = string.indexOf("/*");
		int endParagraph = string.indexOf("*/");
		if (beginParagraph != -1 && endParagraph != -1) {
			String newString = string.substring(0, beginParagraph) + string.substring(endParagraph + "*/".length());
			return newString;
		}
		return string;
	}

	private boolean processLine(ScriptLine scriptLine) {
		if (state == State.BLOCK_COMMENT) {
			int endComment = scriptLine.getString().indexOf("*/");
			if (endComment != -1) {
				scriptLine.setString(scriptLine.getString().substring(endComment + "*/".length()));
				state = State.NORMAL;
			} else {
				return false;
			}
		}
		int oldStringLength = 0;
		do {
			oldStringLength = scriptLine.getString().length();
			scriptLine.setString(removeSingleLineParagraphs(scriptLine.getString()));
		} while (oldStringLength != scriptLine.getString().length());
		int beginParagraph = scriptLine.getString().indexOf("/*");
		int lineComment = scriptLine.getString().indexOf("//");
		if (lineComment != -1 && beginParagraph != -1) {
			if (lineComment < beginParagraph) {
				scriptLine.setString(scriptLine.getString().substring(0, lineComment));
			} else {
				beginGroupComment(scriptLine, beginParagraph);
			}
		} else if (lineComment != -1) {
			scriptLine.setString(scriptLine.getString().substring(0, lineComment));
		} else if (beginParagraph != -1) {
			beginGroupComment(scriptLine, beginParagraph);
		}
		return true;
	}

	public void apply(List<Object> strings) {
		Iterator<Object> iter = strings.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (!(element instanceof ScriptLine)) {
				if (state == State.BLOCK_COMMENT) {
					iter.remove();
				}
				continue;
			}
			if (!this.processLine((ScriptLine) element)) {
				iter.remove();
			}
		}
	}

	private void beginGroupComment(ScriptLine scriptLine, int beginParagraph) {
		state = State.BLOCK_COMMENT;
		int endComment = scriptLine.getString().indexOf("*/");
		if (endComment != -1) {
			scriptLine.setString(scriptLine.getString().substring(0, beginParagraph) + scriptLine.getString().substring(endComment + "*/".length()));
			state = State.NORMAL;
		} else {
			scriptLine.setString(scriptLine.getString().substring(0, beginParagraph));
		}
	}
}