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
package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

public class ResponseTimeStatistic implements ResponseTimeStatisticMBean {

    private final String name;

    private final AtomicLong counter = new AtomicLong();

    private final AtomicLong blockCounter = new AtomicLong();

    private final AtomicLong waitCounter = new AtomicLong();

    private long minTime = Long.MAX_VALUE;

    private long maxTime = 0;

    private volatile BigInteger total = BigInteger.ZERO;

    private volatile BigInteger systemTotal = BigInteger.ZERO;

    public ResponseTimeStatistic(final String name) {
        if ((name == null) || (name.length() == 0)) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;
    }

    @Override
    public long getCount() {
        return counter.get();
    }

    /**
     * total processing time in micro seconds
     * 
     */
    @Override
    public BigInteger getTotalTime() {
        return total;
    }

    @Override
    public double getAverage() {
        final long n = counter.get();
        if (n == 0) {
            return 0;
        }
        return (new BigDecimal(total).divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP).doubleValue());

    }

    public void signal(final Measurement m) {
        counter.incrementAndGet();
        this.blockCounter.addAndGet(m.getBlockCountDelta());
        this.waitCounter.addAndGet(m.getWaitCountDelta());

        if (m.getElapsedUserTime() > 0) {
            final long user = m.getElapsedUserTime();
            final long cpu = m.getElapsedCpuTime();
            final long system = cpu - user;
            if (system > 0) {
                this.systemTotal = this.systemTotal.add(BigInteger.valueOf(system));
            }

        }

        final long t = m.getElapsedTime() / 1000;

        total = total.add(BigInteger.valueOf(t));
        minTime = Math.min(minTime, t);
        maxTime = Math.max(maxTime, t);

    }

    /**
     * @return the minTime
     */
    @Override
    public long getMinTime() {

        return getCount() == 0 ? 0 : minTime;
    }

    /**
     * @return the maxTime
     */
    @Override
    public long getMaxTime() {
        return getCount() == 0 ? 0 : maxTime;
    }

    @Override
    public long getBlockCount() {
        return this.blockCounter.get();
    }

    @Override
    public long getWaitCount() {
        return this.waitCounter.get();
    }

    @Override
    public BigInteger getTotalSystemTime() {
        return this.systemTotal;
    }

    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
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
        final ResponseTimeStatistic other = (ResponseTimeStatistic) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    public void reset() {
        counter.set(0);

        blockCounter.set(0);

        waitCounter.set(0);

        minTime = Long.MAX_VALUE;

        maxTime = 0;

        total = BigInteger.ZERO;

        systemTotal = BigInteger.ZERO;

    }

}
