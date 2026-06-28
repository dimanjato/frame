package util;

import annotation.Controller;
import annotation.WebMapping;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;

public class Utilitaire {
    public Utilitaire(){}

    /**
     * Liste toutes les classes d'un package en scannant le classpath.
     * (Même mécanisme que FrontControllerServlet)
     */
    public List<Class<?>> chercherClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource != null) {
            File directory = new File(resource.getFile().replace("%20", " "));
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            try {
                                classes.add(Class.forName(className));
                            } catch (ClassNotFoundException e) {
                                // Ignorer
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * Retourne la liste des classes annotées avec @Controller dans le package donné.
     */
    public List<Class<?>> findControllers(String packageName) {
        List<Class<?>> controleurs = new ArrayList<>();
        for (Class<?> clazz : chercherClasses(packageName)) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controleurs.add(clazz);
            }
        }
        return controleurs;
    }

    /**
     * Liste toutes les méthodes annotées avec @WebMapping dans une classe donnée.
     * @return une liste de descriptifs : "nomMethode -> /url"
     */
    public List<String> findMethodesWebMapping(Class<?> controllerClass) {
        List<String> resultat = new ArrayList<>();
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(WebMapping.class)) {
                WebMapping mapping = method.getAnnotation(WebMapping.class);
                resultat.add(method.getName() + " -> " + mapping.url());
            }
        }
        return resultat;
    }

    /**
     * Pour un nom de classe complet (ex: "controler.HomeContoller"),
     * charge la classe et liste ses @WebMapping.
     */
    public List<String> findMethodesWebMapping(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return findMethodesWebMapping(clazz);
    }

    /**
     * Affiche dans la console tous les mappings @WebMapping de tous les contrôleurs
     * d'un package donné.
     */
    public void afficherTousLesMappings(String packageController) {
        List<Class<?>> controleurs = findControllers(packageController);

        for (Class<?> clazz : controleurs) {
            List<String> mappings = findMethodesWebMapping(clazz);

            System.out.println("=== " + clazz.getSimpleName() + " ===");
            if (mappings.isEmpty()) {
                System.out.println("  (aucune méthode @WebMapping)");
            } else {
                for (String m : mappings) {
                    System.out.println("  " + m);
                }
            }
            System.out.println();
        }
    }
}
