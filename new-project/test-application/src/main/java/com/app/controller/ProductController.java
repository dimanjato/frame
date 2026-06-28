package com.app.controller;

import com.framework.annotation.Controller;

@Controller("Produits")
public class ProductController {

    public String listProducts() {
        return "List of all products";
    }
}
