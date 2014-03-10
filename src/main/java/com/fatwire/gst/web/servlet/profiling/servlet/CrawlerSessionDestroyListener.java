package com.fatwire.gst.web.servlet.profiling.servlet;

import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * {@link ServletRequestListener} that invalidates sessions for requests from
 * bots and crawlers to prevent session manager overload.
 * 
 * @author dolf
 * 
 */
public class CrawlerSessionDestroyListener implements ServletRequestListener, ServletContextListener {

    private Pattern[] patterns;

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        if (patterns == null || patterns.length == 0)
            return;
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        String ua = request.getHeader("user-agent");
        if (ua == null || ua.length() == 0 || ua.trim().length() == 0)
            return;
        for (Pattern p : patterns) {
            if (p.matcher(ua).matches()) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                break;
            }
        }

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {

    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        patterns = new Pattern[1];
        patterns[0] = Pattern.compile("Wget/.*");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
