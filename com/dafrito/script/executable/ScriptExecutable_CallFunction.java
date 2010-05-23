package com.dafrito.script.executable;

import java.util.List;
import java.util.Vector;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_FunctionNotFound;
import com.dafrito.debug.Exceptions.Exception_Nodeable_IllegalNullReturnValue;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptFunction_Abstract;
import com.dafrito.script.ScriptFunction_Faux;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptExecutable_CallFunction extends ScriptElement implements ScriptExecutable, ScriptValue_Abstract {
    private String functionName;
    private List<ScriptValue_Abstract> params;
    private ScriptValue_Abstract object;

    public ScriptExecutable_CallFunction(Referenced ref, ScriptValue_Abstract object, String functionName,
        List<ScriptValue_Abstract> params) {
        super(ref);
        this.object = object;
        this.functionName = functionName;
        this.params = params;
    }

    public static ScriptValue_Abstract callFunction(ScriptEnvironment env, Referenced ref, ScriptValue_Abstract object,
        String name, List<ScriptValue_Abstract> params) throws Exception_Nodeable {
        assert LegacyDebugger.open("Function Calls", "Calling Function ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        assert LegacyDebugger.open("Function Call Details");
        ScriptValue_Abstract thisObject = null;
        // Get our object
        if(object == null) {
            thisObject = env.getCurrentObject();
            assert LegacyDebugger.addSnapNode("Reverting to current object", thisObject);
        } else {
            assert LegacyDebugger.open("Getting object's core value");
            thisObject = object.getValue();
            assert LegacyDebugger.close("Core value", thisObject);
        }
        // Convert our values of questionable nestingness down to pure values
        Vector<ScriptValue_Abstract> baseList = new Vector<ScriptValue_Abstract>();
        if(params != null && params.size() > 0) {
            assert LegacyDebugger.open("Getting parameters' core values");
            for(int i = 0; i < params.size(); i++) {
                baseList.add(params.get(i).getValue());
            }
            assert LegacyDebugger.close("Core value params", baseList);
        }
        // Get our function
        ScriptFunction_Abstract function = ((ScriptTemplate_Abstract)thisObject).getFunction(name, baseList);
        ScriptTemplate_Abstract functionTemplate = ((ScriptTemplate_Abstract)thisObject).getFunctionTemplate(function);
        if(function == null) {
            function = ((ScriptTemplate_Abstract)thisObject).getFunction(name, baseList);
            if(ref == null) {
                throw new Exception_Nodeable_FunctionNotFound(env, name, params);
            }
            throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
        }
        if(functionTemplate.getType().equals(thisObject.getType()) && !function.isStatic()) {
            functionTemplate = (ScriptTemplate_Abstract)thisObject;
        }
        assert LegacyDebugger.addSnapNode("Function", function);
        assert LegacyDebugger.addSnapNode("Function's Template", functionTemplate);
        if(!function.isStatic() && !(functionTemplate).isObject()) {
            throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
        }
        if(function.isStatic() && (functionTemplate).isObject()) {
            throw new Exception_Nodeable_FunctionNotFound(ref, name, params);
        }
        // Execute that function
        if(function instanceof ScriptFunction_Faux) {
            ((ScriptFunction_Faux)function).setFauxTemplate(functionTemplate);
            ((ScriptFunction_Faux)function).setTemplate((ScriptTemplate_Abstract)thisObject);
        }
        assert LegacyDebugger.close();
        env.advanceStack((ScriptTemplate_Abstract)thisObject, function);
        env.getCurrentFunction().execute(ref, baseList);
        ScriptValue_Abstract returning = env.getCurrentFunction().getReturnValue();
        if(returning == null && !env.getCurrentFunction().getReturnType().equals(ScriptValueType.VOID)) {
            if(ref == null) {
                throw new Exception_Nodeable_IllegalNullReturnValue(env, env.getCurrentFunction());
            }
            throw new Exception_Nodeable_IllegalNullReturnValue(ref, env.getCurrentFunction());
        }
        env.retreatStack();
        assert LegacyDebugger.close();
        return returning;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        return callFunction(getEnvironment(), this, this.object, this.functionName, this.params);
    }

    // ScriptValue_Abstract implementation
    public ScriptValueType getType() {
        try {
            return ((ScriptTemplate_Abstract)this.object.getValue()).getFunction(this.functionName, this.params).getReturnType();
        } catch(Exception_Nodeable ex) {
            throw new Exception_InternalError(getEnvironment(), ex.toString());
        }
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return getValue().castToType(ref, type);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return execute();
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        return getValue().setValue(ref, value);
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesEqual(ref, rhs);
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        return getValue().valuesCompare(ref, rhs);
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Function Call (" + ScriptFunction.getDisplayableFunctionName(this.functionName) + ")");
        assert super.nodificate();
        assert LegacyDebugger.addSnapNode("Parameters", this.params);
        assert LegacyDebugger.close();
        return true;
    }
}
