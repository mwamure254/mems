package com.mfano.mems.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Student;
import com.mfano.mems.models.Transfer;
import com.mfano.mems.services.StudentService;
import com.mfano.mems.services.TransferService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final UserService userService;
    private final TransferService transferService;
    private final StudentService studentService;

    @GetMapping
    public String transfers(@AuthenticationPrincipal CustomUserDetails auth,
        @RequestParam(required = false) String filter,  Model model) {
        userService.getPrices(auth, model);
        Map<Long, Student> student = new HashMap<>();
        for (Transfer t : transferService.findByBranch(auth.getBranch().getId())) {
            student.put(t.getStudent(), studentService.findById(t.getStudent()));
        }
        List<Transfer> transfers = (filter == null) 
            ? transferService.findByBranch(auth.getBranch().getId())
            : transferService.findByBranchAndType(auth.getBranch().getId(), filter);

        model.addAttribute("students", studentService.findByBranchAndActive(auth.getBranch().getId()));
        model.addAttribute("transfers", transfers);
        model.addAttribute("transferredStudent", student);
        return "student/transfers";
    }

    @PostMapping("/{type}")
    public String recordTransfer(@AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Transfer entity, RedirectAttributes red, @PathVariable String type) {
        switch (type) {
            case "in":
                try {
                    transferService.saveIn(auth.getBranch().getId(), entity, red);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addFlashAttribute("error", e.getMessage());
                }
                break;
            case "out":
                try {
                    transferService.saveOut(auth.getBranch().getId(), entity, red);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addFlashAttribute("error", e.getMessage());
                }
                break;
        }

        return "redirect:/transfers";
    }

}