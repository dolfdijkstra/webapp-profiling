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
package com.fatwire.gst.web.servlet.profiling.servlet.view;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistory;
import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistoryBigInteger;
import com.fatwire.gst.web.servlet.profiling.servlet.View;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.TextFormat;
import com.sun.management.UnixOperatingSystemMXBean;

abstract class BaseCounterView implements View {

    protected final NumberFormat rf = new DecimalFormat("#,##0");
    protected final NumberFormat decimalFormat = new DecimalFormat("0.0");
    protected final NumberFormat memf = new DecimalFormat("#,##0.000");

    protected final boolean on;

    protected final ResponseTimeStatistic root;
    protected final int concurrency;
    protected final CounterHistory counterHistory;
    protected final CounterHistoryBigInteger timeHistory;

    protected static final Log log = LogFactory.getLog(View.class);

    protected BaseCounterView(final ResponseTimeStatistic root, final boolean on, final int concurrency,
            final CounterHistory counterHistory, final CounterHistoryBigInteger timeHistory) {

        this.root = root;
        this.on = on;
        this.concurrency = concurrency;
        this.counterHistory = counterHistory;
        this.timeHistory = timeHistory;
    }

    String formatMemory(final long mem) {
        final StringBuilder writer = new StringBuilder();
        if (mem > (1024 * 1024 * 1024)) {
            writer.append(memf.format(mem / (1024L * 1024 * 1024)));
            writer.append(" GB");
        } else if (mem > (1024 * 1024)) {
            writer.append(memf.format(mem / (1024L * 1024)));
            writer.append(" MB");
        } else if (mem > 1024) {
            writer.append(memf.format(mem / 1024L));
            writer.append(" kB");
        } else {
            writer.append(memf.format(mem));
            writer.append(" B");

        }
        return writer.toString();

    }

    protected void sendRefresh(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        int interval = 2;
        if (request.getParameter("interval") != null) {
            try {
                interval = Integer.parseInt(request.getParameter("interval"));
            } catch (final NumberFormatException e) {
                log.debug("'" + request.getParameter("interval") + "' gave a  NumberFormatException");
            }

        }
        if (interval > 0) {
            response.setHeader("Refresh", Integer.toString(interval));
        }

    }

    protected void renderDocument(final TextFormat format) throws IOException {
        format.startDoc("Counters and Statistics");
        format.startSection("SystemStats");
        format.startSubSection("Memory");
        format.startTable();
        // format.tr("heap", formatMemory(heap));

        format.thr("name", "used", "committed");
        for (final MemoryType type : new MemoryType[] { MemoryType.HEAP, MemoryType.NON_HEAP }) {
            long used = 0;
            long committed = 0;
            for (final MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
                final MemoryUsage cu = bean.getUsage();
                if (cu != null) {
                    if (type == bean.getType()) {
                        format.tr(type.name() + " '" + bean.getName() + "'", formatMemory(cu.getUsed()),
                                formatMemory(cu.getCommitted()));
                        used += cu.getUsed();
                        committed += cu.getCommitted();
                    }
                }
            }
            format.tr("Total " + type.name(), formatMemory(used), formatMemory(committed));

        }
        format.endTable();
        format.endSubSection();
        format.startSubSection("GC");
        format.startTable();

        for (final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            format.tr(bean.getName(), Long.toString(bean.getCollectionCount()));

        }
        format.endTable();
        format.endSubSection();
        format.startSubSection("System");
        format.startTable();

        final double load = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        format.tr("System Load Average", decimalFormat.format(load));

        try {
            final OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
            if (os instanceof UnixOperatingSystemMXBean) {
                final UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean) os;
                format.tr("System Cpu Load", decimalFormat.format(unix.getSystemCpuLoad() * 100) + "%");
                format.tr("Process Cpu Load", decimalFormat.format(unix.getProcessCpuLoad() * 100) + "%");

                format.tr("Open FileDescriptor Count", rf.format(unix.getOpenFileDescriptorCount()));

                final long cvms = unix.getCommittedVirtualMemorySize();

                format.tr("Committed VirtualMemory Size", formatMemory(cvms));

            }

        } catch (final Throwable e) {

        }
        format.endTable();
        format.endSubSection();
        format.endSection();
        format.startSection("RequestCounters");
        if (on) {
            format.startSubSection("Requests");
            format.startTable();

            format.tr("Request Count", Long.toString(root.getCount()));

            format.tr("Concurrency", Integer.toString(this.concurrency));

            format.tr("Average Response Time", rf.format(root.getAverage()));
            format.endTable();
            format.endSubSection();

            format.startSubSection("History");
            format.startTable();

            final int[] distances = new int[] { 15 * 60, 5 * 60, 60, 30, 10, 5, 1 };
            final long[] history = this.counterHistory.getHistory(distances);
            final long[] time = this.timeHistory.getHistory(distances);

            String[] f = new String[distances.length + 1];
            f[0] = "pastSeconds";
            for (int i = 0; i < distances.length; i++) {
                f[i + 1] = Integer.toString(distances[i]);
            }
            format.thr(f);

            f = new String[distances.length + 1];
            f[0] = "TPS";
            for (int i = 0; i < distances.length; i++) {
                if (history[i] >= 0) {
                    final double tps = history[i] / (double) distances[i];
                    if (tps >= 1000.0) {
                        f[i + 1] = rf.format(tps);
                    } else {
                        f[i + 1] = decimalFormat.format(tps);
                    }
                }

            }
            format.tr(f);
            f = new String[distances.length + 1];
            f[0] = "RT";
            for (int i = 0; i < distances.length; i++) {
                if (history[i] != 0) {
                    final double avg = time[i] / ((double) history[i]);
                    f[i + 1] = rf.format(avg);
                } else {
                    f[i + 1] = rf.format(0);
                }
            }
            format.tr(f);
            format.endTable();
            format.endSubSection();
            format.endSection();

        }
        format.startSection("Tomcat");

        for (final MBeanServer mBeanServer : MBeanServerFactory.findMBeanServer(null)) {
            final BeanRenderer renderer = new BeanRenderer(format, mBeanServer);
            try {
                renderer.renderTable("ThreadPool", "Catalina:type=ThreadPool,*", new String[] { "minSpareThreads",
                        "currentThreadsBusy", "connectionCount", "currentThreadCount", "maxThreads" });
                renderer.renderTable("GlobalRequestProcessor", "Catalina:type=GlobalRequestProcessor,*", new String[] {
                        "requestCount", "bytesSent", "bytesReceived", "processingTime", "errorCount" });

            } catch (final MalformedObjectNameException e) {
                // ignored
                e.printStackTrace();
            }
        }
        format.endSection();
        // renderer.renderTable("Catalina:type=RequestProcessor,*", new
        // String[]{"stage","requestCount","errorCount","requestBytesSent","bytesSent","processingTime",
        // "requestProcessingTime", "lastRequestProcessingTime"},stageSevenFilter);
        if (!on) {
            format.msg("Gathering of statistics is turned off. Not all metrics are shown.");
        }

        format.endDoc();
    }
}
