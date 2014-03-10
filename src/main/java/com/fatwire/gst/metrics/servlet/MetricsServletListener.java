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
package com.fatwire.gst.metrics.servlet;

import java.io.Closeable;
import java.util.Map;

import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.fatwire.gst.metrics.MetricListener;
import com.fatwire.gst.metrics.Metrics;
import com.fatwire.gst.metrics.SwitchesManager;
import com.fatwire.gst.metrics.ThreadLocalMetricsHolder;
import com.fatwire.gst.metrics.sites.SitesTimeDebugMetricsProvider;

public class MetricsServletListener implements ServletRequestListener, ServletContextListener {
    private Log log = LogFactory.getLog(MetricsServletListener.class);
    private XmlBeanFactory beanFactory;
    private Map<String, MetricListener> listeners;
    private Map<String, SitesTimeDebugMetricsProvider> providers;
    private Metrics metrics = new Metrics();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        try {
            ThreadLocalMetricsHolder.set(metrics);
            if (metrics.isActive()) {

                HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
                String qs = request.getQueryString();
                log.trace(request.getRequestURI());
                if (qs == null) {
                    metrics.start("request", request.getMethod() + " " + request.getRequestURL().toString());
                } else {
                    metrics.start("request", request.getMethod() + " " + request.getRequestURL().append("?").append(qs).toString());
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        try {
            if (metrics.isActive()) {
                metrics.terminate();
            }
            ThreadLocalMetricsHolder.set(null);
        } catch (Exception e) {
            log.warn(e, e);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            beanFactory = new XmlBeanFactory(new ClassPathResource("metrics-listener.xml"));
            listeners = beanFactory.getBeansOfType(MetricListener.class);

            for (Map.Entry<String, MetricListener> e : listeners.entrySet()) {
                MetricListener listener = e.getValue();
                log.info("adding listener to Metrics: " + listener.getClass().getName() + ", active: "
                        + metrics.isActive());
                metrics.addListener(listener);
            }
            providers = beanFactory.getBeansOfType(SitesTimeDebugMetricsProvider.class);
            for (SitesTimeDebugMetricsProvider metricsProvider : providers.values()) {
                metricsProvider.init();
            }
            log.info("metrics is " + (metrics.isActive() ? "" : " not ") + " active.");
            ObjectName name = ObjectName.getInstance("com.fatwire.gst.metrics:type=Switches");
            java.lang.management.ManagementFactory.getPlatformMBeanServer().registerMBean(new SwitchesManager(), name);
        } catch (Throwable e) {
            log.error(e.getMessage() + " whilst configuring metrics listeners", e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            for (Map.Entry<String, MetricListener> e : listeners.entrySet()) {
                MetricListener listener = e.getValue();
                metrics.removeListener(listener);
                if (listener instanceof Closeable) {
                    try {
                        ((Closeable) listener).close();
                    } catch (Exception e1) {
                        log.error(e1.getMessage() + " whilst closing metrics listener " + listener, e1);
                    }
                }
            }
            for (SitesTimeDebugMetricsProvider metricsProvider : providers.values()) {
                metricsProvider.close();
            }
        } catch (Throwable e) {
            log.error(e.getMessage() + " whilst destroying metrics listeners", e);
        }

    }

}
