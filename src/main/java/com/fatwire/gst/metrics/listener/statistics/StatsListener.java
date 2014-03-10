package com.fatwire.gst.metrics.listener.statistics;

import java.util.concurrent.ConcurrentHashMap;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.Metric;
import com.fatwire.gst.metrics.MetricListener;
import com.fatwire.gst.metrics.Metrics;
import com.fatwire.gst.metrics.ThreadLocalMetricsHolder;

public class StatsListener implements MetricListener {

    private boolean active = true;

    private final ConcurrentHashMap<String, Statistic> map = new ConcurrentHashMap<String, Statistic>();

    @Override
    public void start(final Metric metric) {

    }

    @Override
    public void stop(final Metric metric) {
        final String t = metric.getType();
        final Statistic s = get(t);
        s.signal(metric.elapsed());

    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void measurement(final Measurement me) {
        final String t = me.getType();
        final Statistic s = get(t);
        s.signal(me.elapsed());
    }

    public Statistic[] getStats() {
        return map.values().toArray(new Statistic[0]);

    }

    public static Statistic[] getMetricStats() {
        final Metrics m = ThreadLocalMetricsHolder.get();
        final StatsListener l = m == null ? null : m.getListener(StatsListener.class);
        return m == null ? null : l.getStats();
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
