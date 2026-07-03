package util;

import annotation.Controller;
import annotation.WebMapping;
import mapping.Mapping;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

public class Utilitaire {

    private Utilitaire() {}

    /**
     * Scanne un package et retourne la liste des noms complets de classes trouvés.
     */
    public static List<String> getClassInPackage(String packageName, ClassLoader classLoader) throws Exception {
        List<String> classNames = new ArrayList<>();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            throw new Exception("Package introuvable : " + packageName);
        }

        File directory = new File(resource.getFile().replace("%20", " "));
        if (!directory.exists() || !directory.isDirectory()) {
            return classNames;
        }

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                classNames.add(className);
            }
        }
        return classNames;
    }

    /**
     * Filtre une liste de noms de classes pour ne garder que celles
     * qui portent l'annotation donnée.
     */
    public static List<String> getControllers(List<String> classNames, String annotationClassName, java.lang.annotation.ElementType target) throws Exception {
        List<String> controllers = new ArrayList<>();
        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(className);
            }
        }
        return controllers;
    }

    /**
     * Construit la map des mappings (url -> Mapping) pour tous les contrôleurs
     * d'un package donné.
     * Détecte les doublons (url + méthode HTTP) et affiche "url déjà utilisé" via println.
     * Après construction, invoque chaque méthode et affiche le résultat (Sprint 3 bis).
     */
    public static Map<String, Mapping> buildUrlMappings(String packageName) throws Exception {
        Map<String, Mapping> urlMappings = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<String> classNames = getClassInPackage(packageName, classLoader);
        List<String> controllers = getControllers(classNames, "annotation.Controller", java.lang.annotation.ElementType.TYPE);

        for (String controller : controllers) {
            Class<?> clazz = Class.forName(controller);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(WebMapping.class)) {
                    WebMapping mapping = method.getAnnotation(WebMapping.class);
                    String url = mapping.url();
                    String httpMethod = mapping.method();

                    // Vérification des doublons : clé composée "URL:METHOD"
                    String key = url + ":" + (httpMethod.isEmpty() ? "ANY" : httpMethod.toUpperCase());

                    if (urlMappings.containsKey(key)) {
                        // TSOTRA : url déjà utilisé
                        System.out.println("url déjà utilisé : " + url
                            + " (méthode: " + (httpMethod.isEmpty() ? "GET/POST" : httpMethod)
                            + ") dans " + controller + "." + method.getName());
                    } else {
                        Mapping m = new Mapping(controller, method.getName(), httpMethod);
                        urlMappings.put(key, m);

                        // BIS : invoquer la méthode via reflection et afficher le résultat
                        try {
                            Object result = method.invoke(instance);
                            System.out.println("Invocation de " + controller + "." + method.getName()
                                + "() -> " + (result != null ? result : "(void)"));
                        } catch (Exception e) {
                            System.out.println("Erreur lors de l'invocation de " + controller + "." + method.getName()
                                + "() : " + e.getCause().getMessage());
                        }
                    }
                }
            }
        }

        return urlMappings;
    }
}
