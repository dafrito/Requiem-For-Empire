package com.dafrito.ide.legacy;

import java.awt.Component;

import com.dafrito.script.ScriptEnvironment;

public interface ScriptEditorContext {

    ScriptEnvironment getEnvironment();
    void resetTitle(ScriptEditor scriptEditor);
    void showScriptEditor(ScriptEditor scriptEditor);
    void setChanged(boolean changed);
    Component getComponent();
    void setCanRedo(boolean redo);
}
