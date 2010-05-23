package com.dafrito.logging;

import com.dafrito.logging.*;
import com.dafrito.debug.Nodeable;

public class Debugger {
    public static boolean addNode(Nodeable node) {
        if(node != null) {
            node.nodificate();
        } else {
            logObject(null);
        }
        return true;
    }
    
    private static void logObject(Object object) {
        Logging.log(object != null ? object.toString() : "<null>");
    }
    
    public static boolean addNode(Object object) {
        logObject(object);
        return true;
    }
    
    /**
     * @param group Legacy group name. Not used
     * @param message The message to log
     * @return true
     */
    public static boolean addNode(String group, Nodeable message) {
        message.nodificate();
        return true;
    }

    /**
     * @param group Legacy group name. Not used
     * @param object The object to log
     * @return true
     */
    public static boolean addNode(String group, Object object) {
        logObject(object);
        return true;
    }
    
    public static boolean addSnapNode(String message, Nodeable node) {
        return addNode(message, node);
    }
    
    public static boolean addSnapNode(String message, Object node) {
        return addNode(message, node);
    }
    

    public static boolean addSnapNode(DebugString cachedMessage, Object node) {
        return addSnapNode(cachedMessage.toString(), node);
    }
    
    /**
     * @param group Legacy term for categories of log records.
     * @param message The title for the node
     * @param object The node to add
     * @return true
     */
    public static boolean addSnapNode(String group, String message, Object object) {
        return addNode(message, object);
    }
    
    public static boolean close() {
        return true;
    }

    public static boolean close(String message) {
        return addNode(message);
    }
    
    public static boolean close(String message, Object object) {
        return addSnapNode(message, object);
    }
    
    public static boolean close(String message, Nodeable nodeable) {
        return addSnapNode(message, nodeable);
    }
    
    public static boolean close(Nodeable nodeable) {
        return addNode(nodeable);
    }
    
    /**
     * @param someValue The value to close to.
     * @return true
     */
    public static boolean closeNodeTo(Object someValue) {
        return true;
    }

    /**
     * @param someValue The reference value.
     * @return true
     */
    public static boolean ensureCurrentNode(Object someValue) {
        return true;
    }
    
    public static boolean format(String message, Object... args) {
        addNode(String.format(message, args));
        return true;
    }
    
    public static String getString(DebugString debugString) {
        return debugString.toString();
    }

    /**
     * @param group Legacy debugging name. Not used.
     * @param message The message to log
     * @return true
     */
    public static boolean open(String group, String message) {
        addNode(message);
        return true;
    }
    
    public static boolean open(Object message) {
        addNode(message);
        return true;
    }
    
    public static boolean log(Object message) {
        addNode(message);
        return true;
    }
    
    public static boolean log(String message, Object node) {
        addSnapNode(message, node);
        return true;
    }
    
    public static boolean printException(Exception exception) {
        Logging.logSevere(exception.getMessage());
        return true;
    }

    /**
     * @param name The stopwatch to toggle.
     */
    public static void hitStopWatch(String name) {
        // Do nothing.
    }
}
