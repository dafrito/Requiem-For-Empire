package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.gui.InterfaceElement;
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

public class FauxTemplate_InterfaceElement extends FauxTemplate {
    public static final String INTERFACEELEMENTSTRING = "InterfaceElement";
    private InterfaceElement element;

    public FauxTemplate_InterfaceElement(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended,
        List<ScriptValueType> implemented, boolean isAbstract) {
        super(env, type, extended, implemented, isAbstract);
    }

    public FauxTemplate_InterfaceElement(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, INTERFACEELEMENTSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), true);
    }

    public FauxTemplate_InterfaceElement(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public InterfaceElement getElement() {
        return this.element;
    }

    public void setElement(InterfaceElement element) {
        assert LegacyDebugger.open("Interface Element Faux Template Changes", "Changing Interface Element");
        assert LegacyDebugger.addNode(this);
        assert LegacyDebugger.addNode(element);
        this.element = element;
        assert LegacyDebugger.close();
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new FauxTemplate_InterfaceElement(getEnvironment(), getType());
    }

    // addFauxFunction(name,ScriptValueType
    // type,List<ScriptValue_Abstract>params,ScriptKeywordType
    // permission,boolean isAbstract)
    // All functions must be defined here. All function bodies are defined in
    // 'execute'.
    @Override
    public void initialize() throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Initializations", "Initializing interface element faux template");
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
        fxnParams = new LinkedList<ScriptValue_Abstract>();
        addFauxFunction(
            "getUniqueStylesheet",
            ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING),
            new LinkedList<ScriptValue_Abstract>(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        addFauxFunction(
            "getClassStylesheet",
            ScriptValueType.createType(getEnvironment(), Stylesheet.STYLESHEETSTRING),
            new LinkedList<ScriptValue_Abstract>(),
            ScriptKeywordType.PUBLIC,
            false,
            false);
        fxnParams.add(new ScriptValue_Faux(getEnvironment(), ScriptValueType.createType(
            getEnvironment(),
            Stylesheet.STYLESHEETSTRING)));
        addFauxFunction("setUniqueStylesheet", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        addFauxFunction("setClassStylesheet", ScriptValueType.VOID, fxnParams, ScriptKeywordType.PUBLIC, false, false);
        assert LegacyDebugger.close();
    }

    // Function bodies are contained via a series of if statements in execute
    @SuppressWarnings("fallthrough")
    // Template will be null if the object is exactly of this type and is
    // constructing, and thus must be created then
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open("Faux Template Executions", "Executing Interface Element Faux Template Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        FauxTemplate_InterfaceElement template = (FauxTemplate_InterfaceElement)rawTemplate;
        ScriptValue_Abstract returning;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        ScriptValue_Abstract value;
        if(name == null || name.equals("")) {
            if(template == null) {
                template = (FauxTemplate_InterfaceElement)createObject(ref, template);
            }
            switch(params.size()) {
                // Intentionally out of order to allow for case 2 to run case
                // 1's code.
                case 2:
                    value = params.get(1);
                    template.getElement().setClassStylesheet((Stylesheet)value.getValue());
                case 1:
                    value = params.get(0);
                    template.getElement().setUniqueStylesheet((Stylesheet)value.getValue());
                    break;
            }
        } else if(name.equals("getUniqueStylesheet")) {
            returning = Parser.getRiffStylesheet(template.getElement().getUniqueStylesheet());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("getClassStylesheet")) {
            returning = Parser.getRiffStylesheet(template.getElement().getClassStylesheet());
            assert LegacyDebugger.close();
            return returning;
        } else if(name.equals("setUniqueStylesheet")) {
            template.getElement().setUniqueStylesheet(Parser.getStylesheet(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        } else if(name.equals("setClassStylesheet")) {
            template.getElement().setClassStylesheet(Parser.getStylesheet(params.get(0)));
            assert LegacyDebugger.close();
            return null;
        }
        params.clear();
        returning = getExtendedFauxClass().execute(ref, name, params, template);
        assert LegacyDebugger.close();
        return returning;
    }

    // Nodeable implementation
    @Override
    public Object convert() {
        return getElement();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Interface Element Faux Template");
        assert super.nodificate();
        if(getElement() == null) {
            assert LegacyDebugger.addNode("Interface Element: null");
        } else {
            assert LegacyDebugger.addNode(getElement());
        }
        assert LegacyDebugger.close();
        return true;
    }
}
