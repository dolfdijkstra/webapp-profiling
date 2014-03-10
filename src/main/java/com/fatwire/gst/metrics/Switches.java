package com.fatwire.gst.metrics;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Switches {

    private static final ConcurrentHashMap<String, AtomicBoolean> map = new ConcurrentHashMap<String, AtomicBoolean>();

    public static boolean on(String switchName) {
        AtomicBoolean v = get(switchName);
        boolean current = v.get();
        v.set(true);
        return current;

    }

    public static boolean off(String switchName) {
        AtomicBoolean v = get(switchName);
        boolean current = v.get();
        v.set(false);
        return current;
    }

    public static boolean state(String switchName) {
        AtomicBoolean v = get(switchName);
        return v.get();
    }

    private static AtomicBoolean get(String switchName) {
        AtomicBoolean v = map.get(switchName);
        if (v == null) {
            String x = System.getProperty("performance-switch." + switchName, "false");

            v = new AtomicBoolean("true".equalsIgnoreCase(x));
            map.put(switchName, v);
        }
        return v;
    }

    public static Set<String> names() {
        return map.keySet();
    }

}
