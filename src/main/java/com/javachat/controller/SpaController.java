package com.javachat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {
    @RequestMapping(value = {"/{path:^(?!api|public|ws)[^\\.]*}", "/**/{path:^(?!api|public|ws).*}/{path:[^\\.]*}"})
    public String get(){
        return "index";
    }
}
