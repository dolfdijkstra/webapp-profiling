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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MetricsTest {

    @Test
    public void testListener() {
        final Measurements metrics = new Measurements();
        final MeasurementListener l = new BaseMetricListener() {

            @Override
            public void stop(final StartEndMeasurement metric) {
                assertEquals(0, metric.getLevel());

            }

        };
        assertTrue(metrics.addListener(l));
        try {
            final int i = metrics.start("foo", "start");
            assertEquals(0, i);
        } finally {
            metrics.stop();
        }
        assertTrue(metrics.removeListener(l));
    }

    @Test
    public void test_remove_listener_active() {
        final Measurements metrics = new Measurements();
        final MeasurementListener active = new BaseMetricListener();
        final MeasurementListener not_active = new BaseMetricListener() {

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
        final Measurements metrics = new Measurements();
        final MeasurementListener active = new BaseMetricListener();
        final MeasurementListener not_active = new BaseMetricListener() {
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

class BaseMetricListener implements MeasurementListener {

    @Override
    public void stop(final StartEndMeasurement metric) {

    }

    @Override
    public void start(final StartEndMeasurement metric) {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void measurement(final Measurement me) {

    }
}
