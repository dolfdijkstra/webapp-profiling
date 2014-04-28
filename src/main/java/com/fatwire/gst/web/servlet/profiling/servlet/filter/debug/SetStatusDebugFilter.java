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
package com.fatwire.gst.web.servlet.profiling.servlet.filter.debug;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * prints a stack trace to the log file each time the servlet sets a status code
 * 
 * @author Dolf.Dijkstra
 * @since March 10,2014
 */

public class SetStatusDebugFilter implements Filter {

    private static Log log = LogFactory.getLog(SetStatusDebugFilter.class);

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,

    final FilterChain chain) throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {

            chain.doFilter(request, new HttpServletResponseWrapper(

            (HttpServletResponse) response) {

                /* (non-Javadoc)
                 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int, java.lang.String)
                 */
                @Override
                public void sendError(final int sc, final String msg) throws IOException {
                    log.info("sendError", new Exception());
                    super.sendError(sc, msg);
                }

                /* (non-Javadoc)
                 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int)
                 */
                @Override
                public void sendError(final int sc) throws IOException {
                    log.info("sendError", new Exception());
                    super.sendError(sc);
                }

                /* (non-Javadoc)
                 * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int)
                 */
                @Override
                public void setStatus(final int sc) {
                    log.info("setStatus", new Exception());
                    super.setStatus(sc);
                }

                /* (non-Javadoc)
                 * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int, java.lang.String)
                 */
                @Override
                public void setStatus(final int sc, final String sm) {
                    log.info("setStatus", new Exception());
                    super.setStatus(sc, sm);
                }

            });

        } else {

            chain.doFilter(request, response);

        }

    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

}
