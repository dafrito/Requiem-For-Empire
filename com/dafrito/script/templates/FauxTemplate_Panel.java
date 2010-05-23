package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.gui.InterfaceElement_Panel;
import com.dafrito.gui.style.Stylesheet;
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

public class FauxTemplate_Panel extends FauxTemplate_InterfaceElement {
    public static final String PANELSTRING = "Panel";

    public FauxTemplate_Panel(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, PANELSTRING), ScriptValueType.createType(
            env,
            FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Panel(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        setElement(new InterfaceElement_Panel(env, null, null));
    }

    public InterfaceElement_Panel getPanel() {
        return (InterfaceElement_Panel)getElement();
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Panel(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing panel faux template");
        addConstructor(getType(), ScriptValueType.createEmptyParamList());
        List<ScriptValue_Abstract> fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            Stylesheet.STYLESHEETSTRING)));
        addConstructor(getType(), fxnParams);
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            Stylesheet.STYLESHEETSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            Stylesheet.STYLESHEETSTRING)));
        addConstructor(getType(), fxnParams);
        disableFullCreation();
        getExtendedClass().initialize();

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING)));
        addFauxFunction("add", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

        fxnParams = ScriptValueType.createEmptyParamList();
        addFauxFunction("getTerrestrial", ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Terrestrial.TERRESTRIALSTRING), fxnParams, ScriptKeywordType.PUBLIC, false, false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Terrestrial.TERRESTRIALSTRING)));
        addFauxFunction("setTerrestrial", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_RiffDali.RIFFDALISTRING)));
        addFauxFunction("setRiffDali", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
        addFauxFunction("drawRegion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
        addFauxFunction("fillRegion", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
        addFauxFunction(
            "drawTransformedRegion",
            ScriptValueType.VOID,
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING)));
        addFauxFunction(
            "fillTransformedRegion",
            ScriptValueType.VOID,
            fxnParams,
            ScriptKeywordType.PUBLIC,
            false,
            false);

        fxnParams = ScriptValueType.createEmptyParamList();
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.STRING));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Color.COLORSTRING)));
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            FauxTemplate_Point.POINTSTRING)));
        addFauxFunction("drawString", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);

        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Panel Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Panel template = (FauxTemplate_Panel)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Panel)createObject(ref, template);
            }
        } else if(name.equals("add")) {
            template.getPanel().add(Parser.getGraphicalElement(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("getTerrestrial")) {
            returning = Parser.getRiffTerrestrial(template.getPanel().getTerrestrial());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("setTerrestrial")) {
            template.getPanel().setTerrestrial(Parser.getTerrestrial(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setRiffDali")) {
            template.getPanel().setRiffDali(Parser.getTemplate(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("drawRegion")) {
            template.getPanel().drawRegion(Parser.getDiscreteRegion(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("fillRegion")) {
            template.getPanel().fillRegion(Parser.getDiscreteRegion(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("drawTransformedRegion")) {
            template.getPanel().drawTransformedRegion(Parser.getDiscreteRegion(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("fillTransformedRegion")) {
            template.getPanel().fillTransformedRegion(Parser.getDiscreteRegion(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("drawString")) {
            template.getPanel().drawString(
                Parser.getString(params.get(0)),
                Parser.getColor(params.get(1)),
                Parser.getPoint(params.get(2)));
            assert LegacyDebugger.close();
            return null;
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable and ScriptConvertible implementations
    @Override
    public Object convert() {
        return getElement();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Panel Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
