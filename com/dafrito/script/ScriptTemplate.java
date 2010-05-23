package com.dafrito.script;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_AbstractFunctionNotImplemented;
import com.dafrito.debug.Exceptions.Exception_Nodeable_FunctionAlreadyDefined;
import com.dafrito.debug.Exceptions.Exception_Nodeable_IllegalAbstractObjectCreation;
import com.dafrito.debug.Exceptions.Exception_Nodeable_UnimplementedFunction;
import com.dafrito.debug.Exceptions.Exception_Nodeable_VariableAlreadyDefined;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.executable.ScriptExecutable_AssignValue;
import com.dafrito.script.executable.ScriptExecutable_CreateVariable;
import com.dafrito.script.executable.ScriptExecutable_ParseFunction;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Variable;

public class ScriptTemplate extends ScriptTemplate_Abstract implements ScriptConvertible {
    protected Map<String, List<ScriptFunction_Abstract>> functions;
    protected Map<String, ScriptValue_Variable> variables;
    private boolean isObject, isAbstract;
    private boolean isConstructing, fullCreation;
    private List<ScriptExecutable> preconstructors;
    private List<ScriptExecutable> templatePreconstructors;

    public static ScriptTemplate createTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended,
        List<ScriptValueType> interfaces, boolean isAbstract) {
        return new ScriptTemplate(env, type, extended, interfaces, isAbstract);
    }

    @Override
    public ScriptTemplate instantiateTemplate() {
        return new ScriptTemplate(getEnvironment(), getType());
    }

    protected ScriptTemplate(ScriptEnvironment env, ScriptValueType type) {
        super(env, type);
        this.isObject = true;
        this.fullCreation = true;
        this.isConstructing = true;
        this.variables = new HashMap<String, ScriptValue_Variable>();
    }

    protected ScriptTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended,
        List<ScriptValueType> interfaces, boolean isAbstract) {
        super(env, type, extended, interfaces);
        this.fullCreation = true;
        this.isObject = false;
        this.variables = new HashMap<String, ScriptValue_Variable>();
        this.functions = new HashMap<String, List<ScriptFunction_Abstract>>();
        this.templatePreconstructors = new LinkedList<ScriptExecutable>();
        this.preconstructors = new LinkedList<ScriptExecutable>();
        this.isAbstract = isAbstract;
    }

    // Abstract-template implementation
    @Override
    public boolean isFullCreation() {
        return this.fullCreation;
    }

    @Override
    public void disableFullCreation() {
        this.fullCreation = false;
    }

    @Override
    public boolean isConstructing() throws Exception_Nodeable {
        return this.isConstructing;
    }

    @Override
    public void setConstructing(boolean constructing) throws Exception_Nodeable {
        assert LegacyDebugger.open("Object Construction Settings", "Setting constructing-boolean to: " + constructing);
        this.isConstructing = constructing;
        assert LegacyDebugger.addNode(this);
        assert LegacyDebugger.close();
    }

    @Override
    public boolean isObject() {
        return this.isObject;
    }

    @Override
    public boolean isAbstract() throws Exception_Nodeable {
        if(getEnvironment().getTemplate(getType()) != this) {
            return getEnvironment().getTemplate(getType()).isAbstract();
        }
        return this.isAbstract;
    }

    @Override
    public ScriptValue_Variable getStaticReference() throws Exception_Nodeable {
        return new ScriptValue_Variable(getEnvironment(), getType(), this, ScriptKeywordType.PUBLIC);
    }

    @Override
    public ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract passedObject)
            throws Exception_Nodeable {
        assert LegacyDebugger.open("Object Creations", "Object Creation");
        ScriptTemplate_Abstract object = passedObject;
        if(object == null) {
            if(isAbstract()) {
                throw new Exception_Nodeable_IllegalAbstractObjectCreation(ref);
            }
            object = instantiateTemplate();
        }
        assert LegacyDebugger.addNode(object);
        getEnvironment().advanceStack(object, null);
        if(this.preconstructors.size() > 0) {
            assert LegacyDebugger.open("Calling preconstructor expressions ("
                + this.preconstructors.size()
                + " expression(s))");
            if(!object.isConstructing()) {
                object.disableFullCreation();
            }
            object.setConstructing(true);
            for(ScriptExecutable exec : this.preconstructors) {
                exec.execute();
            }
            assert LegacyDebugger.close();
        }
        object.disableFullCreation();
        if(getExtendedClass() != null) {
            assert LegacyDebugger.open("Now sending to base class preconstructor ("
                + getEnvironment().getName(getExtendedClass().getType())
                + ")");
            object = getExtendedClass().createObject(ref, object);
            assert LegacyDebugger.close();
        }
        if(getInterfaces() != null && getInterfaces().size() > 0) {
            assert LegacyDebugger.open("Calling interface preconstructors (" + getInterfaces().size() + " preconstructor(s))");
            for(ScriptValueType interfaceType : getInterfaces()) {
                object = getEnvironment().getTemplate(interfaceType).createObject(ref, object);
            }
            assert LegacyDebugger.close();
        }
        object.setConstructing(false);
        if(object.getFunctions() != null) {
            for(ScriptFunction_Abstract function : object.getFunctions()) {
                if(function.isAbstract()) {
                    throw new Exception_Nodeable_AbstractFunctionNotImplemented(ref, object, function);
                }
            }
        } else {
            assert LegacyDebugger.addNode(object);
        }
        getEnvironment().retreatStack();
        assert LegacyDebugger.close();
        return object;
    }

    @Override
    public ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value)
            throws Exception_Nodeable {
        if(isConstructing()) {
            assert LegacyDebugger.open("Object Variable Additions", "Adding Variable to Object (" + name + ")");
            assert LegacyDebugger.addNode(this);
            assert LegacyDebugger.addNode(value);
            if(this.variables.get(name) != null) {
                throw new Exception_Nodeable_VariableAlreadyDefined(ref, this, name);
            }
            if(isFullCreation() || !value.getPermission().equals(ScriptKeywordType.PRIVATE)) {
                this.variables.put(name, value);
                assert LegacyDebugger.addSnapNode("Variable successfully added", this);
            } else {
                assert LegacyDebugger.addNode("Variable is private, and our template is not in its full-creation phase.");
            }
            assert LegacyDebugger.close();
            return value;
        }
        assert LegacyDebugger.open("Local Variable Additions", "Adding Variable to Stack (" + name + ")");
        assert LegacyDebugger.addNode(value);
        if(getEnvironment().getVariableFromStack(name) != null) {
            throw new Exception_Nodeable_VariableAlreadyDefined(ref, this, name);
        }
        getEnvironment().addVariableToStack(name, value);
        assert LegacyDebugger.close();
        return value;
    }

    @Override
    public ScriptValue_Variable getVariable(String name) throws Exception_Nodeable {
        assert LegacyDebugger.open("Object Variable Retrievals", "Retrieving Variable From Object ('" + name + "')");
        assert LegacyDebugger.addNode(this);
        ScriptValue_Variable var = this.variables.get(name);
        if(var == null && isObject()) {
            assert LegacyDebugger.open("Variable not a member, so checking static members");
            var = getEnvironment().getTemplate(getType()).getVariable(name);
            assert LegacyDebugger.close();
        }
        if(var != null) {
            if(var.getPermission().equals(ScriptKeywordType.PUBLIC)) {
                assert LegacyDebugger.close("Variable found and permission valid (public)", var);
                return var;
            } else if(var.getPermission().equals(ScriptKeywordType.PRIVATE)) {
                if(getEnvironment().getCurrentObject() == this
                    || (getEnvironment().getTemplate(getType()) == this && getEnvironment().getCurrentObject().getType().equals(
                        getType()))) {
                    assert LegacyDebugger.close("Variable found and permission valid (private)", var);
                    return var;
                }
            }
        }
        assert LegacyDebugger.close("Variable not found or permission is invalid");
        return null;
    }

    @Override
    public void addFunction(Referenced ref, String name, ScriptFunction_Abstract function) throws Exception_Nodeable {
        if(getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
            getEnvironment().getTemplate(getType()).addFunction(ref, name, function);
            return;
        }
        assert LegacyDebugger.open("Object Function Additions", "Adding Function to Object ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        assert LegacyDebugger.addNode(this);
        assert LegacyDebugger.addNode(function);
        if(!isFullCreation() && (name == null || name.equals(""))) {
            assert LegacyDebugger.close("Function is a constructor, and our template is not in its full-creation phase.");
            return;
        }
        if(this.functions.get(name) != null) {
            List<ScriptFunction_Abstract> list = this.functions.get(name);
            if(function.isAbstract()) {
                for(ScriptFunction_Abstract currentFxn : list) {
                    // It's an abstract function and we implement it, so return.
                    if(function.areParametersEqual(currentFxn.getParameters())) {
                        assert LegacyDebugger.close("The template has this abstract function implemented.");
                        return;
                    }
                }
            } else {
                for(int i = 0; i < list.size(); i++) {
                    ScriptFunction_Abstract currentFxn = list.get(i);
                    // We already implement this function, so return.
                    if(function.areParametersEqual(currentFxn.getParameters())) {
                        if(currentFxn instanceof ScriptExecutable_ParseFunction) {
                            throw new Exception_Nodeable_FunctionAlreadyDefined(ref, name);
                        }
                        list.remove(i);
                        list.add(function);
                        assert LegacyDebugger.close("The template overloads an existing function");
                        return;
                    }
                }
                list.add(function);
                assert LegacyDebugger.close("Function was successfully added", this);
                return;
            }
        }
        this.functions.put(name, new LinkedList<ScriptFunction_Abstract>());
        this.functions.get(name).add(function);
        assert LegacyDebugger.close("Function was successfully added", this);
    }

    @Override
    public List<ScriptFunction_Abstract> getFunctions() {
        if(this.functions == null) {
            return null;
        }
        List<ScriptFunction_Abstract> allFunctions = new LinkedList<ScriptFunction_Abstract>();
        for(List<ScriptFunction_Abstract> fxnList : this.functions.values()) {
            allFunctions.addAll(fxnList);
        }
        return allFunctions;
    }
    
    protected ScriptTemplate_Abstract getTemplate() {
        return this.getEnvironment().getTemplate(this.getType());
    }

    @Override
    public ScriptFunction_Abstract getFunction(String name, List<ScriptValue_Abstract> params) {
        if(this.getTemplate() != null && this.getTemplate() != this) {
            return this.getTemplate().getFunction(name, params);
        }
        assert LegacyDebugger.open("Object Function Retrievals", "Retrieving Function from Object ("
            + ScriptFunction.getDisplayableFunctionName(name)
            + ")");
        assert LegacyDebugger.addSnapNode("Current template", this);
        List<ScriptFunction_Abstract> list = this.functions.get(name);
        if(list != null && list.size() > 0) {
            assert LegacyDebugger.addSnapNode("Functions found", list);
            for(ScriptFunction_Abstract function : list) {
                if(function.areParametersConvertible(params)) {
                    assert LegacyDebugger.close("Params match, returning function", function);
                    return function;
                }
            }
        }
        ScriptFunction_Abstract fxn = null;
        if(getExtendedClass() != null) {
            fxn = getExtendedClass().getFunction(name, params);
        }
        if(fxn != null) {
            assert LegacyDebugger.close("Returning function", fxn);
        } else {
            assert LegacyDebugger.close("Function not found");
        }
        return fxn;
    }

    @Override
    public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn) {
        if(getEnvironment().getTemplate(getType()) != null && getEnvironment().getTemplate(getType()) != this) {
            return getEnvironment().getTemplate(getType()).getFunctionTemplate(fxn);
        }
        for(List<ScriptFunction_Abstract> fxns : this.functions.values()) {
            if(fxns.contains(fxn)) {
                return this;
            }
        }
        if(getExtendedClass() != null) {
            return getExtendedClass().getFunctionTemplate(fxn);
        }
        return null;
    }

    @Override
    public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
        assert LegacyDebugger.open("Template Preconstructor Additions", "Adding Template Preconstructor");
        assert LegacyDebugger.addNode("Template", this);
        assert LegacyDebugger.addNode(exec);
        this.templatePreconstructors.add(exec);
        assert LegacyDebugger.close();
    }

    @Override
    public void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
        assert LegacyDebugger.open("Preconstructor Additions", "Adding Preconstructor");
        assert LegacyDebugger.addNode("Template", this);
        assert LegacyDebugger.addNode(exec);
        this.preconstructors.add(exec);
        assert LegacyDebugger.close();
    }

    @Override
    public void initialize() throws Exception_Nodeable {
        this.variables.clear();
        if(this.templatePreconstructors == null || this.templatePreconstructors.size() == 0) {
            return;
        }
        assert LegacyDebugger.open("Template Initializations", "Initializing Template (" + getType() + ")");
        if(!isAbstract()) {
            for(Map.Entry<String, List<ScriptFunction_Abstract>> entry : this.functions.entrySet()) {
                List<ScriptFunction_Abstract> entryFunctions = entry.getValue();
                for(int i = 0; i < entryFunctions.size(); i++) {
                    if(entryFunctions.get(i).isAbstract()) {
                        // We don't implement it, so fail.
                        throw new Exception_Nodeable_UnimplementedFunction(getEnvironment(), this, entry.getKey());
                    }
                }
            }
        }
        getEnvironment().advanceStack(this, null);
        setConstructing(true);
        assert LegacyDebugger.open("Executing preconstructors ("
            + this.templatePreconstructors.size()
            + " preconstructor(s))");
        for(ScriptExecutable exec : this.templatePreconstructors) {
            exec.execute();
        }
        setConstructing(false);
        assert LegacyDebugger.close();
        getEnvironment().retreatStack();
        assert LegacyDebugger.close();
    }

    @Override
    public void initializeFunctions(Referenced ref) throws Exception_Nodeable {
        assert LegacyDebugger.open("Unparsed Member-Function Initialization", "Initializing Unparsed Member Functions ("
            + getType()
            + ")");
        getEnvironment().advanceStack(this, null);
        assert LegacyDebugger.open("Adding static member-variables");
        for(ScriptExecutable exec : this.templatePreconstructors) {
            if(exec instanceof ScriptExecutable_CreateVariable) {
                getEnvironment().addVariableToStack(
                    ((ScriptExecutable_CreateVariable)exec).getName(),
                    (ScriptValue_Variable)exec);
            } else if(exec instanceof ScriptExecutable_AssignValue
                && ((ScriptExecutable_AssignValue)exec).getLeft() instanceof ScriptExecutable_CreateVariable) {
                getEnvironment().addVariableToStack(
                    ((ScriptExecutable_CreateVariable)((ScriptExecutable_AssignValue)exec).getLeft()).getName(),
                    (ScriptValue_Variable)((ScriptExecutable_AssignValue)exec).getLeft());
            }
        }
        assert LegacyDebugger.close();
        List<Object> deleteList = new LinkedList<Object>();
        for(Entry<String, List<ScriptFunction_Abstract>> entry : this.functions.entrySet()) {
            List<ScriptFunction_Abstract> entryFunctions = entry.getValue();
            for(int i = 0; i < entryFunctions.size(); i++) {
                if(entryFunctions.get(i) instanceof ScriptExecutable_ParseFunction) {
                    ScriptExecutable_ParseFunction fxn = (ScriptExecutable_ParseFunction)entryFunctions.get(i);
                    entryFunctions.remove(i);
                    getEnvironment().advanceNestedStack();
                    assert fxn != null;
                    if((fxn).getName() == null || (fxn).getName().equals("") || !(fxn).isStatic()) {
                        assert LegacyDebugger.open("Adding member-variables since this function is not static");
                        for(ScriptExecutable exec : this.preconstructors) {
                            if(exec instanceof ScriptExecutable_CreateVariable) {
                                getEnvironment().addVariableToStack(
                                    ((ScriptExecutable_CreateVariable)exec).getName(),
                                    (ScriptValue_Variable)exec);
                            } else if(exec instanceof ScriptExecutable_AssignValue
                                && ((ScriptExecutable_AssignValue)exec).getLeft() instanceof ScriptExecutable_CreateVariable) {
                                getEnvironment().addVariableToStack(
                                    ((ScriptExecutable_CreateVariable)((ScriptExecutable_AssignValue)exec).getLeft()).getName(),
                                    (ScriptValue_Variable)((ScriptExecutable_AssignValue)exec).getLeft());
                            }
                        }
                        assert LegacyDebugger.close();
                    }
                    assert LegacyDebugger.open("Parameter Variable Additions");
                    for(ScriptValue_Abstract value : fxn.getParameters()) {
                        if(value instanceof ScriptExecutable_CreateVariable) {
                            getEnvironment().addVariableToStack(
                                ((ScriptExecutable_CreateVariable)value).getName(),
                                (ScriptExecutable_CreateVariable)value);
                        }
                    }
                    assert LegacyDebugger.close();
                    ScriptFunction_Abstract function = Parser.parseFunction(fxn, getType());
                    assert LegacyDebugger.addSnapNode("Adding function to this template", function);
                    entryFunctions.add(i, function);
                    getEnvironment().retreatNestedStack();
                }
            }
            if(entryFunctions.isEmpty()) {
                deleteList.add(entry.getKey());
            }
        }
        for(Object obj : deleteList) {
            this.functions.remove(obj);
        }
        getEnvironment().retreatStack();
        assert LegacyDebugger.close();
    }

    // Abstract nodeable implementation
    @Override
    public boolean nodificate() {
        if(this.isObject) {
            assert LegacyDebugger.open("Object (" + getType() + ")");
        } else {
            assert LegacyDebugger.open("Object-Template (" + getType() + ")");
        }
        if(getExtendedClass() != null) {
            assert LegacyDebugger.addSnapNode("Extended template (" + getExtendedClass().getType() + ")", getExtendedClass());
        }
        if(getInterfaces() != null && getInterfaces().size() > 0) {
            assert LegacyDebugger.addSnapNode(
                "Implemented templates (" + getInterfaces().size() + " template(s))",
                getInterfaces());
        }
        if(this.functions != null && this.functions.size() > 0) {
            assert LegacyDebugger.open("Functions (" + this.functions.size() + " function(s))");
            for(Map.Entry<String, List<ScriptFunction_Abstract>> entry : this.functions.entrySet()) {
                assert LegacyDebugger.addSnapNode(
                    ScriptFunction.getDisplayableFunctionName(entry.getKey()),
                    entry.getValue());
            }
            assert LegacyDebugger.close();
        }
        if(this.variables != null && this.variables.size() > 0) {
            assert LegacyDebugger.addSnapNode("Variables (" + this.variables.size() + " member variable(s))", this.variables);
        }
        assert LegacyDebugger.addNode("Object: " + this.isObject);
        if(!this.isObject) {
            if(this.templatePreconstructors != null && this.templatePreconstructors.size() > 0) {
                assert LegacyDebugger.addSnapNode("Template Preconstructors ("
                    + this.templatePreconstructors.size()
                    + " static preconstructor(s))", this.templatePreconstructors);
            }
            if(this.preconstructors != null && this.preconstructors.size() > 0) {
                assert LegacyDebugger.addSnapNode(
                    "Preconstructors (" + this.preconstructors.size() + " preconstructor(s))",
                    this.preconstructors);
            }
            assert LegacyDebugger.addNode("Abstract: " + this.isAbstract);
        } else {
            assert LegacyDebugger.addNode("Constructing: " + this.isConstructing);
        }
        assert LegacyDebugger.close();
        return true;
    }

    // ScriptConvertible implementation
    public Object convert() {
        return this;
    }
}
