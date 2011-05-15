import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class InterfaceElement_Label extends InterfaceElement implements Nodeable {
	private String m_string;

	public InterfaceElement_Label(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle, String string) {
		super(env, uniqueStyle, classStyle);
		this.m_string = string;
	}

	@Override
	public Rectangle getDrawingBounds() {
		Graphics2D g2d = this.getRoot().getGraphics();
		Rectangle2D boundingRect = this.getCurrentFont().getStringBounds(this.m_string, g2d.getFontRenderContext());
		return new Rectangle((int) boundingRect.getWidth(), (int) boundingRect.getHeight());
	}

	@Override
	public int getInternalHeight() {
		return (int) this.getDrawingBounds().getHeight() / 2;
	}

	@Override
	public int getInternalWidth() {
		return (int) this.getDrawingBounds().getWidth();
	}

	public String getString() {
		return this.m_string;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Label Interface Element");
		assert super.nodificate();
		assert Debugger.addNode("Label: " + this.m_string);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void paint(Graphics2D g2d) {
		assert Debugger.openNode("Label Painting Operations", "Painting Label");
		assert Debugger.addNode(this);
		super.paint(g2d);
		g2d.drawString(this.m_string, this.getXAnchor(), this.getYAnchor() + this.getInternalHeight());
		assert Debugger.closeNode();
	}

	@Override
	public void setPreferredWidth(int width) {
	}

	public void setString(String string) {
		this.m_string = string;
	}
}
