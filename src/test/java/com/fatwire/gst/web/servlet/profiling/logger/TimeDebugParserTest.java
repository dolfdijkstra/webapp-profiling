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
package com.fatwire.gst.web.servlet.profiling.logger;

import junit.framework.TestCase;

public class TimeDebugParserTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseItPage() throws Exception {
        final int[] count = new int[] { 0 };
        final SimpleTimeDebugParser p = new SimpleTimeDebugParser(new ParserCallback() {

            @Override
            public void update(final String type, final String subType, final long time) {
                count[0]++;
                assertEquals("page", type);
                assertEquals(null, subType);
                assertEquals(2696L, time);
            }

        });
        p.parseIt("Execute time  Hours: 0 Minutes: 0 Seconds: 2:696");
        assertEquals(1, count[0]);
    }

    public void testParseItPage2() throws Exception {
        final int[] count = new int[] { 0 };
        final SimpleTimeDebugParser p = new SimpleTimeDebugParser(new ParserCallback() {

            @Override
            public void update(final String type, final String subType, final long time) {
                count[0]++;
                assertEquals("page", type);

                assertEquals("OpenMarket/Xcelerate/Actions/ShowMyDesktopFront", subType);
                assertEquals(2696L, time);
            }

        });
        p.parseIt("Execute page OpenMarket/Xcelerate/Actions/ShowMyDesktopFront Hours: 0 Minutes: 0 Seconds: 2:696");
        assertEquals(1, count[0]);
    }

    public void testParseItElement() throws Exception {
        final int[] count = new int[] { 0 };
        final SimpleTimeDebugParser p = new SimpleTimeDebugParser(new ParserCallback() {

            @Override
            public void update(final String type, final String subType, final long time) {
                count[0]++;
                assertEquals("element", type);

                assertEquals("OpenMarket/Xcelerate/Search/Event", subType);
                assertEquals(2L, time);
            }

        });
        p.parseIt("Executed element OpenMarket/Xcelerate/Search/Event in 2ms.");
        assertEquals(1, count[0]);
    }

    public void testParseIt_prep_statement() throws Exception {
        final int[] count = new int[] { 0 };
        final SimpleTimeDebugParser p = new SimpleTimeDebugParser(new ParserCallback() {

            @Override
            public void update(final String type, final String subType, final long time) {
                count[0]++;
                assertEquals("sql", type);

                assertEquals("select * from AssetQueues", subType);
                assertEquals(1L, time);
            }

        });
        p.parseIt("Executed prepared statement select * from AssetQueues in 1ms");
        assertEquals(1, count[0]);
    }

    public void testParseIt_sql2() throws Exception {
        final int[] count = new int[] { 0 };
        final SimpleTimeDebugParser p = new SimpleTimeDebugParser(new ParserCallback() {

            @Override
            public void update(final String type, final String subType, final long time) {
                count[0]++;
                assertEquals("sql", type);

                assertEquals("SELECT assettype,assetid,action FROM Global_Q ORDER BY id", subType);
                assertEquals(1L, time);
            }

        });
        p.parseIt("Executed prepared statement SELECT assettype,assetid,action FROM Global_Q ORDER BY id in 1ms");
        assertEquals(1, count[0]);
    }

    public void testParseIt_sql3() throws Exception {
        final int[] count = new int[] { 0 };
        final SimpleTimeDebugParser p = new SimpleTimeDebugParser(new ParserCallback() {

            @Override
            public void update(final String type, final String subType, final long time) {
                count[0]++;
                assertEquals("sql", type);
                assertEquals("SELECT assettype,assetid,action FROM AVIImage_Q ORDER BY id", subType);
                assertEquals(1L, time);
            }

        });
        p.parseIt("Executed query SELECT assettype,assetid,action FROM AVIImage_Q ORDER BY id in 1ms");
        assertEquals(1, count[0]);
    }

}
