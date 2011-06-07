package com.dafrito.rfe.inspect;

/**
 * A class that can manually nodify itself.
 * <p>
 * {@link Inspectable} should be used in preference to this class, as I'm trying
 * to phase {@link Nodeable} implementations out.
 * 
 * @author Aaron Faanes
 * @see Inspectable
 */
public interface Nodeable {
	public void nodificate();
}
