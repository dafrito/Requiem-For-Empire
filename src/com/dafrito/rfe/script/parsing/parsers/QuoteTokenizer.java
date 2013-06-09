/**
 * 
 */
package com.dafrito.rfe.script.parsing.parsers;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.script.exceptions.UnenclosedStringLiteralException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.ScriptLine;
import com.dafrito.rfe.script.values.ScriptValue_String;

public class QuoteTokenizer {

	public List<Object> apply(List<Object> lineList) throws ScriptException {
		for (int i = 0; i < lineList.size(); i++) {
			if (!(lineList.get(i) instanceof ScriptLine)) {
				continue;
			}
			List<Object> returnedList = createQuotedElements((ScriptLine) lineList.get(i));
			lineList.remove(i);
			lineList.addAll(i, returnedList);
		}
		return lineList;
	}

	private List<Object> createQuotedElements(ScriptLine line) throws ScriptException {
		int charElem = line.getString().indexOf("'");
		int stringElem = line.getString().indexOf('"');
		List<Object> list = new LinkedList<Object>();
		// If neither are found, return.
		if (charElem == -1 && stringElem == -1) {
			list.add(line);
			return list;
		} else if ((charElem == -1 || stringElem < charElem) && stringElem != -1) {
			// We've found a string element
			assert stringElem != -1;
			int offset = stringElem + 1;
			int nextStringElem;
			do {
				nextStringElem = line.getString().indexOf('"', offset);
				// If it's not found, throw an error.
				if (nextStringElem == -1) {
					throw new UnenclosedStringLiteralException(line);
				}
				// If we enter this, we're at a literal quotation mark inside our string, and must loop to find the actual closing mark.
				if (nextStringElem != 0 && '\\' == line.getString().charAt(nextStringElem - 1)) {
					offset = nextStringElem + 1;
					nextStringElem = -1;
				}
			} while (nextStringElem == -1);
			list.add(new ScriptLine(line.getString().substring(0, stringElem), line, 0));
			String value = line.getString().substring(stringElem + "\"".length(), nextStringElem);
			list.add(new ScriptValue_String(line.getEnvironment(), value));
			list.add(new ScriptLine(line.getString().substring(nextStringElem + "\"".length()), line, (short) (nextStringElem + "\"".length())));
			return this.apply(list);
		} else {
			// We found a character-string element
			assert charElem != -1;
			int nextCharElem = line.getString().indexOf("'", charElem + 1);
			if (nextCharElem == -1) {
				throw new UnenclosedStringLiteralException(line);
			}
			list.add(new ScriptLine(line.getString().substring(0, charElem), line, 0));
			String value = line.getString().substring(charElem + "'".length(), nextCharElem);
			list.add(new ScriptValue_String(line.getEnvironment(), value));
			list.add(new ScriptLine(line.getString().substring(nextCharElem + "'".length()), line, (short) (nextCharElem + "'".length())));
			return this.apply(list);
		}
	}

}