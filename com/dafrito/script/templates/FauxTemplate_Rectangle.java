package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.gui.InterfaceElement_Rectangle;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Faux;

public class FauxTemplate_Rectangle extends FauxTemplate_InterfaceElement {
    public static final String RECTANGLESTRING = "Rectangle";

    public FauxTemplate_Rectangle(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, RECTANGLESTRING), ScriptValueType.createType(
            env,
            FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING), new LinkedList<ScriptValueType>(), false);
    }

    public FauxTemplate_Rectangle(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        setElement(new InterfaceElement_Rectangle(env, null, null));
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_Rectangle(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing rectangle faux template");
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
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Rectangle Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_Rectangle template = (FauxTemplate_Rectangle)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_Rectangle)createObject(ref, template);
            }
            getExtendedFauxClass().execute(ref, name, params, template);
            assert LegacyDebugger.close();
            return template;
        }
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Convertible and Nodeable implementation
    @Override
    public Object convert() {
        return getElement();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Rectangle Faux Template");
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
