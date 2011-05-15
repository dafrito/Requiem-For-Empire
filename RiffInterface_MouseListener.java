public interface RiffInterface_MouseListener{
	public enum MouseButton{LEFT,MIDDLE,RIGHT}
	public void riffMouseEvent(RiffInterface_MouseEvent event);
}
class RiffInterface_MouseEvent implements Nodeable,RiffInterface_Event{
	final int m_x,m_y;
	final RiffInterface_MouseListener.MouseButton m_button;
	public RiffInterface_MouseEvent(int x,int y,RiffInterface_MouseListener.MouseButton button){
		m_x=x;
		m_y=y;
		m_button=button;
	}
	public int getX(){return m_x;}
	public int getY(){return m_y;}
	public RiffInterface_MouseListener.MouseButton getButton(){return m_button;}
	public boolean nodificate(){
		assert Debugger.openNode("Generic Mouse Event");
		assert Debugger.addNode("X: " + getX());
		assert Debugger.addNode("Y: " + getY());
		assert Debugger.addNode("Button: " + getButton());
		assert Debugger.closeNode();
		return true;
	}
}
class RiffInterface_ClickEvent extends RiffInterface_MouseEvent implements Nodeable{
	private final int m_clicks;
	public RiffInterface_ClickEvent(int x,int y,RiffInterface_MouseListener.MouseButton button,int clicks){
		super(x,y,button);
		m_clicks=clicks;
	}
	public int getClicks(){return m_clicks;}
	public boolean nodificate(){
		assert Debugger.openNode("Mouse-Click Events","Click Event");
		assert super.nodificate();
		assert Debugger.addNode("Clicks: " + m_clicks);
		assert Debugger.closeNode();
		return true;
	}
}
class RiffInterface_MouseDownEvent extends RiffInterface_MouseEvent implements Nodeable{
	public RiffInterface_MouseDownEvent(int x,int y,RiffInterface_MouseListener.MouseButton button){
		super(x,y,button);
	}
	public boolean nodificate(){
		assert Debugger.openNode("Mouse-Down Events","Mouse-Down Event");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
class RiffInterface_MouseUpEvent extends RiffInterface_MouseEvent implements Nodeable{
	public RiffInterface_MouseUpEvent(int x,int y,RiffInterface_MouseListener.MouseButton button){
		super(x,y,button);
	}
	public boolean nodificate(){
		assert Debugger.openNode("Mouse-Up Events","Mouse-Up Event");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
class RiffInterface_DragEvent extends RiffInterface_MouseEvent implements Nodeable{
	private final int m_xOffset,m_yOffset;
	private final double m_distance;
	public RiffInterface_DragEvent(int x,int y,RiffInterface_MouseListener.MouseButton button,int xOffset,int yOffset,double distance){
		super(x,y,button);
		m_xOffset=xOffset;
		m_yOffset=yOffset;
		m_distance=distance;
	}
	public int getXOffset(){return m_xOffset;}
	public int getYOffset(){return m_yOffset;}
	public double getDistance(){return m_distance;}
	public boolean nodificate(){
		assert Debugger.openNode("Mouse-Drag Events","Mouse-Drag Event");
		assert super.nodificate();
		assert Debugger.addNode("X-Offset: " + getXOffset());
		assert Debugger.addNode("Y-Offset: " + getYOffset());
		assert Debugger.closeNode();
		return true;
	}
}
