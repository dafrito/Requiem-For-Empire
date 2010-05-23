package com.dafrito;

import com.dafrito.gui.ExtensionFilter;
import com.dafrito.ide.IDE;
import com.dafrito.ide.editor.Editor;
import com.dafrito.ide.logging.LoggingPerspective;
import com.dafrito.ide.script.ScriptEditorPerspective;
import com.dafrito.ide.script.ScriptEngineAdapter;
import com.dafrito.script.riffscript.LegacyRiffScriptEngine;
import com.dafrito.swing.Dialogs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFrame;


public class RequiemForEmpire implements Runnable {

    public IDE constructIDE() {
        IDE ide = new IDE();

        ide.add(new LoggingPerspective("com.dafrito", "com.dafrito.rfe"));

        ScriptEditorPerspective scriptEditor = new ScriptEditorPerspective();
        ide.add(scriptEditor);

        // Attach a simple adapter for our old scripting engine.
        scriptEditor.setScriptEngineAdapter(new ScriptEngineAdapter<Reader>(new LegacyRiffScriptEngine()) {

                @Override
                protected Reader convert(File file, Editor editor) throws IOException {
                    return new FileReader(file);
                }

            });

        // Open all Riffscript files in the current directory.
        File folder = new File(".");
        ExtensionFilter filter = new ExtensionFilter();
        filter.addExtension("RiffScript");
        File[] files = folder.listFiles(filter);
        for (File file : files) {
            if (file.isFile()) {
                try {
                    scriptEditor.add(Editor.openFile(file));
                } catch (IOException e) {
                    Dialogs.error("File failed to open: " + e.getLocalizedMessage(),
                                  "Error opening " + file.getName());
                }
            }
        }

        return ide;
    }

    public void run() {
        JFrame frame = new JFrame("Requiem for Empire");
        frame.setSize(800, 600);
        frame.setVisible(true);
        this.constructIDE().populate(frame);

    }

    public static void main(String[] args) {
        Runner.run(new RequiemForEmpire(), false);
    }

}
