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
package com.fatwire.gst.metrics.listener.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.Metric;
import com.fatwire.gst.metrics.MetricListener;
import com.fatwire.gst.metrics.Switches;

public class FileListener implements MetricListener, Closeable {
    private static final String NAME = FileListener.class.getName();
    private final TimeUnit precision = TimeUnit.MICROSECONDS;
    private static final String precisionString = " μs";

    private final PrintWriter pw;

    public FileListener(final String filename) throws FileNotFoundException {
        final File f = new File(filename);
        f.getParentFile().mkdirs();
        this.pw = new PrintWriter(f);
    }

    @Override
    public void start(final Metric metric) {
        if (Switches.state(NAME)) {
            final StringBuilder b = new StringBuilder(256);
            b.append(formatDate(new Date()));
            b.append("\t");
            b.append(metric.getId());
            b.append("\t");
            b.append("starting\t");

            b.append(metric.getType());
            b.append("\t");
            b.append(metric.getLevel());
            b.append("\t");
            b.append(convert(metric.getStart() - metric.getLevelZeroStart()));
            b.append("\t");
            b.append(metric.msg());
            write(b.toString());
        }
    }

    private String formatDate(final Date d) {
        return df.get().format(d);
    }

    private final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected DateFormat initialValue() {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(tz);
            return df;
        }

    };

    @Override
    public void stop(final Metric metric) {
        if (Switches.state(NAME)) {
            final StringBuilder b = new StringBuilder(256);
            b.append(formatDate(new Date()));
            b.append("\t");
            b.append(metric.getId());
            b.append("\t");
            b.append("finished\t");
            b.append(metric.getType());
            b.append("\t");
            b.append(metric.getLevel());
            b.append("\t");
            b.append(convert((metric.getStart() + metric.elapsed()) - metric.getLevelZeroStart()));
            b.append("\t");
            b.append(metric.msg());
            b.append("\t");
            b.append(convert(metric.elapsed()));
            b.append("\t");
            b.append(precisionString);
            write(b.toString());

        }
    }

    @Override
    public boolean isActive() {
        return Switches.state(NAME);
    }

    @Override
    public void measurement(final Measurement me) {
        if (Switches.state(NAME)) {
            final StringBuilder b = new StringBuilder(256);
            b.append(formatDate(new Date()));
            b.append("\t");
            b.append(me.getId());
            b.append("\t");
            b.append("finished\t");
            b.append(me.getType());
            b.append("\t");
            b.append(me.getLevel());
            b.append("\t");
            b.append(convert(me.getEnd() - me.getLevelZeroStart()));
            b.append("\t");
            b.append(me.msg());
            b.append("\t");
            b.append(convert(me.elapsed()));
            b.append("\t");
            b.append(precisionString);
            write(b.toString());

        }

    }

    private void write(final String s) {
        if (!pw.checkError())
            pw.write(s);

    }

    private long convert(final long t) {
        return precision.convert(t, TimeUnit.NANOSECONDS);
    }

    @Override
    public void close() throws IOException {
        pw.close();

    }
}
