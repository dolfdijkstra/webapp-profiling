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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The filter is executed once per request, assuming that the whole request is run by the same thread.
 * 
 * @author Dolf.Dijkstra
 * @since Nov 12, 2008
 */
public abstract class RunOnceFilter extends HttpFilter {

    private final ThreadLocal<Boolean> runOnce = new ThreadLocal<Boolean>() {

        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected Boolean initialValue() {
            return false;
        }

    };

    @Override
    protected final void doFilter(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!runOnce.get()) {
            runOnce.set(true);
            try {
                doFilterOnce(request, response, chain);
            } finally {
                runOnce.set(false);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    protected abstract void doFilterOnce(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException;
}
