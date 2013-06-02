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
public class BufferedTreeLogTests {

	private BufferedTreeLog<String> log;
	private TreeBuildingTreeLog<String> treeLog;

	@Before
	public void setUp() {
		log = new BufferedTreeLog<String>();
		treeLog = new TreeBuildingTreeLog<String>("Root");
	}

	@Test
	public void bufferedLogTests() throws Exception {
		assertEquals(1, 1);

		log.setSink(treeLog);

		log.enter(new LogMessage<String>("Group1", null));
		log.log(new LogMessage<String>("A"));
		log.leave();

		log.flush();

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeLog.getModel().getRoot();
		assertEquals(1, root.getChildCount());

		log.flush();

		assertEquals(1, root.getChildCount());

		DefaultMutableTreeNode group1 = (DefaultMutableTreeNode) root.getChildAt(0);
		assertEquals("Group1", group1.getUserObject());
		assertEquals(1, group1.getChildCount());
		assertEquals(true, group1.getChildAt(0).isLeaf());

		log.enter(new LogMessage<String>("Group2", null));
		log.log(new LogMessage<String>("A"));
		log.leave();

		log.flush();

		assertEquals(2, root.getChildCount());

		group1 = (DefaultMutableTreeNode) root.getChildAt(0);
		assertEquals("Group1", group1.getUserObject());
		assertEquals(1, group1.getChildCount());
		assertEquals(true, group1.getChildAt(0).isLeaf());

		DefaultMutableTreeNode group2 = (DefaultMutableTreeNode) root.getChildAt(1);
		assertEquals("Group2", group2.getUserObject());
		assertEquals(1, group2.getChildCount());
		assertEquals(true, group2.getChildAt(0).isLeaf());
	}
}
