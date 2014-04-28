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
import java.util.LinkedList;

public class JsonFormat {

    final PrintWriter writer;
    int c = 0;
    LinkedList<Integer> counterStack = new LinkedList<Integer>();

    public JsonFormat(final PrintWriter writer) {
        super();
        this.writer = writer;
    }

    public void startDoc() {
        writer.print('{');

    }

    public void endDoc() {
        writer.print('}');

    }

    public void nvp(final String name, final int value) {
        name(name);
        writer.print(value);

    }

    private void name(final String name) {
        if (c > 0) {
            writer.print(",");
        }

        writer.print('"');
        writer.print(name);
        writer.print('"');
        writer.print(':');
    }

    public void nvp(final String name, final long value) {
        name(name);
        writer.print(value);
        c++;

    }

    public void nvp(final String name, final double value) {
        name(name);
        writer.print(value);
        c++;

    }

    public void nvp(final String name, final boolean value) {
        name(name);
        writer.print(value);
        c++;

    }

    public void nvp(final String name, final String value) {
        name(name);
        writer.print('"');
        writer.print(value);
        writer.print('"');
        c++;

    }

    public void startArray(final String name) {

        name(name);

        writer.print('[');
        push();
    }

    public void endArray() {

        writer.print(']');
        pop();
        c++;
    }

    public void startObject() {
        if (c > 0) {
            writer.print(",");
        }
        c++;
        writer.print('{');
        push();
    }

    public void startObject(final String name) {

        name(name);
        c++;
        writer.print('{');
        push();
    }

    public void endObject() {

        writer.print('}');
        pop();
    }

    private void push() {
        counterStack.addLast(c);
        c = 0;
    }

    private void pop() {
        c = counterStack.removeLast();
    }

}
