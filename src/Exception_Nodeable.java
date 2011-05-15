import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public abstract class Exception_Nodeable extends Exception implements Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8671870175447120460L;
	private int m_offset, m_lineNumber, m_length;
	private String m_line, m_filename;
	private final ScriptEnvironment m_environment;
	private final Object m_object;

	public Exception_Nodeable(Referenced ref) {
		this(ref.getDebugReference().getEnvironment(), ref.getDebugReference());
	}

	public Exception_Nodeable(ScriptEnvironment env) {
		m_environment = env;
		m_object = null;
		m_filename = null;
		m_line = null;
		m_offset = 0;
		m_lineNumber = 0;
		m_length = -1;
	}

	public Exception_Nodeable(ScriptEnvironment env, Object element) {
		m_environment = env;
		m_object = element;
		m_filename = null;
		m_line = null;
		m_offset = 0;
		m_lineNumber = -1;
		m_length = -1;
	}

	public Exception_Nodeable(ScriptEnvironment env, ScriptElement element) {
		m_object = null;
		m_environment = env;
		if (element != null) {
			m_filename = element.getFilename();
			m_lineNumber = element.getLineNumber();
			m_line = element.getOriginalString();
			m_offset = element.getOffset();
			m_length = element.getLength();
		} else {
			m_offset = 0;
			m_line = null;
			m_filename = null;
			m_lineNumber = -1;
			m_length = -1;
		}
	}

	public ScriptEnvironment getEnvironment() {
		return m_environment;
	}

	public void getExtendedInformation() {
	}

	public String getFilename() {
		return m_filename;
	}

	public String getFragment() {
		return getOriginalString().substring(getOffset(), getOffset() + getLength());
	}

	public int getLength() {
		return m_length;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}

	public String getMessage() {
		return "(Exception) " + getName();
	}

	public abstract String getName();

	public int getOffset() {
		return m_offset;
	}

	public String getOriginalString() {
		return m_line;
	}

	public boolean isAnonymous() {
		return m_filename == null;
	}

	public boolean nodificate() {
		boolean debug = false;
		assert debug = true;
		if (!debug) {
			return true;
		}
		Debugger.openNode("Exceptions and Errors", getName());
		if (m_object != null) {
			assert Debugger.addSnapNode("Reference", m_object);
		}
		getExtendedInformation();
		StringWriter writer;
		printStackTrace(new PrintWriter(writer = new StringWriter()));
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

	public String toString() {
		if (m_filename == null) {
			return getMessage();
		}
		while (m_line.indexOf("\t") == 0 || m_line.indexOf(" ") == 0) {
			m_line = m_line.substring(1);
			m_offset--;
			if (m_offset < 0) {
				m_offset = 0;
			}
		}
		String string = m_filename + ":" + m_lineNumber + ": " + getMessage() + "\n\t" + m_line;
		string += "\n\t";
		for (int i = 0; i < m_offset; i++) {
			string += " ";
		}
		string += "^";
		return string;
	}
}

class Exception_Nodeable_AbstractFunctionNotImplemented extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8937024716489715221L;
	private ScriptTemplate_Abstract m_object;
	private ScriptFunction_Abstract m_function;

	public Exception_Nodeable_AbstractFunctionNotImplemented(Referenced ref, ScriptTemplate_Abstract object, ScriptFunction_Abstract function) {
		super(ref);
		m_object = object;
		m_function = function;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The template is not abstract and does not implement the following function", m_object);
		assert Debugger.addNode(m_function);
	}

	public String getName() {
		return "Abstract Function Not Implememented";
	}
}

class Exception_Nodeable_ClassCast extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 375067664989244754L;
	private String m_value, m_castingValue;

	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract value, ScriptValue_Abstract castValue) throws Exception_Nodeable {
		this(ref, value.getType().getName(), castValue.getType().getName());
	}

	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract castingValue, ScriptValueType type) throws Exception_Nodeable {
		this(ref, type.getName(), type.getName());
	}

	public Exception_Nodeable_ClassCast(Referenced ref, String type, String castType) {
		super(ref.getEnvironment(), ref);
		m_value = type;
		m_castingValue = castType;
	}

	public String getName() {
		return "Casting Exception - Invalid conversion: " + m_castingValue + " --> " + m_value;
	}
}

// Uncategorized Exceptions
class Exception_Nodeable_DivisionByZero extends Exception_Nodeable {
	public Exception_Nodeable_DivisionByZero(Referenced ref) {
		super(ref.getEnvironment(), ref);
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("Illegal mindfucking division by zero was encountered.");
	}

	public String getName() {
		return "Division by Zero";
	}
}

class Exception_Nodeable_FileNotFound extends Exception_Nodeable {
	private String m_name;

	public Exception_Nodeable_FileNotFound(ScriptEnvironment env, String name) {
		super(env);
		m_name = name;
	}

	public String getName() {
		return "File Not Found (" + m_name + ")";
	}
}

class Exception_Nodeable_FunctionAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734795857087945843L;
	private String m_name;

	public Exception_Nodeable_FunctionAlreadyDefined(Referenced ref, String name) {
		super(ref);
		m_name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + ScriptFunction.getDisplayableFunctionName(m_name) + ", is already defined");
	}

	public String getName() {
		return "Function Already Defined (" + ScriptFunction.getDisplayableFunctionName(m_name) + ")";
	}
}

// Function exception(s)
class Exception_Nodeable_FunctionNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4051248649703169850L;
	private String m_name;
	private List m_params;

	public Exception_Nodeable_FunctionNotFound(Object ref, String name, List params) {
		this(((Referenced) ref).getEnvironment(), ref, name, params);
	}

	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, Object ref, String name, List params) {
		super(env, ref);
		m_name = name;
		m_params = params;
	}

	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, String name, List params) {
		super(env);
		m_name = name;
		m_params = params;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + ScriptFunction.getDisplayableFunctionName(m_name) + ", was not found");
	}

	public String getName() {
		return "Function not found (" + ScriptFunction.getDisplayableFunctionName(m_name) + ")";
	}
}

class Exception_Nodeable_IllegalAbstractObjectCreation extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2947890390494988260L;

	public Exception_Nodeable_IllegalAbstractObjectCreation(Referenced ref) {
		super(ref);
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("An abstract object is trying to be instantiated.");
	}

	public String getName() {
		return "Illegal Abstract Object Creation";
	}
}

class Exception_Nodeable_IllegalNullReturnValue extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6453503758041260366L;
	private ScriptFunction_Abstract m_function;

	public Exception_Nodeable_IllegalNullReturnValue(Referenced ref, ScriptFunction_Abstract fxn) {
		super(ref);
		m_function = fxn;
	}

	public Exception_Nodeable_IllegalNullReturnValue(ScriptEnvironment env, ScriptFunction_Abstract fxn) {
		super(env);
		m_function = fxn;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("This function is attempting to return implicitly, even though it is of type, " + m_function.getReturnType(), m_function);
	}

	public String getName() {
		return "Illegal Null Return Value";
	}
}

class Exception_Nodeable_IncomparableObject extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7488728539343927636L;

	public Exception_Nodeable_IncomparableObject(Referenced ref) {
		super(ref);
	}

	public String getName() {
		return "This object has no inherent numeric value and is not directly comparable";
	}
}

class Exception_Nodeable_IncomparableObjects extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1863802775557161344L;
	private ScriptValue_Abstract m_lhs, m_rhs;

	public Exception_Nodeable_IncomparableObjects(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs) {
		super(ref);
		m_lhs = lhs;
		m_rhs = rhs;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The following two objects/primitives are incomparable.");
		assert Debugger.addNode(m_lhs);
		assert Debugger.addNode(m_rhs);
	}

	public String getName() {
		return "Incomparable Objects Exception";
	}
}

class Exception_Nodeable_InvalidAbstractFunctionCall extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7118648667674833605L;
	private ScriptTemplate_Abstract m_template;

	public Exception_Nodeable_InvalidAbstractFunctionCall(Referenced ref, ScriptTemplate_Abstract template) {
		super(ref);
		m_template = template;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("A call was made to an abstract function in this template", m_template);
	}

	public String getName() {
		return "Invalid Abstract Function Call";
	}
}

// Template exceptions
class Exception_Nodeable_InvalidColorRange extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7383423633507695338L;
	private Number m_invalid;
	private FauxTemplate_Color m_template;

	public Exception_Nodeable_InvalidColorRange(FauxTemplate_Color template, Number num) {
		super(template.getEnvironment());
		m_template = template;
		m_invalid = num;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The number provided cannot be decoded to create a valid color (" + m_invalid + ")");
		assert Debugger.addSnapNode("Template", m_template);
	}

	public String getName() {
		return "Invalid Color Range (" + m_invalid + ")";
	}
}

class Exception_Nodeable_TemplateAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7184264414947646959L;
	private String m_name;

	public Exception_Nodeable_TemplateAlreadyDefined(Referenced ref, String name) {
		super(ref);
		m_name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The template, " + m_name + ", is already defined");
	}

	public String getName() {
		return "Template Already Defined (" + m_name + ")";
	}
}

class Exception_Nodeable_TemplateNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6044178726296381294L;
	private String m_name;

	public Exception_Nodeable_TemplateNotFound(Referenced ref, String name) {
		super(ref);
		m_name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The template, " + m_name + ", was not found");
	}

	public String getName() {
		return "Template Not Found (" + m_name + ")";
	}
}

class Exception_Nodeable_UnenclosedBracket extends Exception_Nodeable {
	public Exception_Nodeable_UnenclosedBracket(Referenced elem) {
		super(elem);
	}

	public String getName() {
		return "Unenclosed Bracket";
	}
}

class Exception_Nodeable_UnenclosedStringLiteral extends Exception_Nodeable {
	public Exception_Nodeable_UnenclosedStringLiteral(Referenced elem) {
		super(elem);
	}

	public String getName() {
		return "Unenclosed String";
	}
}

// General Parsing Failure Exceptions
class Exception_Nodeable_UnexpectedType extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7758477287327463222L;
	private String m_expectedType;
	private Object m_providedType;

	public Exception_Nodeable_UnexpectedType(Referenced ref, Object provided, String exp) {
		super(ref);
		m_providedType = provided;
		m_expectedType = exp;
	}

	public Exception_Nodeable_UnexpectedType(Referenced provided, String expectedType) {
		super(provided.getEnvironment(), provided);
		m_expectedType = expectedType;
		m_providedType = provided;
	}

	public Exception_Nodeable_UnexpectedType(ScriptEnvironment env, Object provided, String exp) {
		super(env);
		m_providedType = provided;
		m_expectedType = exp;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The type or keyword, " + m_providedType + ", is unexpected here (" + m_expectedType + " is expected)");
	}

	public String getName() {
		return "Unexpected Type (" + m_providedType + ")";
	}
}

// Function Exceptions
class Exception_Nodeable_UnimplementedFunction extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8027051981016874329L;
	private ScriptTemplate_Abstract m_template;
	private String m_name;

	public Exception_Nodeable_UnimplementedFunction(ScriptEnvironment env, ScriptTemplate_Abstract template, String name) {
		super(env);
		m_template = template;
		m_name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The abstract function, " + m_name + ", is unimplemented", m_template);
	}

	public String getName() {
		return "Unimplemented Abstract Function (" + m_name + ")";
	}
}

class Exception_Nodeable_UnknownModifier extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7862513783807915122L;
	private List<Object> m_modifiers;

	public Exception_Nodeable_UnknownModifier(Referenced ref, List<Object> modifiers) {
		super(ref);
		m_modifiers = modifiers;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("These modifiers (or what are believed to be modifiers) are unparseable to the compiler", m_modifiers);
	}

	public String getName() {
		return "Unknown Modifier(s)";
	}
}

class Exception_Nodeable_UnparseableElement extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7815806790954972711L;
	private String m_source;

	public Exception_Nodeable_UnparseableElement(Referenced ref, String thrownFrom) {
		super(ref);
		m_source = thrownFrom;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("A syntax error has occurred here, or near here, and the script is unparseable (Thrown from: " + m_source + ")");
	}

	public String getName() {
		return "Unparseable Element";
	}
}

// Variable Exceptions
class Exception_Nodeable_VariableAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8437355990865082053L;
	private String m_name;
	private ScriptTemplate_Abstract m_template;

	public Exception_Nodeable_VariableAlreadyDefined(Referenced elem, ScriptTemplate_Abstract template, String name) {
		super(elem);
		m_template = template;
		m_name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The variable, " + m_name + ", has already been defined in the corresponding template", m_template);
	}

	public String getName() {
		return "Predefined Variable (" + m_name + ")";
	}
}

class Exception_Nodeable_VariableNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3774953699349441078L;
	private String m_name;

	public Exception_Nodeable_VariableNotFound(Referenced ref, String name) {
		super(ref);
		m_name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The variable, " + m_name + ", was not found");
	}

	public String getName() {
		return "Variable Not Found (" + m_name + ")";
	}
}

// Variable-Type Exceptions
class Exception_Nodeable_VariableTypeAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3105141324838401870L;
	private String m_type;

	public Exception_Nodeable_VariableTypeAlreadyDefined(Referenced ref, String type) {
		super(ref);
		m_type = type;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The variable type, " + m_type + ", has already been defined");
	}

	public String getName() {
		return "Predefined Variable-Type (" + m_type + ")";
	}
}

class Exception_Nodeable_VariableTypeNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6495019820246931939L;
	private String m_type;

	public Exception_Nodeable_VariableTypeNotFound(Referenced ref, String type) {
		super(ref);
		m_type = type;
	}

	public Exception_Nodeable_VariableTypeNotFound(ScriptEnvironment env, String type) {
		super(env);
		m_type = type;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The variable type, " + m_type + ", was not found");
	}

	public String getName() {
		return "Undefined Variable-Type (" + m_type + ")";
	}
}
