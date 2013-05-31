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
 * A hierarchical log of events.
 * 
 * @author Aaron Faanes
 * @param <T>
 *            the type of message
 */
public interface TreeLog<T> {

	void log(LogMessage<? extends T> message);

	/**
	 * Enter a given scope. Subsequent log messages occur within that scope.
	 * Implementations are free to ignore this scope, which would result in a
	 * flat log.
	 * 
	 * @param scope
	 *            the name of the scope. For example, "Reading foo.txt" or
	 *            "Parsing 'PRE' element".
	 * @param scopeGroup
	 *            the name of the scope group. For example, "File reads" or
	 *            "Element parsing". Implementations may choose to collapse
	 *            scopes that have the same scope group into a single scope
	 *            group node, so take care to use a reasonable plural name.
	 */
	void enter(String scope, String scopeGroup);

	void leave();
}
