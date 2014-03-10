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
package com.fatwire.gst.metrics.sites;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fatwire.gst.metrics.Performance;
import com.fatwire.gst.web.servlet.profiling.logger.ParserCallback;
import com.fatwire.gst.web.servlet.profiling.logger.SimpleTimeDebugParser;

/**
 * @author Dolf Dijkstra
 * 
 */
public class SitesTimeDebugMetricsProvider implements ParserCallback {

    @Override
    public void update(final String type, final String subType, final long time) {
        Performance.measurement(type, subType, time, TimeUnit.MILLISECONDS);
    }

    private Logger log;
    private Level oldLevel;
    private boolean oldAdditivity;

    public void init() {
        log = Logger.getLogger(MetricsLog4JAppender.TIME_DEBUG);
        if (log.getAppender("metrics") == null) {

            oldLevel = log.getLevel();
            oldAdditivity = log.getAdditivity();
            if (!log.isDebugEnabled()) {
                log.setLevel(Level.DEBUG);
                log.setAdditivity(false);
            }

            final SimpleTimeDebugParser parser = new SimpleTimeDebugParser(this);

            final MetricsLog4JAppender a = new MetricsLog4JAppender(parser);
            a.setName("metrics");
            a.activateOptions();
            log.addAppender(a);
        }

    }

    public void close() {
        if (log != null) {
            log.removeAppender("metrics");
            log.setLevel(oldLevel);
            log.setAdditivity(oldAdditivity);

        }
    }
}
