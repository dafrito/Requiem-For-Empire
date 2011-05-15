import java.util.*;
public class FauxTemplate_Rectangle extends FauxTemplate_InterfaceElement implements ScriptConvertible,Nodeable{
	public static final String RECTANGLESTRING="Rectangle";
	public FauxTemplate_Rectangle(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,RECTANGLESTRING),ScriptValueType.createType(env,FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Rectangle(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
		setElement(new InterfaceElement_Rectangle(env,null,null));
	}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Rectangle(getEnvironment(),getType());}
	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing rectangle faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),Stylesheet.STYLESHEETSTRING)));
		addConstructor(getType(),fxnParams);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),Stylesheet.STYLESHEETSTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),Stylesheet.STYLESHEETSTRING)));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing Rectangle Faux Template Function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Rectangle template=(FauxTemplate_Rectangle)rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		ScriptValue_Abstract value;
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Rectangle)createObject(ref,template);}
			getExtendedFauxClass().execute(ref,name,params,template);
			assert Debugger.closeNode();
			return template;
		}
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Convertible and Nodeable implementation
	public Object convert(){return getElement();}
	public boolean nodificate(){
		assert Debugger.openNode("Rectangle Faux Template");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
