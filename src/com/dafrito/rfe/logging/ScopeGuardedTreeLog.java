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

import com.bluespot.logic.predicates.Predicate;
import com.bluespot.logic.predicates.Predicates;

/**
 * @author Aaron Faanes
 * @param <T>
 *            the type of log message
 */
public class ScopeGuardedTreeLog<T> extends ProxyTreeLog<T> {

	int levels;

	private Predicate<? super LogMessage<? extends T>> guard;

	public void setGuard(Predicate<? super LogMessage<? extends T>> guard) {
		this.guard = guard;
	}

	public Predicate<? super LogMessage<? extends T>> getGuard() {
		if (this.guard == null) {
			return Predicates.always();
		}
		return this.guard;
	}

	private boolean isAccepting() {
		return levels > 0;
	}

	@Override
	public void log(LogMessage<? extends T> message) {
		if (isAccepting() || getGuard().test(message)) {
			super.log(message);
		}
	}

	@Override
	public void enter(LogMessage<? extends T> scope) {
		if (levels == 0 && !getGuard().test(scope)) {
			return;
		}
		++levels;
		super.enter(scope);
	}

	@Override
	public void leave() {
		if (levels == 0) {
			return;
		}
		super.leave();
		--levels;
	}
}
