package com.dafrito.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.Timer;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.economy.Asset;
import com.dafrito.script.Parser;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.executable.ScriptExecutable_CallFunction;
import com.dafrito.script.templates.FauxTemplate_Scheduler;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;


class ScheduledEvent implements Comparable<ScheduledEvent>, Nodeable {
    private Long m_time;
    private Asset m_asset;
    private ScriptTemplate_Abstract m_listener;

    public ScheduledEvent(long time, Asset asset, ScriptTemplate_Abstract listener) {
        this.m_time = new Long(time);
        this.m_asset = asset;
        this.m_listener = listener;
    }

    public long getTime() {
        return this.m_time.longValue();
    }

    public Asset getAsset() {
        return this.m_asset;
    }

    public ScriptTemplate_Abstract getListener() {
        return this.m_listener;
    }

    public int compareTo(ScheduledEvent otherEvent) {
        return this.m_time.compareTo(otherEvent.getTime());
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Scheduled Event (" + this.m_time + ")");
        assert LegacyDebugger.addNode(this.m_asset);
        assert LegacyDebugger.addSnapNode("Listener", this.m_listener);
        assert LegacyDebugger.close();
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

    public boolean nodificate() {
        assert LegacyDebugger.open("Scheduler");
        assert LegacyDebugger.addNode("Compression: " + this.m_compression);
        assert LegacyDebugger.addNode("Game-Time (In seconds from start): " + this.m_gameTime);
        assert LegacyDebugger.addNode("Last Iteration: " + this.m_lastIteration);
        assert LegacyDebugger.addNode("Default listener: " + this.m_defaultListener);
        assert LegacyDebugger.addSnapNode("Events (" + this.m_events.size() + " event(s))", this.m_events);
        assert LegacyDebugger.close();
        return true;
    }

    public Scheduler(ScriptEnvironment env) {
        this.m_environment = env;
        this.m_timer = new Timer(10, this);
        this.m_compression = 1.0d;
        env.addTimer(this.m_timer);
    }

    public ScriptEnvironment getEnvironment() {
        return this.m_environment;
    }

    public void start() {
        this.m_timer.start();
        this.m_lastIteration = getCurrentTime();
        this.m_gameTime = 0;
    }

    public void stop() {
        this.m_timer.stop();
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public long getCurrentGameTime() {
        return this.m_gameTime;
    }

    public void schedule(long time, Asset asset, ScriptTemplate_Abstract listener) {
        ScheduledEvent event = new ScheduledEvent(getCurrentGameTime() + time, asset, listener);
        assert LegacyDebugger.addSnapNode("Scheduler Additions", "Adding event to scheduler", event);
        if(this.m_inProgress) {
            this.m_tempList.add(event);
        } else {
            this.m_events.add(event);
        }
    }

    public void setDefaultListener(ScriptTemplate_Abstract defaulter) {
        this.m_defaultListener = defaulter;
    }

    public ScriptTemplate_Abstract getDefaultListener() {
        return this.m_defaultListener;
    }

    public void actionPerformed(ActionEvent e) {
        long differential = (long)(((getCurrentTime() - this.m_lastIteration)) * this.m_compression);
        this.m_lastIteration = getCurrentTime();
        this.m_gameTime += differential;
        List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
        int i = 0;
        try {
            this.m_inProgress = true;
            Iterator<ScheduledEvent> iter = this.m_events.iterator();
            while (iter.hasNext()) {
                ScheduledEvent event = iter.next();
                if(event.getTime() <= this.m_gameTime) {
                    assert LegacyDebugger.open("Event Iteration", "Iterating event");
                    assert LegacyDebugger.addNode(event);
                    i++;
                    ScriptTemplate_Abstract listener = event.getListener();
                    if(listener == null) {
                        listener = getDefaultListener();
                    }
                    params.clear();
                    params.add(Parser.getRiffLong(getEnvironment(), differential));
                    params.add(Parser.getRiffAsset(event.getAsset()));
                    ScriptExecutable_CallFunction.callFunction(getEnvironment(), null, listener, "iterate", params);
                    assert LegacyDebugger.close();
                    iter.remove();
                }
            }
            this.m_inProgress = false;
            for(ScheduledEvent event : this.m_tempList) {
                this.m_events.add(event);
            }
            this.m_tempList.clear();
        } catch(Exception_Nodeable exception) {
            throw new Exception_InternalError(getEnvironment(), exception);
        }
    }

    public Object convert() {
        FauxTemplate_Scheduler scheduler = new FauxTemplate_Scheduler(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Scheduler.SCHEDULERSTRING));
        scheduler.setScheduler(this);
        return scheduler;
    }

}
