package com.fatwire.gst.metrics;

import static org.junit.Assert.*;

import org.junit.Test;

public class MetricsTest {

    @Test
    public void testListener() {
        Metrics metrics = new Metrics();
        MetricListener l = new BaseMetricListener() {

            @Override
            public void stop(Metric metric) {
                assertEquals(0, metric.getLevel());

            }

        };
        assertTrue(metrics.addListener(l));
        try {
            int i = metrics.start("foo", "start");
            assertEquals(0, i);
        } finally {
            metrics.stop();
        }
        assertTrue(metrics.removeListener(l));
    }

    @Test
    public void test_remove_listener_active() {
        Metrics metrics = new Metrics();
        MetricListener active = new BaseMetricListener();
        MetricListener not_active = new BaseMetricListener() {

            @Override
            public boolean isActive() {
                return false;
            }

        };
        metrics.addListener(not_active);
        metrics.addListener(active);
        assertTrue(metrics.isActive());
        metrics.removeListener(active);
        assertFalse(metrics.isActive());
        metrics.removeListener(not_active);

    }

    @Test
    public void test_remove_listener_not_active() {
        Metrics metrics = new Metrics();
        MetricListener active = new BaseMetricListener();
        MetricListener not_active = new BaseMetricListener() {
            @Override
            public boolean isActive() {
                return false;
            }
        };
        metrics.addListener(not_active);
        metrics.addListener(active);
        assertTrue(metrics.isActive());
        metrics.removeListener(not_active);
        assertTrue(metrics.isActive());
        metrics.removeListener(active);

    }

}

class BaseMetricListener implements MetricListener {

    @Override
    public void stop(Metric metric) {

    }

    @Override
    public void start(Metric metric) {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void measurement(Measurement me) {

    }
}
