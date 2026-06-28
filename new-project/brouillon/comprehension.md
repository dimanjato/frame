# Architecture Comprehension Notes

## How the Framework Works

### 1. Annotations Layer
- `@Controller` is a runtime annotation applied to classes.
- It accepts a `value()` parameter for a human-readable controller name.
- The framework scans for this annotation at startup.

### 2. Scanning Layer (ClasseUtilitaire)
- Uses the **Reflections** library (`org.reflections`) to scan a given package.
- `findAnnotatedControllers(String packageName)` returns all classes annotated with `@Controller`.
- `getControllerName(Class<?>)` extracts the display name from the annotation value, falling back to the class simple name (with "Controller" suffix stripped).

### 3. Servlet Layer (FrontControllerServlet)
- Configured via `web.xml` with an init-param `controllerPackage` specifying which package to scan.
- `init()` calls `ClasseUtilitaire.findAnnotatedControllers()` and stores the controller names.
- Both `doGet()` and `doPost()` delegate to `processRequest()`.
- `processRequest()` outputs:
  - Request URI, context path, relative path, HTTP method
  - List of all registered controller names

### 4. Request Flow
```
Browser Request → Tomcat → web.xml mapping (/*)
                → FrontControllerServlet.init() [on startup]
                → FrontControllerServlet.doGet/doPost
                → processRequest() → HTML response with controller list
```

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| Reflections library for scanning | Simplifies classpath scanning vs manual classloader iteration |
| Jakarta Servlet 6.0 | Compatible with Tomcat 10+ |
| JAR packaging for framework | Reusable across multiple web apps |
| WAR packaging for test app | Standard web application deployment |
| Maven multi-module (simple structure) | Two independent Maven projects for clarity |
| @Controller value attribute | Allows custom display names independent of Java class names |

## Dependencies

- **jakarta.servlet-api 6.0.0** — `provided` scope (Tomcat provides at runtime)
- **org.reflections 0.10.2** — compile scope (bundled in the JAR)

## Build Process

1. `mvn clean install` in `framework/` — builds JAR, installs to local Maven repo
2. `mvn clean package` in `test-application/` — builds WAR (resolves framework from local repo)
3. Deploy JAR to Tomcat's `lib/` and WAR to Tomcat's `webapps/`
