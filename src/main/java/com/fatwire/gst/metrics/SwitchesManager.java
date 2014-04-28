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
package com.fatwire.gst.metrics;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

public class SwitchesManager implements DynamicMBean {

    @Override
    public synchronized String getAttribute(final String name) throws AttributeNotFoundException {
        final boolean value = Switches.state(name);
        return Boolean.toString(value);
    }

    @Override
    public synchronized void setAttribute(final Attribute attribute) throws InvalidAttributeValueException,
            MBeanException, AttributeNotFoundException {
        final Object value = attribute.getValue();
        if (!(value instanceof Boolean)) {
            throw new InvalidAttributeValueException("Attribute value not a boolean: " + value);
        }
        final String name = attribute.getName();

        Boolean v = (Boolean) value;
        if (v)
            Switches.on(name);
        else {
            Switches.off(name);
        }

    }

    @Override
    public synchronized AttributeList getAttributes(final String[] names) {
        final AttributeList list = new AttributeList();
        for (final String name : names) {
            final boolean value = Switches.state(name);
            list.add(new Attribute(name, value));
        }
        return list;
    }

    @Override
    public synchronized AttributeList setAttributes(final AttributeList list) {
        final Attribute[] attrs = list.toArray(new Attribute[list.size()]);
        final AttributeList retlist = new AttributeList();
        for (final Attribute attr : attrs) {
            final String name = attr.getName();
            final Object value = attr.getValue();

            if (value instanceof Boolean) {
                Boolean v = (Boolean) value;
                if (v)
                    Switches.on(name);
                else {
                    Switches.off(name);
                }
                retlist.add(new Attribute(name, value));
            }
        }

        return retlist;
    }

    @Override
    public Object invoke(final String name, final Object[] args, final String[] sig) throws MBeanException,
            ReflectionException {

        throw new ReflectionException(new NoSuchMethodException(name));
    }

    @Override
    public synchronized MBeanInfo getMBeanInfo() {
        final SortedSet<String> names = new TreeSet<String>();
        for (final String name : Switches.names()) {
            names.add(name);
        }
        final MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[names.size()];
        final Iterator<String> it = names.iterator();
        for (int i = 0; i < attrs.length; i++) {
            final String name = it.next();
            attrs[i] = new MBeanAttributeInfo(name, Boolean.class.getName(), "Switch " + name, true, // isReadable
                    true, // isWritable
                    false); // isIs
        }
        final MBeanOperationInfo[] opers = null;/*{ new MBeanOperationInfo("on", "Reload properties from file", null, // no
                                                // parameters
                                                "void", MBeanOperationInfo.ACTION) };
                                                */
        return new MBeanInfo(this.getClass().getName(), "Switches Manager MBean", attrs, null, // constructors
                opers, null); // notifications
    }

}
