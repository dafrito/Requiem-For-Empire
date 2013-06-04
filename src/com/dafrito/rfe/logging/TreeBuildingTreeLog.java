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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * A {@link TreeLog} that creates a {@link TreeModel}.
 * 
 * @author Aaron Faanes
 * @param <Message>
 *            the type of message
 * 
 */
public class TreeBuildingTreeLog<Message> implements TreeLog<Message> {

	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	private DefaultTreeModel model = new DefaultTreeModel(root);

	private DefaultMutableTreeNode cursor;

	private int level = 0;

	private Inserter<? super Message> rootInserter;
	private List<Inserter<? super Message>> inserterStack = new ArrayList<>();

	private Map<LogMessage<? extends Message>, DefaultMutableTreeNode> nodeMapping = new HashMap<>();

	public TreeBuildingTreeLog(String name, Inserter<? super Message> rootInserter) {
		root.setUserObject(name);

		cursor = root;

		this.rootInserter = rootInserter;
	}

	public TreeBuildingTreeLog(String name) {
		this(name, new MergingInserter<Message>());
	}

	private DefaultMutableTreeNode newNode(LogMessage<? extends Message> message) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(message);
		nodeMapping.put(message, node);
		return node;
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
	public void log(LogMessage<? extends Message> message) {
		enter(message);
		leave();
	}

	@Override
	public void enter(LogMessage<? extends Message> scope) {
		DefaultMutableTreeNode child = newNode(scope);

		if (level >= inserterStack.size()) {
			assert level == inserterStack.size() : "Level must never be more than one ahead of the inserter stack";
			inserterStack.add(newInserter());
		}

		cursor = inserterStack.get(level++).enter(this, child, scope);
		assert cursor != null : "Cursor must never be null";
	}

	@Override
	public void leave() {
		if (cursor == root) {
			// Pedantically, this is an error. However, if we forget to use an "enter" or forget to call "leave", we don't
			// crash. Instead, the log just gets confused. Having a spurious leave() be called is asymmetric relative to
			// the quiet failures of "enter" and neglected "leaves".
			log(new LogMessage<Message>("Leave was called while at the root node", null));
			return;
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

	private Inserter<? super Message> newInserter() {
		return rootInserter.newInserter();
	}

	public DefaultMutableTreeNode getNodeFor(LogMessage<? extends Message> message) {
		return nodeMapping.get(message);
	}
}

interface Inserter<Message> {

	public Inserter<? super Message> newInserter();

	public DefaultMutableTreeNode enter(TreeBuildingTreeLog<? extends Message> treeLog, DefaultMutableTreeNode scope, LogMessage<? extends Message> message);

	public DefaultMutableTreeNode leave(TreeBuildingTreeLog<? extends Message> treeLog);
}

class DefaultInserter<Message> implements Inserter<Message> {

	@Override
	public Inserter<? super Message> newInserter() {
		return new DefaultInserter<Message>();
	}

	@Override
	public DefaultMutableTreeNode enter(TreeBuildingTreeLog<? extends Message> treeLog, DefaultMutableTreeNode scope, LogMessage<? extends Message> message) {
		treeLog.addNodeAtCursor(scope);
		return scope;
	}

	@Override
	public DefaultMutableTreeNode leave(TreeBuildingTreeLog<? extends Message> treeLog) {
		DefaultMutableTreeNode cursor = treeLog.getCursor();
		if (cursor.isLeaf()) {
			cursor.setAllowsChildren(false);
		}
		treeLog.getModel().nodeStructureChanged(cursor);
		return (DefaultMutableTreeNode) cursor.getParent();
	}
}

class MergingInserter<Message> implements Inserter<Message> {
	private String scopeGroup;
	private boolean merged;

	@Override
	public DefaultMutableTreeNode enter(TreeBuildingTreeLog<? extends Message> treeLog, DefaultMutableTreeNode scope, LogMessage<? extends Message> message) {
		String scopeGroup = message.getCategory();
		DefaultMutableTreeNode cursor = treeLog.getCursor();

		if (scopeGroup == null || !scopeGroup.equals(this.scopeGroup) || cursor.isLeaf()) {
			// No match, so invalidate ourselves.
			this.scopeGroup = scopeGroup;
			merged = false;
			treeLog.addNodeAtCursor(scope);
			return scope;
		}

		DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) cursor.getLastChild();

		if (merged) {
			// we've already merged before, so the last child is our merge node.
			treeLog.getModel().insertNodeInto(scope, lastChild, lastChild.getChildCount());
		} else {
			merged = true;
			// We've never merged before, so we need to create a new merge node and add the last child to it.
			DefaultMutableTreeNode scopeGroupNode = new DefaultMutableTreeNode(message.changeSender(null, null));
			treeLog.getModel().removeNodeFromParent(lastChild);
			scopeGroupNode.add(lastChild);
			scopeGroupNode.add(scope);
			treeLog.addNodeAtCursor(scopeGroupNode);
		}
		return scope;
	}

	@Override
	public DefaultMutableTreeNode leave(TreeBuildingTreeLog<? extends Message> treeLog) {
		DefaultMutableTreeNode cursor = treeLog.getCursor();
		if (cursor.isLeaf()) {
			cursor.setAllowsChildren(false);
		}
		treeLog.getModel().nodeStructureChanged(cursor);
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) cursor.getParent();
		if (hasMerged()) {
			assert parent != null : "A merged scope group must always have its merged node as its parent";
			return (DefaultMutableTreeNode) parent.getParent();
		}
		return parent;
	}

	@Override
	public Inserter<? super Message> newInserter() {
		return new MergingInserter<Message>();
	}

	public boolean hasMerged() {
		return this.merged;
	}
}
