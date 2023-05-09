package com.shotmaniacs.rest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

@WebFilter(urlPatterns = {"/*"}, asyncSupported = true)
public class UrlRewriteFilter implements Filter {
    private final Map<String, String> urls = Map.ofEntries(

            // admin
            entry("/admin/announcements", "/pages/admin/announcements.html"),
            entry("/admin/crew-list", "/pages/admin/crew-list.html"),
            entry("/admin/dashboard", "/pages/admin/dashboard.html"),
            entry("/admin/incoming-events", "/pages/admin/incoming-events.html"),
            entry("/admin/event-calender", "/pages/admin/event-calender.html"),

            // client
            entry("/contacts", "/pages/client/contacts.html"),
            entry("/faq", "/pages/client/faq.html"),
            entry("/privacy", "/pages/client/privacy.html"),
            entry("/tos", "/pages/client/tos.html"),
            entry("/bookings", "/pages/client/client-events.html"),

            // crew
            entry("/crew/dashboard", "/pages/crew/dashboard.html"),

            // auth
            entry("/login", "/pages/auth.html"),
            entry("/signup", "/pages/auth.html"),
            entry("/forgot-password", "/pages/auth.html"),

            // other
            entry("/", "/pages/index.html"),
            entry("/profile", "/pages/profile.html")

    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var wrapper = new HttpServletRequestWrapper((HttpServletRequest) request);
        String original = wrapper.getRequestURI().substring(wrapper.getContextPath().length());

        int paramIndex = original.indexOf('?');
        String originalUrl = (paramIndex != -1) ? original.substring(paramIndex) : original;
        String originalParams = (paramIndex != -1) ? original.substring(0, paramIndex) : "";

        if (urls.containsKey(originalUrl)) {
            String prettyUrl = urls.get(originalUrl) + originalParams;
            System.out.println("Rewriting url: " + originalUrl + " -> " + prettyUrl);
            RequestDispatcher dispatcher = wrapper.getRequestDispatcher(prettyUrl);
            dispatcher.forward(request, response);
        } else {
            chain.doFilter(wrapper, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("Initializing the RewriteURLFilter...");
    }

    @Override
    public void destroy() {
        System.out.println("Destroying the RewriteURLFilter...");
    }
}
