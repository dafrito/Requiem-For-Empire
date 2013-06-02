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
import java.util.Map;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.inspect.Inspection;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.inspect.NodeableInspector;

/**
 * @author Aaron Faanes
 * 
 */
public final class TreeLogHandlers {

	private TreeLogHandlers() {
		// Suppress default constructor to ensure non-instantiability.
		throw new AssertionError("Instantiation not allowed");
	}

	private static TreeLogHandler<Object> NULL = new TreeLogHandler<Object>() {
		@Override
		public boolean handle(LogMessage<? extends Object> message) {
			if (message == null) {
				getLog().log(new LogMessage<Object>("<null>"));
				return true;
			}
			if (message.getMessage() == null) {
				getLog().log(message.changeMessage("<null>"));
				return true;
			}
			return false;
		}
	};

	public static TreeLogHandler<Object> nullHandler() {
		return NULL;
	}

	private static TreeLogHandler<Object> COMMON_STRINGS = new TreeLogHandler<Object>() {
		@Override
		public boolean handle(LogMessage<? extends Object> message) {
			if (message == null || message.getMessage() == null || !(message.getMessage() instanceof CommonString)) {
				return false;
			}
			CommonString string = (CommonString) message.getMessage();
			getLog().log(message.changeMessage(string.getText()));
			return true;
		}
	};

	public static TreeLogHandler<Object> commonStrings() {
		return COMMON_STRINGS;
	}

	private static TreeLogHandler<Object> INSPECTABLE = new TreeLogHandler<Object>() {
		@Override
		public boolean handle(LogMessage<? extends Object> message) {
			if (message == null || message.getMessage() == null) {
				// Quick failures
				return false;
			}
			Object value = message.getMessage();
			if (!value.getClass().isAnnotationPresent(Inspectable.class)) {
				return false;
			}
			NodeableInspector<Object> inspector = new NodeableInspector<Object>(getLog());
			Inspection.reflect(inspector, message);
			inspector.close();
			return true;
		}
	};

	public static TreeLogHandler<Object> inspectable() {
		return INSPECTABLE;
	}

	private static TreeLogHandler<Object> NODEABLE = new TreeLogHandler<Object>() {
		@Override
		public boolean handle(LogMessage<? extends Object> message) {
			if (message == null || message.getMessage() == null) {
				// Quick failures
				return false;
			}
			Object value = message.getMessage();
			if (!(value instanceof Nodeable)) {
				return false;
			}
			// TODO This willl loop endlessly until nodificate can take an argument
			((Nodeable) value).nodificate();
			return true;
		}
	};

	public static TreeLogHandler<Object> nodeable() {
		return NODEABLE;
	}

	private static TreeLogHandler<Object> ITERABLE = new TreeLogHandler<Object>() {
		@Override
		public boolean handle(LogMessage<? extends Object> message) {
			if (message == null || message.getMessage() == null) {
				// Quick failures
				return false;
			}
			Object value = message.getMessage();
			if (!(value instanceof Iterable)) {
				return false;
			}
			Iterator<?> iter = ((Iterable<?>) value).iterator();
			while (iter.hasNext()) {
				getLog().log(message.changeMessage(iter.next()));
			}
			return true;
		}
	};

	public static TreeLogHandler<Object> iterable() {
		return ITERABLE;
	}

	private static TreeLogHandler<Object> MAP = new TreeLogHandler<Object>() {
		@Override
		public boolean handle(LogMessage<? extends Object> message) {
			if (message == null || message.getMessage() == null || !(message.getMessage() instanceof Map)) {
				// Quick failures
				return false;
			}
			Map<?, ?> map = (Map<?, ?>) message.getMessage();
			if (map.isEmpty()) {
				return true;
			}
			Iterator<?> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
				getLog().enter(entry.getKey().toString(), null);
				getLog().log(message.changeMessage(entry.getValue()));
				getLog().leave();
			}
			return true;
		}
	};

	public static TreeLogHandler<Object> map() {
		return MAP;
	}
}
