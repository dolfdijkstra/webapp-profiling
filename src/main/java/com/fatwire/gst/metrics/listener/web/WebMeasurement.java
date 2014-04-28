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
package com.fatwire.gst.metrics.listener.web;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;

public class WebMeasurement {

    /**
     * System.currentTimeMillis() at start
     */
    private final long startTime;
    /**
     * System.nanoTime() at start
     * 
     */
    private final long start;
    /**
     * Elapsed time in nanoseconds since the first measurement started, at the start of this StartEndMeasurement
     * 
     */

    private final long relativeStart;

    /**
     * System.nanoTime() at end, zero is the measurement has not ended.
     * 
     */

    private final long elapsed;
    private final String type;
    private final long id;
    private final int level;
    private final String msg;

    public WebMeasurement(StartEndMeasurement m) {
        this.id = m.getId();
        this.type = m.getType();
        this.level = m.getLevel();
        this.start = m.getStart();
        this.relativeStart = m.getRelativeStart();
        this.startTime = m.getStartTime();
        this.elapsed = m.getElapsed();

        this.msg = m.getMsg();
        

    }

    public WebMeasurement(Measurement m) {
        this.id = m.getId();
        this.type = m.getType();
        this.level = m.getLevel();
        this.start = m.getStart();
        this.relativeStart = m.getStart()-m.getLevelZeroStart();
        this.startTime = m.getStartTime();
        this.elapsed = m.getElapsed();
        this.msg = m.getMsg();


    }

    public long getElapsed() {
        return elapsed;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    public long getRelativeStart() {
        return relativeStart;
    }

}
