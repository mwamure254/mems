package com.mfano.mems.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Category;
import com.mfano.mems.services.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping
    public String categories(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
        userService.getPrices(auth, model);
        model.addAttribute("categories", categoryService.findByBranch(auth.getBranch().getId()));
        return "library/categories";

    }

    @PostMapping("/save")
    public String saveCategory(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Category cat,
            RedirectAttributes red) {
        try {
            categoryService.save( auth.getBranch().getId(), cat, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Category cat, RedirectAttributes redirectAttributes) {
        try {
            categoryService.update(id, cat, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/categories";
    }

    // manage book category module
    @PostMapping("/{option}/{id}")
    public String manageCategories(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes red) {

        switch (option) {
            case "delete":
                try {
                    categoryService.delete(id, red, auth.getBranch().getId());
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Something went wrong, category could not delete.");
                }
                break;

            case "toggle":
                try {
                    categoryService.toggleActive(id, red, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addAttribute("error", "Operation not successful.");
                }
                break;
        }

        return "redirect:/categories";
    }
}
