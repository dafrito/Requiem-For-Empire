package com.dafrito.script;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JOptionPane;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_TemplateAlreadyDefined;
import com.dafrito.debug.Exceptions.Exception_Nodeable_VariableTypeAlreadyDefined;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.script.executable.ScriptExecutable_CallFunction;
import com.dafrito.script.templates.FauxTemplate;
import com.dafrito.script.templates.FauxTemplate_Ace;
import com.dafrito.script.templates.FauxTemplate_Archetype;
import com.dafrito.script.templates.FauxTemplate_ArchetypeTree;
import com.dafrito.script.templates.FauxTemplate_Asset;
import com.dafrito.script.templates.FauxTemplate_Color;
import com.dafrito.script.templates.FauxTemplate_DiscreteRegion;
import com.dafrito.script.templates.FauxTemplate_GraphicalElement;
import com.dafrito.script.templates.FauxTemplate_Interface;
import com.dafrito.script.templates.FauxTemplate_InterfaceElement;
import com.dafrito.script.templates.FauxTemplate_Label;
import com.dafrito.script.templates.FauxTemplate_Line;
import com.dafrito.script.templates.FauxTemplate_List;
import com.dafrito.script.templates.FauxTemplate_MovementEvaluator;
import com.dafrito.script.templates.FauxTemplate_Object;
import com.dafrito.script.templates.FauxTemplate_Panel;
import com.dafrito.script.templates.FauxTemplate_Path;
import com.dafrito.script.templates.FauxTemplate_Point;
import com.dafrito.script.templates.FauxTemplate_Rectangle;
import com.dafrito.script.templates.FauxTemplate_RiffDali;
import com.dafrito.script.templates.FauxTemplate_Scenario;
import com.dafrito.script.templates.FauxTemplate_Scheduler;
import com.dafrito.script.templates.FauxTemplate_SchedulerListener;
import com.dafrito.script.templates.FauxTemplate_Terrain;
import com.dafrito.script.templates.FauxTemplate_Terrestrial;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Variable;

class ThreadStack implements Nodeable {

    private VariableTable variableTable = new VariableTable();
    private Stack<ScriptTemplate_Abstract> objectStack = new Stack<ScriptTemplate_Abstract>(); // Stack
    // of
    // called
    // objects
    private Stack<ScriptFunction_Abstract> functionStack = new Stack<ScriptFunction_Abstract>(); // Stack

    // of
    // called
    // functions

    public ScriptValue_Variable getVariableFromStack(String name) {
        return this.variableTable.getVariableFromStack(name);
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Thread Stack");
        assert LegacyDebugger.addNode(this.variableTable);
        assert LegacyDebugger.addSnapNode("Object stack (" + this.objectStack.size() + ")", this.objectStack);
        assert LegacyDebugger.addSnapNode("Function stack (" + this.functionStack.size() + ")", this.functionStack);
        assert LegacyDebugger.close();
        return true;
    }

    public synchronized void addVariable(String name, ScriptValue_Variable variable) {
        if(variable == null) {
            assert LegacyDebugger.open("Undefined Variable Stack Additions", "Adding Undefined Variable to the Stack ("
                + name
                + ")");
        } else {
            assert LegacyDebugger.open("Variable Stack Additions", "Adding Variable to the Stack (" + name + ")");
            assert LegacyDebugger.addNode(variable);
        }
        assert LegacyDebugger.addNode(this);
        this.variableTable.addVariable(name, variable);
        assert LegacyDebugger.close();
    }

    public synchronized ScriptTemplate_Abstract getCurrentObject() {
        if(this.objectStack.size() == 0) {
            throw new Exception_InternalError("No call stack");
        }
        return this.objectStack.peek();
    }

    public synchronized ScriptFunction_Abstract getCurrentFunction() {
        if(this.functionStack.size() == 0) {
            throw new Exception_InternalError("No call stack");
        }
        return this.functionStack.peek();
    }

    public synchronized void advanceNestedStack() {
        this.variableTable.advanceNestedStack();
    }

    public synchronized void retreatNestedStack() {
        this.variableTable.retreatNestedStack();
    }

    public synchronized void advanceStack(ScriptTemplate_Abstract template, ScriptFunction_Abstract fxn)
            throws Exception_Nodeable {
        assert LegacyDebugger.open("Stack Advancements and Retreats", "Advancing Stack (Stack size before advance: "
            + this.functionStack.size()
            + ")");
        assert (this.functionStack.size() == this.objectStack.size())
            && (this.objectStack.size() == this.variableTable.getStacks().size()) : "Stacks unequal: Function-stack: "
            + this.functionStack.size()
            + " Object-stack: "
            + this.objectStack.size()
            + " Variable-stack: "
            + this.variableTable.getStacks().size();
        if(template != null) {
            assert LegacyDebugger.addSnapNode("Advancing object", template);
        }
        assert LegacyDebugger.addSnapNode("Advancing function", fxn);
        ScriptTemplate_Abstract currentTemplate = template;
        if(currentTemplate == null) {
            currentTemplate = this.getCurrentObject();
        }
        if(currentTemplate != null) {
            this.objectStack.push((ScriptTemplate_Abstract)currentTemplate.getValue());
        } else {
            this.objectStack.push(null);
        }
        this.functionStack.push(fxn);
        this.variableTable.advanceStack();
        assert LegacyDebugger.close();
    }

    public synchronized void retreatStack() {
        assert LegacyDebugger.open("Stack Advancements and Retreats", "Retreating Stack (Stack size before retreat: "
            + this.functionStack.size()
            + ")");
        assert (this.functionStack.size() == this.objectStack.size())
            && (this.objectStack.size() == this.variableTable.getStacks().size()) : "Stacks unequal: Function-stack: "
            + this.functionStack.size()
            + " Object-stack: "
            + this.objectStack.size()
            + " Variable-stack: "
            + this.variableTable.getStacks().size();
        if(this.variableTable.getStacks().size() > 0) {
            this.variableTable.retreatStack();
        }
        if(this.objectStack.size() > 0) {
            this.objectStack.pop();
            if(this.objectStack.size() > 0) {
                assert LegacyDebugger.addSnapNode("New Current Object", this.objectStack.peek());
            }
        }
        if(this.functionStack.size() > 0) {
            this.functionStack.pop();
            if(this.functionStack.size() > 0) {
                assert LegacyDebugger.addSnapNode("New Current Function", this.functionStack.peek());
            }
        }
        assert LegacyDebugger.close();
    }
}

class VariableTable implements Nodeable {

    Stack<VariableStack> stacks = new Stack<VariableStack>();

    public ScriptValue_Variable getVariableFromStack(String name) {
        ScriptValue_Variable variable = this.stacks.peek().getVariableFromStack(name);
        return variable;
    }

    public synchronized void addVariable(String name, ScriptValue_Variable variable) {
        this.stacks.peek().addVariable(name, variable);
    }

    public Stack<VariableStack> getStacks() {
        return this.stacks;
    }

    public synchronized void retreatStack() {
        this.stacks.pop();
    }

    public synchronized void advanceStack() {
        this.stacks.push(new VariableStack());
    }

    public synchronized void advanceNestedStack() {
        this.stacks.peek().advanceNestedStack();
    }

    public synchronized void retreatNestedStack() {
        this.stacks.peek().retreatNestedStack();
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Variable Table");
        assert LegacyDebugger.addSnapNode("Variable Stacks (" + this.stacks.size() + " stack(s))", this.stacks);
        assert LegacyDebugger.close();
        return true;
    }
}

class VariableStack implements Nodeable {

    private Stack<Map<String, ScriptValue_Variable>> nestedStacks = new Stack<Map<String, ScriptValue_Variable>>();

    public VariableStack() {
        this.nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
    }

    public synchronized ScriptValue_Variable getVariableFromStack(String name) {
        for(Map<String, ScriptValue_Variable> map : this.nestedStacks) {
            if(map.containsKey(name)) {
                return map.get(name);
            }
        }
        return null;
    }

    public synchronized void advanceNestedStack() {
        assert LegacyDebugger.open(
            "Stack Advancements and Retreats",
            "Advancing Nested Stack (Nested stack size before advance: " + this.nestedStacks.size() + ")");
        assert LegacyDebugger.addNode(this);
        this.nestedStacks.push(new HashMap<String, ScriptValue_Variable>());
        assert LegacyDebugger.close();
    }

    public synchronized void retreatNestedStack() {
        assert this.nestedStacks.size() > 0;
        assert LegacyDebugger.open(
            "Stack Advancements and Retreats",
            "Retreating Nested Stack (Nested stack size before retreat: " + this.nestedStacks.size() + ")");
        assert LegacyDebugger.addNode(this);
        this.nestedStacks.pop();
        assert LegacyDebugger.close();
    }

    public synchronized void addVariable(String name, ScriptValue_Variable variable) {
        this.nestedStacks.peek().put(name, variable);
    }

    public synchronized boolean nodificate() {
        assert LegacyDebugger.open("Variable Stack");
        assert LegacyDebugger.open("Nested Stacks (" + this.nestedStacks.size() + " stack(s))");
        for(Map<String, ScriptValue_Variable> map : this.nestedStacks) {
            assert LegacyDebugger.open("Stack (" + map.size() + " variable(s))");
            assert LegacyDebugger.addNode(map);
            assert LegacyDebugger.close();
        }
        assert LegacyDebugger.close();
        assert LegacyDebugger.close();
        return true;
    }
}

public class ScriptEnvironment implements Nodeable {

    /**
     * Map of variable-Types(Variable-type-name, short)
     */
    private Map<String, ScriptValueType> variableTypes = new HashMap<String, ScriptValueType>();

    /**
     * Map of object templates(Short,ScriptTemplate)
     */
    private Map<String, ScriptTemplate_Abstract> templates = new HashMap<String, ScriptTemplate_Abstract>();

    private List<javax.swing.Timer> timers = new LinkedList<javax.swing.Timer>();
    private Map<String, ThreadStack> threads = new HashMap<String, ThreadStack>();

    public void reset() {
        assert LegacyDebugger.open("Resetting Environment");
        this.variableTypes.clear();
        this.templates.clear();
        clearStacks();
        initialize();
        assert LegacyDebugger.close();
    }

    public void clearStacks() {
        this.threads.put(Thread.currentThread().getName(), new ThreadStack());
    }

    public ScriptEnvironment() {
        initialize();
    }

    public void initialize() {
        try {
            assert LegacyDebugger.open("Initializing Script Environment");
            // Internal variables
            ScriptValueType.initialize(this);
            // Faux object templates
            FauxTemplate template = new FauxTemplate_Object(this);
            addType(null, FauxTemplate_Object.OBJECTSTRING, template);
            template = new FauxTemplate_List(this);
            addType(null, FauxTemplate_List.LISTSTRING, template);
            template = new Stylesheet(this);
            addType(null, Stylesheet.STYLESHEETSTRING, template);
            template = new FauxTemplate_Interface(this);
            addType(null, FauxTemplate_Interface.INTERFACESTRING, template);
            template = new FauxTemplate_InterfaceElement(this);
            addType(null, FauxTemplate_InterfaceElement.INTERFACEELEMENTSTRING, template);
            template = new FauxTemplate_Label(this);
            addType(null, FauxTemplate_Label.LABELSTRING, template);
            template = new FauxTemplate_Rectangle(this);
            addType(null, FauxTemplate_Rectangle.RECTANGLESTRING, template);
            template = new FauxTemplate_GraphicalElement(this);
            addType(null, FauxTemplate_GraphicalElement.GRAPHICALELEMENTSTRING, template);
            template = new FauxTemplate_Point(this);
            addType(null, FauxTemplate_Point.POINTSTRING, template);
            template = new FauxTemplate_Line(this);
            addType(null, FauxTemplate_Line.LINESTRING, template);
            template = new FauxTemplate_Panel(this);
            addType(null, FauxTemplate_Panel.PANELSTRING, template);
            template = new FauxTemplate_DiscreteRegion(this);
            addType(null, FauxTemplate_DiscreteRegion.DISCRETEREGIONSTRING, template);
            template = new FauxTemplate_Color(this);
            addType(null, FauxTemplate_Color.COLORSTRING, template);
            template = new FauxTemplate_Asset(this);
            addType(null, FauxTemplate_Asset.ASSETSTRING, template);
            template = new FauxTemplate_RiffDali(this);
            addType(null, FauxTemplate_RiffDali.RIFFDALISTRING, template);
            template = new FauxTemplate_Terrestrial(this);
            addType(null, FauxTemplate_Terrestrial.TERRESTRIALSTRING, template);
            template = new FauxTemplate_Scenario(this);
            addType(null, FauxTemplate_Scenario.SCENARIOSTRING, template);
            template = new FauxTemplate_Scheduler(this);
            addType(null, FauxTemplate_Scheduler.SCHEDULERSTRING, template);
            template = new FauxTemplate_SchedulerListener(this);
            addType(null, FauxTemplate_SchedulerListener.SCHEDULERLISTENERSTRING, template);
            template = new FauxTemplate_MovementEvaluator(this);
            addType(null, FauxTemplate_MovementEvaluator.MOVEMENTEVALUATORSTRING, template);
            template = new FauxTemplate_Path(this);
            addType(null, FauxTemplate_Path.PATHSTRING, template);
            template = new FauxTemplate_Terrain(this);
            addType(null, FauxTemplate_Terrain.TERRAINSTRING, template);
            template = new FauxTemplate_Archetype(this);
            addType(null, FauxTemplate_Archetype.ARCHETYPESTRING, template);
            template = new FauxTemplate_Ace(this);
            addType(null, FauxTemplate_Ace.ACESTRING, template);
            template = new FauxTemplate_ArchetypeTree(this);
            addType(null, FauxTemplate_ArchetypeTree.ARCHETYPETREESTRING, template);
            assert LegacyDebugger.close();
        } catch(Exception_Nodeable ex) {
            assert LegacyDebugger.closeNodeTo("Initializing Script Environment");
            throw new Exception_InternalError("Exception occurred during script initialization: " + ex);
        }
    }

    public void addTimer(javax.swing.Timer timer) {
        this.timers.add(timer);
    }

    public void stopExecution() {
        for(javax.swing.Timer timer : this.timers) {
            timer.stop();
        }
        this.timers.clear();
    }
    
    protected String bootstrappingClassName;

    public void execute() {
        try {
            assert LegacyDebugger.open("Executing Script-Environment (Default Run)");
            clearStacks();
            for(ScriptTemplate_Abstract template : this.templates.values()) {
                template.initialize();
            }
            List<ScriptValue_Abstract> params = new LinkedList<ScriptValue_Abstract>();
            if(this.bootstrappingClassName != null) {
                if(getTemplate(this.bootstrappingClassName) != null
                    && getTemplate(this.bootstrappingClassName).getFunction("main", params) != null) {
                    ScriptExecutable_CallFunction.callFunction(
                        this,
                        null,
                        getTemplate(this.bootstrappingClassName),
                        "main",
                        params);
                    return;
                }
            }
            List<String> templateNames = new LinkedList<String>();
            for(Map.Entry<String, ScriptTemplate_Abstract> entry : this.templates.entrySet()) {
                ScriptFunction_Abstract function = entry.getValue().getFunction("main", params);
                if(function != null) {
                    templateNames.add(entry.getKey());
                }
            }
            if(templateNames.isEmpty()) {
                assert LegacyDebugger.close();
                JOptionPane.showMessageDialog(
                    null,
                    "No classes compiled are executable.",
                    "No Executable Class",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object selection;
            if(templateNames.size() > 1) {
                selection = JOptionPane.showInputDialog(
                    null,
                    "Select the appropriate class to run from",
                    "Multiple Executable Classes",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    templateNames.toArray(),
                    templateNames.get(0));
            } else {
                selection = templateNames.get(0);
            }
            if(selection == null) {
                assert LegacyDebugger.close();
                return;
            }
            this.bootstrappingClassName = (String)selection;
            assert LegacyDebugger.addNode(this);
            ScriptExecutable_CallFunction.callFunction(this, null, getTemplate((String)selection), "main", params);
            assert LegacyDebugger.ensureCurrentNode("Executing Script-Environment (Default Run)");
            assert LegacyDebugger.close();
        } catch(Exception_Nodeable ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Executing Script-Environment (Default Run)");
        } catch(Exception_InternalError ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Executing Script-Environment (Default Run)");
        }
    }

    // Stack functions
    public void advanceStack(ScriptTemplate_Abstract template, ScriptFunction_Abstract fxn) throws Exception_Nodeable {
        if(this.threads.get(Thread.currentThread().getName()) == null) {
            this.threads.put(Thread.currentThread().getName(), new ThreadStack());
        }
        this.threads.get(Thread.currentThread().getName()).advanceStack(template, fxn);
    }

    public void retreatStack() {
        this.threads.get(Thread.currentThread().getName()).retreatStack();
    }

    public void advanceNestedStack() {
        this.threads.get(Thread.currentThread().getName()).advanceNestedStack();
    }

    public void retreatNestedStack() {
        this.threads.get(Thread.currentThread().getName()).retreatNestedStack();
    }

    public ScriptFunction_Abstract getCurrentFunction() {
        return this.threads.get(Thread.currentThread().getName()).getCurrentFunction();
    }

    public ScriptTemplate_Abstract getCurrentObject() {
        return this.threads.get(Thread.currentThread().getName()).getCurrentObject();
    }

    public ScriptValue_Variable getVariableFromStack(String name) {
        return this.threads.get(Thread.currentThread().getName()).getVariableFromStack(name);
    }

    public void addVariableToStack(String name, ScriptValue_Variable var) {
        this.threads.get(Thread.currentThread().getName()).addVariable(name, var);
    }

    // Variable functions
    public ScriptValue_Variable retrieveVariable(String name) throws Exception_Nodeable {
        assert LegacyDebugger.open("Variable Retrievals", "Retrieving Variable (" + name + ")");
        ScriptValue_Variable value = null;
        assert LegacyDebugger.addSnapNode(
            "Checking current variable stack",
            this.threads.get(Thread.currentThread().getName()));
        value = getVariableFromStack(name);
        if(value == null) {
            assert LegacyDebugger.open("Checking current object for valid variable");
            assert LegacyDebugger.addNode(getCurrentObject());
            value = getCurrentObject().getVariable(name);
            assert LegacyDebugger.close();
        }
        if(value == null) {
            assert LegacyDebugger.open("Checking static template stack");
            assert LegacyDebugger.addNode(getTemplate(name));
            if(getTemplate(name) != null) {
                value = getTemplate(name).getStaticReference();
            }
            assert LegacyDebugger.close();
        }
        if(value == null) {
            assert LegacyDebugger.addNode("Value not found");
        } else {
            assert LegacyDebugger.addSnapNode("Value found", value);
        }
        assert LegacyDebugger.close();
        return value;
    }

    // Variable-type functions
    public void addType(Referenced ref, String name) throws Exception_Nodeable {
        addType(ref, name, new ScriptValueType(this));
    }

    public void addType(Referenced ref, String name, ScriptValueType keyword) throws Exception_Nodeable {
        assert LegacyDebugger.addNode("Variable-Type Additions", "Adding variable type name to the variable-map ("
            + name
            + ")");
        if(this.variableTypes.get(name) != null) {
            throw new Exception_Nodeable_VariableTypeAlreadyDefined(ref, name);
        }
        this.variableTypes.put(name, keyword);
    }

    public ScriptValueType getType(String name) {
        return this.variableTypes.get(name);
    }

    public String getName(ScriptValueType keyword) {
        assert keyword != null;
        for(Map.Entry<String, ScriptValueType> entry : this.variableTypes.entrySet()) {
            if(keyword.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        throw new Exception_InternalError(this, "Name not found for keyword");
    }

    public void addType(Referenced ref, String name, ScriptTemplate_Abstract template) throws Exception_Nodeable {
        addType(ref, name);
        addTemplate(ref, name, template);
    }

    // Template functions
    public void addTemplate(Referenced ref, String name, ScriptTemplate_Abstract template) throws Exception_Nodeable {
        if(this.templates.get(name) != null) {
            throw new Exception_Nodeable_TemplateAlreadyDefined(ref, name);
        }
        this.templates.put(name, template);
    }

    public ScriptTemplate_Abstract getTemplate(ScriptValueType code) {
        return getTemplate(getName(code));
    }

    public ScriptTemplate_Abstract getTemplate(String name) {
        return this.templates.get(name);
    }

    public boolean isTemplateDefined(String name) {
        return this.templates.get(name) != null;
    }

    // Miscellaneous functions
    public boolean nodificate() {
        assert LegacyDebugger.open("Script Environment");
        assert LegacyDebugger.addSnapNode("Templates: " + this.templates.size() + " templates(s)", this.templates);
        assert LegacyDebugger.addSnapNode("Thread Stacks", this.threads);
        assert LegacyDebugger.close();
        return true;
    }
}
