package com.fatwire.gst.metrics;

public class ThreadLocalMetricsHolder {
    private ThreadLocalMetricsHolder() {
    }

    private static final ThreadLocal<Metrics> tl = new ThreadLocal<Metrics>();

    public static Metrics get() {
        return tl.get();

    }

    public static void set(final Metrics metrics) {
        if (metrics != null && tl.get() != null)
            throw new IllegalStateException("The threadlocal object for the metrics in holding a value.");
        tl.set(metrics);

    }

}
