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

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fatwire.gst.metrics.StartEndMeasurement;
import com.fatwire.gst.metrics.listener.udp.UDPClient;

public class UDPClientTest {

    @Test
    public void test() {
        try {
            final UDPClient client = new UDPClient(null, "224.0.0.103", 9889, 0);

            final StartEndMeasurement m = new StartEndMeasurement(12345678912345L, 0, "foo", 0, "/url");
            m.setEnd(m.getStart() + 20);
            client.sendEnd(m);
            client.close();
        } catch (final IOException e) {

            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
