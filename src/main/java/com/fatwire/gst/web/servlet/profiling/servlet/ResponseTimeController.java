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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeRequestListener;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStats;
import com.fatwire.gst.web.servlet.profiling.servlet.view.CounterHtmlView;
import com.fatwire.gst.web.servlet.profiling.servlet.view.CounterJsonView;
import com.fatwire.gst.web.servlet.profiling.servlet.view.CounterTextView;

public class ResponseTimeController {

    private static final String RESET2 = "reset";

    private static final String ON = "on";

    private static final String BLOCK = "block";

    private static final String TIME = "time";

    private static final String TURN_OFF = "Turn off";

    private static final String TURN_ON = "Turn on";

    private static final String RESET = "Reset";

    final Log log = LogFactory.getLog(getClass());

    private final ServletContext context;

    private final View selfRedirectView = new SelfRedirectView();

    private final Timer timer = new Timer("Requests Performance Collector", true);

    final CounterHistory counterHistory = new CounterHistory(15 * 60);
    final CounterHistoryBigInteger timeHistory = new CounterHistoryBigInteger(15 * 60);

    public ResponseTimeController(final ServletContext context) {
        super();
        this.context = context;
        final TimerTask counterTask = new TimerTask() {

            @Override
            public void run() {
                final ResponseTimeStats stats = ResponseTimeRequestListener.getResponseTimeStatistic(context);
                if (stats.isOn()) {
                    counterHistory.add(stats.getRoot().getCount());
                    timeHistory.add(stats.getRoot().getTotalTime());
                }

            }

        };
        timer.scheduleAtFixedRate(counterTask, 1000, 1000);
    }

    void shutdown() {
        timer.cancel();
    }

    public View handleRequest(final HttpServletRequest request, final HttpServletResponse resp) throws Exception {

        final String pi = request.getPathInfo();
        if ("/configure".equals(pi)) {
            return handleConfigure(request, resp);
        } else if ("/counter".equals(pi)) {
            return handleCounter(request, resp);
        }

        return handleRoot(request);

    }

    private View handleRoot(final HttpServletRequest request) {
        final ResponseTimeStats stats = getResponseTimeStats();
        final ResponseTimeStatistic root = stats.getRoot();

        final LinkedHashSet<ResponseTimeStatistic> set = new LinkedHashSet<ResponseTimeStatistic>();
        if (Boolean.parseBoolean(request.getParameter("all"))) {
            stats.getAll(set);
        } else {
            stats.getLast(set);
        }

        return new StatsView(set, root, stats.isOn());
    }

    private ResponseTimeStats getResponseTimeStats() {
        return ResponseTimeRequestListener.getResponseTimeStatistic(context);
    }

    private View handleCounter(final HttpServletRequest request, final HttpServletResponse resp) {
        final ResponseTimeStats stats = getResponseTimeStats();
        String accept = request.getHeader("Accept");
        if ("json".equalsIgnoreCase(request.getParameter("format"))
                || (accept != null && accept.contains("application/json"))) {
            return new CounterJsonView(stats.getRoot(), stats.isOn(), stats.getConcurrency(), this.counterHistory,
                    this.timeHistory);
        }
        if ("text".equalsIgnoreCase(request.getParameter("format"))
                || (accept != null && accept.contains("text/plain"))) {
            return new CounterTextView(stats.getRoot(), stats.isOn(), stats.getConcurrency(), this.counterHistory,
                    this.timeHistory);
        }

        return new CounterHtmlView(stats.getRoot(), stats.isOn(), stats.getConcurrency(), this.counterHistory,
                this.timeHistory);

    }

    private View handleConfigure(final HttpServletRequest request, final HttpServletResponse resp) {
        if (request.getSession(false) == null) {
            request.getSession(true);
            return selfRedirectView;
        }
        final ResponseTimeStats stats = getResponseTimeStats();
        if ("POST".equalsIgnoreCase(request.getMethod()) && checkValidSubmit(request)) {
            final boolean time = Boolean.parseBoolean(request.getParameter(TIME));
            final boolean block = Boolean.parseBoolean(request.getParameter(BLOCK));

            if (RESET.equals(request.getParameter(RESET2))) {
                log.debug(String.format("Reset(%s,%s)", time, block));
                if (stats.isOn()) {
                    stats.clean(time, block);
                    return new RedirectToStatsView();
                }
            } else if (TURN_ON.equals(request.getParameter(ON))) {
                log.debug(String.format("Turn On(%s,%s)", time, block));
                if (!stats.isOn()) {
                    stats.on(time, block);
                    return new RedirectToStatsView();
                }
            } else if (TURN_OFF.equals(request.getParameter(ON))) {
                log.debug("Turning stats off");
                if (stats.isOn()) {
                    stats.off();
                    return new RedirectToStatsView();
                }
            } else {
                return new View() {

                    @Override
                    public void render(final HttpServletRequest request, final HttpServletResponse response)
                            throws IOException {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }

                };
            }

        }
        return new ConfigView(stats);
    }

    private boolean checkValidSubmit(final HttpServletRequest request) {
        return (request.getParameter(RESET2) != null) || (request.getParameter(ON) != null);
    }

    private final class SelfRedirectView implements View {
        @Override
        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.sendRedirect(request.getRequestURL().toString());
        }
    }

    private final class RedirectToStatsView implements View {
        @Override
        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.sendRedirect(request.getRequestURI().replaceAll("/configure", ""));
        }
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

            final HttpSession session = request.getSession();
            //
            writer.print("<form method='POST'>");
            if ((session != null) && (session.getAttribute(_AUTHKEY) != null)) {
                writer.print("<input type=\"hidden\" name=\"" + _AUTHKEY + "\" value=\""
                        + session.getAttribute(_AUTHKEY) + "\" />");
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
            writer.print("</body></html>");
        }
    }

    private final class StatsView implements View {
        private final LinkedHashSet<ResponseTimeStatistic> set;
        private final ResponseTimeStatistic root;
        // NumberFormat decimalFormat = NumberFormat.getNumberInstance();
        NumberFormat nf = new DecimalFormat("#,##0");
        private final boolean on;

        private StatsView(final LinkedHashSet<ResponseTimeStatistic> set, final ResponseTimeStatistic root,
                final boolean on) {
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
            writer.print("<html><head><title>Performance Statistics for " + context.getServerInfo() + " at "
                    + request.getLocalName() + context.getContextPath() + "</title>");
            writer.print("<style type=\"text/css\">");
            writer.print("td,th,body { font-size: small; font-family: monospace; }");
            writer.print("td {text-align:right}");
            writer.print("td.name {text-align:left}");
            writer.print("</style>");
            writer.println("</head><body>");
            writer.print("<h1>Performance Statistics for " + context.getServerInfo() + " at " + request.getLocalName()
                    + context.getContextPath() + "</h1>");

            writer.print("<p><a href=\"" + request.getRequestURI() + "/configure\">Configuration</a>");
            if (on) {
                writer.print(" <a href=\"" + request.getRequestURI() + "/counter?interval=2\">Counters</a></p>");
            }
            writer.print("</p>");

            if (!on) {
                writer.print("<h3>Gathering of statistics is turned off</h3>");
            } else {
                writer.print("<table><thead>");
                writer.print("<tr>");
                writer.print("<td class=\"name\">name</td><td>count</td><td>average (μs)</td><td>min (μs)</td><td>max (μs)</td><td>total-system (μs)</td><td>total (μs)</td><td>block</td><td>wait</td>");
                writer.print("</tr>");
                writer.print("</thead><tbody>");
                write(writer, root);

                for (final ResponseTimeStatistic stat : set) {
                    write(writer, stat);

                }
                writer.print("</tbody>");
                writer.print("</table>");
            }
            writer.println("</body></html>");
        }

        protected void write(final PrintWriter writer, final ResponseTimeStatistic stat) throws IOException {
            writer.print("<tr>");
            writer.print("<td class=\"name\">");
            writer.print(stat.getName());
            writer.print("</td>");

            writer.print("<td  class=\"count\">");
            writer.print(Long.toString(stat.getCount()));
            writer.print("</td>");

            writer.print("<td class=\"avg\">");
            writer.print(nf.format(stat.getAverage()));
            writer.print("</td>");

            writer.print("<td class=\"min\">");
            writer.print(nf.format(stat.getMinTime()));
            writer.print("</td>");

            writer.print("<td class=\"max\">");
            writer.print(nf.format(stat.getMaxTime()));
            writer.print("</td>");

            writer.print("<td class=\"total-system\">");
            writer.print(nf.format(stat.getTotalSystemTime()));
            writer.print("</td>");

            writer.print("<td class=\"total\">");
            writer.print(nf.format(stat.getTotalTime()));
            writer.print("</td>");

            writer.print("<td class=\"block\">");
            writer.print(nf.format(stat.getBlockCount()));
            writer.print("</td>");

            writer.print("<td class=\"wait\">");
            writer.print(nf.format(stat.getWaitCount()));
            writer.print("</tr>");
        }
    }

    private AttributeListFilter stageSevenFilter = new AttributeListFilter() {
        public boolean filter(AttributeList attributes) {
            for (Attribute a : attributes.asList()) {
                if ("stage".equals(a.getName())) {
                    return !"7".equals(String.valueOf(a.getValue()));
                }

            }
            return true;
        }
    };
}
