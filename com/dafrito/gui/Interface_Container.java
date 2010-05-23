package com.dafrito.gui;

import java.util.List;
public interface Interface_Container{
	public void clear();
	public void add(GraphicalElement element);
	public List<GraphicalElement> getElements();
	public InterfaceElement getContainerElement();
}
