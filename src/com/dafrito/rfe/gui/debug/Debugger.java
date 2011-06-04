/**
 * 
 */
package com.dafrito.rfe.gui.debug;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.dafrito.rfe.inspect.Inspectable;
import com.dafrito.rfe.inspect.Inspection;
import com.dafrito.rfe.inspect.Nodeable;
import com.dafrito.rfe.inspect.NodeableInspector;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.Exception_Nodeable;
import com.dafrito.rfe.strings.Strings;

public class Debugger {
	private static DebugEnvironment debugger;

	public static DebugEnvironment getDebugger() {
		return debugger;
	}

	public static DebugEnvironment.TreeBuildingInspector getDebugInspector() {
		return debugger.getInspector();
	}

	public static void setDebugger(DebugEnvironment debugger) {
		if (Debugger.debugger == null) {
			Debugger.debugger = debugger;
		}
	}

	public static boolean openNode(Object string) {
		openNode(null, string);
		return true;
	}

	public static boolean openNode(Object group, Object string) {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		getDebugInspector().openNode(new Debug_TreeNode(group, string));
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
			getDebugInspector().addNode(new Debug_TreeNode(group, "null"));
			return true;
		}
		if (o.getClass().isAnnotationPresent(Inspectable.class)) {
			NodeableInspector inspector = new NodeableInspector(getDebugInspector());
			Inspection.reflect(inspector, o);
			inspector.close();
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
				getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(getDebugInspector().getLastNodeAdded(), exceptionName);
			}
			return true;
		}
		if (o instanceof Iterable) {
			return addCollectionNode((Iterable<?>) o);
		}
		if (o instanceof Map) {
			return addMapNode((Map<?, ?>) o);
		}
		getDebugInspector().addNode(new Debug_TreeNode(group, o));
		if (o instanceof Exception) {
			String exceptionName;
			if (o instanceof Exception_Nodeable) {
				exceptionName = ((Exception_Nodeable) o).getName();
			} else if (o instanceof Exception_InternalError) {
				exceptionName = ((Exception_InternalError) o).getMessage();
			} else {
				exceptionName = "Exception";
			}
			getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(getDebugInspector().getLastNodeAdded(), exceptionName);
		}
		return true;
	}

	public static boolean addNodeableNode(Object group, Nodeable nodeable) {
		nodeable.nodificate();
		return true;
	}

	public synchronized static boolean addCollectionNode(Iterable<?> iterable) {
		Iterator<?> iter = iterable.iterator();
		while (iter.hasNext()) {
			addNode(iter.next());
		}
		return true;
	}

	public synchronized static boolean addMapNode(Map<?, ?> map) {
		if (map.isEmpty()) {
			return true;
		}
		Iterator<?> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();
			addSnapNode(entry.getKey().toString(), entry.getValue());
		}
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

	public static boolean ensureCurrentNode(Object string) {
		return getDebugInspector().ensureCurrentNode(string);
	}

	public static Debug_TreeNode getLastNodeAdded() {
		return getDebugInspector().getLastNodeAdded();
	}

	public static boolean closeNode() {
		if (getDebugger().isIgnoringThisThread()) {
			return true;
		}
		getDebugInspector().closeNode();
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
		getDebugInspector().closeNodeTo(string);
		return true;
	}

	public static boolean isResetting() {
		return getDebugger().isResetting();
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

	public static boolean atFullAllocation() {
		return getAllocationPercentage() == 100;
	}

	public static int getAllocationPercentage() {
		return (int) ((((double) Runtime.getRuntime().totalMemory()) / ((double) Runtime.getRuntime().maxMemory())) * 100);
	}

	public static int getFreePercentage() {
		return (int) ((((double) Runtime.getRuntime().freeMemory()) / ((double) Runtime.getRuntime().totalMemory())) * 100);
	}

	private static final Map<String, Long> stopWatches = new HashMap<String, Long>();

	/**
	 * Hit a stopwatch named {@code Thread.currentThread().getName()}.
	 * 
	 * @see Debugger#hitStopWatch(String)
	 */
	public static void hitStopWatch() {
		Debugger.hitStopWatch(Thread.currentThread().getName());
	}

	/**
	 * Start or stop a stop watch.
	 * 
	 * @param name
	 *            the name of the stop watch. If no stopwatch is running with
	 *            this name, a stopwatch is begun. Otherwise, the stopwatch is
	 *            stopped.
	 */
	public static void hitStopWatch(String name) {
		if (stopWatches.get(name) == null) {
			stopWatches.put(name, Long.valueOf(System.currentTimeMillis()));
			return;
		}
		System.out.println("StopWatcher: " + name + " executed in " + (((double) (System.currentTimeMillis() - stopWatches.get(name).longValue())) / 1000) + " seconds");
		stopWatches.remove(name);
	}

}