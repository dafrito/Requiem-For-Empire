import java.awt.*;

public class InterfaceElement_Rectangle extends InterfaceElement implements Nodeable{
	public InterfaceElement_Rectangle(ScriptEnvironment env, Stylesheet uniqueStyle, Stylesheet classStyle){
		super(env,uniqueStyle,classStyle);
	}
	public void setPreferredWidth(int width){}
	public void paint(Graphics2D g2d){
		assert Debugger.openNode("Rectangle Painting Operations","Painting Rectangle");
		assert Debugger.addNode(this);
		super.paint(g2d);
		g2d.fill(new Rectangle(getXAnchor(),getYAnchor(),getInternalWidth(),getInternalHeight()));
		assert Debugger.closeNode();
	}
	public boolean nodificate(){
		assert Debugger.openNode("Rectangle Interface Element");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
