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
package com.fatwire.gst.metrics.listener;

import org.junit.Test;

import com.fatwire.gst.metrics.StartEndMeasurement;
import com.fatwire.gst.metrics.listener.udp.MetricProtocol;

public class MetricProtocolTest {

    @Test
    public void test_to_end_byte() {
        final StartEndMeasurement m = new StartEndMeasurement(12345678912345L, 0, "foo", 0, "/url");
        m.setEnd(m.getStart() + 20);
        final byte[] b = new MetricProtocol(1432).toEndByte(m);
        // System.out.println(toHex(b));

    }

    @Test
    public void test_to_end_byte_long_msg() {
        final StartEndMeasurement m = new StartEndMeasurement(12345678912345L, 0, "foo", 0,
                "this is a very long message. Don't cut this off at an inappropriate place");
        m.setEnd(m.getStart() + 20);
        final byte[] b = new MetricProtocol(80).toEndByte(m);
        // System.out.println(toHex(b));

    }

    protected String toHex(final byte[] b) {
        final StringBuilder builder = new StringBuilder(b.length * 3);
        for (final byte a : b) {
            builder.append(String.format("%02X", a)).append(" ");
        }
        return builder.toString();
    }

}
