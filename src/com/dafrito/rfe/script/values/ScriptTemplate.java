package com.dafrito.rfe.script.values;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dafrito.rfe.gui.debug.Debugger;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.script.ScriptEnvironment;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_AbstractFunctionNotImplemented;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_FunctionAlreadyDefined;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_IllegalAbstractObjectCreation;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_UnimplementedFunction;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable_VariableAlreadyDefined;
import com.dafrito.rfe.script.operations.ScriptExecutable;
import com.dafrito.rfe.script.operations.ScriptExecutable_AssignValue;
import com.dafrito.rfe.script.operations.ScriptExecutable_CreateVariable;
import com.dafrito.rfe.script.operations.ScriptExecutable_ParseFunction;
import com.dafrito.rfe.script.parsing.Parser;
import com.dafrito.rfe.script.parsing.Referenced;
import com.dafrito.rfe.script.parsing.ScriptKeywordType;

public class ScriptTemplate extends ScriptTemplate_Abstract implements ScriptValue, Nodeable {
	public static ScriptTemplate createTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> interfaces, boolean isAbstract) {
		return new ScriptTemplate(env, type, extended, interfaces, isAbstract);
	}

	protected Map<String, List<ScriptFunction>> functions;
	protected Map<String, ScriptValue_Variable> variables;
	private boolean isObject, isAbstract;
	private boolean isConstructing, fullCreation;
	private List<ScriptExecutable> preconstructors;
	private List<ScriptExecutable> templatePreconstructors;

	private ScriptValueType extended;

	protected ScriptTemplate(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.isObject = true;
		this.fullCreation = true;
		this.isConstructing = true;
		this.variables = new HashMap<String, ScriptValue_Variable>();
	}

	protected ScriptTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> interfaces, boolean isAbstract) {
		super(env, type, extended, interfaces);
		this.fullCreation = true;
		this.isObject = false;
		this.variables = new HashMap<String, ScriptValue_Variable>();
		this.functions = new HashMap<String, List<ScriptFunction>>();
		this.templatePreconstructors = new LinkedList<ScriptExecutable>();
		this.preconstructors = new LinkedList<ScriptExecutable>();
		this.extended = extended;
		this.isAbstract = isAbstract;
	}

	public ScriptValueType getExtended() {
		return this.extended;
	}

	@Override
	public void addFunction(Referenced ref, String name, ScriptFunction function) throws Exception_Nodeable {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			this.getEnvironment().getTemplate(this.getType()).addFunction(ref, name, function);
			return;
		}
		assert Debugger.openNode("Object Function Additions", "Adding Function to Object (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		assert Debugger.addNode(this);
		assert Debugger.addNode(function);
		if (!this.isFullCreation() && (name == null || name.equals(""))) {
			assert Debugger.closeNode("Function is a constructor, and our template is not in its full-creation phase.");
			return;
		}
		if (this.functions.get(name) != null) {
			List<ScriptFunction> list = this.functions.get(name);
			if (function.isAbstract()) {
				for (ScriptFunction currentFxn : list) {
					// It's an abstract function and we implement it, so return.
					if (function.areParametersEqual(currentFxn.getParameters())) {
						assert Debugger.closeNode("The template has this abstract function implemented.");
						return;
					}
				}
			} else {
				for (int i = 0; i < list.size(); i++) {
					ScriptFunction currentFxn = list.get(i);
					// We already implement this function, so return.
					if (function.areParametersEqual(currentFxn.getParameters())) {
						if (currentFxn instanceof ScriptExecutable_ParseFunction) {
							throw new Exception_Nodeable_FunctionAlreadyDefined(ref, name);
						}
						list.remove(i);
						list.add(function);
						assert Debugger.closeNode("The template overloads an existing function");
						return;
					}
				}
				list.add(function);
				assert Debugger.closeNode("Function was successfully added", this);
				return;
			}
		}
		this.functions.put(name, new LinkedList<ScriptFunction>());
		this.functions.get(name).add(function);
		assert Debugger.closeNode("Function was successfully added", this);
	}

	@Override
	public void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		assert Debugger.openNode("Preconstructor Additions", "Adding Preconstructor");
		assert Debugger.addNode("Template", this);
		assert Debugger.addNode(exec);
		this.preconstructors.add(exec);
		assert Debugger.closeNode();
	}

	@Override
	public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		assert Debugger.openNode("Template Preconstructor Additions", "Adding Template Preconstructor");
		assert Debugger.addNode("Template", this);
		assert Debugger.addNode(exec);
		this.templatePreconstructors.add(exec);
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws Exception_Nodeable {
		if (this.isConstructing()) {
			assert Debugger.openNode("Object Variable Additions", "Adding Variable to Object (" + name + ")");
			assert Debugger.addNode(this);
			assert Debugger.addNode(value);
			if (this.variables.get(name) != null) {
				throw new Exception_Nodeable_VariableAlreadyDefined(ref, this, name);
			}
			if (this.isFullCreation() || !value.getPermission().equals(ScriptKeywordType.PRIVATE)) {
				this.variables.put(name, value);
				assert Debugger.addSnapNode("Variable successfully added", this);
			} else {
				assert Debugger.addNode("Variable is private, and our template is not in its full-creation phase.");
			}
			assert Debugger.closeNode();
			return value;
		}
		assert Debugger.openNode("Local Variable Additions", "Adding Variable to Stack (" + name + ")");
		assert Debugger.addNode(value);
		if (this.getEnvironment().getVariableFromStack(name) != null) {
			throw new Exception_Nodeable_VariableAlreadyDefined(ref, this, name);
		}
		this.getEnvironment().addVariableToStack(name, value);
		assert Debugger.closeNode();
		return value;
	}

	@Override
	public ScriptTemplate_Abstract createObject(Referenced ref, ScriptTemplate_Abstract object) throws Exception_Nodeable {
		assert Debugger.openNode("Object Creations", "Object Creation");
		if (object == null) {
			if (this.isAbstract()) {
				throw new Exception_Nodeable_IllegalAbstractObjectCreation(ref);
			}
			object = this.instantiateTemplate();
		}
		assert Debugger.addNode(object);
		this.getEnvironment().advanceStack(object, null);
		if (this.preconstructors.size() > 0) {
			assert Debugger.openNode("Calling preconstructor expressions (" + this.preconstructors.size() + " expression(s))");
			if (!object.isConstructing()) {
				object.disableFullCreation();
			}
			object.setConstructing(true);
			for (ScriptExecutable exec : this.preconstructors) {
				exec.execute();
			}
			assert Debugger.closeNode();
		}
		object.disableFullCreation();
		if (this.getExtendedClass() != null) {
			assert Debugger.openNode("Now sending to base class preconstructor (" + this.getEnvironment().getName(this.getExtendedClass().getType()) + ")");
			object = this.getExtendedClass().createObject(ref, object);
			assert Debugger.closeNode();
		}
		if (this.getInterfaces() != null && this.getInterfaces().size() > 0) {
			assert Debugger.openNode("Calling interface preconstructors (" + this.getInterfaces().size() + " preconstructor(s))");
			for (ScriptValueType interfaceType : this.getInterfaces()) {
				object = this.getEnvironment().getTemplate(interfaceType).createObject(ref, object);
			}
			assert Debugger.closeNode();
		}
		object.setConstructing(false);
		if (object.getFunctions() != null) {
			for (ScriptFunction function : object.getFunctions()) {
				if (function.isAbstract()) {
					throw new Exception_Nodeable_AbstractFunctionNotImplemented(ref, object, function);
				}
			}
		} else {
			assert Debugger.addNode(object);
		}
		this.getEnvironment().retreatStack();
		assert Debugger.closeNode();
		return object;
	}

	@Override
	public void disableFullCreation() {
		this.fullCreation = false;
	}

	@Override
	public ScriptFunction getFunction(String name, List<ScriptValue> params) {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getFunction(name, params);
		}
		assert Debugger.openNode("Object Function Retrievals", "Retrieving Function from Object (" + RiffScriptFunction.getDisplayableFunctionName(name) + ")");
		assert Debugger.addSnapNode("Current template", this);
		List<ScriptFunction> list = this.functions.get(name);
		if (list != null && !list.isEmpty()) {
			assert Debugger.addSnapNode("Functions found", list);
			for (ScriptFunction function : list) {
				if (function.areParametersConvertible(params)) {
					assert Debugger.closeNode("Params match, returning function", function);
					return function;
				}
			}
		}
		ScriptFunction fxn = null;
		if (this.getExtendedClass() != null) {
			fxn = this.getExtendedClass().getFunction(name, params);
		}
		if (fxn != null) {
			assert Debugger.closeNode("Returning function", fxn);
		} else {
			assert Debugger.closeNode("Function not found");
		}
		return fxn;
	}

	@Override
	public List<ScriptFunction> getFunctions() {
		if (this.functions == null) {
			return null;
		}
		List<ScriptFunction> functions = new LinkedList<ScriptFunction>();
		for (List<ScriptFunction> fxnList : this.functions.values()) {
			functions.addAll(fxnList);
		}
		return functions;
	}

	@Override
	public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction fxn) {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getFunctionTemplate(fxn);
		}
		for (List<ScriptFunction> fxns : this.functions.values()) {
			if (fxns.contains(fxn)) {
				return this;
			}
		}
		if (this.getExtendedClass() != null) {
			return this.getExtendedClass().getFunctionTemplate(fxn);
		} else {
			return null;
		}
	}

	@Override
	public ScriptValue_Variable getStaticReference() throws Exception_Nodeable {
		return new ScriptValue_Variable(this.getEnvironment(), this.getType(), this, ScriptKeywordType.PUBLIC);
	}

	@Override
	public ScriptValue_Variable getVariable(String name) throws Exception_Nodeable {
		assert Debugger.openNode("Object Variable Retrievals", "Retrieving Variable From Object ('" + name + "')");
		assert Debugger.addNode(this);
		ScriptValue_Variable var = this.variables.get(name);
		if (var == null && this.isObject()) {
			assert Debugger.openNode("Variable not a member, so checking static members");
			var = this.getEnvironment().getTemplate(this.getType()).getVariable(name);
			assert Debugger.closeNode();
		}
		if (var != null) {
			if (var.getPermission().equals(ScriptKeywordType.PUBLIC)) {
				assert Debugger.closeNode("Variable found and permission valid (public)", var);
				return var;
			} else if (var.getPermission().equals(ScriptKeywordType.PRIVATE)) {
				if (this.getEnvironment().getCurrentObject() == this || (this.getEnvironment().getTemplate(this.getType()) == this && this.getEnvironment().getCurrentObject().getType().equals(this.getType()))) {
					assert Debugger.closeNode("Variable found and permission valid (private)", var);
					return var;
				}
			}
		}
		assert Debugger.closeNode("Variable not found or permission is invalid");
		return null;
	}

	@Override
	public void initialize() throws Exception_Nodeable {
		this.variables.clear();
		if (this.templatePreconstructors == null || this.templatePreconstructors.size() == 0) {
			return;
		}
		assert Debugger.openNode("Template Initializations", "Initializing Template (" + this.getType() + ")");
		if (!this.isAbstract()) {
			for (Map.Entry<String, List<ScriptFunction>> entry : this.functions.entrySet()) {
				List<ScriptFunction> functions = entry.getValue();
				for (int i = 0; i < functions.size(); i++) {
					if (functions.get(i).isAbstract()) {
						// We don't implement it, so fail.
						throw new Exception_Nodeable_UnimplementedFunction(this.getEnvironment(), this, entry.getKey());
					}
				}
			}
		}
		this.getEnvironment().advanceStack(this, null);
		this.setConstructing(true);
		assert Debugger.openNode("Executing preconstructors (" + this.templatePreconstructors.size() + " preconstructor(s))");
		for (ScriptExecutable exec : this.templatePreconstructors) {
			exec.execute();
		}
		this.setConstructing(false);
		assert Debugger.closeNode();
		this.getEnvironment().retreatStack();
		assert Debugger.closeNode();
	}

	@Override
	public void initializeFunctions(Referenced ref) throws Exception_Nodeable {
		assert Debugger.openNode("Unparsed Member-Function Initialization", "Initializing Unparsed Member Functions (" + this.getType() + ")");
		this.getEnvironment().advanceStack(this, null);
		assert Debugger.openNode("Adding static member-variables");
		for (ScriptExecutable exec : this.templatePreconstructors) {
			if (exec instanceof ScriptExecutable_CreateVariable) {
				this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) exec).getName(), (ScriptValue_Variable) exec);
			} else if (exec instanceof ScriptExecutable_AssignValue && ((ScriptExecutable_AssignValue) exec).getLeft() instanceof ScriptExecutable_CreateVariable) {
				this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) ((ScriptExecutable_AssignValue) exec).getLeft()).getName(), (ScriptValue_Variable) ((ScriptExecutable_AssignValue) exec).getLeft());
			}
		}
		assert Debugger.closeNode();
		List<Object> deleteList = new LinkedList<Object>();
		for (Map.Entry<String, List<ScriptFunction>> entry : this.functions.entrySet()) {
			List<ScriptFunction> functions = entry.getValue();
			for (int i = 0; i < functions.size(); i++) {
				if (functions.get(i) instanceof ScriptExecutable_ParseFunction) {
					ScriptExecutable_ParseFunction fxn = (ScriptExecutable_ParseFunction) functions.get(i);
					functions.remove(i);
					this.getEnvironment().advanceNestedStack();
					assert fxn != null;
					if ((fxn).getName() == null || (fxn).getName().equals("") || !(fxn).isStatic()) {
						assert Debugger.openNode("Adding member-variables since this function is not static");
						for (ScriptExecutable exec : this.preconstructors) {
							if (exec instanceof ScriptExecutable_CreateVariable) {
								this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) exec).getName(), (ScriptValue_Variable) exec);
							} else if (exec instanceof ScriptExecutable_AssignValue && ((ScriptExecutable_AssignValue) exec).getLeft() instanceof ScriptExecutable_CreateVariable) {
								this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) ((ScriptExecutable_AssignValue) exec).getLeft()).getName(), (ScriptValue_Variable) ((ScriptExecutable_AssignValue) exec).getLeft());
							}
						}
						assert Debugger.closeNode();
					}
					assert Debugger.openNode("Parameter Variable Additions");
					for (ScriptValue value : fxn.getParameters()) {
						if (value instanceof ScriptExecutable_CreateVariable) {
							this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) value).getName(), (ScriptExecutable_CreateVariable) value);
						}
					}
					assert Debugger.closeNode();
					ScriptFunction function = Parser.parseFunction(fxn, this.getType());
					assert Debugger.addSnapNode("Adding function to this template", function);
					functions.add(i, function);
					this.getEnvironment().retreatNestedStack();
				}
			}
			if (functions.size() == 0) {
				deleteList.add(entry.getKey());
			}
		}
		for (Object obj : deleteList) {
			this.functions.remove(obj);
		}
		this.getEnvironment().retreatStack();
		assert Debugger.closeNode();
	}

	@Override
	public ScriptTemplate instantiateTemplate() {
		return new ScriptTemplate(this.getEnvironment(), this.getType());
	}

	@Override
	public boolean isAbstract() throws Exception_Nodeable {
		if (this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).isAbstract();
		}
		return this.isAbstract;
	}

	@Override
	public boolean isConstructing() throws Exception_Nodeable {
		return this.isConstructing;
	}

	// Abstract-template implementation
	@Override
	public boolean isFullCreation() {
		return this.fullCreation;
	}

	@Override
	public boolean isObject() {
		return this.isObject;
	}

	@Override
	public void nodificate() {
		if (this.isObject) {
			assert Debugger.openNode("Object (" + this.getType() + ")");
		} else {
			assert Debugger.openNode("Object-Template (" + this.getType() + ")");
		}
		if (this.getExtendedClass() != null) {
			assert Debugger.addSnapNode("Extended template (" + this.getExtendedClass().getType() + ")", this.getExtendedClass());
		}
		if (this.getInterfaces() != null && this.getInterfaces().size() > 0) {
			assert Debugger.addSnapNode("Implemented templates (" + this.getInterfaces().size() + " template(s))", this.getInterfaces());
		}
		if (this.functions != null && this.functions.size() > 0) {
			assert Debugger.openNode("Functions (" + this.functions.size() + " function(s))");
			for (Map.Entry<String, List<ScriptFunction>> entry : this.functions.entrySet()) {
				assert Debugger.addSnapNode(RiffScriptFunction.getDisplayableFunctionName(entry.getKey()), entry.getValue());
			}
			assert Debugger.closeNode();
		}
		if (this.variables != null && this.variables.size() > 0) {
			assert Debugger.addSnapNode("Variables (" + this.variables.size() + " member variable(s))", this.variables);
		}
		assert Debugger.addNode("Object: " + this.isObject);
		if (!this.isObject) {
			if (this.templatePreconstructors != null && this.templatePreconstructors.size() > 0) {
				assert Debugger.addSnapNode("Template Preconstructors (" + this.templatePreconstructors.size() + " static preconstructor(s))", this.templatePreconstructors);
			}
			if (this.preconstructors != null && this.preconstructors.size() > 0) {
				assert Debugger.addSnapNode("Preconstructors (" + this.preconstructors.size() + " preconstructor(s))", this.preconstructors);
			}
			assert Debugger.addNode("Abstract: " + this.isAbstract);
		} else {
			assert Debugger.addNode("Constructing: " + this.isConstructing);
		}
		assert Debugger.closeNode();
	}

	@Override
	public void setConstructing(boolean constructing) throws Exception_Nodeable {
		assert Debugger.openNode("Object Construction Settings", "Setting constructing-boolean to: " + constructing);
		this.isConstructing = constructing;
		assert Debugger.addNode(this);
		assert Debugger.closeNode();
	}
}
