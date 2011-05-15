public class ScriptElement implements Nodeable, Referenced {
	private final ScriptEnvironment m_environment;
	private final int m_lineNumber, m_originalLineOffset, m_length;
	private final String m_original, m_filename;

	public ScriptElement() {
		this.m_environment = null;
		this.m_filename = "";
		this.m_original = "";
		this.m_lineNumber = -1;
		this.m_length = -1;
		this.m_originalLineOffset = 0;
	}

	public ScriptElement(Referenced element) {
		this(element.getDebugReference());
	}

	public ScriptElement(ScriptElement element) {
		this(element.getEnvironment(), element, 0, element.getLength());
	}

	public ScriptElement(ScriptElement element, int oLO, int length) {
		this(element.getEnvironment(), element, oLO, length);
	}

	public ScriptElement(ScriptEnvironment env) {
		this.m_environment = env;
		this.m_filename = "";
		this.m_original = "";
		this.m_lineNumber = -1;
		this.m_length = -1;
		this.m_originalLineOffset = 0;
	}

	public ScriptElement(ScriptEnvironment env, ScriptElement element, int oLO, int length) {
		this.m_environment = env;
		if (element != null) {
			this.m_filename = element.getFilename();
			this.m_lineNumber = element.getLineNumber();
			this.m_originalLineOffset = element.getOffset() + oLO;
			this.m_original = element.getOriginalString();
			this.m_length = length;
		} else {
			this.m_filename = "";
			this.m_original = "";
			this.m_lineNumber = -1;
			this.m_originalLineOffset = 0;
			this.m_length = -1;
		}
	}

	public ScriptElement(ScriptEnvironment env, String filename, int lineNumber, String original, int length) {
		this.m_environment = env;
		this.m_filename = filename;
		this.m_lineNumber = lineNumber;
		this.m_original = original;
		this.m_originalLineOffset = 0;
		this.m_length = length;
	}

	@Override
	public ScriptElement getDebugReference() {
		return this;
	}

	@Override
	public ScriptEnvironment getEnvironment() {
		assert this.m_environment != null : "Environment is null." + this;
		return this.m_environment;
	}

	public String getFilename() {
		return this.m_filename;
	}

	public String getFragment() {
		return this.m_original.substring(this.getOffset(), this.getLength());
	}

	public int getLength() {
		return this.m_length;
	}

	public int getLineNumber() {
		return this.m_lineNumber;
	}

	public int getOffset() {
		return this.m_originalLineOffset;
	}

	public String getOriginalString() {
		return this.m_original;
	}

	@Override
	public boolean nodificate() {
		if (this.getLineNumber() == -1) {
			assert Debugger.addNode("ScriptElement: No information provided");
			return true;
		}
		assert Debugger.addSnapNode("ScriptElement (" + this.getFilename() + " @ " + this.getLineNumber() + ")", Debugger.getString(DebugString.ORIGINALSTRING) + this.getOriginalString() + "'");
		return true;
	}
}
