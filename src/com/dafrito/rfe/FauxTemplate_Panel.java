package com.dafrito.rfe;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.inspect.Nodeable;

public class FauxTemplate_Panel extends FauxTemplate_InterfaceElement implements ScriptConvertible, Nodeable {
	public static final String PANELSTRING = "Panel";

	public FauxTemplate_Panel(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, PANELSTRING), ScriptValueType.createType(env, FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Panel(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.setElement(new InterfaceElement_Panel(env, null, null));
	}

	// Nodeable and ScriptConvertible implementations
	@Override
	public Object convert() {
		return this.getElement();
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Panel Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Panel template = (FauxTemplate_Panel) rawTemplate;
		ScriptValue returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Panel) this.createObject(ref, template);
			}
		} else if (name.equals("add")) {
			template.getPanel().add(Parser.getGraphicalElement(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("getTerrestrial")) {
			returning = Parser.getRiffTerrestrial(ref.getEnvironment(), template.getPanel().getTerrestrial());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setTerrestrial")) {
			template.getPanel().setTerrestrial(Parser.getTerrestrial(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setRiffDali")) {
			template.getPanel().setRiffDali(Parser.getTemplate(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("drawRegion")) {
			template.getPanel().drawRegion(Parser.getDiscreteRegion(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("fillRegion")) {
			template.getPanel().fillRegion(Parser.getDiscreteRegion(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("drawTransformedRegion")) {
			template.getPanel().drawTransformedRegion(Parser.getDiscreteRegion(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("fillTransformedRegion")) {
			template.getPanel().fillTransformedRegion(Parser.getDiscreteRegion(params.get(0)));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("drawString")) {
			template.getPanel().drawString(Parser.getString(params.get(0)), Parser.getColor(params.get(1)), Parser.getPoint(params.get(2)));
			assert Debugger.closeNode();
			return null;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public InterfaceElement_Panel getPanel() {
		return (InterfaceElement_Panel) this.getElement();
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing panel faux template");
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

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING)));
		this.addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		this.addFauxFunction("getTerrestrial", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
		this.addFauxFunction("setTerrestrial", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_RiffDali.RIFFDALISTRING)));
		this.addFauxFunction("setRiffDali", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
		this.addFauxFunction("drawRegion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
		this.addFauxFunction("fillRegion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
		this.addFauxFunction("drawTransformedRegion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
		this.addFauxFunction("fillTransformedRegion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		fxnParams = ScriptValueType.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Color.COLORSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		this.addFauxFunction("drawString", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Panel(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Panel Faux Template");
		super.nodificate();
		assert Debugger.closeNode();
	}
}
