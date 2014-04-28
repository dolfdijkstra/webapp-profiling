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

public class HTMLFormat implements TextFormat {
    final PrintWriter writer;

    public HTMLFormat(final PrintWriter writer) {
        super();
        this.writer = writer;
    }

    @Override
    public void td(final String... o) {
        boolean first = true;
        for (final String s : o) {
            writer.print("<td class=\"" + (first ? "left" : "right") + "\">");
            writer.print(escape(s));
            writer.print("</td>");
            first = false;
        }

    }

    String escape(final Object value) {
        return value == null ? "null" : org.apache.commons.lang.StringEscapeUtils.escapeHtml(value.toString());
    }

    @Override
    public void thr(final String... o) {
        writer.print("<tr>");
        boolean first = true;
        for (final String s : o) {
            writer.print("<th class=\"" + (first ? "left" : "right") + "\">");
            writer.print(escape(s));
            writer.print("</th>");
            first = false;
        }
        writer.print("</tr>");
    }

    @Override
    public void tr(final String... o) {
        writer.print("<tr>");
        td(o);
        writer.print("</tr>");
    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#tableEnd()
     */
    @Override
    public void endTable() {
        writer.print("</table>");

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#tableStart()
     */
    @Override
    public void startTable() {
        writer.print("<table>");

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#msg(java.lang.String)
     */
    @Override
    public void msg(final String msg) {
        writer.print("<p>");
        writer.print(escape(msg));
        writer.print("</p>");

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#startDoc()
     */
    @Override
    public void startDoc(final String title) {
        writer.println("<!DOCTYPE html>");
        writer.print("<html><head>");
        writer.print("<title>");
        writer.print(escape(title));
        writer.print("</title>");
        writer.print("<style>.right {text-align: right} .left {text-align: left} p{ margin: 0}");
        writer.print("div.section div{display: inline-block;}");
        writer.print("div#memory, div#gc, div#requests,{padding-right: 15px;}");
        writer.print("div#history table th,td {padding:2pt}</style>");

        writer.print("</head>");
        writer.print("<body>");

    }

    /* (non-Javadoc)
     * @see com.fatwire.gst.web.servlet.profiling.servlet.TextFormat#endDoc()
     */
    @Override
    public void endDoc() {
        writer.print("</body>");
        writer.print("</html>");
    }

    @Override
    public void startSubSection(final String name) {
        writer.println();
        writer.print("<div id=\"" + name.toLowerCase() + "\" class=\"subsection\">");
        writer.print("<h2>");
        writer.print(name);
        writer.println("</h2>");

    }

    @Override
    public void startSection(final String name) {
        writer.println();
        writer.print("<div id=\"" + name.toLowerCase() + "\" class=\"section\">");

    }

    @Override
    public void endSection() {
        writer.println("</div>");

    }

    @Override
    public void endSubSection() {
        writer.println("</div>");

    }

}
