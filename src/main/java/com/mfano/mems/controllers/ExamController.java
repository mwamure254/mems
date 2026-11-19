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

import com.mfano.mems.auth.services.TermService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Exam;
import com.mfano.mems.services.ExamService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;
    private final UserService userService;
    private final TermService termService;

    @GetMapping
    public String exams(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
        userService.getPrices(auth, model);
        model.addAttribute("exams", examService.findByBranch(auth.getBranch().getId()));
        model.addAttribute("terms", termService.findAll());
        model.addAttribute("open", examService.open(auth.getBranch().getId()));
        return "principal/examination";

    }

    @PostMapping("/save")
    public String saveExam(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Exam entity,
            RedirectAttributes red) {
        try {
            examService.save(auth.getBranch().getId(), entity, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/exams";
    }

    @PostMapping("/update/{id}")
    public String updateExam(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Exam entity, RedirectAttributes redirectAttributes) {
        try {
            examService.update(id, entity, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/exams";
    }

    // manage exam module
    @PostMapping("/{option}/{id}")
    public String manageExams(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes red) {

        switch (option) {
            case "delete":
                try {
                    examService.delete(id, red, auth.getBranch().getId());
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Something went wrong, Exam could not delete.");
                }
                break;

            case "toggle":
                try {
                    examService.toggleActive(id, red, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addAttribute("error", "Operation not successful.");
                }
                break;
        }

        return "redirect:/exams";
    }

}
