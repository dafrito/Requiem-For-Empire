package com.dafrito.gui;

import com.dafrito.debug.Nodeable;
import com.dafrito.logging.LegacyDebugger;

public class RiffInterface_MouseEvent implements Nodeable, RiffInterface_Event {
    final int x, y;
    final RiffInterface_MouseListener.MouseButton button;

    public RiffInterface_MouseEvent(int x, int y, RiffInterface_MouseListener.MouseButton button) {
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public RiffInterface_MouseListener.MouseButton getButton() {
        return this.button;
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Generic Mouse Event");
        assert LegacyDebugger.addNode("X: " + getX());
        assert LegacyDebugger.addNode("Y: " + getY());
        assert LegacyDebugger.addNode("Button: " + getButton());
        assert LegacyDebugger.close();
        return true;
    }
}
