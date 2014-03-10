/*
 * Copyright 2013 Dolf Dijkstra. All Rights Reserved.
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
package com.fatwire.gst.metrics;

public class Metric {
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
     * Elapsed time in nanoseconds since the first measurement started, at the start of this Metric 
     * 
     */
    
    private final long sinceStart;
    
    /**
     * System.nanoTime() at end, zero is the measurement has not ended.
     * 
     */
    
    private long end;
    private final String type;
    private final long id;
    private final int level;
    private final String msg;
    private final Object[] values;

    public Metric(final long id, final int level, final String type, final long requestStart, final String msg,
            final Object... values) {
        super();
        this.id = id;
        this.level = level;
        this.start = System.nanoTime();
        this.sinceStart = requestStart == 0 ? start : requestStart;
        this.startTime = System.currentTimeMillis();
        this.type = type;
        this.msg = msg;
        this.values = values;
    }

    public long elapsed() {
        return end == 0 ? -1 : end - start;
    }

    public String msg() {
        return ((values == null) || (values.length == 0)) ? msg : String.format(msg, values);
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

    /**
     * @param end the end to set
     */
    public void setEnd(final long end) {
        this.end = end;
    }

    public long getLevelZeroStart() {
        return this.sinceStart;
    }

    public long getRelativeStart() {
        return start - this.sinceStart;
    }

}
