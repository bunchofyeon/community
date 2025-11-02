package com.example.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RulesController {

    @GetMapping("/terms")
    public String terms(){
        return "rules/terms";
    }

    @GetMapping("/privacy")
    public String privacy(){
        return "rules/privacy";
    }

}
