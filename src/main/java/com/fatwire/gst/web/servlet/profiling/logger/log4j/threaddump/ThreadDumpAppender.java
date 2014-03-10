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
package com.fatwire.gst.web.servlet.profiling.logger.log4j.threaddump;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.regex.Pattern;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

public class ThreadDumpAppender extends RollingFileAppender {
    private Pattern msgPattern;
    private static final char[] NEW_LINE = "\n".toCharArray();
    private int maxFrames = 12;

    @Override
    protected void subAppend(final LoggingEvent event) {
        if (msgPattern == null) {
            return;
        }
        final Object o = event.getMessage();
        if (o instanceof String) {
            final String s = (String) o;

            if (msgPattern.matcher(s).matches()) {
                final StringBuilder sb = new StringBuilder("Thread Dump because of pattern match on ").append(s)
                        .append(NEW_LINE);
                final ThreadInfo[] ti = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                for (final ThreadInfo t : ti) {
                    threadInfo(t, sb);
                }

                final LoggingEvent e = new LoggingEvent(event.getFQNOfLoggerClass(), event.getLogger(),
                        event.getTimeStamp(), event.getLevel(), sb.toString(), null);
                super.subAppend(e);
            }

        }

    }

    public void threadInfo(final ThreadInfo t, final StringBuilder sb) {
        sb.append("\"" + t.getThreadName() + "\"" + " Id=" + t.getThreadId() + " " + t.getThreadState());
        if (t.getLockName() != null) {
            sb.append(" on " + t.getLockName());
        }
        if (t.getLockOwnerName() != null) {
            sb.append(" owned by \"" + t.getLockOwnerName() + "\" Id=" + t.getLockOwnerId());
        }
        if (t.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (t.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        final StackTraceElement[] stackTrace = t.getStackTrace();

        int i = 0;

        for (; i < stackTrace.length && i < maxFrames; i++) {
            final StackTraceElement ste = stackTrace[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && t.getLockInfo() != null) {
                final Thread.State ts = t.getThreadState();
                switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + t.getLockInfo());
                        sb.append('\n');
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + t.getLockInfo());
                        sb.append('\n');
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  waiting on " + t.getLockInfo());
                        sb.append('\n');
                        break;
                    default:
                }
            }

            for (final MonitorInfo mi : t.getLockedMonitors()) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
        }
        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }

        final LockInfo[] locks = t.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (final LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }
        sb.append('\n');

    }

    public void setPattern(final String pattern) {
        this.msgPattern = Pattern.compile(pattern);
    }

    public String getPattern() {
        return msgPattern.pattern();
    }

    /**
     * @return the maxFrames
     */
    public int getMaxFrames() {
        return maxFrames;
    }

    /**
     * @param maxFrames the maxFrames to set
     */
    public void setMaxFrames(final int maxFrames) {
        this.maxFrames = maxFrames;
    }

}
