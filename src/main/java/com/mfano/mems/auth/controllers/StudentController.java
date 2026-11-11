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

import com.mfano.mems.auth.services.GradeService;
import com.mfano.mems.auth.services.StudentService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Student;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final UserService userService;
    private final StudentService studentService;
    private final GradeService gradeService;

    @GetMapping
    public String students(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
        userService.getPrices(auth, model);
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("selected", "grade");
        return "principal/admission";
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.save(student);
        return "redirect:/students";
    }

    @GetMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id) {
        studentService.updateBy(id);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteBy(id);
        return "redirect:/students";
    }
}
