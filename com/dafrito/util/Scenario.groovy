/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dafrito.util

import com.dafrito.script.ScriptEnvironment;

/**
 *
 * @author Aaron Faanes
 */
class Scenario {
	ScriptEnvironment environment;
    String name;
    Terrestrial terrestrial;
    final Scheduler scheduler = new Scheduler();

    public Scenario(ScriptEnvironment env, Terrestrial terrestrial, String name) {
        this.environment = env;
        this.terrestrial = terrestrial;
        this.name = name;
    }

    public void start() {
        this.scheduler.start();
    }

    public long getGameTime() {
        return this.scheduler.getCurrentGameTime();
    }

}
