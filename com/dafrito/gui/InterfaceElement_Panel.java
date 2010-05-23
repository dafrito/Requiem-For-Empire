package com.dafrito.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.util.Terrestrial;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.economy.ArchetypeMapNode;
import com.dafrito.economy.Asset;
import com.dafrito.geom.DiscreteRegion;
import com.dafrito.geom.Point;
import com.dafrito.geom.Point_Euclidean;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.script.Parser;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.executable.ScriptExecutable_CallFunction;
import com.dafrito.script.templates.FauxTemplate_Panel;
import com.dafrito.script.templates.FauxTemplate_RiffDali;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class InterfaceElement_Panel extends InterfaceElement implements Interface_Container,
                                                                        RiffInterface_MouseListener {
    private List<GraphicalElement> elements;
    private Point_Euclidean offset;
    private Terrestrial terrestrial;
    private ScriptTemplate_Abstract dali;
    private Graphics2D graphics;

    public InterfaceElement_Panel(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
        super(env, uniqueStyle, classStyle);
        this.offset = new Point_Euclidean(env, 0, 0, 0);
        this.elements = new LinkedList<GraphicalElement>();
        this.dali = env.getTemplate(FauxTemplate_RiffDali.RIFFDALISTRING);
    }

    public Terrestrial getTerrestrial() {
        return this.terrestrial;
    }

    public void setTerrestrial(Terrestrial terrestrial) {
        this.terrestrial = terrestrial;
    }

    public InterfaceElement getContainerElement() {
        return this;
    }

    public void setRiffDali(ScriptTemplate_Abstract dali) {
        this.dali = dali;
    }

    public void clear() {
        this.elements.clear();
    }

    public void add(GraphicalElement element) {
        this.elements.add(element);
        element.setParent(this);
        if (getRoot() != null) {
            getRoot().repaint();
        }
    }

    public void addAll(List<GraphicalElement> list) {
        for (GraphicalElement element : list) {
            this.add(element);
        }
        if (getRoot() != null) {
            getRoot().repaint();
        }
    }

    public int size() {
        return this.elements.size();
    }

    public List<GraphicalElement> getElements() {
        return Collections.unmodifiableList(this.elements);
    }

    public Point getOffset() {
        return this.offset;
    }

    public void drawTransformedRegion(DiscreteRegion region) {
        DiscreteRegion.paint(getGraphics(), DiscreteRegion.transform(region, getOffset(), getDrawingBounds(), false),
                             getDrawingBounds(), false);
    }

    public void fillTransformedRegion(DiscreteRegion region) {
        DiscreteRegion.paint(getGraphics(), DiscreteRegion.transform(region, getOffset(), getDrawingBounds(), false),
                             getDrawingBounds(), true);
    }

    public void drawRegion(DiscreteRegion region) {
        DiscreteRegion.paint(getGraphics(), DiscreteRegion.transform(region, getOffset(), getDrawingBounds(), true),
                             getDrawingBounds(), false);
    }

    public void fillRegion(DiscreteRegion region) {
        DiscreteRegion.paint(getGraphics(), DiscreteRegion.transform(region, getOffset(), getDrawingBounds(), true),
                             getDrawingBounds(), true);
    }

    public void drawString(String string, Color color, Point location) {
        Color oldColor = getGraphics().getColor();
        getGraphics().setColor(color);
        Point point = DiscreteRegion.transformPoint(getEnvironment(), location, getOffset(), getDrawingBounds(), true);
        getGraphics().drawString(string, (int)point.getX(), (int)point.getY());
        getGraphics().setColor(oldColor);
    }

    public Graphics2D getGraphics() {
        return this.graphics;
    }

    @Override
    public void paint(Graphics2D g2d) {
        Set<DiscreteRegion> regions;
        this.graphics = g2d;
        if (this.terrestrial.getTree() == null) {
            regions = new HashSet<DiscreteRegion>();
        } else {
            regions = this.terrestrial.getTree().getRegionList();
        }
        assert LegacyDebugger.open("Painting Panel Elements (" + this.elements.size() + " element(s), " + regions.size() + " region(s))");
        super.paint(g2d);
        assert LegacyDebugger.addNode("X-offset: " + this.offset.getX());
        assert LegacyDebugger.addNode("Y-offset: " + this.offset.getY());
        assert LegacyDebugger.addNode("Zoom factor: " + this.offset.getZ());
        try {
            List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
            params.add((ScriptValue_Abstract)convert());
            List<ScriptValue_Abstract> regionList = new LinkedList<ScriptValue_Abstract>();
            List<ScriptValue_Abstract> assetList = new LinkedList<ScriptValue_Abstract>();
            for (DiscreteRegion region : regions) {
                regionList.add((ScriptValue_Abstract)region.convert());
                if (region.getProperty("Archetypes") != null) {
                    for (Asset asset : ((ArchetypeMapNode)region.getProperty("Archetypes")).getAllAssets()) {
                        assetList.add((ScriptValue_Abstract)asset.convert());
                    }
                }
            }
            params.add(Parser.getRiffList(getEnvironment(), regionList));
            params.add(Parser.getRiffList(getEnvironment(), assetList));
            if (this.dali != null) {
                ScriptExecutable_CallFunction.callFunction(getEnvironment(), null, this.dali, "paintPanel", params);
            }
        } catch (Exception_Nodeable exception) {
            throw new Exception_InternalError(getEnvironment(), exception);
        }
        for (GraphicalElement elem : this.elements) {
            elem.paint(g2d);
        }
        g2d.setColor(getBackgroundColor());
        g2d.draw(getDrawingBounds());
        assert LegacyDebugger.close();
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    public void riffMouseEvent(RiffInterface_MouseEvent event) {
        if (event instanceof RiffInterface_DragEvent) {
            if (event.getButton() == RiffInterface_MouseListener.MouseButton.LEFT) {
                this.offset.addX(-((RiffInterface_DragEvent)event).getXOffset() / Math.pow(2, this.offset.getZ()));
                this.offset.addY(-((RiffInterface_DragEvent)event).getYOffset() / Math.pow(2, this.offset.getZ()));
            }
            if (event.getButton() == RiffInterface_MouseListener.MouseButton.RIGHT) {
                this.offset.addZ(((RiffInterface_DragEvent)event).getDistance() / 50);
            }
        }
    }

    // ScriptConvertible implementation

    @Override
    public Object convert() {
        FauxTemplate_Panel panel = new FauxTemplate_Panel(getEnvironment(),
                                                          ScriptValueType.createType(getEnvironment(),
                                                                                     FauxTemplate_Panel.PANELSTRING));
        panel.setElement(this);
        return panel;
    }

    // Nodeable implementation

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Panel Interface Element");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Terrestrial", this.terrestrial);
        assert LegacyDebugger.addSnapNode("Graphical Elements: (" + this.elements.size() + " element(s))",
                                          this.elements);
        assert LegacyDebugger.close();
        return true;
    }

}
