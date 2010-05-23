package com.dafrito.script.executable;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.Returnable;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptExecutable_ReturnValue extends ScriptElement implements ScriptExecutable, Returnable {
    private ScriptValue_Abstract value;

    public ScriptExecutable_ReturnValue(Referenced ref, ScriptValue_Abstract value) {
        super(ref);
        this.value = value;
    }

    // Returnabled implementation
    public boolean shouldReturn() {
        return true;
    }

    public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable {
        return this.value.getValue();
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("Executing returnable script-value");
        ScriptValue_Abstract thisResult = this.value.getValue();
        assert LegacyDebugger.close();
        return thisResult;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Returnable Script-Value");
        assert LegacyDebugger.addSnapNode("Returned Value", this.value);
        assert LegacyDebugger.close();
        return true;
    }
}
