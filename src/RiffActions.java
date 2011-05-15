import java.util.*;

public class RiffActions{
	private static Map<String,Action>m_actions = new HashMap<String,Action>(); // String ActionName, Action
	public static Action getAction(String actionName){
		return RiffActions.m_actions.get(actionName);
	}
	public static void addAction(Action action){
		RiffActions.m_actions.put(action.toString(), action);
	}
}
