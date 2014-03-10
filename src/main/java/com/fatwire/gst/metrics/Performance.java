/*
 * Copyright 2013 Dolf Dijkstra. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.metrics;

import java.util.concurrent.TimeUnit;

public class Performance {
    private Performance() {
    }

    public static boolean isActive() {
        return getMetrics().isActive();
    }

    public static int start(final String type, final String msg, final Object... vals) {
        return getMetrics().start(type, msg, vals);
    }

    public static void measurement(final String type, final String msg, final long elapsed, final TimeUnit unit) {
        getMetrics().measurement(type, msg, elapsed, unit);
    }

    public static void measurementMillis(final String type, final String msg, final long elapsed) {
        getMetrics().measurement(type, msg, elapsed, TimeUnit.MILLISECONDS);
    }

    public static int stop() {
        return getMetrics().stop();
    }

    private static Metrics getMetrics() {
        return ThreadLocalMetricsHolder.get();
    }

}
