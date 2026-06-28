package com.app.controller;

import com.framework.annotation.Controller;

@Controller("Accueil")
public class HomeController {

    public String displayHome() {
        return "Welcome to the home page!";
    }
}
