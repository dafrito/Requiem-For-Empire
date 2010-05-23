package com.dafrito.gui;

import com.dafrito.logging.LegacyDebugger;

public class KeyEvent_KeyDown extends RiffInterface_KeyEvent {
    public KeyEvent_KeyDown(int key) {
        super(key);
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Key Events", "Key-Down Event");
        super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
