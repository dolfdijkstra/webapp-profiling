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
package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ResponseTimeStatsTest {

    @Test
    public void testOn() {
        final ResponseTimeStats s = new ResponseTimeStats("foo", "/p", false, false, false, false);
        Measurement m = s.startMeasurement();
        assertNull(m);
        s.on(false, false);
        m = s.startMeasurement();
        assertNotNull(m);
        assertEquals(0, m.getGeneration());

        s.finishMeasurement(null);
        assertEquals(1, s.getRoot().getCount());

    }

}
