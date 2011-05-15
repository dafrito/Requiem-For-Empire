import java.util.*;
public class FauxTemplate_GraphicalElement extends FauxTemplate implements Nodeable{
	public static final String GRAPHICALELEMENTSTRING="GraphicalElement";
	public FauxTemplate_GraphicalElement(ScriptEnvironment env,ScriptValueType type,ScriptValueType extended,List<ScriptValueType>implemented,boolean isAbstract){
		super(env,type,extended,implemented,isAbstract);
	}
	public FauxTemplate_GraphicalElement(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,GRAPHICALELEMENTSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),true);
	}
	public FauxTemplate_GraphicalElement(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
	}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_GraphicalElement(getEnvironment(),getType());}
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing graphical element faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		disableFullCreation();
		getExtendedClass().initialize();
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing Graphical Element Faux Template Function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_GraphicalElement template=(FauxTemplate_GraphicalElement)rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable implementation
	public boolean nodificate(){
		assert Debugger.openNode("Graphical Element Faux Template");
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}
}
