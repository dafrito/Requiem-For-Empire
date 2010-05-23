package com.dafrito.script;

import java.util.List;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;
import com.dafrito.debug.Exception_InternalError;

public class ScriptGroup extends ScriptElement {
    public enum GroupType {
        curly, parenthetical
    }

    protected List<Object> elements;
    private GroupType type;

    public ScriptGroup(Referenced ref, List<Object> elements, GroupType type) {
        super(ref);
        this.elements = elements;
        this.type = type;
    }

    public GroupType getType() {
        return this.type;
    }

    public List<Object> getElements() {
        return this.elements;
    }

    public void setElements(List<Object> list) {
        this.elements = list;
    }

    @Override
    public boolean nodificate() {
        switch(this.type) {
            case curly:
                assert LegacyDebugger.open(DebugString.SCRIPTGROUPCURLY);
                break;
            case parenthetical:
                assert LegacyDebugger.open(DebugString.SCRIPTGROUPPARENTHETICAL);
                break;
            default:
                throw new Exception_InternalError("Invalid default");
        }
        assert LegacyDebugger.addSnapNode("Elements", this.elements);
        assert LegacyDebugger.close();
        return true;
    }
}
