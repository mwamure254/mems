package com.mfano.mems.auth.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Subject;
import com.mfano.mems.auth.services.SubjectService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subjects")
public class SubjectController {
    private final UserService userService;
    private final SubjectService subjectService;

    @GetMapping
    public String subjects(@AuthenticationPrincipal CustomUserDetails auth, Model red) {
        userService.getPrices(auth, red);
        red.addAttribute("subjects", subjectService.findAll());
        return "admin/subjects";
    }

    @PostMapping("/save")
    public String saveSubject(
            @ModelAttribute Subject subject, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {
        try {
            subjectService.save(subject, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/subjects";
    }

    @PostMapping("/update/{id}")
    public String updateSubject(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Subject subject,
            RedirectAttributes redirectAttributes) {
        try {
            subjectService.update(id, subject, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/subjects";
    }

    // manage subjects module
    @PostMapping("/{option}/{id}")
    public String manageSubjects(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {

        switch (option) {
            case "delete":
                try {
                    subjectService.delete(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                break;

            case "toggle":
                try {
                    subjectService.toggleActive(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                break;
        }

        return "redirect:/subjects";

    }

}
