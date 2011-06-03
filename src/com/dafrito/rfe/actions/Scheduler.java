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
import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.operations.ScriptExecutable_CallFunction;
import com.dafrito.rfe.script.parsing.Parser;
import com.dafrito.rfe.script.proxies.FauxTemplate_Scheduler;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

@Inspectable
public class Scheduler implements ActionListener, ScriptConvertible<FauxTemplate_Scheduler> {
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
		int i = 0;
		try {
			this.inProgress = true;
			Iterator<ScheduledEvent> iter = this.events.iterator();
			while (iter.hasNext()) {
				ScheduledEvent event = iter.next();
				if (event.getTime() <= this.gameTime) {
					assert Debugger.openNode("Event Iteration", "Iterating event");
					assert Debugger.addNode(event);
					i++;
					ScriptTemplate_Abstract listener = event.getListener();
					if (listener == null) {
						listener = this.getDefaultListener();
					}
					params.clear();
					params.add(Parser.getRiffLong(this.getEnvironment(), differential));
					params.add(Parser.getRiffAsset(this.getEnvironment(), event.getAsset()));
					ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, listener, "iterate", params);
					assert Debugger.closeNode();
					iter.remove();
				}
			}
			this.inProgress = false;
			for (ScheduledEvent event : this.tempList) {
				this.events.add(event);
			}
			this.tempList.clear();
		} catch (Exception_Nodeable exception) {
			throw new Exception_InternalError(this.getEnvironment(), exception);
		}
	}

	@Override
	public FauxTemplate_Scheduler convert() {
		FauxTemplate_Scheduler scheduler = new FauxTemplate_Scheduler(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Scheduler.SCHEDULERSTRING));
		scheduler.setScheduler(this);
		return scheduler;
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

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public void schedule(long time, Asset asset, ScriptTemplate_Abstract listener) {
		ScheduledEvent event = new ScheduledEvent(this.getCurrentGameTime() + time, asset, listener);
		assert Debugger.addSnapNode("Scheduler Additions", "Adding event to scheduler", event);
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
