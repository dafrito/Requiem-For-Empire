import java.util.Collection;
import java.util.List;

public class ScriptFunction_Faux extends ScriptFunction implements ScriptFunction_Abstract, Nodeable {
	private FauxTemplate m_fauxTemplate;
	private ScriptTemplate_Abstract m_object;
	private String m_name;

	public ScriptFunction_Faux(FauxTemplate template, String name, ScriptValueType type, List<ScriptValue_Abstract> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) {
		super(type, params, permission, isAbstract, isStatic);
		this.m_fauxTemplate = template;
		this.m_name = name;
	}

	@Override
	public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
		throw new Exception_InternalError("Invalid call in faux function");
	}

	@Override
	public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
		throw new Exception_InternalError("Invalid call in faux function");
	}

	@Override
	public void execute(Referenced ref, List<ScriptValue_Abstract> params) throws Exception_Nodeable {
		if (this.m_name.equals("")) {
			this.setReturnValue(ref, this.m_fauxTemplate.execute(ref, this.m_name, params, null));
		} else {
			if (this.m_object == null) {
				this.m_object = this.m_fauxTemplate;
			}
			this.setReturnValue(ref, this.m_fauxTemplate.execute(ref, this.m_name, params, this.m_object));
		}
	}

	// Nodeable implementation
	@Override
	public boolean nodificate() {
		assert Debugger.openNode("Faux Script-Function (" + ScriptFunction.getDisplayableFunctionName(this.m_name) + ")");
		assert super.nodificate();
		assert Debugger.addNode("Faux Template Type: " + this.m_fauxTemplate.getType());
		assert Debugger.closeNode();
		return true;
	}

	public void setFauxTemplate(ScriptTemplate_Abstract template) {
		assert Debugger.openNode("Faux Function Referenced-Template Changes", "Changing Faux-Function Object");
		assert Debugger.addNode(this);
		assert Debugger.addSnapNode("New object", template);
		this.m_fauxTemplate = (FauxTemplate) template;
		assert Debugger.closeNode();
	}

	public void setTemplate(ScriptTemplate_Abstract template) {
		assert Debugger.openNode("Faux Function Object Changes", "Changing Object");
		assert Debugger.addNode(this);
		assert Debugger.addSnapNode("New object", template);
		this.m_object = template;
		assert Debugger.closeNode();
	}
}
