package com.dafrito.script;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;

public class ScriptOperator extends ScriptElement {
    private final ScriptOperatorType type;

    public ScriptOperator(ScriptLine line, ScriptOperatorType type) {
        super(line);
        this.type = type;
    }

    public static boolean isSemicolon(ScriptOperator op) {
        return op.getType() == ScriptOperatorType.SEMICOLON;
    }

    public ScriptOperatorType getType() {
        return this.type;
    }

    public String getName() {
        return getName(getType());
    }

    public static String getName(ScriptOperatorType operatorCode) {
        switch(operatorCode) {
            case SEMICOLON:
                return "Semicolon";
            case COMMA:
                return "Comma";
            case EQUIVALENCY:
                return "Equivalency";
            case GREATEREQUALS:
                return "Greater than or Equals to";
            case LESSEQUALS:
                return "Less than or Equals to";
            case GREATER:
                return "Greater than";
            case LESS:
                return "Less than";
            case NOT:
                return "Not";
            case AND:
                return "And";
            case OR:
                return "Or";
            case NONEQUIVALENCY:
                return "Nonequivalency";
            case INCREMENT:
                return "Increment";
            case DECREMENT:
                return "Decrement";
            case PLUSEQUALS:
                return "Plus-Equals";
            case MINUSEQUALS:
                return "Minus-Equals";
            case MULTIPLYEQUALS:
                return "Multiply-Equals";
            case DIVIDEEQUALS:
                return "Divide-Equals";
            case MODULUSEQUALS:
                return "Modulus-Equals";
            case ASSIGNMENT:
                return "Assignment";
            case MINUS:
                return "Dash";
            case PLUS:
                return "Addition";
            case MULTIPLY:
                return "Multiply";
            case DIVIDE:
                return "Divide";
            case MODULUS:
                return "Modulus";
            case PERIOD:
                return "Period";
            case COLON:
                return "Colon";
            case POUNDSIGN:
                return "Pound Sign";
            default:
                return "Unknown";
        }
    }

    public static ScriptOperatorType getType(String string) {
        if(string.equals(";")) {
            return ScriptOperatorType.SEMICOLON;
        }
        if(string.equals(",")) {
            return ScriptOperatorType.COMMA;
        }
        if(string.equals("==")) {
            return ScriptOperatorType.EQUIVALENCY;
        }
        if(string.equals(">=")) {
            return ScriptOperatorType.GREATEREQUALS;
        }
        if(string.equals("<=")) {
            return ScriptOperatorType.LESSEQUALS;
        }
        if(string.equals(">")) {
            return ScriptOperatorType.GREATER;
        }
        if(string.equals("<")) {
            return ScriptOperatorType.LESS;
        }
        if(string.equals("!=")) {
            return ScriptOperatorType.NONEQUIVALENCY;
        }
        if(string.equals("!")) {
            return ScriptOperatorType.NOT;
        }
        if(string.equals("&&")) {
            return ScriptOperatorType.AND;
        }
        if(string.equals("||")) {
            return ScriptOperatorType.OR;
        }
        if(string.equals("++")) {
            return ScriptOperatorType.INCREMENT;
        }
        if(string.equals("--")) {
            return ScriptOperatorType.DECREMENT;
        }
        if(string.equals("+=")) {
            return ScriptOperatorType.PLUSEQUALS;
        }
        if(string.equals("-=")) {
            return ScriptOperatorType.MINUSEQUALS;
        }
        if(string.equals("*=")) {
            return ScriptOperatorType.MULTIPLYEQUALS;
        }
        if(string.equals("/=")) {
            return ScriptOperatorType.DIVIDEEQUALS;
        }
        if(string.equals("%=")) {
            return ScriptOperatorType.MODULUSEQUALS;
        }
        if(string.equals("=")) {
            return ScriptOperatorType.ASSIGNMENT;
        }
        if(string.equals("-")) {
            return ScriptOperatorType.MINUS;
        }
        if(string.equals("+")) {
            return ScriptOperatorType.PLUS;
        }
        if(string.equals("*")) {
            return ScriptOperatorType.MULTIPLY;
        }
        if(string.equals("/")) {
            return ScriptOperatorType.DIVIDE;
        }
        if(string.equals("%")) {
            return ScriptOperatorType.MODULUS;
        }
        if(string.equals(".")) {
            return ScriptOperatorType.PERIOD;
        }
        if(string.equals(":")) {
            return ScriptOperatorType.COLON;
        }
        if(string.equals("#")) {
            return ScriptOperatorType.POUNDSIGN;
        }
        return ScriptOperatorType.UNKNOWN;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ScriptOperatorType) {
            return getType().equals(o);
        }
        if(!(o instanceof ScriptOperator)) {
            return false;
        }
        return getType() == ((ScriptOperator)o).getType();
    }

    @Override
    public String toString() {
        return this.type.toString();
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open(LegacyDebugger.getString(DebugString.SCRIPTOPERATOR) + getName());
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
