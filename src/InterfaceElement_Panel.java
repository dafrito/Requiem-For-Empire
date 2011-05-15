import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class InterfaceElement_Panel extends InterfaceElement implements Interface_Container, RiffInterface_MouseListener, Nodeable, ScriptConvertible {
	private List<GraphicalElement> m_elements;
	private Point_Euclidean m_offset;
	private Terrestrial m_terrestrial;
	private ScriptTemplate_Abstract m_dali;
	private Graphics2D m_graphics;

	public InterfaceElement_Panel(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle) {
		super(env, uniqueStyle, classStyle);
		this.m_offset = new Point_Euclidean(env, 0, 0, 0);
		this.m_elements = new LinkedList<GraphicalElement>();
		this.m_dali = env.getTemplate(FauxTemplate_RiffDali.RIFFDALISTRING);
	}

	@Override
	public void add(GraphicalElement element) {
		this.m_elements.add(element);
		element.setParent(this);
		if (this.getRoot() != null) {
			this.getRoot().repaint();
		}
	}

	public void addAll(List list) {
		for (int i = 0; i < list.size(); i++) {
			this.add((GraphicalElement) list.get(i));
		}
		if (this.getRoot() != null) {
			this.getRoot().repaint();
		}
	}

	@Override
	public void clear() {
		this.m_elements.clear();
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
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

	@Override
	public List<GraphicalElement> getElements() {
		return Collections.unmodifiableList(this.m_elements);
	}

	public Graphics2D getGraphics() {
		return this.m_graphics;
	}

	public Point getOffset() {
		return this.m_offset;
	}

	public Terrestrial getTerrestrial() {
		return this.m_terrestrial;
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Panel Interface Element");
		assert super.nodificate();
		assert Debugger.addSnapNode("Terrestrial", this.m_terrestrial);
		assert Debugger.addSnapNode("Graphical Elements: (" + this.m_elements.size() + " element(s))", this.m_elements);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void paint(Graphics2D g2d) {
		Set<DiscreteRegion> regions;
		this.m_graphics = g2d;
		if (this.m_terrestrial.getTree() == null) {
			regions = new HashSet<DiscreteRegion>();
		} else {
			regions = this.m_terrestrial.getTree().getRegionList();
		}
		assert Debugger.openNode("Painting Panel Elements (" + this.m_elements.size() + " element(s), " + regions.size() + " region(s))");
		super.paint(g2d);
		assert Debugger.addNode("X-offset: " + this.m_offset.getX());
		assert Debugger.addNode("Y-offset: " + this.m_offset.getY());
		assert Debugger.addNode("Zoom factor: " + this.m_offset.getZ());
		try {
			List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
			params.add((ScriptValue_Abstract) this.convert());
			List<ScriptValue_Abstract> regionList = new LinkedList<ScriptValue_Abstract>();
			List<ScriptValue_Abstract> assetList = new LinkedList<ScriptValue_Abstract>();
			for (DiscreteRegion region : regions) {
				regionList.add((ScriptValue_Abstract) region.convert());
				if (region.getProperty("Archetypes") != null) {
					for (Asset asset : ((ArchetypeMapNode) region.getProperty("Archetypes")).getAllAssets()) {
						assetList.add((ScriptValue_Abstract) asset.convert());
					}
				}
			}
			params.add(Parser.getRiffList(this.getEnvironment(), regionList));
			params.add(Parser.getRiffList(this.getEnvironment(), assetList));
			if (this.m_dali != null) {
				ScriptExecutable_CallFunction.callFunction(this.getEnvironment(), null, this.m_dali, "paintPanel", params);
			}
		} catch (Exception_Nodeable exception) {
			throw new Exception_InternalError(this.getEnvironment(), exception);
		}
		for (GraphicalElement elem : this.m_elements) {
			elem.paint(g2d);
		}
		g2d.setColor(this.getBackgroundColor());
		g2d.draw(this.getDrawingBounds());
		assert Debugger.closeNode();
	}

	@Override
	public void riffMouseEvent(RiffInterface_MouseEvent event) {
		if (event instanceof RiffInterface_DragEvent) {
			if (event.getButton() == RiffInterface_MouseListener.MouseButton.LEFT) {
				this.m_offset.addX(-((RiffInterface_DragEvent) event).getXOffset() / Math.pow(2, this.m_offset.getZ()));
				this.m_offset.addY(-((RiffInterface_DragEvent) event).getYOffset() / Math.pow(2, this.m_offset.getZ()));
			}
			if (event.getButton() == RiffInterface_MouseListener.MouseButton.RIGHT) {
				this.m_offset.addZ(((RiffInterface_DragEvent) event).getDistance() / 50);
			}
		}
	}

	@Override
	public void setPreferredWidth(int width) {
	}

	public void setRiffDali(ScriptTemplate_Abstract dali) {
		this.m_dali = dali;
	}

	public void setTerrestrial(Terrestrial terrestrial) {
		this.m_terrestrial = terrestrial;
	}

	public int size() {
		return this.m_elements.size();
	}
}
