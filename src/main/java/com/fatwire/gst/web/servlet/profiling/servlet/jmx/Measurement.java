/**
 * 
 */
package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * <p>Class to take a measurement. It is an advanced stopwatch. It records elapsed time, as well as cpu user time, thread block and wait counts that happend during the measurement period.</p>
 * 
 * 
 * <p>This class is not thread-safe. It should be used (by design) in only one thread.</p>
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

    private boolean meaureCpuTime = false;

    private boolean measureCount = false;

    private ThreadMXBean threadMXBean;

    private RunningState state = RunningState.STOPPED;

    /**
     * 
     * Manual override of the thread time and block counting
     * @param time
     * @param count
     */

    public Measurement(boolean time, boolean count) {
        threadMXBean = ManagementFactory.getThreadMXBean();
        if (time && threadMXBean.isCurrentThreadCpuTimeSupported()) {
            if (!threadMXBean.isThreadCpuTimeEnabled()) {
                threadMXBean.setThreadCpuTimeEnabled(true);
            }
            meaureCpuTime = threadMXBean.isCurrentThreadCpuTimeSupported();

        }
        if (count && threadMXBean.isThreadContentionMonitoringSupported()) {
            if (!threadMXBean.isThreadContentionMonitoringEnabled()) {
                threadMXBean.setThreadContentionMonitoringEnabled(true);
            }
            this.measureCount = threadMXBean
                    .isThreadContentionMonitoringEnabled();
        }

    }

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

        this.startTime = System.nanoTime();
        if (meaureCpuTime) {
            startUserTime = threadMXBean.getCurrentThreadUserTime();
            startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        }
        if (measureCount) {
            ThreadInfo info = threadMXBean.getThreadInfo(Thread.currentThread()
                    .getId());
            startBlockedCount = info.getBlockedCount();
            startWaitCount = info.getWaitedCount();
        }

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
        if (meaureCpuTime) {
            endUserTime = threadMXBean.getCurrentThreadUserTime();

            endCpuTime = threadMXBean.getCurrentThreadCpuTime();
            if (this.measureCount) {
                ThreadInfo info = threadMXBean.getThreadInfo(Thread
                        .currentThread().getId());
                endBlockedCount = info.getBlockedCount();
                endWaitCount = info.getWaitedCount();
            }
        }

    }

    /**
     * 
     * @return the number of nanoseconds elapsed between start and stop
     */
    public long getElapsedTime() {
        return endTime - startTime;
    }

    /**
     *  
     * @return the number of nanoseconds as reported by CpuTime elapsed between start and stop
     * @see ThreadMXBean#getCurrentThreadCpuTime()
     */

    public long getElapsedCpuTime() {

        return this.endCpuTime - this.startCpuTime;
    }

    /**
     *  
     * @return the number of nanoseconds as reported by UserTime elapsed between start and stop
     * @see ThreadMXBean#getCurrentThreadUserTime()
     */

    public long getElapsedUserTime() {
        return this.endUserTime - this.startUserTime;
    }

    /**
     * 
     * @return the number of block calls between start and stop
     * @see ThreadInfo#getBlockedCount()
     */
    public long getBlockCountDelta() {
        return this.endBlockedCount - this.startBlockedCount;
    }

    /**
     * 
     * @return the number of block calls between start and stop
     * @see ThreadInfo#getWaitedCount()
     */

    public long getWaitCountDelta() {
        return this.endWaitCount - this.startWaitCount;
    }

}