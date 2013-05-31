/**
 * 
 */
package com.dafrito.rfe.script.parsing.tokens;

/**
 * @author Aaron Faanes
 * 
 */
public interface Cursor {

	public void add(Object... objects);

	public void replace(Object... objects);

	public void remove();
}
