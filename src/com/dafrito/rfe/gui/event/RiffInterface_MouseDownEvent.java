/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.gui.MouseButton;
import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class RiffInterface_MouseDownEvent extends RiffInterface_MouseEvent {
	public RiffInterface_MouseDownEvent(int x, int y, MouseButton button) {
		super(x, y, button);
	}
}