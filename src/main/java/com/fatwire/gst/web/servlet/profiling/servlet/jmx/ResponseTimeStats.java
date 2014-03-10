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

package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.web.servlet.profiling.servlet.Rink;

public class ResponseTimeStats {
    private final class ThreadLocalMeasurement extends ThreadLocal<Measurement> {
        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected Measurement initialValue() {
            return new Measurement(timeFlag, blockCountFlag, generatation.get());
        }

    }

    private final AtomicInteger generatation = new AtomicInteger();
    private final String mBeanName;
    private final Log log = LogFactory.getLog(this.getClass());
    private final ResponseTimeStatistic root;
    private NameBuilder nameBuilder = new NameBuilder();
    private final Rink<ResponseTimeStatistic> rink = new Rink<ResponseTimeStatistic>(1000);

    private final ConcurrentHashMap<String, ResponseTimeStatistic> names = new ConcurrentHashMap<String, ResponseTimeStatistic>(
            800, 0.75f, 400);

    private volatile boolean on = false;
    private volatile boolean timeFlag = false;
    private volatile boolean blockCountFlag = false;
    private boolean jmx;
    private ThreadLocalMeasurement measurements = new ThreadLocalMeasurement();

    public ResponseTimeStats(String name, String context, boolean jmx, boolean on, boolean timeFlag, boolean countFlag) {
        this.jmx = jmx;
        this.on = on;
        this.timeFlag = timeFlag;
        this.blockCountFlag = countFlag;
        this.mBeanName = name + ",context=" + context;
        root = new ResponseTimeStatistic(context);
        try {

            if (jmx)
                ManagementFactory.getPlatformMBeanServer().registerMBean(root, ObjectName.getInstance(mBeanName));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    private void signal(Measurement m, String name) {
        if (!on)
            return;
        root.signal(m);

        ResponseTimeStatistic stat = names.get(name);
        boolean newStat = false;
        if (stat == null) {
            stat = new ResponseTimeStatistic(name);

            ResponseTimeStatistic old = names.putIfAbsent(name, stat);
            if (old != null) {
                stat = old;
                newStat = false;
            } else {
                newStat = true;
            }
        }
        stat.signal(m);
        rink.add(stat);

        if (newStat && jmx) {
            String beanName = mBeanName + "," + name;
            try {

                ManagementFactory.getPlatformMBeanServer().registerMBean(stat, ObjectName.getInstance(beanName));
            } catch (Exception e) {
                log.warn(e.getMessage() + " for " + beanName, e);
            }

        }

    }

    /**
     * @return the root
     */
    public ResponseTimeStatistic getRoot() {
        return root;
    }

    public void getLast(Collection<ResponseTimeStatistic> set) {
        for (ResponseTimeStatistic s : rink) {
            set.add(s);
        }

    }

    public void getAll(Collection<ResponseTimeStatistic> set) {
        for (ResponseTimeStatistic s : names.values()) {
            set.add(s);
        }

    }

    protected void unregister(String query) throws MalformedObjectNameException, NullPointerException {
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

    public void clean(boolean time, boolean block) {
        checkStateChange(time, block);

        boolean s = this.on;
        this.on = false; // block signalling while the internal state is changed
        destroy();
        try {

            if (jmx)
                ManagementFactory.getPlatformMBeanServer().registerMBean(root, ObjectName.getInstance(mBeanName));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        this.on = s;
    }

    public void on(boolean time, boolean count) {
        checkStateChange(time, count);
        this.on = true;
    }

    public void off() {
        this.on = false;
        destroy();
    }

    private void checkStateChange(boolean time, boolean block) {
        if (time != this.timeFlag || block != blockCountFlag) {
            this.timeFlag = time;
            this.blockCountFlag = block;
            int gen = generatation.incrementAndGet();
            log.info(String.format("Changing state,  increasing generation to: %d", gen));
        }
    }

    public void destroy() {
        try {
            if (jmx)
                unregister(mBeanName + ",*");
        } catch (Exception ee) {
            log.error(ee.getMessage(), ee);
        }

        this.names.clear();
        rink.reset();
        root.reset();

    }

    public boolean isOn() {
        return on;
    }

    public boolean isUserTime() {
        return timeFlag;
    }

    public boolean isBlockCount() {
        return blockCountFlag;
    }

    public Measurement startMeasurement() {
        if (!on)
            return null;
        try {
            Measurement m = measurements.get();
            if (generatation.get() != m.getGeneration()) {
                measurements.remove();
                m = measurements.get();
            }

            m.start();
            return m;
        } catch (Throwable e) {
            log.debug(e, e);
        }
        return null;

    }

    public void finishMeasurement(HttpServletRequest request) {
        if (!on)
            return;
        try {

            Measurement measurement = measurements.get();
            if (measurement != null) {
                try {
                    measurement.stop();
                    if (generatation.get() == measurement.getGeneration()) {
                        String name = nameBuilder.extractName(request);
                        signal(measurement, name);
                    }
                } finally {
                    if (generatation.get() != measurement.getGeneration()) {
                        // remove old threadlocal values, so a new Measurement
                        // can
                        // be created through ThreadLocal.initialValue();
                        measurements.remove();
                    }
                }
            }
        } catch (Throwable e) {
            log.debug(e, e);
        }

    }

}
