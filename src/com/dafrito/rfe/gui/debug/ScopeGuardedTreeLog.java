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

/**
 * @author Aaron Faanes
 * @param <T>
 *            the type of log message
 */
public abstract class ScopeGuardedTreeLog<T> implements TreeLog<T> {

	int levels;
	private TreeLog<? super T> sink;

	protected abstract boolean allowEntry(String scope, String scopeGroup);

	private boolean isAccepting() {
		return levels > 0;
	}

	@Override
	public void log(LogMessage<? extends T> message) {
		if (isAccepting()) {
			sink.log(message);
		}
	}

	@Override
	public void enter(String scope, String scopeGroup) {
		if (levels == 0 && !allowEntry(scope, scopeGroup)) {
			return;
		}
		++levels;
		sink.enter(scope, scopeGroup);
	}

	@Override
	public void leave() {
		sink.leave();
		--levels;
	}

	public TreeLog<? super T> getSink() {
		if (sink == null) {
			return NoopTreeLog.instance();
		}
		return sink;
	}

	public void setSink(TreeLog<? super T> sink) {
		this.sink = sink;
	}
}
