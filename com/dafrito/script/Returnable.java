package com.dafrito.script;

import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.types.ScriptValue_Abstract;

public interface Returnable {
    // Returnable implementation
    public boolean shouldReturn();

    public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable;
}
