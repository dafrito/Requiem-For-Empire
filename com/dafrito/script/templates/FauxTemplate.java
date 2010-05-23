package com.dafrito.script.templates;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptFunction_Faux;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.ScriptTemplate;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public abstract class FauxTemplate extends ScriptTemplate {
    public FauxTemplate(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
    }

    public FauxTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended,
        List<ScriptValueType> implemented, boolean isAbstract) {
        super(env, type, extended, implemented, isAbstract);
    }

    public static List<ScriptValue_Abstract> createEmptyParamList() {
        return new LinkedList<ScriptValue_Abstract>();
    }

    // Define default constructor here
    @Override
    public ScriptTemplate instantiateTemplate() {
        return null;
    }

    /**
     * Function bodies are contained via a series of if statements in execute
     * Template will be null if the object is exactly of this type and is
     * constructing, and thus must be created then
     * 
     * TODO: Fix this documentation
     * 
     * @param ref Unknown
     * @param name Unknown
     * @param params Unknown
     * @param rawTemplate Unknown
     * @return The value of this template
     * @throws Exception_Nodeable Thrown sometimes...?
     */
    public ScriptValue_Abstract execute(Referenced ref, String name, List<ScriptValue_Abstract> params,
            ScriptTemplate_Abstract rawTemplate) throws Exception_Nodeable {
        assert LegacyDebugger.open(
            "Faux Template Executions",
            "Executing INSERTFAUXTEMPLATENAMEHERE Faux Template Function ("
                + ScriptFunction.getDisplayableFunctionName(name)
                + ")");
        FauxTemplate_InterfaceElement template = (FauxTemplate_InterfaceElement)rawTemplate;
        assert LegacyDebugger.addSnapNode("Template provided", template);
        assert LegacyDebugger.addSnapNode("Parameters provided", params);
        throw new Exception_InternalError("Invalid default in FauxTemplate:execute");
    }

    @Override
    public boolean nodificate() {
        super.nodificate();
        return true;
    }

    public FauxTemplate getExtendedFauxClass() {
        return (FauxTemplate)getExtendedClass();
    }

    public void addConstructor(ScriptValueType returnType, List<ScriptValue_Abstract> params) throws Exception_Nodeable {
        addFauxFunction("", returnType, params, ScriptKeywordType.PUBLIC, false, true);
    }

    public void addFauxFunction(String name, ScriptValueType returnType, List<ScriptValue_Abstract> params,
            ScriptKeywordType permission, boolean isAbstract, boolean isStatic) throws Exception_Nodeable {
        addFunction(
            null,
            name,
            new ScriptFunction_Faux(this, name, returnType, params, permission, isAbstract, isStatic));
    }

    @Override
    public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in FauxTemplate");
    }

    @Override
    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in FauxTemplate");
    }

    @Override
    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in FauxTemplate");
    }
}
