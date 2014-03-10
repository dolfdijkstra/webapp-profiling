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
package com.fatwire.gst.metrics.listener.logging;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.Metric;
import com.fatwire.gst.metrics.MetricListener;

public class CommonsLoggingListener implements MetricListener {

    private static Log LOG = LogFactory.getLog(CommonsLoggingListener.class.getPackage().getName());

    private TimeUnit precision = TimeUnit.MICROSECONDS;
    private String precisionString =" μs";

    @Override
    public void start(final Metric metric) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("starting " + metric.getType() + "[" + metric.getLevel() + "] at "
                    + convert(metric.getStart() - metric.getLevelZeroStart()) + " " + metric.msg());
        }
    }

    @Override
    public void stop(final Metric metric) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("finished " + metric.getType() + "[" + metric.getLevel() + "] at "
                    + convert(metric.getStart() + metric.elapsed() - metric.getLevelZeroStart()) + " " + metric.msg()
                    + " in " + convert(metric.elapsed()) + precisionString);
        }
    }

    @Override
    public boolean isActive() {
        return LOG.isDebugEnabled();
    }

    @Override
    public void measurement(final Measurement me) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("measurement " + me.getType() + "[" + me.getLevel() + "] at "
                    + convert(me.getEnd() - me.getLevelZeroStart()) + " " + me.msg() + " in " + convert(me.elapsed())
                    + precisionString);
        }

    }

    private long convert(long t) {
        return precision.convert(t, TimeUnit.NANOSECONDS);
    }
}
