package com.mfano.mems.auth.controllers;

import com.mfano.mems.services.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@PreAuthorize("hasAuthority('LIBRARY')")
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {
    private final CategoryService categoryService;
    private final UserService userService;

    // manage /GET/* module
    @GetMapping("/{option}")
    public String manage(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (option) {
            case "dashboard":
                dir = "library/index";
                break;
        }

        return dir;
    }
}