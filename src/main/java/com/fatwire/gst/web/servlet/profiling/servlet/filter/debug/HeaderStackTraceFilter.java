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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * prints a stack trace to the log file each time the servlet sets a response header or a cookie
 * 
 * @author Dolf.Dijkstra
 * @since Jun 27, 2009
 */

public class HeaderStackTraceFilter implements Filter {

    private static Log log = LogFactory.getLog(HeaderStackTraceFilter.class);

    @Override
    public void destroy() {

        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,

    final FilterChain chain) throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {

            chain.doFilter(request, new HttpServletResponseWrapper(

            (HttpServletResponse) response) {

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#addCookie(javax.servlet.http.Cookie)

                 */

                @Override
                public void addCookie(final Cookie cookie) {

                    log.debug(cookie == null ? "null cookie" : cookie.getName()

                    + " '" + cookie.getValue() + "'", new Exception());

                    super.addCookie(cookie);

                }

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#addDateHeader(java.lang.String, long)

                 */

                @Override
                public void addDateHeader(final String name, final long date) {

                    log.debug(name + "=" + date, new Exception());

                    super.addDateHeader(name, date);

                }

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#addHeader(java.lang.String, java.lang.String)

                 */

                @Override
                public void addHeader(final String name, final String value) {

                    log.debug(name + "=" + value, new Exception());

                    super.addHeader(name, value);

                }

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#addIntHeader(java.lang.String, int)

                 */

                @Override
                public void addIntHeader(final String name, final int value) {

                    log.debug(name + "=" + value, new Exception());

                    super.addIntHeader(name, value);

                }

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#setDateHeader(java.lang.String, long)

                 */

                @Override
                public void setDateHeader(final String name, final long date) {

                    log.debug(name + "=" + date, new Exception());

                    super.setDateHeader(name, date);

                }

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#setHeader(java.lang.String, java.lang.String)

                 */

                @Override
                public void setHeader(final String name, final String value) {

                    log.debug(name + "=" + value, new Exception());

                    super.setHeader(name, value);

                }

                /* (non-Javadoc)

                 * @see javax.servlet.http.HttpServletResponseWrapper#setIntHeader(java.lang.String, int)

                 */

                @Override
                public void setIntHeader(final String name, final int value) {

                    log.debug(name + "=" + value, new Exception());

                    super.setIntHeader(name, value);

                }

            });

        } else {

            chain.doFilter(request, response);

        }

    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

        // TODO Auto-generated method stub

    }

}
