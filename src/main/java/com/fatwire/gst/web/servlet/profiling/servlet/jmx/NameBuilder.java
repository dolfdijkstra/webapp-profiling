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
package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

public class NameBuilder {
    private static final String UTF_8 = "UTF-8";
    private final String[] parameters = new String[] { "pagename", "blobtable", "c", "rendermode" };

    String sanitize(final String s) {
        return s.replaceAll("[,=:\"*?]", "_");
    }

    String extractName(final HttpServletRequest request) {
        if (request == null) {
            return "UNKNOWN";
        }
        final StringBuilder b = new StringBuilder("path=").append("\"").append(request.getRequestURI()).append("\"");

        for (final String a : parameters) {
            if (request.getParameter(a) != null) {
                b.append(',');
                b.append(a);
                b.append('=');
                b.append("\"");
                b.append(request.getParameter(a));
                b.append("\"");
                break;
            }
        }
        return b.toString();
    }

    String decode(final String a) {
        try {
            return java.net.URLDecoder.decode(a, UTF_8);
        } catch (final UnsupportedEncodingException e) {
            return a;
        }

    }
}
