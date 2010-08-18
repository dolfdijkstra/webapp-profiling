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

package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import com.fatwire.gst.web.servlet.profiling.servlet.jmx.NameBuilder;

import junit.framework.TestCase;

public class NameBuilderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSanitize() {
        NameBuilder b = new NameBuilder();
        String x = b.sanitize("foo/bar\"boo\"while,now");
        
        assertEquals("foo/bar_boo_while_now",x);
    }

}
