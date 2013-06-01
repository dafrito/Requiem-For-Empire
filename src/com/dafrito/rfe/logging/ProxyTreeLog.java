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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A {@link TreeLog} that distpatches events to other logs.
 * 
 * @author Aaron Faanes
 * @param <T>
 *            the type of message
 * 
 */
public class ProxyTreeLog<T> implements TreeLog<T> {

	List<TreeLog<? super T>> listeners = new CopyOnWriteArrayList<>();

	public void addListener(TreeLog<? super T> log) {
		listeners.add(log);
	}

	public void removeListener(TreeLog<? super T> log) {
		listeners.remove(log);
	}

	@Override
	public void log(LogMessage<? extends T> message) {
		for (TreeLog<? super T> log : listeners) {
			log.log(message);
		}
	}

	@Override
	public void enter(String scope, String scopeGroup) {
		for (TreeLog<? super T> log : listeners) {
			log.enter(scope, scopeGroup);
		}
	}

	@Override
	public void leave() {
		for (TreeLog<? super T> log : listeners) {
			log.leave();
		}
	}

}
