package util;

import java.util.Map;
import java.util.HashMap;

public class ModelView {
    private String ViewName;
    private Map<String, Object> model = new HashMap<>();

    public ModelView(String viewName) {
        this.ViewName = viewName;
    }

    public void addObject(String key, Object value) {
        model.put(key, value);
    }   
    public String getViewName() {
        return ViewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
