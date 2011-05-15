import java.util.*;
public class FauxTemplate_DiscreteRegion extends FauxTemplate_GraphicalElement implements ScriptConvertible,Nodeable{
	public static final String DISCRETEREGIONSTRING="DiscreteRegion";
	private DiscreteRegion m_region;
	private java.awt.Color m_color;
	private static int i=0;
	public FauxTemplate_DiscreteRegion(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,DISCRETEREGIONSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
		assert env!=null;
	}
	public FauxTemplate_DiscreteRegion(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
	}
	public DiscreteRegion getRegion(){return m_region;}
	public void setRegion(DiscreteRegion region){m_region=region;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_DiscreteRegion(getEnvironment(),getType());}
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing discrete region faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract>fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),getType()));
		addConstructor(getType(),fxnParams);
		disableFullCreation();
		getExtendedClass().initialize();
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Point.POINTSTRING)));
		addFauxFunction("add",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Asset.ASSETSTRING)));
		addFauxFunction("addAsset",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.getObjectType(getEnvironment())));
		addFauxFunction("setProperty",ScriptValueType.VOID,fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		fxnParams.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.STRING));
		addFauxFunction("getProperty",ScriptValueType.getObjectType(getEnvironment()),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		fxnParams=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("getCenter",ScriptValueType.createType(getEnvironment(),FauxTemplate_Point.POINTSTRING),fxnParams,ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing Discrete Region Faux Template Function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_DiscreteRegion template=(FauxTemplate_DiscreteRegion)rawTemplate;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_DiscreteRegion)createObject(ref,template);}
			template.setRegion(m_region=new DiscreteRegion(getEnvironment()));
			params.clear();
		}else if(name.equals("add")){
			//m_region.addPoint(Parser.getPoint(aaron is a sand jewparams.get(0)));
			if(params.size()==1){
				template.getRegion().addPoint(Parser.getPoint(params.get(0)));
				assert Debugger.closeNode();
				return null;
			}
		}else if(name.equals("addAsset")){
			if(template.getRegion().getProperty("Archetypes")==null){
				template.getRegion().setProperty("Archetypes",ArchetypeMapNode.createTree(Parser.getAsset(params.get(0))));
			}else{
				((ArchetypeMapNode)template.getRegion().getProperty("Archetypes")).addAsset(Parser.getAsset(params.get(0)));
			}
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("setProperty")){
			if(params.size()==2){
				template.getRegion().setProperty(Parser.getString(params.get(0)),Parser.getObject(params.get(1)));
				assert Debugger.closeNode();
				return null;
			}
		}else if(name.equals("getProperty")){
			if(params.size()==1){
				ScriptValue_Abstract returning=(ScriptValue_Abstract)Parser.convert(getEnvironment(),template.getRegion().getProperty(Parser.getString(params.get(0))));
				assert Debugger.addSnapNode("Retrieved property",returning);
				assert Debugger.closeNode();
				return returning;
			}
		}else if(name.equals("getCenter")){
			ScriptValue_Abstract returning=Parser.getRiffPoint(getEnvironment(),template.getRegion().getCenter());
			assert Debugger.closeNode();
			return returning;
		}
		ScriptValue_Abstract returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable and ScriptConvertible interfaces
	public Object convert(){return m_region;}
	public boolean nodificate(){
		assert Debugger.openNode("Discrete Region Faux Script-Element");
		assert super.nodificate();
		assert Debugger.addNode(m_region);
		assert Debugger.closeNode();
		return true;
	}
}
