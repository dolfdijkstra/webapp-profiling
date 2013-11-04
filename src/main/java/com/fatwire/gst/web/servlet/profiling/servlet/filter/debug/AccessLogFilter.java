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

package com.fatwire.gst.web.servlet.profiling.servlet.filter.debug;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fatwire.gst.web.servlet.profiling.servlet.filter.RunOnceFilter;

public class AccessLogFilter extends RunOnceFilter {

    private char sep = '\t';

    public void destroy() {
        //nothing
    }

    @Override
    protected void doFilterOnce(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (log.isInfoEnabled()) {
            StringBuilder b = new StringBuilder(128);

            b.append(request.getMethod()).append(sep);
            b.append(request.getScheme()).append(sep);

            b.append(request.getServerName()).append(sep);
            b.append(request.getServerPort()).append(sep);

            //b.append(request.getRequestURL()).append(sep);
            b.append(request.getRequestURI()).append(sep);
            b.append(request.getProtocol()).append(sep);

            b.append(request.getContextPath()).append(sep);
            b.append(request.getServletPath()).append(sep);
            b.append(request.getPathInfo()).append(sep);
            b.append(request.getQueryString()).append(sep);

            //b.append(request.getPathTranslated()).append(sep);

            b.append(request.getCharacterEncoding()).append(sep);
            b.append(request.getContentLength()).append(sep);
            b.append(request.getContentType()).append(sep);

            Cookie[] c = request.getCookies();
            if (c != null && c.length > 0) {
                for (int i = 0; i < c.length; i++) {
                    Cookie cookie = c[i];
                    if (i > 0)
                        b.append(";");
                    if (cookie != null) {
                        b.append(cookie.getName()).append("=").append(
                                cookie.getValue());
                    }
                }
            }
            b.append(sep);

            b.append(request.getLocale()).append(sep);

            b.append(request.getRemoteAddr()).append(sep);
            b.append(request.getRemoteHost()).append(sep);
            b.append(request.getRemoteUser()).append(sep);

            b.append(request.getRequestedSessionId()).append(sep);

            HttpSession s = request.getSession(false);
            if (s != null) {
                b.append(s.getId());
            }
            b.append(sep);

            Principal p = request.getUserPrincipal();
            if (p != null) {
                b.append(p.getName());
            }
            log.info(b.toString());
        }
        chain.doFilter(request, response);

    }

    public void init(FilterConfig arg0) throws ServletException {
        //nothing
    }

}
