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

package com.fatwire.gst.web.servlet.profiling.logger;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;

public class Log4JAppenderLifeCycleManager implements LifeCycleManager {

    private Logger log;

    private Level oldLevel;

    private boolean oldAdditivity;

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.LifeCycleManager#init()
     */
    public void init() {
        log = Logger.getLogger(StatisticsAppender.TIME_DEBUG);
        if (log.getAppender("stats") == null) {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();

            oldLevel = log.getLevel();
            oldAdditivity = log.getAdditivity();
            if (!log.isDebugEnabled()) {
                log.setLevel(Level.DEBUG);
                log.setAdditivity(false);
            }
            StatisticsProvider provider = new StatisticsProvider(server);
            TimeDebugParser parser = new TimeDebugParser(provider);

            StatisticsAppender a = new StatisticsAppender(provider, parser);
            a.setName("stats");
            // a.setServer(server);
            a.activateOptions();
            log.addAppender(a);
        }

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.LifeCycleManager#destroy()
     */
    public void destroy() {
        if (log != null) {
            log.removeAppender("stats");
            log.setLevel(oldLevel);
            log.setAdditivity(oldAdditivity);
            removeMBeans();

        }
    }

    private void removeMBeans() {
        try {

            unregister("com.fatwire.gst.web.servlet:type=StatFromTimeDebug,*");
        } catch (MalformedObjectNameException e) {
            LogLog.error(e.getMessage());
        } catch (NullPointerException e) {
            LogLog.error(e.getMessage());
        }
    }

    void detach() {
        if (log != null) {
            log.removeAppender("stats");
        }
    }

    public void unregister(String query) throws MalformedObjectNameException, NullPointerException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance(query);
        Set<ObjectName> mbeans = server.queryNames(name, null);
        for (ObjectName on : mbeans) {
            try {
                server.unregisterMBean(on);
            } catch (Exception ee) {
                log.error(ee.getMessage(), ee);
            }
        }

    }

}
