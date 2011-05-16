package com.dafrito.rfe;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Stylesheet extends FauxTemplate implements Nodeable, ScriptValue_Abstract, ScriptConvertible {
	private Map<StylesheetElementType, StylesheetElement> styleElements = new HashMap<StylesheetElementType, StylesheetElement>(); // element code, element
	private String name;
	private boolean isUnique;
	public static final String STYLESHEETSTRING = "Stylesheet";

	public Stylesheet(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, STYLESHEETSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public Stylesheet(ScriptEnvironment env, boolean flag) {
		super(env, ScriptValueType.createType(env, STYLESHEETSTRING));
	}

	public void addElement(StylesheetElementType type, StylesheetElement element) {
		assert Debugger.openNode("Stylesheet Element Additions", "Adding a" + element.getElementName() + " element to this stylesheet");
		assert Debugger.addNode(this);
		assert Debugger.addNode(element);
		this.styleElements.put(type, element);
		assert Debugger.closeNode();
	}

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		return this;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract template) throws Exception_Nodeable {
		return null;
	}

	public StylesheetElement getElement(StylesheetElementType elementCode) {
		return this.styleElements.get(elementCode);
	}

	public String getName() {
		return this.name;
	}

	// FauxTemplate extensions
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new Stylesheet(this.getEnvironment(), true);
	}

	public boolean isUnique() {
		return this.isUnique;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		if (this.name == null) {
			assert Debugger.openNode("Anonymous stylesheet (" + this.styleElements.size() + " element(s))");
		} else {
			assert Debugger.openNode("Stylesheet: " + this.name + " (" + this.styleElements.size() + " element(s))");
		}
		assert Debugger.addNode(this.styleElements.values());
		assert Debugger.closeNode();
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
}
