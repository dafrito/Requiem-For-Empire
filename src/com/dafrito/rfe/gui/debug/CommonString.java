/**
 * 
 */
package com.dafrito.rfe.gui.debug;

/**
 * A collection of commonly used strings, cached for memory performance.
 * 
 * @author Aaron Faanes
 * 
 */
public enum CommonString {
	ELEMENTS("Elements"),
	SCRIPTGROUPPARENTHETICAL("Script Group (parenthetical)"),
	SCRIPTGROUPCURLY("Script Group (curly)"),
	NUMERICSCRIPTVALUESHORT("Numeric Script-Value (short)"),
	NUMERICSCRIPTVALUEINT("Numeric Script-Value (int)"),
	NUMERICSCRIPTVALUELONG("Numeric Script-Value (long)"),
	NUMERICSCRIPTVALUEFLOAT("Numeric Script-Value (float)"),
	NUMERICSCRIPTVALUEDOUBLE("Numeric Script-Value (double)"),
	PERMISSIONNULL("Permission: null"),
	PERMISSIONPRIVATE("Permission: private"),
	PERMISSIONPROTECTED("Permission: protected"),
	PERMISSIONPUBLIC("Permission: public"),
	SCRIPTLINE("Script Line: "),
	SCRIPTOPERATOR("Script Operator: "),
	ORIGINALSTRING("Original String: '"),
	REFERENCEDELEMENTNULL("Referenced Element: null"),
	OUTPUTTREE("Output Tree");

	private final String text;

	private CommonString(String text) {
		this.text = text;
	}

	/**
	 * Return the cached string.
	 * 
	 * @return the cached string
	 */
	public String getText() {
		return this.text;
	}
}