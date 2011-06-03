/**
 * 
 */
package com.dafrito.rfe.actions;

import com.dafrito.rfe.Asset;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;

@Inspectable
public class ScheduledEvent implements Comparable<ScheduledEvent> {
	private final long time;
	private final Asset asset;
	private final ScriptTemplate_Abstract listener;

	public ScheduledEvent(final long time, final Asset asset, final ScriptTemplate_Abstract listener) {
		this.time = time;
		this.asset = asset;
		this.listener = listener;
	}

	@Inspectable
	public long getTime() {
		return this.time;
	}

	@Inspectable
	public Asset getAsset() {
		return this.asset;
	}

	@Inspectable
	public ScriptTemplate_Abstract getListener() {
		return this.listener;
	}

	@Override
	public int compareTo(ScheduledEvent o) {
		if (this.time > o.getTime()) {
			return 1;
		}
		return this.time == o.getTime() ? 0 : -1;
	}

}