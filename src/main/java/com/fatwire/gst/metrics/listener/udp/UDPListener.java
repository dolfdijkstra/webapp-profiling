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
package com.fatwire.gst.metrics.listener.udp;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;
import com.fatwire.gst.metrics.MeasurementListener;
import com.fatwire.gst.metrics.servlet.MeasurementsServletListener;

public class UDPListener implements MeasurementListener, Closeable {
    private final Log log = LogFactory.getLog(MeasurementsServletListener.class);

    private UDPClient cl;

    public UDPListener(final UDPClient client) {
        cl = client;
    }

    @Override
    public void start(final StartEndMeasurement metric) {
        try {
            if (cl != null) {
                cl.sendStart(metric);
            }
        } catch (final IOException e) {
            log.warn(e);
        }
    }

    @Override
    public void stop(final StartEndMeasurement metric) {
        try {
            if (cl != null) {
                cl.sendEnd(metric);
            }
        } catch (final IOException e) {
            log.warn(e);
        }

    }

    @Override
    public void measurement(final Measurement m) {
        try {
            if (cl != null) {
                cl.sendEvent(m);
            }
        } catch (final IOException e) {
            log.warn(e);
        }
    }

    @Override
    public boolean isActive() {
        return cl != null;
    }

    @Override
    public void close() throws IOException {
        cl.close();
        cl = null;
    }
}
