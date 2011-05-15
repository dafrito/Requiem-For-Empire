import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JOptionPane;

public class ScriptEnvironment implements Nodeable {
	private Map<String, ScriptValueType> m_variableTypes = new HashMap<String, ScriptValueType>(); // Map of variable-Types(Variable-type-name, short)
	private Map<String, ScriptTemplate_Abstract> m_templates = new HashMap<String, ScriptTemplate_Abstract>(); // Map of object templates(Short,ScriptTemplate)
	private List<javax.swing.Timer> m_timers = new LinkedList<javax.swing.Timer>();
	private Map<String, ThreadStack> m_threads = new HashMap<String, ThreadStack>();

	public ScriptEnvironment() {
		this.initialize();
	}

	// Template functions
	public void addTemplate(Referenced ref, String name, ScriptTemplate_Abstract template) throws Exception_Nodeable {
		if (this.m_templates.get(name) != null) {
			throw new Exception_Nodeable_TemplateAlreadyDefined(ref, name);
		}
		this.m_templates.put(name, template);
	}

	public void addTimer(javax.swing.Timer timer) {
		this.m_timers.add(timer);
	}

	// Variable-type functions
	public void addType(Referenced ref, String name) throws Exception_Nodeable {
		this.addType(ref, name, new ScriptValueType(this));
	}

	public void addType(Referenced ref, String name, ScriptTemplate_Abstract template) throws Exception_Nodeable {
		this.addType(ref, name);
		this.addTemplate(ref, name, template);
	}

	public void addType(Referenced ref, String name, ScriptValueType keyword) throws Exception_Nodeable {
		assert Debugger.addNode("Variable-Type Additions", "Adding variable type name to the variable-map (" + name + ")");
		if (this.m_variableTypes.get(name) != null) {
			throw new Exception_Nodeable_VariableTypeAlreadyDefined(ref, name);
		}
		this.m_variableTypes.put(name, keyword);
	}

	public void addVariableToStack(String name, ScriptValue_Variable var) throws Exception_Nodeable {
		this.m_threads.get(Thread.currentThread().getName()).addVariable(name, var);
	}

	public void advanceNestedStack() {
		this.m_threads.get(Thread.currentThread().getName()).advanceNestedStack();
	}

	// Stack functions
	public void advanceStack(ScriptTemplate_Abstract template, ScriptFunction_Abstract fxn) throws Exception_Nodeable {
		if (this.m_threads.get(Thread.currentThread().getName()) == null) {
			this.m_threads.put(Thread.currentThread().getName(), new ThreadStack());
		}
		this.m_threads.get(Thread.currentThread().getName()).advanceStack(template, fxn);
	}

	public void clearStacks() {
		this.m_threads.put(Thread.currentThread().getName(), new ThreadStack());
	}

	public void execute() {
		try {
			assert Debugger.openNode("Executing Script-Environment (Default Run)");
			this.clearStacks();
			Iterator iter = this.m_templates.values().iterator();
			while (iter.hasNext()) {
				((ScriptTemplate_Abstract) iter.next()).initialize();
			}
			List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
			if (Debugger.getPriorityExecutingClass() != null) {
				if (this.getTemplate(Debugger.getPriorityExecutingClass()) != null && this.getTemplate(Debugger.getPriorityExecutingClass()).getFunction("main", params) != null) {
					ScriptExecutable_CallFunction.callFunction(this, null, this.getTemplate(Debugger.getPriorityExecutingClass()), "main", params);
					return;
				}
			}
			ScriptFunction_Abstract function;
			List<String> templateNames = new LinkedList<String>();
			iter = this.m_templates.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				if ((function = ((ScriptTemplate_Abstract) entry.getValue()).getFunction("main", params)) != null) {
					templateNames.add((String) entry.getKey());
				}
			}
			if (templateNames.size() == 0) {
				assert Debugger.closeNode();
				JOptionPane.showMessageDialog(null, "No classes compiled are executable.", "No Executable Class", JOptionPane.WARNING_MESSAGE);
				return;
			}
			Object selection;
			if (templateNames.size() > 1) {
				selection = JOptionPane.showInputDialog(null, "Select the appropriate class to run from", "Multiple Executable Classes", JOptionPane.QUESTION_MESSAGE, null, templateNames.toArray(), templateNames.get(0));
			} else {
				selection = templateNames.get(0);
			}
			if (selection == null) {
				assert Debugger.closeNode();
				return;
			}
			Debugger.setPriorityExecutingClass((String) selection);
			assert Debugger.addNode(this);
			ScriptExecutable_CallFunction.callFunction(this, null, this.getTemplate((String) selection), "main", params);
			assert Debugger.ensureCurrentNode("Executing Script-Environment (Default Run)");
			assert Debugger.closeNode();
		} catch (Exception_Nodeable ex) {
			Debugger.printException(ex);
			assert Debugger.closeNodeTo("Executing Script-Environment (Default Run)");
		} catch (Exception_InternalError ex) {
			Debugger.printException(ex);
			assert Debugger.closeNodeTo("Executing Script-Environment (Default Run)");
		}
	}

	public ScriptFunction_Abstract getCurrentFunction() {
		return this.m_threads.get(Thread.currentThread().getName()).getCurrentFunction();
	}

	public ScriptTemplate_Abstract getCurrentObject() {
		return this.m_threads.get(Thread.currentThread().getName()).getCurrentObject();
	}

	public String getName(ScriptValueType keyword) {
		assert keyword != null;
		Iterator iter = this.m_variableTypes.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (keyword.equals(entry.getValue())) {
				return (String) entry.getKey();
			}
		}
		throw new Exception_InternalError(this, "Name not found for keyword");
	}

	public ScriptTemplate_Abstract getTemplate(ScriptValueType code) {
		return this.getTemplate(this.getName(code));
	}

	public ScriptTemplate_Abstract getTemplate(String name) {
		return this.m_templates.get(name);
	}

	public ScriptValueType getType(String name) {
		return this.m_variableTypes.get(name);
	}

	public ScriptValue_Variable getVariableFromStack(String name) {
		return this.m_threads.get(Thread.currentThread().getName()).getVariableFromStack(name);
	}

	public void initialize() {
		try {
			assert Debugger.openNode("Initializing Script Environment");
			// Internal variables
			ScriptValueType.initialize(this);
			// Faux object templates
			FauxTemplate template = new FauxTemplate_Object(this);
			this.addType(null, FauxTemplate_Object.OBJECTSTRING, template);
			template = new FauxTemplate_List(this);
			this.addType(null, FauxTemplate_List.LISTSTRING, template);
			template = new Stylesheet(this);
			this.addType(null, Stylesheet.STYLESHEETSTRING, template);
			template = new FauxTemplate_Interface(this);
			this.addType(null, FauxTemplate_Interface.INTERFACESTRING, template);
			template = new FauxTemplate_InterfaceElement(this);
			this.addType(null, FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING, template);
			template = new FauxTemplate_Label(this);
			this.addType(null, FauxTemplate_Label.LABELSTRING, template);
			template = new FauxTemplate_Rectangle(this);
			this.addType(null, FauxTemplate_Rectangle.RECTANGLESTRING, template);
			template = new FauxTemplate_GraphicalElement(this);
			this.addType(null, FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING, template);
			template = new FauxTemplate_Point(this);
			this.addType(null, FauxTemplate_Point.POINTSTRING, template);
			template = new FauxTemplate_Line(this);
			this.addType(null, FauxTemplate_Line.LINESTRING, template);
			template = new FauxTemplate_Panel(this);
			this.addType(null, FauxTemplate_Panel.PANELSTRING, template);
			template = new FauxTemplate_DiscreteRegion(this);
			this.addType(null, FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING, template);
			template = new FauxTemplate_Color(this);
			this.addType(null, FauxTemplate_Color.COLORSTRING, template);
			template = new FauxTemplate_Asset(this);
			this.addType(null, FauxTemplate_Asset.ASSETSTRING, template);
			template = new FauxTemplate_RiffDali(this);
			this.addType(null, FauxTemplate_RiffDali.RIFFDALISTRING, template);
			template = new FauxTemplate_Terrestrial(this);
			this.addType(null, FauxTemplate_Terrestrial.TERRESTRIALSTRING, template);
			template = new FauxTemplate_Scenario(this);
			this.addType(null, FauxTemplate_Scenario.SCENARIOSTRING, template);
			template = new FauxTemplate_Scheduler(this);
			this.addType(null, FauxTemplate_Scheduler.SCHEDULERSTRING, template);
			template = new FauxTemplate_SchedulerListener(this);
			this.addType(null, FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING, template);
			template = new FauxTemplate_MovementEvaluator(this);
			this.addType(null, FauxTemplate_MovementEvaluator.MOVEMENTEVALUATORSTRING, template);
			template = new FauxTemplate_Path(this);
			this.addType(null, FauxTemplate_Path.PATHSTRING, template);
			template = new FauxTemplate_Terrain(this);
			this.addType(null, FauxTemplate_Terrain.TERRAINSTRING, template);
			template = new FauxTemplate_Archetype(this);
			this.addType(null, FauxTemplate_Archetype.ARCHETYPESTRING, template);
			template = new FauxTemplate_Ace(this);
			this.addType(null, FauxTemplate_Ace.ACESTRING, template);
			template = new FauxTemplate_ArchetypeTree(this);
			this.addType(null, FauxTemplate_ArchetypeTree.ARCHETYPETREESTRING, template);
			assert Debugger.closeNode();
		} catch (Exception_Nodeable ex) {
			assert Debugger.closeNodeTo("Initializing Script Environment");
			throw new Exception_InternalError("Exception occurred during script initialization: " + ex);
		}
	}

	public boolean isTemplateDefined(String name) {
		return this.m_templates.get(name) != null;
	}

	// Miscellaneous functions
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Script Environment");
		assert Debugger.addSnapNode("Templates: " + this.m_templates.size() + " templates(s)", this.m_templates);
		assert Debugger.addSnapNode("Thread Stacks", this.m_threads);
		assert Debugger.closeNode();
		return true;
	}

	public void reset() {
		assert Debugger.openNode("Resetting Environment");
		this.m_variableTypes.clear();
		this.m_templates.clear();
		this.clearStacks();
		System.gc();
		this.initialize();
		assert Debugger.closeNode();
	}

	public void retreatNestedStack() {
		this.m_threads.get(Thread.currentThread().getName()).retreatNestedStack();
	}

	public void retreatStack() {
		this.m_threads.get(Thread.currentThread().getName()).retreatStack();
	}

	// Variable functions
	public ScriptValue_Variable retrieveVariable(String name) throws Exception_Nodeable {
		assert Debugger.openNode("Variable Retrievals", "Retrieving Variable (" + name + ")");
		ScriptValue_Variable value = null;
		if (value == null) {
			assert Debugger.addSnapNode("Checking current variable stack", this.m_threads.get(Thread.currentThread().getName()));
			value = this.getVariableFromStack(name);
		}
		if (value == null) {
			assert Debugger.openNode("Checking current object for valid variable");
			assert Debugger.addNode(this.getCurrentObject());
			value = this.getCurrentObject().getVariable(name);
			assert Debugger.closeNode();
		}
		if (value == null) {
			assert Debugger.openNode("Checking static template stack");
			assert Debugger.addNode(this.getTemplate(name));
			if (this.getTemplate(name) != null) {
				value = this.getTemplate(name).getStaticReference();
			}
			assert Debugger.closeNode();
		}
		if (value == null) {
			assert Debugger.addNode("Value not found");
		} else {
			assert Debugger.addSnapNode("Value found", value);
		}
		assert Debugger.closeNode();
		return value;
	}

	public void stopExecution() {
		for (javax.swing.Timer timer : this.m_timers) {
			timer.stop();
		}
		this.m_timers.clear();
	}
}

class ThreadStack implements Nodeable {
	private VariableTable m_variableTable = new VariableTable();
	private Stack<ScriptTemplate_Abstract> m_objectStack = new Stack<ScriptTemplate_Abstract>(); // Stack of called objects
	private Stack<ScriptFunction_Abstract> m_functionStack = new Stack<ScriptFunction_Abstract>(); // Stack of called functions

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		if (variable == null) {
			assert Debugger.openNode("Undefined Variable Stack Additions", "Adding Undefined Variable to the Stack (" + name + ")");
		} else {
			assert Debugger.openNode("Variable Stack Additions", "Adding Variable to the Stack (" + name + ")");
			assert Debugger.addNode(variable);
		}
		assert Debugger.addNode(this);
		this.m_variableTable.addVariable(name, variable);
		assert Debugger.closeNode();
	}

	public synchronized void advanceNestedStack() {
		this.m_variableTable.advanceNestedStack();
	}

	public synchronized void advanceStack(ScriptTemplate_Abstract template, ScriptFunction_Abstract fxn) throws Exception_Nodeable {
		assert Debugger.openNode("Stack Advancements and Retreats", "Advancing Stack (Stack size before advance: " + this.m_functionStack.size() + ")");
		assert (this.m_functionStack.size() == this.m_objectStack.size()) && (this.m_objectStack.size() == this.m_variableTable.getStacks().size()) : "Stacks unequal: Function-stack: " + this.m_functionStack.size() + " Object-stack: " + this.m_objectStack.size() + " Variable-stack: " + this.m_variableTable.getStacks().size();
		if (template != null) {
			assert Debugger.addSnapNode("Advancing object", template);
		}
		assert Debugger.addSnapNode("Advancing function", fxn);
		if (template == null) {
			template = this.getCurrentObject();
		}
		if (template != null) {
			this.m_objectStack.push((ScriptTemplate_Abstract) template.getValue());
		} else {
			this.m_objectStack.push(null);
		}
		this.m_functionStack.push(fxn);
		this.m_variableTable.advanceStack();
		assert Debugger.closeNode();
	}

	public synchronized ScriptFunction_Abstract getCurrentFunction() {
		if (this.m_functionStack.size() == 0) {
			throw new Exception_InternalError("No call stack");
		}
		return this.m_functionStack.peek();
	}

	public synchronized ScriptTemplate_Abstract getCurrentObject() {
		if (this.m_objectStack.size() == 0) {
			throw new Exception_InternalError("No call stack");
		}
		return this.m_objectStack.peek();
	}

	public ScriptValue_Variable getVariableFromStack(String name) {
		return this.m_variableTable.getVariableFromStack(name);
	}

	@Override
	public synchronized boolean nodificate() {
		assert Debugger.openNode("Thread Stack");
		assert Debugger.addNode(this.m_variableTable);
		assert Debugger.addSnapNode("Object stack (" + this.m_objectStack.size() + ")", this.m_objectStack);
		assert Debugger.addSnapNode("Function stack (" + this.m_functionStack.size() + ")", this.m_functionStack);
		assert Debugger.closeNode();
		return true;
	}

	public synchronized void retreatNestedStack() {
		this.m_variableTable.retreatNestedStack();
	}

	public synchronized void retreatStack() {
		assert Debugger.openNode("Stack Advancements and Retreats", "Retreating Stack (Stack size before retreat: " + this.m_functionStack.size() + ")");
		assert (this.m_functionStack.size() == this.m_objectStack.size()) && (this.m_objectStack.size() == this.m_variableTable.getStacks().size()) : "Stacks unequal: Function-stack: " + this.m_functionStack.size() + " Object-stack: " + this.m_objectStack.size() + " Variable-stack: " + this.m_variableTable.getStacks().size();
		if (this.m_variableTable.getStacks().size() > 0) {
			this.m_variableTable.retreatStack();
		}
		if (this.m_objectStack.size() > 0) {
			this.m_objectStack.pop();
			if (this.m_objectStack.size() > 0) {
				assert Debugger.addSnapNode("New Current Object", this.m_objectStack.peek());
			}
		}
		if (this.m_functionStack.size() > 0) {
			this.m_functionStack.pop();
			if (this.m_functionStack.size() > 0) {
				assert Debugger.addSnapNode("New Current Function", this.m_functionStack.peek());
			}
		}
		assert Debugger.closeNode();
	}
}

class VariableStack implements Nodeable {
	private Stack<Map<String, ScriptValue_Variable>> m_nestedStacks = new Stack<Map<String, ScriptValue_Variable>>();

	public VariableStack() {
		this.m_nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
	}

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		this.m_nestedStacks.peek().put(name, variable);
	}

	public synchronized void advanceNestedStack() {
		assert Debugger.openNode("Stack Advancements and Retreats", "Advancing Nested Stack (Nested stack size before advance: " + this.m_nestedStacks.size() + ")");
		assert Debugger.addNode(this);
		this.m_nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
		assert Debugger.closeNode();
	}

	public synchronized ScriptValue_Variable getVariableFromStack(String name) {
		for (Map<String, ScriptValue_Variable> map : this.m_nestedStacks) {
			if (map.containsKey(name)) {
				return map.get(name);
			}
		}
		return null;
	}

	@Override
	public synchronized boolean nodificate() {
		assert Debugger.openNode("Variable Stack");
		assert Debugger.openNode("Nested Stacks (" + this.m_nestedStacks.size() + " stack(s))");
		for (Map<String, ScriptValue_Variable> map : this.m_nestedStacks) {
			assert Debugger.openNode("Stack (" + map.size() + " variable(s))");
			assert Debugger.addNode(map);
			assert Debugger.closeNode();
		}
		assert Debugger.closeNode();
		assert Debugger.closeNode();
		return true;
	}

	public synchronized void retreatNestedStack() {
		assert this.m_nestedStacks.size() > 0;
		assert Debugger.openNode("Stack Advancements and Retreats", "Retreating Nested Stack (Nested stack size before retreat: " + this.m_nestedStacks.size() + ")");
		assert Debugger.addNode(this);
		this.m_nestedStacks.pop();
		assert Debugger.closeNode();
	}
}

class VariableTable implements Nodeable {
	Stack<VariableStack> m_stacks = new Stack<VariableStack>();

	public synchronized void addVariable(String name, ScriptValue_Variable variable) {
		this.m_stacks.peek().addVariable(name, variable);
	}

	public synchronized void advanceNestedStack() {
		this.m_stacks.peek().advanceNestedStack();
	}

	public synchronized void advanceStack() {
		this.m_stacks.push(new VariableStack());
	}

	public Stack<VariableStack> getStacks() {
		return this.m_stacks;
	}

	public ScriptValue_Variable getVariableFromStack(String name) {
		ScriptValue_Variable variable = this.m_stacks.peek().getVariableFromStack(name);
		return variable;
	}

	@Override
	public synchronized boolean nodificate() {
		assert Debugger.openNode("Variable Table");
		assert Debugger.addSnapNode("Variable Stacks (" + this.m_stacks.size() + " stack(s))", this.m_stacks);
		assert Debugger.closeNode();
		return true;
	}

	public synchronized void retreatNestedStack() {
		this.m_stacks.peek().retreatNestedStack();
	}

	public synchronized void retreatStack() {
		this.m_stacks.pop();
	}
}
