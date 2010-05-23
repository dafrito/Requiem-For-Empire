package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.geom.Point;
import com.dafrito.geom.Point_Euclidean;
import com.dafrito.gui.GraphicalElement_Line;
import com.dafrito.script.Parser;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_Line extends FauxTemplate {
    public static final String LINESTRING = "Line";
    public Point pointA, pointB;

    public FauxTemplate_Line(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, LINESTRING), ScriptValueType.createType(
            env,
            FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Line(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        this.pointA = new Point_Euclidean(getEnvironment(), 0, 0, 0);
        this.pointB = new Point_Euclidean(getEnvironment(), 0, 0, 0);
    }

    public void setPointA(Point point) {
        this.pointA = point;
    }

    public void setPointB(Point point) {
        this.pointB = point;
    }

    public Point getPointA() {
        return this.pointA;
    }

    public Point getPointB() {
        return this.pointB;
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Line(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing line faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        addConstructor(getType(), fxnParams);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();
        fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        addFauxFunction("setPointA", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setPointB", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = FauxTemplate.createEmptyParamList();
        addFauxFunction(
            "getPointA",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Point.POINTSTRING),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getPointB",
            ScriptValueType.createType(getEnvironment(), FauxTemplate_Point.POINTSTRING),
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams = FauxTemplate.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.DOUBLE));
        addFauxFunction("setX1", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setY1", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setX2", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setY2", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        fxnParams = FauxTemplate.createEmptyParamList();
        addFauxFunction("getX1", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("getY1", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("getX2", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("getY2", ScriptValueType.DOUBLE, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Line Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Line template = (FauxTemplate_Line)rawTemplate;
        ScriptValue_Abstract returning = null;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Line)createObject(ref, template);
            }
            switch(params.size()) {
                case 2:
                    template.setPointA(Parser.getPoint(params.get(0)));
                    template.setPointB(Parser.getPoint(params.get(1)));
                    break;
                case 4:
                    template.setPointA(new Point_Euclidean(getEnvironment(), Parser.getDouble(params.get(0)).doubleValue(), Parser.getDouble(
                        params.get(1)).doubleValue(), 0));
                    template.setPointB(new Point_Euclidean(getEnvironment(), Parser.getDouble(params.get(2)).doubleValue(), Parser.getDouble(
                        params.get(3)).doubleValue(), 0));
            }
            params.clear();
            returning = getExtendedFauxClass().execute(ref, name, params, template);
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getX1")) {
            returning = Parser.getRiffDouble(getEnvironment(), template.getPointA().getX());
        } else if(name.equals("getY1")) {
            returning = Parser.getRiffDouble(getEnvironment(), template.getPointA().getY());
        } else if(name.equals("getX2")) {
            returning = Parser.getRiffDouble(getEnvironment(), template.getPointB().getX());
        } else if(name.equals("getY2")) {
            returning = Parser.getRiffDouble(getEnvironment(), template.getPointB().getY());
        } else if(name.equals("setX1")) {
            template.getPointA().setX(Parser.getDouble(params.get(0)).doubleValue());
        } else if(name.equals("setY1")) {
            template.getPointA().setY(Parser.getDouble(params.get(0)).doubleValue());
        } else if(name.equals("setX2")) {
            template.getPointB().setX(Parser.getDouble(params.get(0)).doubleValue());
        } else if(name.equals("setY2")) {
            template.getPointB().setY(Parser.getDouble(params.get(0)).doubleValue());
        } else if(name.equals("getPointA")) {
            returning = Parser.getRiffPoint(template.getPointA());
        } else if(name.equals("getPointB")) {
            returning = Parser.getRiffPoint(template.getPointB());
        } else if(name.equals("setPointA")) {
            template.setPointA(Parser.getPoint(params.get(0)));
        } else if(name.equals("setPointB")) {
            template.setPointB(Parser.getPoint(params.get(0)));
        } else {
            returning = getExtendedFauxClass().execute(ref, name, params, template);
        }
        assert LegacyDebugger.close();
        return returning;
    }

    // ScriptConvertible and Nodeable implementations
    @Override
    public Object convert() {
        return new GraphicalElement_Line(getEnvironment(), getPointA(), getPointB());
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Line Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.addNode("Point A: " + getPointA());
        assert LegacyDebugger.addNode("Point B: " + getPointB());
        assert LegacyDebugger.close();
        return true;
    }
}
