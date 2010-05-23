package com.dafrito.script.executable;

import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.Referenced;
import com.dafrito.script.Returnable;
import com.dafrito.script.ScriptElement;
import com.dafrito.script.types.ScriptValue_Abstract;
import com.dafrito.script.types.ScriptValue_Boolean;

public class ScriptExecutable_ForStatement extends ScriptElement implements ScriptExecutable, Returnable {
    private ScriptExecutable initializer, tester, repeater;
    private boolean shouldReturn = false;
    private ScriptValue_Abstract returnValue;
    private List<ScriptExecutable> expressions;

    public ScriptExecutable_ForStatement(ScriptExecutable initializer, ScriptExecutable tester,
        ScriptExecutable repeater, List<ScriptExecutable> expressions) {
        super((Referenced)initializer);
        this.initializer = initializer;
        this.tester = tester;
        this.repeater = repeater;
        this.expressions = expressions;
    }

    // Returnable implementation
    public boolean shouldReturn() {
        return this.shouldReturn;
    }

    public ScriptValue_Abstract getReturnValue() throws Exception_Nodeable {
        if(this.returnValue != null) {
            this.returnValue = this.returnValue.getValue();
        }
        return this.returnValue;
    }

    // ScriptExecutable implementation
    public ScriptValue_Abstract execute() throws Exception_Nodeable {
        assert LegacyDebugger.open("For-Statement Executions", "Executing For-Statement");
        getEnvironment().advanceNestedStack();
        assert LegacyDebugger.open("Initializing");
        this.initializer.execute();
        assert LegacyDebugger.close();
        while (((ScriptValue_Boolean)this.tester.execute().getValue()).getBooleanValue()) {
            assert LegacyDebugger.open("Looping", "Looping iteration");
            getEnvironment().advanceNestedStack();
            for(ScriptExecutable exec : this.expressions) {
                exec.execute();
                if(exec instanceof Returnable && ((Returnable)exec).shouldReturn()) {
                    this.returnValue = ((Returnable)exec).getReturnValue();
                    this.shouldReturn = true;
                    assert LegacyDebugger.close();
                    return null;
                }
            }
            getEnvironment().retreatNestedStack();
            assert LegacyDebugger.close();
            assert LegacyDebugger.open("Executing repeater");
            this.repeater.execute();
            assert LegacyDebugger.close();
        }
        getEnvironment().retreatNestedStack();
        assert LegacyDebugger.close();
        return null;
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Script For-Statement");
        assert LegacyDebugger.addSnapNode("Initializer", this.initializer);
        assert LegacyDebugger.addSnapNode("Boolean expression", this.tester);
        assert LegacyDebugger.addSnapNode("Repeating expression", this.repeater);
        assert LegacyDebugger.addSnapNode("Expressions", this.expressions);
        assert LegacyDebugger.close();
        return true;
    }
}
