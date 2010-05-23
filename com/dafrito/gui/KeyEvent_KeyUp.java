package com.dafrito.gui;

import com.dafrito.logging.LegacyDebugger;

public class KeyEvent_KeyUp extends RiffInterface_KeyEvent {
    public KeyEvent_KeyUp(int key) {
        super(key);
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Key Events", "Key-Up Event");
        super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
