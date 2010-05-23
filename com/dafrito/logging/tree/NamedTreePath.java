package com.dafrito.logging.tree;

import javax.swing.tree.TreePath;

public class NamedTreePath extends TreePath {
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
