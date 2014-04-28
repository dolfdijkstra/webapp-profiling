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

public class ThreadLocalMeasurementsHolder {
    private ThreadLocalMeasurementsHolder() {
    }

    private static final ThreadLocal<Measurements> tl = new ThreadLocal<Measurements>();

    public static Measurements get() {
        return tl.get();

    }

    public static void set(final Measurements metrics) {
        if (metrics != null && tl.get() != null)
            throw new IllegalStateException("The threadlocal object for the measurements is holding a value.");
        tl.set(metrics);

    }

}
