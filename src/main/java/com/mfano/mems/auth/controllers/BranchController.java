package com.mfano.mems.auth.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Branch;
import com.mfano.mems.auth.services.BranchService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.utils.documents.FileUpload;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schools")
public class BranchController {
    private final BranchService storeService;
    private final FileUpload fileUpload;
    private final UserService userService;

    @GetMapping
    public String branches(@AuthenticationPrincipal CustomUserDetails auth, Model red) {
        userService.getPrices(auth, red);
        red.addAttribute("branches", storeService.findAll());
        return "admin/schools";
    }

    @PostMapping("/save")
    public String saveBranch(
            @ModelAttribute Branch branch, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes, @RequestParam("file") MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                branch.setDocument(fileUpload.uploadFile(file, "documents", redirectAttributes));
            }
            storeService.createBranch(branch, auth.getBranch().getId(), redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/schools";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Branch branch, @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                branch.setDocument(fileUpload.uploadFile(file, "documents", redirectAttributes));
            }
            storeService.updateBranch(auth.getBranch().getId(), id, branch, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/schools";
    }

    // manage schools module
    @PostMapping("/{option}/{id}")
    public String manageSchools(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {

        switch (option) {
            case "delete":
                try {
                    storeService.deleteBranch(auth.getBranch().getId(), id, redirectAttributes);
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Operation not successful.");
                }
                break;

            case "toggle":
                try {
                    storeService.toggleActive(id, redirectAttributes);
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not successful.");
                }
                break;
        }

        return "redirect:/schools";

    }
}
