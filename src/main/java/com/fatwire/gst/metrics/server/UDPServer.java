package com.fatwire.gst.metrics.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class UDPServer implements Runnable {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private int port;

    private InetAddress address;
    private OutputStream out;

    public UDPServer(String ip, int port) throws UnknownHostException, IOException {
        this.port = port;
        address = InetAddress.getByName(ip);
        out = new FileOutputStream("/tmp/metrics");

    }

    public void run() {
        MulticastSocket socket = null;

        try {
            // socket = new DatagramSocket(new InetSocketAddress(address,
            // port));
            socket = new MulticastSocket(port);

            socket.joinGroup(address);

            byte[] receiveData = new byte[64 * 1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                int len = receivePacket.getLength();
                out.write(receiveData, receivePacket.getOffset(), len);
                // System.out.println(String.format("offset: %d",
                // receivePacket.getOffset()));
                // System.out.println(String.format("len: %d", len));

                // System.out.println(toHex(receiveData,
                // receivePacket.getOffset(), len));
                ByteBuffer bb = ByteBuffer.wrap(receiveData, receivePacket.getOffset(), len);

                byte msgType = bb.get();
                // System.out.println(String.format("msgType: %d", msgType));

                long count = bb.getLong();
                // System.out.println(String.format("count: %d", count));
                int level = bb.getInt();
                // System.out.println(String.format("level: %d", level));
                switch (msgType) {
                    case 0: {
                        long startTime = bb.getLong();
                        // System.out.println(String.format("startTime: %d",
                        // startTime));
                        int typeLength = bb.getInt();
                        // System.out.println(String.format("typeLength: %d",
                        // typeLength));
                        byte[] t = new byte[typeLength];
                        bb.get(t);
                        String type = new String(t, UTF_8);
                        int msgLength = bb.getInt();
                        t = new byte[msgLength];
                        bb.get(t);
                        String msg = new String(t, UTF_8);
                        System.out.println(String.format("%d %d %d %s  %s", count, level, startTime, type, msg));
                    }
                        break;
                    case 1: {
                        long elapsed = bb.getLong();
                        // System.out.println(String.format("elapsed: %d",
                        // elapsed));
                        int typeLength = bb.getInt();
                        // System.out.println(String.format("typeLength: %d",
                        // typeLength));
                        byte[] t = new byte[typeLength];
                        bb.get(t);
                        String type = new String(t, UTF_8);
                        int msgLength = bb.getInt();
                        t = new byte[msgLength];
                        bb.get(t);
                        String msg = new String(t, UTF_8);
                        System.out.println(String.format("%d %d %d %s  %s", count, level, elapsed, type, msg));

                    }
                        break;
                    case 2: {
                        long startTime = bb.getLong();
                        // System.out.println(String.format("startTime: %d",
                        // startTime));

                        long elapsed = bb.getLong();
                        // System.out.println(String.format("elapsed: %d",
                        // elapsed));
                        int typeLength = bb.getInt();
                        // System.out.println(String.format("typeLength: %d",
                        // typeLength));
                        byte[] t = new byte[typeLength];
                        bb.get(t);
                        String type = new String(t, UTF_8);
                        int msgLength = bb.getInt();
                        t = new byte[msgLength];
                        bb.get(t);
                        String msg = new String(t, UTF_8);
                        System.out.println(String.format("%d %d %d %d  %s  %s", count, level, startTime, elapsed, type,
                                msg));
                    }
                        break;

                }

            }
        } catch (SocketTimeoutException e) {
            System.out.println(e.getMessage());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.leaveGroup(address);
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket.close();
        }
    }

    public void close() {
        try {
            if (out != null)
                out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     * @throws UnknownHostException
     * @throws NumberFormatException
     */
    public static void main(String[] args) throws Exception {
        final UDPServer server = new UDPServer(args[0], Integer.parseInt(args[1]));
        final Thread main = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread("shutdown") {

            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                main.interrupt();
                server.close();
            }

        });
        server.run();

    }

    protected String toHex(byte[] b, int offset, int len) {
        StringBuilder builder = new StringBuilder(b.length * 3);
        for (int i = offset; i < len; i++) {
            builder.append(String.format("%02X", b[i])).append(" ");
        }
        return builder.toString();
    }

}
