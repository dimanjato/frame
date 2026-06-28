package com.app.controller;

import com.framework.annotation.Controller;

@Controller("Utilisateurs")
public class UserController {

    public String listUsers() {
        return "List of all users";
    }
}
