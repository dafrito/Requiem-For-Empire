package com.dafrito.debug;

import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.script.Referenced;
import com.dafrito.script.ScriptEnvironment;
import com.dafrito.script.ScriptFunction;
import com.dafrito.script.ScriptFunction_Abstract;
import com.dafrito.script.ScriptTemplate_Abstract;
import com.dafrito.script.templates.FauxTemplate_Color;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public interface Exceptions {
    
    public static class Exception_Nodeable_VariableTypeAlreadyDefined extends Exception_Nodeable {
        private String type;

        public Exception_Nodeable_VariableTypeAlreadyDefined(Referenced ref, String type) {
            super(ref);
            this.type = type;
        }

        @Override
        public String getName() {
            return "Predefined Variable-Type (" + this.type + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The variable type, " + this.type + ", has already been defined");
        }
    }

    public static class Exception_Nodeable_VariableTypeNotFound extends Exception_Nodeable {
        private String type;

        public Exception_Nodeable_VariableTypeNotFound(ScriptEnvironment env, String type) {
            super(env);
            this.type = type;
        }

        public Exception_Nodeable_VariableTypeNotFound(Referenced ref, String type) {
            super(ref);
            this.type = type;
        }

        @Override
        public String getName() {
            return "Undefined Variable-Type (" + this.type + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The variable type, " + this.type + ", was not found");
        }
    }

    // Variable Exceptions
    public static class Exception_Nodeable_VariableAlreadyDefined extends Exception_Nodeable {
        private String name;
        private ScriptTemplate_Abstract template;

        public Exception_Nodeable_VariableAlreadyDefined(Referenced elem, ScriptTemplate_Abstract template, String name) {
            super(elem);
            this.template = template;
            this.name = name;
        }

        @Override
        public String getName() {
            return "Predefined Variable (" + this.name + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addSnapNode("The variable, "
                + this.name
                + ", has already been defined in the corresponding template", this.template);
        }
    }

    public static class Exception_Nodeable_VariableNotFound extends Exception_Nodeable {
        private String name;

        public Exception_Nodeable_VariableNotFound(Referenced ref, String name) {
            super(ref);
            this.name = name;
        }

        @Override
        public String getName() {
            return "Variable Not Found (" + this.name + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The variable, " + this.name + ", was not found");
        }
    }

    public static class Exception_Nodeable_ClassCast extends Exception_Nodeable {
        private String value, castingValue;

        public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract castingValue, ScriptValueType type) {
            this(ref, type.getName(), castingValue.getType().getName());
        }

        public Exception_Nodeable_ClassCast(Referenced ref, ScriptValue_Abstract value, ScriptValue_Abstract castValue) {
            this(ref, value.getType().getName(), castValue.getType().getName());
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

    public static class Exception_Nodeable_IncomparableObjects extends Exception_Nodeable {
        private ScriptValue_Abstract lhs, rhs;

        public Exception_Nodeable_IncomparableObjects(Referenced ref, ScriptValue_Abstract lhs, ScriptValue_Abstract rhs) {
            super(ref);
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public String getName() {
            return "Incomparable Objects Exception";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The following two objects/primitives are incomparable.");
            assert LegacyDebugger.addNode(this.lhs);
            assert LegacyDebugger.addNode(this.rhs);
        }
    }

    public static class Exception_Nodeable_IncomparableObject extends Exception_Nodeable {
        public Exception_Nodeable_IncomparableObject(Referenced ref) {
            super(ref);
        }

        @Override
        public String getName() {
            return "This object has no inherent numeric value and is not directly comparable";
        }
    }

    // Function exception(s)
    public static class Exception_Nodeable_FunctionNotFound extends Exception_Nodeable {
        private final String name;
        private final List<ScriptValue_Abstract> params;

        public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, String name, List<ScriptValue_Abstract> params) {
            super(env);
            this.name = name;
            this.params = params;
        }

        public Exception_Nodeable_FunctionNotFound(Object ref, String name, List<ScriptValue_Abstract> params) {
            this(((Referenced)ref).getEnvironment(), ref, name, params);
        }

        public Exception_Nodeable_FunctionNotFound(ScriptEnvironment env, Object ref, String name,
            List<ScriptValue_Abstract> params) {
            super(env, ref);
            this.name = name;
            this.params = params;
        }

        @Override
        public String getName() {
            return "Function not found (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The function, "
                + ScriptFunction.getDisplayableFunctionName(this.name)
                + ", was not found");
        }

        public List<ScriptValue_Abstract> getParams() {
            return this.params;
        }
    }

    public static class Exception_Nodeable_InvalidAbstractFunctionCall extends Exception_Nodeable {
        private ScriptTemplate_Abstract template;

        public Exception_Nodeable_InvalidAbstractFunctionCall(Referenced ref, ScriptTemplate_Abstract template) {
            super(ref);
            this.template = template;
        }

        @Override
        public String getName() {
            return "Invalid Abstract Function Call";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addSnapNode("A call was made to an abstract function in this template", this.template);
        }
    }

    // Template exceptions
    public static class Exception_Nodeable_InvalidColorRange extends Exception_Nodeable {
        private Number invalid;
        private FauxTemplate_Color template;

        public Exception_Nodeable_InvalidColorRange(FauxTemplate_Color template, Number num) {
            super(template.getEnvironment());
            this.template = template;
            this.invalid = num;
        }

        @Override
        public String getName() {
            return "Invalid Color Range (" + this.invalid + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The number provided cannot be decoded to create a valid color ("
                + this.invalid
                + ")");
            assert LegacyDebugger.addSnapNode("Template", this.template);
        }
    }

    public static class Exception_Nodeable_TemplateNotFound extends Exception_Nodeable {
        private String name;

        public Exception_Nodeable_TemplateNotFound(Referenced ref, String name) {
            super(ref);
            this.name = name;
        }

        @Override
        public String getName() {
            return "Template Not Found (" + this.name + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The template, " + this.name + ", was not found");
        }
    }

    public static class Exception_Nodeable_IllegalAbstractObjectCreation extends Exception_Nodeable {
        public Exception_Nodeable_IllegalAbstractObjectCreation(Referenced ref) {
            super(ref);
        }

        @Override
        public String getName() {
            return "Illegal Abstract Object Creation";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("An abstract object is trying to be instantiated.");
        }
    }

    public static class Exception_Nodeable_TemplateAlreadyDefined extends Exception_Nodeable {
        private String name;

        public Exception_Nodeable_TemplateAlreadyDefined(Referenced ref, String name) {
            super(ref);
            this.name = name;
        }

        @Override
        public String getName() {
            return "Template Already Defined (" + this.name + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The template, " + this.name + ", is already defined");
        }
    }

    public static class Exception_Nodeable_AbstractFunctionNotImplemented extends Exception_Nodeable {
        private ScriptTemplate_Abstract object;
        private ScriptFunction_Abstract function;

        public Exception_Nodeable_AbstractFunctionNotImplemented(Referenced ref, ScriptTemplate_Abstract object,
            ScriptFunction_Abstract function) {
            super(ref);
            this.object = object;
            this.function = function;
        }

        @Override
        public String getName() {
            return "Abstract Function Not Implememented";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addSnapNode(
                "The template is not abstract and does not implement the following function",
                this.object);
            assert LegacyDebugger.addNode(this.function);
        }
    }

    // Function Exceptions
    public static class Exception_Nodeable_UnimplementedFunction extends Exception_Nodeable {
        private ScriptTemplate_Abstract template;
        private String name;

        public Exception_Nodeable_UnimplementedFunction(ScriptEnvironment env, ScriptTemplate_Abstract template,
            String name) {
            super(env);
            this.template = template;
            this.name = name;
        }

        @Override
        public String getName() {
            return "Unimplemented Abstract Function (" + this.name + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addSnapNode("The abstract function, " + this.name + ", is unimplemented", this.template);
        }
    }

    public static class Exception_Nodeable_FunctionAlreadyDefined extends Exception_Nodeable {
        private String name;

        public Exception_Nodeable_FunctionAlreadyDefined(Referenced ref, String name) {
            super(ref);
            this.name = name;
        }

        @Override
        public String getName() {
            return "Function Already Defined (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The function, "
                + ScriptFunction.getDisplayableFunctionName(this.name)
                + ", is already defined");
        }
    }

    public static class Exception_Nodeable_IllegalNullReturnValue extends Exception_Nodeable {
        private ScriptFunction_Abstract function;

        public Exception_Nodeable_IllegalNullReturnValue(ScriptEnvironment env, ScriptFunction_Abstract fxn) {
            super(env);
            this.function = fxn;
        }

        public Exception_Nodeable_IllegalNullReturnValue(Referenced ref, ScriptFunction_Abstract fxn) {
            super(ref);
            this.function = fxn;
        }

        @Override
        public String getName() {
            return "Illegal Null Return Value";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addSnapNode("This function is attempting to return implicitly, even though it is of type, "
                + this.function.getReturnType(), this.function);
        }
    }

    // General Parsing Failure Exceptions
    public static class Exception_Nodeable_UnexpectedType extends Exception_Nodeable {
        private String expectedType;
        private Object providedType;

        public Exception_Nodeable_UnexpectedType(ScriptEnvironment env, Object provided, String exp) {
            super(env);
            this.providedType = provided;
            this.expectedType = exp;
        }

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

        @Override
        public String getName() {
            return "Unexpected Type (" + this.providedType + ")";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("The type or keyword, "
                + this.providedType
                + ", is unexpected here ("
                + this.expectedType
                + " is expected)");
        }
    }

    public static class Exception_Nodeable_UnknownModifier extends Exception_Nodeable {
        private List<Object> modifiers;

        public Exception_Nodeable_UnknownModifier(Referenced ref, List<Object> modifiers) {
            super(ref);
            this.modifiers = modifiers;
        }

        @Override
        public String getName() {
            return "Unknown Modifier(s)";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addSnapNode(
                "These modifiers (or what are believed to be modifiers) are unparseable to the compiler",
                this.modifiers);
        }
    }

    public static class Exception_Nodeable_UnparseableElement extends Exception_Nodeable {
        private String source;

        public Exception_Nodeable_UnparseableElement(Referenced ref, String thrownFrom) {
            super(ref);
            this.source = thrownFrom;
        }

        @Override
        public String getName() {
            return "Unparseable Element";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("A syntax error has occurred here, or near here, and the script is unparseable (Thrown from: "
                + this.source
                + ")");
        }
    }

    // Uncategorized Exceptions
    public static class Exception_Nodeable_DivisionByZero extends Exception_Nodeable {
        public Exception_Nodeable_DivisionByZero(Referenced ref) {
            super(ref.getEnvironment(), ref);
        }

        @Override
        public String getName() {
            return "Division by Zero";
        }

        @Override
        public void getExtendedInformation() {
            assert LegacyDebugger.addNode("Illegal mindfucking division by zero was encountered.");
        }
    }

    public static class Exception_Nodeable_FileNotFound extends Exception_Nodeable {
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

    public static class Exception_Nodeable_UnenclosedBracket extends Exception_Nodeable {
        public Exception_Nodeable_UnenclosedBracket(Referenced elem) {
            super(elem);
        }

        @Override
        public String getName() {
            return "Unenclosed Bracket";
        }
    }

    public static class Exception_Nodeable_UnenclosedStringLiteral extends Exception_Nodeable {
        public Exception_Nodeable_UnenclosedStringLiteral(Referenced elem) {
            super(elem);
        }

        @Override
        public String getName() {
            return "Unenclosed String";
        }
    }
}
