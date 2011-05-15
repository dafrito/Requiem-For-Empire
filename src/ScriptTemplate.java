import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScriptTemplate extends ScriptTemplate_Abstract implements ScriptValue_Abstract, Nodeable, ScriptConvertible {
	public static ScriptTemplate createTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> interfaces, boolean isAbstract) {
		return new ScriptTemplate(env, type, extended, interfaces, isAbstract);
	}

	protected Map<String, List<ScriptFunction_Abstract>> m_functions;
	protected Map<String, ScriptValue_Variable> m_variables;
	private boolean m_isObject, m_isAbstract;
	private boolean m_isConstructing, m_fullCreation;
	private List<ScriptExecutable> m_preconstructors;
	private List<ScriptExecutable> m_templatePreconstructors;

	private ScriptValueType m_extended;

	protected ScriptTemplate(ScriptEnvironment env, ScriptValueType type) {
		super(env, type);
		this.m_isObject = true;
		this.m_fullCreation = true;
		this.m_isConstructing = true;
		this.m_variables = new HashMap<String, ScriptValue_Variable>();
	}

	protected ScriptTemplate(ScriptEnvironment env, ScriptValueType type, ScriptValueType extended, List<ScriptValueType> interfaces, boolean isAbstract) {
		super(env, type, extended, interfaces);
		this.m_fullCreation = true;
		this.m_isObject = false;
		this.m_variables = new HashMap<String, ScriptValue_Variable>();
		this.m_functions = new HashMap<String, List<ScriptFunction_Abstract>>();
		this.m_templatePreconstructors = new LinkedList<ScriptExecutable>();
		this.m_preconstructors = new LinkedList<ScriptExecutable>();
		this.m_extended = extended;
		this.m_isAbstract = isAbstract;
	}

	@Override
	public void addFunction(Referenced ref, String name, ScriptFunction_Abstract function) throws Exception_Nodeable {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			this.getEnvironment().getTemplate(this.getType()).addFunction(ref, name, function);
			return;
		}
		assert Debugger.openNode("Object Function Additions", "Adding Function to Object (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		assert Debugger.addNode(this);
		assert Debugger.addNode(function);
		if (!this.isFullCreation() && (name == null || name.equals(""))) {
			assert Debugger.closeNode("Function is a constructor, and our template is not in its full-creation phase.");
			return;
		}
		if (this.m_functions.get(name) != null) {
			List<ScriptFunction_Abstract> list = this.m_functions.get(name);
			if (function.isAbstract()) {
				for (ScriptFunction_Abstract currentFxn : list) {
					// It's an abstract function and we implement it, so return.
					if (function.areParametersEqual(currentFxn.getParameters())) {
						assert Debugger.closeNode("The template has this abstract function implemented.");
						return;
					}
				}
			} else {
				for (int i = 0; i < list.size(); i++) {
					ScriptFunction_Abstract currentFxn = list.get(i);
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
		this.m_functions.put(name, new LinkedList<ScriptFunction_Abstract>());
		this.m_functions.get(name).add(function);
		assert Debugger.closeNode("Function was successfully added", this);
	}

	@Override
	public void addPreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		assert Debugger.openNode("Preconstructor Additions", "Adding Preconstructor");
		assert Debugger.addNode("Template", this);
		assert Debugger.addNode(exec);
		this.m_preconstructors.add(exec);
		assert Debugger.closeNode();
	}

	@Override
	public void addTemplatePreconstructorExpression(ScriptExecutable exec) throws Exception_Nodeable {
		assert Debugger.openNode("Template Preconstructor Additions", "Adding Template Preconstructor");
		assert Debugger.addNode("Template", this);
		assert Debugger.addNode(exec);
		this.m_templatePreconstructors.add(exec);
		assert Debugger.closeNode();
	}

	@Override
	public ScriptValue_Variable addVariable(Referenced ref, String name, ScriptValue_Variable value) throws Exception_Nodeable {
		if (this.isConstructing()) {
			assert Debugger.openNode("Object Variable Additions", "Adding Variable to Object (" + name + ")");
			assert Debugger.addNode(this);
			assert Debugger.addNode(value);
			if (this.m_variables.get(name) != null) {
				throw new Exception_Nodeable_VariableAlreadyDefined(ref, this, name);
			}
			if (this.isFullCreation() || !value.getPermission().equals(ScriptKeywordType.PRIVATE)) {
				this.m_variables.put(name, value);
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

	// ScriptConvertible implementation
	@Override
	public Object convert() {
		return this;
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
		if (this.m_preconstructors.size() > 0) {
			assert Debugger.openNode("Calling preconstructor expressions (" + this.m_preconstructors.size() + " expression(s))");
			if (!object.isConstructing()) {
				object.disableFullCreation();
			}
			object.setConstructing(true);
			for (ScriptExecutable exec : this.m_preconstructors) {
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
			for (ScriptFunction_Abstract function : object.getFunctions()) {
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
		this.m_fullCreation = false;
	}

	@Override
	public ScriptFunction_Abstract getFunction(String name, List<ScriptValue_Abstract> params) {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getFunction(name, params);
		}
		assert Debugger.openNode("Object Function Retrievals", "Retrieving Function from Object (" + ScriptFunction.getDisplayableFunctionName(name) + ")");
		assert Debugger.addSnapNode("Current template", this);
		List<ScriptFunction_Abstract> list = this.m_functions.get(name);
		if (list != null && list.size() > 0) {
			assert Debugger.addSnapNode("Functions found", list);
			for (ScriptFunction_Abstract function : list) {
				if (function.areParametersConvertible(params)) {
					assert Debugger.closeNode("Params match, returning function", function);
					return function;
				}
			}
		}
		ScriptFunction_Abstract fxn = null;
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
	public List<ScriptFunction_Abstract> getFunctions() {
		if (this.m_functions == null) {
			return null;
		}
		List<ScriptFunction_Abstract> functions = new LinkedList<ScriptFunction_Abstract>();
		for (List<ScriptFunction_Abstract> fxnList : this.m_functions.values()) {
			functions.addAll(fxnList);
		}
		return functions;
	}

	@Override
	public ScriptTemplate_Abstract getFunctionTemplate(ScriptFunction_Abstract fxn) {
		if (this.getEnvironment().getTemplate(this.getType()) != null && this.getEnvironment().getTemplate(this.getType()) != this) {
			return this.getEnvironment().getTemplate(this.getType()).getFunctionTemplate(fxn);
		}
		for (List<ScriptFunction_Abstract> fxns : this.m_functions.values()) {
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
		ScriptValue_Variable var = this.m_variables.get(name);
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
		this.m_variables.clear();
		if (this.m_templatePreconstructors == null || this.m_templatePreconstructors.size() == 0) {
			return;
		}
		assert Debugger.openNode("Template Initializations", "Initializing Template (" + this.getType() + ")");
		if (!this.isAbstract()) {
			for (Map.Entry entry : this.m_functions.entrySet()) {
				List<ScriptFunction_Abstract> functions = this.m_functions.get(entry.getKey());
				for (int i = 0; i < functions.size(); i++) {
					if (functions.get(i).isAbstract()) {
						// We don't implement it, so fail.
						throw new Exception_Nodeable_UnimplementedFunction(this.getEnvironment(), this, (String) entry.getKey());
					}
				}
			}
		}
		this.getEnvironment().advanceStack(this, null);
		this.setConstructing(true);
		assert Debugger.openNode("Executing preconstructors (" + this.m_templatePreconstructors.size() + " preconstructor(s))");
		for (ScriptExecutable exec : this.m_templatePreconstructors) {
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
		for (ScriptExecutable exec : this.m_templatePreconstructors) {
			if (exec instanceof ScriptExecutable_CreateVariable) {
				this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) exec).getName(), (ScriptValue_Variable) exec);
			} else if (exec instanceof ScriptExecutable_AssignValue && ((ScriptExecutable_AssignValue) exec).getLeft() instanceof ScriptExecutable_CreateVariable) {
				this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) ((ScriptExecutable_AssignValue) exec).getLeft()).getName(), (ScriptValue_Variable) ((ScriptExecutable_AssignValue) exec).getLeft());
			}
		}
		assert Debugger.closeNode();
		List<Object> deleteList = new LinkedList<Object>();
		for (Map.Entry entry : this.m_functions.entrySet()) {
			List<ScriptFunction_Abstract> functions = this.m_functions.get(entry.getKey());
			for (int i = 0; i < functions.size(); i++) {
				if (functions.get(i) instanceof ScriptExecutable_ParseFunction) {
					ScriptExecutable_ParseFunction fxn = (ScriptExecutable_ParseFunction) functions.get(i);
					functions.remove(i);
					this.getEnvironment().advanceNestedStack();
					assert fxn != null;
					if ((fxn).getName() == null || (fxn).getName().equals("") || !(fxn).isStatic()) {
						assert Debugger.openNode("Adding member-variables since this function is not static");
						for (ScriptExecutable exec : this.m_preconstructors) {
							if (exec instanceof ScriptExecutable_CreateVariable) {
								this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) exec).getName(), (ScriptValue_Variable) exec);
							} else if (exec instanceof ScriptExecutable_AssignValue && ((ScriptExecutable_AssignValue) exec).getLeft() instanceof ScriptExecutable_CreateVariable) {
								this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) ((ScriptExecutable_AssignValue) exec).getLeft()).getName(), (ScriptValue_Variable) ((ScriptExecutable_AssignValue) exec).getLeft());
							}
						}
						assert Debugger.closeNode();
					}
					assert Debugger.openNode("Parameter Variable Additions");
					for (ScriptValue_Abstract value : fxn.getParameters()) {
						if (value instanceof ScriptExecutable_CreateVariable) {
							this.getEnvironment().addVariableToStack(((ScriptExecutable_CreateVariable) value).getName(), (ScriptExecutable_CreateVariable) value);
						}
					}
					assert Debugger.closeNode();
					ScriptFunction_Abstract function = Parser.parseFunction(fxn, this.getType());
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
			this.m_functions.remove(obj);
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
		return this.m_isAbstract;
	}

	@Override
	public boolean isConstructing() throws Exception_Nodeable {
		return this.m_isConstructing;
	}

	// Abstract-template implementation
	@Override
	public boolean isFullCreation() {
		return this.m_fullCreation;
	}

	@Override
	public boolean isObject() {
		return this.m_isObject;
	}

	// Abstract nodeable implementation
	@Override
	public boolean nodificate() {
		if (this.m_isObject) {
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
		if (this.m_functions != null && this.m_functions.size() > 0) {
			assert Debugger.openNode("Functions (" + this.m_functions.size() + " function(s))");
			for (Map.Entry entry : this.m_functions.entrySet()) {
				assert Debugger.addSnapNode(ScriptFunction.getDisplayableFunctionName((String) entry.getKey()), entry.getValue());
			}
			assert Debugger.closeNode();
		}
		if (this.m_variables != null && this.m_variables.size() > 0) {
			assert Debugger.addSnapNode("Variables (" + this.m_variables.size() + " member variable(s))", this.m_variables);
		}
		assert Debugger.addNode("Object: " + this.m_isObject);
		if (!this.m_isObject) {
			if (this.m_templatePreconstructors != null && this.m_templatePreconstructors.size() > 0) {
				assert Debugger.addSnapNode("Template Preconstructors (" + this.m_templatePreconstructors.size() + " static preconstructor(s))", this.m_templatePreconstructors);
			}
			if (this.m_preconstructors != null && this.m_preconstructors.size() > 0) {
				assert Debugger.addSnapNode("Preconstructors (" + this.m_preconstructors.size() + " preconstructor(s))", this.m_preconstructors);
			}
			assert Debugger.addNode("Abstract: " + this.m_isAbstract);
		} else {
			assert Debugger.addNode("Constructing: " + this.m_isConstructing);
		}
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void setConstructing(boolean constructing) throws Exception_Nodeable {
		assert Debugger.openNode("Object Construction Settings", "Setting constructing-boolean to: " + constructing);
		this.m_isConstructing = constructing;
		assert Debugger.addNode(this);
		assert Debugger.closeNode();
	}
}
