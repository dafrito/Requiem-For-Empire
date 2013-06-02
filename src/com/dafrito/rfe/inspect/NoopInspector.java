/**
 * 
 */
package com.dafrito.rfe.inspect;

/**
 * An {@link Inspector} that does nothing.
 * 
 * @author Aaron Faanes
 * @param <T>
 *            the common supertype of inspected values
 */
public class NoopInspector<T> implements Inspector<T> {

	@Override
	public void field(T name, T value) {
	}

	@Override
	public void value(T value) {
	}

	@Override
	public Inspector<T> group(T groupName) {
		return this;
	}

	@Override
	public void comment(T note) {
	}

	@Override
	public void close() {
	}

}
