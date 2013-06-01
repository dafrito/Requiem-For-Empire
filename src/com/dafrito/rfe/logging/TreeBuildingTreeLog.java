/**
 * Copyright (c) 2013 Aaron Faanes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.dafrito.rfe.logging;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * A {@link TreeLog} that creates a {@link TreeModel}.
 * 
 * @author Aaron Faanes
 * @param <T>
 *            the type of message
 * 
 */
public class TreeBuildingTreeLog<T> implements TreeLog<T> {

	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	private TreeModel model = new DefaultTreeModel(root);

	private DefaultMutableTreeNode cursor;

	private int level = 0;

	private List<DefaultInserter> inserterStack = new ArrayList<>();

	public TreeBuildingTreeLog(String name) {
		root.setUserObject(name);

		cursor = root;
		System.out.println(cursor);

		// Prime the inserter stack
		inserterStack.add(new DefaultInserter());
	}

	private DefaultMutableTreeNode newNode(LogMessage<? extends T> message) {
		String messageString = "";
		if (message != null && message.getMessage() != null) {
			messageString = message.getMessage().toString();
		}
		return new DefaultMutableTreeNode(messageString);
	}

	public String getName() {
		return (String) root.getUserObject();
	}

	public TreeModel getModel() {
		return model;
	}

	@Override
	public void log(LogMessage<? extends T> message) {
		cursor.add(newNode(message));
	}

	@Override
	public void enter(String scope, String scopeGroup) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(scope);
		cursor = inserterStack.get(level++).enter(cursor, child, scopeGroup);
		assert cursor != null : "Cursor must never be null";
		if (level >= inserterStack.size()) {
			assert level == inserterStack.size() : "Level must never be more than one ahead of the inserter stack";
			inserterStack.add(new DefaultInserter());
		}
	}

	@Override
	public void leave() {
		if (cursor == root) {
			// Do nothing. Pedantically, this is an error, but if the log is cleared, then
			// we may end up with leave()'s that are leaving branches that we've already cleared.
			// As a result, I prefer to be quiet here.
			return;
		}
		assert level >= 0 : "Level must be at least one before leaving";
		cursor = inserterStack.get(level--).leave(cursor);
		assert cursor != null : "Cursor must never be null (it's possible the cursor tried to advance past the root";
	}
}

class DefaultInserter {
	private String scopeGroup;
	private boolean merged;

	public DefaultMutableTreeNode enter(DefaultMutableTreeNode cursor, DefaultMutableTreeNode child, String scopeGroup) {
		if (scopeGroup == null || this.scopeGroup != scopeGroup || cursor.isLeaf()) {
			// No match, so invalidate ourselves.
			this.scopeGroup = scopeGroup;
			merged = false;
			cursor.add(child);
			return child;
		}
		DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) cursor.getLastChild();
		if (merged) {
			// we've already merged before, so the last child is our merge node.
			lastChild.add(child);
		} else {
			merged = true;
			// We've never merged before, so we need to create a new merge node and add the last child to it.
			DefaultMutableTreeNode scopeGroupNode = new DefaultMutableTreeNode(scopeGroup);
			scopeGroupNode.add(lastChild);
			scopeGroupNode.add(child);
		}
		return child;
	}

	public DefaultMutableTreeNode leave(DefaultMutableTreeNode cursor) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) cursor.getParent();
		if (hasMerged()) {
			assert parent != null : "A merged scope group must always have its merged node as its parent";
			return (DefaultMutableTreeNode) parent.getParent();
		}
		return parent;
	}

	public boolean hasMerged() {
		return this.merged;
	}
}
