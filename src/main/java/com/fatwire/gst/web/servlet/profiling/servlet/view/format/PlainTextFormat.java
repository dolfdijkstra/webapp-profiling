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
package com.fatwire.gst.web.servlet.profiling.servlet.view.format;

import java.io.PrintWriter;

public class PlainTextFormat implements TextFormat {
    final PrintWriter writer;

    public PlainTextFormat(final PrintWriter writer) {
        super();
        this.writer = writer;
    }

    @Override
    public void td(final String... o) {
        int i = 0;
        for (final String s : o) {
            if (i > 0) {
                writer.print('\t');
            }
            writer.print(s);
            if (i == 0) {
                writer.print(':');
            }

            i++;
        }
        writer.println();

    }

    @Override
    public void thr(final String... o) {
        int i = 0;
        for (final String s : o) {
            if (i > 0) {
                writer.print('\t');
            }
            writer.print(s);
            i++;
        }
        writer.println();

    }

    @Override
    public void tr(final String... o) {
        td(o);
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#tableEnd()
     */
    @Override
    public void endTable() {
        // writer.println("tableEnd");
        writer.println();
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#tableStart()
     */
    @Override
    public void startTable() {
        // writer.println("tableStart");
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#msg(java.lang.String)
     */
    @Override
    public void msg(final String msg) {

        writer.println(msg);

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#startDoc()
     */
    @Override
    public void startDoc(final String title) {

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#endDoc()
     */
    @Override
    public void endDoc() {
    }

    @Override
    public void startSubSection(final String name) {
        writer.println(name);

    }

    @Override
    public void startSection(final String name) {

    }

    @Override
    public void endSection() {

    }

    @Override
    public void endSubSection() {

    }

}
