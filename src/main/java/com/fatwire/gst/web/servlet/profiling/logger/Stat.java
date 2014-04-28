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
/**
 * 
 */
package com.fatwire.gst.web.servlet.profiling.logger;

import java.math.BigDecimal;

import javax.management.ObjectName;

public class Stat implements StatMBean {
    private String type;

    private String subType;

    private long min = Long.MAX_VALUE;

    private long max = Long.MIN_VALUE;

    private int count = 0;

    private BigDecimal total = BigDecimal.valueOf(0);
    private ObjectName name;

    public Stat() {
    };

    synchronized void update(final long t) {
        count++;
        total = total.add(BigDecimal.valueOf(t));
        min = Math.min(min, t);
        max = Math.max(max, t);

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#getType()
     */
    @Override
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#getSubType()
     */
    @Override
    public String getSubType() {
        return subType;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#getMin()
     */
    @Override
    public long getMin() {
        return count == 0 ? 0 : min;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#getMax()
     */
    @Override
    public long getMax() {
        return count == 0 ? 0 : max;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#getCount()
     */
    @Override
    public int getCount() {
        return count;
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#reset()
     */
    @Override
    public void reset() {
        min = Long.MAX_VALUE;
        max = Long.MIN_VALUE;
        count = 0;
        total = BigDecimal.valueOf(0);
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.logger.StatMBean#getAverage()
     */
    @Override
    public double getAverage() {
        if (count == 0) {
            return Double.NaN;
        }
        return total.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public ObjectName getName() {
        return name;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * @param subType the subType to set
     */
    public void setSubType(final String subType) {
        this.subType = subType;
    }

    /**
     * @param name the name to set
     */
    public void setName(final ObjectName name) {
        this.name = name;
    }
}
