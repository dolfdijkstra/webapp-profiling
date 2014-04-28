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
package com.fatwire.gst.metrics.listener.web;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

import com.fatwire.gst.metrics.Measurement;
import com.fatwire.gst.metrics.StartEndMeasurement;

public class WebMeasurements {

    private static Comparator<WebMeasurement> comparator = new Comparator<WebMeasurement>() {

        @Override
        public int compare(WebMeasurement o1, WebMeasurement o2) {
            return Long.compare(o1.getStart(), o2.getStart());
        }

    };

    private final LinkedList<WebMeasurement> metrics = new LinkedList<WebMeasurement>();

    public void add(StartEndMeasurement metric) {
        metrics.add(new WebMeasurement(metric));
    }

    public Collection<WebMeasurement> getMeasurements() {
        TreeSet<WebMeasurement> m = new TreeSet<WebMeasurement>(comparator);
        m.addAll(metrics);
        return Collections.unmodifiableSet(m);

    }

    public void add(Measurement me) {
        metrics.add(new WebMeasurement(me));

    }

}
