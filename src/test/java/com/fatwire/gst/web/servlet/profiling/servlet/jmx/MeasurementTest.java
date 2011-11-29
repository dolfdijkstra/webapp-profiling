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

package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MeasurementTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMeasure() throws InterruptedException {

        Measurement m = new Measurement();
        m.start();
        Object o = new Object();
        synchronized (o) {
            o.wait(500);
        }
        long j = 0;
        for (int i = 0; i < 6000000; i++) {
            j = j + (j * i);
        }
        m.stop();
        long t = m.getElapsedTime();
        System.out.println("getElapsedTime: " + t);
        long c = m.getElapsedCpuTime();
        System.out.println("getElapsedCpuTime: " + c);
        long u = m.getElapsedUserTime();
        System.out.println("getElapsedUserTime: " + u);
        long b = m.getBlockCountDelta();
        System.out.println("getBlockCountDelta: " + b);
        long w = m.getWaitCountDelta();
        System.out.println("getWaitCountDelta: " + w);

    }

    public void _testBlock() {
        Measurement m = new Measurement();
        final Object o = new Object();
        final Thread current = Thread.currentThread();
        Thread x = new Thread() {
            @Override
            public void run() {
                System.out.println("started");
                synchronized (o) {

                    System.out.println("in synchronized block");
                    System.out.println("state of starting thread " + current.getState());
                    System.out.println("notifyAll");
                    o.notifyAll();
                }
            }

        };
        m.start();

        synchronized (o) {
            System.out.println("starting");
            x.start();

            try {
                System.out.println("state of notifying thread " + x.getState());
                System.out.println("waiting");
                o.wait();
                System.out.println("done waiting");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        m.stop();

        try {
            x.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(0, m.getBlockCountDelta());
        Assert.assertEquals(1, m.getWaitCountDelta());
    }

    public void testMeasurementPerformance() {
        final int count = 100000;
        long start = System.nanoTime();
        for (int o = 0; o < count; o++) {
            System.nanoTime();
            System.nanoTime();
        }
        System.out.println("System.nanoTime() measurement: " + (System.nanoTime() - start) / count);
        start = System.nanoTime();
        for (int o = 0; o < count; o++) {
            System.currentTimeMillis();
            System.currentTimeMillis();
        }
        System.out.println("System.currentTimeMillis() measurement: " + (System.nanoTime() - start) / count);

        final boolean c[] = new boolean[] { true, false, true, false };
        final boolean t[] = new boolean[] { true, true, false, false };
        final AtomicInteger j = new AtomicInteger();
        for (int i = 0; i < 4; i++) {
            Thread x = new Thread() {
                @Override
                public void run() {
                    measurement(c[j.get()], t[j.get()], count);
                    j.incrementAndGet();
                }

            };
            x.start();
            try {
                x.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void measurement(boolean count, boolean time, int c) {
        Measurement m = new Measurement();
        m.start();
        Measurement t = new Measurement(time, count);

        for (int i = 0; i < c; i++) {
            t.start();
            t.stop();
        }
        m.stop();
        System.out.println("time: " + (time ? " true" : "false") + ", count: " + (count ? " true" : "false") + "\t" + c
                + " measurements took: " + Long.toString(m.getElapsedTime() / 1000) + " us, on average "
                + Double.toString(m.getElapsedTime() / c) + " ns");
        //System.out.println(m.toString());
    }

    public void testResolution() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int count = 0;
        long startTime = threadBean.getCurrentThreadCpuTime();
        long stopTime = threadBean.getCurrentThreadCpuTime();
        while (stopTime == startTime) {
            stopTime = threadBean.getCurrentThreadCpuTime();
            count++;
        }
        System.out.println("Resolution via CpuTime: " + (stopTime - startTime) + "ns in " + count + " iterations.");

    }

    public void testResolutionUser() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int count = 0;
        long startTime = threadBean.getCurrentThreadUserTime();
        long stopTime = threadBean.getCurrentThreadUserTime();
        while (stopTime == startTime) {
            stopTime = threadBean.getCurrentThreadUserTime();
            count++;
        }
        System.out.println("Resolution via UserTime: " + (stopTime - startTime) + "ns in " + count + " iterations.");

    }

}
