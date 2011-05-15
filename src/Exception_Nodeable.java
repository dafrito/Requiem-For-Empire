import java.io.*;
import java.util.List;
import java.util.Iterator;
public abstract class Exception_Nodeable extends Exception implements Nodeable{
	private int m_offset,m_lineNumber,m_length;
	private String m_line,m_filename;
	private final ScriptEnvironment m_environment;
	private final Object m_object;
	public Exception_Nodeable(Referenced ref){
		this(ref.getDebugReference().getEnvironment(),ref.getDebugReference());
	}
	public Exception_Nodeable(ScriptEnvironment env){
		m_environment=env;
		m_object=null;
		m_filename=null;
		m_line=null;
		m_offset=0;
		m_lineNumber=0;
		m_length=-1;
	}
	public Exception_Nodeable(ScriptEnvironment env,Object element){
		m_environment=env;
		m_object=element;
		m_filename=null;
		m_line=null;
		m_offset=0;
		m_lineNumber=-1;
		m_length=-1;
	}
	public Exception_Nodeable(ScriptEnvironment env,ScriptElement element){
		m_object=null;
		m_environment=env;
		if(element!=null){
			m_filename=element.getFilename();
			m_lineNumber=element.getLineNumber();
			m_line=element.getOriginalString();
			m_offset=element.getOffset();
			m_length=element.getLength();
		}else{
			m_offset=0;
			m_line=null;
			m_filename=null;
			m_lineNumber=-1;
			m_length=-1;
		}
	}
	public boolean isAnonymous(){return m_filename==null;}
	public String getOriginalString(){return m_line;}
	public int getLength(){return m_length;}
	public int getOffset(){return m_offset;}
	public String getFilename(){return m_filename;}
	public int getLineNumber(){return m_lineNumber;}
	public abstract String getName();
	public void getExtendedInformation(){}
	public String getMessage(){return "(Exception) "+getName();}
	public ScriptEnvironment getEnvironment(){return m_environment;}
	public String getFragment(){
		return getOriginalString().substring(getOffset(),getOffset()+getLength());
	}
	public boolean nodificate(){
		boolean debug=false;
		assert debug=true;
		if(!debug){return true;}
		Debugger.openNode("Exceptions and Errors",getName());
		if(m_object!=null){assert Debugger.addSnapNode("Reference",m_object);}
		getExtendedInformation();
		StringWriter writer;
		printStackTrace(new PrintWriter(writer=new StringWriter()));
		String[] messages=writer.toString().split("\n");
		boolean flag=false;
		int added=0;
		for(int i=0;i<messages.length;i++){
			if(!flag&&messages[i].trim().indexOf("at")==0){
				flag=true;
				assert Debugger.openNode("Call-stack");
			}
			if(flag&&added==5){assert Debugger.openNode("Full Call-Stack");}
			if(messages[i].trim().indexOf("^")!=0){
				assert Debugger.addNode(messages[i].trim());
			}
			if(flag){added++;}
		}
		if(added>5){assert Debugger.closeNode();}
		if(flag){assert Debugger.closeNode();}
		assert Debugger.closeNode();
		return true;
	}
	public String toString(){
		if(m_filename==null){return getMessage();}
		while(m_line.indexOf("\t")==0||m_line.indexOf(" ")==0){
			m_line=m_line.substring(1);
			m_offset--;
			if(m_offset<0){m_offset=0;}
		}
		String string=m_filename+":"+m_lineNumber+": "+getMessage()+"\n\t"+m_line;
		string += "\n\t";
		for(int i=0;i<m_offset;i++){
			string += " ";
		}
		string += "^";
		return string;
	}
}
// Variable-Type Exceptions
class Exception_Nodeable_VariableTypeAlreadyDefined extends Exception_Nodeable{
	private String m_type;
	public Exception_Nodeable_VariableTypeAlreadyDefined(Referenced ref, String type){
		super(ref);
		m_type=type;
	}
	public String getName(){return "Predefined Variable-Type ("+m_type+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The variable type, "+m_type+", has already been defined");}
}
class Exception_Nodeable_VariableTypeNotFound extends Exception_Nodeable{
	private String m_type;
	public Exception_Nodeable_VariableTypeNotFound(ScriptEnvironment env, String type){
		super(env);
		m_type=type;
	}
	public Exception_Nodeable_VariableTypeNotFound(Referenced ref, String type){
		super(ref);
		m_type=type;
	}
	public String getName(){return "Undefined Variable-Type ("+m_type+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The variable type, "+m_type+", was not found");}
}
// Variable Exceptions
class Exception_Nodeable_VariableAlreadyDefined extends Exception_Nodeable{
	private String m_name;
	private ScriptTemplate_Abstract m_template;
	public Exception_Nodeable_VariableAlreadyDefined(Referenced elem,ScriptTemplate_Abstract template,String name){
		super(elem);
		m_template=template;
		m_name=name;
	}
	public String getName(){return "Predefined Variable ("+m_name+")";}
	public void getExtendedInformation(){assert Debugger.addSnapNode("The variable, "+m_name+", has already been defined in the corresponding template",m_template);}
}
class Exception_Nodeable_VariableNotFound extends Exception_Nodeable{
	private String m_name;
	public Exception_Nodeable_VariableNotFound(Referenced ref,String name){
		super(ref);
		m_name=name;
	}
	public String getName(){return "Variable Not Found ("+m_name+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The variable, "+m_name+", was not found");}
}
class Exception_Nodeable_ClassCast extends Exception_Nodeable{
	private String m_value, m_castingValue;
	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract castingValue, ScriptValueType type)throws Exception_Nodeable{
		this(ref,type.getName(),type.getName());
	}
	public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract value,ScriptValue_Abstract castValue)throws Exception_Nodeable{
		this(ref,value.getType().getName(),castValue.getType().getName());
	}
	public Exception_Nodeable_ClassCast(Referenced ref, String type, String castType){
		super(ref.getEnvironment(),ref);
		m_value=type;
		m_castingValue=castType;
	}
	public String getName(){return "Casting Exception - Invalid conversion: " + m_castingValue + " --> " + m_value;}
}
class Exception_Nodeable_IncomparableObjects extends Exception_Nodeable{
	private ScriptValue_Abstract m_lhs,m_rhs;
	public Exception_Nodeable_IncomparableObjects(Referenced ref,ScriptValue_Abstract lhs, ScriptValue_Abstract rhs){
		super(ref);
		m_lhs=lhs;
		m_rhs=rhs;
	}
	public String getName(){return "Incomparable Objects Exception";}
	public void getExtendedInformation(){
		assert Debugger.addNode("The following two objects/primitives are incomparable.");
		assert Debugger.addNode(m_lhs);
		assert Debugger.addNode(m_rhs);
	}
}
class Exception_Nodeable_IncomparableObject extends Exception_Nodeable{
	public Exception_Nodeable_IncomparableObject(Referenced ref){
		super(ref);
	}
	public String getName(){return "This object has no inherent numeric value and is not directly comparable";}
}
// Function exception(s)
class Exception_Nodeable_FunctionNotFound extends Exception_Nodeable{
	private String m_name;
	private List m_params;
	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env,String name,List params){
		super(env);
		m_name=name;
		m_params=params;
	}
	public Exception_Nodeable_FunctionNotFound(Object ref, String name, List params){
		this(((Referenced)ref).getEnvironment(),ref,name,params);
	}
	public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env,Object ref, String name, List params){
		super(env,ref);
		m_name=name;
		m_params=params;
	}
	public String getName(){return "Function not found ("+ScriptFunction.getDisplayableFunctionName(m_name)+")";}
	public void getExtendedInformation(){
		assert Debugger.addNode("The function, "+ScriptFunction.getDisplayableFunctionName(m_name)+", was not found");
	}
}
class Exception_Nodeable_InvalidAbstractFunctionCall extends Exception_Nodeable{
	private ScriptTemplate_Abstract m_template;
	public Exception_Nodeable_InvalidAbstractFunctionCall(Referenced ref,ScriptTemplate_Abstract template){
		super(ref);
		m_template=template;
	}
	public String getName(){return "Invalid Abstract Function Call";}
	public void getExtendedInformation(){assert Debugger.addSnapNode("A call was made to an abstract function in this template",m_template);}
}
// Template exceptions
class Exception_Nodeable_InvalidColorRange extends Exception_Nodeable{
	private Number m_invalid;
	private FauxTemplate_Color m_template;
	public Exception_Nodeable_InvalidColorRange(FauxTemplate_Color template,Number num){
		super(template.getEnvironment());
		m_template=template;
		m_invalid=num;
	}
	public String getName(){return "Invalid Color Range ("+m_invalid+")";}
	public void getExtendedInformation(){
		assert Debugger.addNode("The number provided cannot be decoded to create a valid color ("+m_invalid+")");
		assert Debugger.addSnapNode("Template",m_template);
	}
}
class Exception_Nodeable_TemplateNotFound extends Exception_Nodeable{
	private String m_name;
	public Exception_Nodeable_TemplateNotFound(Referenced ref,String name){
		super(ref);
		m_name=name;
	}
	public String getName(){return "Template Not Found ("+m_name+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The template, "+m_name+", was not found");}
}
class Exception_Nodeable_IllegalAbstractObjectCreation extends Exception_Nodeable{
	public Exception_Nodeable_IllegalAbstractObjectCreation(Referenced ref){
		super(ref);
	}
	public String getName(){return "Illegal Abstract Object Creation";}
	public void getExtendedInformation(){assert Debugger.addNode("An abstract object is trying to be instantiated.");}
}
class Exception_Nodeable_TemplateAlreadyDefined extends Exception_Nodeable{
	private String m_name;
	public Exception_Nodeable_TemplateAlreadyDefined(Referenced ref,String name){
		super(ref);
		m_name=name;
	}
	public String getName(){return "Template Already Defined ("+m_name+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The template, "+m_name+", is already defined");}
}
class Exception_Nodeable_AbstractFunctionNotImplemented extends Exception_Nodeable{
	private ScriptTemplate_Abstract m_object;
	private ScriptFunction_Abstract m_function;
	public Exception_Nodeable_AbstractFunctionNotImplemented(Referenced ref,ScriptTemplate_Abstract object,ScriptFunction_Abstract function){
		super(ref);
		m_object=object;
		m_function=function;
	}
	public String getName(){return "Abstract Function Not Implememented";}
	public void getExtendedInformation(){
		assert Debugger.addSnapNode("The template is not abstract and does not implement the following function",m_object);
		assert Debugger.addNode(m_function);
	}
}
// Function Exceptions
class Exception_Nodeable_UnimplementedFunction extends Exception_Nodeable{
	private ScriptTemplate_Abstract m_template;
	private String m_name;
	public Exception_Nodeable_UnimplementedFunction(ScriptEnvironment env,ScriptTemplate_Abstract template,String name){
		super(env);
		m_template=template;
		m_name=name;
	}
	public String getName(){return "Unimplemented Abstract Function ("+m_name+")";}
	public void getExtendedInformation(){assert Debugger.addSnapNode("The abstract function, "+m_name+", is unimplemented",m_template);}
}
class Exception_Nodeable_FunctionAlreadyDefined extends Exception_Nodeable{
	private String m_name;
	public Exception_Nodeable_FunctionAlreadyDefined(Referenced ref, String name){
		super(ref);
		m_name=name;
	}
	public String getName(){return "Function Already Defined ("+ScriptFunction.getDisplayableFunctionName(m_name)+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The function, "+ScriptFunction.getDisplayableFunctionName(m_name)+", is already defined");}
}
class Exception_Nodeable_IllegalNullReturnValue extends Exception_Nodeable{
	private ScriptFunction_Abstract m_function;
	public Exception_Nodeable_IllegalNullReturnValue(ScriptEnvironment env, ScriptFunction_Abstract fxn){
		super(env);
		m_function=fxn;
	}
	public Exception_Nodeable_IllegalNullReturnValue(Referenced ref, ScriptFunction_Abstract fxn){
		super(ref);
		m_function=fxn;
	}
	public String getName(){return "Illegal Null Return Value";}
	public void getExtendedInformation(){assert Debugger.addSnapNode("This function is attempting to return implicitly, even though it is of type, "+m_function.getReturnType(),m_function);}
}
// General Parsing Failure Exceptions
class Exception_Nodeable_UnexpectedType extends Exception_Nodeable{
	private String m_expectedType;
	private Object m_providedType;
	public Exception_Nodeable_UnexpectedType(ScriptEnvironment env, Object provided, String exp){
		super(env);
		m_providedType=provided;
		m_expectedType=exp;
	}
	public Exception_Nodeable_UnexpectedType(Referenced ref, Object provided, String exp){
		super(ref);
		m_providedType=provided;
		m_expectedType=exp;
	}
	public Exception_Nodeable_UnexpectedType(Referenced provided,String expectedType){
		super(provided.getEnvironment(),provided);
		m_expectedType=expectedType;
		m_providedType=provided;
	}
	public String getName(){return "Unexpected Type ("+m_providedType+")";}
	public void getExtendedInformation(){assert Debugger.addNode("The type or keyword, "+m_providedType+", is unexpected here ("+m_expectedType+" is expected)");}
}
class Exception_Nodeable_UnknownModifier extends Exception_Nodeable{
	private List<Object>m_modifiers;
	public Exception_Nodeable_UnknownModifier(Referenced ref,List<Object>modifiers){
		super(ref);
		m_modifiers=modifiers;
	}
	public String getName(){return "Unknown Modifier(s)";}
	public void getExtendedInformation(){assert Debugger.addSnapNode("These modifiers (or what are believed to be modifiers) are unparseable to the compiler",m_modifiers);}
}
class Exception_Nodeable_UnparseableElement extends Exception_Nodeable{
	private String m_source;
	public Exception_Nodeable_UnparseableElement(Referenced ref,String thrownFrom){
		super(ref);
		m_source=thrownFrom;
	}
	public String getName(){return "Unparseable Element";}
	public void getExtendedInformation(){assert Debugger.addNode("A syntax error has occurred here, or near here, and the script is unparseable (Thrown from: " +m_source+")");}
}
// Uncategorized Exceptions
class Exception_Nodeable_DivisionByZero extends Exception_Nodeable{
	public Exception_Nodeable_DivisionByZero(Referenced ref){
		super(ref.getEnvironment(),ref);
	}
	public String getName(){return "Division by Zero";}
	public void getExtendedInformation(){assert Debugger.addNode("Illegal mindfucking division by zero was encountered.");}
}
class Exception_Nodeable_FileNotFound extends Exception_Nodeable{
	private String m_name;
	public Exception_Nodeable_FileNotFound(ScriptEnvironment env,String name){
		super(env);
		m_name=name;
	}
	public String getName(){return "File Not Found ("+m_name+")";}
}
class Exception_Nodeable_UnenclosedBracket extends Exception_Nodeable{
	public Exception_Nodeable_UnenclosedBracket(Referenced elem){
		super(elem);
	}
	public String getName(){return "Unenclosed Bracket";}
}
class Exception_Nodeable_UnenclosedStringLiteral extends Exception_Nodeable{
	public Exception_Nodeable_UnenclosedStringLiteral(Referenced elem){
		super(elem);
	}
	public String getName(){return "Unenclosed String";}
}
