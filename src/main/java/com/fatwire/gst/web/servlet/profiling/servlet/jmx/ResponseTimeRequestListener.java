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

package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ResponseTimeRequestListener implements ServletRequestListener, ServletContextListener {
    public static final String MBEAN_NAME = "com.fatwire.gst.web.servlet:type=ResponseTimeStatistic";

    private final Log log = LogFactory.getLog(this.getClass());

    private static Map<String, ResponseTimeStats> contextMap = new ConcurrentHashMap<String, ResponseTimeStats>();

    private ResponseTimeStats contextStat;

    public void requestInitialized(ServletRequestEvent event) {
        if (event.getServletRequest() instanceof HttpServletRequest)
            try {
                contextStat.startMeasurement();
            } catch (Throwable e) {
                log.debug(e, e);
            }

    }

    public void requestDestroyed(ServletRequestEvent event) {
        if (event.getServletRequest() instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
            contextStat.finishMeasurement(request);

        }

    }

    public void contextInitialized(ServletContextEvent sce) {
        log.debug("contextInitialized " + sce.getServletContext().getServletContextName());
        boolean timeFlag = Boolean.parseBoolean(System.getProperty(ResponseTimeRequestListener.class.getName()
                .toLowerCase() + ".time", "false"));
        boolean countFlag = Boolean.parseBoolean(System.getProperty(ResponseTimeRequestListener.class.getName()
                .toLowerCase() + ".count", "false"));
        boolean onFlag = Boolean.parseBoolean(System.getProperty(ResponseTimeRequestListener.class.getName()
                .toLowerCase() + ".on", "false"));

        String path = getPath(sce.getServletContext());
        contextStat = new ResponseTimeStats(MBEAN_NAME, path, true, onFlag, timeFlag, countFlag);
        contextMap.put(path, contextStat);

    }

    public void contextDestroyed(ServletContextEvent sce) {
        String path = getPath(sce.getServletContext());

        contextMap.remove(path);
        try {
            contextStat.destroy();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        contextStat = null;

    }

    private static String getPath(ServletContext servletContext) {
        String path = servletContext.getContextPath();
        if (path == null)
            path = "/";
        return path;
    }

    public static ResponseTimeStats getResponseTimeStatistic(ServletContext context) {
        return contextMap.get(getPath(context));
    }

}
