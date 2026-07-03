package com.example.test;

import mapping.Mapping;
import util.Utilitaire;

import java.util.Map;

public class ScanTest {

    public static void main(String[] args) throws Exception {
        String packageName = "com.example.controller";
        Map<String, Mapping> urlMappings = Utilitaire.buildUrlMappings(packageName);

        System.out.println("Mappings trouvés pour le package : " + packageName);
        for (Map.Entry<String, Mapping> entry : urlMappings.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().getClassName()
                    + "." + entry.getValue().getMethodName() + "() [" + entry.getValue().getHttpMethod() + "]");
        }
    }
}
