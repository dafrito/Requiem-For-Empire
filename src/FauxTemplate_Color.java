import java.util.*;
public class FauxTemplate_Color extends FauxTemplate implements ScriptConvertible,Nodeable{
	public static final String COLORSTRING="Color";
	private java.awt.Color m_color;
	public FauxTemplate_Color(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,COLORSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Color(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
		m_color=java.awt.Color.BLACK;
	}
	public java.awt.Color getColor(){return m_color;}
	public void setColor(java.awt.Color color){m_color=color;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Color(getEnvironment(),getType());}
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing color faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addConstructor(getType(),fxnParams);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.INT));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.INT));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.INT));
		addConstructor(getType(),fxnParams);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.FLOAT));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.FLOAT));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.FLOAT));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getRed",ScriptValueType.INT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getGreen",ScriptValueType.INT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getBlue",ScriptValueType.INT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getRedOpacity",ScriptValueType.FLOAT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getGreenOpacity",ScriptValueType.FLOAT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getBlueOpacity",ScriptValueType.FLOAT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.INT));
		addFauxFunction("setRed",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("setGreen",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("setBlue",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.FLOAT));
		addFauxFunction("setRed",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("setGreen",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("setBlue",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addFauxFunction("setColor",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing color faux template function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Color template=(FauxTemplate_Color)rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Color)createObject(ref,template);}
			if(params.size()==1){
				template.setColor(RiffJavaToolbox.getColor(Parser.getString(params.get(0))));
			}else if(params.size()==3){
				if(params.get(0).isConvertibleTo(ScriptValueType.INT)){
					int r,g,b;
					r=Parser.getInteger(params.get(0));
					g=Parser.getInteger(params.get(1));
					b=Parser.getInteger(params.get(2));
					if(r<0||r>255){throw new Exception_Nodeable_InvalidColorRange(this,new Integer(r));}
					if(g<0||g>255){throw new Exception_Nodeable_InvalidColorRange(this,new Integer(g));}
					if(b<0||b>255){throw new Exception_Nodeable_InvalidColorRange(this,new Integer(b));}
					template.setColor(new java.awt.Color(r,g,b));
				}else{;
					float r,g,b;
					r=Parser.getFloat(params.get(0));
					g=Parser.getFloat(params.get(1));
					b=Parser.getFloat(params.get(2));
					if(r<0.0d||r>1.0d){throw new Exception_Nodeable_InvalidColorRange(this,new Float(r));}
					if(g<0.0d||g>1.0d){throw new Exception_Nodeable_InvalidColorRange(this,new Float(g));}
					if(b<0.0d||b>1.0d){throw new Exception_Nodeable_InvalidColorRange(this,new Float(b));}
					template.setColor(new java.awt.Color(r,g,b));
				}
			}
			params.clear();
		}else if(name.equals("getRed")){
			returning=Parser.getRiffInt(getEnvironment(),template.getColor().getRed());
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("getGreen")){
			returning=Parser.getRiffInt(getEnvironment(),template.getColor().getGreen());
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("getBlue")){
			returning=Parser.getRiffInt(getEnvironment(),template.getColor().getBlue());
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("setRed")){
			int value=0;
			if(params.get(0).getType().equals(ScriptValueType.FLOAT)){
				value=(int)(Parser.getFloat(params.get(0))*255.0d);
			}else{
				value=Parser.getInteger(params.get(0));
			}
			template.setColor(new java.awt.Color(value,template.getColor().getGreen(),template.getColor().getBlue()));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("setGreen")){
			int value=0;
			if(params.get(0).getType().equals(ScriptValueType.FLOAT)){
				value=(int)(Parser.getFloat(params.get(0))*255.0d);
			}else{
				value=Parser.getInteger(params.get(0));
			}
			template.setColor(new java.awt.Color(template.getColor().getRed(),value,template.getColor().getBlue()));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("setBlue")){
			int value=0;
			if(params.get(0).getType().equals(ScriptValueType.FLOAT)){
				value=(int)(Parser.getFloat(params.get(0))*255.0d);
			}else{
				value=Parser.getInteger(params.get(0));
			}
			template.setColor(new java.awt.Color(template.getColor().getRed(),template.getColor().getGreen(),value));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("setColor")){
			template.setColor(RiffJavaToolbox.getColor(Parser.getString(params.get(0))));
			assert Debugger.closeNode();
			return null;
		}
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable and ScriptConvertible interfaces
	public Object convert(){return m_color;}
	public boolean nodificate(){
		assert Debugger.openNode("Color Faux Template");
		assert super.nodificate();
		if(m_color==null){assert Debugger.addNode("Color: null");
		}else{assert Debugger.addNode("Color: " +RiffJavaToolbox.getColorName(m_color));}
		assert Debugger.closeNode();
		return true;
	}
}
