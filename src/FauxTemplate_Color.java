import java.util.LinkedList;
import java.util.List;

public class FauxTemplate_Color extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String COLORSTRING = "Color";
	private java.awt.Color m_color;

	public FauxTemplate_Color(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, COLORSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Color(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.m_color = java.awt.Color.BLACK;
	}

	// Nodeable and ScriptConvertible interfaces
	@Override
	public Object convert() {
		return this.m_color;
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing color faux template function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Color template = (FauxTemplate_Color) rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Color) this.createObject(ref, template);
			}
			if (params.size() == 1) {
				template.setColor(RiffJavaToolbox.getColor(Parser.getString(params.get(0))));
			} else if (params.size() == 3) {
				if (params.get(0).isConvertibleTo(ScriptValueType.INT)) {
					int r, g, b;
					r = Parser.getInteger(params.get(0));
					g = Parser.getInteger(params.get(1));
					b = Parser.getInteger(params.get(2));
					if (r < 0 || r > 255) {
						throw new Exception_Nodeable_InvalidColorRange(this, new Integer(r));
					}
					if (g < 0 || g > 255) {
						throw new Exception_Nodeable_InvalidColorRange(this, new Integer(g));
					}
					if (b < 0 || b > 255) {
						throw new Exception_Nodeable_InvalidColorRange(this, new Integer(b));
					}
					template.setColor(new java.awt.Color(r, g, b));
				} else {
					;
					float r, g, b;
					r = Parser.getFloat(params.get(0));
					g = Parser.getFloat(params.get(1));
					b = Parser.getFloat(params.get(2));
					if (r < 0.0d || r > 1.0d) {
						throw new Exception_Nodeable_InvalidColorRange(this, new Float(r));
					}
					if (g < 0.0d || g > 1.0d) {
						throw new Exception_Nodeable_InvalidColorRange(this, new Float(g));
					}
					if (b < 0.0d || b > 1.0d) {
						throw new Exception_Nodeable_InvalidColorRange(this, new Float(b));
					}
					template.setColor(new java.awt.Color(r, g, b));
				}
			}
			params.clear();
		} else if (name.equals("getRed")) {
			returning = Parser.getRiffInt(this.getEnvironment(), template.getColor().getRed());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getGreen")) {
			returning = Parser.getRiffInt(this.getEnvironment(), template.getColor().getGreen());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getBlue")) {
			returning = Parser.getRiffInt(this.getEnvironment(), template.getColor().getBlue());
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("setRed")) {
			int value = 0;
			if (params.get(0).getType().equals(ScriptValueType.FLOAT)) {
				value = (int) (Parser.getFloat(params.get(0)) * 255.0d);
			} else {
				value = Parser.getInteger(params.get(0));
			}
			template.setColor(new java.awt.Color(value, template.getColor().getGreen(), template.getColor().getBlue()));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setGreen")) {
			int value = 0;
			if (params.get(0).getType().equals(ScriptValueType.FLOAT)) {
				value = (int) (Parser.getFloat(params.get(0)) * 255.0d);
			} else {
				value = Parser.getInteger(params.get(0));
			}
			template.setColor(new java.awt.Color(template.getColor().getRed(), value, template.getColor().getBlue()));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setBlue")) {
			int value = 0;
			if (params.get(0).getType().equals(ScriptValueType.FLOAT)) {
				value = (int) (Parser.getFloat(params.get(0)) * 255.0d);
			} else {
				value = Parser.getInteger(params.get(0));
			}
			template.setColor(new java.awt.Color(template.getColor().getRed(), template.getColor().getGreen(), value));
			assert Debugger.closeNode();
			return null;
		} else if (name.equals("setColor")) {
			template.setColor(RiffJavaToolbox.getColor(Parser.getString(params.get(0))));
			assert Debugger.closeNode();
			return null;
		}
		returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		assert Debugger.closeNode();
		return returning;
	}

	public java.awt.Color getColor() {
		return this.m_color;
	}

	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing color faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addConstructor(this.getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.INT));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.INT));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.INT));
		this.addConstructor(this.getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.FLOAT));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.FLOAT));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.FLOAT));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		this.addFauxFunction("getRed", ScriptValueType.INT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getGreen", ScriptValueType.INT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getBlue", ScriptValueType.INT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getRedOpacity", ScriptValueType.FLOAT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getGreenOpacity", ScriptValueType.FLOAT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getBlueOpacity", ScriptValueType.FLOAT, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.INT));
		this.addFauxFunction("setRed", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setGreen", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setBlue", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.FLOAT));
		this.addFauxFunction("setRed", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setGreen", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setBlue", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		this.addFauxFunction("setColor", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Color(this.getEnvironment(), this.getType());
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Color Faux Template");
		assert super.nodificate();
		if (this.m_color == null) {
			assert Debugger.addNode("Color: null");
		} else {
			assert Debugger.addNode("Color: " + RiffJavaToolbox.getColorName(this.m_color));
		}
		assert Debugger.closeNode();
		return true;
	}

	public void setColor(java.awt.Color color) {
		this.m_color = color;
	}
}
