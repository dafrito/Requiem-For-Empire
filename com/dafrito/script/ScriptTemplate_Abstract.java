package com.dafrito.script;

import java.util.Collections;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Variable;

public abstract class ScriptTemplate_Abstract implements ScriptValue_Abstract, Nodeable {
    private final ScriptEnvironment environment;
    private final ScriptValueType type;
    private ScriptValueType extended;
    private List<ScriptValueType> interfaces;

    public ScriptTemplate_Abstract(ScriptEnvironment env, ScriptValueType type) {
        this.environment = env;
        this.type = type;
    }

    public ScriptTemplate_Abstract(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended,
        List<ScriptValueType> interfaces) {
        this.environment = env;
        this.type = type;
        this.extended = extended;
        this.interfaces = interfaces;
    }

    public ScriptTemplate_Abstract getExtendedClass() {
        assert getEnvironment() != null : "Environment is null: " + this;
        if(getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
            return getEnvironment().getTemplate(getType()).getExtendedClass();
        }
        if(this.extended == null) {
            return null;
        }
        assert getEnvironment().getTemplate(this.extended) != null : "Extended class is null!";
        return getEnvironment().getTemplate(this.extended);
    }

    public List<ScriptValueType> getInterfaces() {
        if(getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
            return getEnvironment().getTemplate(getType()).getInterfaces();
        }
        return Collections.unmodifiableList(this.interfaces);
    }

    // Abstract-value implementation
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public ScriptValueType getType() {
        return this.type;
    }

    public boolean isConvertibleTo(ScriptValueType testType) {
        if(getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
            return getEnvironment().getTemplate(getType()).isConvertibleTo(testType);
        }
        assert LegacyDebugger.open("Value Type Match Test", "Checking for Type-Match (" + getType() + " to " + testType + ")");
        assert LegacyDebugger.addNode(this);
        if(getType().equals(testType)) {
            assert LegacyDebugger.close("Direct match.");
            return true;
        }
        if(getInterfaces() != null) {
            for(ScriptValueType scriptInterface : getInterfaces()) {
                if(ScriptValueType.isConvertibleTo(getEnvironment(), scriptInterface, testType)) {
                    assert LegacyDebugger.close("Interface type match.");
                    return true;
                }
            }
        }
        assert LegacyDebugger.open("No type match, checking extended classes for match.");
        if(getExtendedClass() != null && getExtendedClass().isConvertibleTo(testType)) {
            assert LegacyDebugger.close();
            assert LegacyDebugger.close();
            return true;
        }
        assert LegacyDebugger.close();
        assert LegacyDebugger.close();
        return false;
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType referenceType) throws Exception_Nodeable {
        if(isConvertibleTo(referenceType)) {
            return this;
        }
        throw new Exception_Nodeable_ClassCast(ref, this, referenceType);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return this;
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return(this == rhs);
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        throw new Exception_InternalError("Templates have no inherent value, and thus their value cannot be set directly.");
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        throw new Exception_InternalError("Templates have no inherent value, and thus cannot be compared.");
    }

    // Remaining unimplemented ScriptValue_Abstract functions
    public abstract boolean nodificate();

    // Abstract-template implementation
    public abstract boolean isFullCreation();

    public abstract void disableFullCreation();

    public abstract boolean isConstructing() throws Exception_Nodeable;

    public abstract void setConstructing(boolean constructing) throws Exception_Nodeable;

    public abstract ScriptTemplate instantiateTemplate();

    public abstract boolean isObject();

    public abstract boolean isAbstract() throws Exception_Nodeable;

    public abstract ScriptValue_Variable getStaticReference() throws Exception_Nodeable;

    public abstract ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object)
            throws Exception_Nodeable;

    public abstract ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value)
            throws Exception_Nodeable;

    public abstract ScriptValue_Variable getVariable(String name) throws Exception_Nodeable;

    public abstract void addFunction(Referenced ref, String name, ScriptFunction_Abstract function)
            throws Exception_Nodeable;

    public abstract List<ScriptFunction_Abstract> getFunctions();

    public abstract ScriptFunction_Abstract getFunction(String name, List<ScriptValue_Abstract> params);

    public abstract ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn);

    public abstract void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable;

    public abstract void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable;

    public abstract void initialize() throws Exception_Nodeable;

    public abstract void initializeFunctions(Referenced ref) throws Exception_Nodeable;
}
