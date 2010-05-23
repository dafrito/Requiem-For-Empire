package com.dafrito.economy;

public abstract class Action implements Comparable<Action> {

    /** TODO Document Action.execute
     * 
     * What is this class? What is execute?
     * 
     * @param superOrder Unknown
     * @throws FailedActionException Unknown exception
     */
    public static void execute(Order superOrder) throws FailedActionException {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public boolean equals(Object obj) {
        Action action = (Action)obj;
        return this.toString().equals(action.toString());
    }

    public int compareTo(Action action) {
        return this.toString().compareTo(action.toString());
    }
}
