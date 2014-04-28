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

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Measurements {

    static Log LOG = LogFactory.getLog(Measurements.class);

    private final ThreadLocal<LinkedList<StartEndMeasurement>> tl = new ThreadLocal<LinkedList<StartEndMeasurement>>() {

        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected LinkedList<StartEndMeasurement> initialValue() {
            return new LinkedList<StartEndMeasurement>();
        }

    };

    private final AtomicLong counter = new AtomicLong();
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final Set<MeasurementListener> listeners = new CopyOnWriteArraySet<MeasurementListener>();

    public boolean isActive() {
        return active.get();
    }

    public boolean addListener(final MeasurementListener listener) {
        active.compareAndSet(false, listener.isActive());
        return listeners.add(listener);
    }

    public MeasurementListener[] getListeners() {
        return listeners.toArray(new MeasurementListener[listeners.size()]);
    }

    @SuppressWarnings("unchecked")
    public <T> T getListener(Class<T> type) {
        for (MeasurementListener l : getListeners()) {
            if (type.isAssignableFrom(l.getClass())) {
                return (T) l;
            }
        }
        return null;
    }

    public long getCount() {
        return counter.get();
    }

    public boolean removeListener(final MeasurementListener listener) {
        final boolean b = active.get();
        final boolean r = listeners.remove(listener);
        for (final MeasurementListener l : listeners) {
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
        final LinkedList<StartEndMeasurement> li = tl.get();
        StartEndMeasurement m = li.peekLast();
        if (m == null) {
            m = new StartEndMeasurement(counter.incrementAndGet(), 0, type, 0, msg, vals);
        } else {
            m = new StartEndMeasurement(m.getId(), m.getLevel() + 1, type, m.getLevelZeroStart(), msg, vals);
        }
        li.addLast(m);
        for (final MeasurementListener l : listeners) {
            l.start(m);
        }
        return m.getLevel();
    }

    public void measurement(final String type, final String msg, final long elapsed, final TimeUnit unit) {
        final LinkedList<StartEndMeasurement> li = tl.get();
        final StartEndMeasurement m = li.peekLast();
        if (m != null) {
            long startTime = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(elapsed, unit);
            final Measurement me = new Measurement(m.getId(), m.getLevel() + 1, type, startTime,
                    TimeUnit.NANOSECONDS.convert(elapsed, unit), m.getLevelZeroStart(), msg);
            for (final MeasurementListener l : listeners) {
                try {
                    l.measurement(me);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }

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
        final LinkedList<StartEndMeasurement> li = tl.get();
        final StartEndMeasurement m = li.pollLast();
        if (m == null) {
            throw new IllegalStateException("no metric found");
        }
        m.setEnd(nano);
        for (final MeasurementListener l : listeners) {
            try {
                l.stop(m);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return m.getLevel();
    }

}
