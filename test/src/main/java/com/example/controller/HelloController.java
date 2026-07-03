package com.example.controller;

import annotation.Controller;
import annotation.WebMapping;

@Controller
public class HelloController {

    @WebMapping(url = "/hello", method = "GET")
    public String hello() {
        return "Hello from FrontController test project!";
    }

    @WebMapping(url = "/home")
    public String home() {
        return "Home page from test controller.";
    }
}
