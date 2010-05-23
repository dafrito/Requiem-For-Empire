package com.dafrito.gui;

import com.dafrito.logging.LegacyDebugger;

public interface RiffInterface_MouseListener {
    public enum MouseButton {
        LEFT,
        MIDDLE,
        RIGHT;
    }

    public void riffMouseEvent(RiffInterface_MouseEvent event);
}


class RiffInterface_ClickEvent extends RiffInterface_MouseEvent {
    private final int clicks;

    public RiffInterface_ClickEvent(int x, int y, RiffInterface_MouseListener.MouseButton button, int clicks) {
        super(x, y, button);
        this.clicks = clicks;
    }

    public int getClicks() {
        return this.clicks;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Mouse-Click Events", "Click Event");
        assert super.nodificate();
        assert LegacyDebugger.addNode("Clicks: " + this.clicks);
        assert LegacyDebugger.close();
        return true;
    }
}

class RiffInterface_MouseDownEvent extends RiffInterface_MouseEvent {
    public RiffInterface_MouseDownEvent(int x, int y, RiffInterface_MouseListener.MouseButton button) {
        super(x, y, button);
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Mouse-Down Events", "Mouse-Down Event");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}

class RiffInterface_MouseUpEvent extends RiffInterface_MouseEvent {
    public RiffInterface_MouseUpEvent(int x, int y, RiffInterface_MouseListener.MouseButton button) {
        super(x, y, button);
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Mouse-Up Events", "Mouse-Up Event");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}

class RiffInterface_DragEvent extends RiffInterface_MouseEvent {
    private final int xOffset, yOffset;
    private final double distance;

    public RiffInterface_DragEvent(int x, int y, RiffInterface_MouseListener.MouseButton button, int xOffset,
                                   int yOffset, double distance) {
        super(x, y, button);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.distance = distance;
    }

    public int getXOffset() {
        return this.xOffset;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public double getDistance() {
        return this.distance;
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Mouse-Drag Events", "Mouse-Drag Event");
        assert super.nodificate();
        assert LegacyDebugger.addNode("X-Offset: " + getXOffset());
        assert LegacyDebugger.addNode("Y-Offset: " + getYOffset());
        assert LegacyDebugger.close();
        return true;
    }
}
