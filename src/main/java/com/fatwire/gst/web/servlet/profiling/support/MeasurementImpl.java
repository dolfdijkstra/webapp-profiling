package com.fatwire.gst.web.servlet.profiling.support;

import com.fatwire.gst.web.servlet.profiling.Hierarchy;
import com.fatwire.gst.web.servlet.profiling.Measurement;

/**
 * @author Dolf.Dijkstra
 * 
 */
public final class MeasurementImpl implements Measurement {
    private final Hierarchy key;

    final long startNanoTime;

    final long startTime;

    long endTime;

    boolean running = true;

    MeasurementImpl(String key) {
        this(new Hierarchy(key));
    }

    public MeasurementImpl(Hierarchy name) {
        this.key = name;
        startNanoTime = System.nanoTime();

        startTime = System.currentTimeMillis();

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.Measurement#elapsed()
     */
    public long elapsed() {
        if (running)
            return System.nanoTime() - startNanoTime;
        return endTime - startNanoTime;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.Measurement#stop()
     */
    public void stop() {
        endTime = System.nanoTime();
        running = false;

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.Measurement#getName()
     */
    public Hierarchy getName() {
        return key;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.Measurement#getStartTime()
     */
    public long getStartTime() {
        return startTime;
    }
}