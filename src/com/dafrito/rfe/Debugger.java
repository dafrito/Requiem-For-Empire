/**
 * 
 */
package com.dafrito.rfe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.util.Strings;

public class Debugger {
	// Debug_Environment fxns
	private static Map<String, Long> stopWatches = new HashMap<String, Long>();
	private static DebugEnvironment debugger;

	public synchronized static boolean addCollectionNode(Object group, Collection<?> list) {
		if (list.size() == 0) {
			return true;
		}
		Iterator<?> iter = list.iterator();
		while (iter.hasNext()) {
			addNode(iter.next());
		}
		return true;
	}

	public synchronized static boolean addMapNode(Object group, Map<?, ?> map) {
		if (map.size() == 0) {
			return true;
		}
		Iterator<?> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
			addSnapNode(entry.getKey().toString(), entry.getValue());
		}
		return true;
	}

	public static boolean addNode(Object o) {
		return addNode(null, o);
	}

	public static boolean addNode(Object group, Object o) {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		if (o == null) {
			getDebugger().addNode(new Debug_TreeNode(group, "null"));
			return true;
		}
		if (o instanceof Nodeable) {
			addNodeableNode(group, (Nodeable) o);
			if (o instanceof Exception) {
				String exceptionName;
				if (o instanceof Exception_Nodeable) {
					exceptionName = ((Exception_Nodeable) o).getName();
				} else if (o instanceof Exception_InternalError) {
					exceptionName = ((Exception_InternalError) o).getMessage();
				} else {
					exceptionName = "Exception";
				}
				getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(getDebugger().getLastNodeAdded(), exceptionName);
			}
			return true;
		}
		if (o instanceof Collection) {
			return addCollectionNode(group, (Collection<?>) o);
		}
		if (o instanceof Map) {
			return addMapNode(group, (Map<?, ?>) o);
		}
		getDebugger().addNode(new Debug_TreeNode(group, o));
		if (o instanceof Exception) {
			String exceptionName;
			if (o instanceof Exception_Nodeable) {
				exceptionName = ((Exception_Nodeable) o).getName();
			} else if (o instanceof Exception_InternalError) {
				exceptionName = ((Exception_InternalError) o).getMessage();
			} else {
				exceptionName = "Exception";
			}
			getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(getDebugger().getLastNodeAdded(), exceptionName);
		}
		return true;
	}

	public static boolean addNodeableNode(Object group, Nodeable nodeable) {
		nodeable.nodificate();
		return true;
	}

	public static boolean addSnapNode(Object name, Object o) {
		return addSnapNode(null, name, o);
	}

	public static boolean addSnapNode(Object group, Object name, Object o) {
		openNode(group, name);
		addNode(o);
		closeNode();
		return true;
	}

	public static boolean atFullAllocation() {
		return getAllocationPercentage() == 100;
	}

	public static boolean closeNode() {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		getDebugger().closeNode();
		return true;
	}

	public static boolean closeNode(Object string) {
		addNode(string);
		closeNode();
		return true;
	}

	public static boolean closeNode(Object string, Object object) {
		addSnapNode(string, object);
		closeNode();
		return true;
	}

	public static boolean closeNodeTo(Object string) {
		getDebugger().closeNodeTo(string);
		return true;
	}

	public static boolean ensureCurrentNode(Object string) {
		return getDebugger().ensureCurrentNode(string);
	}

	public static int getAllocationPercentage() {
		return (int) ((((double) Runtime.getRuntime().totalMemory()) / ((double) Runtime.getRuntime().maxMemory())) * 100);
	}

	public static DebugEnvironment getDebugger() {
		return debugger;
	}

	public static int getFreePercentage() {
		return (int) ((((double) Runtime.getRuntime().freeMemory()) / ((double) Runtime.getRuntime().totalMemory())) * 100);
	}

	public static Debug_TreeNode getLastNodeAdded() {
		return getDebugger().getLastNodeAdded();
	}

	public static String getPriorityExecutingClass() {
		return getDebugger().getPriorityExecutingClass();
	}

	public static String getString(DebugString value) {
		return Debug_TreeNode.getPrecached(value).toString();
	}

	public static void hitStopWatch(String name) {
		if (stopWatches.get(name) == null) {
			stopWatches.put(name, new Long(System.currentTimeMillis()));
			return;
		}
		System.out.println("StopWatcher: " + name + " executed in " + (((double) (System.currentTimeMillis() - stopWatches.get(name).longValue())) / 1000) + " seconds");
		stopWatches.remove(name);
	}

	public static boolean isResetting() {
		return getDebugger().isResetting();
	}

	// Noding functions.
	public static boolean openNode(Object string) {
		openNode(null, string);
		return true;
	}

	public static boolean openNode(Object group, Object string) {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		getDebugger().openNode(new Debug_TreeNode(group, string));
		return true;
	}

	public static boolean printDebug(String category) {
		String slash = "";
		int offset = 0;
		if (category.charAt(0) == '/') {
			slash = "/";
			offset++;
		}
		String[] categoryArray = category.split("/");
		return printDebug(category, "(" + slash + categoryArray[1 + offset] + ":" + categoryArray[0 + offset] + ")");
	}

	public static boolean printDebug(String category, Collection<?> list) {
		return printDebug(category, Strings.displayList(list));
	}

	public static boolean printDebug(String category, Object obj) {
		return getDebugger().printDebug(category, obj);
	}

	public static void printException(Exception ex) {
		System.out.println(ex);
		if (ex instanceof Exception_Nodeable || ex instanceof Exception_InternalError) {
			assert addNode("Exceptions and Errors", ex);
		} else {
			assert addSnapNode("Exceptions and Errors", "Exception", ex);
		}
	}

	public static void report() {
		getDebugger().report();
	}

	public static void reset() {
		getDebugger().reset();
	}

	public static void setDebugger(DebugEnvironment debugger) {
		if (Debugger.debugger == null) {
			Debugger.debugger = debugger;
		}
	}

	public static void setExceptionsMode(boolean value) {
		getDebugger().setExceptionsMode(value);
	}

	public static void setPriorityExecutingClass(String name) {
		getDebugger().setPriorityExecutingClass(name);
	}
}