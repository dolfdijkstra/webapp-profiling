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

package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

public class NameBuilder {
    private final String[] parameters = new String[] { "pagename", "blobtable" };
    String sanitize(String s) {
        return s.replaceAll("[,=:\"*?]", "_");
    }

    String extractName(HttpServletRequest request) {

        StringBuilder b = new StringBuilder(",path=").append(request
                .getRequestURI());
        //        if (request.getQueryString() != null) {
        //            for (String part : request.getQueryString().split("&")) {
        //                if (part.startsWith("pagename=")) {
        //                    b.append(',');
        //                    try {
        //                        b.append(java.net.URLDecoder.decode(part, "UTF-8"));
        //                    } catch (UnsupportedEncodingException e) {
        //                        b.append(part);
        //                    }
        //                } else if (part.startsWith("blobtable=")) {
        //                    b.append(',').append(part);
        //                }
        //            }
        //
        //        }

        for (String a : parameters) {
            if (request.getParameter(a) != null) {
                b.append(',');
                b.append(a);
                b.append('=');
                try {
                    b.append(java.net.URLDecoder.decode(
                            request.getParameter(a), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    b.append(request.getParameter(a));
                }
                break;
            }
        }
        return b.toString();
    }

}
