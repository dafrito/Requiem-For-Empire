package com.dafrito.rfe;
import javax.swing.tree.TreePath;

public class NamedTreePath extends TreePath {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5723264748755821508L;
	private String name;

	public NamedTreePath(String name, Object[] array) {
		super(array);
		this.name = name;
	}

	public NamedTreePath(String name, TreePath path) {
		super(path.getPath());
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
