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
package com.fatwire.gst.metrics.listener.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;
import com.fatwire.gst.metrics.MeasurementListener;
import com.fatwire.gst.metrics.Measurements;
import com.fatwire.gst.metrics.ThreadLocalMeasurementsHolder;
import com.fatwire.gst.web.servlet.profiling.servlet.Rink;

public class WebListener implements MeasurementListener {

    private static final Log log = LogFactory.getLog(WebListener.class);
    private static final int RINK_SIZE = 500;

    private final Rink<WebMeasurements> rink = new Rink<WebMeasurements>(RINK_SIZE);

    private final ThreadLocal<WebMeasurements> tl = new ThreadLocal<WebMeasurements>();

    @Override
    public void start(StartEndMeasurement metric) {
        if (log.isTraceEnabled()) {
            log.trace(metric.toString());
        }

        // initiate WebMeasurements on level zero
        if (metric.getLevel() == 0) {
            WebMeasurements wm = tl.get();
            if (wm == null) {
                wm = new WebMeasurements();
                tl.set(wm);
            }
        }

    }

    @Override
    public void measurement(Measurement me) {
        if (log.isTraceEnabled()) {
            log.trace(me.toString());
        }

        WebMeasurements wm = tl.get();
        wm.add(me);
    }

    @Override
    public void stop(StartEndMeasurement metric) {
        if (log.isTraceEnabled()) {
            log.trace(metric.toString());
        }
        WebMeasurements wm = tl.get();
        wm.add(metric);
        if (metric.getLevel() == 0) {
            rink.add(tl.get());
            tl.set(null);
        }

    }

    @Override
    public boolean isActive() {
        return true;
    }

    private WebMeasurements[] getWebMeasurements() {
        WebMeasurements[] r = new WebMeasurements[RINK_SIZE];
        int i = 0;
        for (WebMeasurements w : rink) {
            r[i] = w;
            i++;
        }
        if (i < RINK_SIZE) {
            WebMeasurements[] n = new WebMeasurements[i];
            System.arraycopy(r, 0, n, 0, i);
            r = n;
        }
        return r;
    }

    public static WebMeasurements[] getCurrentWebMeasurements() {
        final Measurements m = ThreadLocalMeasurementsHolder.get();
        final WebListener l = (m == null ? null : m.getListener(WebListener.class));
        return l == null ? new WebMeasurements[0] : l.getWebMeasurements();
    }
}
