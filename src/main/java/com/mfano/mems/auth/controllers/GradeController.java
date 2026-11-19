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

import com.mfano.mems.auth.services.GradeService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.dtos.EntityDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/grades")
public class GradeController {
    private final UserService userService;
    private final GradeService gradeService;

    @GetMapping
    public String grades(@AuthenticationPrincipal CustomUserDetails auth, Model red) {
        userService.getPrices(auth, red);
        red.addAttribute("grades", gradeService.findAll());
        return "admin/grades";
    }

    // Grade saving
    @PostMapping("/save")
    public String saveEntity(@ModelAttribute EntityDto ent,
            @AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes red) {
        try {
            gradeService.save(ent, red, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            red.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/grades";
    }

    // Role update
    @PostMapping("/update/{id}")
    public String updateEntity(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long id, @ModelAttribute EntityDto entity,
            RedirectAttributes redirectAttributes) {
        try {
            gradeService.update(id, entity, redirectAttributes, auth.getBranch().getId());

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/grades";
    }

    // manage grades module
    @PostMapping("/{option}/{id}")
    public String manageGrades(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {

        switch (option) {
            case "delete":
                try {
                    gradeService.delete(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                break;

            case "toggle":
                try {
                    gradeService.toggleActive(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                break;
        }

        return "redirect:/grades";
    }
}
