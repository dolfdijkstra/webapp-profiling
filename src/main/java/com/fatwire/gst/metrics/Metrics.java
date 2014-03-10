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

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Metrics {

    private final ThreadLocal<LinkedList<Metric>> tl = new ThreadLocal<LinkedList<Metric>>() {

        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected LinkedList<Metric> initialValue() {
            return new LinkedList<Metric>();
        }

    };

    private final AtomicLong counter = new AtomicLong();
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final Set<MetricListener> listeners = new CopyOnWriteArraySet<MetricListener>();

    public boolean isActive() {
        return active.get();
    }

    public boolean addListener(final MetricListener listener) {
        active.compareAndSet(false, listener.isActive());
        return listeners.add(listener);
    }

    public MetricListener[] getListeners() {
        return listeners.toArray(new MetricListener[listeners.size()]);
    }

    @SuppressWarnings("unchecked")
    public <T> T getListener(Class<T> type) {
        for (MetricListener l : getListeners()) {
            if (type.isAssignableFrom(l.getClass())) {
                return (T) l;
            }
        }
        return null;
    }

    public long getCount() {
        return counter.get();
    }

    public boolean removeListener(final MetricListener listener) {
        final boolean b = active.get();
        final boolean r = listeners.remove(listener);
        for (final MetricListener l : listeners) {
            if (l.isActive() != b) {
                active.compareAndSet(b, l.isActive());
            }
        }
        if (listeners.isEmpty()) {
            active.set(false);
        }
        return r;
    }

    public int start(final String type, final String msg, final Object... vals) {
        final LinkedList<Metric> li = tl.get();
        Metric m = li.peekLast();
        if (m == null) {
            m = new Metric(counter.incrementAndGet(), 0, type, 0, msg, vals);
        } else {
            m = new Metric(m.getId(), m.getLevel() + 1, type, m.getLevelZeroStart(), msg, vals);
        }
        li.addLast(m);
        for (final MetricListener l : listeners) {
            l.start(m);
        }
        return m.getLevel();
    }

    public void measurement(final String type, final String msg, final long elapsed, final TimeUnit unit) {
        final LinkedList<Metric> li = tl.get();
        final Metric m = li.peekLast();
        if (m != null) {
            final Measurement me = new Measurement(m.getId(), m.getLevel() + 1, type, msg,
                    TimeUnit.NANOSECONDS.convert(elapsed, unit), m.getLevelZeroStart());
            for (final MetricListener l : listeners) {
                l.measurement(me);
            }

        }

    }

    public int terminate() {
        int i = 0;
        while (stop() > 0 && i < 500) { // don't spin forever due to an error in
                                        // stop().
            i++;
        }
        return i;
    }

    public int stop() {
        final long nano = System.nanoTime();
        final LinkedList<Metric> li = tl.get();
        final Metric m = li.pollLast();
        if (m == null) {
            throw new IllegalStateException("no metric found");
        }
        m.setEnd(nano);
        for (final MetricListener l : listeners) {
            l.stop(m);
        }

        return m.getLevel();
    }

}
