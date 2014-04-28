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
package com.fatwire.gst.metrics.servlet;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.metrics.listener.web.WebListener;
import com.fatwire.gst.metrics.listener.web.WebMeasurement;
import com.fatwire.gst.metrics.listener.web.WebMeasurements;
import com.fatwire.gst.web.servlet.profiling.servlet.View;

public class MeasurementsController {

    final Log log = LogFactory.getLog(getClass());

    private final ServletContext context;

    public MeasurementsController(final ServletContext context) {
        super();
        this.context = context;
    }

    public View handleRequest(final HttpServletRequest request, final HttpServletResponse resp) throws Exception {
        return handleRoot(request);

    }

    private View handleRoot(final HttpServletRequest request) {

        return new MetricsView(WebListener.getCurrentWebMeasurements());
    }

    private final class MetricsView implements View {
        private final NumberFormat nf = new DecimalFormat("#,##0");
        private final NumberFormat decf = new DecimalFormat("0.00");
        private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        private final WebMeasurements[] wm;

        public MetricsView(final WebMeasurements[] currentWebMeasurements) {
            this.wm = currentWebMeasurements;
        }

        @Override
        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.setContentType("text/html; charset=\"UTF-8\"");
            response.setCharacterEncoding("UTF-8");
            final boolean extended = Boolean.parseBoolean(request.getParameter("extended"));
            final PrintWriter writer = response.getWriter();
            writer.println("<!DOCTYPE html>");
            writer.print("<html><head><title>Performance Measurements for "
                    + escapeHtml(context.getServerInfo() + " at " + request.getLocalName() + context.getContextPath())
                    + "</title>");
            writer.print("<style type=\"text/css\">");
            writer.print("table {width:100%}");
            writer.print("td,th {font-size: small; font-family: monospace; }");
            writer.print("tr.request td {background-color: plum}");
            writer.print("td.right {text-align:right;white-space:nowrap}");
            writer.print("td.name {text-align:left}");
            writer.print("</style>");
            writer.println("</head><body>");
            writer.print("<h1>Performance Measurements for " + escapeHtml(context.getServerInfo()) + " at "
                    + escapeHtml(request.getLocalName() + context.getContextPath()) + "</h1>");
            if (extended) {
                writer.print("<a href=\"" + request.getRequestURL().append("?extended=false").toString()
                        + "\">Short report</a>");
            } else {
                writer.print("<a href=\"" + request.getRequestURL().append("?extended=true").toString()
                        + "\">Extended report</a>");
            }
            writer.print("<table><thead>");
            writer.print("<tr>");
            writer.print("<th>type</th><th class=\"right\">start</th><th class=\"right\">level</th><th class=\"right\">elapsed (μs)</th><th>message</th>");
            writer.print("</tr>");
            writer.print("</thead>");

            for (int i = wm.length - 1; i >= 0; i--) {
                if (extended) {
                    writeWebMeasurements(writer, wm[i]);
                } else {
                    writeShortWebMeasurements(writer, wm[i]);
                }
            }
            writer.println();
            writer.print("</table>");
            writer.println("</body></html>");
        }

        protected void writeShortWebMeasurements(final PrintWriter writer, final WebMeasurements w) throws IOException {
            boolean first = true;
            writer.print("<tbody>");
            for (final WebMeasurement m : w.getMeasurements()) {
                if (first) {
                    writer.println();

                    writer.print("<tr>");
                    writer.print("<td>");
                    escapeHtml(writer, m.getType());
                    writer.print("</td>");

                    writer.print("<td class=\"right\">");
                    writer.print(df.format(new Date(m.getStartTime())));
                    writer.print("</td>");
                    writer.print("<td class=\"right\">");
                    writer.print(Long.toString(m.getLevel()));
                    writer.print("</td>");

                    writer.print("<td class=\"right\">");
                    writer.print(nf.format(m.getElapsed() / 1000));
                    writer.print("</td>");
                    writer.print("<td>");
                    escapeHtml(writer, m.getMsg());
                    writer.print("</td>");
                    writer.print("</tr>");
                }

                first = false;
            }
            writer.print("</tbody>");
        }

        protected void writeWebMeasurements(final PrintWriter writer, final WebMeasurements w) throws IOException {
            boolean first = true;
            long spanTime = -1;
            writer.print("<tbody>");
            for (final WebMeasurement m : w.getMeasurements()) {
                writer.println();
                writer.print("<tr class=\"");
                escapeHtml(writer, m.getType());
                writer.print("\">");
                writer.print("<td>");
                escapeHtml(writer, m.getType());
                writer.print("</td>");

                writer.print("<td class=\"right\">");
                if (first) {
                    writer.print(df.format(new Date(m.getStartTime())));
                    spanTime = m.getElapsed();
                    if (spanTime <= 0) {
                        spanTime = 1;
                    }
                } else {
                    writer.print(nf.format(m.getRelativeStart() / 1000));
                }
                writer.print("</td>");
                writer.print("<td class=\"right\">");
                writer.print(Long.toString(m.getLevel()));
                writer.print("</td>");

                writer.print("<td class=\"right\">");
                writer.print(nf.format(m.getElapsed() / 1000));
                writer.print("</td>");
                final double relativeStartPct = (m.getRelativeStart() * 40.0) / spanTime; // 100% time == 40% width, leaves room for text
                writer.print("<td><div style=\"margin-left:");
                writer.print(decf.format(relativeStartPct));
                writer.print("%;border-left: 2pt solid;padding-left:5pt;\">");
                escapeHtml(writer, m.getMsg());
                writer.print("</span></td>");
                writer.print("</tr>");
                first = false;
            }
            writer.print("</tbody>");
        }

    }

}
