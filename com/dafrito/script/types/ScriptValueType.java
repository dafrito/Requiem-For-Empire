package com.dafrito.script.types;

import java.util.LinkedList;
import java.util.List;

import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeyword;
import com.dafrito.script.ScriptKeywordType;
import com.dafrito.script.templates.FauxTemplate_Object;

public class ScriptValueType {

    public static ScriptValueType VOID = new ScriptValueType(null);
    public static ScriptValueType BOOLEAN = new ScriptValueType(null);
    public static ScriptValueType SHORT = new ScriptValueType(null);
    public static ScriptValueType INT = new ScriptValueType(null);
    public static ScriptValueType LONG = new ScriptValueType(null);
    public static ScriptValueType FLOAT = new ScriptValueType(null);
    public static ScriptValueType DOUBLE = new ScriptValueType(null);
    public static ScriptValueType STRING = new ScriptValueType(null);
    private final int type;
    private static int identifierSeed = 0;
    private final ScriptEnvironment environment;

    public static List<ScriptValue_Abstract> createEmptyParamList() {
        return new LinkedList<ScriptValue_Abstract>();
    }

    public ScriptValueType(ScriptEnvironment env) {
        this.type = identifierSeed++;
        this.environment = env;
    }

    public ScriptValueType(ScriptEnvironment env, int type) {
        this.type = type;
        this.environment = env;
    }

    public ScriptKeywordType getKeywordType() {
        if(equals(VOID)) {
            return ScriptKeywordType.VOID;
        }
        if(equals(BOOLEAN)) {
            return ScriptKeywordType.BOOLEAN;
        }
        if(equals(SHORT)) {
            return ScriptKeywordType.SHORT;
        }
        if(equals(INT)) {
            return ScriptKeywordType.INT;
        }
        if(equals(LONG)) {
            return ScriptKeywordType.LONG;
        }
        if(equals(FLOAT)) {
            return ScriptKeywordType.FLOAT;
        }
        if(equals(DOUBLE)) {
            return ScriptKeywordType.DOUBLE;
        }
        if(equals(STRING)) {
            return ScriptKeywordType.STRING;
        }
        return null;
    }

    public String getName() {
        return getName(this);
    }

    // Miscellaneous functions
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public static void initialize(ScriptEnvironment env) throws Exception_Nodeable {
        env.addType(null, "void", ScriptValueType.VOID);
        env.addType(null, "boolean", ScriptValueType.BOOLEAN);
        env.addType(null, "short", ScriptValueType.SHORT);
        env.addType(null, "int", ScriptValueType.INT);
        env.addType(null, "long", ScriptValueType.LONG);
        env.addType(null, "float", ScriptValueType.FLOAT);
        env.addType(null, "double", ScriptValueType.DOUBLE);
        env.addType(null, "String", ScriptValueType.STRING);
    }

    public static String getName(ScriptValueType type) {
        if(type.equals(VOID)) {
            return "void";
        } else if(type.equals(BOOLEAN)) {
            return "boolean";
        } else if(type.equals(SHORT)) {
            return "short";
        } else if(type.equals(INT)) {
            return "int";
        } else if(type.equals(LONG)) {
            return "long";
        } else if(type.equals(FLOAT)) {
            return "float";
        } else if(type.equals(DOUBLE)) {
            return "double";
        } else if(type.equals(STRING)) {
            return "string";
        }
        assert type.getEnvironment() != null;
        return type.getEnvironment().getName(type);
    }

    public static ScriptValueType getObjectType(ScriptEnvironment env) {
        return createType(env, FauxTemplate_Object.OBJECTSTRING);
    }

    /**
     * Standard type-checking functions getBaseType is necessary because our
     * extensions of ValueType overload it to provide their functionality with
     * us still using just this getType
     * 
     * @return The base type of this value
     * 
     * @throws Exception_Nodeable Thrown by subclasses
     */
    public ScriptValueType getBaseType() throws Exception_Nodeable {
        return this;
    }

    public int getType() throws Exception_Nodeable {
        return getBaseType() == this ? this.type : getBaseType().getType();
    }

    public static boolean isConvertibleTo(ScriptEnvironment env, ScriptValueType base, ScriptValueType cast) {
        if(isNumericType(base)) {
            return ScriptValue_Numeric.testNumericValueConversion(base, cast);
        }
        if(isPrimitiveType(base)) {
            return base.equals(cast);
        }
        return env.getTemplate(base).isConvertibleTo(cast);
    }

    // Type-creation functions
    public static ScriptValueType createType(ScriptEnvironment env, String type) {
        return new ScriptValueType_StringDeferrer(env, type);
    }

    public static ScriptValueType createType(Referenced ref, String type) {
        return new ScriptValueType_StringDeferrer(ref, type);
    }

    public static ScriptValueType createType(ScriptValue_Abstract value) {
        return new ScriptValueType_ValueDeferrer(value);
    }

    public static ScriptValueType createType(ScriptValue_Abstract template, String name) {
        return new ScriptValueType_ObjectDeferrer(template, name);
    }

    public static ScriptValueType createType(ScriptEnvironment env, ScriptValue_Abstract template, String name) {
        return new ScriptValueType_ObjectDeferrer(env, template, name);
    }

    // Easy-check functions
    public static ScriptValueType getType(Number number) {
        if(number instanceof Integer) {
            return ScriptValueType.INT;
        }
        if(number instanceof Double) {
            return ScriptValueType.DOUBLE;
        }
        if(number instanceof Short) {
            return ScriptValueType.SHORT;
        }
        if(number instanceof Long) {
            return ScriptValueType.LONG;
        }
        if(number instanceof Float) {
            return ScriptValueType.FLOAT;
        }
        throw new Exception_InternalError("Invalid default");
    }

    public static boolean isNumericType(ScriptValueType type) {
        if(type.equals(ScriptValueType.SHORT)) {
            return true;
        }
        if(type.equals(ScriptValueType.INT)) {
            return true;
        }
        if(type.equals(ScriptValueType.LONG)) {
            return true;
        }
        if(type.equals(ScriptValueType.FLOAT)) {
            return true;
        }
        if(type.equals(ScriptValueType.DOUBLE)) {
            return true;
        }
        return false;
    }

    public boolean isPrimitiveType() {
        return isPrimitiveType(this);
    }

    public static boolean isPrimitiveType(ScriptValueType keyword) {
        return keyword != null
            && (isNumericType(keyword) || keyword.equals(ScriptValueType.BOOLEAN) || keyword.equals(ScriptValueType.STRING));
    }

    public static boolean isStringType(ScriptValueType type) {
        return type.equals(ScriptValueType.STRING);
    }

    public static boolean isBooleanType(ScriptValueType type) {
        return type.equals(ScriptValueType.BOOLEAN);
    }

    public static boolean isReturnablePrimitiveType(ScriptValueType type) {
        return(isPrimitiveType(type) || type.equals(ScriptValueType.VOID));
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        try {
            if(o instanceof ScriptKeywordType) {
                return ScriptKeyword.getValueType((ScriptKeywordType)o).getType() == getType();
            } else if(o instanceof String) {
                return toString().equals(o);
            } else {
                return ((ScriptValueType)o).getType() == getType();
            }
        } catch(Exception_Nodeable ex) {
            throw new Exception_InternalError("Failed value-type comparison", ex);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
