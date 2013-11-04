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

package com.fatwire.gst.web.servlet.profiling.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.web.servlet.profiling.servlet.Util;

public class LoggerFilter extends HttpFilter implements Filter {
    private final Log log = LogFactory.getLog(this.getClass());

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(final FilterConfig config) throws ServletException {
        log.info("init: " + config.getFilterName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(final HttpServletRequest request,
            final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        String sessionid;
        if (request.getSession(false) != null) {
            sessionid = request.getSession().getId();
        } else {
            sessionid = "";
        }
        final String url = Util.getFullUrl(request);
        final long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            final long end = System.currentTimeMillis();

            log(sessionid, (end - start), url);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // nothing to destroy
    }

    /**
     * 
     * override to do something more fancy then logging to commons-logging
     * @param session
     * @param elapsed
     * @param url
     */
    protected void log(final String session, final long elapsed,
            final String url) {
        if (log.isInfoEnabled()) {
            log.info(session + "|" + url + " in " + Long.toString(elapsed)
                    + " ms");
        }

    }

}
