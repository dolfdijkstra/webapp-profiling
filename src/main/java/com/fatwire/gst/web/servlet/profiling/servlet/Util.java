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

import javax.servlet.http.HttpServletRequest;

public class Util {
    private Util() {
    }

    /**
     * Construct a String representation of this request
     * 
     * @param request
     * @return the url from the request
     */
    public static String getFullUrl(final HttpServletRequest request) {

        final StringBuilder b = new StringBuilder(request.getMethod()).append(
                '|').append(request.getRequestURI());
        if (request.getQueryString() != null) {
            b.append('?').append(request.getQueryString());
        }
        return b.toString();

    }
}