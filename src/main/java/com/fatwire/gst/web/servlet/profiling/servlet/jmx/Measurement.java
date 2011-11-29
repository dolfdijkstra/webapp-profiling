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

/**
 * 
 */
package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * <p>
 * Class to take a measurement. It is an advanced stopwatch. It records elapsed
 * time, as well as cpu user time, thread block and wait counts that happend
 * during the measurement period.
 * </p>
 * 
 * 
 * <p>
 * This class is not thread-safe. It should be used (by design) in only one
 * thread.
 * </p>
 * 
 * @author Dolf.Dijkstra
 * 
 */
public class Measurement {

    private enum RunningState {
        STARTED, STOPPED
    };

    private long startTime;

    private long startUserTime;

    private long startCpuTime;

    private long startBlockedCount;

    private long startWaitCount;

    private long endTime;

    private long endUserTime;

    private long endCpuTime;

    private long endBlockedCount;

    private long endWaitCount;

    private boolean measureCpuTime = false;

    private boolean measureCount = false;

    private ThreadMXBean threadMXBean;

    private RunningState state = RunningState.STOPPED;

    /**
     * 
     * Manual override of the thread time and block counting
     * 
     * @param time {@link ThreadMXBean#setThreadCpuTimeEnabled(boolean)}
     * @param count {@link ThreadMXBean#setThreadContentionMonitoringEnabled(boolean)}
     */

    public Measurement(boolean time, boolean count) {
        threadMXBean = ManagementFactory.getThreadMXBean();
        if (time && threadMXBean.isCurrentThreadCpuTimeSupported()) {
            if (!threadMXBean.isThreadCpuTimeEnabled()) {
                threadMXBean.setThreadCpuTimeEnabled(true);
            }
            measureCpuTime = threadMXBean.isCurrentThreadCpuTimeSupported();

        }
        if (count && threadMXBean.isThreadContentionMonitoringSupported()) {
            if (!threadMXBean.isThreadContentionMonitoringEnabled()) {
                threadMXBean.setThreadContentionMonitoringEnabled(true);
            }
            measureCount = threadMXBean.isThreadContentionMonitoringEnabled();
        }

    }

    /**
     * Default constructor with time=true and count=true)
     */
    public Measurement() {
        this(true, true);

    }

    /**
     * To start a measurement
     * 
     */
    void start() {
        if (state == RunningState.STARTED)
            throw new IllegalStateException("Measurement already started");

        state = RunningState.STARTED;
        if (measureCpuTime) {
            startUserTime = threadMXBean.getCurrentThreadUserTime();
            startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        }
        if (measureCount) {
            ThreadInfo info = threadMXBean.getThreadInfo(Thread.currentThread().getId());
            startBlockedCount = info.getBlockedCount();
            startWaitCount = info.getWaitedCount();
        }
        this.startTime = System.nanoTime();

    }

    /**
     * To stop a started Measurement
     * 
     */
    void stop() {
        if (state == RunningState.STOPPED)
            throw new IllegalStateException("Measurement not running");
        state = RunningState.STOPPED;
        this.endTime = System.nanoTime();
        if (measureCpuTime) {
            endUserTime = threadMXBean.getCurrentThreadUserTime();
            endCpuTime = threadMXBean.getCurrentThreadCpuTime();
        }
        if (measureCount) {
            ThreadInfo info = threadMXBean.getThreadInfo(Thread.currentThread().getId());
            endBlockedCount = info.getBlockedCount();
            endWaitCount = info.getWaitedCount();
            
        }

    }

    /**
     * 
     * @return the number of nanoseconds elapsed between start and stop
     */
    public long getElapsedTime() {
        if (state == RunningState.STARTED)
            throw new IllegalStateException("Illegal when measurement is taken.");
        return endTime - startTime;
    }

    /**
     * 
     * @return the number of nanoseconds as reported by CpuTime elapsed between
     *         start and stop
     * @see ThreadMXBean#getCurrentThreadCpuTime()
     */

    public long getElapsedCpuTime() {
        if (state == RunningState.STARTED)
            throw new IllegalStateException("Illegal when measurement is taken.");

        return this.endCpuTime - this.startCpuTime;
    }

    /**
     * 
     * @return the number of nanoseconds as reported by UserTime elapsed between
     *         start and stop
     * @see ThreadMXBean#getCurrentThreadUserTime()
     */

    public long getElapsedUserTime() {
        if (state == RunningState.STARTED)
            throw new IllegalStateException("Illegal when measurement is taken.");
        return this.endUserTime - this.startUserTime;
    }

    /**
     * 
     * @return the number of block calls between start and stop
     * @see ThreadInfo#getBlockedCount()
     */
    public long getBlockCountDelta() {
        if (state == RunningState.STARTED)
            throw new IllegalStateException("Illegal when measurement is taken.");
        return this.endBlockedCount - this.startBlockedCount;
    }

    /**
     * 
     * @return the number of block calls between start and stop
     * @see ThreadInfo#getWaitedCount()
     */

    public long getWaitCountDelta() {
        if (state == RunningState.STARTED)
            throw new IllegalStateException("Illegal when measurement is taken.");
        return this.endWaitCount - this.startWaitCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Measurement [measureCpuTime=" + measureCpuTime + ", measureCount=" + measureCount
                + ", getElapsedTime()=" + getElapsedTime() + ", getElapsedCpuTime()=" + getElapsedCpuTime()
                + ", getElapsedUserTime()=" + getElapsedUserTime() + ", getBlockCountDelta()=" + getBlockCountDelta()
                + ", getWaitCountDelta()=" + getWaitCountDelta() + "]";
    }

}
