import java.util.*;
public class FauxTemplate_List extends FauxTemplate implements ScriptConvertible,Nodeable{
	public static final String LISTSTRING="List";
	private List<ScriptValue_Abstract>m_list=new LinkedList<ScriptValue_Abstract>();
	public FauxTemplate_List(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,LISTSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_List(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
	}
	public List<ScriptValue_Abstract>getList(){return m_list;}
	public void setList(List<ScriptValue_Abstract>list){m_list=list;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_List(getEnvironment(),getType());}
	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing list faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		disableFullCreation();
		getExtendedClass().initialize();
		List<ScriptValue_Abstract>fxnParams=FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.getObjectType(getEnvironment())));
		addFauxFunction("add",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),getType()));
		addFauxFunction("addAll",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=FauxTemplate.createEmptyParamList();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.INT));
		addFauxFunction("get",ScriptValueType.getObjectType(getEnvironment()),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=FauxTemplate.createEmptyParamList();
		addFauxFunction("size",ScriptValueType.INT,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing List Faux Template Function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_List template=(FauxTemplate_List)rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_List)createObject(ref,template);}
			params.clear();
		}else if(name.equals("add")){
			template.getList().add(params.get(0).getValue());
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("addAll")){
			template.getList().addAll(Parser.getList(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("get")){
			assert Debugger.closeNode();
			return template.getList().get(Parser.getInteger(params.get(0)));
		}else if(name.equals("size")){
			assert Debugger.closeNode();
			return Parser.getRiffInt(getEnvironment(),template.getList().size());
		}
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Convertible and Nodeable implementations
	public Object convert(){return m_list;}
	public boolean nodificate(){
		if(m_list==null){
			assert Debugger.openNode("List Faux Template (0 element(s))");
		}else{
			assert Debugger.openNode("List Faux Template ("+m_list.size()+" element(s))");
		}
		assert super.nodificate();
		assert Debugger.addSnapNode(DebugString.ELEMENTS,m_list);
		assert Debugger.closeNode();
		return true;
	}
}
