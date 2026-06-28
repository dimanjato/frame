# Custom MVC Framework

A lightweight Java MVC framework built with Jakarta Servlet API. The framework automatically discovers controller classes annotated with `@Controller` using the Reflections library and routes all requests through a `FrontControllerServlet`.

## Project Structure

```
new-project/
├── framework/                          # Framework module (JAR)
│   ├── pom.xml                         # Maven build file
│   └── src/main/java/com/framework/
│       ├── annotation/
│       │   └── Controller.java         # Custom @Controller annotation
│       ├── servlet/
│       │   └── FrontControllerServlet.java  # Entry-point servlet
│       └── util/
│           └── ClasseUtilitaire.java   # Reflections-based scanner
│
├── test-application/                   # Test web app module (WAR)
│   ├── pom.xml                         # Maven build file (depends on framework)
│   └── src/main/
│       ├── java/com/app/controller/
│       │   ├── HomeController.java     # @Controller("Accueil")
│       │   ├── UserController.java     # @Controller("Utilisateurs")
│       │   └── ProductController.java  # @Controller("Produits")
│       └── webapp/WEB-INF/
│           └── web.xml                 # Servlet mapping to /*
│
├── deployFramework.sh                  # Build framework & deploy to Tomcat
├── deployTestApplication.sh            # Build test app & deploy WAR to Tomcat
├── assets/                             # Project assets (diagrams, screenshots)
├── brouillon/                          # Working notes and drafts
└── README.md                           # This file
```

## Prerequisites

- Java 17+
- Apache Maven 3.8+
- Apache Tomcat 10+ (for Jakarta Servlet 6.0)
- Git (optional)

## Building

### 1. Build the Framework JAR

```bash
cd framework
mvn clean install
```

This compiles the framework, runs the Reflections dependency, and installs the JAR into your local Maven repository (~/.m2/repository/).

### 2. Build the Test Application WAR

```bash
cd test-application
mvn clean package
```

This produces `test-application/target/test-application-1.0.0.war`.

## Deployment

### Option A: Automated Deployment Scripts

**Deploy framework to Tomcat** (copies JAR to Tomcat's lib directory):

```bash
./deployFramework.sh /path/to/tomcat/lib
```

**Deploy the test application** (copies WAR to Tomcat's webapps directory):

```bash
./deployTestApplication.sh /path/to/tomcat/webapps
```

### Option B: Manual Deployment

1. Copy `framework/target/framework-1.0.0.jar` to `TOMCAT_HOME/lib/`
2. Copy `test-application/target/test-application-1.0.0.war` to `TOMCAT_HOME/webapps/`
3. Restart Tomcat (or wait for auto-deployment)

### Option C: Rename WAR for Shorter URL

```bash
cp test-application/target/test-application-1.0.0.war /path/to/tomcat/webapps/test-app.war
```

Then access at: http://localhost:8080/test-app/

## How It Works

1. **`@Controller` annotation** — Marks a Java class as a controller. Accepts an optional `value()` parameter for a display name.

2. **`ClasseUtilitaire`** — Uses the Reflections library to scan a specified package at runtime and discover all classes annotated with `@Controller`.

3. **`FrontControllerServlet`** — Init parameter `controllerPackage` specifies which package to scan. During `init()`, it calls `ClasseUtilitaire` to find and register all controllers. Both `doGet()` and `doPost()` delegate to `processRequest()`, which outputs:
   - The requested URI
   - The context path and relative path
   - The HTTP method
   - The list of registered controller names

4. **`web.xml`** — Maps `FrontControllerServlet` to `/*` so every request goes through it.

## Verification

After deployment, open the application in a browser:

```
http://localhost:8080/test-application-1.0.0/
```

You should see:
- The request URI, context path, relative path, and HTTP method
- A list of registered controllers: **Accueil**, **Utilisateurs**, **Produits**

## License

MIT
