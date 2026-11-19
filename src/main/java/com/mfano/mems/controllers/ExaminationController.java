package com.mfano.mems.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mfano.mems.auth.services.GradeService;
import com.mfano.mems.auth.services.SubjectService;
import com.mfano.mems.auth.services.TermService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Student;
import com.mfano.mems.services.ExamService;
import com.mfano.mems.services.MarkService;
import com.mfano.mems.services.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/examination")
public class ExaminationController {
    private final UserService userService;
    private final ExamService examService;
    private final MarkService markService;
    private final StudentService studentService;
    private final GradeService gradeService;
    private final TermService termService;
    private final SubjectService subjectService;

    @GetMapping
    public String examination(
            @RequestParam(required = false) Long exam,
            @RequestParam(required = false) Long grade,
            @RequestParam(required = false) Long subject,
            @AuthenticationPrincipal CustomUserDetails auth, Model model) {
        userService.getPrices(auth, model);

        model.addAttribute("exams", examService.findByBranch(auth.getBranch().getId()));
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("terms", termService.findAll());
        model.addAttribute("subjects", subjectService.findAll());

        if (grade != null) {
            List<Student> students = studentService.findByBranchAndGrade(auth.getBranch().getId(), grade);
            model.addAttribute("students", students);

            // Load existing marks
            Map<Long, Integer> existingMarks = new HashMap<>();
            if (exam != null && subject != null) {
                markService.findByExamAndSubject(exam, subject)
                        .forEach(m -> existingMarks.put(m.getStudent(), m.getScore()));
            }
            model.addAttribute("existingMarks", existingMarks);
        }

        model.addAttribute("selectedExam", exam);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedSubject", subject);
        return "principal/examination";

    }
}
