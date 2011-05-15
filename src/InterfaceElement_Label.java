import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class InterfaceElement_Label extends InterfaceElement implements Nodeable{
	private String m_string;
	public InterfaceElement_Label(ScriptEnvironment env,Stylesheet uniqueStyle, Stylesheet classStyle, String string){
		super(env,uniqueStyle,classStyle);
		m_string=string;
	}
	public String getString(){return m_string;}
	public void setString(String string){m_string=string;}
	public int getInternalWidth(){
		return (int)getDrawingBounds().getWidth();
	}
	public int getInternalHeight(){
		return (int)getDrawingBounds().getHeight()/2;
	}
	public void setPreferredWidth(int width){}
	public Rectangle getDrawingBounds(){
		Graphics2D g2d=getRoot().getGraphics();
		Rectangle2D boundingRect=getCurrentFont().getStringBounds(m_string, g2d.getFontRenderContext());
		return new Rectangle((int)boundingRect.getWidth(), (int)boundingRect.getHeight());
	}
	public void paint(Graphics2D g2d){
		assert Debugger.openNode("Label Painting Operations","Painting Label");
		assert Debugger.addNode(this);
		super.paint(g2d);
		g2d.drawString(m_string, getXAnchor(), getYAnchor()+getInternalHeight());
		assert Debugger.closeNode();
	}
	public boolean nodificate(){
		assert Debugger.openNode("Label Interface Element");
		assert super.nodificate();
		assert Debugger.addNode("Label: "+m_string);
		assert Debugger.closeNode();
		return true;
	}
}
