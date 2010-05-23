package com.dafrito.script.types;

import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_VariableTypeNotFound;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;

public class ScriptValueType_StringDeferrer extends ScriptValueType {
    private final String typeString;
    private Referenced reference;

    public ScriptValueType_StringDeferrer(ScriptEnvironment env, String string) {
        super(env);
        assert env != null : "Environment is null";
        assert string != null : "String is null";
        this.typeString = string;
    }

    public ScriptValueType_StringDeferrer(Referenced ref, String string) {
        super(ref.getEnvironment());
        assert ref.getEnvironment() != null : "String is null";
        assert string != null : "Environment is null";
        this.reference = ref;
        this.typeString = string;
    }

    @Override
    public ScriptValueType getBaseType() throws Exception_Nodeable {
        ScriptValueType kw = getEnvironment().getType(this.typeString);
        if(kw == null) {
            if(this.reference == null) {
                throw new Exception_Nodeable_VariableTypeNotFound((ScriptEnvironment)null, this.typeString);
            }
            throw new Exception_Nodeable_VariableTypeNotFound(this.reference, this.typeString);
        }
        return kw;
    }
}
