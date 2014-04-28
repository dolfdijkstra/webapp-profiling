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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CounterHistoryTest {

    @Test
    public void testFirst_small() {

        final CounterHistory ch = new CounterHistory(4);
        assertEquals(1, ch.first(4, 3));
        assertEquals(0, ch.first(4, 4));
        assertEquals(4, ch.first(3, 4));
        assertEquals(4, ch.first(2, 3));

    }

    @Test
    public void testGetHistory() {
        final CounterHistory ch = new CounterHistory(60 * 15);
        for (int i = 0; i < ((60 * 15) + 3); i++) {
            ch.add(i * 30);
        }
        final int[] distances = new int[] { 15 * 60, 5 * 60, 60, 30, 10, 5, 1 };
        final long[] h = ch.getHistory(distances);
        final long[] expected = new long[] { 27000, 9000, 1800, 900, 300, 150, 30 };
        assertTrue(Arrays.equals(expected, h));

    }

    @Test
    public void testGetHistory_not_full() {
        final CounterHistory ch = new CounterHistory(60 * 5);
        for (int i = 0; i < 61; i++) {
            ch.add(i * 30);
        }
        final int[] distances = new int[] { 5 * 60, 60, 30, 10, 5, 1 };
        final long[] h = ch.getHistory(distances);
        final long[] expected = new long[] { 1800, 1800, 900, 300, 150, 30 };
        assertTrue(Arrays.equals(expected, h));

    }

    void print(final long[] h) {
        for (final long i : h) {
            System.out.println(i);
        }

    }

    void print(final int[] h) {
        for (final int i : h) {
            System.out.println(i);
        }

    }

}
