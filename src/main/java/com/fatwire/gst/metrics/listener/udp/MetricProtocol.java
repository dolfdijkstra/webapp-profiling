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
package com.fatwire.gst.metrics.listener.udp;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;

public class MetricProtocol {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final int int_len = Integer.SIZE / 8;
    private final int long_len = Long.SIZE / 8;
    private final int max_size;

    public MetricProtocol(final int maxMessageSize) {
        max_size = maxMessageSize;
    }

    public byte[] toEndByte(final StartEndMeasurement metric) {
        final String msg = metric.getMsg();

        byte[] bMsg;

        bMsg = msg.getBytes(UTF_8);
        final byte[] bType = metric.getType().getBytes(UTF_8);

        final int len = 1 + (int_len * 3) + (long_len * 3) + bMsg.length + bType.length;
        if (len > max_size) {
            final double max_msg = max_size - ((int_len * 3) + (long_len * 3) + bType.length);
            final String n = msg.substring(0, (int) ((max_msg / bMsg.length) * bMsg.length) - 3) + "...";
            bMsg = n.getBytes(UTF_8);
            if (bMsg.length > max_size) {
                System.err.println("still too long: " + bMsg.length + " " + n);

            }

        }
        final ByteBuffer bb = ByteBuffer.allocate(len);
        bb.put((byte) 2);
        bb.putLong(metric.getId());
        bb.putInt(metric.getLevel());

        bb.putLong(metric.getStartTime());
        bb.putLong(metric.getElapsed());
        bb.putInt(bType.length);
        bb.put(bType);
        bb.putInt(bMsg.length);
        bb.put(bMsg);

        final byte[] b = bb.array();

        return b;
    }

    public byte[] toStartByte(final StartEndMeasurement metric) {
        final String msg = metric.getMsg();

        byte[] bMsg;

        bMsg = msg.getBytes(UTF_8);
        final byte[] bType = metric.getType().getBytes(UTF_8);

        final int len = 1 + (int_len * 3) + (long_len * 3) + bMsg.length + bType.length;
        if (len > max_size) {
            final double max_msg = max_size - ((int_len * 3) + (long_len * 3) + bType.length);
            final String n = msg.substring(0, (int) ((max_msg / bMsg.length) * bMsg.length) - 3) + "...";
            bMsg = n.getBytes(UTF_8);
            if (bMsg.length > max_size) {
                System.err.println("still too long: " + bMsg.length + " " + n);

            }

        }
        final ByteBuffer bb = ByteBuffer.allocate(len);
        bb.put((byte) 0);
        bb.putLong(metric.getId());
        bb.putInt(metric.getLevel());
        bb.putLong(metric.getStartTime());
        bb.putInt(bType.length);
        bb.put(bType);
        bb.putInt(bMsg.length);
        bb.put(bMsg);

        final byte[] b = bb.array();

        return b;
    }

    public byte[] toEventByte(final Measurement m) {
        final String msg = m.getMsg();
        byte[] bMsg;

        bMsg = msg.getBytes(UTF_8);
        final byte[] bType = m.getType().getBytes(UTF_8);

        final int len = 1 + (int_len * 3) + (long_len * 3) + bMsg.length + bType.length;
        if (len > max_size) {
            final double max_msg = max_size - ((int_len * 3) + (long_len * 3) + bType.length);
            final String n = msg.substring(0, (int) ((max_msg / bMsg.length) * bMsg.length) - 3) + "...";
            bMsg = n.getBytes(UTF_8);
            if (bMsg.length > max_size) {
                System.err.println("still too long: " + bMsg.length + " " + n);

            }

        }
        final ByteBuffer bb = ByteBuffer.allocate(len);
        bb.put((byte) 1);
        bb.putLong(m.getId());
        bb.putInt(m.getLevel());
        bb.putLong(m.getElapsed());
        bb.putInt(bType.length);
        bb.put(bType);
        bb.putInt(bMsg.length);
        bb.put(bMsg);

        final byte[] b = bb.array();

        return b;

    }
}
