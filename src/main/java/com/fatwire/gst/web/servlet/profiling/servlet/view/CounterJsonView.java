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
import java.io.PrintWriter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistory;
import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistoryBigInteger;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.JsonFormat;
import com.sun.management.UnixOperatingSystemMXBean;

public final class CounterJsonView extends BaseCounterView {

    private static final double KB = 1024;

    public CounterJsonView(final ResponseTimeStatistic root, final boolean on, final int concurrency,
            final CounterHistory counterHistory, final CounterHistoryBigInteger timeHistory) {
        super(root, on, concurrency, counterHistory, timeHistory);

    }

    @Override
    public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=\"UTF-8\"");
        response.setCharacterEncoding("UTF-8");
        int[] distances = new int[] { 15 * 60, 5 * 60, 60, 30, 10, 5, 1 };
        if (request.getParameter("distances") != null) {
            final String[] s = request.getParameter("distances").split(",");
            distances = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                distances[i] = Integer.parseInt(s[i]);
            }
        }

        final PrintWriter writer = response.getWriter();
        final JsonFormat format = new JsonFormat(writer);
        renderJson(format, distances);

    }

    public void renderJson(final JsonFormat format, final int[] distances) throws IOException {

        format.startDoc();
        if (!on) {
            format.nvp("status", "off");
        } else {
            format.nvp("status", "on");
            final long heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
            format.nvp("heap", formatMem(heap));

            format.startArray("memory");
            for (final MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
                final MemoryUsage cu = bean.getUsage();
                if (cu != null) {
                    format.startObject();
                    format.nvp("type", bean.getType().name());
                    format.nvp("name", bean.getName());
                    format.nvp("used", formatMem(cu.getUsed()));
                    format.nvp("committed", formatMem(cu.getCommitted()));
                    format.nvp("max", formatMem(cu.getMax()));
                    format.endObject();
                }
            }
            format.endArray();
            format.startArray("gc");

            for (final GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
                format.startObject();
                format.nvp("name", bean.getName());
                format.nvp("CollectionCount", bean.getCollectionCount());
                format.nvp("CollectionTime", bean.getCollectionTime());
                format.endObject();
            }
            format.endArray();
            final double load = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
            format.nvp("load", load);
            try {
                final OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
                if (os instanceof UnixOperatingSystemMXBean) {
                    final UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean) os;

                    format.nvp("SystemCpuLoad", round(unix.getSystemCpuLoad(), 4) * 100);

                    format.nvp("ProcessCpuLoad", round(unix.getProcessCpuLoad(), 4) * 100);

                    format.nvp("OpenFileDescriptorCount", unix.getOpenFileDescriptorCount());

                    final long cvms = unix.getCommittedVirtualMemorySize();

                    format.nvp("CommittedVirtualMemorySize", formatMem(cvms));

                }

            } catch (final Throwable e) {

            }
            format.nvp("count", root.getCount());
            format.nvp("concurrency", this.concurrency);
            format.nvp("average_response_time", root.getAverage());

            final long[] history = this.counterHistory.getHistory(distances);
            format.startObject("TPS");
            for (int i = 0; i < distances.length; i++) {
                format.nvp(Integer.toString(distances[i]) + "s", round(div(history[i], distances[i]), 2));
            }
            format.endObject();

            final long[] time = this.timeHistory.getHistory(distances);

            format.startObject("RT");
            for (int i = 0; i < distances.length; i++) {
                if (history[i] != 0) {
                    final double avg = time[i] / ((double) history[i]);
                    format.nvp(Integer.toString(distances[i]) + "s", (long) avg);
                } else {
                    format.nvp(Integer.toString(distances[i]) + "s", 0);
                }
            }
            format.endObject();

        }
        format.endDoc();
    }

    private long formatMem(final long heap) {
        // return memf.format(heap / (KB));
        return Math.round(heap / KB);
    }

    double div(final long h, final int l) {
        return l != 0 ? h / (double) l : 0;
    }

    double round(final double h, final int precision) {
        final double p = Math.pow(10.0, precision);
        return Math.round(h * p) / p;
    }
}
