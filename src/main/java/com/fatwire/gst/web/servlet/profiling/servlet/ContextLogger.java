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

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContextLogger implements ServletContextListener {
    private final Log log = LogFactory.getLog(this.getClass());

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @SuppressWarnings("unchecked")
    public void contextInitialized(final ServletContextEvent event) {
        final ServletContext sc = event.getServletContext();
        log.info("ServerInfo: " + sc.getServerInfo());
        log.info("MajorVersion: " + sc.getMajorVersion());
        log.info("MinorVersion: " + sc.getMinorVersion());
        log.info("ServletContextName: " + sc.getServletContextName());

        for (final Enumeration<String> e = sc.getInitParameterNames(); e
                .hasMoreElements();) {
            final String name = e.nextElement();
            log.info("Init-Param: " + name + "=" + sc.getInitParameter(name));

        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(final ServletContextEvent event) {

        log.info("contextDestroyed: " + event.toString());

    }

}
