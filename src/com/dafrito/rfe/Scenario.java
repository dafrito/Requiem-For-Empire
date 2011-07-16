package com.dafrito.rfe;

import com.dafrito.rfe.actions.Scheduler;
import com.dafrito.rfe.script.ScriptEnvironment;

public class Scenario {
	private Scheduler scheduler;
	private Terrestrial terrestrial;

	public Scenario(ScriptEnvironment env, Terrestrial terrestrial) {
		this.terrestrial = terrestrial;
		this.scheduler = new Scheduler(env);
	}

	public long getGameTime() {
		return this.scheduler.getCurrentGameTime();
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public Terrestrial getTerrestrial() {
		return this.terrestrial;
	}

	public void setTerrestrial(Terrestrial terrestrial) {
		this.terrestrial = terrestrial;
	}

	public void start() {
		this.scheduler.start();
	}
}
