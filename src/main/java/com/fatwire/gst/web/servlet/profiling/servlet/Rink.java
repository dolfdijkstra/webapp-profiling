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

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 
 * A data structure to hold a list with a preset number of items. The storing class is a {@link ArrayBlockingQueue}.
 * 
 * @author Dolf Dijkstra
 * 
 * @param <T>
 */
public class Rink<T> implements Iterable<T> {

    private final ArrayBlockingQueue<T> q;

    public Rink(int size) {
        q = new ArrayBlockingQueue<T>(size);
    }

    public void add(T t) {
        while (!q.offer(t)) {
            q.poll();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return q.iterator();
    }

    public void reset() {
        q.clear();
    }

}
