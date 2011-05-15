import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public abstract class Exception_Nodeable extends Exception implements Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8671870175447120460L;
	private int offset, lineNumber, length;
	private String line, filename;
	private final ScriptEnvironment environment;
	private final Object object;

	public Exception_Nodeable(Referenced ref) {
		this(ref.getDebugReference().getEnvironment(), ref.getDebugReference());
	}

	public Exception_Nodeable(ScriptEnvironment env) {
		this.environment = env;
		this.object = null;
		this.filename = null;
		this.line = null;
		this.offset = 0;
		this.lineNumber = 0;
		this.length = -1;
	}

	public Exception_Nodeable(ScriptEnvironment env, Object element) {
		this.environment = env;
		this.object = element;
		this.filename = null;
		this.line = null;
		this.offset = 0;
		this.lineNumber = -1;
		this.length = -1;
	}

	public Exception_Nodeable(ScriptEnvironment env, ScriptElement element) {
		this.object = null;
		this.environment = env;
		if (element != null) {
			this.filename = element.getFilename();
			this.lineNumber = element.getLineNumber();
			this.line = element.getOriginalString();
			this.offset = element.getOffset();
			this.length = element.getLength();
		} else {
			this.offset = 0;
			this.line = null;
			this.filename = null;
			this.lineNumber = -1;
			this.length = -1;
		}
	}

	public ScriptEnvironment getEnvironment() {
		return this.environment;
	}

	public void getExtendedInformation() {
	}

	public String getFilename() {
		return this.filename;
	}

	public String getFragment() {
		return this.getOriginalString().substring(this.getOffset(), this.getOffset() + this.getLength());
	}

	public int getLength() {
		return this.length;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	@Override
	public String getMessage() {
		return "(Exception) " + this.getName();
	}

	public abstract String getName();

	public int getOffset() {
		return this.offset;
	}

	public String getOriginalString() {
		return this.line;
	}

	public boolean isAnonymous() {
		return this.filename == null;
	}

	@Override
	public boolean nodificate() {
		boolean debug = false;
		assert debug = true;
		if (!debug) {
			return true;
		}
		Debugger.openNode("Exceptions and Errors", this.getName());
		if (this.object != null) {
			assert Debugger.addSnapNode("Reference", this.object);
		}
		this.getExtendedInformation();
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
		if (this.filename == null) {
			return this.getMessage();
		}
		while (this.line.indexOf("\t") == 0 || this.line.indexOf(" ") == 0) {
			this.line = this.line.substring(1);
			this.offset--;
			if (this.offset < 0) {
				this.offset = 0;
			}
		}
		String string = this.filename + ":" + this.lineNumber + ": " + this.getMessage() + "\n\t" + this.line;
		string += "\n\t";
		for (int i = 0; i < this.offset; i++) {
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
	private ScriptTemplate_Abstract object;
	private ScriptFunction_Abstract function;

	public Exception_Nodeable_AbstractFunctionNotImplemented(Referenced ref, ScriptTemplate_Abstract object, ScriptFunction_Abstract function) {
		super(ref);
		this.object = object;
		this.function = function;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The template is not abstract and does not implement the following function", this.object);
		assert Debugger.addNode(this.function);
	}

	@Override
	public String getName() {
		return "Abstract Function Not Implememented";
	}
}

class Exception_Nodeable_ClassCast extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 375067664989244754L;
	private String value, castingValue;

	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract value, ScriptValue_Abstract castValue) throws Exception_Nodeable {
		this(ref, value.getType().getName(), castValue.getType().getName());
	}

	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract castingValue, ScriptValueType type) throws Exception_Nodeable {
		this(ref, type.getName(), type.getName());
	}

	public Exception_Nodeable_ClassCast(Referenced ref, String type, String castType) {
		super(ref.getEnvironment(), ref);
		this.value = type;
		this.castingValue = castType;
	}

	@Override
	public String getName() {
		return "Casting Exception - Invalid conversion: " + this.castingValue + " --> " + this.value;
	}
}

// Uncategorized Exceptions
class Exception_Nodeable_DivisionByZero extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3401425743716487200L;

	public Exception_Nodeable_DivisionByZero(Referenced ref) {
		super(ref.getEnvironment(), ref);
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("Illegal mindfucking division by zero was encountered.");
	}

	@Override
	public String getName() {
		return "Division by Zero";
	}
}

class Exception_Nodeable_FileNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2126754108417225024L;
	private String name;

	public Exception_Nodeable_FileNotFound(ScriptEnvironment env, String name) {
		super(env);
		this.name = name;
	}

	@Override
	public String getName() {
		return "File Not Found (" + this.name + ")";
	}
}

class Exception_Nodeable_FunctionAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6734795857087945843L;
	private String name;

	public Exception_Nodeable_FunctionAlreadyDefined(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + ScriptFunction.getDisplayableFunctionName(this.name) + ", is already defined");
	}

	@Override
	public String getName() {
		return "Function Already Defined (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")";
	}
}

// Function exception(s)
class Exception_Nodeable_FunctionNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4051248649703169850L;
	private String name;
	private List params;

	public Exception_Nodeable_FunctionNotFound(Object ref, String name, List params) {
		this(((Referenced) ref).getEnvironment(), ref, name, params);
	}

	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, Object ref, String name, List params) {
		super(env, ref);
		this.name = name;
		this.params = params;
	}

	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, String name, List params) {
		super(env);
		this.name = name;
		this.params = params;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The function, " + ScriptFunction.getDisplayableFunctionName(this.name) + ", was not found");
	}

	@Override
	public String getName() {
		return "Function not found (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")";
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

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("An abstract object is trying to be instantiated.");
	}

	@Override
	public String getName() {
		return "Illegal Abstract Object Creation";
	}
}

class Exception_Nodeable_IllegalNullReturnValue extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6453503758041260366L;
	private ScriptFunction_Abstract function;

	public Exception_Nodeable_IllegalNullReturnValue(Referenced ref, ScriptFunction_Abstract fxn) {
		super(ref);
		this.function = fxn;
	}

	public Exception_Nodeable_IllegalNullReturnValue(ScriptEnvironment env, ScriptFunction_Abstract fxn) {
		super(env);
		this.function = fxn;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("This function is attempting to return implicitly, even though it is of type, " + this.function.getReturnType(), this.function);
	}

	@Override
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

	@Override
	public String getName() {
		return "This object has no inherent numeric value and is not directly comparable";
	}
}

class Exception_Nodeable_IncomparableObjects extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1863802775557161344L;
	private ScriptValue_Abstract lhs, rhs;

	public Exception_Nodeable_IncomparableObjects(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs) {
		super(ref);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The following two objects/primitives are incomparable.");
		assert Debugger.addNode(this.lhs);
		assert Debugger.addNode(this.rhs);
	}

	@Override
	public String getName() {
		return "Incomparable Objects Exception";
	}
}

class Exception_Nodeable_InvalidAbstractFunctionCall extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7118648667674833605L;
	private ScriptTemplate_Abstract template;

	public Exception_Nodeable_InvalidAbstractFunctionCall(Referenced ref, ScriptTemplate_Abstract template) {
		super(ref);
		this.template = template;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("A call was made to an abstract function in this template", this.template);
	}

	@Override
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
	private Number invalid;
	private FauxTemplate_Color template;

	public Exception_Nodeable_InvalidColorRange(FauxTemplate_Color template, Number num) {
		super(template.getEnvironment());
		this.template = template;
		this.invalid = num;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The number provided cannot be decoded to create a valid color (" + this.invalid + ")");
		assert Debugger.addSnapNode("Template", this.template);
	}

	@Override
	public String getName() {
		return "Invalid Color Range (" + this.invalid + ")";
	}
}

class Exception_Nodeable_TemplateAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7184264414947646959L;
	private String name;

	public Exception_Nodeable_TemplateAlreadyDefined(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The template, " + this.name + ", is already defined");
	}

	@Override
	public String getName() {
		return "Template Already Defined (" + this.name + ")";
	}
}

class Exception_Nodeable_TemplateNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6044178726296381294L;
	private String name;

	public Exception_Nodeable_TemplateNotFound(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The template, " + this.name + ", was not found");
	}

	@Override
	public String getName() {
		return "Template Not Found (" + this.name + ")";
	}
}

class Exception_Nodeable_UnenclosedBracket extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4450711851110352808L;

	public Exception_Nodeable_UnenclosedBracket(Referenced elem) {
		super(elem);
	}

	@Override
	public String getName() {
		return "Unenclosed Bracket";
	}
}

class Exception_Nodeable_UnenclosedStringLiteral extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7575185880647475669L;

	public Exception_Nodeable_UnenclosedStringLiteral(Referenced elem) {
		super(elem);
	}

	@Override
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
	private String expectedType;
	private Object providedType;

	public Exception_Nodeable_UnexpectedType(Referenced ref, Object provided, String exp) {
		super(ref);
		this.providedType = provided;
		this.expectedType = exp;
	}

	public Exception_Nodeable_UnexpectedType(Referenced provided, String expectedType) {
		super(provided.getEnvironment(), provided);
		this.expectedType = expectedType;
		this.providedType = provided;
	}

	public Exception_Nodeable_UnexpectedType(ScriptEnvironment env, Object provided, String exp) {
		super(env);
		this.providedType = provided;
		this.expectedType = exp;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("The type or keyword, " + this.providedType + ", is unexpected here (" + this.expectedType + " is expected)");
	}

	@Override
	public String getName() {
		return "Unexpected Type (" + this.providedType + ")";
	}
}

// Function Exceptions
class Exception_Nodeable_UnimplementedFunction extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8027051981016874329L;
	private ScriptTemplate_Abstract template;
	private String name;

	public Exception_Nodeable_UnimplementedFunction(ScriptEnvironment env, ScriptTemplate_Abstract template, String name) {
		super(env);
		this.template = template;
		this.name = name;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The abstract function, " + this.name + ", is unimplemented", this.template);
	}

	@Override
	public String getName() {
		return "Unimplemented Abstract Function (" + this.name + ")";
	}
}

class Exception_Nodeable_UnknownModifier extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7862513783807915122L;
	private List<Object> modifiers;

	public Exception_Nodeable_UnknownModifier(Referenced ref, List<Object> modifiers) {
		super(ref);
		this.modifiers = modifiers;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addSnapNode("These modifiers (or what are believed to be modifiers) are unparseable to the compiler", this.modifiers);
	}

	@Override
	public String getName() {
		return "Unknown Modifier(s)";
	}
}

class Exception_Nodeable_UnparseableElement extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7815806790954972711L;
	private String source;

	public Exception_Nodeable_UnparseableElement(Referenced ref, String thrownFrom) {
		super(ref);
		this.source = thrownFrom;
	}

	@Override
	public void getExtendedInformation() {
		assert Debugger.addNode("A syntax error has occurred here, or near here, and the script is unparseable (Thrown from: " + this.source + ")");
	}

	@Override
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
	private String name;
	private ScriptTemplate_Abstract template;

	public Exception_Nodeable_VariableAlreadyDefined(Referenced elem, ScriptTemplate_Abstract template, String name) {
		super(elem);
		this.template = template;
		this.name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addSnapNode("The variable, " + this.name + ", has already been defined in the corresponding template", this.template);
	}

	public String getName() {
		return "Predefined Variable (" + this.name + ")";
	}
}

class Exception_Nodeable_VariableNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3774953699349441078L;
	private String name;

	public Exception_Nodeable_VariableNotFound(Referenced ref, String name) {
		super(ref);
		this.name = name;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The variable, " + this.name + ", was not found");
	}

	public String getName() {
		return "Variable Not Found (" + this.name + ")";
	}
}

// Variable-Type Exceptions
class Exception_Nodeable_VariableTypeAlreadyDefined extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3105141324838401870L;
	private String type;

	public Exception_Nodeable_VariableTypeAlreadyDefined(Referenced ref, String type) {
		super(ref);
		this.type = type;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The variable type, " + this.type + ", has already been defined");
	}

	public String getName() {
		return "Predefined Variable-Type (" + this.type + ")";
	}
}

class Exception_Nodeable_VariableTypeNotFound extends Exception_Nodeable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6495019820246931939L;
	private String type;

	public Exception_Nodeable_VariableTypeNotFound(Referenced ref, String type) {
		super(ref);
		this.type = type;
	}

	public Exception_Nodeable_VariableTypeNotFound(ScriptEnvironment env, String type) {
		super(env);
		this.type = type;
	}

	public void getExtendedInformation() {
		assert Debugger.addNode("The variable type, " + this.type + ", was not found");
	}

	public String getName() {
		return "Undefined Variable-Type (" + this.type + ")";
	}
}
