package mapping;

/**
 * Represents a URL mapping to a controller method.
 * Stores the class name, method name, and HTTP method (GET/POST).
 */
public class Mapping {
    private String className;
    private String methodName;
    private String httpMethod; // "GET", "POST", or "" for both

    public Mapping() {}

    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
        this.httpMethod = "";
    }

    public Mapping(String className, String methodName, String httpMethod) {
        this.className = className;
        this.methodName = methodName;
        this.httpMethod = httpMethod == null ? "" : httpMethod.toUpperCase();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod == null ? "" : httpMethod.toUpperCase();
    }

    /**
     * Checks if this mapping accepts the given HTTP method.
     * An empty httpMethod means it accepts both GET and POST.
     */
    public boolean acceptMethod(String method) {
        if (httpMethod.isEmpty()) return true;
        return httpMethod.equalsIgnoreCase(method);
    }

    /**
     * Returns a composite key used for duplicate detection: "URL:METHOD"
     */
    public String getCompositeKey(String url) {
        return url + ":" + (httpMethod.isEmpty() ? "ANY" : httpMethod);
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                '}';
    }
}
