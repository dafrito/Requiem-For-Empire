package com.dafrito.script;

import com.dafrito.logging.DebugString;
import com.dafrito.logging.LegacyDebugger;

public class ScriptLine extends ScriptElement {

    private String string;

    public ScriptLine(ScriptEnvironment env, String filename, int num, String string) {
        super(env, filename, num, string, string.length());
        this.string = string;
    }

    public ScriptLine(String string, ScriptLine otherLine, int oLO) {
        super(otherLine, oLO, string.length());
        this.string = string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    @Override
    public String toString() {
        return getFilename() + "@" + getLineNumber() + ": \"" + this.string + '"';
    }

    @Override
    public boolean nodificate() {
        assert LegacyDebugger.open(LegacyDebugger.getString(DebugString.SCRIPTLINE) + this.string);
        assert super.nodificate();
        assert LegacyDebugger.close();
        return true;
    }
}
