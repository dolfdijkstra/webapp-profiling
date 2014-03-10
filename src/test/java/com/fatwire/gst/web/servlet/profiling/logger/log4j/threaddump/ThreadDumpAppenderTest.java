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
package com.fatwire.gst.web.servlet.profiling.logger.log4j.threaddump;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;

public class ThreadDumpAppenderTest extends TestCase {

    public void testSubAppendLoggingEvent() {
        String msg = "Execute page OpenMarket/Xcelerate/Search/Event Hours: 0 Minutes: 0 Seconds: 0:008";
        ThreadDumpAppender app = new ThreadDumpAppender();
        app.setPattern("Execute page.*");
        StringWriter writer = new StringWriter();
        app.setWriter(writer);
        app.setLayout(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
        LoggingEvent event = new LoggingEvent("foo", new Logger("foo") {
        }, Level.INFO, msg, null);
        app.doAppend(event);
        Assert.assertTrue(writer.toString().length() > 0);
    }

}
