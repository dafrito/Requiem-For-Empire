public class ScriptValue_Numeric implements ScriptValue_Abstract,ScriptConvertible,Nodeable{
	private final ScriptEnvironment m_environment;
	private final ScriptValueType m_type;
	private Number m_number;
	public ScriptValue_Numeric(ScriptEnvironment env,short value){this(env,new Short(value));}
	public ScriptValue_Numeric(ScriptEnvironment env,int value){this(env,new Integer(value));}
	public ScriptValue_Numeric(ScriptEnvironment env,long value){this(env,new Long(value));}
	public ScriptValue_Numeric(ScriptEnvironment env,float value){this(env,new Float(value));}
	public ScriptValue_Numeric(ScriptEnvironment env,double value){this(env,new Double(value));}
	public ScriptValue_Numeric(ScriptEnvironment env,Number value){
		m_environment=env;
		m_type=ScriptValueType.getType(value);
		m_number=value;
	}
	// Required ScriptValue_Abstract implementation
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public ScriptValueType getType(){return ScriptValueType.getType(m_number);}
	public boolean isConvertibleTo(ScriptValueType type){return ScriptValueType.isConvertibleTo(getEnvironment(),getType(),type);}
	public ScriptValue_Abstract castToType(Referenced ref,ScriptValueType type)throws Exception_Nodeable{
		assert Debugger.openNode("Type Casting","Casting ("+getType()+" to "+type+")");
		ScriptValue_Abstract value;
		ScriptEnvironment environment=getEnvironment();
		if(ref!=null){environment=ref.getEnvironment();}
		switch(type.getKeywordType()){
			case SHORT:value=new ScriptValue_Numeric(environment,new Short(m_number.shortValue()));break;
			case INT:value=new ScriptValue_Numeric(environment,new Integer(m_number.intValue()));break;
			case LONG:value=new ScriptValue_Numeric(environment,new Long(m_number.longValue()));break;
			case FLOAT:value=new ScriptValue_Numeric(environment,new Float(m_number.floatValue()));break;
			case DOUBLE:value=new ScriptValue_Numeric(environment,new Double(m_number.doubleValue()));break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,type);
		}
		assert Debugger.closeNode("Returned value",this);
		return value;
	}
	public ScriptValue_Abstract getValue()throws Exception_Nodeable{return this;}
	public ScriptValue_Abstract setValue(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		Number number=((ScriptValue_Numeric)value.castToType(ref,getType())).getNumericValue();
		switch(m_type.getKeywordType()){
			 case SHORT:m_number=new Short(number.shortValue());break;
			 case INT:m_number=new Integer(number.intValue());break;
			 case LONG:m_number=new Long(number.longValue());break;
			 case FLOAT:m_number=new Float(number.floatValue());break;
			 case DOUBLE:m_number=new Double(number.doubleValue());break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,value);
		}
		return this;
	}
	public boolean valuesEqual(Referenced ref,ScriptValue_Abstract rhs)throws Exception_Nodeable{
		if(!ScriptValueType.isNumericType(rhs.getType())){throw new Exception_Nodeable_ClassCast(ref,rhs,getType());}
		return ((ScriptValue_Numeric)getValue()).doubleValue()==((ScriptValue_Numeric)rhs.getValue()).doubleValue();
	}
	public int valuesCompare(Referenced ref,ScriptValue_Abstract rhs)throws Exception_Nodeable{
		ScriptValue_Numeric right=(ScriptValue_Numeric)rhs.castToType(ref,getType());
		if(m_number.equals(right.getNumericValue())){return 0;}
		if(m_number.doubleValue()>right.getNumericValue().doubleValue()){return 1;}
		return -1;
	}
	// Extensions for number-stuff
	public double doubleValue(){return getNumericValue().doubleValue();}
	public float floatValue(){return getNumericValue().floatValue();}
	public long longValue(){return getNumericValue().longValue();}
	public int intValue(){return getNumericValue().intValue();}
	public short shortValue(){return getNumericValue().shortValue();}
	public Number getNumericValue(){return m_number;}
	public ScriptValue_Abstract setNumericValue(Number value){m_number=value;return this;}
	public Number increment(Referenced ref)throws Exception_Nodeable{return increment(ref,new ScriptValue_Numeric(getEnvironment(),1.0d));}
	public Number increment(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Numeric Value Increments","Incrementing Numeric Value");
		assert Debugger.addSnapNode("Number before increment",value);
		Number number=((ScriptValue_Numeric)value.castToType(ref,getType())).getNumericValue();
		switch(getType().getKeywordType()){
			case SHORT:number=new Integer(getNumericValue().intValue()+number.intValue());break;
			case INT:number=new Integer(getNumericValue().intValue()+number.intValue());break;
			case LONG:number=new Long(getNumericValue().longValue()+number.longValue());break;
			case FLOAT:number=new Float(getNumericValue().floatValue()+number.floatValue());break;
			case DOUBLE:number=new Double(getNumericValue().doubleValue()+number.doubleValue());break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,value);
		}
		assert Debugger.closeNode("Number after increment",""+number);
		return number;
	}
	public Number decrement(Referenced ref)throws Exception_Nodeable{return increment(ref,new ScriptValue_Numeric(getEnvironment(),1.0d));}
	public Number decrement(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Numeric Value Decrements","Decrementing Numeric Value");
		assert Debugger.addSnapNode("Number before decrement",value);
		Number number=((ScriptValue_Numeric)value.castToType(ref,getType())).getNumericValue();
		switch(getType().getKeywordType()){
			case SHORT:number=new Integer(getNumericValue().intValue()-number.intValue());break;
			case INT:number=new Integer(getNumericValue().intValue()-number.intValue());break;
			case LONG:number=new Long(getNumericValue().longValue()-number.longValue());break;
			case FLOAT:number=new Float(getNumericValue().floatValue()-number.floatValue());break;
			case DOUBLE:number=new Double(getNumericValue().doubleValue()-number.doubleValue());break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,value);
		}
		assert Debugger.closeNode("Number after decrement",""+number);
		return number;
	}
	public Number multiply(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Numeric Value Multiplications","Multiplying Numeric Value");
		assert Debugger.addSnapNode("Number before multiplication",value);
		Number number=((ScriptValue_Numeric)value.castToType(ref,getType())).getNumericValue();
		switch(getType().getKeywordType()){
			case SHORT:number=new Integer(getNumericValue().intValue()*number.intValue());break;
			case INT:number=new Integer(getNumericValue().intValue()*number.intValue());break;
			case LONG:number=new Long(getNumericValue().longValue()*number.longValue());break;
			case FLOAT:number=new Float(getNumericValue().floatValue()*number.floatValue());break;
			case DOUBLE:number=new Double(getNumericValue().doubleValue()*number.doubleValue());break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,value);
		}
		assert Debugger.closeNode("Number after multiplication",""+number);
		return number;
	}
	public Number divide(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Numeric Value Divisions","Dividing Numeric Value");
		assert Debugger.addSnapNode("Number before division",value);
		Number number=((ScriptValue_Numeric)value.castToType(ref,getType())).getNumericValue();
		if(number.doubleValue()==0){throw new Exception_Nodeable_DivisionByZero(ref);}
		switch(getType().getKeywordType()){
			case SHORT:number=new Integer(getNumericValue().intValue()/number.intValue());break;
			case INT:number=new Integer(getNumericValue().intValue()/number.intValue());break;
			case LONG:number=new Long(getNumericValue().longValue()/number.longValue());break;
			case FLOAT:number=new Float(getNumericValue().floatValue()/number.floatValue());break;
			case DOUBLE:number=new Double(getNumericValue().doubleValue()/number.doubleValue());break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,value);
		}
		assert Debugger.closeNode("Number after division",""+number);
		return number;
	}
	public Number modulus(Referenced ref, ScriptValue_Abstract value)throws Exception_Nodeable{
		assert Debugger.openNode("Numeric Value Modulations","Modulating Numeric Value");
		assert Debugger.addSnapNode("Number before modulus",value);
		Number number=((ScriptValue_Numeric)value.castToType(ref,getType())).getNumericValue();
		if(number.doubleValue()==0){throw new Exception_Nodeable_DivisionByZero(ref);}
		switch(getType().getKeywordType()){
			case SHORT:number=new Integer(getNumericValue().intValue()%number.intValue());break;
			case INT:number=new Integer(getNumericValue().intValue()%number.intValue());break;
			case LONG:number=new Long(getNumericValue().longValue()%number.longValue());break;
			case FLOAT:number=new Float(getNumericValue().floatValue()%number.floatValue());break;
			case DOUBLE:number=new Double(getNumericValue().doubleValue()%number.doubleValue());break;
			default:throw new Exception_Nodeable_ClassCast(ref,this,value);
		}
		assert Debugger.closeNode("Number after modulus",""+number);
		return number;
	}
	// Overloaded Functions
	public Object convert(){return m_number;}
	public boolean nodificate(){
		switch(getType().getKeywordType()){
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
			default:throw new Exception_InternalError("Invalid default");
		}
		if(m_number==null){assert Debugger.addNode("Numeric value: null");
		}else{assert Debugger.addNode("Numeric value: " + m_number.doubleValue());}
		assert Debugger.addNode("Reference: "+this);
		assert Debugger.closeNode();
		return true;
	}
	// Static functions
	public static boolean testNumericValueConversion(ScriptValueType base,ScriptValueType cast){
		// If type is a short or integer, all numeric types are valid (We implement all shorts as integers)
		// If type is a long, all numeric types but int and short are valid
		// If type is a float, only floats and doubles are valid
		// If type is a double, only another double is valid
		assert Debugger.openNode("Type-Match Tests","Checking for Type-Match ("+base+" to " +cast+")");
		if(ScriptValueType.isNumericType(base)){
			if(!ScriptValueType.isNumericType(cast)){assert Debugger.closeNode("Potential destination-type is not numeric, returning false.");return false;}
			if(base.equals(cast)){assert Debugger.closeNode("Potential destination-type is equal to source, returning true.");return true;}
			if(base.equals(ScriptKeywordType.SHORT)){assert Debugger.closeNode("Source-type is a short; all numeric types are valid casts, returning true.");return true;}
			if(base.equals(ScriptKeywordType.INT)){assert Debugger.closeNode("Source-type is an integer; all numeric types are valid casts, returning true.");return true;}
			if(base.equals(ScriptKeywordType.LONG)&&!cast.equals(ScriptKeywordType.SHORT)&&!cast.equals(ScriptKeywordType.INT)){
				assert Debugger.closeNode("Source-type is a long, and destination-type is a long or of higher precision, returning true.");
				return true;
			}
			if(base.equals(ScriptKeywordType.FLOAT)&&cast.equals(ScriptKeywordType.DOUBLE)){
				assert Debugger.closeNode("Source-type is a float, and cast is a double, so returning type.");
				return true;
			}
		}
		assert Debugger.closeNode("Invalid cast, returning false.");
		return false;
	}
}
