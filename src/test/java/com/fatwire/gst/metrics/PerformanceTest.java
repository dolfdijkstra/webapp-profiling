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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PerformanceTest {

    @Test
    public void testStart() {
        init();
        try {
            final int i = Performance.start("foo", "start");
            assertEquals(0, i);
        } finally {
            Performance.stop();
            ThreadLocalMeasurementsHolder.set(null);
        }
    }

    private void init() {
        ThreadLocalMeasurementsHolder.set(new Measurements());
    }

    @Test
    public void testStop() {
        init();
        Performance.start("foo", "start");
        final int j = Performance.stop();

        assertEquals(0, j);
        ThreadLocalMeasurementsHolder.set(null);
    }

}
