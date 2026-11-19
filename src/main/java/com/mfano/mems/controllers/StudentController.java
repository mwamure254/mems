package com.mfano.mems.controllers;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Grade;
import com.mfano.mems.auth.services.GradeService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Student;
import com.mfano.mems.services.StudentService;
import com.mfano.mems.utils.documents.FileUpload;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {
    private final UserService userService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final FileUpload fileUpload;

    @GetMapping
    public String students(@AuthenticationPrincipal CustomUserDetails auth, 
        @RequestParam(required = false) Long filter, Model model) {
        userService.getPrices(auth, model);

        Map<Long, Grade> grade = new HashMap<>();
        for (Student s : studentService.findByBranch(auth.getBranch().getId())) {
            if (s.getGrade() != null) {
                grade.put(s.getGrade(), gradeService.findById(s.getGrade()));
            }
        }
        List<Student> students = (filter == null) 
            ? studentService.findByBranch(auth.getBranch().getId()) 
            : studentService.findByBranchAndGrade(auth.getBranch().getId(), filter);

        model.addAttribute("students", students);
        model.addAttribute("grade", grade);
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("selected", filter);
        return "principal/admission";
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student entity, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes red, @RequestParam("file") MultipartFile upload) {
        try {
            if (upload != null && !upload.isEmpty()) {
                entity.setUpload(fileUpload.uploadFile(upload, "profiles", red));
            }
            studentService.save(auth.getBranch().getId(), entity, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, Student entity, @AuthenticationPrincipal CustomUserDetails auth,
            @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (file != null && !file.isEmpty()) {
                entity.setUpload(fileUpload.uploadFile(file, "profiles", redirectAttributes));
            }
            studentService.update(id, entity, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/students";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            studentService.toggleActive(id, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            RedirectAttributes red) {
        try {
            studentService.delete(auth.getBranch().getId(), id, red);
        } catch (Exception e) {
            red.addFlashAttribute("error", "Something went wrong, announcement could not delete.");
        }
        return "redirect:/students";
    }
}
