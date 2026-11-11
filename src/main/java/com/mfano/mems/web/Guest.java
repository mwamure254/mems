package com.mfano.mems.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guest")
public class Guest {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }
}
