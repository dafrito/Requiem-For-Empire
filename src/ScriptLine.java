public class ScriptLine extends ScriptElement implements Nodeable {
	private String m_string;

	public ScriptLine(ScriptEnvironment env, String filename, int num, String string) {
		super(env, filename, num, string, string.length());
		this.m_string = string;
	}

	public ScriptLine(String string, ScriptLine otherLine, int oLO) {
		super(otherLine, oLO, string.length());
		this.m_string = string;
	}

	public String getString() {
		return this.m_string;
	}

	@Override
	public boolean nodificate() {
		assert Debugger.openNode(Debugger.getString(DebugString.SCRIPTLINE) + this.m_string);
		assert super.nodificate();
		assert Debugger.closeNode();
		return true;
	}

	public void setString(String string) {
		this.m_string = string;
	}

	@Override
	public String toString() {
		return this.getFilename() + "@" + this.getLineNumber() + ": \"" + this.m_string + '"';
	}
}
