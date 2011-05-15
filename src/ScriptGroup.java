import java.util.List;

public class ScriptGroup extends ScriptElement implements Nodeable {
	public enum GroupType {
		curly, parenthetical
	}

	protected List<Object> m_elements;
	private GroupType m_type;

	public ScriptGroup(Referenced ref, List<Object> elements, GroupType type) {
		super(ref);
		m_elements = elements;
		m_type = type;
	}

	public List<Object> getElements() {
		return m_elements;
	}

	public GroupType getType() {
		return m_type;
	}

	@Override
	public boolean nodificate() {
		switch (m_type) {
		case curly:
			assert Debugger.openNode(DebugString.SCRIPTGROUPCURLY);
			break;
		case parenthetical:
			assert Debugger.openNode(DebugString.SCRIPTGROUPPARENTHETICAL);
			break;
		default:
			throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.addSnapNode(DebugString.ELEMENTS, m_elements);
		assert Debugger.closeNode();
		return true;
	}

	public void setElements(List<Object> list) {
		m_elements = list;
	}
}
