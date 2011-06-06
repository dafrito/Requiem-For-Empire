package com.dafrito.rfe.script;

/**
 * Indicate that this object is convertible to a scriptable object. We actually
 * use this interface to indicate "convertibility" in both directions, otherwise
 * I'd change the type parameter to something more specific.
 * 
 * @author Aaron Faanes
 * 
 * @param <T>
 *            the type of the converted object
 */
public interface ScriptConvertible<T> {
	public T convert(ScriptEnvironment env);
}
