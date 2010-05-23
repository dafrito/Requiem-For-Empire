package com.dafrito.script;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptFunction implements Nodeable, ScriptFunction_Abstract {
    public static String getDisplayableFunctionName(String name) {
        if(name == null || name.equals("")) {
            return "constructor";
        }
        return name;
    }

    private ScriptValueType type;
    private List<ScriptValue_Abstract> params;
    private ScriptKeywordType permission;
    private ScriptValue_Abstract returnValue;
    private boolean isAbstract, isStatic;
    private List<ScriptExecutable> expressions = new LinkedList<ScriptExecutable>();

    public ScriptFunction(ScriptValueType returnType, List<ScriptValue_Abstract> params, ScriptKeywordType permission,
        boolean isAbstract, boolean isStatic) {
        this.type = returnType;
        this.params = params;
        this.permission = permission;
        this.isAbstract = isAbstract;
        this.isStatic = isStatic;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public ScriptKeywordType getPermission() {
        return this.permission;
    }

    public ScriptValueType getReturnType() {
        return this.type;
    }

    public ScriptValue_Abstract getReturnValue() {
        return this.returnValue;
    }

    public void setReturnValue(Referenced ref, ScriptValue_Abstract value) throws Exception_Nodeable {
        if(getReturnType().equals(ScriptKeywordType.VOID)) {
            return;
        }
        assert LegacyDebugger.open("Setting Return-Value");
        assert LegacyDebugger.addSnapNode("Function", this);
        assert LegacyDebugger.addSnapNode("Value", value);
        this.returnValue = value.castToType(ref, getReturnType());
        assert LegacyDebugger.close();
    }

    public List<ScriptValue_Abstract> getParameters() {
        return this.params;
    }

    public void execute(Referenced ref, List<ScriptValue_Abstract> valuesGiven) throws Exception_Nodeable {
        String currNode = "Executing Function Expressions (" + this.expressions.size() + " expressions)";
        assert LegacyDebugger.open("Function Expression Executions", currNode);
        if(valuesGiven != null && valuesGiven.size() > 0) {
            assert LegacyDebugger.open("Assigning Initial Parameters (" + valuesGiven.size() + " parameter(s))");
            assert areParametersConvertible(valuesGiven) : "Parameters-convertible test failed in execute";
            for(int i = 0; i < getParameters().size(); i++) {
                getParameters().get(i).setValue(ref, valuesGiven.get(i));
            }
            assert LegacyDebugger.close();
        }
        for(ScriptExecutable exec : this.expressions) {
            exec.execute();
            if(exec instanceof Returnable && ((Returnable)exec).shouldReturn()) {
                setReturnValue(exec.getDebugReference(), ((Returnable)exec).getReturnValue());
                assert LegacyDebugger.ensureCurrentNode(currNode);
                assert LegacyDebugger.close();
                return;
            }
        }
        if(!LegacyDebugger.ensureCurrentNode(currNode)) {
            
            LegacyDebugger.ensureCurrentNode(currNode);
            //assert false;
        }
        assert LegacyDebugger.close();
    }

    public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
        assert exp != null;
        this.expressions.add(exp);
    }

    public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
        for(ScriptExecutable exec : list) {
            addExpression(exec);
        }
    }

    public boolean areParametersConvertible(List<ScriptValue_Abstract> list) {
        return areParametersConvertible(getParameters(), list);
    }

    public static boolean areParametersConvertible(List<ScriptValue_Abstract> source, List<ScriptValue_Abstract> list) {
        assert LegacyDebugger.open("Parameter-Convertibility Tests", "Parameter-Convertibility Test");
        assert LegacyDebugger.addNode("Keys must be convertible to their function-param socket counterpart.");
        assert LegacyDebugger.addSnapNode("Function-Parameter Sockets", source);
        assert LegacyDebugger.addSnapNode("Parameter Keys", list);
        if(list.size() != source.size()) {
            assert LegacyDebugger.close("Parameter sizes do not match (" + list.size() + " and " + source.size() + ")");
            return false;
        }
        for(int i = 0; i < list.size(); i++) {
            if(!list.get(i).isConvertibleTo(source.get(i).getType())) {
                assert LegacyDebugger.close("Parameters are not equal ("
                    + source.get(i).getType()
                    + " and "
                    + list.get(i).getType()
                    + ")");
                return false;
            }
        }
        assert LegacyDebugger.close("Parameters match.");
        return true;
    }

    public boolean areParametersEqual(List<ScriptValue_Abstract> list) {
        return areParametersEqual(getParameters(), list);
    }

    public static boolean areParametersEqual(List<ScriptValue_Abstract> source, List<ScriptValue_Abstract> list) {
        assert LegacyDebugger.open("Parameter-Equality Tests", "Parameter-Equality Test");
        assert LegacyDebugger.addSnapNode("Function-Parameter Sockets", source);
        assert LegacyDebugger.addSnapNode("Parameter Keys", list);
        if(list.size() != source.size()) {
            assert LegacyDebugger.close("Parameter sizes do not match (" + list.size() + " and " + source.size() + ")");
            return false;
        }
        for(int i = 0; i < list.size(); i++) {
            if(!source.get(i).getType().equals(list.get(i).getType())) {
                assert LegacyDebugger.close("Parameters are not equal ("
                    + source.get(i).getType()
                    + " and "
                    + list.get(i).getType()
                    + ")");
                return false;
            }
        }
        assert LegacyDebugger.close("Parameters match.");
        return true;
    }

    public boolean nodificate() {
        assert LegacyDebugger.open("Script-Function (Returning " + this.type.getName() + ")");
        if(this.params != null && this.params.size() > 0) {
            assert LegacyDebugger.addSnapNode("Parameters: " + this.params.size() + " parameter(s)", this.params);
        }
        if(this.expressions != null && this.expressions.size() > 0) {
            assert LegacyDebugger.addSnapNode(
                "Expressions: " + this.expressions.size() + " expression(s)",
                this.expressions);
        }
        if(this.permission == null) {
            LegacyDebugger.addNode(DebugString.PERMISSIONNULL);
        } else {
            switch(this.permission) {
                case PRIVATE:
                    assert LegacyDebugger.addNode(DebugString.PERMISSIONPRIVATE);
                    break;
                case PROTECTED:
                    LegacyDebugger.addNode(DebugString.PERMISSIONPROTECTED);
                    break;
                case PUBLIC:
                    LegacyDebugger.addNode(DebugString.PERMISSIONPUBLIC);
                    break;
                default:
                    throw new UnsupportedOperationException("Unexpected Default");
            }
        }
        assert LegacyDebugger.addNode("Abstract: " + this.isAbstract);
        assert LegacyDebugger.addNode("Static: " + this.isStatic);
        assert LegacyDebugger.addNode("Return Value Reference: " + this.returnValue);
        assert LegacyDebugger.close();
        return true;
    }
}
