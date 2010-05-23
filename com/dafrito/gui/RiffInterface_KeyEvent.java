package com.dafrito.gui;

import com.dafrito.debug.Nodeable;
import com.dafrito.logging.LegacyDebugger;

public class RiffInterface_KeyEvent implements Nodeable, RiffInterface_Event {
    private int key;

    public RiffInterface_KeyEvent(int key) {
        this.key = key;
    }

    public int getKeyCode() {
        return this.key;
    }

    public boolean nodificate() {
        assert LegacyDebugger.addNode("Key code: " + this.key);
        return true;
    }
}
