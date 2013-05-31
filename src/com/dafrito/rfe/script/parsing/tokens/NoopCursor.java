/**
 * 
 */
package com.dafrito.rfe.script.parsing.tokens;

/**
 * @author Aaron Faanes
 * 
 */
public class NoopCursor implements Cursor {

	private static final NoopCursor INSTANCE = new NoopCursor();

	public static Cursor instance() {
		return INSTANCE;
	}

	@Override
	public void add(Object... objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void replace(Object... objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

}
