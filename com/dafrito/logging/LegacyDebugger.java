package com.dafrito.logging;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.dafrito.logging.tree.LogViewTreeNode;
import com.dafrito.debug.Exception_InternalError;
import com.dafrito.debug.Exception_Nodeable;
import com.dafrito.debug.Nodeable;

public class LegacyDebugger {
    // Debug_Environment fxns
    private static Map<String, Long> stopWatches = new HashMap<String, Long>();
    private static LogManager environment;
    
    public static void hitStopWatch(String name) {
        if(stopWatches.get(name) == null) {
            stopWatches.put(name, new Long(System.currentTimeMillis()));
            return;
        }
        double time = ((double)(System.currentTimeMillis() - stopWatches.get(name).longValue())) / 1000;
        System.out.println(String.format("StopWatcher: %s executed in %s seconds", name, time));
        stopWatches.remove(name);
    }

    public static LogManager getDebugger() {
        return LegacyDebugger.environment;
    }

    public static void setDebugger(LogManager debugger) {
        if(LegacyDebugger.environment == null) {
            LegacyDebugger.environment = debugger;
        }
    }

    public static void printException(Exception ex) {
        System.out.println(ex);
        if(ex instanceof Exception_Nodeable || ex instanceof Exception_InternalError) {
            assert addNode("Exceptions and Errors", ex);
        } else {
            assert addSnapNode("Exceptions and Errors", "Exception", ex);
        }
    }

    // Noding functions.
    public static boolean open(Object string) {
        open(null, string);
        return true;
    }

    public static boolean open(Object group, Object string) {
        if(getDebugger().isIgnoringThisThread()) {
            return true;
        }
        getDebugger().openNode(new LogViewTreeNode(group, string));
        return true;
    }

    public static boolean addSnapNode(Object name, Object o) {
        return addSnapNode(null, name, o);
    }

    /**
     * Creates a nested node that contains only this node.
     * 
     * @param group The name of the group
     * @param name The name of this node
     * @param o The data associated with this node
     * @return True
     */
    public static boolean addSnapNode(Object group, Object name, Object o) {
        open(group, name);
        addNode(o);
        close();
        return true;
    }
    
    /**
     * @param associatedMessage Message that describes the nodeable
     * @param nodeable The nodeable to log
     * @return True
     */
    public static boolean log(String associatedMessage, Nodeable nodeable) {
        LegacyDebugger.log(associatedMessage);
        LegacyDebugger.addNodeableNode(nodeable);
        return true;
    }

    public static boolean log(Object value) {
        return LegacyDebugger.addNode(value);
    }
    
    public static boolean log(Nodeable nodeable) {
        return LegacyDebugger.addNodeableNode(nodeable);
    }

    public static boolean log(String message) {
        return LegacyDebugger.addNode(message);
    }

    public static boolean format(String message, Object...args) {
        return LegacyDebugger.addNode(String.format(message, args));
    }

    public static boolean addNode(Object o) {
        return addNode(null, o);
    }

    public static boolean addNode(Object group, Object o) {
        if(getDebugger().isIgnoringThisThread()) {
            return true;
        }
        if(o == null) {
            getDebugger().addNode(new LogViewTreeNode(group, "null"));
            return true;
        }
        if(o instanceof Nodeable) {
            addNodeableNode((Nodeable)o);
            /*if(o instanceof Exception) {
                String exceptionName;
                if(o instanceof Exception_Nodeable) {
                    exceptionName = ((Exception_Nodeable)o).getName();
                } else if(o instanceof Exception_InternalError) {
                    exceptionName = ((Exception_InternalError)o).getMessage();
                } else {
                    exceptionName = "Exception";
                }
                getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(
                    getDebugger().getLastNodeAdded(),
                    exceptionName);
            }*/
            return true;
        }
        if(o instanceof Collection) {
            return addCollectionNode(group, (Collection<?>)o);
        }
        if(o instanceof Map) {
            return addMapNode(group, (Map<?,?>)o);
        }
        getDebugger().addNode(new LogViewTreeNode(group, o));
      /*  if(o instanceof Exception) {
            String exceptionName;
            if(o instanceof Exception_Nodeable) {
                exceptionName = ((Exception_Nodeable)o).getName();
            } else if(o instanceof Exception_InternalError) {
                exceptionName = ((Exception_InternalError)o).getMessage();
            } else {
                exceptionName = "Exception";
            }
            getDebugger().getUnfilteredOutput().getHotspotPanel().createHotspot(
                getDebugger().getLastNodeAdded(),
                exceptionName);
        }*/
        return true;
    }

    public static boolean addNodeableNode(Nodeable nodeable) {
        nodeable.nodificate();
        return true;
    }

    public static LogViewTreeNode getLastNodeAdded() {
        return getDebugger().getLastNodeAdded();
    }

    public synchronized static boolean addCollectionNode(Object groupName, Collection<?> list) {
        LegacyDebugger.log(groupName);
        if(list.isEmpty()) {
            LegacyDebugger.log("<This list has no elements>");
            return true;
        }
        for(Object element : list) {
            LegacyDebugger.log(element);
        }
        return true;
    }

    public synchronized static boolean addMapNode(Object associatedData, Map<?,?> map) {
        log(associatedData);
        for(Map.Entry<?,?> entry : map.entrySet())
            addSnapNode(entry.getKey().toString(), entry.getValue());
        return true;
    }

    public static String getString(DebugString value) {
        return LogViewTreeNode.getPrecached(value).toString();
    }

    public static boolean close() {
        if(getDebugger().isIgnoringThisThread()) {
            return true;
        }
        getDebugger().closeNode();
        return true;
    }

    public static boolean close(Object string) {
        addNode(string);
        close();
        return true;
    }
    
    public static boolean close(Nodeable nodeable) {
        log(nodeable);
        close();
        return true;
    }
    

    public static boolean close(String associatedMessage, Nodeable nodeable) {
        log(associatedMessage, nodeable);
        close();
        return true;
    }

    public static boolean ensureCurrentNode(Object nodeValue) {
        return getDebugger().ensureCurrentNode(nodeValue);
    }

    public static boolean close(Object string, Object object) {
        addSnapNode(string, object);
        close();
        return true;
    }

    public static String getBootstrappingClassName() {
        return getDebugger().getBootstrappingClassName();
    }

    public static void setBootstrappingClassName(String name) {
        getDebugger().setBootstrappingClassName(name);
    }

    public static void setExceptionsMode(boolean value) {
        getDebugger().setExceptionsMode(value);
    }

    public static boolean isResetting() {
        return getDebugger().isResetting();
    }

    public static int getAllocationPercentage() {
        return (int)((((double)Runtime.getRuntime().totalMemory()) / ((double)Runtime.getRuntime().maxMemory())) * 100);
    }

    public static int getFreePercentage() {
        return (int)((((double)Runtime.getRuntime().freeMemory()) / ((double)Runtime.getRuntime().totalMemory())) * 100);
    }

    public static boolean atFullAllocation() {
        return getAllocationPercentage() == 100;
    }

    public static void reset() {
        getDebugger().reset();
    }

    public static boolean closeNodeTo(Object string) {
        getDebugger().closeNodeTo(string);
        return true;
    }
}
