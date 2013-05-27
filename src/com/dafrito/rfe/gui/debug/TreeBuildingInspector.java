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

import java.util.ArrayList;

public class TreeBuildingInspector {

	/**
	 * 
	 */
	private final DebugEnvironment debugEnvironment;

	TreeBuildingInspector(DebugEnvironment debugEnvironment) {
		this.debugEnvironment = debugEnvironment;
	}

	public void addNode(Debug_TreeNode node) {
		this.addNode(node, false);
	}

	public void addNode(Debug_TreeNode node, boolean setAsCurrent) {
		if (this.debugEnvironment.isIgnoringThisThread()) {
			return;
		}
		String threadName = Thread.currentThread().getName();
		if (this.debugEnvironment.filteredOutputMap.get(threadName) == null || this.debugEnvironment.filteredOutputMap.get(threadName).isEmpty()) {
			this.debugEnvironment.filteredOutputMap.put(threadName, new ArrayList<Debug_Listener>());
			this.debugEnvironment.filteredOutputMap.get(threadName).add(new Debug_Listener(threadName, this.debugEnvironment, null, threadName));
			this.debugEnvironment.filteredPanes.add(threadName, this.debugEnvironment.filteredOutputMap.get(threadName).get(0));
		}
		for (Debug_Listener listener : this.debugEnvironment.filteredOutputMap.get(Thread.currentThread().getName())) {
			if (listener.isCapturing()) {
				if (!listener.getTreePanel().getFilter().isListening()) {
					listener.getTreePanel().getFilter().sniffNode(node);
					if (listener.getTreePanel().getFilter().isListening()) {
						this.addNodeToOutput(node, setAsCurrent, listener);
					}
				} else {
					this.addNodeToOutput(node, setAsCurrent, listener);
				}
				return;
			}
		}
		for (Debug_Listener listener : this.debugEnvironment.filteredOutputMap.get(Thread.currentThread().getName())) {
			this.addNodeToOutput(node.duplicate(), setAsCurrent, listener);
		}
	}

	private void addNodeToOutput(Debug_TreeNode node, boolean setAsCurrent, Debug_Listener listener) {
		if (!listener.getTreePanel().getFilter().isListening()) {
			listener.getTreePanel().getFilter().sniffNode(node);
			if (!listener.getTreePanel().getFilter().isListening()) {
				return;
			}
			node = new Debug_TreeNode_Orphaned(listener.getSource().getTreePanel().getLastNodeAdded());
		}
		if (listener.getTreePanel().getCurrentNode().hasChildren() && listener.getTreePanel().getCurrentNode().getLastChild().getGroup() != null && node.getGroup() != null && listener.getTreePanel().getCurrentNode().getLastChild().getGroup().equals(node.getGroup())) {
			if (listener.getTreePanel().getCurrentNode().getLastChild().getData().equals(node.getGroup())) {
				listener.getTreePanel().getCurrentNode().getLastChild().addChild(node);
				node.setPracticalParent(listener.getTreePanel().getCurrentNode());
				if (setAsCurrent) {
					listener.getTreePanel().setAsCurrent(node);
				}
			} else {
				Debug_TreeNode lastNode = listener.getTreePanel().getCurrentNode().removeLastChild();
				Debug_TreeNode groupNode = new Debug_TreeNode(node.getGroupCode(), node.getGroupCode());
				groupNode.addChild(lastNode);
				groupNode.addChild(node);
				lastNode.setPracticalParent(listener.getTreePanel().getCurrentNode());
				node.setPracticalParent(listener.getTreePanel().getCurrentNode());
				listener.getTreePanel().addNode(groupNode);
				if (setAsCurrent) {
					listener.getTreePanel().setAsCurrent(node);
				}
			}
			return;
		}
		listener.getTreePanel().addNode(node);
		if (setAsCurrent) {
			listener.getTreePanel().setAsCurrent(node);
		}
	}

	public void closeNode() {
		if (this.debugEnvironment.isIgnoringThisThread()) {
			return;
		}
		for (Debug_Listener listener : this.debugEnvironment.filteredOutputMap.get(Thread.currentThread().getName())) {
			if (listener.isCapturing() && listener.getTreePanel().getFilter().isListening()) {
				listener.getTreePanel().closeNode();
				return;
			}
		}
		for (Debug_Listener listener : this.debugEnvironment.filteredOutputMap.get(Thread.currentThread().getName())) {
			if (listener.getTreePanel().getFilter().isListening()) {
				listener.getTreePanel().closeNode();
			}
		}
	}

	public Debug_TreeNode getLastNodeAdded() {
		return this.debugEnvironment.getUnfilteredOutput().getTreePanel().getLastNodeAdded();
	}

	// Node stuff
	public void openNode(Debug_TreeNode node) {
		this.addNode(node, true);
	}

}