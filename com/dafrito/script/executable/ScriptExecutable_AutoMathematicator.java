package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.ScriptOperatorType;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Numeric;

public class ScriptExecutable_AutoMathematicator extends ScriptElement implements ScriptExecutable,
        ScriptValue_Abstract {
    private ScriptValue_Abstract value;
    private ScriptOperatorType operator;
    private boolean isPost;

    public ScriptExecutable_AutoMathematicator(Referenced ref, ScriptValue_Abstract value, ScriptOperatorType operator,
        boolean isPost) {
        super(ref);
        this.value = value;
        this.operator = operator;
        this.isPost = isPost;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("Auto-Mathematicator Executions", "Executing Auto-Mathematicator");
        ScriptValue_Abstract returning;
        if(this.operator == ScriptOperatorType.INCREMENT) {
            returning = ((ScriptValue_Numeric)this.value.getValue()).setNumericValue(((ScriptValue_Numeric)this.value.getValue()).increment(this));
        } else {
            returning = ((ScriptValue_Numeric)this.value.getValue()).setNumericValue(((ScriptValue_Numeric)this.value.getValue()).decrement(this));
        }
        assert LegacyDebugger.close();
        return returning;
    }

    // ScriptValue_Abstract implementation
    public ScriptValueType getType() {
        return this.value.getType();
    }

    public boolean isConvertibleTo(ScriptValueType type) {
        return this.value.isConvertibleTo(type);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
        return this.value = this.value.castToType(ref, type);
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        assert this.value.getValue() instanceof ScriptValue_Numeric : "Should be a ScriptValue_Numeric: "
            + this.value.getValue();
        ScriptValue_Numeric thisResult = new ScriptValue_Numeric(getEnvironment(), ((ScriptValue_Numeric)this.value.getValue()).getNumericValue());
        ScriptValue_Numeric otherValue = (ScriptValue_Numeric)execute().getValue();
        if(this.isPost) {
            return thisResult;
        } 
        return otherValue;
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
        if(this.operator == ScriptOperatorType.INCREMENT) {
            if(this.isPost) {
                assert LegacyDebugger.open("Auto-Mathematicators", "Auto-Mathematicator(Post-Incrementing)");
            } else {
                assert LegacyDebugger.open("Auto-Mathematicators", "Auto-Mathematicator(Pre-Incrementing)");
            }
        } else {
            if(this.isPost) {
                assert LegacyDebugger.open("Auto-Mathematicators", "Auto-Mathematicator(Post-Decrementing)");
            } else {
                assert LegacyDebugger.open("Auto-Mathematicators", "Auto-Mathematicator(Pre-Decrementing)");
            }
        }
        assert LegacyDebugger.addSnapNode("Value", this.value);
        assert LegacyDebugger.close();
        return true;
    }
}
