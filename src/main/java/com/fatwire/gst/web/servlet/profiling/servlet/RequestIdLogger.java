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
package com.fatwire.gst.web.servlet.profiling.servlet;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;

public class RequestIdLogger implements ServletRequestListener {
    private Log log = LogFactory.getLog("com.fatwire.logging.cs.time");
    private AtomicLong idCounter = new AtomicLong(System.currentTimeMillis());
    private ThreadLocal<StartId> ids = new ThreadLocal<StartId>();

    public void requestInitialized(ServletRequestEvent sre) {
        long id = idCounter.incrementAndGet();

        MDC.put("request-id", id);
        if (log.isDebugEnabled()) {

            StartId si = new StartId();
            si.id = id;
            si.url = getUrl(sre.getServletRequest());
            si.start = System.nanoTime();
            ids.set(si);
            log.debug("Start request " + id + " " + si.url);
        }
    }

    public void requestDestroyed(ServletRequestEvent sre) {
        if (log.isDebugEnabled()) {

            StartId si = ids.get();
            if (si != null) {
                long id = si.id;
                long elapsed = (System.nanoTime() - si.start) / 1000000L;

                log.debug("Executed request " + id + " '" + si.url + "' in " + (elapsed) + " ms");
            }
        }
        MDC.remove("request-id");

    }

    private String getUrl(ServletRequest servletRequest) {
        if (servletRequest instanceof HttpServletRequest) {
            return getUrl((HttpServletRequest) servletRequest);
        }
        return params(servletRequest);
    }

    private String params(ServletRequest r) {
        if (r == null)
            return "";
        @SuppressWarnings("unchecked")
        Map<String, String[]> m = r.getParameterMap();
        if (m == null || m.isEmpty())
            return "";
        StringBuilder b = new StringBuilder();
        for (Entry<String, String[]> e : m.entrySet()) {
            for (String v : e.getValue()) {
                b.append(b.length() == 0 ? "?" : "&");
                b.append(e.getKey()).append("=").append(v);
            }
        }
        return b.toString();
    }

    private String getUrl(HttpServletRequest request) {
        return request.getRequestURL().append(params(request)).toString();

    }

    static class StartId {
        long id;
        long start;
        String url;
    }

}
