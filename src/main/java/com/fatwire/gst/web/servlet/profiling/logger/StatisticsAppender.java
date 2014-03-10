/*
 * Copyright 2006 FatWire Corporation. All Rights Reserved.
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

/**
 * 
 */
package com.fatwire.gst.web.servlet.profiling.logger;

import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

public class StatisticsAppender extends BaseAppender {
    public static final String TIME_DEBUG = "com.fatwire.logging.cs.time";

    private StatisticsProvider provider;

    /**
     * @param provider
     * @param parser
     */
    public StatisticsAppender(StatisticsProvider provider, SimpleTimeDebugParser parser) {
        super();
        this.provider = provider;
        this.parser = parser;
    }

    private SimpleTimeDebugParser parser;// = new SimpleTimeDebugParser(this);

    public Stat[] getStats() {
        return provider.getStats();
    }

    protected void append(LoggingEvent event) {
        if (TIME_DEBUG.equals(event.getLoggerName())) {
            // it's ours
            if (event.getMessage() != null) {
                try {
                    parser.parseIt(String.valueOf(event.getMessage()));
                } catch (Exception e) {
                    LogLog.debug(e.getMessage(), e);
                }
            }
        }
    }

    // @Override
    public void close() {
        if (closed)
            return;
        closed = true;
        provider.close();
    }

    // @Override
    public boolean requiresLayout() {
        return false;
    }

}
