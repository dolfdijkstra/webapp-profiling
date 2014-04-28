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
package com.fatwire.gst.web.servlet.profiling.servlet;

import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * {@link ServletRequestListener} that invalidates sessions for requests from
 * bots and crawlers to prevent session manager overload.
 * 
 * @author dolf
 * 
 */
public class CrawlerSessionDestroyListener implements ServletRequestListener, ServletContextListener {

    private Pattern[] patterns;

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        if (patterns == null || patterns.length == 0)
            return;
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        String ua = request.getHeader("user-agent");
        if (ua == null || ua.length() == 0 || ua.trim().length() == 0)
            return;
        for (Pattern p : patterns) {
            if (p.matcher(ua).matches()) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                break;
            }
        }

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        patterns = new Pattern[1];
        patterns[0] = Pattern.compile("Wget/.*");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
