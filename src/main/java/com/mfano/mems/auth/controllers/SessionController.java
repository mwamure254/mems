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

import com.mfano.mems.auth.models.Term;
import com.mfano.mems.auth.services.TermService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {
    private final UserService userService;
    private final TermService termService;

    @GetMapping
    public String grades(@AuthenticationPrincipal CustomUserDetails auth, Model red) {
        userService.getPrices(auth, red);
        red.addAttribute("terms", termService.findAll());
        return "admin/terms";
    }

    @PostMapping("/save")
    public String saveTerm(
            @ModelAttribute Term term, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {
        try {
            termService.save(term, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/sessions";
    }

    @PostMapping("/update/{id}")
    public String updateTerm(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Term term,
            RedirectAttributes redirectAttributes) {
        try {
            termService.update(id, term, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/sessions";
    }

    // manage sessions module
    @PostMapping("/{option}/{id}")
    public String manageSessions(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {

        switch (option) {
            case "delete":
                try {
                    termService.delete(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                break;

            case "toggle":
                try {
                    termService.toggleActive(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                break;
        }

        return "redirect:/sessions";
    }
}
