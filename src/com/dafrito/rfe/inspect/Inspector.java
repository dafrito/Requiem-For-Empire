package com.dafrito.rfe.inspect;

import com.dafrito.rfe.gui.debug.Debugger;

/**
 * Allows inspection of arbitrary content.
 * <p>
 * This is intended to supersede the old debug-centric inspection we wrote with
 * one that's much more flexible. Currently, this interface is pretty generic,
 * but I might make it more specific if this is warranted.
 * 
 * @author Aaron Faanes
 * @param <T>
 *            the common super-type of visited elements.
 * @see Debugger
 * @see DebugEnvironment
 */
public interface Inspector<T> {

	/**
	 * Visit a field.
	 * 
	 * @param name
	 *            the logical name of the field
	 * @param value
	 *            the value of the specified field. It may be null.
	 */
	public void field(String name, T value);

	/**
	 * Visit some value. This may be the logical content of some group, or an
	 * element in a collection.
	 * 
	 * @param value
	 *            the visited value. It may be null.
	 */
	public void value(T value);

	/**
	 * Visit a inner group. The returned {@link Inspector} must be used to
	 * insert content for the inner group.
	 * <p>
	 * Don't retain these inspectors any longer than you need them. They're not
	 * intended to be used beyond the lifespan of their parents.
	 * 
	 * @param groupName
	 *            the logical name of the group
	 * @return a {@link Inspector} that must be used to visit the content of the
	 *         group
	 */
	public Inspector<T> group(String groupName);

	/**
	 * Visit a comment. This lets inspectors ignore descriptive information,
	 * while still retaining values passed through {@link #value(Object)}.
	 * 
	 * @param note
	 *            the comment that is made
	 */
	public void comment(T note);

	/**
	 * Freeze this inspector, so any construction that's taken place can be
	 * finalized.
	 */
	public void close();

}
