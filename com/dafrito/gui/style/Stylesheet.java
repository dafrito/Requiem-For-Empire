package com.dafrito.gui.style;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.templates.FauxTemplate;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class Stylesheet extends FauxTemplate {
    private Map<StylesheetElementType, StylesheetElement> styleElements = new HashMap<StylesheetElementType, StylesheetElement>(); // element

    private String name;
    private boolean isUnique;
    public static final String STYLESHEETSTRING = "Stylesheet";

    public Stylesheet(ScriptEnvironment env) {
        super(env, ScriptValueType.createType(env, STYLESHEETSTRING), ScriptValueType.getObjectType(env), new LinkedList<ScriptValueType>(), false);
    }

    public StylesheetElement getElement(StylesheetElementType elementCode) {
        return this.styleElements.get(elementCode);
    }

    public boolean isUnique() {
        return this.isUnique;
    }

    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addElement(StylesheetElementType type, StylesheetElement element) {
        assert LegacyDebugger.open("Stylesheet Element Additions", "Adding a"
            + element.getElementName()
            + " element to this stylesheet");
        assert LegacyDebugger.addNode(this);
        assert LegacyDebugger.addNode(element);
        this.styleElements.put(type, element);
        assert LegacyDebugger.close();
    }

    // ScriptConvertible implementation
    @Override
    public Object convert() {
        return this;
    }

    // FauxTemplate extensions
    @Override
    public ScriptTemplate instantiateTemplate() {
        return new Stylesheet(getEnvironment());
    }

    // ScriptExecutable implementation
    @Override
    public ScriptValue_Abstract execute(Referenced ref, String givenName, List<ScriptValue_Abstract> params,
        ScriptTemplate_Abstract template) throws Exception_Nodeable {
        return null;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        if(this.name == null) {
            assert LegacyDebugger.open("Anonymous stylesheet (" + this.styleElements.size() + " element(s))");
        } else {
            assert LegacyDebugger.open("Stylesheet: " + this.name + " (" + this.styleElements.size() + " element(s))");
        }
        assert LegacyDebugger.addNode(this.styleElements.values());
        assert LegacyDebugger.close();
        return true;
    }
}
