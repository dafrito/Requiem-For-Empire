package com.dafrito.rfe;

import com.dafrito.rfe.script.ScriptEnvironment;

public class Scenario {
	private ScriptEnvironment environment;
	private Scheduler scheduler;
	private String name;
	private Terrestrial terrestrial;

	public Scenario(ScriptEnvironment env, Terrestrial terrestrial, String name) {
		this.environment = env;
		this.name = name;
		this.terrestrial = terrestrial;
		this.scheduler = new Scheduler(env);
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public long getGameTime() {
		return this.scheduler.getCurrentGameTime();
	}

	public String getName() {
		return this.name;
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public Terrestrial getTerrestrial() {
		return this.terrestrial;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTerrestrial(Terrestrial terrestrial) {
		this.terrestrial = terrestrial;
	}

	public void start() {
		this.scheduler.start();
	}
}
