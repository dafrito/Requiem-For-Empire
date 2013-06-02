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

import com.bluespot.logic.handlers.Handler;
import com.bluespot.logic.handlers.Handlers;

/**
 * @author Aaron Faanes
 * @param <Message>
 *            the type of log message
 * 
 */
public class HandledTreeLog<Message> extends ProxyTreeLog<Message> {

	private Handler<? super LogMessage<? extends Message>> handler;

	public Handler<? super LogMessage<? extends Message>> getHandler() {
		if (handler == null) {
			return Handlers.noop();
		}
		return handler;
	}

	public void setHandler(Handler<? super LogMessage<? extends Message>> handler) {
		this.handler = handler;
	}

	@Override
	public void log(LogMessage<? extends Message> message) {
		if (!getHandler().handle(message)) {
			super.log(message);
		}
	}
}
