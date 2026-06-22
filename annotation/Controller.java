package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Crucial pour que le framework la voie à l'exécution
@Target(ElementType.TYPE)           // S'applique uniquement sur les classes
public @interface Controller {
}