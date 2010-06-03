package com.fatwire.gst.web.servlet.profiling;

/**
 * Interface to take a measurement.
 * 
 * @author Dolf.Dijkstra
 * 
 */
public interface Measurement {

    /**
     * 
     * 
     * @return the Hierarchy name of this measurement
     */
    Hierarchy getName();

    /**
     * 
     * @return the time the measurement was started in epoch
     */
    long getStartTime();

    /**
     * stop the measurement
     */

    void stop();

    /**
     * elapsed time in nanoseconds since creation or elepsed time between creation and stop()
     */

    long elapsed();

}
