package com.dafrito.util;

import com.dafrito.economy.Action;

import java.util.HashMap;
import java.util.Map;


public class RiffActions {
    private static Map<String, Action> actions = new HashMap<String, Action>();

    public static Action getAction(String actionName) {
        return RiffActions.actions.get(actionName);
    }

    public static void addAction(Action action) {
        RiffActions.actions.put(action.toString(), action);
    }
}
