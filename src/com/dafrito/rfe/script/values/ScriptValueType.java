package com.dafrito.rfe.script.values;

import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.InternalException;
import com.dafrito.rfe.script.exceptions.ScriptException;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;
import com.dafrito.rfe.script.proxies.FauxTemplate_Object;

public class ScriptValueType {
	public static final ScriptValueType VOID = new ScriptValueType();
	public static final ScriptValueType BOOLEAN = new ScriptValueType();
	public static final ScriptValueType SHORT = new ScriptValueType();
	public static final ScriptValueType INT = new ScriptValueType();
	public static final ScriptValueType LONG = new ScriptValueType();
	public static final ScriptValueType FLOAT = new ScriptValueType();
	public static final ScriptValueType DOUBLE = new ScriptValueType();
	public static final ScriptValueType STRING = new ScriptValueType();

	public static ScriptValueType createType(Referenced ref, String type) {
		return new ScriptValueType_StringDeferrer(ref, type);
	}

	public static ScriptValueType createType(ScriptEnvironment env, ScriptValue template, String name) {
		return new ScriptValueType_ObjectDeferrer(env, template, name);
	}

	// Type-creation functions
	public static ScriptValueType createType(ScriptEnvironment env, String type) {
		return new ScriptValueType_StringDeferrer(env, type);
	}

	public static ScriptValueType createType(ScriptValue value) {
		return new ScriptValueType_ValueDeferrer(value);
	}

	public static ScriptValueType createType(ScriptValue template, String name) {
		return new ScriptValueType_ObjectDeferrer(template, name);
	}

	public static String getName(ScriptValueType type) {
		if (type.equals(VOID)) {
			return "void";
		} else if (type.equals(BOOLEAN)) {
			return "boolean";
		} else if (type.equals(SHORT)) {
			return "short";
		} else if (type.equals(INT)) {
			return "int";
		} else if (type.equals(LONG)) {
			return "long";
		} else if (type.equals(FLOAT)) {
			return "float";
		} else if (type.equals(DOUBLE)) {
			return "double";
		} else if (type.equals(STRING)) {
			return "string";
		}
		assert type.getEnvironment() != null;
		return type.getEnvironment().getName(type);
	}

	public static ScriptValueType getObjectType(ScriptEnvironment env) {
		return createType(env, FauxTemplate_Object.OBJECTSTRING);
	}

	// Easy-check functions
	public static ScriptValueType getType(Number number) {
		if (number instanceof Integer) {
			return ScriptValueType.INT;
		}
		if (number instanceof Double) {
			return ScriptValueType.DOUBLE;
		}
		if (number instanceof Short) {
			return ScriptValueType.SHORT;
		}
		if (number instanceof Long) {
			return ScriptValueType.LONG;
		}
		if (number instanceof Float) {
			return ScriptValueType.FLOAT;
		}
		throw new IllegalArgumentException("Invalid default");
	}

	public static boolean isBooleanType(ScriptValueType type) {
		return type.equals(ScriptValueType.BOOLEAN);
	}

	public static boolean isConvertibleTo(ScriptEnvironment env, ScriptValueType base, ScriptValueType cast) {
		if (isNumericType(base)) {
			return ScriptValue_Numeric.testNumericValueConversion(base, cast);
		}
		if (isPrimitiveType(base)) {
			return base.equals(cast);
		}
		return env.getTemplate(base).isConvertibleTo(cast);
	}

	public static boolean isNumericType(ScriptValueType type) {
		if (type.equals(ScriptValueType.SHORT)) {
			return true;
		}
		if (type.equals(ScriptValueType.INT)) {
			return true;
		}
		if (type.equals(ScriptValueType.LONG)) {
			return true;
		}
		if (type.equals(ScriptValueType.FLOAT)) {
			return true;
		}
		if (type.equals(ScriptValueType.DOUBLE)) {
			return true;
		}
		return false;
	}

	public static boolean isPrimitiveType(ScriptValueType keyword) {
		return keyword != null && (isNumericType(keyword) || keyword.equals(ScriptValueType.BOOLEAN) || keyword.equals(ScriptValueType.STRING));
	}

	public static boolean isReturnablePrimitiveType(ScriptValueType type) {
		return (isPrimitiveType(type) || type.equals(ScriptValueType.VOID));
	}

	public static boolean isStringType(ScriptValueType type) {
		return type.equals(ScriptValueType.STRING);
	}

	private final int type;

	private static int identifierSeed = 0;

	private final ScriptEnvironment environment;

	public ScriptValueType() {
		this(null);
	}

	public ScriptValueType(ScriptEnvironment env) {
		this.type = identifierSeed++;
		this.environment = env;
	}

	public ScriptValueType(ScriptEnvironment env, int type) {
		this.type = type;
		this.environment = env;
	}

	// Standard type-checking functions
	// getBaseType is necessary because our extensions of ValueType overload it to provide their functionality with us
	// still using just this getType
	public ScriptValueType getBaseType() throws ScriptException {
		return this;
	}

	// Miscellaneous functions
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public ScriptKeywordType getKeywordType() {
		if (this.equals(VOID)) {
			return ScriptKeywordType.VOID;
		}
		if (this.equals(BOOLEAN)) {
			return ScriptKeywordType.BOOLEAN;
		}
		if (this.equals(SHORT)) {
			return ScriptKeywordType.SHORT;
		}
		if (this.equals(INT)) {
			return ScriptKeywordType.INT;
		}
		if (this.equals(LONG)) {
			return ScriptKeywordType.LONG;
		}
		if (this.equals(FLOAT)) {
			return ScriptKeywordType.FLOAT;
		}
		if (this.equals(DOUBLE)) {
			return ScriptKeywordType.DOUBLE;
		}
		if (this.equals(STRING)) {
			return ScriptKeywordType.STRING;
		}
		return null;
	}

	public String getName() {
		return getName(this);
	}

	public int getType() throws ScriptException {
		if (this.getBaseType() == this) {
			return this.type;
		} else {
			return this.getBaseType().getType();
		}
	}

	public boolean isPrimitiveType() {
		return isPrimitiveType(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ScriptKeywordType) {
			return this.getKeywordType() != null && this.getKeywordType().equals(obj);
		}
		if (obj instanceof String) {
			return this.getName().equals(obj);
		}
		if (!(obj instanceof ScriptValueType)) {
			return false;
		}
		try {
			return ((ScriptValueType) obj).getType() == this.getType();
		} catch (ScriptException e) {
			throw new InternalException(this.getEnvironment(), e);
		}
	}

	@Override
	public int hashCode() {
		try {
			return this.getType();
		} catch (ScriptException e) {
			throw new InternalException(this.getEnvironment(), e);
		}
	}

	@Override
	public String toString() {
		return String.format("ScriptValueType[%s]", this.getName());
	}
}
