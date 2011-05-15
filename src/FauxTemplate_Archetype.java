import java.util.*;
public class FauxTemplate_Archetype extends FauxTemplate implements ScriptConvertible,Nodeable{
	public static final String ARCHETYPESTRING="Archetype";
	private Archetype m_archetype;
	public FauxTemplate_Archetype(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,ARCHETYPESTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Archetype(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
	}
	public Archetype getArchetype(){return m_archetype;}
	public void setArchetype(Archetype archetype){m_archetype=archetype;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Archetype(getEnvironment(),getType());}
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing archetype faux template");
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Ace.ACESTRING)));
		addFauxFunction("addParent",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getName",ScriptValueType.STRING,new LinkedList<ScriptValue_Abstract>(),ScriptKeywordType.PUBLIC,false,false);
		addFauxFunction("getParents",ScriptValueType.createType(getEnvironment(),FauxTemplate_List.LISTSTRING),new LinkedList<ScriptValue_Abstract>(),ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing archetype faux template function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Archetype template=(FauxTemplate_Archetype)rawTemplate;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Archetype)createObject(ref,template);}
			template.setArchetype(new Archetype(getEnvironment(),Parser.getString(params.get(0))));
			params.clear();
		}else if(name.equals("addParent")){
			template.getArchetype().addParent(Parser.getAce(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("getName")){
			ScriptValue_Abstract returning=Parser.getRiffString(getEnvironment(),template.getArchetype().getName());
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("getParents")){
			List<ScriptValue_Abstract>parents=new LinkedList<ScriptValue_Abstract>();
			for(Ace parent:template.getArchetype().getParents()){
				parents.add(Parser.getRiffAce(parent));
			}
			ScriptValue_Abstract returning=Parser.getRiffList(getEnvironment(),parents);
			assert Debugger.closeNode();
			return null;
		}
		ScriptValue_Abstract returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable and ScriptConvertible interfaces
	public Object convert(){return m_archetype;}
	public boolean nodificate(){
		assert Debugger.openNode("Archetype Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(m_archetype);
		assert Debugger.closeNode();
		return true;
	}
}
