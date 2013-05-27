/**
 * 
 */
package com.dafrito.rfe.inspect;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.gui.debug.TreeBuildingInspector;

/**
 * An {@link Inspector} that records nodes using an underlying
 * {@link TreeBuildingInspector}. This class bridges the modern
 * {@link Inspectable} API in order to remove the older {@link Nodeable}
 * framework.
 * 
 * @author Aaron Faanes
 * 
 */
public class NodeableInspector implements Inspector<Object> {

	private final TreeBuildingInspector builder;

	private boolean frozen = false;

	private NodeableInspector child;

	/**
	 * Create a new {@link NodeableInspector} using the specified tree-builder.
	 * 
	 * @param builder
	 *            the underlying builder. It's not used completely yet.
	 */
	public NodeableInspector(TreeBuildingInspector builder) {
		if (builder == null) {
			throw new NullPointerException("debugger must not be null");
		}
		this.builder = builder;
	}

	@Override
	public void field(String name, Object value) {
		this.ensureUnfrozen();
		this.closeChild();
		if (value instanceof Integer) {
			// XXX This is a hack since we use Integer values as keys
			// to cached entries. Since Inspector doesn't know anything about
			// caching, we force the value to a string here.
			//
			// Once we get caching sorted out, we can remove this code.
			value = value.toString();
		}
		assert Debugger.addSnapNode(name, value);
	}

	@Override
	public void value(Object value) {
		this.ensureUnfrozen();
		this.closeChild();
		assert Debugger.addNode(value);
	}

	@Override
	public Inspector<Object> group(String groupName) {
		this.ensureUnfrozen();
		this.closeChild();
		this.child = new NodeableInspector(this.builder);
		assert Debugger.openNode(groupName);
		return this.child;
	}

	@Override
	public void comment(String note) {
		this.ensureUnfrozen();
		this.closeChild();
		assert Debugger.addNode(note);
	}

	private void ensureUnfrozen() {
		if (this.frozen) {
			throw new IllegalStateException("NodeableInspector cannot be modified since it is frozen");
		}
	}

	@Override
	public void close() {
		if (this.frozen) {
			return;
		}
		this.closeChild();
		this.frozen = true;
	}

	private void closeChild() {
		if (this.child == null) {
			return;
		}
		this.builder.closeNode();
		this.child.close();
		this.child = null;
	}

}
