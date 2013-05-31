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
package com.dafrito.rfe.gui.debug;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * @author Aaron Faanes
 * @param <T>
 *            the type of message
 * 
 */
public class TreeBuildingTreeLog<T> implements TreeLog<T> {

	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	private TreeModel model = new DefaultTreeModel(root);

	private DefaultMutableTreeNode cursor = root;

	public TreeBuildingTreeLog(T rootMessage) {
		root.setUserObject(rootMessage);
	}

	private DefaultMutableTreeNode newNode(LogMessage<? extends T> message) {
		String messageString = "";
		if (message != null && message.getMessage() != null) {
			messageString = message.getMessage().toString();
		}
		return new DefaultMutableTreeNode(messageString);
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
		cursor.add(child);
		cursor = child;
	}

	@Override
	public void leave() {
		cursor = (DefaultMutableTreeNode) cursor.getParent();
	}

}
