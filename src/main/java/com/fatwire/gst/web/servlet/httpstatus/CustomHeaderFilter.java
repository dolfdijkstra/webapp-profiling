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

package com.fatwire.gst.web.servlet.httpstatus;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A response header filter sets the customer status headers from
 * ContentServer.
 * </p>
 *
 *
 * @since 16 June 2009
 * @author Daniel Iversen, Ram Sabnavis, Dolf Dijkstra
 *
 */
public class CustomHeaderFilter implements Filter {

    private static Log log = LogFactory.getLog(CustomHeaderFilter.class);

    /**
     * This method is called by application server at the shutdown and destroys
     * filterConf object
     */

    public void destroy() {

    }

    /**
     * This method performs the filtering process on the response headers to set
     * the custom headers in the response object and initiates the subsequent
     * filter chain
     *
     * @param request
     *            Request object
     * @param response
     *            Response Object
     * @param filterChain
     *            FilterChain Object
     * @throws IOException
     *             , ServletException
     */

    public void doFilter(final ServletRequest request,
            final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {

        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (log.isDebugEnabled()) {
            printRequestHeaders(request);
        }

        final HttpServletResponseWrapper wrapper = new StatusFilterHttpResponseWrapper(
                httpResponse);

        filterChain.doFilter(request, wrapper);

    }

    @SuppressWarnings("unchecked")
    private void printRequestHeaders(final ServletRequest request) {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;

        for (Enumeration en = httpRequest.getHeaderNames(); en
                .hasMoreElements();) {
            String headername = (String) en.nextElement();
            log.debug(headername + ": " + httpRequest.getHeader(headername));
        }
    }

    /**
     * This method is called by application server at the startup and
     * initializes the FilterConfig object
     *
     * @param filterConf
     *            FilterConfig object
     * @throws ServletException
     */

    public void init(final FilterConfig filterConf) throws ServletException {
    }
}// end of CustomHeaderFilter

