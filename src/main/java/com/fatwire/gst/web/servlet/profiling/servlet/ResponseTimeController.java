/*
 * Copyright 2013 Dolf Dijkstra. All Rights Reserved.
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

package com.fatwire.gst.web.servlet.profiling.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeRequestListener;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStats;

public class ResponseTimeController {

    private static final String RESET2 = "reset";

    private static final String ON = "on";

    private static final String BLOCK2 = "block";

    private static final String TIME2 = "time";

    private static final String TURN_OFF = "Turn off";

    private static final String TURN_ON = "Turn on";

    private static final String RESET = "Reset";

    private final Log log = LogFactory.getLog(getClass());

    private final ServletContext context;

    public ResponseTimeController(final ServletContext context) {
        super();
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public View handleRequest(final HttpServletRequest request, final HttpServletResponse resp) throws Exception {

        final String pi = request.getPathInfo();
        log.debug(pi);
        Set<Entry<String, String[]>> s = request.getParameterMap().entrySet();

        for (Entry<String, String[]> e : s) {
            log.debug(e.getKey() + ": " + Arrays.asList(e.getValue()));
        }
        if ("/configure".equals(pi)) {
            return handleConfigure(request, resp);
        }
        return handleRoot(request);

    }

    private View handleRoot(final HttpServletRequest request) {
        final ResponseTimeStats stats = ResponseTimeRequestListener.getResponseTimeStatistic(context);
        final ResponseTimeStatistic root = stats.getRoot();

        final LinkedHashSet<ResponseTimeStatistic> set = new LinkedHashSet<ResponseTimeStatistic>();
        if (Boolean.parseBoolean(request.getParameter("all"))) {
            stats.getAll(set);
        } else {
            stats.getLast(set);
        }

        return new StatsView(set, root, stats.isOn());
    }

    private View handleConfigure(final HttpServletRequest request, final HttpServletResponse resp) {
        if(request.getSession(false) ==null){
            request.getSession(true);
            return new View() {

                @Override
                public void render(HttpServletRequest request, HttpServletResponse response) throws IOException {
                    response.sendRedirect(request.getRequestURL().toString() );
                }

            };
        }
        final ResponseTimeStats stats = ResponseTimeRequestListener.getResponseTimeStatistic(context);
        if ("POST".equalsIgnoreCase(request.getMethod()) && checkValidSubmit(request)) {
            final boolean time = Boolean.parseBoolean(request.getParameter(TIME2));
            final boolean block = Boolean.parseBoolean(request.getParameter(BLOCK2));

            if (RESET.equals(request.getParameter(RESET2))) {
                log.debug(String.format("Reset(%s,%s)", time, block));
                stats.clean(time, block);
            } else if (TURN_ON.equals(request.getParameter(ON))) {
                log.debug(String.format("Turn On(%s,%s)", time, block));
                stats.on(time, block);
            } else if (TURN_OFF.equals(request.getParameter(ON))) {
                log.debug("Turning stats off");
                stats.off();
            } else {
                return new View() {

                    @Override
                    public void render(HttpServletRequest request, HttpServletResponse response) throws IOException {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }

                };
            }

        }
        return new ConfigView(stats);
    }

    private boolean checkValidSubmit(HttpServletRequest request) {
        return request.getParameter(RESET2) != null || request.getParameter(ON) != null;
    }

    private final class ConfigView implements View {
        private static final String _AUTHKEY = "_authkey_";
        private final ResponseTimeStats stats;

        private ConfigView(final ResponseTimeStats stats) {
            this.stats = stats;
        }

        @Override
        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.setContentType("text/html; charset=\"UTF-8\"");
            response.setCharacterEncoding("UTF-8");

            final PrintWriter writer = response.getWriter();
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>Configuration for Performance Statistics for " + context.getServerInfo()
                    + " at " + request.getLocalName() + context.getContextPath() + "</title>");
            writer.println("<style type=\"text/css\">");
            writer.println("td,th,body { font-size: small; font-family: monospace; }");
            writer.println("td {text-align:right}");
            writer.println("td.name {text-align:left}");
            writer.println("</style>");
            writer.println("</head><body>");
            writer.print("<h1>Configuration for Performance Statistics for " + context.getServerInfo() + " at "
                    + request.getLocalName() + context.getContextPath() + "</h1>");
            writer.print("<p><a href=\"../perf\">Statistics</a></p>");

            HttpSession session = request.getSession();
            //
            writer.print("<form method='POST'>");
            if (session != null && session.getAttribute(_AUTHKEY) != null) {
                writer.print("<input type=\"hidden\" name=\"" + _AUTHKEY + "\" value=\""
                        + session.getAttribute(_AUTHKEY) + "\" />");
                writer.print("<input type=\"hidden\" name=\"pagename\" value=\"fatwire/wem/sso/ssoLogin\"/>");
            }

            writer.print("<input type=\"checkbox\" name=\"time\" value=\"true\""
                    + (stats.isUserTime() ? "checked" : "") + "> Collect ThreadCpuTime<br>");
            writer.print("<input type=\"checkbox\" name=\"block\" value=\"true\""
                    + (stats.isBlockCount() ? "checked" : "") + "> Collect ThreadContentionMonitoring<br>");
            writer.print("<p>Collecting ThreadCpuTime and ThreadContentionMonitoring is expensive. They introduce, especially ThreadCpuTime, a significant overhead in measuring the performance of requests</p>");

            if (stats.isOn()) {
                writer.print("<input type=\"submit\" name=\"on\" value=\"" + TURN_OFF + "\">");
                writer.print("<input type=\"submit\" name=\"reset\" value=\"" + RESET + "\">");
            } else {
                writer.print("<input type=\"submit\" name=\"on\" value=\"" + TURN_ON + "\">");
            }
            writer.print("</form>");
            // } else {
            // writer.print("Login to Sites to change the configuration");
            // }
            writer.write("</body></html>");
        }
    }

    private final class StatsView implements View {
        private final LinkedHashSet<ResponseTimeStatistic> set;
        private final ResponseTimeStatistic root;
        // NumberFormat nf = NumberFormat.getNumberInstance();
        NumberFormat nf = new DecimalFormat("#,##0");
        private final boolean on;

        private StatsView(final LinkedHashSet<ResponseTimeStatistic> set, final ResponseTimeStatistic root, boolean on) {
            this.set = set;
            this.root = root;
            this.on = on;
        }

        @Override
        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.setContentType("text/html; charset=\"UTF-8\"");
            response.setCharacterEncoding("UTF-8");

            final PrintWriter writer = response.getWriter();
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>Performance Statistics for " + context.getServerInfo() + " at "
                    + request.getLocalName() + context.getContextPath() + "</title>");
            writer.println("<style type=\"text/css\">");
            writer.println("td,th,body { font-size: small; font-family: monospace; }");
            writer.println("td {text-align:right}");
            writer.println("td.name {text-align:left}");
            writer.println("</style>");
            writer.println("</head><body>");
            writer.print("<h1>Performance Statistics for " + context.getServerInfo() + " at " + request.getLocalName()
                    + context.getContextPath() + "</h1>");
            writer.print("<p><a href=\"perf/configure\">Configuration</a></p>");
            if (!on) {
                writer.write("<h3>Gathering of statistics is turned off</h3>");
            } else {
                writer.write("<table><thead>");
                writer.write("<tr>");
                writer.write("<td class=\"name\">name</td><td>count</td><td>average (μs)</td><td>min (μs)</td><td>max (μs)</td><td>total-system (μs)</td><td>total (μs)</td><td>block</td><td>wait</td>");
                writer.write("</tr>");
                writer.write("</thead><tbody>");
                write(writer, root);

                for (final ResponseTimeStatistic stat : set) {
                    write(writer, stat);

                }
                writer.write("</tbody>");
                writer.write("</table>");
            }
            writer.write("</body></html>");
        }

        protected void write(final Writer writer, final ResponseTimeStatistic stat) throws IOException {
            writer.write("<tr>");
            writer.write("<td class=\"name\">");
            writer.write(stat.getName());
            writer.write("</td>");

            writer.write("<td  class=\"count\">");
            writer.write(Integer.toString(stat.getCount()));
            writer.write("</td>");

            writer.write("<td class=\"avg\">");
            writer.write(nf.format(stat.getAverage()));
            writer.write("</td>");

            writer.write("<td class=\"min\">");
            writer.write(nf.format(stat.getMinTime()));
            writer.write("</td>");

            writer.write("<td class=\"max\">");
            writer.write(nf.format(stat.getMaxTime()));
            writer.write("</td>");

            writer.write("<td class=\"total-system\">");
            writer.write(nf.format(stat.getTotalSystemTime()));
            writer.write("</td>");

            writer.write("<td class=\"total\">");
            writer.write(nf.format(stat.getTotalTime()));
            writer.write("</td>");

            writer.write("<td class=\"block\">");
            writer.write(nf.format(stat.getBlockCount()));
            writer.write("</td>");

            writer.write("<td class=\"wait\">");
            writer.write(nf.format(stat.getWaitCount()));
            writer.write("</tr>");
        }
    }
}
