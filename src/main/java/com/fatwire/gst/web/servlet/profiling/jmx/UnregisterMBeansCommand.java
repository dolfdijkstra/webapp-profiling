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

package com.fatwire.gst.web.servlet.profiling.jmx;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnregisterMBeansCommand {
    private static final Log log = LogFactory.getLog(UnregisterMBeansCommand.class);

    public static void unregister(String query) throws MalformedObjectNameException, NullPointerException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance(query);
        Set<ObjectName> mbeans = server.queryNames(name, null);
        for (ObjectName on : mbeans) {
            try {
                server.unregisterMBean(on);
            } catch (Exception ee) {
                log.error(ee.getMessage(), ee);
            }
        }

    }

}
