package com.fatwire.gst.web.servlet.profiling.servlet.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.gst.web.servlet.profiling.jmx.UnregisterMBeansCommand;

public class ResponseTimeRequestListener implements ServletRequestListener,
        ServletContextListener {

    private final Log log = LogFactory.getLog(this.getClass());

    private static final String MBEAN_NAME = "com.fatwire.gst.web.servlet:type=ResponseTimeStatistic";

    private NameBuilder nameBuilder = new NameBuilder();

    private ResponseTimeStatistic root = new ResponseTimeStatistic();

    private ThreadLocal<Measurement> time = new ThreadLocal<Measurement>() {

        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected Measurement initialValue() {
            return new Measurement();
        }

    };

    private Map<String, ResponseTimeStatistic> names = new ConcurrentHashMap<String, ResponseTimeStatistic>(
            800, 0.75f, 400);

    public void requestDestroyed(ServletRequestEvent event) {
        if (event.getServletRequest() instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) event
                    .getServletRequest();
            Measurement m = time.get();
            m.stop();

            root.signal(request, m);
            String name = nameBuilder.extractName(request);
            ResponseTimeStatistic x = names.get(name);
            if (x == null) {
                x = new ResponseTimeStatistic();

                names.put(name, x);
                try {
                    ManagementFactory.getPlatformMBeanServer().registerMBean(x,
                            ObjectName.getInstance(MBEAN_NAME + name));
                } catch (Exception e) {
                    log.warn(e.getMessage() + " for " + MBEAN_NAME + name, e);
                }
            }
            x.signal(request, m);

        }

    }

    public void requestInitialized(ServletRequestEvent event) {
        if (event.getServletRequest() instanceof HttpServletRequest)
            time.get().start();

    }

    public void contextDestroyed(ServletContextEvent sce) {
        time = null;
        try {
            UnregisterMBeansCommand.unregister(MBEAN_NAME + ",*");
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    public void contextInitialized(ServletContextEvent sce) {
        log.debug("contextInitialized "
                + sce.getServletContext().getServletContextName());
        try {

            ManagementFactory.getPlatformMBeanServer().registerMBean(root,
                    ObjectName.getInstance(MBEAN_NAME));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

}
