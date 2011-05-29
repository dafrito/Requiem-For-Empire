package com.dafrito.rfe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.Timer;

import com.dafrito.rfe.inspect.Nodeable;

class ScheduledEvent implements Comparable<ScheduledEvent>, Nodeable {
	private Long time;
	private Asset asset;
	private ScriptTemplate_Abstract listener;

	public ScheduledEvent(long time, Asset asset, ScriptTemplate_Abstract listener) {
		this.time = new Long(time);
		this.asset = asset;
		this.listener = listener;
	}

	@Override
	public int compareTo(ScheduledEvent o) {
		return this.time.compareTo(o.getTime());
	}

	public Asset getAsset() {
		return this.asset;
	}

	public ScriptTemplate_Abstract getListener() {
		return this.listener;
	}

	public long getTime() {
		return this.time.longValue();
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Scheduled Event (" + this.time + ")");
		assert Debugger.addNode(this.asset);
		assert Debugger.addSnapNode("Listener", this.listener);
		assert Debugger.closeNode();
	}
}

public class Scheduler implements ActionListener, ScriptConvertible, Nodeable {
	private Timer timer;
	private ScriptTemplate_Abstract defaultListener;
	private TreeSet<ScheduledEvent> events = new TreeSet<ScheduledEvent>();
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
	public Object convert() {
		FauxTemplate_Scheduler scheduler = new FauxTemplate_Scheduler(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Scheduler.SCHEDULERSTRING));
		scheduler.setScheduler(this);
		return scheduler;
	}

	public long getCurrentGameTime() {
		return this.gameTime;
	}

	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public ScriptTemplate_Abstract getDefaultListener() {
		return this.defaultListener;
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Scheduler");
		assert Debugger.addNode("Compression: " + this.compression);
		assert Debugger.addNode("Game-Time (In seconds from start): " + this.gameTime);
		assert Debugger.addNode("Last Iteration: " + this.lastIteration);
		assert Debugger.addNode("Default listener: " + this.defaultListener);
		assert Debugger.addSnapNode("Events (" + this.events.size() + " event(s))", this.events);
		assert Debugger.closeNode();
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
