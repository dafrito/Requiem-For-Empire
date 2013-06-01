package com.dafrito.rfe.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.dafrito.rfe.ArchetypeMapNode;
import com.dafrito.rfe.Asset;
import com.dafrito.rfe.Terrestrial;
import com.dafrito.rfe.geom.DiscreteRegion;
import com.dafrito.rfe.geom.points.Point;
import com.dafrito.rfe.geom.points.EuclideanPoint;
import com.dafrito.rfe.gui.event.RiffInterface_DragEvent;
import com.dafrito.rfe.gui.event.RiffInterface_MouseEvent;
import com.dafrito.rfe.gui.event.RiffInterface_MouseListener;
import com.dafrito.rfe.gui.style.Stylesheet;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.logging.Logs;
import com.dafrito.rfe.script.Conversions;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.operations.ScriptExecutable_CallFunction;
import com.dafrito.rfe.script.proxies.FauxTemplate_Panel;
import com.dafrito.rfe.script.proxies.FauxTemplate_RiffDali;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

/**
 * A scriptable panel that draws {@link DiscreteRegion} and {@link Asset}
 * objects.
 * 
 * @author Aaron Faanes
 * @see FauxTemplate_RiffDali
 */
public class InterfaceElement_Panel extends InterfaceElement implements ScriptConvertible<FauxTemplate_Panel>, Interface_Container, RiffInterface_MouseListener {
	private List<GraphicalElement> elements;
	private EuclideanPoint offset;
	private Terrestrial terrestrial;
	private ScriptTemplate_Abstract dali;
	private Graphics2D graphics;

	public InterfaceElement_Panel(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
		super(env, uniqueStyle, classStyle);
		this.offset = new EuclideanPoint(0, 0, 0);
		this.elements = new LinkedList<GraphicalElement>();
		this.dali = env.getTemplate(FauxTemplate_RiffDali.RIFFDALISTRING);
	}

	@Override
	public void add(GraphicalElement element) {
		this.elements.add(element);
		element.setParent(this);
		if (this.getRoot() != null) {
			this.getRoot().repaint();
		}
	}

	public void addAll(List<GraphicalElement> list) {
		for (int i = 0; i < list.size(); i++) {
			this.add(list.get(i));
		}
		if (this.getRoot() != null) {
			this.getRoot().repaint();
		}
	}

	@Override
	public void clear() {
		this.elements.clear();
	}

	// ScriptConvertible implementation
	@Override
	public FauxTemplate_Panel convert(ScriptEnvironment env) {
		FauxTemplate_Panel panel = new FauxTemplate_Panel(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Panel.PANELSTRING));
		panel.setElement(this);
		return panel;
	}

	public void drawRegion(DiscreteRegion region) {
		DiscreteRegion.paint(this.getGraphics(), DiscreteRegion.transform(region, this.getOffset(), this.getDrawingBounds(), true), this.getDrawingBounds(), false);
	}

	public void drawString(String string, Color color, Point location) {
		Color oldColor = this.getGraphics().getColor();
		this.getGraphics().setColor(color);
		Point point = DiscreteRegion.transformPoint(this.getEnvironment(), location, this.getOffset(), this.getDrawingBounds(), true);
		this.getGraphics().drawString(string, (int) point.getX(), (int) point.getY());
		this.getGraphics().setColor(oldColor);
	}

	public void drawTransformedRegion(DiscreteRegion region) {
		DiscreteRegion.paint(this.getGraphics(), DiscreteRegion.transform(region, this.getOffset(), this.getDrawingBounds(), false), this.getDrawingBounds(), false);
	}

	public void fillRegion(DiscreteRegion region) {
		DiscreteRegion.paint(this.getGraphics(), DiscreteRegion.transform(region, this.getOffset(), this.getDrawingBounds(), true), this.getDrawingBounds(), true);
	}

	public void fillTransformedRegion(DiscreteRegion region) {
		DiscreteRegion.paint(this.getGraphics(), DiscreteRegion.transform(region, this.getOffset(), this.getDrawingBounds(), false), this.getDrawingBounds(), true);
	}

	@Override
	public InterfaceElement getContainerElement() {
		return this;
	}

	@Inspectable
	@Override
	public List<GraphicalElement> getElements() {
		return Collections.unmodifiableList(this.elements);
	}

	public Graphics2D getGraphics() {
		return this.graphics;
	}

	public Point getOffset() {
		return this.offset;
	}

	@Inspectable
	public Terrestrial getTerrestrial() {
		return this.terrestrial;
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	@Override
	public void paint(Graphics2D g2d) {
		Set<DiscreteRegion> regions;
		this.graphics = g2d;
		if (this.terrestrial.getTree() == null) {
			regions = Collections.emptySet();
		} else {
			regions = this.terrestrial.getTree().getRegionList();
		}
		assert Logs.openNode("Painting Panel Elements (" + this.elements.size() + " element(s), " + regions.size() + " region(s))");
		super.paint(g2d);
		assert Logs.addNode("X-offset: " + this.offset.getX());
		assert Logs.addNode("Y-offset: " + this.offset.getY());
		assert Logs.addNode("Zoom factor: " + this.offset.getZ());
		try {
			List<ScriptValue> params = new LinkedList<ScriptValue>();
			params.add(this.convert(this.getEnvironment()));
			List<ScriptValue> regionList = new LinkedList<ScriptValue>();
			List<ScriptValue> assetList = new LinkedList<ScriptValue>();
			for (DiscreteRegion region : regions) {
				regionList.add(Conversions.wrapDiscreteRegion(this.getEnvironment(), region));
				if (region.getProperty("Archetypes") != null) {
					for (Asset asset : ((ArchetypeMapNode) region.getProperty("Archetypes")).getAllAssets()) {
						assetList.add(Conversions.wrapAsset(this.getEnvironment(), asset));
					}
				}
			}
			params.add(Conversions.wrapList(this.getEnvironment(), regionList));
			params.add(Conversions.wrapList(this.getEnvironment(), assetList));
			if (this.dali != null) {
				ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, this.dali, "paintPanel", params);
			}
		} catch (ScriptException exception) {
			throw new Exception_InternalError(this.getEnvironment(), exception);
		}
		for (GraphicalElement elem : this.elements) {
			elem.paint(g2d);
		}
		g2d.setColor(this.getBackgroundColor());
		g2d.draw(this.getDrawingBounds());
		assert Logs.closeNode();
	}

	@Override
	public void riffMouseEvent(RiffInterface_MouseEvent event) {
		if (event instanceof RiffInterface_DragEvent) {
			if (event.getButton() == MouseButton.LEFT) {
				this.offset.addX(-((RiffInterface_DragEvent) event).getXOffset() / Math.pow(2, this.offset.getZ()));
				this.offset.addY(-((RiffInterface_DragEvent) event).getYOffset() / Math.pow(2, this.offset.getZ()));
			}
			if (event.getButton() == MouseButton.RIGHT) {
				this.offset.addZ(((RiffInterface_DragEvent) event).getDistance() / 50);
			}
		}
	}

	@Override
	public void setPreferredWidth(int width) {
	}

	public void setRiffDali(ScriptTemplate_Abstract dali) {
		this.dali = dali;
	}

	public void setTerrestrial(Terrestrial terrestrial) {
		this.terrestrial = terrestrial;
	}

	public int size() {
		return this.elements.size();
	}
}
