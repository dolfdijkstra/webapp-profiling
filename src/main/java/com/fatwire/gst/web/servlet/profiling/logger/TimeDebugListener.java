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

package com.fatwire.gst.web.servlet.profiling.logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4JLogger;

public class TimeDebugListener implements ServletContextListener {
    private LifeCycleManager manager;

    public void contextDestroyed(ServletContextEvent sce) {
        if (manager != null) {
            manager.destroy();
        }

    }

    public void contextInitialized(ServletContextEvent sce) {
        if (this.isLog4JEnabled()) {
            sce.getServletContext().log(
                    "enabling Log4JAppenderLifeCycleManager");
            manager = new Log4JAppenderLifeCycleManager();
            manager.init();
        } else {
            sce.getServletContext().log(
                    "not enabling Log4JAppenderLifeCycleManager");

        }
    }

    boolean isLog4JEnabled() {
        try {
            return Log4JLogger.class.isInstance(LogFactory
                    .getLog("com.fatwire.logging.cs.time"));
        } catch (Throwable e) {
            return false;
        }

    }

}
