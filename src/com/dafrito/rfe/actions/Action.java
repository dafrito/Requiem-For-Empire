package com.dafrito.rfe.actions;

public abstract class Action implements Comparable<Action> {
	public static void execute(Order superOrder) throws FailedActionException {
	}

	public abstract String getName();

	@Override
	public int compareTo(Action obj) {
		return this.getName().compareTo(obj.getName());
	}

	// XXX This shouldn't use #toString; it should use a different method
	@Override
	public boolean equals(Object obj) {
		return this.getName().equals(((Action) obj).getName());
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
}
