/**
 * 
 */
package com.dafrito.rfe.gui.event;

import com.dafrito.rfe.inspect.Inspectable;

@Inspectable
public class KeyEvent_KeyDown extends RiffInterface_KeyEvent {
	public KeyEvent_KeyDown(int key) {
		super(key);
	}
}