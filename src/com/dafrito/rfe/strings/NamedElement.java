package com.dafrito.rfe.strings;
public class NamedElement {
	String name, formalName, adjective;

	public NamedElement() {
	}

	public NamedElement(String name, String formal, String adj) {
		this.name = name;
		this.formalName = formal;
		this.adjective = adj;
	}

	public String getAdjective() {
		return this.adjective;
	}

	public String getFormalName() {
		return this.formalName;
	}

	public String getName() {
		return this.name;
	}

	public void setAdjective(String adjective) {
		this.adjective = adjective;
	}

	public void setFormalName(String name) {
		this.formalName = name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
