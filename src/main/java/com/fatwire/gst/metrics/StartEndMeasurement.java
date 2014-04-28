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
package com.fatwire.gst.metrics;

import java.util.Arrays;

public class StartEndMeasurement {
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
     * System.nanoTime() at start of level zero
     * 
     */

    private final long startAtLevelZero;

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

    public StartEndMeasurement(final long id, final int level, final String type, final long requestStart, final String msg,
            final Object... values) {
        super();
        this.id = id;
        this.level = level;
        this.start = System.nanoTime();
        this.startAtLevelZero = requestStart == 0 ? start : requestStart;
        this.startTime = System.currentTimeMillis();
        this.type = type;
        this.msg = msg;
        this.values = values;
    }

    /**
     * @return elapsed time in nanoseconds or zero when this metric has not finished
     */
    public long getElapsed() {
        return end == 0 ? -1 : end - start;
    }

    public String getMsg() {
        return ((values == null) || (values.length == 0)) ? msg : String.format(msg, values);
    }

    /**
     * @return the wall clock time in milliseconds at the time of start
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @return System.nanotime at the time this metric started
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
     * @return the request id
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

    /**
     * 
     * 

     * @return System.nanoTime() at start of level zero
     */
    public long getLevelZeroStart() {
        return this.startAtLevelZero;
    }

    /**
     * @return elapsed time in nanoseconds from level zero start till the start of this metric
     */
    public long getRelativeStart() {
        return start - this.startAtLevelZero;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "StartEndMeasurement [id=" + id + ", type=" + type + ", level=" + level + ", startTime=" + startTime + ", start="
                + start + ", startAtLevelZero=" + startAtLevelZero + ", end=" + end + ", msg=" + getMsg() + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + (int) (id ^ (id >>> 32));
        result = (prime * result) + level;
        result = (prime * result) + ((msg == null) ? 0 : msg.hashCode());
        result = (prime * result) + (int) (startAtLevelZero ^ (startAtLevelZero >>> 32));
        result = (prime * result) + (int) (start ^ (start >>> 32));
        result = (prime * result) + (int) (startTime ^ (startTime >>> 32));
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());
        result = (prime * result) + Arrays.hashCode(values);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StartEndMeasurement other = (StartEndMeasurement) obj;
        if (id != other.id) {
            return false;
        }
        if (level != other.level) {
            return false;
        }
        if (msg == null) {
            if (other.msg != null) {
                return false;
            }
        } else if (!msg.equals(other.msg)) {
            return false;
        }
        if (startAtLevelZero != other.startAtLevelZero) {
            return false;
        }
        if (start != other.start) {
            return false;
        }
        if (startTime != other.startTime) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (!Arrays.equals(values, other.values)) {
            return false;
        }
        return true;
    }

}
