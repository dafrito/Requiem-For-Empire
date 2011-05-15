import java.util.Collection;
import java.util.List;

public interface ScriptFunction_Abstract {
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable;

	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable;

	public boolean areParametersConvertible(List<ScriptValue_Abstract> list);

	public boolean areParametersEqual(List<ScriptValue_Abstract> list);

	public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable;

	public List<ScriptValue_Abstract> getParameters();

	public ScriptKeywordType getPermission();

	public ScriptValueType getReturnType();

	public ScriptValue_Abstract getReturnValue();

	// ScriptFunction implementation
	public boolean isAbstract();

	public boolean isStatic();

	public boolean nodificate();

	public void setReturnValue(Referenced element, ScriptValue_Abstract value) throws Exception_Nodeable;
}
