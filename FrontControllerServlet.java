package framework;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import annotation.Controller; // Assurez-vous que le package correspond à votre annotation

public class FrontControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String url = req.getRequestURI();
        
        // On récupère la liste des contrôleurs du projet Test
        List<Class<?>> controllers = detectControllers("controler");

        // On envoie tout à la méthode d'affichage
        output(url, controllers, req, resp);
    }

    private List<Class<?>> detectControllers(String packageName) {
        List<Class<?>> controllerClasses = new ArrayList<>();
        String path = packageName.replace('.', '/');
        
        // On utilise le ContextClassLoader pour cibler l'application web active (le projet Test)
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource != null) {
            // Décodage du chemin pour éviter les problèmes d'espaces (%20) sous Windows
            File directory = new File(resource.getFile().replace("%20", " "));
            
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".class")) {
                            // Recomposer le nom complet : controler.NomClasse
                            String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);
                                // On vérifie si la classe du projet Test porte l'annotation @Controller
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    controllerClasses.add(clazz);
                                }
                            } catch (ClassNotFoundException e) {
                                // Ignorer ou journaliser
                            }
                        }
                    }
                }
            }
        }
        return controllerClasses;
    }

    private void output(String url, List<Class<?>> controllers, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<html><body>");
        out.println("<h2>URL reçue par le Framework du JAR : " + url + "</h2>");
        out.println("<h3>Scan des contrôleurs du projet utilisateur :</h3>");
        
        if (controllers.isEmpty()) {
            out.println("<p style='color:red;'>Aucun contrôleur détecté dans le package 'controler'.</p>");
        } else {
            out.println("<ul>");
            for (Class<?> clazz : controllers) {
                out.println("<li><b>" + clazz.getSimpleName() + "</b> (" + clazz.getName() + ")</li>");
            }
            out.println("</ul>");
        }
        out.println("</body></html>");
    }
}