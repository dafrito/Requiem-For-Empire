public abstract class Action implements Comparable{
	public static void execute(Order superOrder) throws FailedActionException{}
	public abstract String toString();
	public boolean equals(Object obj){
		return toString().equals(((Action)obj).toString());
	}
	public int compareTo(Object obj){
		return toString().compareTo(((Action)obj).toString());
	}
}
