package com.dafrito.rfe.actions;
import java.util.HashMap;
import java.util.Map;

public class RiffActions {
	private static Map<String, Action> actions = new HashMap<String, Action>(); // String ActionName, Action

	public static void addAction(Action action) {
		RiffActions.actions.put(action.getName(), action);
	}

	public static Action getAction(String actionName) {
		return RiffActions.actions.get(actionName);
	}
}
