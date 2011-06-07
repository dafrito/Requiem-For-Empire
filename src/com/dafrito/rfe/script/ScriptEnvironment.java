package com.dafrito.rfe.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.gui.style.Stylesheet;
import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_TemplateAlreadyDefined;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_VariableTypeAlreadyDefined;
import com.dafrito.rfe.script.operations.ScriptExecutable_CallFunction;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.proxies.FauxTemplate;
import com.dafrito.rfe.script.proxies.FauxTemplate_Ace;
import com.dafrito.rfe.script.proxies.FauxTemplate_Archetype;
import com.dafrito.rfe.script.proxies.FauxTemplate_ArchetypeTree;
import com.dafrito.rfe.script.proxies.FauxTemplate_Asset;
import com.dafrito.rfe.script.proxies.FauxTemplate_Color;
import com.dafrito.rfe.script.proxies.FauxTemplate_DiscreteRegion;
import com.dafrito.rfe.script.proxies.FauxTemplate_GraphicalElement;
import com.dafrito.rfe.script.proxies.FauxTemplate_Interface;
import com.dafrito.rfe.script.proxies.FauxTemplate_InterfaceElement;
import com.dafrito.rfe.script.proxies.FauxTemplate_Label;
import com.dafrito.rfe.script.proxies.FauxTemplate_Line;
import com.dafrito.rfe.script.proxies.FauxTemplate_List;
import com.dafrito.rfe.script.proxies.FauxTemplate_MovementEvaluator;
import com.dafrito.rfe.script.proxies.FauxTemplate_Object;
import com.dafrito.rfe.script.proxies.FauxTemplate_Panel;
import com.dafrito.rfe.script.proxies.FauxTemplate_Path;
import com.dafrito.rfe.script.proxies.FauxTemplate_Point;
import com.dafrito.rfe.script.proxies.FauxTemplate_Rectangle;
import com.dafrito.rfe.script.proxies.FauxTemplate_RiffDali;
import com.dafrito.rfe.script.proxies.FauxTemplate_Scenario;
import com.dafrito.rfe.script.proxies.FauxTemplate_Scheduler;
import com.dafrito.rfe.script.proxies.FauxTemplate_SchedulerListener;
import com.dafrito.rfe.script.proxies.FauxTemplate_Terrain;
import com.dafrito.rfe.script.proxies.FauxTemplate_Terrestrial;
import com.dafrito.rfe.script.values.ScriptFunction;
import com.dafrito.rfe.script.values.ScriptTemplate_Abstract;
import com.dafrito.rfe.script.values.ScriptValue;
import com.dafrito.rfe.script.values.ScriptValueType;
import com.dafrito.rfe.script.values.ScriptValue_Variable;

@Inspectable
public class ScriptEnvironment {
	private final Map<String, ScriptValueType> variableTypes = new HashMap<String, ScriptValueType>(); // Map of variable-Types(Variable-type-name, short)
	private final Map<String, ScriptTemplate_Abstract> templates = new HashMap<String, ScriptTemplate_Abstract>(); // Map of object templates(Short,ScriptTemplate)
	private final List<javax.swing.Timer> timers = new LinkedList<javax.swing.Timer>();
	private final ThreadLocal<ThreadStack> threads = new ThreadLocal<ThreadStack>() {
		@Override
		protected ThreadStack initialValue() {
			return new ThreadStack();
		}
	};

	public ScriptEnvironment() {
		this.initialize();
	}

	// Template functions
	public void addTemplate(Referenced ref, String name, ScriptTemplate_Abstract template) throws Exception_Nodeable {
		if (this.templates.get(name) != null) {
			throw new Exception_Nodeable_TemplateAlreadyDefined(ref, name);
		}
		this.templates.put(name, template);
	}

	public void addTimer(javax.swing.Timer timer) {
		this.timers.add(timer);
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
		if (this.variableTypes.get(name) != null) {
			throw new Exception_Nodeable_VariableTypeAlreadyDefined(ref, name);
		}
		this.variableTypes.put(name, keyword);
	}

	public void addVariableToStack(String name, ScriptValue_Variable var) throws Exception_Nodeable {
		this.threads.get().addVariable(name, var);
	}

	public void advanceNestedStack() {
		this.threads.get().advanceNestedStack();
	}

	// Stack functions
	public void advanceStack(ScriptTemplate_Abstract template, ScriptFunction fxn) throws Exception_Nodeable {
		if (fxn == null) {
			throw new NullPointerException("fxn must not be null");
		}
		this.threads.get().advanceStack(template, fxn);
	}

	public void clearStacks() {
		this.threads.set(new ThreadStack());
	}

	public void execute() {
		assert Debugger.openNode("Executing Script-Environment (Default Run)");
		try {
			this.clearStacks();
			for (ScriptTemplate_Abstract template : this.templates.values()) {
				template.initialize();
			}
			List<ScriptValue> params = Collections.emptyList();
			if (Debugger.getDebugger().getPriorityExecutingClass() != null) {
				if (this.getTemplate(Debugger.getDebugger().getPriorityExecutingClass()) != null && this.getTemplate(Debugger.getDebugger().getPriorityExecutingClass()).getFunction("main", params) != null) {
					ScriptExecutable_CallFunction.callFunction(this, null, this.getTemplate(Debugger.getDebugger().getPriorityExecutingClass()), "main", params);
					return;
				}
			}
			List<String> templateNames = new ArrayList<String>();
			for (Map.Entry<String, ScriptTemplate_Abstract> entry : this.templates.entrySet()) {
				if (entry.getValue().getFunction("main", params) != null) {
					templateNames.add(entry.getKey());
				}
			}
			if (templateNames.isEmpty()) {
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
				return;
			}
			Debugger.getDebugger().setPriorityExecutingClass((String) selection);
			assert Debugger.addNode(this);
			ScriptExecutable_CallFunction.callFunction(this, null, this.getTemplate((String) selection), "main", params);
		} catch (Exception_Nodeable ex) {
			Debugger.printException(ex);
		} catch (Exception_InternalError ex) {
			Debugger.printException(ex);
		} finally {
			assert Debugger.closeNode();
		}
	}

	public ScriptFunction getCurrentFunction() {
		return this.threads.get().getCurrentFunction();
	}

	public ScriptTemplate_Abstract getCurrentObject() {
		return this.threads.get().getCurrentObject();
	}

	public String getName(ScriptValueType keyword) {
		assert keyword != null;
		for (Map.Entry<String, ScriptValueType> entry : this.variableTypes.entrySet()) {
			if (keyword.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		throw new Exception_InternalError(this, "Name not found for keyword");
	}

	public ScriptTemplate_Abstract getTemplate(ScriptValueType code) {
		return this.getTemplate(this.getName(code));
	}

	@Inspectable
	public Map<String, ScriptTemplate_Abstract> getTemplates() {
		return Collections.unmodifiableMap(this.templates);
	}

	public ScriptTemplate_Abstract getTemplate(String name) {
		return this.templates.get(name);
	}

	public ScriptValueType getType(String name) {
		return this.variableTypes.get(name);
	}

	public ScriptValue_Variable getVariableFromStack(String name) {
		return this.threads.get().getVariableFromStack(name);
	}

	public void initialize() {
		assert Debugger.openNode("Initializing Script Environment");
		try {
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
		} catch (Exception_Nodeable ex) {
			throw new Exception_InternalError("Exception occurred during script initialization: " + ex);
		} finally {
			assert Debugger.closeNode();
		}
	}

	public boolean isTemplateDefined(String name) {
		return this.templates.get(name) != null;
	}

	public void reset() {
		assert Debugger.openNode("Resetting Environment");
		this.variableTypes.clear();
		this.templates.clear();
		this.clearStacks();
		System.gc();
		this.initialize();
		assert Debugger.closeNode();
	}

	public void retreatNestedStack() {
		this.threads.get().retreatNestedStack();
	}

	public void retreatStack() {
		this.threads.get().retreatStack();
	}

	// Variable functions
	public ScriptValue_Variable retrieveVariable(String name) throws Exception_Nodeable {
		assert Debugger.openNode("Variable Retrievals", "Retrieving Variable (" + name + ")");
		ScriptValue_Variable value = null;
		if (value == null) {
			assert Debugger.addSnapNode("Checking current variable stack", this.threads.get());
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
		for (javax.swing.Timer timer : this.timers) {
			timer.stop();
		}
		this.timers.clear();
	}
}
