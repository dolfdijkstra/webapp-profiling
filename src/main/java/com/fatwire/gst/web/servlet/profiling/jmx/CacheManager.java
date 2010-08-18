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

package com.fatwire.gst.web.servlet.profiling.jmx;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import COM.FutureTense.Util.ftTimedHashtable;

public class CacheManager implements CacheManagerMBean {

    private CacheManagerRunnable runnable;

    private MBeanServer server;

    private Object signal = new Object();

    public int getNumberOfCaches() {
        return ftTimedHashtable.getAllCacheNames().size();
    }

    public void shutdown() throws Exception {
        if (runnable != null) {
            runnable.shutDown();
            server=null;
        }

    }

    public void start() throws Exception {
        if (runnable == null) {
            server = ManagementFactory.getPlatformMBeanServer();
            runnable = new CacheManagerRunnable();
            final Thread t = new Thread(runnable, "CacheManager JMX Thread");
            t.setDaemon(true);
            t.start();
        }

    }

    class CacheManagerRunnable implements Runnable {
        private final Log log = LogFactory.getLog(this.getClass());

        private final long waitTime = 5000L;

        private boolean stop = false;

        public void run() {

            while (!stop) {
                try {
                    synchronized (signal) {
                        signal.wait(waitTime);
                    }
                    registerMBeans();
                } catch (final Exception e) {
                    log.warn(e.getMessage(),e);
                }

            }
        }

        /**
         * registers ftTimedHashtable's RuntimeStats as MBeans in the MBean server
         */
        @SuppressWarnings("unchecked")
        private void registerMBeans() {
            if (stop) return;
            //TODO: deregistering of MBeans when ftTimedHashtable no longer exists
            final Set<String> hashNames = ftTimedHashtable.getAllCacheNames();
            for (final String hashName : hashNames) {
                if (stop) break;
                try {

                    final ObjectName name = new ObjectName(
                             "com.fatwire.gst.web.servlet:type=Cache,name="
                            + ObjectName.quote(hashName));
                    //log.debug(name);
                    if (server !=null && !server.isRegistered(name)) {
                        server.registerMBean(new CacheStats(hashName), name);

                    }

                } catch (final MalformedObjectNameException e) {
                    log.error(e.getMessage(), e);
                } catch (final InstanceAlreadyExistsException e) {
                    log.error(e.getMessage(), e);
                } catch (final MBeanRegistrationException e) {
                    log.error(e.getMessage(), e);
                } catch (final NotCompliantMBeanException e) {
                    log.error(e.getMessage(), e);
                }
            }

        }

        synchronized void shutDown() {
            stop = true;
            synchronized (signal) {
                signal.notifyAll();
            }

        }

    }


}
