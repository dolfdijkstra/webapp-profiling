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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistory;
import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistoryBigInteger;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.PlainTextFormat;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.TextFormat;

public final class CounterTextView extends BaseCounterView {

    public CounterTextView(final ResponseTimeStatistic root, final boolean on, final int concurrency,
            final CounterHistory counterHistory, final CounterHistoryBigInteger timeHistory) {
        super(root, on, concurrency, counterHistory, timeHistory);
    }

    @Override
    public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=\"UTF-8\"");
        response.setCharacterEncoding("UTF-8");
        sendRefresh(request, response);
        final TextFormat html = new PlainTextFormat(response.getWriter());
        renderDocument(html);

    }

    /*
            public void renderX(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
                response.setContentType("text/plain; charset=\"UTF-8\"");
                response.setCharacterEncoding("UTF-8");
                int interval = 2;
                if (request.getParameter("interval") != null) {
                    try {
                        interval = Integer.parseInt(request.getParameter("interval"));
                    } catch (NumberFormatException e) {
                        log.debug("'" + request.getParameter("interval") + "' gave a  NumberFormatException");
                    }

                }
                if (interval > 0)
                    response.setHeader("Refresh", Integer.toString(interval));

                final PrintWriter writer = response.getWriter();
                if (!on) {
                    writer.print("Gathering of statistics is turned off\n\r");
                } else {
                    final long heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();

                    writer.print("heap:\t");
                    formatMemory(writer, heap);
                    writer.println();
                    for (MemoryType type : new MemoryType[] { MemoryType.HEAP, MemoryType.NON_HEAP }) {
                        for (MemoryPoolMXBean bean : ManagementFactory.getMemoryPoolMXBeans()) {
                            MemoryUsage cu = bean.getUsage();
                            if (cu != null) {
                                if (type == bean.getType()) {

                                    writer.print(bean.getType().name());
                                    writer.print(" '");
                                    writer.print(bean.getName());

                                    writer.print("':\t");
                                    long mem = cu.getUsed();

                                    formatMemory(writer, mem);
                                    writer.print("\t");
                                    formatMemory(writer, cu.getCommitted());
                                    writer.println();
                                }
                            }
                        }
                    }

                    writer.println();

                    for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
                        writer.print("GC '");
                        writer.print(bean.getName());

                        writer.print("':\t");
                        writer.println(bean.getCollectionCount());

                    }
                    writer.println();
                    final double load = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();

                    writer.print("System Load Average:\t");
                    writer.print(decimalFormat.format(load));
                    writer.println();

                    try {
                        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
                        if (os instanceof UnixOperatingSystemMXBean) {
                            UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean) os;

                            writer.print("System Cpu Load:\t");
                            writer.print(decimalFormat.format(unix.getSystemCpuLoad() * 100));
                            writer.println("%");
                            writer.print("Process Cpu Load:\t");
                            writer.print(decimalFormat.format(unix.getProcessCpuLoad() * 100));
                            writer.println("%");

                            writer.print("Open FileDescriptor Count:\t");
                            writer.print(rf.format(unix.getOpenFileDescriptorCount()));
                            writer.println();

                            long cvms = unix.getCommittedVirtualMemorySize();

                            writer.print("Committed VirtualMemory Size:\t");
                            formatMemory(writer, cvms);
                            writer.println();

                        }

                    } catch (Throwable e) {

                    }
                    writer.println();
                    writer.print("Request Count:\t");
                    writer.print(Long.toString(root.getCount()));
                    writer.println();
                    writer.print("Concurrency:\t");
                    writer.print(Integer.toString(stats.getConcurrency()));
                    writer.println();
                    writer.println();
                    writer.print("Average Response Time:\t");
                    writer.print(rf.format(root.getAverage()));
                    writer.println();
                    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

                    BeanRenderer renderer = new BeanRenderer(writer, mBeanServer);
                    try {
                        renderer.renderTable("Catalina:type=ThreadPool,*", new String[] { "minSpareThreads",
                                "currentThreadsBusy", "connectionCount", "currentThreadCount", "maxThreads" });
                        renderer.renderTable("Catalina:type=GlobalRequestProcessor,*", new String[] { "requestCount",
                                "bytesSent", "bytesReceived", "processingTime", "errorCount" });

                    } catch (MalformedObjectNameException e) {
                        // ignored
                        e.printStackTrace();
                    }
                    // renderer.renderTable("Catalina:type=RequestProcessor,*", new
                    // String[]{"stage","requestCount","errorCount","requestBytesSent","bytesSent","processingTime",
                    // "requestProcessingTime", "lastRequestProcessingTime"},stageSevenFilter);

                    writer.println("History:");

                    final int[] distances = new int[] { 15 * 60, 5 * 60, 60, 30, 10, 5, 1 };
                    final long[] history = counterHistory.getHistory(distances);
                    final long[] time = timeHistory.getHistory(distances);

                    for (int i = 0; i < distances.length; i++) {
                        writer.print(Integer.toString(distances[i]));
                        writer.print("\t");
                    }

                    writer.println();
                    for (int i = 0; i < distances.length; i++) {
                        if (history[i] >= 0) {
                            final double tps = history[i] / (double) distances[i];
                            writer.print(decimalFormat.format(tps));
                            writer.print("\t");
                        }

                    }
                    writer.println();
                    for (int i = 0; i < distances.length; i++) {
                        if (history[i] != 0) {
                            final double avg = time[i] / ((double) history[i]);
                            writer.print(rf.format(avg));
                        } else {
                            writer.print(rf.format(0));
                        }
                        writer.print("\t");

                    }

                    writer.println();
                }
            }

            void formatMemory(PrintWriter writer, long mem) {
                if (mem > (1024 * 1024 * 1024)) {
                    writer.print(memf.format(mem / (1024L * 1024 * 1024)));
                    writer.print(" GB");
                } else if (mem > (1024 * 1024)) {
                    writer.print(memf.format(mem / (1024L * 1024)));
                    writer.print(" MB");
                } else if (mem > 1024) {
                    writer.print(memf.format(mem / 1024L));
                    writer.print(" kB");
                } else {
                    writer.print(memf.format(mem));
                    writer.print(" B");

                }

            }
      */
}
