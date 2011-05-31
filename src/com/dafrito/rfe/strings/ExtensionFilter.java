package com.dafrito.rfe.strings;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class ExtensionFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
	private Set<String> extensions = new HashSet<String>();
	private String description;

	public ExtensionFilter() {
	}

	public ExtensionFilter(String desc) {
		this.description = desc;
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String string = Strings.getExtension(file);
		for (String test : this.extensions) {
			if (string.equals(test)) {
				return true;
			}
		}
		return false;
	}

	public void addExtension(String extension) {
		this.extensions.add(extension.toLowerCase());
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}
}
