package com.dafrito.rfe.actions;

public abstract class Action implements Comparable<Action> {
	public abstract String getName();

	@Override
	public int compareTo(Action obj) {
		return this.getName().compareTo(obj.getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Action)) {
			return false;
		}
		return this.getName().equals(((Action) obj).getName());
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
}
