import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.Timer;

class ScheduledEvent implements Comparable, Nodeable {
	private Long m_time;
	private Asset m_asset;
	private ScriptTemplate_Abstract m_listener;

	public ScheduledEvent(long time, Asset asset, ScriptTemplate_Abstract listener) {
		this.m_time = new Long(time);
		this.m_asset = asset;
		this.m_listener = listener;
	}

	@Override
	public int compareTo(Object o) {
		return this.m_time.compareTo(((ScheduledEvent) o).getTime());
	}

	public Asset getAsset() {
		return this.m_asset;
	}

	public ScriptTemplate_Abstract getListener() {
		return this.m_listener;
	}

	public long getTime() {
		return this.m_time.longValue();
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Scheduled Event (" + this.m_time + ")");
		assert Debugger.addNode(this.m_asset);
		assert Debugger.addSnapNode("Listener", this.m_listener);
		assert Debugger.closeNode();
		return true;
	}
}

public class Scheduler implements ActionListener, ScriptConvertible, Nodeable {
	private Timer m_timer;
	private ScriptTemplate_Abstract m_defaultListener;
	private TreeSet<ScheduledEvent> m_events = new TreeSet<ScheduledEvent>();
	private List<ScheduledEvent> m_tempList = new LinkedList<ScheduledEvent>();
	private ScriptEnvironment m_environment;
	private long m_lastIteration = -1;
	private long m_gameTime;
	private double m_compression;
	private boolean m_inProgress;

	public Scheduler(ScriptEnvironment env) {
		this.m_environment = env;
		this.m_timer = new Timer(10, this);
		this.m_compression = 1.0d;
		env.addTimer(this.m_timer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long differential = (long) (((this.getCurrentTime() - this.m_lastIteration)) * this.m_compression);
		this.m_lastIteration = this.getCurrentTime();
		this.m_gameTime += differential;
		List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
		int i = 0;
		try {
			this.m_inProgress = true;
			Iterator<ScheduledEvent> iter = this.m_events.iterator();
			while (iter.hasNext()) {
				ScheduledEvent event = iter.next();
				if (event.getTime() <= this.m_gameTime) {
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
			this.m_inProgress = false;
			for (ScheduledEvent event : this.m_tempList) {
				this.m_events.add(event);
			}
			this.m_tempList.clear();
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
		return this.m_gameTime;
	}

	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public ScriptTemplate_Abstract getDefaultListener() {
		return this.m_defaultListener;
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Scheduler");
		assert Debugger.addNode("Compression: " + this.m_compression);
		assert Debugger.addNode("Game-Time (In seconds from start): " + this.m_gameTime);
		assert Debugger.addNode("Last Iteration: " + this.m_lastIteration);
		assert Debugger.addNode("Default listener: " + this.m_defaultListener);
		assert Debugger.addSnapNode("Events (" + this.m_events.size() + " event(s))", this.m_events);
		assert Debugger.closeNode();
		return true;
	}

	public void schedule(long time, Asset asset, ScriptTemplate_Abstract listener) {
		ScheduledEvent event = new ScheduledEvent(this.getCurrentGameTime() + time, asset, listener);
		assert Debugger.addSnapNode("Scheduler Additions", "Adding event to scheduler", event);
		if (this.m_inProgress) {
			this.m_tempList.add(event);
		} else {
			this.m_events.add(event);
		}
	}

	public void setDefaultListener(ScriptTemplate_Abstract defaulter) {
		this.m_defaultListener = defaulter;
	}

	public void start() {
		this.m_timer.start();
		this.m_lastIteration = this.getCurrentTime();
		this.m_gameTime = 0;
	}

	public void stop() {
		this.m_timer.stop();
	}

}
