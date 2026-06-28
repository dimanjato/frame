package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark a class as a controller.
 * Classes annotated with @Controller will be automatically
 * detected and registered by the FrontControllerServlet.
 * 
 * The value attribute can be used to specify a custom name
 * for the controller (defaults to the class simple name if empty).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
    String value() default "";
}
