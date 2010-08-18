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

import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.helpers.LogLog;

public class StatisticsProvider implements TimeDebugParser.ParserCallback, StatisticsProviderMBean {

    public static String NAME = "com.fatwire.gst.web.servlet:type=StatisticsProvider";

    private ConcurrentHashMap<String, Stat> stats = new ConcurrentHashMap<String, Stat>(200, 0.75f, 2); // append
                                                                                                        // is
                                                                                                        // called
                                                                                                        // from
                                                                                                        // asynchronized
                                                                                                        // method

    private MBeanServer server;

    /**
     * @param server
     */
    public StatisticsProvider(MBeanServer server) {
        super();
        this.server = server;
    }

    public Stat[] getStats() {
        synchronized (stats) {
            return stats.values().toArray(new Stat[0]);
        }
    }

    public void close() {

        for (Stat s : getStats()) {
            if (s.getName() != null) {
                try {
                    server.unregisterMBean(s.getName());
                } catch (Throwable e) {
                    LogLog.warn(e.getMessage(), e);
                }
            }
        }
        stats.clear();
        server = null;
    }

    public void update(String type, String subType, long time) {
        if (time > 3722801423L)
            return; // do not log large values do to bug in CS when turning on
                    // time debug.
        Stat s = getStat(type, subType);
        s.update(time);

    }

    private Stat getStat(String type, String subType) {
        String n = subType != null ? (type + "-" + subType) : type;
        Stat s = stats.get(n);
        if (s == null) {
            ObjectName name = null;
            try {
                name = new ObjectName("com.fatwire.gst.web.servlet:type=StatFromTimeDebug,group=" + type
                        + (subType != null ? ",subType=" + subType : ""));
            } catch (MalformedObjectNameException e) {
                LogLog.warn(e.getMessage(), e);
            } catch (NullPointerException e) {
                LogLog.warn(e.getMessage(), e);
            }
            s = new Stat();
            s.setType(type);
            s.setSubType(subType);
            s.setName(name);
            stats.put(n, s);
            if (name != null) {
                try {
                    this.server.registerMBean(s, name);
                } catch (InstanceAlreadyExistsException e) {
                    LogLog.warn(e.getMessage(), e);
                } catch (MBeanRegistrationException e) {
                    LogLog.warn(e.getMessage(), e);
                } catch (NotCompliantMBeanException e) {
                    LogLog.warn(e.getMessage(), e);
                }
            }

        }
        return s;
    }

    /**
     * @return the server
     */
    public MBeanServer getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(MBeanServer server) {
        this.server = server;
    }

    public void reset() {
        for (Stat s : getStats()) {
            s.reset();
        }
    }

}
