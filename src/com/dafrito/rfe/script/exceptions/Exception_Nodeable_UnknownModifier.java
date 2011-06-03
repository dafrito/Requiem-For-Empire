/**
 * 
 */
package com.dafrito.rfe.script.exceptions;

import java.util.List;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.script.parsing.Referenced;

public class Exception_Nodeable_UnknownModifier extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7862513783807915122L;
	private List<Object> modifiers;

	public Exception_Nodeable_UnknownModifier(Referenced ref, List<Object> modifiers) {
		super(ref);
		this.modifiers = modifiers;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("These modifiers (or what are believed to be modifiers) are unparseable to the compiler", this.modifiers);
	}

	@Override
	public String getName() {
		return "Unknown Modifier(s)";
	}
}