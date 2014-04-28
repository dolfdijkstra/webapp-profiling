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
package com.fatwire.gst.web.servlet.profiling.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class RinkTest {

    @Test
    public void testRink() {
        final Rink<String> rink = new Rink<String>(50);
        assertNotNull(rink);
    }

    @Test
    public void testIteration() {
        final Rink<String> rink = new Rink<String>(50);

        for (int i = 0; i < 52; i++) {
            rink.add(String.valueOf(i));
        }
        int c = 0;
        for (final String s : rink) {
            assertEquals(String.valueOf(c + 2), s);
            c++;
        }
        assertEquals(50, c);
    }
}
