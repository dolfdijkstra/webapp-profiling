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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.fatwire.gst.web.servlet.profiling.servlet.AttributeListFilter;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.TextFormat;

class BeanRenderer {
    private MBeanServer mBeanServer = null;
    private final TextFormat f;

    BeanRenderer(final TextFormat html, final MBeanServer server) {
        this.mBeanServer = server;
        this.f = html;
    }

    Set<ObjectName> listBeans(final String qry) throws MalformedObjectNameException {
        return listBeans(new ObjectName(qry));
    }

    Set<ObjectName> listBeans(final ObjectName qry) {

        Set<ObjectName> names = null;
        names = mBeanServer.queryNames(qry, null);
        return names;
    }

    void renderTable(final String sectionName, final String qry, final String[] attrNames,
            final AttributeListFilter filter) throws IOException, MalformedObjectNameException {
        final List<List<?>> rows = createModel(qry, attrNames, filter);
        renderRows(sectionName, rows);
    }

    void renderTable(final String sectionName, final String qry, final String[] attrNames) throws IOException,
            MalformedObjectNameException {
        final List<List<?>> rows = createModel(qry, attrNames, null);
        renderRows(sectionName, rows);
    }

    void renderRows(final String sectionName, final List<List<?>> rows) throws IOException {

        if (rows.size() < 2) {
            return;
        }
        f.startSubSection(sectionName);
        f.startTable();
        final Iterator<List<?>> i = rows.iterator();
        final List<?> head = i.next();
        f.thr(toArray((head)));
        while (i.hasNext()) {
            final List<?> row = i.next();
            final String[] s = new String[row.size()];
            int c = 0;
            for (final Iterator<?> j = row.iterator(); j.hasNext();) {

                final Object value = j.next();
                if ((value instanceof Integer) || (value instanceof Long)) {
                    s[c] = String.format("%,d", value);
                } else {
                    s[c] = String.valueOf(value);
                }
                c++;
            }

            f.tr(s);
        }
        f.endTable();

    }

    private String[] toArray(final List<?> list) {
        final String[] s = new String[list.size()];
        int i = 0;
        for (final Iterator<?> j = list.iterator(); j.hasNext();) {
            final Object o = j.next();
            s[i] = o == null ? null : o.toString();
            i++;
        }

        return s;
    }

    /*
            void renderJsonTable(List<List<Object>> rows) throws IOException {

                if (rows.size() < 2)
                    return;
                Iterator<List<Object>> i = rows.iterator();
                List<Object> head = i.next();// first row hold the column headers
                f.print("[");
                while (i.hasNext()) {
                    List<Object> row = i.next();
                    Iterator<Object> j = row.iterator();
                    f.print('{');
                    for (Object s : head) {
                        Object value = j.next();
                        f.print('{');
                        f.print('"');
                        f.print(escapeJSON(s));
                        f.print('"');
                        f.print(':');

                        if (value instanceof Integer || value instanceof Long) {
                            f.print(String.format("%,d", value));
                        } else if (value instanceof Double || value instanceof Float) {
                            f.print(String.format("%.2f", value));
                        } else {
                            f.print('"');
                            f.print(escapeJSON(value));
                            f.print('"');
                        }
                        if (j.hasNext())
                            f.print(',');

                    }
                    f.print('}');
                    if (i.hasNext())
                        f.print(',');
                }
                f.print("]");

            }

    */
    List<List<?>> createModel(final String qry, final String[] attrNames, final AttributeListFilter filter)
            throws MalformedObjectNameException {

        final Set<ObjectName> n = listBeans(qry);
        final List<List<?>> rows = new LinkedList<List<?>>();
        final List<String> head = new LinkedList<String>();
        head.add("name");
        for (final String s : attrNames) {
            head.add(s);
        }
        rows.add(head);
        for (final Iterator<ObjectName> it = n.iterator(); it.hasNext();) {

            final ObjectName oname = it.next();
            try {
                final AttributeList attributes = mBeanServer.getAttributes(oname, attrNames);
                final boolean b = filter == null ? true : filter.filter(attributes);
                if (b) {
                    final LinkedList<Object> row = new LinkedList<Object>();
                    rows.add(row);

                    row.add(String.valueOf(oname.getKeyProperty("name")));
                    // double loop to maintain column order and to fill in the blanks
                    for (final String s : attrNames) {
                        int i = 0;
                        for (final Attribute attribute : attributes.asList()) {
                            if (s.equals(attribute.getName())) {
                                i++;
                                row.add(attribute.getValue());
                            }

                        }
                        if (i == 0) {
                            row.add(null);
                        }
                    }
                }
            } catch (final Exception e) {
                // f.println("\"Error\": \"" + escape(e.toString()) + "\"");
            }

        }
        return rows;
    }

}
