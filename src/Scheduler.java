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
		m_time = new Long(time);
		m_asset = asset;
		m_listener = listener;
	}

	@Override
	public int compareTo(Object o) {
		return m_time.compareTo(((ScheduledEvent) o).getTime());
	}

	public Asset getAsset() {
		return m_asset;
	}

	public ScriptTemplate_Abstract getListener() {
		return m_listener;
	}

	public long getTime() {
		return m_time.longValue();
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Scheduled Event (" + m_time + ")");
		assert Debugger.addNode(m_asset);
		assert Debugger.addSnapNode("Listener", m_listener);
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
		m_environment = env;
		m_timer = new Timer(10, this);
		m_compression = 1.0d;
		env.addTimer(m_timer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		long differential = (long) (((getCurrentTime() - m_lastIteration)) * m_compression);
		m_lastIteration = getCurrentTime();
		m_gameTime += differential;
		List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
		int i = 0;
		try {
			m_inProgress = true;
			Iterator<ScheduledEvent> iter = m_events.iterator();
			while (iter.hasNext()) {
				ScheduledEvent event = iter.next();
				if (event.getTime() <= m_gameTime) {
					assert Debugger.openNode("Event Iteration", "Iterating event");
					assert Debugger.addNode(event);
					i++;
					ScriptTemplate_Abstract listener = event.getListener();
					if (listener == null) {
						listener = getDefaultListener();
					}
					params.clear();
					params.add(Parser.getRiffLong(getEnvironment(), differential));
					params.add(Parser.getRiffAsset(getEnvironment(), event.getAsset()));
					ScriptExecutable_CallFunction.callFunction(getEnvironment(), null, listener, "iterate", params);
					assert Debugger.closeNode();
					iter.remove();
				}
			}
			m_inProgress = false;
			for (ScheduledEvent event : m_tempList) {
				m_events.add(event);
			}
			m_tempList.clear();
		} catch (Exception_Nodeable exception) {
			throw new Exception_InternalError(getEnvironment(), exception);
		}
	}

	@Override
	public Object convert() {
		FauxTemplate_Scheduler scheduler = new FauxTemplate_Scheduler(getEnvironment(), ScriptValueType.createType(getEnvironment(), FauxTemplate_Scheduler.SCHEDULERSTRING));
		scheduler.setScheduler(this);
		return scheduler;
	}

	public long getCurrentGameTime() {
		return m_gameTime;
	}

	public long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public ScriptTemplate_Abstract getDefaultListener() {
		return m_defaultListener;
	}

	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Scheduler");
		assert Debugger.addNode("Compression: " + m_compression);
		assert Debugger.addNode("Game-Time (In seconds from start): " + m_gameTime);
		assert Debugger.addNode("Last Iteration: " + m_lastIteration);
		assert Debugger.addNode("Default listener: " + m_defaultListener);
		assert Debugger.addSnapNode("Events (" + m_events.size() + " event(s))", m_events);
		assert Debugger.closeNode();
		return true;
	}

	public void schedule(long time, Asset asset, ScriptTemplate_Abstract listener) {
		ScheduledEvent event = new ScheduledEvent(getCurrentGameTime() + time, asset, listener);
		assert Debugger.addSnapNode("Scheduler Additions", "Adding event to scheduler", event);
		if (m_inProgress) {
			m_tempList.add(event);
		} else {
			m_events.add(event);
		}
	}

	public void setDefaultListener(ScriptTemplate_Abstract defaulter) {
		m_defaultListener = defaulter;
	}

	public void start() {
		m_timer.start();
		m_lastIteration = getCurrentTime();
		m_gameTime = 0;
	}

	public void stop() {
		m_timer.stop();
	}

}
