public abstract class Action implements Comparable {
	public static void execute(Order superOrder) throws FailedActionException {
	}

	@Override
	public int compareTo(Object obj) {
		return this.toString().compareTo(((Action) obj).toString());
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(((Action) obj).toString());
	}

	@Override
	public abstract String toString();
}
