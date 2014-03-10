package com.fatwire.gst.web.servlet.profiling.logger;

public interface ParserCallback {
    void update(String type, String subType, long time);
}