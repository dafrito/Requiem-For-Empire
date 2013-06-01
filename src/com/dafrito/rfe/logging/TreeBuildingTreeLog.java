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

import javax.swing.SwingUtilities;
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

	private DefaultTreeModel model = new DefaultTreeModel(root);

	private DefaultMutableTreeNode cursor;

	private int level = 0;

	private Inserter rootInserter;
	private List<Inserter> inserterStack = new ArrayList<>();

	public TreeBuildingTreeLog(String name, Inserter rootInserter) {
		root.setUserObject(name);

		cursor = root;

		this.rootInserter = rootInserter;
	}

	public TreeBuildingTreeLog(String name) {
		this(name, new MergingInserter());
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

	public DefaultTreeModel getModel() {
		return model;
	}

	public void addNodeAtCursor(DefaultMutableTreeNode node) {
		model.insertNodeInto(node, cursor, cursor.getChildCount());
	}

	@Override
	public void log(LogMessage<? extends T> message) {
		addNodeAtCursor(newNode(message));
	}

	@Override
	public void enter(String scope, String scopeGroup) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(scope);

		if (level >= inserterStack.size()) {
			assert level == inserterStack.size() : "Level must never be more than one ahead of the inserter stack";
			inserterStack.add(newInserter());
		}

		cursor = inserterStack.get(level++).enter(this, child, scopeGroup);
		assert cursor != null : "Cursor must never be null";
	}

	@Override
	public void leave() {
		if (cursor == root) {
			throw new IllegalStateException("leave() must never be called when at root");
		}
		assert level >= 0 : "Level must be at least one before leaving";
		cursor = inserterStack.get(--level).leave(this);
		assert cursor != null : "Cursor must never be null (it's possible the cursor tried to advance past the root";
	}

	public int getLevel() {
		return level;
	}

	public DefaultMutableTreeNode getCursor() {
		return cursor;
	}

	private Inserter newInserter() {
		return rootInserter.newInserter();
	}
}

interface Inserter {

	public Inserter newInserter();

	public DefaultMutableTreeNode enter(TreeBuildingTreeLog<?> treeLog, DefaultMutableTreeNode child, String scopeGroup);

	public DefaultMutableTreeNode leave(TreeBuildingTreeLog<?> treeLog);
}

class DefaultInserter implements Inserter {

	@Override
	public Inserter newInserter() {
		return new DefaultInserter();
	}

	@Override
	public DefaultMutableTreeNode enter(TreeBuildingTreeLog<?> treeLog, DefaultMutableTreeNode child, String scopeGroup) {
		treeLog.addNodeAtCursor(child);
		return child;
	}

	@Override
	public DefaultMutableTreeNode leave(TreeBuildingTreeLog<?> treeLog) {
		return (DefaultMutableTreeNode) treeLog.getCursor().getParent();
	}
}

class MergingInserter implements Inserter {
	private String scopeGroup;
	private boolean merged;

	@Override
	public DefaultMutableTreeNode enter(TreeBuildingTreeLog<?> treeLog, DefaultMutableTreeNode child, String scopeGroup) {
		DefaultMutableTreeNode cursor = treeLog.getCursor();
		if (scopeGroup == null || this.scopeGroup != scopeGroup || cursor.isLeaf()) {
			// No match, so invalidate ourselves.
			this.scopeGroup = scopeGroup;
			merged = false;
			treeLog.addNodeAtCursor(child);
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
			treeLog.getModel().removeNodeFromParent(lastChild);
			scopeGroupNode.add(lastChild);
			scopeGroupNode.add(child);
			treeLog.addNodeAtCursor(scopeGroupNode);
		}
		return child;
	}

	@Override
	public DefaultMutableTreeNode leave(TreeBuildingTreeLog<?> treeLog) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeLog.getCursor().getParent();
		if (hasMerged()) {
			assert parent != null : "A merged scope group must always have its merged node as its parent";
			return (DefaultMutableTreeNode) parent.getParent();
		}
		return parent;
	}

	@Override
	public Inserter newInserter() {
		return new MergingInserter();
	}

	public boolean hasMerged() {
		return this.merged;
	}
}
