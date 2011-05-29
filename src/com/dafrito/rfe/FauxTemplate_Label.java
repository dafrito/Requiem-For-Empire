package com.dafrito.rfe;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.inspect.Nodeable;

public class FauxTemplate_Label extends FauxTemplate_InterfaceElement implements ScriptConvertible, Nodeable {
	public static final String LABELSTRING = "Label";

	public FauxTemplate_Label(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, LABELSTRING), ScriptValueType.createType(env, FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Label(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.setElement(new InterfaceElement_Label(env, null, null, ""));
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Label Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Label template = (FauxTemplate_Label) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Label) this.createObject(ref, template);
			}
			String label = "";
			if (params.size() > 0) {
				label = Parser.getString(params.get(params.size() - 1));
				params.remove(params.size() - 1);
			}
			template.getLabel().setString(label);
		} else if (name.equals("setString")) {
			template.getLabel().setString(Parser.getString(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getString")) {
			returning = Parser.getRiffString(this.getEnvironment(), template.getLabel().getString());
			assert Debugger.closeNode();
			return returning;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public InterfaceElement_Label getLabel() {
		return (InterfaceElement_Label) this.getElement();
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing label faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("setLabel", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		this.addFauxFunction("getLabel", ScriptValueType.STRING, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Label(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		if (this.getLabel() != null) {
			assert Debugger.openNode("Label Faux Template (" + this.getLabel().getString() + ")");
		} else {
			assert Debugger.openNode("Label Faux Template");
		}
		super.nodificate();
		assert Debugger.closeNode();
	}
}
