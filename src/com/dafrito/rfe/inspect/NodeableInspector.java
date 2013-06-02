/**
 * 
 */
package com.dafrito.rfe.inspect;

import com.dafrito.rfe.logging.LogMessage;
import com.dafrito.rfe.logging.TreeLog;

/**
 * An {@link Inspector} that records nodes using an underlying
 * {@link TreeBuildingInspector}. This class bridges the modern
 * {@link Inspectable} API in order to remove the older {@link Nodeable}
 * framework.
 * 
 * @author Aaron Faanes
 * @param <Message>
 *            the type of log message
 * 
 */
public class NodeableInspector<Message> implements Inspector<Message> {

	private final TreeLog<? super Message> log;

	private boolean frozen = false;

	private NodeableInspector<Message> child;

	/**
	 * Create a new {@link NodeableInspector} using the specified tree-builder.
	 * 
	 * @param log
	 *            the log that receives our inspected values
	 */
	public NodeableInspector(TreeLog<? super Message> log) {
		if (log == null) {
			throw new NullPointerException("debugger must not be null");
		}
		this.log = log;
	}

	@Override
	public void field(Message name, Message value) {
		this.ensureUnfrozen();
		this.closeChild();
		log.enter(new LogMessage<Message>(name));
		log.log(new LogMessage<Message>(value));
		log.leave();
	}

	@Override
	public void value(Message value) {
		this.ensureUnfrozen();
		this.closeChild();
		log.log(new LogMessage<Message>(value));
	}

	@Override
	public Inspector<Message> group(Message groupName) {
		this.ensureUnfrozen();
		this.closeChild();
		this.child = new NodeableInspector<Message>(log);

		log.enter(new LogMessage<Message>(groupName));
		return this.child;
	}

	@Override
	public void comment(Message note) {
		this.ensureUnfrozen();
		this.closeChild();
		log.log(new LogMessage<Message>(note));
	}

	private void ensureUnfrozen() {
		if (this.frozen) {
			throw new IllegalStateException("NodeableInspector cannot be modified since it is frozen");
		}
	}

	@Override
	public void close() {
		if (this.frozen) {
			return;
		}
		this.closeChild();
		this.frozen = true;
	}

	private void closeChild() {
		if (this.child == null) {
			return;
		}
		this.log.leave();
		this.child.close();
		this.child = null;
	}

}
