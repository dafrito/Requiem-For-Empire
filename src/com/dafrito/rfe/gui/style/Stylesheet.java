package com.dafrito.rfe.gui.style;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.proxies.FauxTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;

@Inspectable
public class Stylesheet extends FauxTemplate implements ScriptValue, ScriptConvertible<Stylesheet> {
	private final Map<StylesheetProperty, Object> styleElements = new EnumMap<StylesheetProperty, Object>(StylesheetProperty.class);
	public static final String STYLESHEETSTRING = "Stylesheet";

	public Stylesheet(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, STYLESHEETSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public Stylesheet(ScriptEnvironment env, boolean flag) {
		super(env, ScriptValueType.createType(env, STYLESHEETSTRING));
	}

	public void addElement(StylesheetProperty type, Object element) {
		assert Debugger.openNode("Stylesheet Element Additions", "Adding a " + element + " element to this stylesheet");
		assert Debugger.addNode(this);
		assert Debugger.addNode(element);
		this.styleElements.put(type, element);
		assert Debugger.closeNode();
	}

	// ScriptConvertible implementation
	@Override
	public Stylesheet convert(ScriptEnvironment env) {
		return this;
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract template) throws Exception_Nodeable {
		return null;
	}

	public Object getElement(StylesheetProperty elementCode) {
		return this.styleElements.get(elementCode);
	}

	// FauxTemplate extensions
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new Stylesheet(this.getEnvironment(), true);
	}

	@Inspectable
	public Map<StylesheetProperty, Object> getProperties() {
		return Collections.unmodifiableMap(this.styleElements);
	}
}
