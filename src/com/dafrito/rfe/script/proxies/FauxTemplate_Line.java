package com.dafrito.rfe.script.proxies;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.rfe.Debugger;
import com.dafrito.rfe.gui.GraphicalElement_Line;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.points.Point;
import com.dafrito.rfe.points.Point_Euclidean;
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

public class FauxTemplate_Line extends FauxTemplate implements ScriptConvertible, Nodeable {
	public static final String LINESTRING = "Line";
	public Point pointA, pointB;

	public FauxTemplate_Line(ScriptEnvironment env) {
		super(env, ScriptValueType.createType(env, LINESTRING), ScriptValueType.createType(env, FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
	}

	public FauxTemplate_Line(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.pointA = new Point_Euclidean(this.getEnvironment(), 0, 0, 0);
		this.pointB = new Point_Euclidean(this.getEnvironment(), 0, 0, 0);
	}

	// ScriptConvertible and Nodeable implementations
	@Override
	public Object convert() {
		return new GraphicalElement_Line(this.getEnvironment(), this.getPointA(), this.getPointB());
	}

	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	@Override
	public ScriptValue execute(Referenced ref, String name, List<ScriptValue> params, ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Executions", "Executing Line Faux Template Function (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		FauxTemplate_Line template = (FauxTemplate_Line) rawTemplate;
		ScriptValue returning = null;
		assert Debugger.addSnapNode("Template provided", template);
		assert Debugger.addSnapNode("Parameters provided", params);
		if (name == null || name.equals("")) {
			if (template == null) {
				template = (FauxTemplate_Line) this.createObject(ref, template);
			}
			switch (params.size()) {
			case 2:
				template.setPointA(Parser.getPoint(params.get(0)));
				template.setPointB(Parser.getPoint(params.get(1)));
				break;
			case 4:
				template.setPointA(new Point_Euclidean(this.getEnvironment(), Parser.getDouble(params.get(0)).doubleValue(), Parser.getDouble(params.get(1)).doubleValue(), 0));
				template.setPointB(new Point_Euclidean(this.getEnvironment(), Parser.getDouble(params.get(2)).doubleValue(), Parser.getDouble(params.get(3)).doubleValue(), 0));
			}
			params.clear();
			returning = this.getExtendedFauxClass().execute(ref, name, params, template);
			assert Debugger.closeNode();
			return returning;
		} else if (name.equals("getX1")) {
			returning = Parser.getRiffDouble(this.getEnvironment(), template.getPointA().getX());
		} else if (name.equals("getY1")) {
			returning = Parser.getRiffDouble(this.getEnvironment(), template.getPointA().getY());
		} else if (name.equals("getX2")) {
			returning = Parser.getRiffDouble(this.getEnvironment(), template.getPointB().getX());
		} else if (name.equals("getY2")) {
			returning = Parser.getRiffDouble(this.getEnvironment(), template.getPointB().getY());
		} else if (name.equals("setX1")) {
			template.getPointA().setX(Parser.getDouble(params.get(0)).doubleValue());
		} else if (name.equals("setY1")) {
			template.getPointA().setY(Parser.getDouble(params.get(0)).doubleValue());
		} else if (name.equals("setX2")) {
			template.getPointB().setX(Parser.getDouble(params.get(0)).doubleValue());
		} else if (name.equals("setY2")) {
			template.getPointB().setY(Parser.getDouble(params.get(0)).doubleValue());
		} else if (name.equals("getPointA")) {
			returning = Parser.getRiffPoint(this.getEnvironment(), template.getPointA());
		} else if (name.equals("getPointB")) {
			returning = Parser.getRiffPoint(this.getEnvironment(), template.getPointB());
		} else if (name.equals("setPointA")) {
			template.setPointA(Parser.getPoint(params.get(0)));
		} else if (name.equals("setPointB")) {
			template.setPointB(Parser.getPoint(params.get(0)));
		} else {
			returning = this.getExtendedFauxClass().execute(ref, name, params, template);
		}
		assert Debugger.closeNode();
		return returning;
	}

	public Point getPointA() {
		return this.pointA;
	}

	public Point getPointB() {
		return this.pointB;
	}

	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	@Override
	public void initialize() throws Exception_Nodeable {
		assert Debugger.openNode("Faux Template Initializations", "Initializing line faux template");
		this.addConstructor(this.getType(), ScriptValueType.createEmptyParamList());
		List<ScriptValue> fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		this.addConstructor(this.getType(), fxnParams);
		fxnParams = new LinkedList<ScriptValue>();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.STRING));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addConstructor(this.getType(), fxnParams);
		this.disableFullCreation();
		this.getExtendedClass().initialize();
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING)));
		this.addFauxFunction("setPointA", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setPointB", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		this.addFauxFunction("getPointA", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getPointB", ScriptValueType.createType(this.getEnvironment(), FauxTemplate_Point.POINTSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(this.getEnvironment(), ScriptValueType.DOUBLE));
		this.addFauxFunction("setX1", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setY1", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setX2", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("setY2", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		fxnParams = FauxTemplate.createEmptyParamList();
		this.addFauxFunction("getX1", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getY1", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getX2", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		this.addFauxFunction("getY2", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
		assert Debugger.closeNode();
	}

	// Define default constructor here
	@Override
	public ScriptTemplate instantiateTemplate() {
		return new FauxTemplate_Line(this.getEnvironment(), this.getType());
	}

	@Override
	public void nodificate() {
		assert Debugger.openNode("Line Faux Template");
		super.nodificate();
		assert Debugger.addNode("Point A: " + this.getPointA());
		assert Debugger.addNode("Point B: " + this.getPointB());
		assert Debugger.closeNode();
	}

	public void setPointA(Point point) {
		this.pointA = point;
	}

	public void setPointB(Point point) {
		this.pointB = point;
	}
}
