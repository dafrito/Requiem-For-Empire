import java.util.*;
public class FauxTemplate_Scheduler extends FauxTemplate implements Nodeable,ScriptConvertible{
	public static final String SCHEDULERSTRING="Scheduler";
	private Scheduler m_scheduler;
	public FauxTemplate_Scheduler(ScriptEnvironment env){
		super(env,ScriptValueType.createType(env,SCHEDULERSTRING),ScriptValueType.getObjectType(env),new LinkedList<ScriptValueType>(),false);
	}
	public FauxTemplate_Scheduler(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
	}
	public void setScheduler(Scheduler scheduler){m_scheduler=scheduler;}
	public Scheduler getScheduler(){return m_scheduler;}
	// Define default constructor here
	public ScriptTemplate instantiateTemplate(){return new FauxTemplate_Scheduler(getEnvironment(),getType());}
	// addFauxFunction(name,ScriptValueType type,List<ScriptValue_Abstract>params,ScriptKeywordType permission,boolean isAbstract)
	// All functions must be defined here. All function bodies are defined in 'execute'.
	public void initialize()throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Initializations","Initializing scheduler faux template");
		addConstructor(getType(),ScriptValueType.createEmptyParamList());
		List<ScriptValue_Abstract>params=new LinkedList<ScriptValue_Abstract>();
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING)));
		addConstructor(getType(),params);
		disableFullCreation();
		getExtendedClass().initialize();
		params=new LinkedList<ScriptValue_Abstract>();
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.LONG));
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Asset.ASSETSTRING)));
		addFauxFunction("schedule",ScriptValueType.VOID,params,ScriptKeywordType.PUBLIC,false,false);
		params=new LinkedList<ScriptValue_Abstract>();
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.LONG));
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_Asset.ASSETSTRING)));
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING)));
		addFauxFunction("schedule",ScriptValueType.VOID,params,ScriptKeywordType.PUBLIC,false,false);
		params=new LinkedList<ScriptValue_Abstract>();
		params.add(new ScriptValue_Faux(getEnvironment(),ScriptValueType.createType(getEnvironment(),FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING)));
		addFauxFunction("setDefaultListener",ScriptValueType.VOID,params,ScriptKeywordType.PUBLIC,false,false);
		params=new LinkedList<ScriptValue_Abstract>();
		addFauxFunction("start",ScriptValueType.VOID,params,ScriptKeywordType.PUBLIC,false,false);
		assert Debugger.closeNode();
	}
	// Function bodies are contained via a series of if statements in execute
	// Template will be null if the object is exactly of this type and is constructing, and thus must be created then
	public ScriptValue_Abstract execute(Referenced ref,String name,List<ScriptValue_Abstract>params,ScriptTemplate_Abstract rawTemplate)throws Exception_Nodeable{
		assert Debugger.openNode("Faux Template Executions","Executing scheduler faux template function ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		FauxTemplate_Scheduler template=(FauxTemplate_Scheduler)rawTemplate;
		ScriptValue_Abstract returning;
		assert Debugger.addSnapNode("Template provided",template);
		assert Debugger.addSnapNode("Parameters provided",params);
		ScriptValue_Abstract value;
		if(name==null||name.equals("")){
			if(template==null){template=(FauxTemplate_Scheduler)createObject(ref,template);}
			switch(params.size()){
				case 1:
				template.getScheduler().setDefaultListener(Parser.getSchedulerListener(params.get(0)));
				case 0:
				assert Debugger.closeNode();
				return template;
			}
		}else if(name.equals("schedule")){
			ScriptTemplate_Abstract listener=null;
			if(params.size()==3){
				listener=Parser.getTemplate(params.get(2));
			}
			template.getScheduler().schedule(Parser.getNumber(params.get(0)).longValue(),Parser.getAsset(params.get(1)),listener);
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("setDefaultListener")){
			template.getScheduler().setDefaultListener(Parser.getSchedulerListener(params.get(0)));
			assert Debugger.closeNode();
			return null;
		}else if(name.equals("start")){
			template.getScheduler().start();
			assert Debugger.closeNode();
			return null;
		}
		returning=getExtendedFauxClass().execute(ref,name,params,template);
		assert Debugger.closeNode();
		return returning;
	}
	// Nodeable implementation
	public Object convert(){return m_scheduler;}
	public boolean nodificate(){
		assert Debugger.openNode("Scheduler Faux Template");
		assert super.nodificate();
		assert Debugger.addNode(m_scheduler);
		assert Debugger.closeNode();
		return true;
	}
}
