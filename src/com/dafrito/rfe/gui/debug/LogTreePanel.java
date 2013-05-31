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

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * @author Aaron Faanes
 * @param <T>
 *            The type of logged message
 * 
 */
public class LogTreePanel<T> extends JPanel {

	private final JTree messages = new JTree();

	private final TreeBuildingTreeLog<T> log;

	public LogTreePanel(T name) {
		log = new TreeBuildingTreeLog<T>(name);

		messages.setModel(log.getModel());

		setLayout(new GridLayout(1, 0));
		add(new JScrollPane(messages));
	}

	public void addSource(ProxyTreeLog<? extends T> source) {
		source.addListener(log);
	}

	public void removeSource(ProxyTreeLog<? extends T> source) {
		source.removeListener(log);
	}

	private static final long serialVersionUID = -5324431255659963739L;
}