package com.dafrito.rfe;
import java.util.Collection;
import java.util.List;


public class ScriptExecutable_ParseFunction extends ScriptElement implements ScriptFunction_Abstract, ScriptExecutable, Nodeable {
	private boolean isStatic, isAbstract;
	private ScriptGroup body;
	private ScriptValue_Abstract template;
	private String name;
	private ScriptValueType returnType;
	private List<ScriptValue_Abstract> parameters;
	private ScriptKeywordType permission;

	public ScriptExecutable_ParseFunction(Referenced ref, ScriptValueType returnType, ScriptValue_Abstract object, String name, List<ScriptValue_Abstract> paramList, ScriptKeywordType permission, boolean isStatic, boolean isAbstract, ScriptGroup body) {
		super(ref);
		this.name = name;
		this.template = object;
		this.returnType = returnType;
		this.parameters = paramList;
		this.permission = permission;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		this.body = body;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public boolean areParametersConvertible(List<ScriptValue_Abstract> list) {
		return ScriptFunction.areParametersConvertible(this.getParameters(), list);
	}

	@Override
	public boolean areParametersEqual(List<ScriptValue_Abstract> list) {
		return ScriptFunction.areParametersEqual(this.getParameters(), list);
	}

	// ScriptExecutable implementation
	@Override
	public ScriptValue_Abstract execute() throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	public ScriptGroup getBody() {
		return this.body;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public List<ScriptValue_Abstract> getParameters() {
		return this.parameters;
	}

	@Override
	public ScriptKeywordType getPermission() {
		return this.permission;
	}

	@Override
	public ScriptValueType getReturnType() {
		return this.returnType;
	}

	@Override
	public ScriptValue_Abstract getReturnValue() {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}

	// ScriptFunction implementation
	@Override
	public boolean isAbstract() {
		return this.isAbstract;
	}

	@Override
	public boolean isStatic() {
		return this.isStatic;
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Unparsed Script-Function (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")");
		assert super.nodificate();
		assert Debugger.addNode("Static: " + this.isStatic);
		assert Debugger.addSnapNode("Body", this.body);
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public void setReturnValue(Referenced element, ScriptValue_Abstract value) {
		throw new Exception_InternalError(this.getEnvironment(), "Invalid call in unparsed function");
	}
}
