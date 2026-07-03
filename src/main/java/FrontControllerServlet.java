

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mapping.Mapping;
import util.Utilitaire;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

public class FrontControllerServlet extends HttpServlet {

    private Map<String, Mapping> urlMappings;

    @Override
    public void init() throws ServletException {
        String packageName = getInitParameter("packageName");

        if (packageName == null || packageName.isEmpty()) {
            throw new ServletException("Le paramètre 'packageName' est requis dans web.xml.");
        }

        try {
            urlMappings = Utilitaire.buildUrlMappings(packageName);
            System.out.println("[FrontControllerServlet] Initialisation terminée. "
                    + urlMappings.size() + " mapping(s) enregistré(s).");
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation : " + e.getMessage(), e);
        }
    }

    private Mapping findMapping(String url, String httpMethod) {
        // Cherche d'abord avec méthode spécifique
        String specificKey = url + ":" + httpMethod;
        Mapping mapping = urlMappings.get(specificKey);
        if (mapping != null) return mapping;

        // Sinon cherche avec la clé ANY (méthode non spécifiée)
        String anyKey = url + ":ANY";
        return urlMappings.get(anyKey);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String httpMethod)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = uri.substring(contextPath.length());

        try (PrintWriter out = response.getWriter()) {
            Mapping mapping = findMapping(url, httpMethod);

            if (mapping != null) {
                try {
                    Class<?> clazz = Class.forName(mapping.getClassName());
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getDeclaredMethod(mapping.getMethodName());
                    Object result = method.invoke(instance);

                    // Redirige vers la JSP si le résultat commence par "/"
                    if (result != null && result.toString().startsWith("/")) {
                        String viewPath = result.toString();
                        request.getRequestDispatcher(viewPath).forward(request, response);
                    } else {
                        out.println("<!DOCTYPE html>");
                        out.println("<html><head><meta charset='UTF-8'><title>Résultat</title></head><body>");
                        out.println("<h1>Résultat de l'invocation</h1>");
                        out.println("<p><strong>Classe :</strong> " + mapping.getClassName() + "</p>");
                        out.println("<p><strong>Méthode :</strong> " + mapping.getMethodName() + "()</p>");
                        out.println("<p><strong>Méthode HTTP :</strong> " + httpMethod + "</p>");
                        out.println("<p><strong>Résultat :</strong> " + (result != null ? result.toString() : "(void)") + "</p>");
                        out.println("</body></html>");
                    }
                } catch (Exception e) {
                    throw new ServletException("Erreur lors de l'invocation de "
                            + mapping.getClassName() + "." + mapping.getMethodName()
                            + "() : " + e.getCause().getMessage(), e);
                }
            } else {
                out.println("<!DOCTYPE html>");
                out.println("<html><head><meta charset='UTF-8'><title>URL non trouvée</title></head><body>");
                out.println("<h1>URL non reconnue</h1>");
                out.println("<p>L'URL <strong>" + url + "</strong> (méthode " + httpMethod + ") n'est pas reconnue.</p>");
                out.println("<h3>URLs disponibles :</h3>");
                out.println("<ul>");
                for (Map.Entry<String, Mapping> entry : urlMappings.entrySet()) {
                    String key = entry.getKey();
                    Mapping m = entry.getValue();
                    // key format: "URL:METHOD"
                    String[] parts = key.split(":", 2);
                    String displayUrl = parts[0];
                    String method = parts.length > 1 ? parts[1] : "ANY";
                    out.println("<li><strong>" + displayUrl + "</strong> [" + method + "] → "
                            + m.getClassName() + "." + m.getMethodName() + "()</li>");
                }
                out.println("</ul>");
                out.println("</body></html>");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "POST");
    }
}
