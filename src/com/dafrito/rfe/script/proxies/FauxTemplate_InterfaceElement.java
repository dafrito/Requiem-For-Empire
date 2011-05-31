package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.gui.InterfaceElement;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.Parser;
import com.dafrito.rfe.script.Referenced;
import com.dafrito.rfe.script.ScriptConvertible;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.ScriptFunction;
import com.dafrito.rfe.script.ScriptKeywordType;
import com.dafrito.rfe.script.ScriptTemplate;
import com.dafrito.rfe.script.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.ScriptValue;
import com.dafrito.rfe.script.ScriptValueType;
import com.dafrito.rfe.script.ScriptValue_Faux;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.style.Stylesheet;

public class FauxTemplate_InterfaceElement extends FauxTemplate implements Nodeable, ScriptConvertible {
	public static final String INTERFACEELEMENTSTRING = "InterfaceElement";
	private InterfaceElement element;

	public FauxTemplate_InterfaceElement(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, INTERFACEELEMENTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
	}

	public FauxTemplate_InterfaceElement(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
	}

	public FauxTemplate_InterfaceElement(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> implemented, boolean isAbstract) {
		super(env, type, extended, implemented, isAbstract);
	}

	// Nodeable implementation
	@Override
	public Object convert() {
		return this.getElement();
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Interface Element Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_InterfaceElement template = (FauxTemplate_InterfaceElement) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		ScriptValue value;
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_InterfaceElement) this.createObject(ref, template);
			}
			switch (params.size()) {
			// Intentionally out of order to allow for case 2 to run case 1's code.
			case 2:
				value = params.get(1);
				template.getElement().setClassStylesheet((Stylesheet) value.getValue());
			case 1:
				value = params.get(0);
				template.getElement().setUniqueStylesheet((Stylesheet) value.getValue());
				break;
			}
		} else if (name.equals("getUniqueStylesheet")) {
			returning = Parser.getRiffStylesheet(template.getElement().getUniqueStylesheet());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getClassStylesheet")) {
			returning = Parser.getRiffStylesheet(template.getElement().getClassStylesheet());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setUniqueStylesheet")) {
			template.getElement().setUniqueStylesheet(Parser.getStylesheet(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setClassStylesheet")) {
			template.getElement().setClassStylesheet(Parser.getStylesheet(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}
		params.clear();
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public InterfaceElement getElement() {
		return this.element;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing interface element faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		this.addConstructor(this.getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue>();
		this.addFauxFunction("getUniqueStylesheet", ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING), new LinkedList<ScriptValue>(), ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getClassStylesheet", ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING), new LinkedList<ScriptValue>(), ScriptKeywordType.PUBLIC, false, false);
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		this.addFauxFunction("setUniqueStylesheet", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setClassStylesheet", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_InterfaceElement(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Interface Element Faux Template");
		super.nodificate();
		if (this.getElement() == null) {
			assert Debugger.addNode("Interface Element: null");
		} else {
			assert Debugger.addNode(this.getElement());
		}
		assert Debugger.closeNode();
	}

	public void setElement(InterfaceElement element) {
		assert Debugger.openNode("Interface Element Faux Template Changes", "Changing Interface Element");
		assert Debugger.addNode(this);
		assert Debugger.addNode(element);
		this.element = element;
		assert Debugger.closeNode();
	}
}
