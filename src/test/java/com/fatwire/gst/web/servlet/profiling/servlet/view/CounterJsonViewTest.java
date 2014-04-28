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
package com.fatwire.gst.web.servlet.profiling.servlet.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistory;
import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistoryBigInteger;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.JsonFormat;

public class CounterJsonViewTest {

    @Test
    public void testRenderJsonOff() throws IOException {
        final CounterHistory counterHistory = new CounterHistory(15 * 60);
        final CounterHistoryBigInteger timeHistory = new CounterHistoryBigInteger(15 * 60);

        final ResponseTimeStatistic root = new ResponseTimeStatistic("/cs");
        final CounterJsonView v = new CounterJsonView(root, false, 1, counterHistory, timeHistory);
        final StringWriter s = new StringWriter();
        final PrintWriter pw = new PrintWriter(s);
        final JsonFormat format = new JsonFormat(pw);
        v.renderJson(format, new int[] { 900, 60, 1 });
        System.out.println(s.toString());

    }

    @Test
    public void testRenderJsonOn() throws IOException {

        final CounterHistory counterHistory = new CounterHistory(15 * 60);
        final CounterHistoryBigInteger timeHistory = new CounterHistoryBigInteger(15 * 60);

        final ResponseTimeStatistic root = new ResponseTimeStatistic("/cs");
        final CounterJsonView v = new CounterJsonView(root, true, 1, counterHistory, timeHistory);
        final StringWriter s = new StringWriter();
        final PrintWriter pw = new PrintWriter(s);
        final JsonFormat format = new JsonFormat(pw);
        v.renderJson(format, new int[] { 900, 60, 1 });
        System.out.println(s.toString());

    }

}
