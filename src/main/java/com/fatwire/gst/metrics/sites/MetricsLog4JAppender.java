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
/**
 * 
 */
package com.fatwire.gst.metrics.sites;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.fatwire.gst.web.servlet.profiling.logger.BaseAppender;
import com.fatwire.gst.web.servlet.profiling.logger.TimeDebugParser;

public class MetricsLog4JAppender extends BaseAppender {
    public static final String TIME_DEBUG = "com.fatwire.logging.cs.time";

    public MetricsLog4JAppender(final TimeDebugParser parser) {
        this.parser = parser;
    }

    private final TimeDebugParser parser;

    @Override
    protected void append(final LoggingEvent event) {
        if (TIME_DEBUG.equals(event.getLoggerName())) {
            // it's ours
            if (event.getMessage() != null) {
                try {
                    parser.parseIt(String.valueOf(event.getMessage()));
                } catch (final Exception e) {
                    LogLog.debug(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

}
