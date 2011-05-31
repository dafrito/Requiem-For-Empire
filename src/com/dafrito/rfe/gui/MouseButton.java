/**
 * 
 */
package com.dafrito.rfe.gui;

import com.dafrito.rfe.script.exceptions.Exception_InternalError;

public enum MouseButton {
	LEFT, MIDDLE, RIGHT;

	public static MouseButton getButton(int button) {
		switch (button) {
		case java.awt.event.MouseEvent.BUTTON1:
			return MouseButton.LEFT;
		case java.awt.event.MouseEvent.BUTTON2:
			return MouseButton.MIDDLE;
		case java.awt.event.MouseEvent.BUTTON3:
			return MouseButton.RIGHT;
		case java.awt.event.MouseEvent.NOBUTTON:
			return null;
		}
		throw new Exception_InternalError("Invalid default");
	}
}