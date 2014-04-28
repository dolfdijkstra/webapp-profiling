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

/**
 * @author Dolf Dijkstra
 * 
 */
public class Measurement {
    private final long id;
    private final String type;

    private final int level;
    private final String msg;
    /**
     * System.nanoTime() at start
     */

    private final long start;

    /**
     * System.currentTimeMillis() at start
     */
    private final long startTime;
    /**
     * System.nanoTime() at end
     */

    private final long end;
    /**
     * elapsed time in nanoseconds
     * 
     */
    private final long elapsed;

    private final Object[] values;

    /**
     * System.nanoTime() at start of level zero (begin of request)
     */

    private final long levelZeroStart;

    public Measurement(final long id, final int level, final String type, long startTime, final long elapsed,
            final long levelZeroStart, final String msg, final Object... values) {
        this.id = id;
        this.level = level;
        this.type = type;
        this.startTime = startTime;
        this.elapsed = elapsed;

        this.end = System.nanoTime();
        long t = end - elapsed;
        //compensate for rounding errors
        this.start = t >= levelZeroStart ? t : levelZeroStart + 1;

        this.levelZeroStart = levelZeroStart;
        this.msg = msg;
        this.values = values;
    }

    /**
     * elapsed time in nanoseconds
     * 
     */
    public long getElapsed() {
        return elapsed;
    }

    /**
     * The message
     * 
     */

    public String getMsg() {
        return ((values == null) || (values.length == 0)) ? msg : String.format(msg, values);
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
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    /**
     * @return the levelZeroStart
     */
    public long getLevelZeroStart() {
        return levelZeroStart;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (elapsed ^ (elapsed >>> 32));
        result = prime * result + (int) (end ^ (end >>> 32));
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + level;
        result = prime * result + (int) (levelZeroStart ^ (levelZeroStart >>> 32));
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
        result = prime * result + (int) (start ^ (start >>> 32));
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Measurement other = (Measurement) obj;
        if (elapsed != other.elapsed) {
            return false;
        }
        if (end != other.end) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (level != other.level) {
            return false;
        }
        if (levelZeroStart != other.levelZeroStart) {
            return false;
        }
        if (msg == null) {
            if (other.msg != null) {
                return false;
            }
        } else if (!msg.equals(other.msg)) {
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Measurement [id=" + id + ", type=" + type + ", level=" + level + ", levelZeroStart=" + levelZeroStart
                + ", start=" + start + ", startTime=" + startTime + ", end=" + end + ", elapsed=" + elapsed + ", msg="
                + getMsg() + "]";
    }

}
