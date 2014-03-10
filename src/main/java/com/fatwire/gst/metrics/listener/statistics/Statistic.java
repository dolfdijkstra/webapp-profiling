package com.fatwire.gst.metrics.listener.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistic {

    private final String name;

    private final AtomicInteger counter = new AtomicInteger();

    private long minTime = Long.MAX_VALUE;

    private long maxTime = 0;

    private volatile BigDecimal total = BigDecimal.ZERO;

    public Statistic(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("name cannot be null or empty");
        this.name = name;
    }

    public int getCount() {
        return counter.get();
    }

    /**
     * total processing time in micro seconds
     * 
     */
    public BigDecimal getTotalTime() {
        return total;
    }

    public double getAverage() {
        int n = counter.get();
        if (n == 0)
            return 0;
        return (total.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP).doubleValue());

    }

    void signal(long elapsed) {
        counter.incrementAndGet();

        long t = elapsed / 1000;

        total = total.add(BigDecimal.valueOf(t));
        minTime = Math.min(minTime, t);
        maxTime = Math.max(maxTime, t);

    }

    /**
     * @return the minTime
     */
    public long getMinTime() {

        return getCount() == 0 ? 0 : minTime;
    }

    /**
     * @return the maxTime
     */
    public long getMaxTime() {
        return getCount() == 0 ? 0 : maxTime;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Statistic other = (Statistic) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
