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

import com.dafrito.rfe.gui.logging.LogViewer;

class SenderReference {
	private final Object reference;
	private final Object name;

	public SenderReference(Object reference, Object name) {
		this.reference = reference;
		this.name = name;
	}

	public Object getReference() {
		return reference;
	}

	@Override
	public String toString() {
		return name.toString();
	}

	@Override
	public int hashCode() {
		return reference.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SenderReference)) {
			return false;
		}
		SenderReference other = (SenderReference) obj;
		return reference.equals(other.getReference());
	}
}

/**
 * @author Aaron Faanes
 * 
 */
public class TreeLogServer implements Runnable {

	private ServerSocket serverSocket;
	private CompositeTreeLog<String> log;
	private LogViewer<? super String> sink;

	public TreeLogServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	private static final String space = "\\s*";
	private static final Pattern PATTERN = Pattern.compile(
			"^"
					+ space + "(<+|>+)?" // scope
					+ space + "(\\d+)?" // timestamp
					+ space + "(?: \\(+" + "([^)]+?)" + "\\)+ )?" // category
					+ space + "(?: \\[+" + "([^\\]]+?)" + "\\]+(?:@(?:0x)?([0-9a-fA-F]+))?)?" // sender and sender id
					+ space + "(.+)?" // message
					+ "$",
			Pattern.COMMENTS
			);

	private static final int SCOPE = 1;
	private static final int TIMESTAMP = 2;
	private static final int CATEGORY = 3;
	private static final int SENDER = 4;
	private static final int SENDER_ID = 5;
	private static final int MESSAGE = 6;

	private static enum ScopeAction {
		NONE,
		ENTER,
		LEAVE
	};

	private void readLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (!matcher.matches()) {
			return;
		}

		long timestamp = System.currentTimeMillis();
		String category = matcher.group(CATEGORY);
		Object sender = matcher.group(SENDER);
		Object senderId = matcher.group(SENDER_ID);

		String message = matcher.group(MESSAGE);
		ScopeAction action = ScopeAction.NONE;

		if (matcher.group(TIMESTAMP) != null) {
			timestamp = Long.valueOf(matcher.group(TIMESTAMP));
		}

		if (senderId != null) {
			sender = new SenderReference(senderId, sender);
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
		if (this.sink == null) {
			return;
		}
		log = new CompositeTreeLog<>();
		sink.addLogPanel(log, String.format("%s:%d", connection.getInetAddress().getHostAddress(), connection.getPort()));

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
		if (this.sink == null) {
			return;
		}
		try {
			while (true) {
				serve(serverSocket.accept());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSink(LogViewer<? super String> sink) {
		this.sink = sink;
	}
}
