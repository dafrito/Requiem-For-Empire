package com.dafrito.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.script.ScriptEnvironment;

public class InterfaceElement_Rectangle extends InterfaceElement {
    public InterfaceElement_Rectangle(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
        super(env, uniqueStyle, classStyle);
    }

    @Override
    public void paint(Graphics2D g2d) {
        assert LegacyDebugger.open("Rectangle Painting Operations", "Painting Rectangle");
        assert LegacyDebugger.addNode(this);
        super.paint(g2d);
        g2d.fill(new Rectangle(getXAnchor(), getYAnchor(), getInternalWidth(), getInternalHeight()));
        assert LegacyDebugger.close();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Rectangle Interface Element");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
