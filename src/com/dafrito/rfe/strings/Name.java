package com.dafrito.rfe.strings;

import com.dafrito.rfe.inspect.Inspectable;

/**
 * An immutable representation of a name.
 * 
 * @author Aaron Faanes
 */
@Inspectable
public class Name {
	private final String name, formalName, adjective;

	public Name(final String name, final String formalName, final String adjective) {
		if (name == null) {
			throw new NullPointerException("name must not be null");
		}
		this.name = name;
		if (formalName == null) {
			throw new NullPointerException("formalName must not be null");
		}
		this.formalName = formalName;
		if (adjective == null) {
			throw new NullPointerException("adjective must not be null");
		}
		this.adjective = adjective;
	}

	@Inspectable
	public String getName() {
		return this.name;
	}

	@Inspectable
	public String getFormalName() {
		return this.formalName;
	}

	@Inspectable
	public String getAdjective() {
		return this.adjective;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Name)) {
			return false;
		}
		Name other = (Name) obj;
		return this.getName().equals(other.getName()) &&
				this.getFormalName().equals(other.getFormalName()) &&
				this.getAdjective().equals(other.getAdjective());
	}

	@Override
	public int hashCode() {
		int result = 11;
		result = 31 * result + this.getName().hashCode();
		result = 31 * result + this.getFormalName().hashCode();
		result = 31 * result + this.getAdjective().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return String.format("Name[s:%s, f:%s, a:%s]", this.getName(), this.getFormalName(), this.getAdjective());
	}

}
