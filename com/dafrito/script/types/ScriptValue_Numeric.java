package com.dafrito.script.types;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_ClassCast;
import com.dafrito.debug.Exceptions.Exception_Nodeable_DivisionByZero;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptConvertible;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptKeywordType;

public class ScriptValue_Numeric implements ScriptValue_Abstract, ScriptConvertible, Nodeable {
    private final ScriptEnvironment environment;
    private final ScriptValueType type;
    private Number number;

    public ScriptValue_Numeric(ScriptEnvironment env, short value) {
        this(env, new Short(value));
    }

    public ScriptValue_Numeric(ScriptEnvironment env, int value) {
        this(env, new Integer(value));
    }

    public ScriptValue_Numeric(ScriptEnvironment env, long value) {
        this(env, new Long(value));
    }

    public ScriptValue_Numeric(ScriptEnvironment env, float value) {
        this(env, new Float(value));
    }

    public ScriptValue_Numeric(ScriptEnvironment env, double value) {
        this(env, new Double(value));
    }

    public ScriptValue_Numeric(ScriptEnvironment env, Number value) {
        this.environment = env;
        this.type = ScriptValueType.getType(value);
        this.number = value;
    }

    // Required ScriptValue_Abstract implementation
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    public ScriptValueType getType() {
        return ScriptValueType.getType(this.number);
    }

    public boolean isConvertibleTo(ScriptValueType referenceType) {
        return ScriptValueType.isConvertibleTo(getEnvironment(), getType(), referenceType);
    }

    public ScriptValue_Abstract castToType(Referenced ref, ScriptValueType referenceType) throws Exception_Nodeable {
        assert LegacyDebugger.open("Type Casting", "Casting (" + getType() + " to " + referenceType + ")");
        ScriptValue_Abstract value;
        ScriptEnvironment workingEnvironment = getEnvironment();
        if(ref != null) {
            workingEnvironment = ref.getEnvironment();
        }
        switch(referenceType.getKeywordType()) {
            case SHORT:
                value = new ScriptValue_Numeric(workingEnvironment, new Short(this.number.shortValue()));
                break;
            case INT:
                value = new ScriptValue_Numeric(workingEnvironment, new Integer(this.number.intValue()));
                break;
            case LONG:
                value = new ScriptValue_Numeric(workingEnvironment, new Long(this.number.longValue()));
                break;
            case FLOAT:
                value = new ScriptValue_Numeric(workingEnvironment, new Float(this.number.floatValue()));
                break;
            case DOUBLE:
                value = new ScriptValue_Numeric(workingEnvironment, new Double(this.number.doubleValue()));
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, referenceType);
        }
        assert LegacyDebugger.close("Returned value", this);
        return value;
    }

    public ScriptValue_Abstract getValue() throws Exception_Nodeable {
        return this;
    }

    public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        Number newNumber = ((ScriptValue_Numeric)value.castToType(ref, getType())).getNumericValue();
        switch(this.type.getKeywordType()) {
            case SHORT:
                this.number = new Short(newNumber.shortValue());
                break;
            case INT:
                this.number = new Integer(newNumber.intValue());
                break;
            case LONG:
                this.number = new Long(newNumber.longValue());
                break;
            case FLOAT:
                this.number = new Float(newNumber.floatValue());
                break;
            case DOUBLE:
                this.number = new Double(newNumber.doubleValue());
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, value);
        }
        return this;
    }

    public boolean valuesEqual(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        if(!ScriptValueType.isNumericType(rhs.getType())) {
            throw new Exception_Nodeable_ClassCast(ref, rhs, getType());
        }
        return ((ScriptValue_Numeric)getValue()).doubleValue() == ((ScriptValue_Numeric)rhs.getValue()).doubleValue();
    }

    public int valuesCompare(Referenced ref, ScriptValue_Abstract rhs) throws Exception_Nodeable {
        ScriptValue_Numeric right = (ScriptValue_Numeric)rhs.castToType(ref, getType());
        if(this.number.equals(right.getNumericValue())) {
            return 0;
        }
        if(this.number.doubleValue() > right.getNumericValue().doubleValue()) {
            return 1;
        }
        return -1;
    }

    // Extensions for number-stuff
    public double doubleValue() {
        return getNumericValue().doubleValue();
    }

    public float floatValue() {
        return getNumericValue().floatValue();
    }

    public long longValue() {
        return getNumericValue().longValue();
    }

    public int intValue() {
        return getNumericValue().intValue();
    }

    public short shortValue() {
        return getNumericValue().shortValue();
    }

    public Number getNumericValue() {
        return this.number;
    }

    public ScriptValue_Abstract setNumericValue(Number value) {
        this.number = value;
        return this;
    }

    public Number increment(Referenced ref) throws Exception_Nodeable {
        return increment(ref, new ScriptValue_Numeric(getEnvironment(), 1.0d));
    }

    public Number increment(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Numeric Value Increments", "Incrementing Numeric Value");
        assert LegacyDebugger.addSnapNode("Number before increment", value);
        Number newNumber = ((ScriptValue_Numeric)value.castToType(ref, getType())).getNumericValue();
        switch(getType().getKeywordType()) {
            case SHORT:
                newNumber = new Integer(getNumericValue().intValue() + newNumber.intValue());
                break;
            case INT:
                newNumber = new Integer(getNumericValue().intValue() + newNumber.intValue());
                break;
            case LONG:
                newNumber = new Long(getNumericValue().longValue() + newNumber.longValue());
                break;
            case FLOAT:
                newNumber = new Float(getNumericValue().floatValue() + newNumber.floatValue());
                break;
            case DOUBLE:
                newNumber = new Double(getNumericValue().doubleValue() + newNumber.doubleValue());
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, value);
        }
        assert LegacyDebugger.close("Number after increment", "" + newNumber);
        return newNumber;
    }

    public Number decrement(Referenced ref) throws Exception_Nodeable {
        return increment(ref, new ScriptValue_Numeric(getEnvironment(), 1.0d));
    }

    public Number decrement(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Numeric Value Decrements", "Decrementing Numeric Value");
        assert LegacyDebugger.addSnapNode("Number before decrement", value);
        Number newNumber = ((ScriptValue_Numeric)value.castToType(ref, getType())).getNumericValue();
        switch(getType().getKeywordType()) {
            case SHORT:
                newNumber = new Integer(getNumericValue().intValue() - newNumber.intValue());
                break;
            case INT:
                newNumber = new Integer(getNumericValue().intValue() - newNumber.intValue());
                break;
            case LONG:
                newNumber = new Long(getNumericValue().longValue() - newNumber.longValue());
                break;
            case FLOAT:
                newNumber = new Float(getNumericValue().floatValue() - newNumber.floatValue());
                break;
            case DOUBLE:
                newNumber = new Double(getNumericValue().doubleValue() - newNumber.doubleValue());
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, value);
        }
        assert LegacyDebugger.close("Number after decrement", "" + newNumber);
        return newNumber;
    }

    public Number multiply(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Numeric Value Multiplications", "Multiplying Numeric Value");
        assert LegacyDebugger.addSnapNode("Number before multiplication", value);
        Number newNumber = ((ScriptValue_Numeric)value.castToType(ref, getType())).getNumericValue();
        switch(getType().getKeywordType()) {
            case SHORT:
                newNumber = new Integer(getNumericValue().intValue() * newNumber.intValue());
                break;
            case INT:
                newNumber = new Integer(getNumericValue().intValue() * newNumber.intValue());
                break;
            case LONG:
                newNumber = new Long(getNumericValue().longValue() * newNumber.longValue());
                break;
            case FLOAT:
                newNumber = new Float(getNumericValue().floatValue() * newNumber.floatValue());
                break;
            case DOUBLE:
                newNumber = new Double(getNumericValue().doubleValue() * newNumber.doubleValue());
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, value);
        }
        assert LegacyDebugger.close("Number after multiplication", "" + newNumber);
        return newNumber;
    }

    public Number divide(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Numeric Value Divisions", "Dividing Numeric Value");
        assert LegacyDebugger.addSnapNode("Number before division", value);
        Number newNumber = ((ScriptValue_Numeric)value.castToType(ref, getType())).getNumericValue();
        if(newNumber.doubleValue() == 0) {
            throw new Exception_Nodeable_DivisionByZero(ref);
        }
        switch(getType().getKeywordType()) {
            case SHORT:
                newNumber = new Integer(getNumericValue().intValue() / newNumber.intValue());
                break;
            case INT:
                newNumber = new Integer(getNumericValue().intValue() / newNumber.intValue());
                break;
            case LONG:
                newNumber = new Long(getNumericValue().longValue() / newNumber.longValue());
                break;
            case FLOAT:
                newNumber = new Float(getNumericValue().floatValue() / newNumber.floatValue());
                break;
            case DOUBLE:
                newNumber = new Double(getNumericValue().doubleValue() / newNumber.doubleValue());
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, value);
        }
        assert LegacyDebugger.close("Number after division", "" + newNumber);
        return newNumber;
    }

    public Number modulus(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        assert LegacyDebugger.open("Numeric Value Modulations", "Modulating Numeric Value");
        assert LegacyDebugger.addSnapNode("Number before modulus", value);
        Number newNumber = ((ScriptValue_Numeric)value.castToType(ref, getType())).getNumericValue();
        if(newNumber.doubleValue() == 0) {
            throw new Exception_Nodeable_DivisionByZero(ref);
        }
        switch(getType().getKeywordType()) {
            case SHORT:
                newNumber = new Integer(getNumericValue().intValue() % newNumber.intValue());
                break;
            case INT:
                newNumber = new Integer(getNumericValue().intValue() % newNumber.intValue());
                break;
            case LONG:
                newNumber = new Long(getNumericValue().longValue() % newNumber.longValue());
                break;
            case FLOAT:
                newNumber = new Float(getNumericValue().floatValue() % newNumber.floatValue());
                break;
            case DOUBLE:
                newNumber = new Double(getNumericValue().doubleValue() % newNumber.doubleValue());
                break;
            default:
                throw new Exception_Nodeable_ClassCast(ref, this, value);
        }
        assert LegacyDebugger.close("Number after modulus", "" + newNumber);
        return newNumber;
    }

    // Overloaded Functions
    public Object convert() {
        return this.number;
    }

    public boolean nodificate() {
        switch(getType().getKeywordType()) {
            case SHORT:
                assert LegacyDebugger.open(DebugString.NUMERICSCRIPTVALUESHORT);
                break;
            case INT:
                assert LegacyDebugger.open(DebugString.NUMERICSCRIPTVALUEINT);
                break;
            case LONG:
                assert LegacyDebugger.open(DebugString.NUMERICSCRIPTVALUELONG);
                break;
            case FLOAT:
                assert LegacyDebugger.open(DebugString.NUMERICSCRIPTVALUEFLOAT);
                break;
            case DOUBLE:
                assert LegacyDebugger.open(DebugString.NUMERICSCRIPTVALUEDOUBLE);
                break;
            default:
                throw new Exception_InternalError("Invalid default");
        }
        if(this.number == null) {
            assert LegacyDebugger.addNode("Numeric value: null");
        } else {
            assert LegacyDebugger.addNode("Numeric value: " + this.number.doubleValue());
        }
        assert LegacyDebugger.addNode("Reference: " + this);
        assert LegacyDebugger.close();
        return true;
    }

    // Static functions
    public static boolean testNumericValueConversion(ScriptValueType base, ScriptValueType cast) {
        // If type is a short or integer, all numeric types are valid (We
        // implement all shorts as integers)
        // If type is a long, all numeric types but int and short are valid
        // If type is a float, only floats and doubles are valid
        // If type is a double, only another double is valid
        assert LegacyDebugger.open("Type-Match Tests", "Checking for Type-Match (" + base + " to " + cast + ")");
        if(ScriptValueType.isNumericType(base)) {
            if(!ScriptValueType.isNumericType(cast)) {
                assert LegacyDebugger.close("Potential destination-type is not numeric, returning false.");
                return false;
            }
            if(base.equals(cast)) {
                assert LegacyDebugger.close("Potential destination-type is equal to source, returning true.");
                return true;
            }
            if(base.equals(ScriptKeywordType.SHORT)) {
                assert LegacyDebugger.close("Source-type is a short; all numeric types are valid casts, returning true.");
                return true;
            }
            if(base.equals(ScriptKeywordType.INT)) {
                assert LegacyDebugger.close("Source-type is an integer; all numeric types are valid casts, returning true.");
                return true;
            }
            if(base.equals(ScriptKeywordType.LONG)
                && !cast.equals(ScriptKeywordType.SHORT)
                && !cast.equals(ScriptKeywordType.INT)) {
                assert LegacyDebugger.close("Source-type is a long, and destination-type is a long or of higher precision, returning true.");
                return true;
            }
            if(base.equals(ScriptKeywordType.FLOAT) && cast.equals(ScriptKeywordType.DOUBLE)) {
                assert LegacyDebugger.close("Source-type is a float, and cast is a double, so returning type.");
                return true;
            }
        }
        assert LegacyDebugger.close("Invalid cast, returning false.");
        return false;
    }
}
