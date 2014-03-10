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

package com.fatwire.gst.web.servlet.profiling.support;

import java.util.LinkedList;
import java.util.List;

import com.fatwire.gst.web.servlet.profiling.Hierarchy;
import com.fatwire.gst.web.servlet.profiling.Instrumentation;
import com.fatwire.gst.web.servlet.profiling.Measurement;

public class DefaultInstrumentation implements Instrumentation {

    private final List<Measurement> measurements = new LinkedList<Measurement>();

    public Measurement[] getMeasurements() {
        Measurement[] m = measurements.toArray(new Measurement[measurements
                .size()]);
        measurements.clear();
        return m;
    }

    public Measurement start(final String key) {

        return new MeasurementImpl(key);
    }

    public Measurement start(Hierarchy name) {
        return new MeasurementImpl(name);
    }

}
