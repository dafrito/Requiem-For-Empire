import java.util.*;
public class FauxTemplate_Asset extends FauxTemplate implements ScriptConvertible,Nodeable{
	public static final String ASSETSTRING="Asset";
	private Asset m_asset;
	public FauxTemplate_Asset(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,ASSETSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Asset(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
		m_asset=new Asset(env);
	}
	public Asset getAsset(){return m_asset;}
	public void setAsset(Asset asset){m_asset=asset;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Asset(getEnvironment(),getType());}
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing asset faux template");
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Point.POINTSTRING)));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.getObjectType(getEnvironment())));
		addFauxFunction("setProperty",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addFauxFunction("getProperty",ScriptValueType.getObjectType(getEnvironment()),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Ace.ACESTRING)));
		addFauxFunction("addAce",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getAces",ScriptValueType.createType(getEnvironment(),FauxTemplate_List.LISTSTRING),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getLocation",ScriptValueType.createType(getEnvironment(),FauxTemplate_Point.POINTSTRING),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Point.POINTSTRING)));
		addFauxFunction("setLocation",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing Asset Faux Template Function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Asset template=(FauxTemplate_Asset)rawTemplate;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Asset)createObject(ref,template);}
			template.getAsset().setLocation(Parser.getPoint(params.get(0)));
			params.clear();
		}else if(name.equals("setProperty")){
			if(params.size()==2){
				template.getAsset().setProperty(Parser.getString(params.get(0)),params.get(1).getValue());
				assert Debugger.closeNode();
				return null;
			}
		}else if(name.equals("getProperty")){
			if(params.size()==1){
				ScriptValue_Abstract value=(ScriptValue_Abstract)((ScriptConvertible)template.getAsset().getProperty(Parser.getString(params.get(0)))).convert();
				assert Debugger.closeNode();
				return value;
			}
		}else if(name.equals("addAce")){
			template.getAsset().addAce(Parser.getAce(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("getAces")){
			List<ScriptValue_Abstract>list=new LinkedList<ScriptValue_Abstract>();
			for(Ace ace:template.getAsset().getAces()){
				list.add(Parser.getRiffAce(ace));
			}
			ScriptValue_Abstract returning=Parser.getRiffList(getEnvironment(),list);
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("getLocation")){
			assert template.getAsset().getLocation()!=null:"Asset location is null!";
			ScriptValue_Abstract returning=Parser.getRiffPoint(getEnvironment(),template.getAsset().getLocation());
			assert Debugger.closeNode();
			return returning;
		}else if(name.equals("setLocation")){
			template.getAsset().setLocation(Parser.getPoint(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}
		ScriptValue_Abstract returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable and ScriptConvertible interfaces
	public Object convert(){return m_asset;}
	public boolean nodificate(){
		assert Debugger.openNode("Asset Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(m_asset);
		assert Debugger.closeNode();
		return true;
	}
}
