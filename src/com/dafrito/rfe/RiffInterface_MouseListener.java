package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Nodeable;

class RiffInterface_ClickEvent extends RiffInterface_MouseEvent implements Nodeable {
	private final int clicks;

	public RiffInterface_ClickEvent(int x, int y, MouseButton button, int clicks) {
		super(x, y, button);
		this.clicks = clicks;
	}

	public int getClicks() {
		return this.clicks;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Click Events", "Click Event");
		super.nodificate();
		assert Debugger.addNode("Clicks: " + this.clicks);
		assert Debugger.closeNode();
	}
}

class RiffInterface_DragEvent extends RiffInterface_MouseEvent implements Nodeable {
	private final int xOffset, yOffset;
	private final double distance;

	public RiffInterface_DragEvent(int x, int y, MouseButton button, int xOffset, int yOffset, double distance) {
		super(x, y, button);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.distance = distance;
	}

	public double getDistance() {
		return this.distance;
	}

	public int getXOffset() {
		return this.xOffset;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Drag Events", "Mouse-Drag Event");
		super.nodificate();
		assert Debugger.addNode("X-Offset: " + this.getXOffset());
		assert Debugger.addNode("Y-Offset: " + this.getYOffset());
		assert Debugger.closeNode();
	}
}

class RiffInterface_MouseDownEvent extends RiffInterface_MouseEvent implements Nodeable {
	public RiffInterface_MouseDownEvent(int x, int y, MouseButton button) {
		super(x, y, button);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Down Events", "Mouse-Down Event");
		super.nodificate();
		assert Debugger.closeNode();
	}
}

class RiffInterface_MouseEvent implements Nodeable, RiffInterface_Event {
	final int x, y;
	final MouseButton button;

	public RiffInterface_MouseEvent(int x, int y, MouseButton button) {
		this.x = x;
		this.y = y;
		this.button = button;
	}

	public MouseButton getButton() {
		return this.button;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Generic Mouse Event");
		assert Debugger.addNode("X: " + this.getX());
		assert Debugger.addNode("Y: " + this.getY());
		assert Debugger.addNode("Button: " + this.getButton());
		assert Debugger.closeNode();
	}
}

public interface RiffInterface_MouseListener {
	public void riffMouseEvent(RiffInterface_MouseEvent event);
}

class RiffInterface_MouseUpEvent extends RiffInterface_MouseEvent implements Nodeable {
	public RiffInterface_MouseUpEvent(int x, int y, MouseButton button) {
		super(x, y, button);
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Mouse-Up Events", "Mouse-Up Event");
		super.nodificate();
		assert Debugger.closeNode();
	}
}
