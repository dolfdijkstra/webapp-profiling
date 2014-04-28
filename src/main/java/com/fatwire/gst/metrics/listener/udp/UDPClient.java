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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;

public class UDPClient {

    private static Log LOG = LogFactory.getLog(UDPClient.class);

    private final int port;

    private final DatagramSocket socket;
    private final InetAddress address;
    private final AtomicLong s = new AtomicLong();
    private final MetricProtocol protocol;
    /*
     *  1500 MTU - 60 IP hdr - 8 UDP hdr = 1432 bytes
     *   576 MTU - 60 IP hdr - 8 UDP hdr = 508 bytes: number of bytes that FOR SURE will not cause fragmentation
     *   
     *   576 is smallest MTU possible
     *   1500 is default MTU on most network cards 
     *    
     */
    private final static int MAX_MESSAGE_SIZE = 1432;

    public UDPClient(final String client_ip, final String server_ip, final int port, final int ttl) throws IOException {
        LOG.info(String.format("creating UDPClient with arguments client_ip: %s, server_ip: %s, port %d, ttl%d",
                client_ip, server_ip, port, ttl));
        this.port = port;
        address = InetAddress.getByName(server_ip);

        final InetAddress local = (client_ip == null) || (client_ip.length() == 0) ? null : InetAddress
                .getByName(client_ip);

        if (address.isMulticastAddress()) {

            final MulticastSocket s = new MulticastSocket(new InetSocketAddress(local, 0));
            s.setTimeToLive(ttl);
            this.socket = s;
        } else {
            this.socket = new DatagramSocket(new InetSocketAddress(local, 0));
        }
        if ((local != null) && !local.isAnyLocalAddress()) {

            final NetworkInterface intf = NetworkInterface.getByInetAddress(local);
            final int mtu = intf.getMTU();
            protocol = new MetricProtocol(mtu - 60 - 8);
        } else {
            protocol = new MetricProtocol(MAX_MESSAGE_SIZE);
        }
    }

    public void sendStart(final StartEndMeasurement metric) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("start %d %d %s", metric.getId(), metric.getLevel(), metric.getMsg()));
        }

        final byte[] b = protocol.toStartByte(metric);
        send(b);
    }

    public void sendEvent(final Measurement m) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("event %d %d %d %s", m.getId(), m.getLevel(), m.getElapsed(), m.getMsg()));
        }

        final byte[] b = protocol.toEventByte(m);
        send(b);
    }

    public void sendEnd(final StartEndMeasurement metric) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace(String.format("end %d %d %d %s", metric.getId(), metric.getLevel(), metric.getElapsed(),
                    metric.getMsg()));
        }
        final byte[] b = protocol.toEndByte(metric);
        send(b);
    }

    private void send(final byte[] b) throws IOException {
        final DatagramPacket sendPacket = new DatagramPacket(b, b.length, address, port);
        socket.send(sendPacket);
        s.incrementAndGet();
    }

    /**
     * @return the s
     */
    public long getNumSendMessages() {
        return s.get();
    }

    public void close() throws IOException {
        socket.close();
    }

}
