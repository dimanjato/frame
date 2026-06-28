package com.framework.servlet;

import com.framework.annotation.Controller;
import com.framework.util.ClasseUtilitaire;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FrontControllerServlet is the entry point for all HTTP requests.
 * 
 * On initialization (init()), it scans the configured base package
 * for classes annotated with @Controller, stores their names,
 * and registers them for request routing.
 * 
 * Both GET and POST requests are handled through processRequest(),
 * which outputs the requested URI and the list of discovered
 * controller names for verification purposes.
 */
public class FrontControllerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final List<String> controllerNames = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        String packageName = getInitParameter("controllerPackage");
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new ServletException(
                "Init parameter 'controllerPackage' is required. "
                + "Set it in web.xml as an init-param for FrontControllerServlet."
            );
        }

        try {
            Set<Class<?>> controllers = ClasseUtilitaire.findAnnotatedControllers(packageName.trim());

            if (controllers.isEmpty()) {
                System.out.println("[FrontController] No @Controller classes found in package: " + packageName);
            } else {
                for (Class<?> controllerClass : controllers) {
                    String name = ClasseUtilitaire.getControllerName(controllerClass);
                    controllerNames.add(name);
                    System.out.println("[FrontController] Registered controller: " + name
                            + " (" + controllerClass.getName() + ")");
                }
            }

            System.out.println("[FrontController] Initialization complete. "
                    + controllerNames.size() + " controller(s) registered.");
        } catch (Exception e) {
            throw new ServletException("Error initializing FrontControllerServlet: " + e.getMessage(), e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String requestUri = request.getRequestURI();
            String contextPath = request.getContextPath();
            String relativePath = requestUri.substring(contextPath.length());

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head><title>FrontController - Request Info</title></head>");
            out.println("<body>");
            out.println("<h1>FrontController Servlet</h1>");
            out.println("<hr>");
            out.println("<h2>Request Details</h2>");
            out.println("<p><strong>Request URI:</strong> " + requestUri + "</p>");
            out.println("<p><strong>Context Path:</strong> " + contextPath + "</p>");
            out.println("<p><strong>Relative Path:</strong> " + relativePath + "</p>");
            out.println("<p><strong>Method:</strong> " + request.getMethod() + "</p>");
            out.println("<hr>");
            out.println("<h2>Registered Controllers</h2>");

            if (controllerNames.isEmpty()) {
                out.println("<p><em>No controllers registered.</em></p>");
            } else {
                out.println("<ul>");
                for (String name : controllerNames) {
                    out.println("<li>" + name + "</li>");
                }
                out.println("</ul>");
            }

            out.println("<hr>");
            out.println("<p><small>FrontControllerServlet v1.0.0</small></p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
