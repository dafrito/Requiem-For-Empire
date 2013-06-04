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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aaron Faanes
 * 
 */
public class TreeLogServer implements Runnable {

	private ServerSocket serverSocket;
	private TreeLog<? super String> log;

	public TreeLogServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	private static final String space = "\\s*";
	private static final Pattern PATTERN = Pattern.compile(
			"^"
					+ space + "(<+|>+)?"
					+ space + "(\\d+)?"
					+ space + "(?: \\(+" + "([^)]+?)" + "\\)+ )?"
					+ space + "(?: \\[+" + "([^\\]]+?)" + "\\]+ )?"
					+ space + "(.+)?"
					+ "$",
			Pattern.COMMENTS
			);

	private static final int SCOPE = 1;
	private static final int TIMESTAMP = 2;
	private static final int CATEGORY = 3;
	private static final int SENDER = 4;
	private static final int MESSAGE = 5;

	private static enum ScopeAction {
		NONE,
		ENTER,
		LEAVE
	};

	public void setLog(TreeLog<? super String> log) {
		this.log = log;
	}

	private void readLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (!matcher.matches()) {
			return;
		}

		long timestamp = System.currentTimeMillis();
		String category = matcher.group(CATEGORY);
		String sender = matcher.group(SENDER);
		String message = matcher.group(MESSAGE);
		ScopeAction action = ScopeAction.NONE;

		if (matcher.group(TIMESTAMP) != null) {
			timestamp = Long.valueOf(matcher.group(TIMESTAMP));
		}
		if (matcher.group(SCOPE) != null) {
			switch (matcher.group(SCOPE).charAt(0)) {
			case '>':
				action = ScopeAction.ENTER;
				break;
			case '<':
				action = ScopeAction.LEAVE;
				break;
			default:
				throw new AssertionError("Impossible (I probably botched the regex)");
			}
		}

		LogMessage<String> logMessage = new LogMessage<String>(timestamp, sender, category, message);

		switch (action) {
		case ENTER:
			log.enter(logMessage);
			break;
		case NONE:
			log.log(logMessage);
			break;
		case LEAVE:
			if (category != null || message != null) {
				log.log(logMessage);
			}
			log.leave();
		}
	}

	private void serve(Socket connection) throws IOException {
		log.enter(new LogMessage<String>("Connection received from " + connection));
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				log.log(new LogMessage<String>("Disconnected"));
				break;
			}
			readLine(line);
		}
		log.leave();
	}

	@Override
	public void run() {
		try {
			while (true) {
				serve(serverSocket.accept());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
