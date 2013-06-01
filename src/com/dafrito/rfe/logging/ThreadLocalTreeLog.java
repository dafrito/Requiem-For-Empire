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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bluespot.logic.actors.Actor;

/**
 * @author Aaron Faanes
 * @param <T>
 *            the type of log message
 * @param <Log>
 *            the type of {@link TreeLog} created for each thread by this log
 * 
 */
public abstract class ThreadLocalTreeLog<T, Log extends TreeLog<? super T>> implements TreeLog<T> {

	private List<Actor<? super Log>> listeners = new CopyOnWriteArrayList<>();

	private Set<Log> logs = new HashSet<>();

	/**
	 * Creates a new log for the specified thread. The implementation of this
	 * method must be thread-safe.
	 * 
	 * @param thread
	 *            the thread for the log
	 * @return a new log for the specified thread
	 */
	protected abstract Log newTreeLog(Thread thread);

	private ThreadLocal<Log> log = new ThreadLocal<Log>() {
		@Override
		protected Log initialValue() {
			Log log = newTreeLog(Thread.currentThread());
			logs.add(log);
			dispatchNewTreeLog(log);
			return log;
		}
	};

	private synchronized void dispatchNewTreeLog(Log log) {
		for (Actor<? super Log> listener : listeners) {
			listener.receive(log);
		}
	}

	/**
	 * Adds a listener that will be called once for every log that is created,
	 * including those that have already been created. Logs are created whenever
	 * a message is received from a thread that has not been seen before.
	 * 
	 * @param listener
	 *            the listener that will receive newly created logs
	 */
	public void addListener(Actor<? super Log> listener) {
		listeners.add(listener);
		for (Log log : getLogs()) {
			listener.receive(log);
		}
	}

	/**
	 * Immediately remove the specified listener.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(Actor<? super Log> listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the set of all logs created by this log
	 */
	public Set<Log> getLogs() {
		return Collections.unmodifiableSet(logs);
	}

	@Override
	public void log(LogMessage<? extends T> message) {
		get().log(message);
	}

	@Override
	public void enter(String scope, String scopeGroup) {
		get().enter(scope, scopeGroup);
	}

	@Override
	public void leave() {
		get().leave();
	}

	private TreeLog<? super T> get() {
		return log.get();
	}
}
