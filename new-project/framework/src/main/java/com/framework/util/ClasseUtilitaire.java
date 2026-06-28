package com.framework.util;

import com.framework.annotation.Controller;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Set;

/**
 * Utility class for scanning and discovering annotated controllers
 * using the Reflections library.
 * 
 * This class provides methods to find all classes annotated with
 * @Controller in a given package, and to retrieve their names.
 */
public class ClasseUtilitaire {

    /**
     * Scans the specified package and returns all classes annotated with @Controller.
     *
     * @param packageName the base package to scan (e.g., "com.app.controller")
     * @return a Set of Class<?> objects annotated with @Controller
     */
    public static Set<Class<?>> findAnnotatedControllers(String packageName) {
        Reflections reflections = new Reflections(
                packageName,
                new TypeAnnotationsScanner(),
                new SubTypesScanner(false)
        );
        return reflections.getTypesAnnotatedWith(Controller.class);
    }

    /**
     * Returns a display-friendly name for a controller class.
     * If the @Controller annotation has a non-empty value, that value is used.
     * Otherwise, the class simple name (minus "Controller" suffix if present) is returned.
     *
     * @param controllerClass the controller class
     * @return the controller name
     */
    public static String getControllerName(Class<?> controllerClass) {
        Controller annotation = controllerClass.getAnnotation(Controller.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        String simpleName = controllerClass.getSimpleName();
        if (simpleName.endsWith("Controller") && simpleName.length() > 10) {
            return simpleName.substring(0, simpleName.length() - 10);
        }
        return simpleName;
    }
}
