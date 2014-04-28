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

import java.math.BigInteger;

public class CounterHistoryBigInteger {
    private BigInteger[] counterHistory;
    private int next = 0;
    private int last;

    public CounterHistoryBigInteger(int maxDistance) {
        counterHistory = new BigInteger[maxDistance + 1];
    }

    public void add(BigInteger t) {
        last = next;
        counterHistory[next] = t;
        next = (next + 1) % counterHistory.length;

    }

    public long[] getHistory(int... distances) {
        long[] history = new long[distances.length];
        for (int d = 0; d < distances.length; d++) {
            int first = first(last, distances[d]);
            if (counterHistory[last] != null && counterHistory[first] != null) {
                history[d] = counterHistory[last].subtract(counterHistory[first]).longValue();
            } else {
                history[d] = 0;
            }
            if (history[d] < -1)
                history[d] = -1;
        }
        return history;
    }

    int first(int last, int distance) {
        int length = counterHistory.length;
        if (distance >= length)
            throw new IllegalArgumentException();
        int f = last - distance;
        if (f < 0) {
            f = length + last - distance;
        }
        return f;
    }

}
