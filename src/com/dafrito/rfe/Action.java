package com.dafrito.rfe;

public abstract class Action implements Comparable<Action> {
	public static void execute(Order superOrder) throws FailedActionException {
	}

	@Override
	public int compareTo(Action obj) {
		return this.toString().compareTo(obj.toString());
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(((Action) obj).toString());
	}

	@Override
	public abstract String toString();
}
