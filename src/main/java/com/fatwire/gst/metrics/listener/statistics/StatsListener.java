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
package com.fatwire.gst.metrics.listener.statistics;

import java.util.concurrent.ConcurrentHashMap;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;
import com.fatwire.gst.metrics.MeasurementListener;
import com.fatwire.gst.metrics.Measurements;
import com.fatwire.gst.metrics.ThreadLocalMeasurementsHolder;

public class StatsListener implements MeasurementListener {

    private boolean active = true;

    private final ConcurrentHashMap<String, Statistic> map = new ConcurrentHashMap<String, Statistic>();

    @Override
    public void start(final StartEndMeasurement metric) {

    }

    @Override
    public void stop(final StartEndMeasurement metric) {
        final String t = metric.getType();
        final Statistic s = get(t);
        s.signal(metric.getElapsed());

    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void measurement(final Measurement me) {
        final String t = me.getType();
        final Statistic s = get(t);
        s.signal(me.getElapsed());
    }

    public Statistic[] getStats() {
        return map.values().toArray(new Statistic[0]);

    }

    public static Statistic[] getMetricStats() {
        final Measurements m = ThreadLocalMeasurementsHolder.get();
        final StatsListener l = m == null ? null : m.getListener(StatsListener.class);
        return l == null ? new Statistic[0] : l.getStats();
    }

    private Statistic get(final String t) {
        Statistic s = map.get(t);
        if (s == null) {
            s = new Statistic(t);
            final Statistic o = map.putIfAbsent(t, s);
            if (o != null) {
                s = o;
            }
        }
        return s;
    }

    /**
     * @param active the active to set
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

}
