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

import java.math.BigInteger;

public interface ResponseTimeStatisticMBean {

    /**
     * 
     * @return the number of values in this statistic
     */
    long getCount();

    /**
     * 
     * @return the total time of the values in this statistic
     */
    BigInteger getTotalTime();

    /**
     * 
     * 
     * @return the average of the values in this statistic
     */
    double getAverage();

    /**
     * @return the minTime
     */
    long getMinTime();

    /**
     * @return the maxTime
     */
    long getMaxTime();

    /**
     * 
     * @return the total number of time a thread has been blocked during the execution of this request
     * @see java.lang.management.ThreadInfo#getBlockedCount()
     */
    public long getBlockCount();

    /**
     * 
     * @return the total number of time a thread has been waiting during the execution of this request
     * @see java.lang.management.ThreadInfo#getWaitedCount()
     */

    public long getWaitCount();

    /**
     * 
     * @return the total time a thread has spend in system mode
     * @see java.lang.management.ThreadMXBean#getCurrentThreadCpuTime()
     * @see java.lang.management.ThreadMXBean#getCurrentThreadUserTime()
     */

    public BigInteger getTotalSystemTime();

}
