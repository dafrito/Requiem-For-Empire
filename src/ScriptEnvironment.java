import java.util.*;
import javax.swing.JOptionPane;
class ThreadStack implements Nodeable{
	private VariableTable m_variableTable=new VariableTable();
	private Stack<ScriptTemplate_Abstract>m_objectStack=new Stack<ScriptTemplate_Abstract>(); // Stack of called objects
	private Stack<ScriptFunction_Abstract>m_functionStack=new Stack<ScriptFunction_Abstract>(); // Stack of called functions
	public ScriptValue_Variable getVariableFromStack(String name){
		return m_variableTable.getVariableFromStack(name);
	}
	public synchronized boolean nodificate(){
		assert Debugger.openNode("Thread Stack");
		assert Debugger.addNode(m_variableTable);
		assert Debugger.addSnapNode("Object stack ("+m_objectStack.size()+")",m_objectStack);
		assert Debugger.addSnapNode("Function stack ("+m_functionStack.size()+")",m_functionStack);
		assert Debugger.closeNode();
		return true;
	}
	public synchronized void addVariable(String name,ScriptValue_Variable variable){
		if(variable==null){
			assert Debugger.openNode("Undefined Variable Stack Additions","Adding Undefined Variable to the Stack ("+name+")");
		}else{
			assert Debugger.openNode("Variable Stack Additions","Adding Variable to the Stack ("+name+")");
			assert Debugger.addNode(variable);
		}
		assert Debugger.addNode(this);
		m_variableTable.addVariable(name,variable);
		assert Debugger.closeNode();
	}
	public synchronized ScriptTemplate_Abstract getCurrentObject(){
		if(m_objectStack.size()==0){throw new Exception_InternalError("No call stack");}
		return m_objectStack.peek();
	}
	public synchronized ScriptFunction_Abstract getCurrentFunction(){
		if(m_functionStack.size()==0){throw new Exception_InternalError("No call stack");}
		return m_functionStack.peek();
	}
	public synchronized void advanceNestedStack(){
		m_variableTable.advanceNestedStack();
	}
	public synchronized void retreatNestedStack(){
		m_variableTable.retreatNestedStack();	
	}
	public synchronized void advanceStack(ScriptTemplate_Abstract template,ScriptFunction_Abstract fxn)throws Exception_Nodeable{
		assert Debugger.openNode("Stack Advancements and Retreats","Advancing Stack (Stack size before advance: " + m_functionStack.size()+")");
		assert (m_functionStack.size()==m_objectStack.size())&&(m_objectStack.size()==m_variableTable.getStacks().size()):"Stacks unequal: Function-stack: " + m_functionStack.size() + " Object-stack: " + m_objectStack.size() + " Variable-stack: " + m_variableTable.getStacks().size();
		if(template!=null){
			assert Debugger.addSnapNode("Advancing object",template);
		}
		assert Debugger.addSnapNode("Advancing function",fxn);
		if(template==null){template=getCurrentObject();}
		if(template!=null){m_objectStack.push((ScriptTemplate_Abstract)template.getValue());
		}else{m_objectStack.push(null);}
		m_functionStack.push(fxn);
		m_variableTable.advanceStack();
		assert Debugger.closeNode();
	}
	public synchronized void retreatStack(){
		assert Debugger.openNode("Stack Advancements and Retreats","Retreating Stack (Stack size before retreat: " + m_functionStack.size()+")");
		assert (m_functionStack.size()==m_objectStack.size())&&(m_objectStack.size()==m_variableTable.getStacks().size()):"Stacks unequal: Function-stack: " + m_functionStack.size() + " Object-stack: " + m_objectStack.size() + " Variable-stack: " + m_variableTable.getStacks().size();
		if(m_variableTable.getStacks().size()>0){
			m_variableTable.retreatStack();
		}
		if(m_objectStack.size()>0){
			m_objectStack.pop();
			if(m_objectStack.size()>0){
				assert Debugger.addSnapNode("New Current Object",m_objectStack.peek());
			}
		}
		if(m_functionStack.size()>0){
			m_functionStack.pop();
			if(m_functionStack.size()>0){
				assert Debugger.addSnapNode("New Current Function",m_functionStack.peek());
			}
		}
		assert Debugger.closeNode();
	}
}
class VariableTable implements Nodeable{
	Stack<VariableStack>m_stacks=new Stack<VariableStack>();
	public ScriptValue_Variable getVariableFromStack(String name){
		ScriptValue_Variable variable=m_stacks.peek().getVariableFromStack(name);
		return variable;
	}
	public synchronized void addVariable(String name,ScriptValue_Variable variable){
		m_stacks.peek().addVariable(name,variable);
	}
	public Stack<VariableStack>getStacks(){return m_stacks;}
	public synchronized void retreatStack(){m_stacks.pop();}
	public synchronized void advanceStack(){m_stacks.push(new VariableStack());}
	public synchronized void advanceNestedStack(){
		m_stacks.peek().advanceNestedStack();
	}
	public synchronized void retreatNestedStack(){
		m_stacks.peek().retreatNestedStack();
	}
	public synchronized boolean nodificate(){
		assert Debugger.openNode("Variable Table");
		assert Debugger.addSnapNode("Variable Stacks ("+m_stacks.size()+" stack(s))",m_stacks);
		assert Debugger.closeNode();
		return true;
	}
}
class VariableStack implements Nodeable{
	private Stack<Map<String,ScriptValue_Variable>>m_nestedStacks=new Stack<Map<String,ScriptValue_Variable>>();
	public VariableStack(){
		m_nestedStacks.push(new HashMap<String,ScriptValue_Variable>());
	}
	public synchronized ScriptValue_Variable getVariableFromStack(String name){
		for(Map<String,ScriptValue_Variable> map:m_nestedStacks){
			if(map.containsKey(name)){return map.get(name);}
		}
		return null;
	}
	public synchronized void advanceNestedStack(){
		assert Debugger.openNode("Stack Advancements and Retreats","Advancing Nested Stack (Nested stack size before advance: " + m_nestedStacks.size()+")");
		assert Debugger.addNode(this);
		m_nestedStacks.push(new HashMap<String,ScriptValue_Variable>());
		assert Debugger.closeNode();
	}
	public synchronized void retreatNestedStack(){
		assert m_nestedStacks.size()>0;
		assert Debugger.openNode("Stack Advancements and Retreats","Retreating Nested Stack (Nested stack size before retreat: " + m_nestedStacks.size()+")");
		assert Debugger.addNode(this);
		m_nestedStacks.pop();
		assert Debugger.closeNode();
	}
	public synchronized void addVariable(String name,ScriptValue_Variable variable){
		m_nestedStacks.peek().put(name,variable);
	}
	public synchronized boolean nodificate(){
		assert Debugger.openNode("Variable Stack");
		assert Debugger.openNode("Nested Stacks ("+m_nestedStacks.size()+" stack(s))");
		for(Map<String,ScriptValue_Variable> map:m_nestedStacks){
			assert Debugger.openNode("Stack ("+map.size()+" variable(s))");
			assert Debugger.addNode(map);
			assert Debugger.closeNode();
		}
		assert Debugger.closeNode();
		assert Debugger.closeNode();
		return true;
	}
}
public class ScriptEnvironment implements Nodeable{
	private Map<String,ScriptValueType>m_variableTypes=new HashMap<String,ScriptValueType>(); // Map of variable-Types(Variable-type-name, short)
	private Map<String,ScriptTemplate_Abstract>m_templates=new HashMap<String,ScriptTemplate_Abstract>(); // Map of object templates(Short,ScriptTemplate)
	private List<javax.swing.Timer>m_timers=new LinkedList<javax.swing.Timer>();
	private Map<String,ThreadStack>m_threads=new HashMap<String,ThreadStack>();
	public void reset(){
		assert Debugger.openNode("Resetting Environment");
		m_variableTypes.clear();
		m_templates.clear();
		clearStacks();
		System.gc();
		initialize();
		assert Debugger.closeNode();
	}
	public void clearStacks(){
		m_threads.put(Thread.currentThread().getName(),new ThreadStack());
	}
	public ScriptEnvironment(){
		initialize();
	}
	public void initialize(){
		try{
			assert Debugger.openNode("Initializing Script Environment");
			// Internal variables
			ScriptValueType.initialize(this);
			// Faux object templates
			FauxTemplate template=new FauxTemplate_Object(this);
			addType(null,FauxTemplate_Object.OBJECTSTRING,template);
			template=new FauxTemplate_List(this);
			addType(null,FauxTemplate_List.LISTSTRING,template);
			template=new Stylesheet(this);
			addType(null,Stylesheet.STYLESHEETSTRING,template);
			template=new FauxTemplate_Interface(this);
			addType(null,FauxTemplate_Interface.INTERFACESTRING,template);
			template=new FauxTemplate_InterfaceElement(this);
			addType(null,FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING,template);
			template=new FauxTemplate_Label(this);
			addType(null,FauxTemplate_Label.LABELSTRING,template);
			template=new FauxTemplate_Rectangle(this);
			addType(null,FauxTemplate_Rectangle.RECTANGLESTRING,template);
			template=new FauxTemplate_GraphicalElement(this);
			addType(null,FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING,template);
			template=new FauxTemplate_Point(this);
			addType(null,FauxTemplate_Point.POINTSTRING,template);
			template=new FauxTemplate_Line(this);
			addType(null,FauxTemplate_Line.LINESTRING,template);
			template=new FauxTemplate_Panel(this);
			addType(null,FauxTemplate_Panel.PANELSTRING,template);
			template=new FauxTemplate_DiscreteRegion(this);
			addType(null,FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING,template);
			template=new FauxTemplate_Color(this);
			addType(null,FauxTemplate_Color.COLORSTRING,template);
			template=new FauxTemplate_Asset(this);
			addType(null,FauxTemplate_Asset.ASSETSTRING,template);
			template=new FauxTemplate_RiffDali(this);
			addType(null,FauxTemplate_RiffDali.RIFFDALISTRING,template);
			template=new FauxTemplate_Terrestrial(this);
			addType(null,FauxTemplate_Terrestrial.TERRESTRIALSTRING,template);
			template=new FauxTemplate_Scenario(this);
			addType(null,FauxTemplate_Scenario.SCENARIOSTRING,template);
			template=new FauxTemplate_Scheduler(this);
			addType(null,FauxTemplate_Scheduler.SCHEDULERSTRING,template);
			template=new FauxTemplate_SchedulerListener(this);
			addType(null,FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING,template);
			template=new FauxTemplate_MovementEvaluator(this);
			addType(null,FauxTemplate_MovementEvaluator.MOVEMENTEVALUATORSTRING,template);
			template=new FauxTemplate_Path(this);
			addType(null,FauxTemplate_Path.PATHSTRING,template);
			template=new FauxTemplate_Terrain(this);
			addType(null,FauxTemplate_Terrain.TERRAINSTRING,template);
			template=new FauxTemplate_Archetype(this);
			addType(null,FauxTemplate_Archetype.ARCHETYPESTRING,template);
			template=new FauxTemplate_Ace(this);
			addType(null,FauxTemplate_Ace.ACESTRING,template);
			template=new FauxTemplate_ArchetypeTree(this);
			addType(null,FauxTemplate_ArchetypeTree.ARCHETYPETREESTRING,template);
			assert Debugger.closeNode();
		}catch(Exception_Nodeable ex){
			assert Debugger.closeNodeTo("Initializing Script Environment");
			throw new Exception_InternalError("Exception occurred during script initialization: "+ex);
		}
	}
	public void addTimer(javax.swing.Timer timer){m_timers.add(timer);}
	public void stopExecution(){
		for(javax.swing.Timer timer:m_timers){
			timer.stop();
		}
		m_timers.clear();
	}
	public void execute(){
		try{
			assert Debugger.openNode("Executing Script-Environment (Default Run)");
			clearStacks();
			Iterator iter=m_templates.values().iterator();
			while(iter.hasNext()){
				((ScriptTemplate_Abstract)iter.next()).initialize();
			}
			List<ScriptValue_Abstract>params=new LinkedList<ScriptValue_Abstract>();
			if(Debugger.getPriorityExecutingClass()!=null){
				if(getTemplate(Debugger.getPriorityExecutingClass())!=null&&getTemplate(Debugger.getPriorityExecutingClass()).getFunction("main",params)!=null){
					ScriptExecutable_CallFunction.callFunction(this,null,getTemplate(Debugger.getPriorityExecutingClass()),"main",params);
					return;
				}
			}
			ScriptFunction_Abstract function;
			List<String>templateNames=new LinkedList<String>();
			iter=m_templates.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry=(Map.Entry)iter.next();
				if((function=((ScriptTemplate_Abstract)entry.getValue()).getFunction("main",params))!=null){
					templateNames.add((String)entry.getKey());
				}
			}
			if(templateNames.size()==0){assert Debugger.closeNode();JOptionPane.showMessageDialog(null,"No classes compiled are executable.","No Executable Class",JOptionPane.WARNING_MESSAGE);return;}
			Object selection;
			if(templateNames.size()>1){
				selection=JOptionPane.showInputDialog(null,"Select the appropriate class to run from","Multiple Executable Classes",JOptionPane.QUESTION_MESSAGE,null,templateNames.toArray(),templateNames.get(0));
			}else{
				selection=templateNames.get(0);
			}
			if(selection==null){assert Debugger.closeNode();return;}
			Debugger.setPriorityExecutingClass((String)selection);
			assert Debugger.addNode(this);
			ScriptExecutable_CallFunction.callFunction(this,null,getTemplate((String)selection),"main",params);
			assert Debugger.ensureCurrentNode("Executing Script-Environment (Default Run)");
			assert Debugger.closeNode();
		}catch(Exception_Nodeable ex){
			Debugger.printException(ex);
			assert Debugger.closeNodeTo("Executing Script-Environment (Default Run)");
		}catch(Exception_InternalError ex){
			Debugger.printException(ex);
			assert Debugger.closeNodeTo("Executing Script-Environment (Default Run)");
		}
	}
	// Stack functions
	public void advanceStack(ScriptTemplate_Abstract template,ScriptFunction_Abstract fxn)throws Exception_Nodeable{
		if(m_threads.get(Thread.currentThread().getName())==null){
			m_threads.put(Thread.currentThread().getName(),new ThreadStack());
		}
		m_threads.get(Thread.currentThread().getName()).advanceStack(template,fxn);
	}
	public void retreatStack(){
		m_threads.get(Thread.currentThread().getName()).retreatStack();
	}
	public void advanceNestedStack(){
		m_threads.get(Thread.currentThread().getName()).advanceNestedStack();
	}
	public void retreatNestedStack(){
		m_threads.get(Thread.currentThread().getName()).retreatNestedStack();
	}
	public ScriptFunction_Abstract getCurrentFunction(){
		return m_threads.get(Thread.currentThread().getName()).getCurrentFunction();
	}
	public ScriptTemplate_Abstract getCurrentObject(){
		return m_threads.get(Thread.currentThread().getName()).getCurrentObject();
	}
	public ScriptValue_Variable getVariableFromStack(String name){
		return m_threads.get(Thread.currentThread().getName()).getVariableFromStack(name);
	}
	public void addVariableToStack(String name,ScriptValue_Variable var)throws Exception_Nodeable{
		m_threads.get(Thread.currentThread().getName()).addVariable(name,var);
	}
	// Variable functions
	public ScriptValue_Variable retrieveVariable(String name)throws Exception_Nodeable{
		assert Debugger.openNode("Variable Retrievals","Retrieving Variable ("+name+")");
		ScriptValue_Variable value=null;
		if(value==null){
			assert Debugger.addSnapNode("Checking current variable stack",m_threads.get(Thread.currentThread().getName()));
			value=getVariableFromStack(name);
		}
 		if(value==null){
			assert Debugger.openNode("Checking current object for valid variable");
			assert Debugger.addNode(getCurrentObject());
			value=getCurrentObject().getVariable(name);
			assert Debugger.closeNode();
		}
		if(value==null){
			assert Debugger.openNode("Checking static template stack");
			assert Debugger.addNode(getTemplate(name));
			if(getTemplate(name)!=null){value=getTemplate(name).getStaticReference();}
			assert Debugger.closeNode();
		}
		if(value==null){assert Debugger.addNode("Value not found");
		}else{assert Debugger.addSnapNode("Value found",value);}
		assert Debugger.closeNode();
		return value;
	}
	// Variable-type functions
	public void addType(Referenced ref,String name)throws Exception_Nodeable{addType(ref,name,new ScriptValueType(this));}
	public void addType(Referenced ref,String name,ScriptValueType keyword) throws Exception_Nodeable{
		assert Debugger.addNode("Variable-Type Additions","Adding variable type name to the variable-map ("+name+")");
		if(m_variableTypes.get(name)!=null){throw new Exception_Nodeable_VariableTypeAlreadyDefined(ref,name);}
		m_variableTypes.put(name,keyword);
	}
	public ScriptValueType getType(String name){return m_variableTypes.get(name);}
	public String getName(ScriptValueType keyword){
		assert keyword!=null;
		Iterator iter=m_variableTypes.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			if(keyword.equals(entry.getValue())){return(String)entry.getKey();}
		}
		throw new Exception_InternalError(this,"Name not found for keyword");
	}
	public void addType(Referenced ref,String name,ScriptTemplate_Abstract template)throws Exception_Nodeable{
		addType(ref,name);
		addTemplate(ref,name,template);
	}
	// Template functions
	public void addTemplate(Referenced ref,String name,ScriptTemplate_Abstract template)throws Exception_Nodeable{
		if(m_templates.get(name)!=null){throw new Exception_Nodeable_TemplateAlreadyDefined(ref,name);}
		m_templates.put(name,template);
	}
	public ScriptTemplate_Abstract getTemplate(ScriptValueType code){return getTemplate(getName(code));}
	public ScriptTemplate_Abstract getTemplate(String name){return m_templates.get(name);}
	public boolean isTemplateDefined(String name){return m_templates.get(name)!=null;}
	// Miscellaneous functions
	public boolean nodificate(){
		assert Debugger.openNode("Script Environment");
		assert Debugger.addSnapNode("Templates: " + m_templates.size() + " templates(s)",m_templates);
		assert Debugger.addSnapNode("Thread Stacks",m_threads);
		assert Debugger.closeNode();
		return true;
	}
}
