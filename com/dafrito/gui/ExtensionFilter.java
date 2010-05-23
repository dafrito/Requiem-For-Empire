package com.dafrito.gui;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.dafrito.util.RiffToolbox;

public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
    private Set<String> m_extensions = new HashSet<String>();
    private String m_description;

    public ExtensionFilter() {
        // Do nothing.
    }

    public ExtensionFilter(String desc) {
        this.m_description = desc;
    }

    public void addExtension(String extension) {
        this.m_extensions.add(extension.toLowerCase());
    }

    @Override
    public boolean accept(File file) {
        if(file.isDirectory()) {
            return true;
        }
        String string = RiffToolbox.getExtension(file);
        for(String test : this.m_extensions) {
            if(string.equals(test)) {
                return true;
            }
        }
        return false;
    }

    public void setDescription(String desc) {
        this.m_description = desc;
    }

    @Override
    public String getDescription() {
        return this.m_description;
    }
}
