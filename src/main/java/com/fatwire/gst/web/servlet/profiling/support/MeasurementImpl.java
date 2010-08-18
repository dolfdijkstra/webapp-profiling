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