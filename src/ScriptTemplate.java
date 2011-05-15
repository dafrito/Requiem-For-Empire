import java.util.*;
public class ScriptTemplate extends ScriptTemplate_Abstract implements ScriptValue_Abstract,Nodeable,ScriptConvertible{
	protected Map<String,List<ScriptFunction_Abstract>>m_functions;
	protected Map<String,ScriptValue_Variable>m_variables;
	private boolean m_isObject,m_isAbstract;
	private boolean m_isConstructing,m_fullCreation;
	private List<ScriptExecutable>m_preconstructors;
	private List<ScriptExecutable>m_templatePreconstructors;
	private ScriptValueType m_extended;
	public static ScriptTemplate createTemplate(ScriptEnvironment env,ScriptValueType type,ScriptValueType extended,List<ScriptValueType>interfaces,boolean isAbstract){
		return new ScriptTemplate(env,type,extended,interfaces,isAbstract);
	}
	public ScriptTemplate instantiateTemplate(){return new ScriptTemplate(getEnvironment(),getType());}
	protected ScriptTemplate(ScriptEnvironment env,ScriptValueType type){
		super(env,type);
		m_isObject=true;
		m_fullCreation=true;
		m_isConstructing=true;
		m_variables=new HashMap<String,ScriptValue_Variable>();
	}
	protected ScriptTemplate(ScriptEnvironment env,ScriptValueType type,ScriptValueType extended,List<ScriptValueType>interfaces,boolean isAbstract){
		super(env,type,extended,interfaces);
		m_fullCreation=true;
		m_isObject=false;
		m_variables=new HashMap<String,ScriptValue_Variable>();
		m_functions=new HashMap<String,List<ScriptFunction_Abstract>>();
		m_templatePreconstructors=new LinkedList<ScriptExecutable>();
		m_preconstructors=new LinkedList<ScriptExecutable>();
		m_extended=extended;
		m_isAbstract=isAbstract;
	}
	// Abstract-template implementation
	public boolean isFullCreation(){return m_fullCreation;}
	public void disableFullCreation(){m_fullCreation=false;}
	public boolean isConstructing()throws Exception_Nodeable{return m_isConstructing;}
	public void setConstructing(boolean constructing)throws Exception_Nodeable{
		assert Debugger.openNode("Object Construction Settings","Setting constructing-boolean to: "+constructing);
		m_isConstructing=constructing;
		assert Debugger.addNode(this);
		assert Debugger.closeNode();
	}
	public boolean isObject(){return m_isObject;}
	public boolean isAbstract()throws Exception_Nodeable{
		if(getEnvironment().getTemplate(getType())!=this){return getEnvironment().getTemplate(getType()).isAbstract();}
		return m_isAbstract;
	}
	public ScriptValue_Variable getStaticReference()throws Exception_Nodeable{
		return new ScriptValue_Variable(getEnvironment(),getType(),this,ScriptKeywordType.PUBLIC);
	}
	public ScriptTemplate_Abstract createObject(Referenced ref,ScriptTemplate_Abstract object)throws Exception_Nodeable{
		assert Debugger.openNode("Object Creations","Object Creation");
		if(object==null){
			if(isAbstract()){throw new Exception_Nodeable_IllegalAbstractObjectCreation(ref);}
			object=instantiateTemplate();
		}
		assert Debugger.addNode(object);
		getEnvironment().advanceStack(object,null);
		if(m_preconstructors.size()>0){
			assert Debugger.openNode("Calling preconstructor expressions ("+m_preconstructors.size()+" expression(s))");
			if(!object.isConstructing()){object.disableFullCreation();}
			object.setConstructing(true);
			for(ScriptExecutable exec:m_preconstructors){
				exec.execute();
			}
			assert Debugger.closeNode();
		}
		object.disableFullCreation();
		if(getExtendedClass()!=null){
			assert Debugger.openNode("Now sending to base class preconstructor ("+getEnvironment().getName(getExtendedClass().getType())+")");
			object=getExtendedClass().createObject(ref,object);
			assert Debugger.closeNode();
		}
		if(getInterfaces()!=null&&getInterfaces().size()>0){
			assert Debugger.openNode("Calling interface preconstructors ("+getInterfaces().size()+" preconstructor(s))");
			for(ScriptValueType interfaceType:getInterfaces()){
				object=getEnvironment().getTemplate(interfaceType).createObject(ref,object);
			}
			assert Debugger.closeNode();
		}
		object.setConstructing(false);
		if(object.getFunctions()!=null){
			for(ScriptFunction_Abstract function:object.getFunctions()){
				if(function.isAbstract()){throw new Exception_Nodeable_AbstractFunctionNotImplemented(ref,object,function);}
			}
		}else{
			assert Debugger.addNode(object);
		}
		getEnvironment().retreatStack();
		assert Debugger.closeNode();
		return object;
	}
	public ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value)throws Exception_Nodeable{
		if(isConstructing()){
			assert Debugger.openNode("Object Variable Additions","Adding Variable to Object ("+name+")");
			assert Debugger.addNode(this);
			assert Debugger.addNode(value);
			if(m_variables.get(name)!=null){throw new Exception_Nodeable_VariableAlreadyDefined(ref,this,name);}
			if(isFullCreation()||!value.getPermission().equals(ScriptKeywordType.PRIVATE)){
				m_variables.put(name,value);
				assert Debugger.addSnapNode("Variable successfully added",this);
			}else{
				assert Debugger.addNode("Variable is private, and our template is not in its full-creation phase.");
			}
			assert Debugger.closeNode();
			return value;
		}
		assert Debugger.openNode("Local Variable Additions","Adding Variable to Stack ("+name+")");
		assert Debugger.addNode(value);
		if(getEnvironment().getVariableFromStack(name)!=null){throw new Exception_Nodeable_VariableAlreadyDefined(ref,this,name);}
		getEnvironment().addVariableToStack(name,value);
		assert Debugger.closeNode();
		return value;
	}
	public ScriptValue_Variable getVariable(String name)throws Exception_Nodeable{
		assert Debugger.openNode("Object Variable Retrievals","Retrieving Variable From Object ('"+name+"')");
		assert Debugger.addNode(this);
		ScriptValue_Variable var=m_variables.get(name);
		if(var==null&&isObject()){
			assert Debugger.openNode("Variable not a member, so checking static members");
			var=getEnvironment().getTemplate(getType()).getVariable(name);
			assert Debugger.closeNode();
		}
		if(var!=null){
			if(var.getPermission().equals(ScriptKeywordType.PUBLIC)){
				assert Debugger.closeNode("Variable found and permission valid (public)",var);
				return var;
			}else if(var.getPermission().equals(ScriptKeywordType.PRIVATE)){
				if(getEnvironment().getCurrentObject()==this||(getEnvironment().getTemplate(getType())==this&&getEnvironment().getCurrentObject().getType().equals(getType()))){
					assert Debugger.closeNode("Variable found and permission valid (private)",var);
					return var;
				}
			}
		}
		assert Debugger.closeNode("Variable not found or permission is invalid");
		return null;
	}
	public void addFunction(Referenced ref,String name,ScriptFunction_Abstract function)throws Exception_Nodeable{
		if(getEnvironment().getTemplate(getType())!=null&&getEnvironment().getTemplate(getType())!=this){getEnvironment().getTemplate(getType()).addFunction(ref,name,function);return;}
		assert Debugger.openNode("Object Function Additions","Adding Function to Object ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		assert Debugger.addNode(this);
		assert Debugger.addNode(function);
		if(!isFullCreation()&&(name==null||name.equals(""))){
			assert Debugger.closeNode("Function is a constructor, and our template is not in its full-creation phase.");
			return;
		}
		if(m_functions.get(name)!=null){
			List<ScriptFunction_Abstract>list=m_functions.get(name);
			if(function.isAbstract()){
				for(ScriptFunction_Abstract currentFxn:list){
					// It's an abstract function and we implement it, so return.
					if(function.areParametersEqual(currentFxn.getParameters())){
						assert Debugger.closeNode("The template has this abstract function implemented.");
						return;
					}
				}
			}else{
				for(int i=0;i<list.size();i++){
					ScriptFunction_Abstract currentFxn=list.get(i);
					// We already implement this function, so return.
					if(function.areParametersEqual(currentFxn.getParameters())){
						if(currentFxn instanceof ScriptExecutable_ParseFunction){throw new Exception_Nodeable_FunctionAlreadyDefined(ref,name);}
						list.remove(i);
						list.add(function);
						assert Debugger.closeNode("The template overloads an existing function");
						return;
					}
				}
				list.add(function);
				assert Debugger.closeNode("Function was successfully added",this);
				return;
			}
		}
		m_functions.put(name,new LinkedList<ScriptFunction_Abstract>());
		m_functions.get(name).add(function);
		assert Debugger.closeNode("Function was successfully added",this);
	}
	public List<ScriptFunction_Abstract>getFunctions(){
		if(m_functions==null){return null;}
		List<ScriptFunction_Abstract>functions=new LinkedList<ScriptFunction_Abstract>();
		for(List<ScriptFunction_Abstract>fxnList:m_functions.values()){
			functions.addAll(fxnList);
		}
		return functions;
	}
	public ScriptFunction_Abstract getFunction(String name,List<ScriptValue_Abstract>params){
		if(getEnvironment().getTemplate(getType())!=null&&getEnvironment().getTemplate(getType())!=this){
			return getEnvironment().getTemplate(getType()).getFunction(name,params);
		}
		assert Debugger.openNode("Object Function Retrievals","Retrieving Function from Object ("+ScriptFunction.getDisplayableFunctionName(name)+")");
		assert Debugger.addSnapNode("Current template",this);
		List<ScriptFunction_Abstract>list=m_functions.get(name);
		if(list!=null&&list.size()>0){
			assert Debugger.addSnapNode("Functions found",list);
			for(ScriptFunction_Abstract function:list){
				if(function.areParametersConvertible(params)){
					assert Debugger.closeNode("Params match, returning function",function);
					return function;
				}
			}
		}
		ScriptFunction_Abstract fxn=null;
		if(getExtendedClass()!=null){
			fxn=getExtendedClass().getFunction(name,params);
		}
		if(fxn!=null){assert Debugger.closeNode("Returning function",fxn);
		}else{assert Debugger.closeNode("Function not found");}
		return fxn;
	}
	public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn){
		if(getEnvironment().getTemplate(getType())!=null&&getEnvironment().getTemplate(getType())!=this){
			return getEnvironment().getTemplate(getType()).getFunctionTemplate(fxn);
		}
		for(List<ScriptFunction_Abstract>fxns:m_functions.values()){
			if(fxns.contains(fxn)){return this;}
		}
		if(getExtendedClass()!=null){
			return getExtendedClass().getFunctionTemplate(fxn);
		}else{
			return null;
		}
	}
	public void addTemplatePreconstructorExpression(ScriptExecutable exec)throws Exception_Nodeable{
		assert Debugger.openNode("Template Preconstructor Additions","Adding Template Preconstructor");
		assert Debugger.addNode("Template",this);
		assert Debugger.addNode(exec);
		m_templatePreconstructors.add(exec);
		assert Debugger.closeNode();
	}
	public void addPreconstructorExpression(ScriptExecutable exec)throws Exception_Nodeable{
		assert Debugger.openNode("Preconstructor Additions","Adding Preconstructor");
		assert Debugger.addNode("Template",this);
		assert Debugger.addNode(exec);
		m_preconstructors.add(exec);
		assert Debugger.closeNode();
	}
	public void initialize()throws Exception_Nodeable{
		m_variables.clear();
		if(m_templatePreconstructors==null||m_templatePreconstructors.size()==0){return;}
		assert Debugger.openNode("Template Initializations","Initializing Template ("+getType()+")");
		if(!isAbstract()){
			for(Map.Entry entry:m_functions.entrySet()){
				List<ScriptFunction_Abstract>functions=m_functions.get(entry.getKey());
				for(int i=0;i<functions.size();i++){
					if(functions.get(i).isAbstract()){
						// We don't implement it, so fail.
						throw new Exception_Nodeable_UnimplementedFunction(getEnvironment(),this,(String)entry.getKey());
					}
				}
			}
		}
		getEnvironment().advanceStack(this,null);
		setConstructing(true);
		assert Debugger.openNode("Executing preconstructors ("+m_templatePreconstructors.size()+" preconstructor(s))");
		for(ScriptExecutable exec:m_templatePreconstructors){
			exec.execute();
		}
		setConstructing(false);
		assert Debugger.closeNode();
		getEnvironment().retreatStack();
		assert Debugger.closeNode();
	}
	public void initializeFunctions(Referenced ref)throws Exception_Nodeable{
		assert Debugger.openNode("Unparsed Member-Function Initialization","Initializing Unparsed Member Functions ("+getType()+")");
		getEnvironment().advanceStack(this,null);
		assert Debugger.openNode("Adding static member-variables");
		for(ScriptExecutable exec:m_templatePreconstructors){
			if(exec instanceof ScriptExecutable_CreateVariable){
				getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable)exec).getName(),(ScriptValue_Variable)exec);
			}else if(exec instanceof ScriptExecutable_AssignValue&&((ScriptExecutable_AssignValue)exec).getLeft()instanceof ScriptExecutable_CreateVariable){
				getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable)((ScriptExecutable_AssignValue)exec).getLeft()).getName(),(ScriptValue_Variable)((ScriptExecutable_AssignValue)exec).getLeft());
			}
		}
		assert Debugger.closeNode();
		List<Object>deleteList=new LinkedList<Object>();
		for(Map.Entry entry:m_functions.entrySet()){
			List<ScriptFunction_Abstract>functions=m_functions.get(entry.getKey());
			for(int i=0;i<functions.size();i++){
				if(functions.get(i) instanceof ScriptExecutable_ParseFunction){
					ScriptExecutable_ParseFunction fxn=(ScriptExecutable_ParseFunction)functions.get(i);
					functions.remove(i);
					getEnvironment().advanceNestedStack();
					assert fxn!=null;
					if(((ScriptExecutable_ParseFunction)fxn).getName()==null||((ScriptExecutable_ParseFunction)fxn).getName().equals("")||!((ScriptExecutable_ParseFunction)fxn).isStatic()){
						assert Debugger.openNode("Adding member-variables since this function is not static");
						for(ScriptExecutable exec:m_preconstructors){
							if(exec instanceof ScriptExecutable_CreateVariable){
								getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable)exec).getName(),(ScriptValue_Variable)exec);
							}else if(exec instanceof ScriptExecutable_AssignValue&&((ScriptExecutable_AssignValue)exec).getLeft()instanceof ScriptExecutable_CreateVariable){
								getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable)((ScriptExecutable_AssignValue)exec).getLeft()).getName(),(ScriptValue_Variable)((ScriptExecutable_AssignValue)exec).getLeft());
							}
						}
						assert Debugger.closeNode();
					}
					assert Debugger.openNode("Parameter Variable Additions");
					for(ScriptValue_Abstract value:fxn.getParameters()){
						if(value instanceof ScriptExecutable_CreateVariable){
							getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable)value).getName(),(ScriptExecutable_CreateVariable)value);
						}
					}
					assert Debugger.closeNode();
					ScriptFunction_Abstract function=Parser.parseFunction(fxn,getType());
					assert Debugger.addSnapNode("Adding function to this template",function);
					functions.add(i,function);
					getEnvironment().retreatNestedStack();
				}
			}
			if(functions.size()==0){
				deleteList.add(entry.getKey());
			}
		}
		for(Object obj:deleteList){
			m_functions.remove(obj);
		}
		getEnvironment().retreatStack();
		assert Debugger.closeNode();
	}
	// Abstract nodeable implementation
	public boolean nodificate(){
		if(m_isObject){assert Debugger.openNode("Object ("+getType()+")");
		}else{assert Debugger.openNode("Object-Template ("+getType()+")");}
		if(getExtendedClass()!=null){
			assert Debugger.addSnapNode("Extended template ("+getExtendedClass().getType()+")",getExtendedClass());
		}
		if(getInterfaces()!=null&&getInterfaces().size()>0){
			assert Debugger.addSnapNode("Implemented templates (" + getInterfaces().size() + " template(s))",getInterfaces());
		}
		if(m_functions!=null&&m_functions.size()>0){
			assert Debugger.openNode("Functions ("+m_functions.size()+" function(s))");
			for(Map.Entry entry:m_functions.entrySet()){
				assert Debugger.addSnapNode(ScriptFunction.getDisplayableFunctionName((String)entry.getKey()),entry.getValue());
			}
			assert Debugger.closeNode();
		}
		if(m_variables!=null&&m_variables.size()>0){
			assert Debugger.addSnapNode("Variables ("+m_variables.size()+" member variable(s))",m_variables);
		}
		assert Debugger.addNode("Object: "+m_isObject);
		if(!m_isObject){
			if(m_templatePreconstructors!=null&&m_templatePreconstructors.size()>0){
				assert Debugger.addSnapNode("Template Preconstructors ("+m_templatePreconstructors.size()+" static preconstructor(s))",m_templatePreconstructors);
			}
			if(m_preconstructors!=null&&m_preconstructors.size()>0){
				assert Debugger.addSnapNode("Preconstructors ("+m_preconstructors.size()+" preconstructor(s))",m_preconstructors);
			}
			assert Debugger.addNode("Abstract: "+m_isAbstract);
		}else{
			assert Debugger.addNode("Constructing: "+m_isConstructing);
		}
		assert Debugger.closeNode();
		return true;
	}
	// ScriptConvertible implementation
	public Object convert(){return this;}
}
