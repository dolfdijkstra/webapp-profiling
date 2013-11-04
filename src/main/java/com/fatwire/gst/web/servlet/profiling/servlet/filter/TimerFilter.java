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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatwire.gst.web.servlet.profiling.servlet.Util;

/**
 * 
 * Servlet filter that prints response time to log
 * 
 * @author Dolf.Dijkstra
 * 
 */

public class TimerFilter extends HttpFilter implements Filter {

    /**
     * Prints the time it took to process this request.
     * If you don't want this info, shut down the filter by removing it from the web-app.
     * Otherwise this filter will consume resources.
     * 
     */

    public void doFilter(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        long start = System.currentTimeMillis();
        String url = Util.getFullUrl(request);
        try {
            chain.doFilter(request, response);
        } finally {
            long end = System.currentTimeMillis();
            log.debug(Long.toString(end - start) + " ms for " + url);
        }

    }

}
