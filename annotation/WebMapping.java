package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annote une méthode d'un contrôleur pour lui associer une URL.
 * Exemple : @WebMapping(url = "/users/list")
 */
@Retention(RetentionPolicy.RUNTIME) // Vue à l'exécution par le framework
@Target(ElementType.METHOD)          // Applicable uniquement sur les méthodes
public @interface WebMapping {
    String url();
}
