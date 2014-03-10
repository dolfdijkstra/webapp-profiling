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

public class Measurement {
    private final long elapsed;
    private final String type;
    private final long id;
    private final int level;
    private final String msg;
    private final long start;
    private final long end;
    private final Object[] values;
    private final long levelZeroStart;

    public Measurement(final long id, final int level, final String type, final String msg, final long elapsed,
            final long levelZeroStart, final Object... values) {
        this.id = id;
        this.level = level;
        this.type = type;
        this.msg = msg;
        this.elapsed = elapsed;
        this.values = values;
        this.end = System.nanoTime();
        this.start = end - elapsed;
        this.levelZeroStart = levelZeroStart;
    }

    public long elapsed() {
        return elapsed;
    }

    public String msg() {
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

}
