package com.dafrito.rfe;

import com.dafrito.rfe.inspect.Nodeable;

public class ScriptValue_Numeric implements ScriptValue, ScriptConvertible, Nodeable {
	// Static functions
	public static boolean testNumericValueConversion(ScriptValueType base, ScriptValueType cast) {
		// If type is a short or integer, all numeric types are valid (We implement all shorts as integers)
		// If type is a long, all numeric types but int and short are valid
		// If type is a float, only floats and doubles are valid
		// If type is a double, only another double is valid
		assert Debugger.openNode("Type-Match Tests", "Checking for Type-Match (" + base + " to " + cast + ")");
		if (ScriptValueType.isNumericType(base)) {
			if (!ScriptValueType.isNumericType(cast)) {
				assert Debugger.closeNode("Potential destination-type is not numeric, returning false.");
				return false;
			}
			if (base.equals(cast)) {
				assert Debugger.closeNode("Potential destination-type is equal to source, returning true.");
				return true;
			}
			if (base.equals(ScriptKeywordType.SHORT)) {
				assert Debugger.closeNode("Source-type is a short; all numeric types are valid casts, returning true.");
				return true;
			}
			if (base.equals(ScriptKeywordType.INT)) {
				assert Debugger.closeNode("Source-type is an integer; all numeric types are valid casts, returning true.");
				return true;
			}
			if (base.equals(ScriptKeywordType.LONG) && !cast.equals(ScriptKeywordType.SHORT) && !cast.equals(ScriptKeywordType.INT)) {
				assert Debugger.closeNode("Source-type is a long, and destination-type is a long or of higher precision, returning true.");
				return true;
			}
			if (base.equals(ScriptKeywordType.FLOAT) && cast.equals(ScriptKeywordType.DOUBLE)) {
				assert Debugger.closeNode("Source-type is a float, and cast is a double, so returning type.");
				return true;
			}
		}
		assert Debugger.closeNode("Invalid cast, returning false.");
		return false;
	}

	private final ScriptEnvironment environment;
	private final ScriptValueType type;

	private Number number;

	public ScriptValue_Numeric(ScriptEnvironment env, double value) {
		this(env, new Double(value));
	}

	public ScriptValue_Numeric(ScriptEnvironment env, float value) {
		this(env, new Float(value));
	}

	public ScriptValue_Numeric(ScriptEnvironment env, int value) {
		this(env, new Integer(value));
	}

	public ScriptValue_Numeric(ScriptEnvironment env, long value) {
		this(env, new Long(value));
	}

	public ScriptValue_Numeric(ScriptEnvironment env, Number value) {
		this.environment = env;
		this.type = ScriptValueType.getType(value);
		this.number = value;
	}

	public ScriptValue_Numeric(ScriptEnvironment env, short value) {
		this(env, new Short(value));
	}

	@Override
	public ScriptValue castToType(Referenced ref, ScriptValueType type) throws Exception_Nodeable {
		assert Debugger.openNode("Type Casting", "Casting (" + this.getType() + " to " + type + ")");
		ScriptValue value;
		ScriptEnvironment environment = this.getEnvironment();
		if (ref != null) {
			environment = ref.getEnvironment();
		}
		switch (type.getKeywordType()) {
		case SHORT:
			value = new ScriptValue_Numeric(environment, new Short(this.number.shortValue()));
			break;
		case INT:
			value = new ScriptValue_Numeric(environment, new Integer(this.number.intValue()));
			break;
		case LONG:
			value = new ScriptValue_Numeric(environment, new Long(this.number.longValue()));
			break;
		case FLOAT:
			value = new ScriptValue_Numeric(environment, new Float(this.number.floatValue()));
			break;
		case DOUBLE:
			value = new ScriptValue_Numeric(environment, new Double(this.number.doubleValue()));
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, type);
		}
		assert Debugger.closeNode("Returned value", this);
		return value;
	}

	// Overloaded Functions
	@Override
	public Object convert() {
		return this.number;
	}

	public Number decrement(Referenced ref) throws Exception_Nodeable {
		return this.increment(ref, new ScriptValue_Numeric(this.getEnvironment(), 1.0d));
	}

	public Number decrement(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Numeric Value Decrements", "Decrementing Numeric Value");
		assert Debugger.addSnapNode("Number before decrement", value);
		Number number = ((ScriptValue_Numeric) value.castToType(ref, this.getType())).getNumericValue();
		switch (this.getType().getKeywordType()) {
		case SHORT:
			number = new Integer(this.getNumericValue().intValue() - number.intValue());
			break;
		case INT:
			number = new Integer(this.getNumericValue().intValue() - number.intValue());
			break;
		case LONG:
			number = new Long(this.getNumericValue().longValue() - number.longValue());
			break;
		case FLOAT:
			number = new Float(this.getNumericValue().floatValue() - number.floatValue());
			break;
		case DOUBLE:
			number = new Double(this.getNumericValue().doubleValue() - number.doubleValue());
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, value);
		}
		assert Debugger.closeNode("Number after decrement", "" + number);
		return number;
	}

	public Number divide(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Numeric Value Divisions", "Dividing Numeric Value");
		assert Debugger.addSnapNode("Number before division", value);
		Number number = ((ScriptValue_Numeric) value.castToType(ref, this.getType())).getNumericValue();
		if (number.doubleValue() == 0) {
			throw new Exception_Nodeable_DivisionByZero(ref);
		}
		switch (this.getType().getKeywordType()) {
		case SHORT:
			number = new Integer(this.getNumericValue().intValue() / number.intValue());
			break;
		case INT:
			number = new Integer(this.getNumericValue().intValue() / number.intValue());
			break;
		case LONG:
			number = new Long(this.getNumericValue().longValue() / number.longValue());
			break;
		case FLOAT:
			number = new Float(this.getNumericValue().floatValue() / number.floatValue());
			break;
		case DOUBLE:
			number = new Double(this.getNumericValue().doubleValue() / number.doubleValue());
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, value);
		}
		assert Debugger.closeNode("Number after division", "" + number);
		return number;
	}

	// Extensions for number-stuff
	public double doubleValue() {
		return this.getNumericValue().doubleValue();
	}

	public float floatValue() {
		return this.getNumericValue().floatValue();
	}

	// Required ScriptValue_Abstract implementation
	@Override
	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public Number getNumericValue() {
		return this.number;
	}

	@Override
	public ScriptValueType getType() {
		return ScriptValueType.getType(this.number);
	}

	@Override
	public ScriptValue getValue() throws Exception_Nodeable {
		return this;
	}

	public Number increment(Referenced ref) throws Exception_Nodeable {
		return this.increment(ref, new ScriptValue_Numeric(this.getEnvironment(), 1.0d));
	}

	public Number increment(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Numeric Value Increments", "Incrementing Numeric Value");
		assert Debugger.addSnapNode("Number before increment", value);
		Number number = ((ScriptValue_Numeric) value.castToType(ref, this.getType())).getNumericValue();
		switch (this.getType().getKeywordType()) {
		case SHORT:
			number = new Integer(this.getNumericValue().intValue() + number.intValue());
			break;
		case INT:
			number = new Integer(this.getNumericValue().intValue() + number.intValue());
			break;
		case LONG:
			number = new Long(this.getNumericValue().longValue() + number.longValue());
			break;
		case FLOAT:
			number = new Float(this.getNumericValue().floatValue() + number.floatValue());
			break;
		case DOUBLE:
			number = new Double(this.getNumericValue().doubleValue() + number.doubleValue());
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, value);
		}
		assert Debugger.closeNode("Number after increment", "" + number);
		return number;
	}

	public int intValue() {
		return this.getNumericValue().intValue();
	}

	@Override
	public boolean isConvertibleTo(ScriptValueType type) {
		return ScriptValueType.isConvertibleTo(this.getEnvironment(), this.getType(), type);
	}

	public long longValue() {
		return this.getNumericValue().longValue();
	}

	public Number modulus(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Numeric Value Modulations", "Modulating Numeric Value");
		assert Debugger.addSnapNode("Number before modulus", value);
		Number number = ((ScriptValue_Numeric) value.castToType(ref, this.getType())).getNumericValue();
		if (number.doubleValue() == 0) {
			throw new Exception_Nodeable_DivisionByZero(ref);
		}
		switch (this.getType().getKeywordType()) {
		case SHORT:
			number = new Integer(this.getNumericValue().intValue() % number.intValue());
			break;
		case INT:
			number = new Integer(this.getNumericValue().intValue() % number.intValue());
			break;
		case LONG:
			number = new Long(this.getNumericValue().longValue() % number.longValue());
			break;
		case FLOAT:
			number = new Float(this.getNumericValue().floatValue() % number.floatValue());
			break;
		case DOUBLE:
			number = new Double(this.getNumericValue().doubleValue() % number.doubleValue());
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, value);
		}
		assert Debugger.closeNode("Number after modulus", "" + number);
		return number;
	}

	public Number multiply(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		assert Debugger.openNode("Numeric Value Multiplications", "Multiplying Numeric Value");
		assert Debugger.addSnapNode("Number before multiplication", value);
		Number number = ((ScriptValue_Numeric) value.castToType(ref, this.getType())).getNumericValue();
		switch (this.getType().getKeywordType()) {
		case SHORT:
			number = new Integer(this.getNumericValue().intValue() * number.intValue());
			break;
		case INT:
			number = new Integer(this.getNumericValue().intValue() * number.intValue());
			break;
		case LONG:
			number = new Long(this.getNumericValue().longValue() * number.longValue());
			break;
		case FLOAT:
			number = new Float(this.getNumericValue().floatValue() * number.floatValue());
			break;
		case DOUBLE:
			number = new Double(this.getNumericValue().doubleValue() * number.doubleValue());
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, value);
		}
		assert Debugger.closeNode("Number after multiplication", "" + number);
		return number;
	}

	@Override
	public void nodificate() {
		switch (this.getType().getKeywordType()) {
		case SHORT:
			assert Debugger.openNode(DebugString.NUMERICSCRIPTVALUESHORT);
			break;
		case INT:
			assert Debugger.openNode(DebugString.NUMERICSCRIPTVALUEINT);
			break;
		case LONG:
			assert Debugger.openNode(DebugString.NUMERICSCRIPTVALUELONG);
			break;
		case FLOAT:
			assert Debugger.openNode(DebugString.NUMERICSCRIPTVALUEFLOAT);
			break;
		case DOUBLE:
			assert Debugger.openNode(DebugString.NUMERICSCRIPTVALUEDOUBLE);
			break;
		default:
			throw new Exception_InternalError("Invalid default");
		}
		if (this.number == null) {
			assert Debugger.addNode("Numeric value: null");
		} else {
			assert Debugger.addNode("Numeric value: " + this.number.doubleValue());
		}
		assert Debugger.addNode("Reference: " + this);
		assert Debugger.closeNode();
	}

	public ScriptValue setNumericValue(Number value) {
		this.number = value;
		return this;
	}

	@Override
	public ScriptValue setValue(Referenced ref, ScriptValue value) throws Exception_Nodeable {
		Number number = ((ScriptValue_Numeric) value.castToType(ref, this.getType())).getNumericValue();
		switch (this.type.getKeywordType()) {
		case SHORT:
			this.number = new Short(number.shortValue());
			break;
		case INT:
			this.number = new Integer(number.intValue());
			break;
		case LONG:
			this.number = new Long(number.longValue());
			break;
		case FLOAT:
			this.number = new Float(number.floatValue());
			break;
		case DOUBLE:
			this.number = new Double(number.doubleValue());
			break;
		default:
			throw new Exception_Nodeable_ClassCast(ref, this, value);
		}
		return this;
	}

	public short shortValue() {
		return this.getNumericValue().shortValue();
	}

	@Override
	public int valuesCompare(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		ScriptValue_Numeric right = (ScriptValue_Numeric) rhs.castToType(ref, this.getType());
		if (this.number.equals(right.getNumericValue())) {
			return 0;
		}
		if (this.number.doubleValue() > right.getNumericValue().doubleValue()) {
			return 1;
		}
		return -1;
	}

	@Override
	public boolean valuesEqual(Referenced ref, ScriptValue rhs) throws Exception_Nodeable {
		if (!ScriptValueType.isNumericType(rhs.getType())) {
			throw new Exception_Nodeable_ClassCast(ref, rhs, this.getType());
		}
		return ((ScriptValue_Numeric) this.getValue()).doubleValue() == ((ScriptValue_Numeric) rhs.getValue()).doubleValue();
	}
}
