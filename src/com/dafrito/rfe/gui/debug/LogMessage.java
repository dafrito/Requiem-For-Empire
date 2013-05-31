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
 * A simple message.
 * 
 * @author Aaron Faanes
 * @param <T>
 *            the type of message
 * 
 */
public class LogMessage<T> {

	private long timestamp;

	private Object sender;

	private T category;

	private T message;

	public LogMessage(Object sender, T category, T message) {
		this(System.currentTimeMillis(), sender, category, message);
	}

	public LogMessage(T category, T message) {
		this(System.currentTimeMillis(), null, category, message);
	}

	public LogMessage(T message) {
		this(System.currentTimeMillis(), null, null, message);
	}

	public LogMessage(long timestamp, Object sender, T category, T message) {
		this.timestamp = timestamp;
		this.sender = sender;
		this.category = category;
		this.message = message;
	}

	public T getMessage() {
		return this.message;
	}

	public T getCategory() {
		return this.category;
	}

	public Object getSender() {
		return this.sender;
	}

	public long getTimestamp() {
		return this.timestamp;
	}
}
