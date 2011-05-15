import java.util.*;
public class FauxTemplate_Ace extends FauxTemplate implements ScriptConvertible,Nodeable{
	public static final String ACESTRING="Ace";
	private Ace m_ace;
	public FauxTemplate_Ace(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,ACESTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Ace(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
	}
	public Ace getAce(){return m_ace;}
	public void setAce(Ace ace){m_ace=ace;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Ace(getEnvironment(),getType());}
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing ace faux template");
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Archetype.ARCHETYPESTRING)));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.DOUBLE));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.DOUBLE));
		addFauxFunction("setEfficiency",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getEfficiency",ScriptValueType.DOUBLE,new LinkedList<ScriptValue_Abstract>(),ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getArchetype",ScriptValueType.createType(getEnvironment(),FauxTemplate_Archetype.ARCHETYPESTRING),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing ace faux template function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Ace template=(FauxTemplate_Ace)rawTemplate;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Ace)createObject(ref,template);}
			template.setAce(new Ace(getEnvironment(),Parser.getArchetype(params.get(0)),Parser.getDouble(params.get(1))));
			params.clear();
		}else if(name.equals("setEfficiency")){
			template.getAce().setEfficiency(Parser.getDouble(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("getEfficiency")){
			ScriptValue_Abstract returning=Parser.getRiffDouble(getEnvironment(),template.getAce().getEfficiency());
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("getArchetype")){
			ScriptValue_Abstract returning=Parser.getRiffArchetype(template.getAce().getArchetype());
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue_Abstract returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable and ScriptConvertible interfaces
	public Object convert(){return m_ace;}
	public boolean nodificate(){
		assert Debugger.openNode("Ace Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(m_ace);
		assert Debugger.closeNode();
		return true;
	}
}
