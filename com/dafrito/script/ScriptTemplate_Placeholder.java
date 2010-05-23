package com.dafrito.script;

import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Variable;

public class ScriptTemplate_Placeholder extends ScriptTemplate_Abstract {

    private String name;

    public ScriptTemplate_Placeholder(ScriptEnvironment env, String name) {
        super(env, null, null, null);
        this.name = name;
    }

    private ScriptTemplate_Abstract getTemplate() {
        try {
            ScriptTemplate_Abstract template = (ScriptTemplate_Abstract)getEnvironment().retrieveVariable(this.name).getValue();
            assert template != null : "Template could not be retrieved (" + this.name + ")";
            return template;
        } catch(Exception_Nodeable ex) {
            throw new Exception_InternalError("Exception occurred while retrieving template: " + ex);
        }
    }

    // Abstract-value implementation
    @Override
    public ScriptValueType getType() {
        return getTemplate().getType();
    }

    @Override
    public boolean isConvertibleTo(ScriptValueType type) {
        return getTemplate().isConvertibleTo(type);
    }

    @Override
    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return getTemplate().castToType(ref, type);
    }

    @Override
    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return getTemplate().getValue();
    }

    @Override
    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getTemplate().setValue(ref, value);
    }

    @Override
    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getTemplate().valuesEqual(ref, rhs);
    }

    @Override
    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getTemplate().valuesCompare(ref, rhs);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Template Placeholder (" + this.name + ")");
        assert LegacyDebugger.addSnapNode("Referenced Template", getTemplate());
        assert LegacyDebugger.close();
        return true;
    }

    // Abstract-template implementation
    @Override
    public boolean isFullCreation() {
        return getTemplate().isFullCreation();
    }

    @Override
    public void disableFullCreation() {
        getTemplate().disableFullCreation();
    }

    @Override
    public boolean isConstructing() throws Exception_Nodeable {
        return getTemplate().isConstructing();
    }

    @Override
    public void setConstructing(boolean constructing) throws Exception_Nodeable {
        getTemplate().setConstructing(constructing);
    }

    @Override
    public boolean isObject() {
        return getTemplate().isObject();
    }

    @Override
    public boolean isAbstract() throws Exception_Nodeable {
        return getTemplate().isAbstract();
    }

    @Override
    public ScriptTemplate instantiateTemplate() {
        return getTemplate().instantiateTemplate();
    }

    @Override
    public ScriptValue_Variable getStaticReference() throws Exception_Nodeable {
        return getTemplate().getStaticReference();
    }

    @Override
    public ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object)
            throws Exception_Nodeable {
        return getTemplate().createObject(ref, object);
    }

    @Override
    public ScriptValue_Variable addVariable(Referenced ref, String variableName, ScriptValue_Variable value)
            throws Exception_Nodeable {
        return getTemplate().addVariable(ref, variableName, value);
    }

    @Override
    public ScriptValue_Variable getVariable(String variableName) throws Exception_Nodeable {
        return getTemplate().getVariable(variableName);
    }

    @Override
    public void addFunction(Referenced ref, String functionName, ScriptFunction_Abstract function) throws Exception_Nodeable {
        getTemplate().addFunction(ref, functionName, function);
    }

    @Override
    public List<ScriptFunction_Abstract> getFunctions() {
        return getTemplate().getFunctions();
    }

    @Override
    public ScriptFunction_Abstract getFunction(String functionName, List<ScriptValue_Abstract> params) {
        return getTemplate().getFunction(functionName, params);
    }

    @Override
    public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn) {
        return getTemplate().getFunctionTemplate(fxn);
    }

    @Override
    public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
        getTemplate().addTemplatePreconstructorExpression(exec);
    }

    @Override
    public void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
        getTemplate().addPreconstructorExpression(exec);
    }

    @Override
    public void initialize() throws Exception_Nodeable {
        getTemplate().initialize();
    }

    @Override
    public void initializeFunctions(Referenced ref) throws Exception_Nodeable {
        getTemplate().initializeFunctions(ref);
    }

    // Overloaded ScriptTemplate_Abstract fxns
    @Override
    public ScriptTemplate_Abstract getExtendedClass() {
        return getTemplate().getExtendedClass();
    }

    @Override
    public List<ScriptValueType> getInterfaces() {
        return getTemplate().getInterfaces();
    }
}
