package com.dafrito.rfe.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Timer;

import com.dafrito.rfe.Asset;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.Conversions;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable_CallFunction;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;

@Inspectable
public class Scheduler implements ActionListener {
	private Timer timer;
	private ScriptTemplate_Abstract defaultListener;
	private SortedSet<ScheduledEvent> events = new TreeSet<ScheduledEvent>();
	private List<ScheduledEvent> tempList = new LinkedList<ScheduledEvent>();
	private ScriptEnvironment environment;
	private long lastIteration = -1;
	private long gameTime;
	private double compression;
	private boolean inProgress;

	public Scheduler(ScriptEnvironment env) {
		this.environment = env;
		this.timer = new Timer(10, this);
		this.compression = 1.0d;
		env.addTimer(this.timer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long differential = (long) (((this.getCurrentTime() - this.lastIteration)) * this.compression);
		this.lastIteration = this.getCurrentTime();
		this.gameTime += differential;
		List<ScriptValue> params = new LinkedList<ScriptValue>();
		try {
			this.inProgress = true;
			Iterator<ScheduledEvent> iter = this.events.iterator();
			while (iter.hasNext()) {
				ScheduledEvent event = iter.next();
				if (event.getTime() <= this.gameTime) {
					assert Logs.openNode("Event Iteration", "Iterating event");
					assert Logs.addNode(event);
					ScriptTemplate_Abstract listener = event.getListener();
					if (listener == null) {
						listener = this.getDefaultListener();
					}
					params.clear();
					params.add(Conversions.wrapLong(this.getEnvironment(), differential));
					params.add(Conversions.wrapAsset(this.getEnvironment(), event.getAsset()));
					ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, listener, "iterate", params);
					assert Logs.closeNode();
					iter.remove();
				}
			}
			this.inProgress = false;
			for (ScheduledEvent event : this.tempList) {
				this.events.add(event);
			}
			this.tempList.clear();
		} catch (ScriptException exception) {
			throw new Exception_InternalError(this.getEnvironment(), exception);
		}
	}

	@Inspectable
	public long getLastIteration() {
		return this.lastIteration;
	}

	@Inspectable
	public double getCompression() {
		return this.compression;
	}

	@Inspectable
	public long getCurrentGameTime() {
		return this.gameTime;
	}

	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	@Inspectable
	public ScriptTemplate_Abstract getDefaultListener() {
		return this.defaultListener;
	}

	@Inspectable
	public SortedSet<ScheduledEvent> getEvents() {
		return Collections.unmodifiableSortedSet(this.events);
	}

	private ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public void schedule(long time, Asset asset, ScriptTemplate_Abstract listener) {
		ScheduledEvent event = new ScheduledEvent(this.getCurrentGameTime() + time, asset, listener);
		assert Logs.addSnapNode("Scheduler Additions", "Adding event to scheduler", event);
		if (this.inProgress) {
			this.tempList.add(event);
		} else {
			this.events.add(event);
		}
	}

	public void setDefaultListener(ScriptTemplate_Abstract defaulter) {
		this.defaultListener = defaulter;
	}

	public void start() {
		this.timer.start();
		this.lastIteration = this.getCurrentTime();
		this.gameTime = 0;
	}

	public void stop() {
		this.timer.stop();
	}

}
