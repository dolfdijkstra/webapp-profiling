/*
 * Copyright (C) 2006 Dolf Dijkstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
