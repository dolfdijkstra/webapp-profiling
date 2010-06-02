package com.fatwire.gst.web.servlet.profiling;

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
     * elapsed time in nanoseconds since start or elepsed time between creation and stop()
     */

    long elapsed();

}
