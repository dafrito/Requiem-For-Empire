import java.util.List;

public class ScriptGroup extends ScriptElement implements Nodeable {
	public enum GroupType {
		curly, parenthetical
	}

	protected List<Object> elements;
	private GroupType type;

	public ScriptGroup(Referenced ref, List<Object> elements, GroupType type) {
		super(ref);
		this.elements = elements;
		this.type = type;
	}

	public List<Object> getElements() {
		return this.elements;
	}

	public GroupType getType() {
		return this.type;
	}

	@Override
	public boolean nodificate() {
		switch (this.type) {
		case curly:
			assert Debugger.openNode(DebugString.SCRIPTGROUPCURLY);
			break;
		case parenthetical:
			assert Debugger.openNode(DebugString.SCRIPTGROUPPARENTHETICAL);
			break;
		default:
			throw new Exception_InternalError("Invalid default");
		}
		assert Debugger.addSnapNode(DebugString.ELEMENTS, this.elements);
		assert Debugger.closeNode();
		return true;
	}

	public void setElements(List<Object> list) {
		this.elements = list;
	}
}
