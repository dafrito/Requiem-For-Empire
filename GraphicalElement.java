import java.awt.Graphics2D;
import java.awt.Rectangle;
public interface GraphicalElement{
	public Rectangle getDrawingBounds();
	public void paint(Graphics2D g2d);
	public ScriptEnvironment getEnvironment();
	public Interface_Container getParent();
	public void setParent(Interface_Container container);
	public boolean isFocusable();
	public void setXAnchor(int x);
	public void setYAnchor(int y);
	public void setPreferredWidth(int width);
}
