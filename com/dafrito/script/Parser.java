package com.dafrito.script;

import java.awt.Color;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.logging.Logging;
import com.dafrito.util.Scenario;
import com.dafrito.util.Scheduler;
import com.dafrito.util.Terrestrial;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Exceptions.Exception_Nodeable_TemplateNotFound;
import com.dafrito.debug.Exceptions.Exception_Nodeable_UnenclosedBracket;
import com.dafrito.debug.Exceptions.Exception_Nodeable_UnenclosedStringLiteral;
import com.dafrito.debug.Exceptions.Exception_Nodeable_UnexpectedType;
import com.dafrito.debug.Exceptions.Exception_Nodeable_UnknownModifier;
import com.dafrito.debug.Exceptions.Exception_Nodeable_UnparseableElement;
import com.dafrito.debug.Exceptions.Exception_Nodeable_VariableAlreadyDefined;
import com.dafrito.debug.Exceptions.Exception_Nodeable_VariableNotFound;
import com.dafrito.economy.Ace;
import com.dafrito.economy.Archetype;
import com.dafrito.economy.Asset;
import com.dafrito.geom.DiscreteRegion;
import com.dafrito.geom.Point;
import com.dafrito.geom.Point_Path;
import com.dafrito.gui.GraphicalElement;
import com.dafrito.gui.InterfaceElement;
import com.dafrito.gui.style.Stylesheet;
import com.dafrito.gui.style.StylesheetElementType;
import com.dafrito.gui.style.Elements.StylesheetAbsoluteHeightElement;
import com.dafrito.gui.style.Elements.StylesheetAbsoluteWidthElement;
import com.dafrito.gui.style.Elements.StylesheetBackgroundColorElement;
import com.dafrito.gui.style.Elements.StylesheetBorderElement;
import com.dafrito.gui.style.Elements.StylesheetColorElement;
import com.dafrito.gui.style.Elements.StylesheetFontSizeElement;
import com.dafrito.gui.style.Elements.StylesheetHeightElement;
import com.dafrito.gui.style.Elements.StylesheetMarginElement;
import com.dafrito.gui.style.Elements.StylesheetPaddingElement;
import com.dafrito.gui.style.Elements.StylesheetPercentageHeightElement;
import com.dafrito.gui.style.Elements.StylesheetPercentageWidthElement;
import com.dafrito.gui.style.Elements.StylesheetWidthElement;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.executable.ScriptExecutable_AssignValue;
import com.dafrito.script.executable.ScriptExecutable_AutoMathematicator;
import com.dafrito.script.executable.ScriptExecutable_CallFunction;
import com.dafrito.script.executable.ScriptExecutable_CastExpression;
import com.dafrito.script.executable.ScriptExecutable_CreateVariable;
import com.dafrito.script.executable.ScriptExecutable_EvalAssignMathExpression;
import com.dafrito.script.executable.ScriptExecutable_EvaluateBoolean;
import com.dafrito.script.executable.ScriptExecutable_EvaluateMathExpression;
import com.dafrito.script.executable.ScriptExecutable_ForStatement;
import com.dafrito.script.executable.ScriptExecutable_IfStatement;
import com.dafrito.script.executable.ScriptExecutable_ParseFunction;
import com.dafrito.script.executable.ScriptExecutable_RetrieveCurrentObject;
import com.dafrito.script.executable.ScriptExecutable_RetrieveVariable;
import com.dafrito.script.executable.ScriptExecutable_ReturnValue;
import com.dafrito.script.templates.FauxTemplate_Ace;
import com.dafrito.script.templates.FauxTemplate_Archetype;
import com.dafrito.script.templates.FauxTemplate_Asset;
import com.dafrito.script.templates.FauxTemplate_Color;
import com.dafrito.script.templates.FauxTemplate_DiscreteRegion;
import com.dafrito.script.templates.FauxTemplate_InterfaceElement;
import com.dafrito.script.templates.FauxTemplate_List;
import com.dafrito.script.templates.FauxTemplate_Object;
import com.dafrito.script.templates.FauxTemplate_Path;
import com.dafrito.script.templates.FauxTemplate_Point;
import com.dafrito.script.templates.FauxTemplate_Scheduler;
import com.dafrito.script.templates.FauxTemplate_Terrestrial;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Boolean;
import com.dafrito.script.types.ScriptValue_Null;
import com.dafrito.script.types.ScriptValue_Numeric;
import com.dafrito.script.types.ScriptValue_String;
import com.dafrito.script.types.ScriptValue_Variable;
import com.dafrito.util.RiffJavaToolbox;
import com.dafrito.util.RiffToolbox;

/**
 * File --> List of naked strings List of naked strings --> Comments-removed
 * naked strings Comments-removed naked strings --> Curly Bracket Groupings
 * Curly Bracket Groupings --> Lists of line of code
 * 
 * @author Aaron
 */
class StylesheetParams {
    private Referenced reference;
    private List<Object> modifiers;
    private String name;
    private ScriptGroup body;

    public StylesheetParams(Referenced ref, List<Object> modifiers, String name, ScriptGroup body) {
        this.reference = ref;
        this.modifiers = modifiers;
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return this.name;
    }

    public ScriptGroup getBody() {
        return this.body;
    }

    public List<Object> getModifiers() {
        return this.modifiers;
    }

    public Referenced getDebugReference() {
        return this.reference;
    }
}

class TemplateParams {
    private Referenced reference;
    private List<Object> modifiers;
    private String name;
    private ScriptGroup body;

    public TemplateParams(Referenced ref, String name, List<Object> modifiers, ScriptGroup body) {
        this.reference = ref;
        this.modifiers = modifiers;
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return this.name;
    }

    public ScriptGroup getBody() {
        return this.body;
    }

    public List<Object> getModifiers() {
        return this.modifiers;
    }

    public Referenced getDebugReference() {
        return this.reference;
    }
}

public class Parser {
    private static List<StylesheetParams> stylesheetParams = new LinkedList<StylesheetParams>();
    private static List<TemplateParams> classParams = new LinkedList<TemplateParams>();

    // Initial parsing functions
    public static List<ScriptValueType> createTypeList(List<ScriptValue_Abstract> values) {
        List<ScriptValueType> keywords = new LinkedList<ScriptValueType>();
        if(values == null) {
            return keywords;
        }
        for(ScriptValue_Abstract value : values) {
            keywords.add(value.getType());
        }
        return keywords;
    }

    public static void clearPreparseLists() {
        stylesheetParams.clear();
        classParams.clear();
    }

    public static Vector<Exception> parseFile(ScriptEnvironment env, String filename, List<Object> stringList) {
        Vector<Exception> exceptions = new Vector<Exception>();
        try {
            clearPreparseLists();
            assert LegacyDebugger.open("File Parsing", "Parsing file (" + filename + ")");
            assert LegacyDebugger.addSnapNode("Elements", stringList);
            preparseElements(env, preparseList(stringList));
            parseElements(env);
            assert LegacyDebugger.addSnapNode("Parsed successfully", env);
            assert LegacyDebugger.close();
        } catch(Exception_Nodeable ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Parsing file (" + filename + ")");
            exceptions.add(ex);
        } catch(Exception_InternalError ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Parsing file (" + filename + ")");
            exceptions.add(ex);
        }
        return exceptions;
    }

    public static Vector<Exception> preparseFile(ScriptEnvironment env, String filename, List<Object> stringList) {
        Vector<Exception> exceptions = new Vector<Exception>();
        try {
            assert LegacyDebugger.open("File Preparsing", "Preparsing file (" + filename + ")");
            assert LegacyDebugger.addSnapNode("Elements", stringList);
            preparseElements(env, preparseList(stringList));
            assert LegacyDebugger.close("Preparsed successfully");
        } catch(Exception_Nodeable ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Preparsing file (" + filename + ")");
            exceptions.add(ex);
        } catch(Exception_InternalError ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Preparsing file (" + filename + ")");
            exceptions.add(ex);
        }
        return exceptions;
    }

    public static List<Object> preparseList(List<Object> stringList) throws Exception_Nodeable {
        List<Object> parseList = stringList;
        parseList = removeComments(stringList);
        parseList = createQuotedElements(stringList);
        parseList = createGroupings(stringList, "{", "}", ScriptGroup.GroupType.curly, false);
        parseList = createGroupings(stringList, "(", ")", ScriptGroup.GroupType.parenthetical, true);
        parseList = parseOperators(stringList);
        parseList = removeEmptyScriptLines(stringList);
        parseList = splitByWhitespace(stringList);
        parseList = removeEmptyScriptLines(stringList);
        parseList = extractKeywords(stringList);
        parseList = extractNumbers(stringList);
        return parseList;
    }

    // Object-oriented parsing functions
    public static void preparseElements(ScriptEnvironment env, List<Object> lineList) throws Exception_Nodeable {
        assert LegacyDebugger.open("Preparsing Elements", "Preparsing Elements (" + lineList.size() + " element(s))");
        assert LegacyDebugger.addSnapNode("Elements", lineList);
        List<Object> modifiers = new LinkedList<Object>();
        for(int i = 0; i < lineList.size(); i++) {
            Referenced element = (Referenced)lineList.get(i);
            if(element instanceof ScriptKeyword) {
                if(element.equals(ScriptKeywordType.CLASS)) {
                    i++;
                    if(!(lineList.get(i) instanceof ScriptLine)) {
                        // If there's no name for the class, throw an exception.
                        // (Anonymous classes are not currently allowed.)
                        throw new Exception_Nodeable_UnexpectedType((Referenced)lineList.get(i), "Class name");
                    }
                    String name = ((ScriptLine)lineList.get(i)).getString();
                    i++;
                    ScriptGroup body = null;
                    if(modifiers.size() == 1) {
                        // We ignore public keywords for classes and treat them
                        // all as such.
                        if(ScriptKeywordType.PUBLIC.equals(modifiers.get(0))) {
                            modifiers.remove(0);
                        } else {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(0), "Keyword");
                        }
                    } else if(modifiers.size() > 1) {
                        // We don't allow modifiers for classes.
                        throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(0), "Keyword");
                    }
                    do {
                        // Collect suffix modifiers until we reach our
                        // curlyGroup
                        if(lineList.get(i) instanceof ScriptGroup) {
                            body = (ScriptGroup)lineList.get(i);
                            break;
                        }
                        modifiers.add(lineList.get(i));
                        i++;
                    } while (i < lineList.size());
                    if(body == null) {
                        // If there's no body, throw an exception
                        throw new Exception_Nodeable_UnexpectedType((Referenced)lineList.get(i - 1), "Class Definition Body");
                    }
                    List<Object> thisModifiers = new LinkedList<Object>();
                    thisModifiers.addAll(modifiers);
                    classParams.add(new TemplateParams(element, name, thisModifiers, body));
                    env.addType(element, name);
                    modifiers.clear();
                    continue;
                }
            }
            modifiers.add(lineList.get(i));
        }
        if(modifiers.size() != 0) {
            throw new Exception_Nodeable_UnknownModifier((Referenced)lineList.get(lineList.size() - 1), modifiers);
        }
        assert LegacyDebugger.close();
    }

    public static Vector<Exception> parseElements(ScriptEnvironment env) {
        Vector<Exception> exceptions = new Vector<Exception>();
        try {
            assert LegacyDebugger.open("Element Parsing", "Parsing Elements");
            List<ScriptTemplate_Abstract> queuedTemplates = new LinkedList<ScriptTemplate_Abstract>();
            for(TemplateParams params : classParams) {
                ScriptTemplate_Abstract template = preparseTemplate(
                    params.getDebugReference(),
                    env,
                    params.getModifiers(),
                    params.getBody(),
                    params.getName());
                queuedTemplates.add(template);
                env.addTemplate(params.getDebugReference(), params.getName(), template);
            }
            for(int i = 0; i < queuedTemplates.size(); i++) {
                queuedTemplates.get(i).initializeFunctions(classParams.get(i).getDebugReference());
            }
            assert LegacyDebugger.close();
        } catch(Exception_Nodeable ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Parsing Elements");
            exceptions.add(ex);
        } catch(Exception_InternalError ex) {
            LegacyDebugger.printException(ex);
            assert LegacyDebugger.closeNodeTo("Parsing Elements");
            exceptions.add(ex);
        }
        return exceptions;
    }

    public static ScriptTemplate_Abstract preparseTemplate(Referenced ref, ScriptEnvironment env,
        List<Object> modifiers, ScriptGroup body, String className) throws Exception_Nodeable {
        assert LegacyDebugger.open("Template Preparsing", "Preparsing Template (" + className + ")");
        assert LegacyDebugger.addSnapNode("Modifiers (" + modifiers.size() + " modifier(s))", modifiers);
        assert LegacyDebugger.addSnapNode("Template Body (" + body.getElements().size() + " element(s))", body);
        String extendedClass = null;
        List<ScriptValueType> implemented = new LinkedList<ScriptValueType>();
        for(int i = 0; i < modifiers.size(); i++) {
            ScriptElement obj = (ScriptElement)modifiers.get(i);
            assert LegacyDebugger.addSnapNode("(" + i + ") Current element", obj);
            if(modifiers.get(i) instanceof ScriptKeyword) {
                if(modifiers.get(i).equals(ScriptKeywordType.EXTENDS)) {
                    if(i >= modifiers.size() - 1 || !(modifiers.get(i + 1) instanceof ScriptLine)) {
                        throw new Exception_Nodeable_UnexpectedType(env, modifiers.get(i), "Class type");
                    }
                    extendedClass = ((ScriptLine)modifiers.get(i + 1)).getString();
                    assert LegacyDebugger.addNode("Extended class parsed (" + extendedClass + ")");
                    modifiers.remove(i);
                    modifiers.remove(i);
                    i--;
                } else if(modifiers.get(i).equals(ScriptKeywordType.IMPLEMENTS)) {
                    if(i == modifiers.size() - 1) {
                        throw new Exception_Nodeable_UnexpectedType(env, modifiers.get(i), "Interfaces");
                    }
                    boolean flag = false;
                    while (i < modifiers.size()) {
                        flag = true;
                        modifiers.remove(i);
                        if(!(modifiers.get(i) instanceof ScriptLine)) {
                            throw new Exception_Nodeable_UnexpectedType(env, modifiers.get(i), "Interface type");
                        }
                        implemented.add(ScriptValueType.createType(
                            (ScriptLine)modifiers.get(i),
                            ((ScriptLine)modifiers.get(i)).getString()));
                        modifiers.remove(i);
                    }
                    if(!flag) {
                        throw new Exception_Nodeable_UnexpectedType(env, obj, "Interfaces");
                    }
                } else {
                    throw new Exception_Nodeable_UnexpectedType(env, modifiers.get(i), "Keyword");
                }
            }
        }
        List<Object> list = body.getElements();
        if(extendedClass == null || extendedClass.equals("")) {
            extendedClass = FauxTemplate_Object.OBJECTSTRING;
        }
        ScriptTemplate_Abstract template = ScriptTemplate.createTemplate(
            ref.getEnvironment(),
            ScriptValueType.createType(ref, className),
            ScriptValueType.createType(ref, extendedClass),
            implemented,
            false);
        template.setConstructing(true);
        env.advanceStack(template, null);
        List<Object> elements = new LinkedList<Object>();
        boolean stylesheet = false;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i) instanceof ScriptOperator
                && ((ScriptOperator)list.get(i)).getType() == ScriptOperatorType.SEMICOLON) {
                List<Object> expressionList = new LinkedList<Object>();
                expressionList.addAll(elements);
                parseExpression(env, expressionList, true, ScriptValueType.createType(env, className));
                elements.clear();
                continue;
            }
            if(list.get(i) instanceof ScriptGroup) {
                if(stylesheet) {
                    List<Object> expressionList = new LinkedList<Object>();
                    expressionList.addAll(elements);
                    expressionList.add(list.get(i));
                    parseExpression(env, expressionList, true, ScriptValueType.createType(env, className));
                    elements.clear();
                    stylesheet = false;
                    continue;
                }
                String name = ((ScriptLine)list.get(i - 1)).getString();
                if(name.equals(className)) {
                    name = "";
                }
                ScriptExecutable_ParseFunction fxn = preparseFunction(
                    env,
                    template,
                    elements,
                    (ScriptGroup)list.get(i),
                    (ScriptGroup)list.get(++i),
                    name);
                template.addFunction(ref, name, fxn);
                elements.clear();
                continue;
            }
            if(list.get(i) instanceof ScriptKeyword && list.get(i).equals(ScriptKeywordType.STYLESHEET)) {
                stylesheet = true;
            }
            elements.add(list.get(i));
        }
        env.retreatStack();
        template.setConstructing(false);
        assert LegacyDebugger.close();
        return template;
    }

    public static ScriptFunction_Abstract parseFunction(ScriptExecutable_ParseFunction function, ScriptValueType type)
            throws Exception_Nodeable {
        assert LegacyDebugger.open("Parsing Functions", "Parsing Function ("
            + ScriptFunction.getDisplayableFunctionName(function.getName())
            + ")");
        ScriptFunction_Abstract fxn;
        if(function.getName() == null || function.getName().equals("")) {
            fxn = new ScriptFunction_Constructor(function.getReturnType(), function.getParameters(), function.getPermission());
        } else {
            fxn = new ScriptFunction(function.getReturnType(), function.getParameters(), function.getPermission(), function.isAbstract(), function.isStatic());
        }
        fxn.addExpressions(parseBodyList(function.getEnvironment(), function.getBody().getElements(), type));
        assert LegacyDebugger.close();
        return fxn;
    }

    public static ScriptExecutable_ParseFunction preparseFunction(ScriptEnvironment env,
        ScriptTemplate_Abstract object, List<Object> modifiers, ScriptGroup paramGroup, ScriptGroup body, String name)
            throws Exception_Nodeable {
        if(name.equals("")) {
            assert LegacyDebugger.open("Preparsing Functions", "Preparsing Function (constructor)");
        } else {
            assert LegacyDebugger.open("Preparsing Functions", "Preparsing Function (" + name + ")");
        }
        if(object == null)
            throw new NullPointerException();
        assert LegacyDebugger.addSnapNode("Reference Template", object);
        assert LegacyDebugger.addSnapNode("Modifiers", modifiers);
        assert LegacyDebugger.addSnapNode("Parameters", paramGroup);
        assert LegacyDebugger.addSnapNode("Body", body);
        ScriptExecutable_ParseFunction function;
        assert env != null : "ScriptEnvironment for parseFunction is null.";
        ScriptValueType returnType = null;
        Referenced ref = null;
        String functionName = null;
        ScriptKeywordType permission = ScriptKeywordType.PRIVATE;
        boolean isStatic, isAbstract;
        isStatic = isAbstract = false;
        for(int i = 0; i < modifiers.size(); i++) {
            if(modifiers.get(i) instanceof ScriptKeyword) {
                if(ref == null) {
                    ref = (ScriptKeyword)modifiers.get(i);
                }
                if(i == modifiers.size() - 1) {
                    throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(i), "Function name");
                }
                ScriptKeyword keyword = (ScriptKeyword)modifiers.get(i);
                if(keyword.equals(ScriptKeywordType.STATIC)) {
                    assert LegacyDebugger.addNode("Modifier Parsing", "Static modifier found");
                    isStatic = true;
                } else if(keyword.equals(ScriptKeywordType.ABSTRACT)) {
                    assert LegacyDebugger.addNode("Modifier Parsing", "Abstract modifier found");
                    isAbstract = true;
                } else if(keyword.equals(ScriptKeywordType.PRIVATE)
                    || keyword.equals(ScriptKeywordType.PUBLIC)
                    || keyword.equals(ScriptKeywordType.PROTECTED)) {
                    assert LegacyDebugger.addNode("Modifier Parsing", "Permission modifier found (" + keyword + ")");
                    permission = keyword.getType();
                } else {
                    if(ScriptValueType.isReturnablePrimitiveType(keyword.getValueType()) && returnType == null) {
                        returnType = keyword.getValueType();
                    } else {
                        throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(i), "Modifier");
                    }
                }
            }
            if(modifiers.get(i) instanceof ScriptLine) {
                if(ref == null) {
                    ref = (ScriptLine)modifiers.get(i);
                }
                if(i == modifiers.size() - 1) {
                    // It's a function name
                    if(returnType != null) {
                        functionName = ((ScriptLine)modifiers.get(i)).getString();
                        break;
                    }
                    // It's a constructor
                    returnType = ScriptValueType.createType(
                        (ScriptLine)modifiers.get(i),
                        ((ScriptLine)modifiers.get(i)).getString());
                    break;
                }
                if(i != modifiers.size() - 2) {
                    throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(i), "Modifier");
                }
                if(!(modifiers.get(modifiers.size() - 1) instanceof ScriptLine)) {
                    throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(modifiers.size() - 1), "Function name");
                }
                if(returnType != null) {
                    throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(i), "Modifier");
                }
                returnType = ScriptValueType.createType(
                    (ScriptLine)modifiers.get(i),
                    ((ScriptLine)modifiers.get(i)).getString());
                functionName = ((ScriptLine)modifiers.get(modifiers.size() - 1)).getString();
            }
        }
        if(functionName == null) {
            function = new ScriptExecutable_ParseFunction(ref, returnType, functionName, parseParamGroup(
                env,
                paramGroup,
                object.getType()), permission, true, false, body);
            assert LegacyDebugger.addSnapNode("Function parsed is a constructor (" + returnType + ")", function);
        } else if(returnType != null) {
            function = new ScriptExecutable_ParseFunction(ref, returnType, functionName, parseParamGroup(
                env,
                paramGroup,
                object.getType()), permission, isStatic, isAbstract, body);
            assert LegacyDebugger.addSnapNode("Function parsed is a regular function with a primitive return type ("
                + returnType
                + ")", function);
        } else {
            throw new Exception_Nodeable_UnexpectedType((Referenced)modifiers.get(modifiers.size() - 1), "Function parameters");
        }
        assert LegacyDebugger.close();
        return function;
    }

    public static ScriptExecutable parseFlowElement(ScriptEnvironment env, List<Object> list, ScriptValueType type)
            throws Exception_Nodeable {
        assert LegacyDebugger.open("Flow Element Parsing", "Parsing Flow Element");
        assert LegacyDebugger.addSnapNode("Elements", list);
        for(int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if(obj instanceof ScriptExecutable) {
                continue;
            }
            if(obj instanceof ScriptKeyword) {
                if(obj.equals(ScriptKeywordType.FOR)) {
                    env.advanceNestedStack();
                    list.remove(i);
                    if(!(list.get(i) instanceof ScriptGroup)) {
                        throw new Exception_Nodeable_UnexpectedType(env, list.get(i), "Param group");
                    }
                    List<ScriptExecutable> parameterList = parseBodyList(
                        env,
                        ((ScriptGroup)list.get(i)).getElements(),
                        type);
                    if(parameterList.size() != 3) {
                        throw new Exception_Nodeable_UnexpectedType(env, list.get(i), "'for' statement parameters");
                    }
                    list.remove(i);
                    if(!(list.get(i) instanceof ScriptGroup)) {
                        throw new Exception_Nodeable_UnexpectedType(env, list.get(i), "Curly group");
                    }
                    List<ScriptExecutable> bodyList = parseBodyList(env, ((ScriptGroup)list.get(i)).getElements(), type);
                    ScriptExecutable_ForStatement forStatement = new ScriptExecutable_ForStatement(parameterList.get(0), parameterList.get(1), parameterList.get(2), bodyList);
                    assert LegacyDebugger.close("For statement parsed", forStatement);
                    env.retreatNestedStack();
                    return forStatement;
                }
                if(obj.equals(ScriptKeywordType.ELSE)) {
                    env.advanceNestedStack();
                    list.remove(i);
                    i--;
                    if(i < 0 || !(list.get(i) instanceof ScriptExecutable_IfStatement)) {
                        int q = i;
                        if(q < 0) {
                            q = 0;
                        }
                        throw new Exception_Nodeable_UnexpectedType(env, list.get(q), "If statement");
                    }
                    ScriptExecutable_IfStatement previous = (ScriptExecutable_IfStatement)list.get(i);
                    list.remove(i);
                    if(list.get(i) instanceof ScriptKeyword && list.get(i).equals(ScriptKeywordType.IF)) {
                        Referenced ref = (Referenced)list.get(i);
                        list.remove(i);
                        previous.setElseStatement(parseIfStatement(ref, list, null, i, type));
                    } else if(list.get(i) instanceof ScriptGroup) {
                        Referenced ref = (Referenced)list.get(i);
                        previous.setElseStatement(parseIfStatement(
                            ref,
                            list,
                            new ScriptValue_Boolean(((Referenced)obj).getEnvironment(), true),
                            i,
                            type));
                    } else {
                        throw new Exception_Nodeable_UnexpectedType(env, list.get(i), "Keyword or code group");
                    }
                    LegacyDebugger.close("'Else' script group parsed", previous);
                    env.retreatNestedStack();
                    return null;
                }
                if(obj.equals(ScriptKeywordType.IF)) {
                    env.advanceNestedStack();
                    Referenced ref = (Referenced)list.get(i);
                    list.remove(i);
                    ScriptExecutable exec = parseIfStatement(ref, list, null, i, type);
                    list.remove(i);
                    env.retreatNestedStack();
                    if(i < list.size()
                        && list.get(i) instanceof ScriptKeyword
                        && list.get(i).equals(ScriptKeywordType.ELSE)) {
                        assert LegacyDebugger.open("Found else keyword, recursing...");
                        list.add(i, exec);
                        parseFlowElement(env, list, type);
                        LegacyDebugger.close();
                    }
                    LegacyDebugger.close("If group parsed", exec);
                    return exec;
                }
            }
        }
        throw new Exception_Nodeable_UnexpectedType(env, list.get(0), "Keyword");
    }

    public static List<ScriptExecutable> parseBodyList(ScriptEnvironment env, List<Object> bodyElements,
        ScriptValueType type) throws Exception_Nodeable {
        assert LegacyDebugger.open("Body List Parsing", "Parsing Body List (" + bodyElements.size() + " element(s))");
        List<Object> elements = new LinkedList<Object>();
        List<ScriptExecutable> statementBodyList = new LinkedList<ScriptExecutable>();
        for(int j = 0; j < bodyElements.size(); j++) {
            if(bodyElements.get(j) instanceof ScriptOperator
                && ((ScriptOperator)bodyElements.get(j)).getType() == ScriptOperatorType.SEMICOLON) {
                statementBodyList.add(parseExpression(env, elements, false, type));
                elements.clear();
                continue;
            }
            elements.add(bodyElements.get(j));
            if(bodyElements.get(j) instanceof ScriptGroup
                && ((ScriptGroup)bodyElements.get(j)).getType() == ScriptGroup.GroupType.curly) {
                if(j + 1 < bodyElements.size() && bodyElements.get(j + 1).equals(ScriptKeywordType.ELSE)) {
                    continue;
                }
                statementBodyList.add(parseFlowElement(env, elements, type));
                elements.clear();
                continue;
            }
        }
        if(elements.size() != 0) {
            statementBodyList.add(parseExpression(env, elements, false, type));
        }
        assert LegacyDebugger.close();
        return statementBodyList;
    }

    public static ScriptExecutable_IfStatement parseIfStatement(Referenced ref, List<Object> list,
        ScriptValue_Abstract value, int i, ScriptValueType type) throws Exception_Nodeable {
        assert LegacyDebugger.open("If-Statement Parsing", "Parsing 'if' Statement (" + list.size() + " element(s))");
        assert LegacyDebugger.addSnapNode("Boolean-Testing-Value", value);
        assert LegacyDebugger.addSnapNode("Body Elements", list);
        ScriptValue_Abstract workingValue = value;
        if(workingValue == null) {
            if(!(list.get(i) instanceof ScriptGroup)) {
                throw new Exception_Nodeable_UnexpectedType(ref, list.get(i), "Param group");
            }
            workingValue = (ScriptValue_Abstract)parseExpression(
                ref.getEnvironment(),
                ((ScriptGroup)list.get(i)).getElements(),
                false,
                type);
            list.remove(i);
        }
        if(!(list.get(i) instanceof ScriptGroup)) {
            throw new Exception_Nodeable_UnexpectedType(ref, list.get(i), "Curly group");
        }
        ScriptExecutable_IfStatement statement = new ScriptExecutable_IfStatement(ref, workingValue, parseBodyList(
            ref.getEnvironment(),
            ((ScriptGroup)list.get(i)).getElements(),
            type));
        assert LegacyDebugger.close();
        return statement;
    }

    public static Stylesheet parseStylesheet(Referenced ref, boolean isUnique, String name, ScriptGroup group)
            throws Exception_Nodeable {
        assert LegacyDebugger.open("Stylesheet Parsing", "Parsing Stylesheet (" + name + ")");
        assert LegacyDebugger.addNode("Unique: " + isUnique);
        List<Object> elements = group.getElements();
        assert LegacyDebugger.addSnapNode("Elements (" + elements.size() + " element(s))", elements);
        Stylesheet stylesheet = new Stylesheet(ref.getEnvironment());
        stylesheet.setName(name);
        stylesheet.setUnique(isUnique);
        for(int i = 0; i < elements.size(); i++) {
            if(elements.get(i) instanceof ScriptOperator
                && ((ScriptOperator)elements.get(i)).getType() == ScriptOperatorType.SEMICOLON) {
                continue;
            }
            assert elements.get(i) instanceof ScriptLine : "This element should be a ScriptLine: " + elements.get(i);
            Referenced keyRef = (ScriptLine)elements.get(i);
            String key = ((ScriptLine)elements.get(i)).getString();
            int offset = i + 1;
            if(elements.get(offset) instanceof ScriptOperator
                && ((ScriptOperator)elements.get(i + 1)).getType() == ScriptOperatorType.MINUS) {
                key = key + "-" + ((ScriptLine)elements.get(i + 2)).getString();
                offset += 2;
            }
            key = key.toLowerCase();
            if(key.equals("color")) {
                StylesheetColorElement colorElem;
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                if(elements.get(offset) instanceof ScriptOperator
                    && ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.POUNDSIGN) {
                    offset++;
                    if(elements.get(offset) instanceof ScriptLine) {
                        colorElem = new StylesheetColorElement("#" + ((ScriptLine)elements.get(offset)).getString());
                    } else {
                        colorElem = new StylesheetColorElement("#"
                            + ((ScriptValue_Numeric)elements.get(offset)).longValue());
                    }

                } else {
                    colorElem = new StylesheetColorElement(((ScriptLine)elements.get(offset)).getString());
                }
                assert LegacyDebugger.addSnapNode("Stylesheet Element Parsing", "Color stylesheet-element parsed", colorElem);
                stylesheet.addElement(StylesheetElementType.COLOR, colorElem);
                i += offset - i;
                continue;
            }
            if(key.equals("font-size")) {
                StylesheetFontSizeElement fontSizeElem;
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                fontSizeElem = new StylesheetFontSizeElement(((ScriptValue_Numeric)elements.get(offset)).intValue());
                assert LegacyDebugger.addSnapNode(
                    "Stylesheet Element Parsing",
                    "Font size stylesheet-element parsed",
                    fontSizeElem);
                stylesheet.addElement(StylesheetElementType.FONTSIZE, fontSizeElem);
                i += offset - i;
                continue;
            }
            if(key.equals("width")) {
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                StylesheetWidthElement widthElem;
                if(elements.get(offset + 1) instanceof ScriptOperator
                    && ((ScriptOperator)elements.get(offset + 1)).getType() == ScriptOperatorType.MODULUS) {
                    widthElem = new StylesheetPercentageWidthElement(((ScriptValue_Numeric)elements.get(offset)).doubleValue() / 100);
                    offset++;
                } else {
                    assert elements.get(offset) instanceof ScriptValue_Numeric : "Should be a numeric value: "
                        + elements.get(offset);
                    widthElem = new StylesheetAbsoluteWidthElement(((ScriptValue_Numeric)elements.get(offset)).intValue());
                }
                assert LegacyDebugger.addSnapNode("Stylesheet Element Parsing", "Width stylesheet-element parsed", widthElem);
                stylesheet.addElement(StylesheetElementType.WIDTH, widthElem);
                i += offset - i;
                continue;
            }
            if(key.equals("height")) {
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                StylesheetHeightElement heightElem;
                if(elements.get(offset + 1) instanceof ScriptOperator
                    && ((ScriptOperator)elements.get(offset + 1)).getType() == ScriptOperatorType.MODULUS) {
                    heightElem = new StylesheetPercentageHeightElement(((ScriptValue_Numeric)elements.get(offset)).doubleValue() / 100);
                    offset++;
                } else {
                    assert elements.get(offset) instanceof ScriptValue_Numeric : "Should be a numeric value: "
                        + elements.get(offset);
                    heightElem = new StylesheetAbsoluteHeightElement(((ScriptValue_Numeric)elements.get(offset)).intValue());
                }
                assert LegacyDebugger.addSnapNode(
                    "Stylesheet Element Parsing",
                    "Height stylesheet-element parsed",
                    heightElem);
                stylesheet.addElement(StylesheetElementType.HEIGHT, heightElem);
                i += offset - i;
                continue;
            }
            if(key.equals("margin-bottom")
                || key.equals("margin-top")
                || key.equals("margin-left")
                || key.equals("margin-right")
                || key.equals("margin")) {
                StylesheetMarginElement marginElem;
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                assert elements.get(offset) instanceof ScriptValue_Numeric : "This element should be a ScriptValue_Numeric: "
                    + elements.get(offset);
                marginElem = new StylesheetMarginElement(((ScriptValue_Numeric)elements.get(offset)).intValue());
                assert LegacyDebugger.addSnapNode(
                    "Stylesheet Element Parsing",
                    "Margin stylesheet-element parsed",
                    marginElem);
                if(key.equals("margin")) {
                    stylesheet.addElement(StylesheetElementType.MARGINBOTTOM, marginElem);
                    stylesheet.addElement(StylesheetElementType.MARGINTOP, marginElem);
                    stylesheet.addElement(StylesheetElementType.MARGINLEFT, marginElem);
                    stylesheet.addElement(StylesheetElementType.MARGINRIGHT, marginElem);
                } else if(key.equals("margin-bottom")) {
                    stylesheet.addElement(StylesheetElementType.MARGINBOTTOM, marginElem);
                } else if(key.equals("margin-top")) {
                    stylesheet.addElement(StylesheetElementType.MARGINTOP, marginElem);
                } else if(key.equals("margin-left")) {
                    stylesheet.addElement(StylesheetElementType.MARGINLEFT, marginElem);
                } else if(key.equals("margin-right")) {
                    stylesheet.addElement(StylesheetElementType.MARGINRIGHT, marginElem);
                }
                i += offset - i;
                continue;
            }
            if(key.equals("padding-bottom")
                || key.equals("padding-top")
                || key.equals("padding-left")
                || key.equals("padding-right")
                || key.equals("padding")) {
                StylesheetPaddingElement paddingElem;
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                assert elements.get(offset) instanceof ScriptValue_Numeric : "This element should be a ScriptValue_Numeric: "
                    + elements.get(offset);
                paddingElem = new StylesheetPaddingElement(((ScriptValue_Numeric)elements.get(offset)).intValue());
                assert LegacyDebugger.addSnapNode(
                    "Stylesheet Element Parsing",
                    "Padding stylesheet-element parsed",
                    paddingElem);
                if(key.equals("padding")) {
                    stylesheet.addElement(StylesheetElementType.PADDINGBOTTOM, paddingElem);
                    stylesheet.addElement(StylesheetElementType.PADDINGTOP, paddingElem);
                    stylesheet.addElement(StylesheetElementType.PADDINGLEFT, paddingElem);
                    stylesheet.addElement(StylesheetElementType.PADDINGRIGHT, paddingElem);
                } else if(key.equals("padding-bottom")) {
                    stylesheet.addElement(StylesheetElementType.PADDINGBOTTOM, paddingElem);
                } else if(key.equals("padding-top")) {
                    stylesheet.addElement(StylesheetElementType.PADDINGTOP, paddingElem);
                } else if(key.equals("padding-left")) {
                    stylesheet.addElement(StylesheetElementType.PADDINGLEFT, paddingElem);
                } else if(key.equals("padding-right")) {
                    stylesheet.addElement(StylesheetElementType.PADDINGRIGHT, paddingElem);
                }
                i += offset - i;
                continue;
            }
            if(key.equals("border-bottom")
                || key.equals("border-top")
                || key.equals("border-left")
                || key.equals("border-right")
                || key.equals("border")) {
                StylesheetBorderElement borderElem;
                if(!(elements.get(offset) instanceof ScriptOperator)
                    || ((ScriptOperator)elements.get(offset)).getType() != ScriptOperatorType.COLON) {
                    if(elements.get(offset) instanceof Referenced) {
                        throw new Exception_Nodeable_UnexpectedType((Referenced)elements.get(offset), "colon");
                    }
                    throw new Exception_Nodeable_UnexpectedType(keyRef, elements.get(offset), "colon");
                }
                offset++;
                if(!(elements.get(offset) instanceof ScriptValue_Numeric)) {
                    if(elements.get(offset) instanceof Referenced) {
                        throw new Exception_Nodeable_UnexpectedType((Referenced)elements.get(offset), "Number");
                    }
                    throw new Exception_Nodeable_UnexpectedType(keyRef, elements.get(offset), "Number");
                }
                if(!(elements.get(offset + 1) instanceof ScriptKeyword)) {
                    if(elements.get(offset + 1) instanceof Referenced) {
                        throw new Exception_Nodeable_UnexpectedType((Referenced)elements.get(offset + 1), "Border Style");
                    }
                    throw new Exception_Nodeable_UnexpectedType(keyRef, elements.get(offset + 1), "Border Style");
                }
                int width = ((ScriptValue_Numeric)elements.get(offset++)).intValue();
                ScriptKeywordType style = ((ScriptKeyword)elements.get(offset++)).getType();
                Color color;
                if(elements.get(offset) instanceof ScriptOperator
                    && ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.POUNDSIGN) {
                    offset++;
                    if(elements.get(offset) instanceof ScriptLine) {
                        color = RiffJavaToolbox.getColor("#" + ((ScriptLine)elements.get(offset)).getString());
                    } else {
                        color = RiffJavaToolbox.getColor("#" + ((ScriptValue_Numeric)elements.get(offset)).longValue());
                    }
                } else {
                    color = RiffJavaToolbox.getColor(((ScriptLine)elements.get(offset)).getString());
                    if(color == null) {
                        throw new Exception_Nodeable_UnparseableElement((ScriptLine)elements.get(offset), "parseStylesheet");
                    }
                }
                borderElem = new StylesheetBorderElement(width, style, color);
                assert LegacyDebugger.addSnapNode(
                    "Stylesheet Element Parsing",
                    "Border stylesheet-element parsed",
                    borderElem);
                if(key.equals("border")) {
                    stylesheet.addElement(StylesheetElementType.BORDERBOTTOM, borderElem);
                    stylesheet.addElement(StylesheetElementType.BORDERTOP, borderElem);
                    stylesheet.addElement(StylesheetElementType.BORDERLEFT, borderElem);
                    stylesheet.addElement(StylesheetElementType.BORDERRIGHT, borderElem);
                } else if(key.equals("border-bottom")) {
                    stylesheet.addElement(StylesheetElementType.BORDERBOTTOM, borderElem);
                } else if(key.equals("border-top")) {
                    stylesheet.addElement(StylesheetElementType.BORDERTOP, borderElem);
                } else if(key.equals("border-left")) {
                    stylesheet.addElement(StylesheetElementType.BORDERLEFT, borderElem);
                } else if(key.equals("border-right")) {
                    stylesheet.addElement(StylesheetElementType.BORDERRIGHT, borderElem);
                }
                i += offset - i;
                continue;
            }
            if(key.equals("background-color")) {
                assert elements.get(offset) instanceof ScriptOperator;
                assert ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.COLON;
                offset++;
                Color color;
                if(elements.get(offset) instanceof ScriptOperator
                    && ((ScriptOperator)elements.get(offset)).getType() == ScriptOperatorType.POUNDSIGN) {
                    offset++;
                    if(elements.get(offset) instanceof ScriptLine) {
                        color = RiffJavaToolbox.getColor("#" + ((ScriptLine)elements.get(offset)).getString());
                    } else {
                        color = RiffJavaToolbox.getColor("#" + ((ScriptValue_Numeric)elements.get(offset)).longValue());
                    }
                } else {
                    color = RiffJavaToolbox.getColor(((ScriptLine)elements.get(offset)).getString());
                }
                StylesheetBackgroundColorElement bgColorElem = new StylesheetBackgroundColorElement(color);
                assert LegacyDebugger.addSnapNode(
                    "Stylesheet Element Parsing",
                    "Background color stylesheet-element parsed",
                    bgColorElem);
                stylesheet.addElement(StylesheetElementType.BACKGROUNDCOLOR, bgColorElem);
                i += offset - i;
                continue;
            }
        }
        assert LegacyDebugger.close();
        return stylesheet;
    }

    public static ScriptExecutable parseExpression(ScriptEnvironment env, List<Object> list,
        boolean automaticallyAddToStack, ScriptValueType type) throws Exception_Nodeable {
        if(list.size() == 1 && list.get(0) instanceof ScriptExecutable) {
            return (ScriptExecutable)list.get(0);
        }
        for(int i = 0; i < list.size(); i++) {
            // assert Debugger.addSnapNode(DebugString.ELEMENTS,list);
            Object obj = list.get(i);
            // assert Debugger.addSnapNode("Current element ("+obj+")",obj);
            Object nextObj = null;
            if(i < list.size() - 1) {
                nextObj = list.get(i + 1);
            }
            if(obj instanceof ScriptExecutable) {
                if(i == list.size() - 1) {
                    return (ScriptExecutable)obj;
                }
                continue;
            }
            if(obj.equals(ScriptKeywordType.NULL)) {
                ScriptExecutable returnValue = new ScriptValue_Null((Referenced)obj);
                assert LegacyDebugger.addSnapNode("Expression Parsing", "'null' keyword parsed", returnValue);
                return returnValue;
            }
            // This keyword
            if(obj.equals(ScriptKeywordType.THIS)) {
                ScriptExecutable returnValue = new ScriptExecutable_RetrieveCurrentObject((Referenced)obj, type);
                assert LegacyDebugger.addSnapNode("Expression Parsing", "'this' keyword parsed", returnValue);
                return returnValue;
            }
            // Returns!
            if(obj.equals(ScriptKeywordType.RETURN)) {
                Referenced ref = (Referenced)list.get(i);
                list.remove(i);
                ScriptExecutable_ReturnValue returnValue;
                if(list.size() == 1 && list.get(0) instanceof ScriptValue_Abstract) {
                    returnValue = new ScriptExecutable_ReturnValue(ref, (ScriptValue_Abstract)list.get(0));
                } else {
                    returnValue = new ScriptExecutable_ReturnValue(ref, (ScriptValue_Abstract)parseExpression(
                        env,
                        list,
                        automaticallyAddToStack,
                        type));
                }
                assert LegacyDebugger.addSnapNode("Expression Parsing", "Return value parsed", returnValue);
                return returnValue;
            }
            if(obj instanceof ScriptGroup) {
                ScriptGroup group = (ScriptGroup)obj;
                List<Object> groupList = group.getElements();
                list.remove(i);
                if(groupList.size() == 1) {
                    // Casting
                    ScriptExecutable_CastExpression caster = null;
                    if(groupList.get(0) instanceof ScriptLine) {
                        caster = new ScriptExecutable_CastExpression((Referenced)groupList.get(0), ScriptValueType.createType(
                            (ScriptLine)groupList.get(0),
                            ((ScriptLine)groupList.get(0)).getString()), parseExpression(
                            env,
                            list,
                            automaticallyAddToStack,
                            type));
                    } else if(groupList.get(0) instanceof ScriptKeyword) {
                        caster = new ScriptExecutable_CastExpression((Referenced)groupList.get(0), ((ScriptKeyword)groupList.get(0)).getValueType(), parseExpression(
                            env,
                            list,
                            automaticallyAddToStack,
                            type));
                    }
                    assert LegacyDebugger.addSnapNode("Expression Parsing", "Cast Expression Parsed", caster);
                    return caster;
                }
                list.add(i, parseExpression(env, groupList, automaticallyAddToStack, type));
                continue;
            }
            // Primitive object creation.
            if(obj instanceof ScriptKeyword
                && (((ScriptKeyword)obj).equals(ScriptKeywordType.STYLESHEET) || ScriptValueType.isPrimitiveType(((ScriptKeyword)obj).getValueType()))) {
                // obj is ScriptKeyword for variable type
                // nextObj is the variableName
                ScriptKeywordType permission = ScriptKeywordType.PRIVATE;
                boolean isStatic = false;
                boolean isUnique = false;
                int loc = i;
                while (loc > 0) {
                    if(!(list.get(loc) instanceof ScriptKeyword)) {
                        throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(loc), "Keyword");
                    }
                    loc--;
                }
                int temp = i;
                i = loc;
                loc = temp - loc;
                for(; loc > 0; loc--) {
                    ScriptKeywordType currKeyword = ((ScriptKeyword)list.get(i)).getType();
                    if(currKeyword == ScriptKeywordType.PRIVATE || currKeyword == ScriptKeywordType.PUBLIC) {
                        permission = ((ScriptKeyword)list.get(i)).getType();
                    } else if(currKeyword == ScriptKeywordType.STATIC) {
                        isStatic = true;
                    } else if(currKeyword == ScriptKeywordType.UNIQUE) {
                        isUnique = true;
                    }
                    list.remove(i);
                }
                do {
                    if(!(list.get(i + 1) instanceof ScriptLine)) {
                        throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(i + 1), "Variable name");
                    }
                    String name = ((ScriptLine)list.get(i + 1)).getString();
                    if(env.retrieveVariable(name) != null) {
                        throw new Exception_Nodeable_VariableAlreadyDefined((Referenced)list.get(i + 1), null, name);
                    }
                    ScriptValue_Variable creator;
                    if(((ScriptKeyword)obj).equals(ScriptKeywordType.STYLESHEET)) {
                        creator = new ScriptExecutable_CreateVariable((Referenced)list.get(i), ScriptValueType.createType(
                            env,
                            Stylesheet.STYLESHEETSTRING), name, permission);
                    } else {
                        creator = new ScriptExecutable_CreateVariable((Referenced)list.get(i), ((ScriptKeyword)obj).getValueType(), name, permission);
                    }
                    ScriptExecutable exec = (ScriptExecutable)creator;
                    if(((ScriptKeyword)obj).equals(ScriptKeywordType.STYLESHEET)) {
                        if(list.size() > i + 2 && list.get(i + 2) instanceof ScriptGroup) {
                            exec = new ScriptExecutable_AssignValue((Referenced)list.get(i + 1), (ScriptValue_Abstract)exec, parseStylesheet(
                                (Referenced)list.get(i + 1),
                                isUnique,
                                name,
                                (ScriptGroup)list.get(i + 2)));
                        }
                        list.remove(i + 2);
                    }
                    assert LegacyDebugger.addSnapNode("Expression Parsing", "Variable creation element parsed", exec);
                    if(automaticallyAddToStack
                        && env.getCurrentObject() != null
                        && env.getCurrentObject().isConstructing()) {
                        if(isStatic) {
                            env.getCurrentObject().addTemplatePreconstructorExpression(exec);
                        } else {
                            env.getCurrentObject().addPreconstructorExpression(exec);
                        }
                    }
                    env.addVariableToStack(name, creator);
                    list.remove(i);
                    list.remove(i);
                    list.add(i, exec);
                    if(i < list.size() - 1) {
                        i++;
                    }
                } while (list.get(i).equals(ScriptOperatorType.COMMA));
                return parseExpression(env, list, automaticallyAddToStack, type);
            }
            // Object creation
            if(obj.equals(ScriptKeywordType.NEW)) {
                if(!(nextObj instanceof ScriptLine)) {
                    throw new Exception_Nodeable_UnexpectedType((Referenced)nextObj, "Object name");
                }
                if(!(list.get(i + 2) instanceof ScriptGroup)) {
                    throw new Exception_Nodeable_UnexpectedType((Referenced)nextObj, "Parameters");
                }
                if(env.getTemplate(((ScriptLine)nextObj).getString()) == null) {
                    throw new Exception_Nodeable_TemplateNotFound((ScriptLine)nextObj, ((ScriptLine)nextObj).getString());
                }
                ScriptExecutable returnValue = new ScriptExecutable_CallFunction((Referenced)obj, new ScriptExecutable_RetrieveVariable((Referenced)obj, null, ((ScriptLine)nextObj).getString(), env.retrieveVariable(
                    ((ScriptLine)nextObj).getString()).getType()), "", parseParamGroup(
                    env,
                    (ScriptGroup)list.get(i + 2),
                    type));
                assert LegacyDebugger.addSnapNode("Object construction element parsed", returnValue);
                list.remove(i);
                list.remove(i);
                list.remove(i);
                return returnValue;
            }
            /*
             * Operators that still need to be implemented public static final
             * short COLON=23;
             */
            // Operators!
            if(obj instanceof ScriptOperator) {
                ScriptValue_Variable lhs;
                ScriptValue_Abstract left;
                ScriptExecutable returnValue;
                switch(((ScriptOperator)obj).getType()) {
                    case GREATER:
                    case LESS:
                    case GREATEREQUALS:
                    case LESSEQUALS:
                    case EQUIVALENCY:
                    case NONEQUIVALENCY:
                        if(i < 1 || !(list.get(i - 1) instanceof ScriptValue_Abstract)) {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(i), "Variable");
                        }
                        i--;
                        left = (ScriptValue_Abstract)list.get(i);
                        list.remove(i);
                        list.remove(i);
                        if(list.size() == 1 && list.get(0) instanceof ScriptValue_Abstract) {
                            returnValue = new ScriptExecutable_EvaluateBoolean((Referenced)obj, left, (ScriptValue_Abstract)list.get(i), ((ScriptOperator)obj).getType());
                            list.remove(i);
                            assert LegacyDebugger.addSnapNode("Expression Parsing", "Boolean expression parsed", returnValue);
                            return returnValue;
                        }
                        returnValue = new ScriptExecutable_EvaluateBoolean((Referenced)obj, left, (ScriptValue_Abstract)parseExpression(
                            env,
                            list,
                            automaticallyAddToStack,
                            type), ((ScriptOperator)obj).getType());
                        assert LegacyDebugger.addSnapNode("Expression Parsing", "Boolean expression parsed", returnValue);
                        return returnValue;
                    case PERIOD:
                        if(i < 1 || !(list.get(i - 1) instanceof ScriptValue_Abstract)) {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(i - 1), "Variable");
                        }
                        i--;
                        left = (ScriptValue_Abstract)list.get(i);
                        list.remove(i);
                        list.remove(i);
                        if(!(list.get(i) instanceof ScriptLine)) {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(i), "Function name");
                        }
                        ScriptLine name = (ScriptLine)list.get(i);
                        list.remove(i);
                        if(list.get(i) instanceof ScriptGroup) {
                            ScriptGroup group = (ScriptGroup)list.get(i);
                            returnValue = new ScriptExecutable_CallFunction(group, left, name.getString(), parseParamGroup(
                                env,
                                group.getElements(),
                                type));
                            assert LegacyDebugger.addSnapNode(
                                "Expression Parsing",
                                "Object function call parsed",
                                returnValue);
                            list.remove(i);
                        } else {
                            returnValue = new ScriptExecutable_RetrieveVariable(name, left, name.getString(), env.getTemplate(
                                left.getType()).getVariable(name.getString()).getType());
                            assert LegacyDebugger.addSnapNode(
                                "Expression Parsing",
                                "Object member-variable placeholder parsed",
                                returnValue);
                        }
                        list.add(i, returnValue);
                        return parseExpression(env, list, automaticallyAddToStack, type);
                    case ASSIGNMENT:
                        i--;
                        if(!(list.get(i) instanceof ScriptExecutable)) {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(i), "Variable name");
                        }
                        lhs = (ScriptValue_Variable)list.get(i);
                        list.remove(i);
                        list.remove(i);
                        if(list.size() == 1 && list.get(i) instanceof ScriptValue_Abstract) {
                            returnValue = new ScriptExecutable_AssignValue((Referenced)lhs, lhs, (ScriptValue_Abstract)list.get(i));
                        } else {
                            returnValue = new ScriptExecutable_AssignValue((Referenced)lhs, lhs, (ScriptValue_Abstract)parseExpression(
                                env,
                                list,
                                automaticallyAddToStack,
                                type));
                        }
                        assert LegacyDebugger.addSnapNode(
                            "Expression Parsing",
                            "Variable assignment expression parsed",
                            returnValue);
                        return returnValue;
                    case PLUS:
                    case MINUS:
                    case MULTIPLY:
                    case DIVIDE:
                    case MODULUS:
                        if(i == 0 || !(list.get(0) instanceof ScriptValue_Abstract)) {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(0), "Variable");
                        }
                        left = (ScriptValue_Abstract)list.get(0);
                        list.remove(0);
                        list.remove(0);
                        // public
                        // ScriptExecutable_EvaluateMathExpression(Referenced
                        // ref, ScriptValue_Abstract lhs, ScriptValue_Abstract
                        // rhs,ScriptOperatorType expressionType)
                        if(list.size() == 1 && list.get(0) instanceof ScriptValue_Abstract) {
                            returnValue = new ScriptExecutable_EvaluateMathExpression((Referenced)obj, left, (ScriptValue_Abstract)list.get(0), ((ScriptOperator)obj).getType());
                        } else {
                            returnValue = new ScriptExecutable_EvaluateMathExpression((Referenced)obj, left, (ScriptValue_Abstract)parseExpression(
                                env,
                                list,
                                automaticallyAddToStack,
                                type), ((ScriptOperator)obj).getType());
                        }
                        assert LegacyDebugger.addSnapNode("Expression Parsing", "Mathematical expression parsed", returnValue);
                        return returnValue;
                    case PLUSEQUALS:
                    case MINUSEQUALS:
                    case MULTIPLYEQUALS:
                    case DIVIDEEQUALS:
                    case MODULUSEQUALS:
                        if(i == 0 || !(list.get(0) instanceof ScriptExecutable)) {
                            throw new Exception_Nodeable_UnexpectedType((Referenced)list.get(0), "Variable");
                        }
                        lhs = (ScriptValue_Variable)list.get(0);
                        list.remove(0);
                        list.remove(0);
                        if(list.size() == 1 && list.get(0) instanceof ScriptValue_Abstract) {
                            // public
                            // ScriptExecutable_EvalAssignMathExpression(Referenced
                            // ref, ScriptValue lhs, ScriptValue
                            // rhs,ScriptOperatorType operation){
                            returnValue = new ScriptExecutable_EvalAssignMathExpression((Referenced)lhs, lhs, (ScriptValue_Abstract)list.get(0), ((ScriptOperator)obj).getType());
                        } else {
                            returnValue = new ScriptExecutable_EvalAssignMathExpression((Referenced)lhs, lhs, (ScriptValue_Abstract)parseExpression(
                                env,
                                list,
                                automaticallyAddToStack,
                                type), ((ScriptOperator)obj).getType());
                        }
                        assert LegacyDebugger.addSnapNode(
                            "Expression Parsing",
                            "Mathematical assignment expression parsed",
                            returnValue);
                        return returnValue;
                    case INCREMENT:
                    case DECREMENT:
                        // public ScriptExecutable_AutoMathematicator(Referenced
                        // ref,ScriptValue_Abstract value,ScriptOperatorType
                        // operator,boolean isPost)
                        if(i > 0 && list.get(i - 1) instanceof ScriptValue_Abstract) {
                            // Post-increment
                            i--;
                            returnValue = new ScriptExecutable_AutoMathematicator((Referenced)list.get(i + 1), (ScriptValue_Abstract)list.get(i), ((ScriptOperator)obj).getType(), true);
                            assert LegacyDebugger.addSnapNode("Expression Parsing", "Auto-mathematicator parsed", returnValue);
                            list.remove(i);
                            list.remove(i);
                            return returnValue;
                        }
                        // Pre-increment
                        list.remove(i);
                        returnValue = new ScriptExecutable_AutoMathematicator((Referenced)list.get(i), (ScriptValue_Abstract)parseExpression(
                            env,
                            list,
                            automaticallyAddToStack,
                            type), ((ScriptOperator)obj).getType(), false);
                        assert LegacyDebugger.addSnapNode("Expression Parsing", "Auto-mathematicator parsed", returnValue);
                        return returnValue;
                    default:
                        throw new UnsupportedOperationException("Unknonw ScriptOperatorType");
                }
            }
            // Placeholder object creation, function calls, right-side variables
            if(obj instanceof ScriptLine) {
                ScriptExecutable returnValue;
                if(nextObj == null || nextObj instanceof ScriptOperator) {
                    // It's a variable we've previously defined
                    if(env.retrieveVariable(((ScriptLine)obj).getString()) == null) {
                        LegacyDebugger.addSnapNode("Environment before exception", env);
                        throw new Exception_Nodeable_VariableNotFound((Referenced)obj, ((ScriptLine)obj).getString());
                    }
                    list.remove(i);
                    returnValue = new ScriptExecutable_RetrieveVariable((Referenced)obj, null, ((ScriptLine)obj).getString(), env.retrieveVariable(
                        ((ScriptLine)obj).getString()).getType());
                    list.add(i, returnValue);
                    if(nextObj == null) {
                        assert LegacyDebugger.addSnapNode("Expression Parsing", "Variable placeholder parsed", returnValue);
                        return (ScriptExecutable)list.get(i);
                    }
                    assert LegacyDebugger.addSnapNode("Expression Parsing", "Variable placeholder parsed", returnValue);
                    return parseExpression(env, list, automaticallyAddToStack, type);
                }
                if(nextObj instanceof ScriptGroup) {
                    if(i + 1 == list.size() - 1 || !(list.get(i + 2) instanceof ScriptGroup)) {
                        // It's a function call!
                        // public ScriptExecutable_CallFunction(Referenced
                        // ref,ScriptValue_Abstract object,String
                        // functionName,List<ScriptValue_Abstract>params)
                        ScriptExecutable_CallFunction fxnCall = new ScriptExecutable_CallFunction((Referenced)obj, null, ((ScriptLine)obj).getString(), parseParamGroup(
                            env,
                            (ScriptGroup)nextObj,
                            type));
                        list.remove(i);
                        list.remove(i);
                        list.add(i, fxnCall);
                        assert LegacyDebugger.addSnapNode("Expression Parsing", "Function call parsed", fxnCall);
                        return fxnCall;
                    }
                }
                if(nextObj instanceof ScriptLine) {
                    // Object placeholder creation
                    if(env.retrieveVariable(((ScriptLine)nextObj).getString()) != null) {
                        throw new Exception_Nodeable_VariableAlreadyDefined((Referenced)nextObj, null, ((ScriptLine)nextObj).getString());
                    }
                    ScriptKeywordType permission = ScriptKeywordType.PRIVATE;
                    boolean isStatic = false;
                    if(i > 0 && list.get(i - 1) instanceof ScriptKeyword) {
                        ScriptKeywordType currKeyword = ((ScriptKeyword)list.get(i - 1)).getType();
                        if(currKeyword.equals(ScriptKeywordType.PRIVATE)
                            || currKeyword.equals(ScriptKeywordType.PUBLIC)) {
                            permission = currKeyword;
                        } else if(currKeyword.equals(ScriptKeywordType.STATIC)) {
                            isStatic = true;
                            break;
                        }
                        list.remove(i - 1);
                        i--;
                    }
                    ScriptExecutable_CreateVariable creator = new ScriptExecutable_CreateVariable((Referenced)obj, ScriptValueType.createType(
                        (ScriptLine)obj,
                        ((ScriptLine)obj).getString()), ((ScriptLine)list.get(i + 1)).getString(), permission);
                    assert LegacyDebugger.addSnapNode("Expression Parsing", "Variable creation element parsed", creator);
                    if(automaticallyAddToStack
                        && env.getCurrentObject() != null
                        && env.getCurrentObject().isConstructing()) {
                        if(isStatic) {
                            env.getCurrentObject().addTemplatePreconstructorExpression(creator);
                        } else {
                            env.getCurrentObject().addPreconstructorExpression(creator);
                        }
                    }
                    env.addVariableToStack(creator.getName(), creator);
                    list.remove(i);
                    list.remove(i);
                    list.add(i, creator);
                    return parseExpression(env, list, automaticallyAddToStack, type);
                }
            }
        }
        if(list.size() == 1 && list.get(0) instanceof ScriptExecutable) {
            return (ScriptExecutable)list.get(0);
        }
        throw new Exception_InternalError(env, "Defaulted in parseExpression");
    }

    public static List<ScriptValue_Abstract> parseParamGroup(ScriptEnvironment env, ScriptGroup group,
        ScriptValueType type) throws Exception_Nodeable {
        return parseParamGroup(env, group.getElements(), type);
    }

    public static List<ScriptValue_Abstract> parseParamGroup(ScriptEnvironment env, List<Object> elementsList,
        ScriptValueType type) throws Exception_Nodeable {
        assert LegacyDebugger.open("Parameter-Group Parsing", "Parsing Parameter-Group ("
            + elementsList.size()
            + " element(s) in group)");
        assert LegacyDebugger.addSnapNode("Elements", elementsList);
        List<ScriptValue_Abstract> groupList = new LinkedList<ScriptValue_Abstract>();
        List<Object> currentParamList = new LinkedList<Object>();
        env.advanceNestedStack();
        for(Object obj : elementsList) {
            if(obj instanceof ScriptOperator && ((ScriptOperator)obj).getType() == ScriptOperatorType.COMMA) {
                if(currentParamList.size() == 1 && currentParamList.get(0) instanceof ScriptValue_Abstract) {
                    groupList.add((ScriptValue_Abstract)currentParamList.get(0));
                } else {
                    groupList.add((ScriptValue_Abstract)parseExpression(env, currentParamList, false, type));
                }
                currentParamList.clear();
                continue;
            }
            currentParamList.add(obj);
        }
        if(currentParamList.size() > 0) {
            if(currentParamList.size() == 1 && currentParamList.get(0) instanceof ScriptValue_Abstract) {
                groupList.add((ScriptValue_Abstract)currentParamList.get(0));
            } else {
                groupList.add((ScriptValue_Abstract)parseExpression(env, currentParamList, false, type));
            }
        }
        env.retreatNestedStack();
        assert LegacyDebugger.close();
        return groupList;
    }

    // Procedural parsing functions
    public static List<Object> extractNumbers(List<Object> lineList) {
        for(int i = 0; i < lineList.size(); i++) {
            if(lineList.get(i) instanceof ScriptGroup) {
                ((ScriptGroup)lineList.get(i)).setElements(extractNumbers(((ScriptGroup)lineList.get(i)).getElements()));
                continue;
            }
            if(!(lineList.get(i) instanceof ScriptLine)) {
                continue;
            }
            ScriptLine line = (ScriptLine)lineList.get(i);
            String number = line.getString();
            if(number.matches("^[0-9]*px$")) {
                String numString = ((ScriptLine)lineList.get(i)).getString().substring(
                    0,
                    ((ScriptLine)lineList.get(i)).getString().length() - 2);
                ScriptValue_Numeric scriptShort = new ScriptValue_Numeric(line.getEnvironment(), Short.parseShort(numString));
                assert LegacyDebugger.addSnapNode(
                    "Number Extractions",
                    "Short numeric value parsed (" + number + ")",
                    scriptShort);
                lineList.remove(i);
                lineList.add(i, scriptShort);
                continue;
            }
            if(!number.matches("^[0-9]+[fd]{0,1}$")) {
                continue;
            }
            if(number.matches("^[0-9]*$")
                && i < lineList.size() - 1
                && lineList.get(i + 1) instanceof ScriptOperator
                && ((ScriptOperator)lineList.get(i + 1)).getType() == ScriptOperatorType.MODULUS) {
                ScriptValue_Numeric scriptFloat = new ScriptValue_Numeric(line.getEnvironment(), Float.parseFloat(number));
                lineList.remove(i);
                lineList.add(i, scriptFloat);
                continue;
            }
            if(i > 0
                && lineList.get(i - 1) instanceof ScriptOperator
                && ((ScriptOperator)lineList.get(i - 1)).getType() == ScriptOperatorType.PERIOD) {
                number = "." + number;
                lineList.remove(i - 1);
                i--;
                if(i > 0
                    && lineList.get(i - 1) instanceof ScriptOperator
                    && ((ScriptOperator)lineList.get(i - 1)).getType() == ScriptOperatorType.MINUS) {
                    if(i <= 1 || !(lineList.get(i - 2) instanceof ScriptValue_Numeric)) {
                        number = "-" + number;
                        lineList.remove(i - 1);
                        i--;
                    }
                }
            } else if(i > 0
                && lineList.get(i - 1) instanceof ScriptOperator
                && ((ScriptOperator)lineList.get(i - 1)).getType() == ScriptOperatorType.MINUS) {
                if(i <= 1 || !(lineList.get(i - 2) instanceof ScriptValue_Numeric)) {
                    number = "-" + number;
                    lineList.remove(i - 1);
                    i--;
                }
            }
            if(i < lineList.size() - 1
                && lineList.get(i + 1) instanceof ScriptOperator
                && ((ScriptOperator)lineList.get(i + 1)).getType() == ScriptOperatorType.PERIOD) {
                number += "." + ((ScriptLine)lineList.get(i + 2)).getString();
                lineList.remove(i + 1);
                lineList.remove(i + 1);
            }
            if(number.length() > 3 && number.substring(number.length() - 2).equals("em")) {
                number = number.substring(0, number.length() - 2);
                ScriptValue_Numeric scriptFloat = new ScriptValue_Numeric(line.getEnvironment(), Float.parseFloat(number) * 14);
                assert LegacyDebugger.addSnapNode(
                    "Number Extractions",
                    "Float numeric value parsed (" + number + ")",
                    scriptFloat);
                lineList.remove(i);
                lineList.remove(i);
                lineList.add(i, scriptFloat);
                continue;
            }
            if(number.charAt(number.length() - 1) == 'f') {
                ScriptValue_Numeric scriptFloat = new ScriptValue_Numeric(line.getEnvironment(), Float.parseFloat(number));
                assert LegacyDebugger.addSnapNode(
                    "Number Extractions",
                    "Float numeric value parsed (" + number + ")",
                    scriptFloat);
                lineList.remove(i);
                lineList.add(i, scriptFloat);
                continue;
            }
            if(number.indexOf(".") != -1) {
                ScriptValue_Numeric scriptDouble = new ScriptValue_Numeric(line.getEnvironment(), Double.parseDouble(number));
                assert LegacyDebugger.addSnapNode(
                    "Number Extractions",
                    "Double numeric value parsed (" + number + ")",
                    scriptDouble);
                lineList.remove(i);
                lineList.add(i, scriptDouble);
                continue;
            }
            if(number.length() < 5) {
                ScriptValue_Numeric scriptShort = new ScriptValue_Numeric(line.getEnvironment(), Short.parseShort(number));
                lineList.remove(i);
                lineList.add(i, scriptShort);
                continue;
            }
            if(number.length() < 10) {
                ScriptValue_Numeric scriptInt = new ScriptValue_Numeric(line.getEnvironment(), Integer.parseInt(number));
                assert LegacyDebugger.addSnapNode(
                    "Number Extractions",
                    "Integer numeric value parsed (" + number + ")",
                    scriptInt);
                lineList.remove(i);
                lineList.add(i, scriptInt);
                continue;
            }
            ScriptValue_Numeric scriptLong = new ScriptValue_Numeric(line.getEnvironment(), Long.parseLong(number));
            assert LegacyDebugger.addSnapNode("Number Extractions", "Long numeric value parsed (" + number + ")", scriptLong);
            lineList.remove(i);
            lineList.add(i, scriptLong);
        }
        return lineList;
    }

    public static List<Object> extractKeywords(List<Object> lineList) throws Exception_Nodeable {
        for(int i = 0; i < lineList.size(); i++) {
            if(lineList.get(i) instanceof ScriptGroup) {
                ((ScriptGroup)lineList.get(i)).setElements(extractKeywords(((ScriptGroup)lineList.get(i)).getElements()));
                continue;
            }
            if(!(lineList.get(i) instanceof ScriptLine)) {
                continue;
            }
            if(!ScriptKeywordType.UNKNOWN.equals(ScriptKeyword.getType(((ScriptLine)lineList.get(i)).getString()))) {
                lineList.add(
                    i,
                    new ScriptKeyword((Referenced)lineList.get(i), ScriptKeyword.getType(((ScriptLine)lineList.get(i)).getString())));
                lineList.remove(i + 1);
            }
        }
        return lineList;
    }

    public static List<Object> createQuotedElements(List<Object> lineList) throws Exception_Nodeable {
        for(int i = 0; i < lineList.size(); i++) {
            if(!(lineList.get(i) instanceof ScriptLine)) {
                continue;
            }
            List<Object> returnedList = createQuotedElements((ScriptLine)lineList.get(i));
            lineList.remove(i);
            lineList.addAll(i, returnedList);
        }
        return lineList;
    }

    public static List<Object> createQuotedElements(ScriptLine line) throws Exception_Nodeable {
        int charElem = line.getString().indexOf("'");
        int stringElem = line.getString().indexOf('"');
        List<Object> list = new LinkedList<Object>();
        // If neither are found, return.
        if(charElem == -1 && stringElem == -1) {
            list.add(line);
            return list;
        } else if((charElem == -1 || stringElem < charElem) && stringElem != -1) {
            // We've found a string element
            assert stringElem != -1;
            int offset = stringElem + 1;
            int nextStringElem;
            do {
                nextStringElem = line.getString().indexOf('"', offset);
                // If it's not found, throw an error.
                if(nextStringElem == -1) {
                    throw new Exception_Nodeable_UnenclosedStringLiteral(line);
                }
                // If we enter this, we're at a literal quotation mark inside
                // our string, and must loop to find the actual closing mark.
                if(nextStringElem != 0 && '\\' == line.getString().charAt(nextStringElem - 1)) {
                    offset = nextStringElem + 1;
                    nextStringElem = -1;
                }
            } while (nextStringElem == -1);
            list.add(new ScriptLine(line.getString().substring(0, stringElem), line, 0));
            String value = line.getString().substring(stringElem + "\"".length(), nextStringElem);
            list.add(new ScriptValue_String(line.getEnvironment(), value));
            list.add(new ScriptLine(line.getString().substring(nextStringElem + "\"".length()), line, (short)(nextStringElem + "\"".length())));
            return createQuotedElements(list);
        } else {
            // We found a character-string element
            assert charElem != -1;
            int nextCharElem = line.getString().indexOf("'", charElem + 1);
            if(nextCharElem == -1) {
                throw new Exception_Nodeable_UnenclosedStringLiteral(line);
            }
            list.add(new ScriptLine(line.getString().substring(0, charElem), line, 0));
            String value = line.getString().substring(charElem + "'".length(), nextCharElem);
            list.add(new ScriptValue_String(line.getEnvironment(), value));
            list.add(new ScriptLine(line.getString().substring(nextCharElem + "'".length()), line, (short)(nextCharElem + "'".length())));
            return createQuotedElements(list);
        }
    }

    public static List<Object> removeSingleLineGroupings(List<Object> lineList, String openChar, String closingChar,
        ScriptGroup.GroupType type, boolean recurse) {
        for(int i = 0; i < lineList.size(); i++) {
            if(lineList.get(i) instanceof ScriptGroup) {
                if(recurse) {
                    ((ScriptGroup)lineList.get(i)).setElements(removeSingleLineGroupings(
                        ((ScriptGroup)lineList.get(i)).getElements(),
                        openChar,
                        closingChar,
                        type,
                        recurse));
                }
                continue;
            }
            if(!(lineList.get(i) instanceof ScriptLine)) {
                continue;
            }
            List<Object> returnedList = removeSingleLineGroupings(
                (ScriptLine)lineList.get(i),
                openChar,
                closingChar,
                type,
                recurse);
            lineList.remove(i);
            lineList.addAll(i, returnedList);
        }
        return lineList;
    }

    public static List<Object> removeSingleLineGroupings(ScriptLine line, String openChar, String closingChar,
        ScriptGroup.GroupType type, boolean recurse) {
        int endGroup = -1;
        int beginGroup = -1;
        int offset = 0;
        String string;
        while (true) {
            endGroup = line.getString().indexOf(closingChar, offset);
            if(endGroup == -1) {
                List<Object> list = new LinkedList<Object>();
                list.add(line);
                return list;
            }
            string = line.getString().substring(0, endGroup);
            beginGroup = string.lastIndexOf(openChar);
            if(beginGroup != -1) {
                break;
            }
            offset = endGroup + 1;
        }
        assert LegacyDebugger.open("Single-Line Grouping Removals", "Removing Single-Line Groupings (Syntax: "
            + openChar
            + "..."
            + closingChar
            + " )");
        assert LegacyDebugger.addNode(line);
        assert LegacyDebugger.addNode("Allowed to Recurse: " + recurse);
        List<Object> list = new LinkedList<Object>();
        List<Object> itemList = new LinkedList<Object>();
        ScriptLine newGroup = new ScriptLine(string.substring(beginGroup + openChar.length()), line, (short)(beginGroup + openChar.length()));
        assert LegacyDebugger.open("Recursing for left-side groups.");
        list.addAll(removeSingleLineGroupings(
            new ScriptLine(line.getString().substring(0, beginGroup), line, (short)0),
            openChar,
            closingChar,
            type,
            recurse));
        assert LegacyDebugger.close();
        itemList.add(newGroup);
        list.add(new ScriptGroup(line, itemList, type));
        assert LegacyDebugger.open("Recursing for right-side groups.");
        list.addAll(removeSingleLineGroupings(
            new ScriptLine(line.getString().substring(endGroup + closingChar.length()), line, (short)(endGroup + closingChar.length())),
            openChar,
            closingChar,
            type,
            recurse));
        assert LegacyDebugger.close();
        assert LegacyDebugger.close();
        return list;
    }

    public static List<Object> createGroupings(List<Object> passedList, String openChar, String closingChar,
        ScriptGroup.GroupType type, boolean recurse) throws Exception_Nodeable {
        assert LegacyDebugger.open("Character-Group Parsing", "Creating Groupings (Syntax: "
            + openChar
            + "..."
            + closingChar
            + " )");
        assert LegacyDebugger.addSnapNode(DebugString.ELEMENTS, passedList);
        assert LegacyDebugger.addNode("Allowed to Recurse: " + recurse);
        List<Object> workingStringList = removeSingleLineGroupings(passedList, openChar, closingChar, type, recurse);
        boolean foundGroup = false;
        for(int i = 0; i < workingStringList.size(); i++) {
            Object obj = workingStringList.get(i);
            if(obj instanceof ScriptGroup) {
                if(recurse) {
                    assert LegacyDebugger.open("Group found - recursing to parse");
                    ((ScriptGroup)obj).setElements(createGroupings(
                        ((ScriptGroup)obj).getElements(),
                        openChar,
                        closingChar,
                        type,
                        recurse));
                    assert LegacyDebugger.close();
                }
                continue;
            }
            if(!(obj instanceof ScriptLine)) {
                continue;
            }
            ScriptLine scriptLine = (ScriptLine)obj;
            int j = scriptLine.getString().indexOf(closingChar);
            if(j == -1) {
                continue;
            }
            assert LegacyDebugger.addSnapNode(
                "Found closing character - searching backwards for opening character",
                scriptLine);
            List<Object> newList = new LinkedList<Object>();
            newList.add(new ScriptLine(scriptLine.getString().substring(0, j), scriptLine, (short)0));
            scriptLine.setString(scriptLine.getString().substring(j + closingChar.length()));
            for(int q = i - 1; q >= 0; q--) {
                if(!(workingStringList.get(q) instanceof ScriptLine)) {
                    newList.add(workingStringList.get(q));
                    workingStringList.remove(q);
                    i--;
                    continue;
                }
                ScriptLine backwardScriptLine = (ScriptLine)workingStringList.get(q);
                int x = backwardScriptLine.getString().lastIndexOf(openChar);
                if(x == -1) {
                    if(q == 0) {
                        throw new Exception_Nodeable_UnenclosedBracket(scriptLine);
                    }
                    newList.add(backwardScriptLine);
                    workingStringList.remove(q);
                    i--;
                    continue;
                }
                assert LegacyDebugger.addSnapNode("Found opening character", backwardScriptLine);
                newList.add(new ScriptLine(backwardScriptLine.getString().substring(x + openChar.length()), backwardScriptLine, (short)(x + openChar.length())));
                backwardScriptLine.setString(backwardScriptLine.getString().substring(0, x));
                Collections.reverse(newList);
                assert LegacyDebugger.open("Recursing to parse elements in newly created group");
                workingStringList.add(i, new ScriptGroup((Referenced)newList.get(0), createGroupings(
                    newList,
                    openChar,
                    closingChar,
                    type,
                    recurse), type));
                assert LegacyDebugger.close();
                assert LegacyDebugger.open("Recursing to parse remaining elements");
                List<Object> list = createGroupings(workingStringList, openChar, closingChar, type, recurse);
                assert LegacyDebugger.close();
                assert LegacyDebugger.close();
                return list;
            }
        }
        if(!foundGroup) {
            assert LegacyDebugger.close("No group found - naturally returning");
        }
        return workingStringList;
    }

    public static List<Object> parseOperators(ScriptLine line) {
        List<Object> list = parseOperator(line, ";");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, ",");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, ".");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, ":");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "#");
        if(list != null) {
            return parseOperators(list);
        }
        // Comparision operations
        list = parseOperator(line, "==");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "!=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, ">=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "<=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, ">");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "<");
        if(list != null) {
            return parseOperators(list);
        }
        // Boolean operations
        list = parseOperator(line, "!");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "&&");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "||");
        if(list != null) {
            return parseOperators(list);
        }
        // Single-line equation operations
        list = parseOperator(line, "++");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "--");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "+=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "-=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "*=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "/=");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "%=");
        if(list != null) {
            return parseOperators(list);
        }
        // Assignment operation
        list = parseOperator(line, "=");
        if(list != null) {
            return parseOperators(list);
        }
        // Mathematical operations.
        list = parseOperator(line, "-");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "+");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "*");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "/");
        if(list != null) {
            return parseOperators(list);
        }
        list = parseOperator(line, "%");
        if(list != null) {
            return parseOperators(list);
        }
        list = new LinkedList<Object>();
        list.add(line);
        return list;
    }

    public static List<Object> splitByWhitespace(ScriptLine line) {
        assert LegacyDebugger.open("Split-By-Whitespace Operations", String.format(
            "Splitting line by whitespace (%d:'%s')",
            line.getLineNumber(),
            line.getString()));
        assert LegacyDebugger.addSnapNode("Line", line);
        List<Object> list = new LinkedList<Object>();
        if(line.getString().indexOf(" ") == -1 && line.getString().indexOf("\t") == -1) {
            list.add(line);
            assert LegacyDebugger.close("Line contains no whitespace.");
            return list;
        }
        String string = line.getString();
        String nextWord = "";
        int offset = 0;
        ScriptLine workingLine = line;
        while (string.length() > 0) {
            if(string.indexOf(" ") == 0 || string.indexOf("\t") == 0) {
                if(nextWord.length() > 0) {
                    list.add(workingLine = new ScriptLine(nextWord, workingLine, offset));
                    offset = nextWord.length();
                    nextWord = "";
                }
                offset++;
                string = string.substring(1);
            } else {
                nextWord += string.charAt(0);
                string = string.substring(1);
            }
        }
        if(nextWord.length() > 0) {
            list.add(new ScriptLine(nextWord, workingLine, offset));
        }
        assert LegacyDebugger.close("Returning list", list);
        return list;
    }

    public static List<Object> splitByWhitespace(List<Object> list) {
        assert LegacyDebugger.open("Split-By-Whitespace List Operations", "Splitting lines in list by whitespace ("
            + list.size()
            + " element(s))");
        assert LegacyDebugger.addSnapNode("Elements", list);
        for(int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if(obj instanceof ScriptGroup) {
                ((ScriptGroup)obj).setElements(splitByWhitespace(((ScriptGroup)obj).getElements()));
                continue;
            }
            if(!(obj instanceof ScriptLine)) {
                continue;
            }
            list.remove(i);
            list.addAll(i, splitByWhitespace((ScriptLine)obj));
        }
        assert LegacyDebugger.close();
        return list;
    }

    public static List<Object> parseOperator(ScriptLine line, String operator) {
        int location = line.getString().indexOf(operator);
        if(location != -1) {
            assert LegacyDebugger.open("Operator Parsing", ScriptOperator.getName(ScriptOperator.getType(operator))
                + " found in script-line: "
                + line.getString());
            assert LegacyDebugger.addNode(line);
            List<Object> list = new LinkedList<Object>();
            String string = line.getString().substring(0, location).trim();
            String originalString = line.getString();
            if(string.length() > 0) {
                list.add(line);
                line.setString(string);
            }
            list.add(new ScriptOperator(new ScriptLine(operator, line, location), ScriptOperator.getType(operator)));
            string = originalString.substring(location + operator.length()).trim();
            if(string.length() > 0) {
                list.add(new ScriptLine(string, line, (short)(location + operator.length())));
            }
            assert LegacyDebugger.close("Split-string list formed from operator parse", list);
            return list;
        }
        return null;
    }

    public static List<Object> parseOperators(List<Object> list) {
        for(int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            if(element instanceof ScriptGroup) {
                ((ScriptGroup)element).setElements(parseOperators(((ScriptGroup)element).getElements()));
                continue;
            }
            if(!(element instanceof ScriptLine)) {
                continue;
            }
            list.remove(i);
            list.addAll(i, parseOperators((ScriptLine)element));
        }
        return list;
    }

    public static List<Object> removeEmptyScriptLines(List<Object> list) {
        assert LegacyDebugger.open("Empty Script-Line Removals", "Empty Script-Line Removal");
        assert LegacyDebugger.addSnapNode(DebugString.ELEMENTS, list);
        int q = 0;
        for(int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            if(element instanceof ScriptGroup) {
                assert LegacyDebugger.open("Found script-group - recursing to parse");
                ((ScriptGroup)element).setElements(removeEmptyScriptLines(((ScriptGroup)element).getElements()));
                assert LegacyDebugger.close();
                continue;
            }
            if(!(element instanceof ScriptLine)) {
                continue;
            }
            if(((ScriptLine)element).getString().trim().length() == 0) {
                q++;
                list.remove(i);
                i--;
            }
        }
        assert LegacyDebugger.close("Final elements (" + q + " removal(s))", list);
        return list;
    }

    public static String removeSingleLineParagraphs(String string) {
        int beginParagraph = string.indexOf("/*");
        int endParagraph = string.indexOf("*/");
        if(beginParagraph != -1 && endParagraph != -1) {
            String newString = string.substring(0, beginParagraph) + string.substring(endParagraph + "*/".length());
            return newString;
        }
        return string;
    }

    public static List<Object> removeComments(List<Object> stringList) {
        boolean isParagraphCommenting = false;
        List<Object> list = new LinkedList<Object>();
        for(Object element : stringList) {
            if(!(element instanceof ScriptLine)) {
                if(!isParagraphCommenting) {
                    list.add(element);
                }
                continue;
            }
            ScriptLine scriptLine = (ScriptLine)element;
            if(isParagraphCommenting) {
                int endComment = scriptLine.getString().indexOf("*/");
                if(endComment != -1) {
                    scriptLine.setString(scriptLine.getString().substring(endComment + "*/".length()));
                    isParagraphCommenting = false;
                } else {
                    continue;
                }
            }
            int oldStringLength = 0;
            do {
                oldStringLength = scriptLine.getString().length();
                scriptLine.setString(removeSingleLineParagraphs(scriptLine.getString()));
            } while (oldStringLength != scriptLine.getString().length());
            int beginParagraph = scriptLine.getString().indexOf("/*");
            int lineComment = scriptLine.getString().indexOf("//");
            if(lineComment != -1 && beginParagraph != -1) {
                if(lineComment < beginParagraph) {
                    scriptLine.setString(scriptLine.getString().substring(0, lineComment));
                } else {
                    isParagraphCommenting = true;
                    int endComment = scriptLine.getString().indexOf("*/");
                    if(endComment != -1) {
                        scriptLine.setString(scriptLine.getString().substring(0, beginParagraph)
                            + scriptLine.getString().substring(endComment + "*/".length()));
                        isParagraphCommenting = false;
                    } else {
                        scriptLine.setString(scriptLine.getString().substring(0, beginParagraph));
                    }
                }
            } else if(lineComment != -1) {
                scriptLine.setString(scriptLine.getString().substring(0, lineComment));
            } else if(beginParagraph != -1) {
                isParagraphCommenting = true;
                int endComment = scriptLine.getString().indexOf("*/");
                if(endComment != -1) {
                    scriptLine.setString(scriptLine.getString().substring(0, beginParagraph)
                        + scriptLine.getString().substring(endComment + "*/".length()));
                    isParagraphCommenting = false;
                } else {
                    scriptLine.setString(scriptLine.getString().substring(0, beginParagraph));
                }
            }
            list.add(scriptLine);
        }
        return list;
    }

    // Script->Engine conversion functions
    public static String getString(Object obj) throws Exception_Nodeable {
        return (String)convert(obj);
    }

    public static Boolean getBoolean(Object obj) throws Exception_Nodeable {
        return (Boolean)convert(obj);
    }

    public static Number getNumber(Object obj) throws Exception_Nodeable {
        return (Number)convert(obj);
    }

    public static Short getShort(Object obj) throws Exception_Nodeable {
        return (Short)convert(((ScriptValue_Abstract)obj).castToType(null, ScriptValueType.SHORT));
    }

    public static Integer getInteger(Object obj) throws Exception_Nodeable {
        return (Integer)convert(((ScriptValue_Abstract)obj).castToType(null, ScriptValueType.INT));
    }

    public static Long getLong(Object obj) throws Exception_Nodeable {
        return (Long)convert(((ScriptValue_Abstract)obj).castToType(null, ScriptValueType.LONG));
    }

    public static Float getFloat(Object obj) throws Exception_Nodeable {
        return (Float)convert(((ScriptValue_Abstract)obj).castToType(null, ScriptValueType.FLOAT));
    }

    public static Double getDouble(Object obj) throws Exception_Nodeable {
        return (Double)convert(((ScriptValue_Abstract)obj).castToType(null, ScriptValueType.DOUBLE));
    }

    public static GraphicalElement getGraphicalElement(Object obj) throws Exception_Nodeable {
        return (GraphicalElement)convert(obj);
    }

    public static List<ScriptValue_Abstract> getList(Object obj) throws Exception_Nodeable {
        return ((FauxTemplate_List)getCoreValue(obj)).getList();
    }

    public static DiscreteRegion getDiscreteRegion(Object obj) throws Exception_Nodeable {
        return (DiscreteRegion)convert(obj);
    }

    public static Point getPoint(Object obj) throws Exception_Nodeable {
        return (Point)convert(obj);
    }

    public static InterfaceElement getElement(Object obj) throws Exception_Nodeable {
        return (InterfaceElement)convert(obj);
    }

    public static Object getObject(Object obj) throws Exception_Nodeable {
        return convert(obj);
    }

    public static ScriptTemplate_Abstract getTemplate(Object obj) throws Exception_Nodeable {
        return (ScriptTemplate_Abstract)((ScriptValue_Abstract)obj).getValue();
    }

    public static Terrestrial getTerrestrial(Object obj) throws Exception_Nodeable {
        return (Terrestrial)convert(obj);
    }

    public static Scheduler getScheduler(Object obj) throws Exception_Nodeable {
        return (Scheduler)convert(obj);
    }

    public static Asset getAsset(Object obj) throws Exception_Nodeable {
        return (Asset)convert(obj);
    }

    public static ScriptTemplate_Abstract getSchedulerListener(Object obj) throws Exception_Nodeable {
        return (ScriptTemplate_Abstract)getCoreValue(obj);
    }

    public static Scenario getScenario(Object obj) throws Exception_Nodeable {
        return (Scenario)convert(obj);
    }

    public static Color getColor(Object obj) throws Exception_Nodeable {
        return (Color)convert(obj);
    }

    // Engine->Script Conversion functions
    public static FauxTemplate_Ace getRiffAce(Object obj) throws Exception_Nodeable {
        return (FauxTemplate_Ace)convert(obj);
    }

    public static Ace getAce(Object obj) throws Exception_Nodeable {
        return (Ace)convert(obj);
    }

    public static FauxTemplate_Archetype getRiffArchetype(Object obj) throws Exception_Nodeable {
        return (FauxTemplate_Archetype)convert(obj);
    }

    public static Archetype getArchetype(Object obj) throws Exception_Nodeable {
        return (Archetype)convert(obj);
    }

    public static Stylesheet getRiffStylesheet(Object obj) throws Exception_Nodeable {
        return (Stylesheet)convert(obj);
    }

    public static Stylesheet getStylesheet(Object obj) throws Exception_Nodeable {
        return (Stylesheet)convert(obj);
    }

    public static FauxTemplate_InterfaceElement getRiffElement(InterfaceElement elem) {
        return (FauxTemplate_InterfaceElement)convert(elem);
    }

    public static FauxTemplate_Asset getRiffAsset(Asset asset) {
        return (FauxTemplate_Asset)convert(asset);
    }

    public static ScriptValue_Numeric getRiffNumber(ScriptEnvironment env, Number value) {
        return (ScriptValue_Numeric)convert(env, value);
    }

    public static ScriptValue_Numeric getRiffDouble(ScriptEnvironment env, double value) {
        return (ScriptValue_Numeric)convert(env, value);
    }

    public static ScriptValue_Numeric getRiffFloat(ScriptEnvironment env, float value) {
        return (ScriptValue_Numeric)convert(env, value);
    }

    public static ScriptValue_Numeric getRiffLong(ScriptEnvironment env, long value) {
        return (ScriptValue_Numeric)convert(env, value);
    }

    public static ScriptValue_Numeric getRiffInt(ScriptEnvironment env, int value) {
        return (ScriptValue_Numeric)convert(env, value);
    }

    public static ScriptValue_Numeric getRiffShort(ScriptEnvironment env, short value) {
        return (ScriptValue_Numeric)convert(env, value);
    }

    public static ScriptValue_Boolean getRiffBoolean(ScriptEnvironment env, boolean value) {
        return (ScriptValue_Boolean)convert(env, value);
    }

    public static ScriptValue_String getRiffString(ScriptEnvironment env, String value) {
        return (ScriptValue_String)convert(env, value);
    }

    public static FauxTemplate_Point getRiffPoint(Object obj) throws Exception_Nodeable {
        return (FauxTemplate_Point)convert(obj);
    }

    public static FauxTemplate_List getRiffList(ScriptEnvironment env, List<ScriptValue_Abstract> list) {
        return (FauxTemplate_List)convert(env, list);
    }

    public static FauxTemplate_Color getRiffColor(Object obj) throws Exception_Nodeable {
        return (FauxTemplate_Color)convert(obj);
    }

    public static FauxTemplate_Color getRiffColor(ScriptEnvironment env, Color color) {
        return (FauxTemplate_Color)convert(env, color);
    }

    public static FauxTemplate_Terrestrial getRiffTerrestrial(Terrestrial terrestrial) throws Exception_Nodeable {
        return (FauxTemplate_Terrestrial)convert(terrestrial);
    }

    public static FauxTemplate_Scheduler getRiffScheduler(Scheduler scheduler) {
        return (FauxTemplate_Scheduler)convert(scheduler);
    }

    public static FauxTemplate_Path getRiffPath(Point_Path path) {
        return (FauxTemplate_Path)convert(path);
    }

    public static FauxTemplate_DiscreteRegion getRiffDiscreteRegion(DiscreteRegion region) {
        return (FauxTemplate_DiscreteRegion)convert(region);
    }

    // Conversion assistance functions
    public static ScriptValue_Abstract getCoreValue(Object obj) throws Exception_Nodeable {
        return ((ScriptValue_Abstract)obj).getValue();
    }

    public static Object convert(Object object) throws Exception_Nodeable {
        if(object instanceof ScriptConvertible) {
            return Parser.convert((ScriptConvertible)object);
        }
        return Parser.convert((ScriptConvertible)((ScriptValue_Abstract)object).getValue());
    }

    public static Object convert(ScriptConvertible object) {
        return object.convert();
    }

    // Generic-conversion fxns
    public static Object convert(ScriptEnvironment env, Number num) {
        return new ScriptValue_Numeric(env, num);
    }

    public static Object convert(ScriptEnvironment env, boolean value) {
        return new ScriptValue_Boolean(env, value);
    }

    public static Object convert(ScriptEnvironment env, short num) {
        return new ScriptValue_Numeric(env, new Short(num));
    }

    public static Object convert(ScriptEnvironment env, int num) {
        return new ScriptValue_Numeric(env, new Integer(num));
    }

    public static Object convert(ScriptEnvironment env, long num) {
        return new ScriptValue_Numeric(env, new Long(num));
    }

    public static Object convert(ScriptEnvironment env, float num) {
        return new ScriptValue_Numeric(env, new Float(num));
    }

    public static Object convert(ScriptEnvironment env, double num) {
        return new ScriptValue_Numeric(env, new Double(num));
    }

    public static Object convert(ScriptEnvironment env, String string) {
        return new ScriptValue_String(env, string);
    }

    public static Object convert(ScriptEnvironment env, Color color) {
        FauxTemplate_Color fauxColor = new FauxTemplate_Color(env, ScriptValueType.createType(
            env,
            FauxTemplate_Color.COLORSTRING));
        fauxColor.setColor(color);
        return fauxColor;
    }

    public static Object convert(ScriptEnvironment env, List<ScriptValue_Abstract> elements) {
        FauxTemplate_List list = new FauxTemplate_List(env, ScriptValueType.createType(
            env,
            FauxTemplate_List.LISTSTRING));
        list.setList(elements);
        return list;
    }

    // Miscellaneous functions
    public static String printParseList(List<Object> list, int offset) {
        String string = new String();
        for(int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if(obj instanceof ScriptGroup) {
                string += RiffToolbox.tab(offset)
                    + "Script Group:\n"
                    + printParseList(((ScriptGroup)obj).getElements(), offset + 1);
                continue;
            }
            string += RiffToolbox.tab(offset) + obj + "\n";
        }
        return string;
    }

    public static void compile(ScriptEnvironment environment, Reader reader) {
        List<Object> scriptLines = new ArrayList<Object>();
        try {
            LineNumberReader lineReader = new LineNumberReader(reader);
            for(String line = ""; line != null; line = lineReader.readLine()) {
                scriptLines.add(new ScriptLine(environment, reader.toString(), lineReader.getLineNumber(), line));
            }
        } catch(IOException ex) {
            ex.printStackTrace();
            Logging.logException(ex);
        }
        Parser.preparseFile(environment, reader.toString(), scriptLines);
    }
}
