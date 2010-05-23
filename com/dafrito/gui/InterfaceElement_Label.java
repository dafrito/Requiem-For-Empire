package com.dafrito.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.script.ScriptEnvironment;

public class InterfaceElement_Label extends InterfaceElement {
    private String string;

    public InterfaceElement_Label(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle, String string) {
        super(env, uniqueStyle, classStyle);
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public int getInternalWidth() {
        return (int)getDrawingBounds().getWidth();
    }

    @Override
    public int getInternalHeight() {
        return (int)getDrawingBounds().getHeight() / 2;
    }

    @Override
    public Rectangle getDrawingBounds() {
        Graphics2D g2d = getRoot().getGraphics();
        Rectangle2D boundingRect = getCurrentFont().getStringBounds(this.string, g2d.getFontRenderContext());
        return new Rectangle((int)boundingRect.getWidth(), (int)boundingRect.getHeight());
    }

    @Override
    public void paint(Graphics2D g2d) {
        assert LegacyDebugger.open("Label Painting Operations", "Painting Label");
        assert LegacyDebugger.addNode(this);
        super.paint(g2d);
        g2d.drawString(this.string, getXAnchor(), getYAnchor() + getInternalHeight());
        assert LegacyDebugger.close();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Label Interface Element");
        assert super.nodificate();
        assert LegacyDebugger.addNode("Label: " + this.string);
        assert LegacyDebugger.close();
        return true;
    }
}
