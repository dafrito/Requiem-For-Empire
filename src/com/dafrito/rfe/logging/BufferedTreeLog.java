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

import java.util.ArrayDeque;
import java.util.Deque;

import com.bluespot.logic.runnables.Runnables;

/**
 * @author Aaron Faanes
 * @param <Message>
 *            the type of log message
 * 
 */
public class BufferedTreeLog<Message> extends ProxyTreeLog<Message> implements Runnable {

	private static enum Action {
		MESSAGE,
		ENTER_SCOPE,
		ENTER_SCOPE_WITH_GROUP,
		LEAVE_SCOPE
	};

	private Deque<Object> commands = new ArrayDeque<>();

	private Runnable notifier;

	private boolean hasNotified;
	private boolean alwaysNotify;

	@Override
	public void run() {
		flush();
	}

	private synchronized Deque<Object> swapCommands() {
		Deque<Object> flushedCommands = commands;
		commands = new ArrayDeque<>();
		hasNotified = false;
		return flushedCommands;
	}

	public void flush() {
		Deque<Object> flushed = swapCommands();
		while (!flushed.isEmpty()) {
			switch ((Action) flushed.removeFirst()) {
			case ENTER_SCOPE:
				String scope = (String) flushed.removeFirst();
				super.enter(scope, null);
				break;
			case ENTER_SCOPE_WITH_GROUP:
				scope = (String) flushed.removeFirst();
				String scopeGroup = (String) flushed.removeFirst();
				super.enter(scope, scopeGroup);
				break;
			case LEAVE_SCOPE:
				super.leave();
				break;
			case MESSAGE:
				@SuppressWarnings("unchecked")
				LogMessage<? extends Message> message = (LogMessage<? extends Message>) flushed.removeFirst();
				super.log(message);
				break;
			default:
				throw new AssertionError("Unhandled action");
			}
		}
	}

	@Override
	public synchronized void log(LogMessage<? extends Message> message) {
		commands.addLast(Action.MESSAGE);
		commands.addLast(message);
		dispatch();
	}

	@Override
	public synchronized void enter(String scope, String scopeGroup) {
		if (scopeGroup != null) {
			commands.addLast(Action.ENTER_SCOPE_WITH_GROUP);
			commands.addLast(scope);
			commands.addLast(scopeGroup);
		} else {
			commands.addLast(Action.ENTER_SCOPE);
			commands.addLast(scope);
		}
		dispatch();
	}

	@Override
	public synchronized void leave() {
		commands.addLast(Action.LEAVE_SCOPE);
		dispatch();
	}

	private void dispatch() {
		if (!hasNotified || alwaysNotify) {
			hasNotified = true;
			getNotifier().run();
		}
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
