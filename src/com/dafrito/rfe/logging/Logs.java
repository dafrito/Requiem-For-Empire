/**
 * 
 */
package com.dafrito.rfe.logging;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import com.bluespot.logic.actors.Actor;
import com.bluespot.logic.handlers.ChainedHandler;
import com.bluespot.logic.handlers.Handler;
import com.dafrito.rfe.script.exceptions.Exception_InternalError;
import com.dafrito.rfe.script.exceptions.ScriptException;

public final class Logs {

	private Logs() {
		// Suppress default constructor to ensure non-instantiability.
		throw new AssertionError("Instantiation not allowed");
	}

	private static ChainedHandler<LogMessage<? extends Object>> masterHandler;

	private static ThreadLocalTreeLog<Object, CompositeTreeLog<Object>> threadLocalLog;

	private static HandledTreeLog<Object> masterLog;

	private static boolean initialized = false;

	private static synchronized void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;

		threadLocalLog = new ThreadLocalTreeLog<Object, CompositeTreeLog<Object>>() {
			@Override
			protected CompositeTreeLog<? super Object> newTreeLog(Thread thread) {
				return new CompositeTreeLog<>();
			}
		};

		masterHandler = new ChainedHandler<>();

		masterLog = new HandledTreeLog<>();
		masterLog.setHandler(masterHandler);
		masterLog.setSink(threadLocalLog);

		addHandler(TreeLogHandlers.commonStrings());
		addHandler(TreeLogHandlers.inspectable());
		addHandler(TreeLogHandlers.nodeable());
		addHandler(TreeLogHandlers.iterable());
		addHandler(TreeLogHandlers.map());
		addHandler(TreeLogHandlers.nullHandler());
	}

	private static HandledTreeLog<Object> getMasterLog() {
		initialize();
		return masterLog;
	}

	private static ThreadLocalTreeLog<Object, CompositeTreeLog<Object>> getThreadLocalLog() {
		initialize();
		return threadLocalLog;
	}

	private static ChainedHandler<LogMessage<? extends Object>> getMasterHandler() {
		initialize();
		return masterHandler;
	}

	/**
	 * Immediately add the specified listener. See
	 * {@link ThreadLocalTreeLog#addListener(Actor)} for details.
	 * <p>
	 * I use <? super Object> here simply to remind myself that this is the type
	 * I want in case I change from using Object at some point in the future.
	 * Since it's Object right now, it's equivalent to <Object>.
	 * 
	 * @param listener
	 *            the listener that will receive new logs for every thread
	 * @see ThreadLocalTreeLog#addListener(Actor)
	 * @see CompositeTreeLog#removeListener(TreeLog)
	 */
	public static void addListener(Actor<? super CompositeTreeLog<? super Object>> listener) {
		getThreadLocalLog().addListener(listener);
	}

	/**
	 * Immediately remove the specified log.
	 * <p>
	 * I use <? super Object> here simply to remind myself that this is the type
	 * I want in case I change from using Object at some point in the future.
	 * Since it's Object right now, it's equivalent to <Object>.
	 * 
	 * @param listener
	 *            the listener to remove
	 * @see ThreadLocalTreeLog#removeListener(Actor)
	 */
	public static void removeListener(Actor<? super CompositeTreeLog<? super Object>> listener) {
		getThreadLocalLog().removeListener(listener);
	}

	public static void addHandler(Handler<? super LogMessage<? extends Object>> handler) {
		getMasterHandler().addHandler(handler);
	}

	public static void addHandler(TreeLogHandler<Object> handler) {
		handler.setLog(getMasterLog());
		getMasterHandler().addHandler(handler);
	}

	public static void removeHandler(Handler<? super LogMessage<? extends Object>> handler) {
		getMasterHandler().removeHandler(handler);
	}

	public static boolean openNode(CommonString scope) {
		return openNode(scope.getText());
	}

	public static boolean openNode(String scope) {
		openNode(null, scope);
		return true;
	}

	public static boolean openNode(String scopeGroup, String scope) {
		getMasterLog().enter(scope, scopeGroup != null ? scopeGroup : scope);
		return true;
	}

	public static boolean addNode(Object message) {
		return addNode(null, message);
	}

	public static boolean addNode(Object scope, Object message) {
		getMasterLog().log(new LogMessage<Object>(message));
		if (message instanceof Exception) {
			registerHotspot((Exception) message);
		}
		return true;
	}

	// TODO Remove this warning when we're ready to reimplement hotspots
	@SuppressWarnings("unused")
	private static void registerHotspot(Exception exception) {
		String exceptionName;
		if (exception instanceof ScriptException) {
			exceptionName = ((ScriptException) exception).getName();
		} else if (exception instanceof Exception_InternalError) {
			exceptionName = ((Exception_InternalError) exception).getMessage();
		} else {
			exceptionName = "Exception";
		}
		// TODO Reimplement hotspots
		// getDebugger().getUnfilteredOutput().getHotspotPanel().addHotspot(getDebugInspector().getLastNodeAdded(), exceptionName);
	}

	public static boolean addSnapNode(CommonString scope, Object message) {
		return addSnapNode(scope.getText(), message);
	}

	public static boolean addSnapNode(String scope, Object message) {
		return addSnapNode(null, scope, message);
	}

	public static boolean addSnapNode(String scopeGroup, String scope, Object message) {
		openNode(scopeGroup, scope);
		addNode(message);
		closeNode();
		return true;
	}

	public static boolean closeNode() {
		getMasterLog().leave();
		return true;
	}

	public static boolean closeNode(Object message) {
		addNode(message);
		closeNode();
		return true;
	}

	public static boolean closeNode(String scope, Object message) {
		addSnapNode(scope, message);
		closeNode();
		return true;
	}

	public static void printException(Exception ex) {
		System.err.println(ex);
		if (ex instanceof ScriptException || ex instanceof Exception_InternalError) {
			assert addNode("Exceptions and Errors", ex);
		} else {
			assert addSnapNode("Exceptions and Errors", "Exception", ex);
		}
	}

	public static void report() {
		System.out.println("Performance Report");
		NumberFormat nf = NumberFormat.getInstance();
		System.out.println("Maximum Memory Available: " + nf.format(Runtime.getRuntime().maxMemory()) + " bytes (" + Logs.getAllocationPercentage() + "% allocated)");
		System.out.println("Used Memory Before GC: " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes (" + Logs.getFreePercentage() + "% free)");
		System.gc();
		System.out.println("Used Memory After GC : " + nf.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " bytes (" + Logs.getFreePercentage() + "% free)");
		System.out.println("Free Memory: " + nf.format(Runtime.getRuntime().freeMemory()) + " bytes");
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
	 * @see Logs#hitStopWatch(String)
	 */
	public static void hitStopWatch() {
		Logs.hitStopWatch(Thread.currentThread().getName());
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