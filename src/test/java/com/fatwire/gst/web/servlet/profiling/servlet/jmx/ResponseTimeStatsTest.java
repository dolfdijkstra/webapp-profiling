package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResponseTimeStatsTest {

    @Test
    public void testOn() {
        ResponseTimeStats s = new ResponseTimeStats("foo", "/p", false, false, false, false);
        Measurement m = s.startMeasurement();
        assertNull(m);
        s.on(false, false);
        m = s.startMeasurement();
        assertNotNull(m);
        assertEquals(0, m.getGeneration());

        s.finishMeasurement(null);
        assertEquals(1,s.getRoot().getCount());
        

    }

}
