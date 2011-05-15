class RiffInterface_ClickEvent extends RiffInterface_MouseEvent implements Nodeable {
	private final int m_clicks;

	public RiffInterface_ClickEvent(int x, int y, RiffInterface_MouseListener.MouseButton button, int clicks) {
		super(x, y, button);
		this.m_clicks = clicks;
	}

	public int getClicks() {
		return this.m_clicks;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Mouse-Click Events", "Click Event");
		assert super.nodificate();
		assert Debugger.addNode("Clicks: " + this.m_clicks);
		assert Debugger.closeNode();
		return true;
	}
}

class RiffInterface_DragEvent extends RiffInterface_MouseEvent implements Nodeable {
	private final int m_xOffset, m_yOffset;
	private final double m_distance;

	public RiffInterface_DragEvent(int x, int y, RiffInterface_MouseListener.MouseButton button, int xOffset, int yOffset, double distance) {
		super(x, y, button);
		this.m_xOffset = xOffset;
		this.m_yOffset = yOffset;
		this.m_distance = distance;
	}

	public double getDistance() {
		return this.m_distance;
	}

	public int getXOffset() {
		return this.m_xOffset;
	}

	public int getYOffset() {
		return this.m_yOffset;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Mouse-Drag Events", "Mouse-Drag Event");
		assert super.nodificate();
		assert Debugger.addNode("X-Offset: " + this.getXOffset());
		assert Debugger.addNode("Y-Offset: " + this.getYOffset());
		assert Debugger.closeNode();
		return true;
	}
}

class RiffInterface_MouseDownEvent extends RiffInterface_MouseEvent implements Nodeable {
	public RiffInterface_MouseDownEvent(int x, int y, RiffInterface_MouseListener.MouseButton button) {
		super(x, y, button);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Mouse-Down Events", "Mouse-Down Event");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}

class RiffInterface_MouseEvent implements Nodeable, RiffInterface_Event {
	final int m_x, m_y;
	final RiffInterface_MouseListener.MouseButton m_button;

	public RiffInterface_MouseEvent(int x, int y, RiffInterface_MouseListener.MouseButton button) {
		this.m_x = x;
		this.m_y = y;
		this.m_button = button;
	}

	public RiffInterface_MouseListener.MouseButton getButton() {
		return this.m_button;
	}

	public int getX() {
		return this.m_x;
	}

	public int getY() {
		return this.m_y;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Generic Mouse Event");
		assert Debugger.addNode("X: " + this.getX());
		assert Debugger.addNode("Y: " + this.getY());
		assert Debugger.addNode("Button: " + this.getButton());
		assert Debugger.closeNode();
		return true;
	}
}

public interface RiffInterface_MouseListener {
	public enum MouseButton {
		LEFT, MIDDLE, RIGHT
	}

	public void riffMouseEvent(RiffInterface_MouseEvent event);
}

class RiffInterface_MouseUpEvent extends RiffInterface_MouseEvent implements Nodeable {
	public RiffInterface_MouseUpEvent(int x, int y, RiffInterface_MouseListener.MouseButton button) {
		super(x, y, button);
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Mouse-Up Events", "Mouse-Up Event");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
