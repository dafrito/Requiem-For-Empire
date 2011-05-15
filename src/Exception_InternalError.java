import java.io.PrintWriter;
import java.io.StringWriter;

public class Exception_InternalError extends RuntimeException implements Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6803130804744137902L;
	private int m_offset, m_lineNumber, m_length;
	private String m_line, m_filename, m_message;
	private final ScriptEnvironment m_environment;
	private final Object m_object;

	public Exception_InternalError(Referenced ref, String message) {
		this(ref.getDebugReference().getEnvironment(), ref.getDebugReference(), message);
	}

	public Exception_InternalError(ScriptEnvironment env, Exception object) {
		this(env, object, "");
	}

	public Exception_InternalError(ScriptEnvironment env, Object element, String message) {
		this.m_environment = env;
		this.m_object = element;
		this.m_filename = null;
		this.m_line = null;
		this.m_offset = 0;
		this.m_lineNumber = -1;
		this.m_length = -1;
		this.m_message = message;
	}

	public Exception_InternalError(ScriptEnvironment env, ScriptElement element, String message) {
		this.m_object = null;
		this.m_environment = env;
		this.m_message = message;
		if (element != null) {
			this.m_filename = element.getFilename();
			this.m_lineNumber = element.getLineNumber();
			this.m_line = element.getOriginalString();
			this.m_offset = element.getOffset();
			this.m_length = element.getLength();
		} else {
			this.m_offset = 0;
			this.m_line = null;
			this.m_filename = null;
			this.m_lineNumber = -1;
			this.m_length = -1;
		}
	}

	public Exception_InternalError(ScriptEnvironment env, String message) {
		this.m_environment = env;
		this.m_object = null;
		this.m_filename = null;
		this.m_line = null;
		this.m_offset = 0;
		this.m_lineNumber = 0;
		this.m_length = -1;
		this.m_message = message;
	}

	public Exception_InternalError(String message) {
		this((ScriptEnvironment) null, message);
	}

	public Exception_InternalError(String message, Exception exception) {
		this(null, exception, message);
	}

	public ScriptEnvironment getEnvironment() {
		return this.m_environment;
	}

	public String getFilename() {
		return this.m_filename;
	}

	public String getFragment() {
		return this.getOriginalString().substring(this.getOffset(), this.getOffset() + this.getLength());
	}

	public int getLength() {
		return this.m_length;
	}

	public int getLineNumber() {
		return this.m_lineNumber;
	}

	@Override
	public String getMessage() {
		return "(Internal Error) " + this.getName();
	}

	public String getName() {
		return this.m_message;
	}

	public int getOffset() {
		return this.m_offset;
	}

	public String getOriginalString() {
		return this.m_line;
	}

	public boolean isAnonymous() {
		return this.m_filename == null;
	}

	@Override
	public boolean nodificate() {
		boolean debug = false;
		assert debug = true;
		if (!debug) {
			return true;
		}
		assert Debugger.openNode("Exceptions and Errors", this.getMessage());
		if (this.m_object != null) {
			assert Debugger.addNode(this.m_object);
		}
		StringWriter writer;
		this.printStackTrace(new PrintWriter(writer = new StringWriter()));
		String[] messages = writer.toString().split("\n");
		boolean flag = false;
		int added = 0;
		for (int i = 0; i < messages.length; i++) {
			if (!flag && messages[i].trim().indexOf("at") == 0) {
				flag = true;
				assert Debugger.openNode("Call-stack");
			}
			if (flag && added == 5) {
				assert Debugger.openNode("Full Call-Stack");
			}
			if (messages[i].trim().indexOf("^") != 0) {
				assert Debugger.addNode(messages[i].trim());
			}
			if (flag) {
				added++;
			}
		}
		if (added > 5) {
			assert Debugger.closeNode();
		}
		if (flag) {
			assert Debugger.closeNode();
		}
		assert Debugger.closeNode();
		return true;
	}

	@Override
	public String toString() {
		if (this.m_object != null) {
			if (this.m_object instanceof Exception) {
				StringWriter writer;
				((Exception) this.m_object).printStackTrace(new PrintWriter(writer = new StringWriter()));
				return this.getMessage() + "\nObject given: " + this.m_object + "\n" + writer;
			} else {
				return this.getMessage() + "\nObject given: " + this.m_object;
			}
		}
		if (this.m_filename == null) {
			return this.getMessage();
		}
		while (this.m_line.indexOf("\t") == 0 || this.m_line.indexOf(" ") == 0) {
			this.m_line = this.m_line.substring(1);
			this.m_offset--;
			if (this.m_offset < 0) {
				this.m_offset = 0;
			}
		}
		String string = this.m_filename + ":" + this.m_lineNumber + ": " + this.getMessage() + "\n\t" + this.m_line;
		string += "\n\t";
		for (int i = 0; i < this.m_offset; i++) {
			string += " ";
		}
		string += "^";
		return string;
	}
}
