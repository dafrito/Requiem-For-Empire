package com.dafrito.script.executable;

import java.util.Collection;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptFunction_Abstract;
import com.dafrito.script.ScriptGroup;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptExecutable_ParseFunction extends ScriptElement implements ScriptFunction_Abstract, ScriptExecutable {
    private boolean isStatic, isAbstract;
    private ScriptGroup body;
    private String name;
    private ScriptValueType returnType;
    private List<ScriptValue_Abstract> parameters;
    private ScriptKeywordType permission;

    public ScriptExecutable_ParseFunction(Referenced ref, ScriptValueType returnType, 
        String name, List<ScriptValue_Abstract> paramList, ScriptKeywordType permission, boolean isStatic,
        boolean isAbstract, ScriptGroup body) {
        super(ref);
        this.name = name;
        this.returnType = returnType;
        this.parameters = paramList;
        this.permission = permission;
        this.isStatic = isStatic;
        this.isAbstract = isAbstract;
        this.body = body;
    }

    public String getName() {
        return this.name;
    }

    public ScriptGroup getBody() {
        return this.body;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    // ScriptFunction implementation
    public boolean isAbstract() {
        return this.isAbstract;
    }

    public ScriptKeywordType getPermission() {
        return this.permission;
    }

    public ScriptValueType getReturnType() {
        return this.returnType;
    }

    public ScriptValue_Abstract getReturnValue() {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in unparsed function");
    }

    public void setReturnValue(Referenced element, ScriptValue_Abstract value) {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in unparsed function");
    }

    public List<ScriptValue_Abstract> getParameters() {
        return this.parameters;
    }

    public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in unparsed function");
    }

    public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in unparsed function");
    }

    public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in unparsed function");
    }

    public boolean areParametersConvertible(List<ScriptValue_Abstract> list) {
        return ScriptFunction.areParametersConvertible(getParameters(), list);
    }

    public boolean areParametersEqual(List<ScriptValue_Abstract> list) {
        return ScriptFunction.areParametersEqual(getParameters(), list);
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        throw new Exception_InternalError(getEnvironment(), "Invalid call in unparsed function");
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Unparsed Script-Function ("
            + ScriptFunction.getDisplayableFunctionName(this.name)
            + ")");
        assert super.nodificate();
        assert LegacyDebugger.addNode("Static: " + this.isStatic);
        assert LegacyDebugger.addSnapNode("Body", this.body);
        assert LegacyDebugger.close();
        return true;
    }
}
