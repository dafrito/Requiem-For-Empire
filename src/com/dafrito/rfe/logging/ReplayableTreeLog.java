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

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bluespot.logic.actors.Actor;

/**
 * This log is thread-safe. However, as with all TreeLogs, external
 * synchronization is required to ensure log entries are not interleaved with
 * one another.
 * 
 * @author Aaron Faanes
 * @param <Message>
 *            the type of log message
 * 
 * @see BufferedTreeLog
 */
public class ReplayableTreeLog<Message> implements TreeLog<Message> {

	private Queue<Actor<TreeLog<? super Message>>> actions = new ConcurrentLinkedQueue<>();

	public synchronized int play(TreeLog<? super Message> sink) {
		return play(sink, 0);
	}

	public synchronized int play(TreeLog<? super Message> sink, int maxPlayed) {
		int played = 0;

		Iterator<Actor<TreeLog<? super Message>>> iter = actions.iterator();
		while (true) {
			if (maxPlayed > 0 && played == maxPlayed) {
				break;
			}
			if (!iter.hasNext()) {
				break;
			}
			iter.next().receive(sink);
			++played;
		}
		return played;
	}

	public synchronized int remove(int maxRemoved) {
		int removed = 0;

		while (true) {
			if (actions.isEmpty()) {
				break;
			}
			if (removed == maxRemoved) {
				break;
			}
			actions.remove();
			++removed;
		}
		return removed;
	}

	public synchronized boolean isEmpty() {
		return actions.isEmpty();
	}

	@Override
	public void log(LogMessage<? extends Message> message) {
		actions.add(new MessageLogAction<Message>(message));
	}

	@Override
	public void enter(LogMessage<? extends Message> scope) {
		actions.add(new EnterLogAction<Message>(scope));
	}

	@Override
	public void leave() {
		actions.add(new LeaveLogAction<Message>());
	}

	@Override
	public void reset() {
		actions.add(new ResetLogAction<Message>());
	}
}

class EnterLogAction<Message> implements Actor<TreeLog<? super Message>> {
	private final LogMessage<? extends Message> scope;

	public EnterLogAction(LogMessage<? extends Message> scope) {
		this.scope = scope;
	}

	@Override
	public void receive(TreeLog<? super Message> log) {
		if (log == null) {
			return;
		}
		log.enter(scope);
	}
}

/**
 * This class could be a singleton if I got the generics to work properly.
 * 
 * @author Aaron Faanes
 * 
 * @param <Message>
 */
class LeaveLogAction<Message> implements Actor<TreeLog<? super Message>> {
	@Override
	public void receive(TreeLog<? super Message> log) {
		if (log == null) {
			return;
		}
		log.leave();
	}
}

class ResetLogAction<Message> implements Actor<TreeLog<? super Message>> {
	@Override
	public void receive(TreeLog<? super Message> log) {
		if (log == null) {
			return;
		}
		log.reset();
	}
}

class MessageLogAction<Message> implements Actor<TreeLog<? super Message>> {
	private final LogMessage<? extends Message> message;

	public MessageLogAction(LogMessage<? extends Message> message) {
		this.message = message;
	}

	@Override
	public void receive(TreeLog<? super Message> log) {
		if (log == null) {
			return;
		}
		log.log(message);
	}
}
