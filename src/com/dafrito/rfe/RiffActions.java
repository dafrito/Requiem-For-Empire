package com.dafrito.rfe;
import java.util.HashMap;
import java.util.Map;

public class RiffActions {
	private static Map<String, Action> actions = new HashMap<String, Action>(); // String ActionName, Action

	public static void addAction(Action action) {
		RiffActions.actions.put(action.toString(), action);
	}

	public static Action getAction(String actionName) {
		return RiffActions.actions.get(actionName);
	}
}
