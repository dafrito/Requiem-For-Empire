package com.dafrito.script;

import java.util.Collection;
import java.util.List;

import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.script.executable.ScriptExecutable;
import com.dafrito.script.templates.FauxTemplate;
import com.dafrito.script.types.ScriptValueType;
import com.dafrito.script.types.ScriptValue_Abstract;

public class ScriptFunction_Faux extends ScriptFunction {
    private FauxTemplate fauxTemplate;
    private ScriptTemplate_Abstract object;
    private String name;

    public ScriptFunction_Faux(FauxTemplate template, String name, ScriptValueType type,
        List<ScriptValue_Abstract> params, ScriptKeywordType permission, boolean isAbstract, boolean isStatic) {
        super(type, params, permission, isAbstract, isStatic);
        this.fauxTemplate = template;
        this.name = name;
    }

    public void setFauxTemplate(ScriptTemplate_Abstract template) {
        assert LegacyDebugger.open("Faux Function Referenced-Template Changes", "Changing Faux-Function Object");
        assert LegacyDebugger.addNode(this);
        assert LegacyDebugger.addSnapNode("New object", template);
        this.fauxTemplate = (FauxTemplate)template;
        assert LegacyDebugger.close();
    }

    public void setTemplate(ScriptTemplate_Abstract template) {
        assert LegacyDebugger.open("Faux Function Object Changes", "Changing Object");
        assert LegacyDebugger.addNode(this);
        assert LegacyDebugger.addSnapNode("New object", template);
        this.object = template;
        assert LegacyDebugger.close();
    }

    @Override
    public void execute(Referenced ref, List<ScriptValue_Abstract> params) throws Exception_Nodeable {
        if(this.name.equals("")) {
            setReturnValue(ref, this.fauxTemplate.execute(ref, this.name, params, null));
        } else {
            if(this.object == null) {
                this.object = this.fauxTemplate;
            }
            setReturnValue(ref, this.fauxTemplate.execute(ref, this.name, params, this.object));
        }
    }

    @Override
    public void addExpression(ScriptExecutable exp) throws Exception_Nodeable {
        throw new Exception_InternalError("Invalid call in faux function");
    }

    @Override
    public void addExpressions(Collection<ScriptExecutable> list) throws Exception_Nodeable {
        throw new Exception_InternalError("Invalid call in faux function");
    }

    // Nodeable implementation
    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open("Faux Script-Function (" + ScriptFunction.getDisplayableFunctionName(this.name) + ")");
        assert super.nodificate();
        assert LegacyDebugger.addNode("Faux Template Type: " + this.fauxTemplate.getType());
        assert LegacyDebugger.close();
        return true;
    }
}
