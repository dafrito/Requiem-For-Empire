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

import static org.junit.Assert.*;

import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Aaron Faanes
 * 
 */
public class TreeBuildingTreeLogTests {

	private TreeBuildingTreeLog<String> log;

	@Before
	public void setUp() {
		log = new TreeBuildingTreeLog<String>("Root");
	}

	private DefaultMutableTreeNode getRoot() {
		assertNotNull(log.getModel());
		assertNotNull(log.getModel().getRoot());
		assertTrue(log.getModel().getRoot() instanceof DefaultMutableTreeNode);
		return (DefaultMutableTreeNode) log.getModel().getRoot();
	}

	private DefaultMutableTreeNode getLastChild() {
		assertTrue(!getRoot().isLeaf());
		return (DefaultMutableTreeNode) getRoot().getLastChild();
	}

	@Test
	public void testEmptyLog() {
		assertEquals("Root", getRoot().getUserObject());
		assertEquals(0, getRoot().getDepth());
	}

	@Test
	public void testLogOneMessage() {
		log.log(new LogMessage<String>("Hello"));
		assertEquals(1, getRoot().getDepth());
		assertEquals(1, getRoot().getChildCount());

		log.log(new LogMessage<String>("Hello Again!"));
		assertEquals(1, getRoot().getDepth());
		assertEquals(2, getRoot().getChildCount());

		log.enter("Scope", null);
		assertEquals(1, getRoot().getDepth());
		assertEquals(3, getRoot().getChildCount());

		log.leave();
		assertEquals(getRoot(), log.getCursor());
		assertEquals(1, getRoot().getDepth());
		assertEquals(3, getRoot().getChildCount());
	}

	@Test
	public void moreMerging() throws Exception {
		log.enter("A", "Group");
		log.log(new LogMessage<String>("Message"));
		log.leave();

		assertEquals("A", getLastChild().getUserObject());
		assertEquals(1, getRoot().getChildCount());
		assertEquals(2, getRoot().getDepth());

		log.enter("B", "Group");
		log.log(new LogMessage<String>("Message"));
		log.leave();

		assertEquals("Group", getLastChild().getUserObject());
		assertEquals(1, getRoot().getChildCount());
		assertEquals(3, getRoot().getDepth());

		assertEquals(getRoot(), log.getCursor());

		log.enter("C", null);
		log.log(new LogMessage<String>("Message"));
		log.leave();

		assertEquals(2, getRoot().getChildCount());
		assertEquals(3, getRoot().getDepth());

		assertEquals("C", getLastChild().getUserObject());
	}

	@Test
	public void equalScopeGroupsAreMerged() throws Exception {
		log.enter("A", "GroupA");
		log.log(new LogMessage<String>("Message"));
		log.leave();

		log.enter("B", "GroupA");
		log.log(new LogMessage<String>("Message"));
		log.leave();

		log.enter("C", "GroupB");
		log.log(new LogMessage<String>("Message"));
		log.leave();

		log.enter("D", "GroupB");
		log.log(new LogMessage<String>("Message"));
		log.leave();

		assertEquals(2, getRoot().getChildCount());
		assertEquals(3, getRoot().getDepth());

		log.log(new LogMessage<String>("E"));

		assertEquals(3, getRoot().getChildCount());
		assertEquals(3, getRoot().getDepth());
	}

}
