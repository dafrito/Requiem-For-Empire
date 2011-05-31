package com.dafrito.rfe.gui;
import java.util.List;


public interface Interface_Container {
	public void add(GraphicalElement element);

	public void clear();

	public InterfaceElement getContainerElement();

	public List<GraphicalElement> getElements();
}
