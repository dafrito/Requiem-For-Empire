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

import com.bluespot.logic.runnables.Runnables;

/**
 * @author Aaron Faanes
 * @param <Message>
 *            the type of log message
 * @see ReplayableTreeLog
 */
public class BufferedTreeLog<Message> extends ProxyTreeLog<Message> implements Runnable {

	private ReplayableTreeLog<Message> log = new ReplayableTreeLog<>();

	private Runnable notifier;

	private boolean hasNotified;
	private boolean alwaysNotify;

	private int flushSize = 0;

	public void setFlushSize(int flushSize) {
		this.flushSize = flushSize;
	}

	public int flush() {
		return flush(0);
	}

	public synchronized int flush(int maxFlushed) {
		int actuallyRemoved = log.remove(log.play(getSink(), maxFlushed));

		hasNotified = false;
		if (!log.isEmpty()) {
			dispatch();
		}

		return actuallyRemoved;
	}

	@Override
	public void log(LogMessage<? extends Message> message) {
		log.log(message);
		dispatch();
	}

	@Override
	public void enter(LogMessage<? extends Message> scope) {
		log.enter(scope);
		dispatch();
	}

	@Override
	public void leave() {
		log.leave();
		dispatch();
	}

	private void dispatch() {
		if (!hasNotified || alwaysNotify) {
			hasNotified = true;
			getNotifier().run();
		}
	}

	@Override
	public void run() {
		flush(flushSize);
	}

	public boolean getAlwaysNotify() {
		return this.alwaysNotify;
	}

	public void setAlwaysNotify(boolean alwaysNotify) {
		this.alwaysNotify = alwaysNotify;

		// Dispatch a (possibly) spurious event to ensure everything is in sync
		getNotifier().run();
	}

	public Runnable getNotifier() {
		if (notifier == null) {
			return Runnables.noop();
		}
		return notifier;
	}

	public void setNotifier(Runnable notifier) {
		this.notifier = notifier;
	}

}
