package com.mfano.mems.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Grade;
import com.mfano.mems.auth.models.Subject;
import com.mfano.mems.auth.services.GradeService;
import com.mfano.mems.auth.services.SubjectService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.dtos.MarkDto;
import com.mfano.mems.dtos.MarkForm;
import com.mfano.mems.dtos.ReportCardDTO;
import com.mfano.mems.models.Exam;
import com.mfano.mems.models.Marks;
import com.mfano.mems.models.Student;
import com.mfano.mems.services.ExamService;
import com.mfano.mems.services.MarkService;
import com.mfano.mems.services.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/marks")
@RequiredArgsConstructor
public class MarkController {
    private final StudentService studentService;
    private final MarkService markService;
    private final GradeService gradeService;
    private final SubjectService subjectService;
    private final ExamService examService;
    private final UserService userService;

    @GetMapping
    public String marksPage(@RequestParam(required = false) Long exam,
            @RequestParam(required = false) Long grade,
            @RequestParam(required = false) Long subject,
            @AuthenticationPrincipal CustomUserDetails auth, Model model) {

        userService.getPrices(auth, model);
        Map<Long, Exam> exm = new HashMap<>();

        model.addAttribute("exams", examService.findByBranch(auth.getBranch().getId()));
        model.addAttribute("grades", gradeService.findAll());
        model.addAttribute("subjects", subjectService.findAll());

        if (grade != null) {
            List<Student> students = studentService.findByBranchAndGrade(auth.getBranch().getId(), grade);
            model.addAttribute("students", students);

            // Load existing marks
            Map<Long, Integer> existingMarks = new HashMap<>();
            if (exam != null && subject != null) {
                markService.findByExamAndSubject(exam, subject)
                        .forEach(m -> existingMarks.put(m.getStudent(), m.getScore()));
                exm.put(exam, examService.findById(exam));
                model.addAttribute("exam", exm);
            }
            model.addAttribute("existingMarks", existingMarks);
        }

        model.addAttribute("selectedExam", exam);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedSubject", subject);
        return "teacher/marks";
    }

    @PostMapping("/save")
    public String saveMarks(@RequestParam Long exam,
            @RequestParam Long subject, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute MarkForm form, RedirectAttributes red) {
                
        if (form != null && examService.findById(exam).isActive() != false) {
            form.getMarks().forEach(m -> {
                Marks mark = markService.findByStudentAndExamAndSubject(m.getStudent(), exam, subject);
                if (mark != null) {
                    mark.setScore(m.getScore());
                    mark.setLevel(getLevel(m.getScore()));
                    mark.setPoints(getPoints(m.getScore()));
                    mark.setRemark(getRemark(m.getScore()));
                } else {
                    mark = new Marks();
                    mark.setExam(exam);
                    mark.setSubject(subject);
                    mark.setScore(m.getScore());
                    mark.setLevel(getLevel(m.getScore()));
                    mark.setPoints(getPoints(m.getScore()));
                    mark.setRemark(getRemark(m.getScore()));
                    mark.setStudent(m.getStudent());
                    mark.setBranch(auth.getBranch().getId());
                }
                markService.save(auth.getBranch().getId(), mark, red);
            });
            red.addFlashAttribute("message", "Marks saved successfully.");
        } else {
            red.addFlashAttribute("error", "Marks empty or exam closed.");
        }
        return "redirect:/marks?exam=" + exam + "&grade=" + "&subject=" + subject + "&success";
    }

    @GetMapping("/report-cards")
    public String reportCard(@RequestParam(required = false) Long exam,
            @RequestParam(required = false) Long grade,
            @RequestParam(required = false) Long student,
            Model model, @AuthenticationPrincipal CustomUserDetails auth) {

        userService.getPrices(auth, model);
        model.addAttribute("exams", examService.findByBranch(auth.getBranch().getId()));
        model.addAttribute("grades", gradeService.findAll());

        if (grade != null) {
            model.addAttribute("students", studentService.findByBranchAndGrade(auth.getBranch().getId(), grade));
        }

        if (student != null && exam != null) {
            Student s = studentService.findById(student);
            Exam e = examService.findById(exam);
            Grade g = gradeService.findById(grade);

            List<Marks> marks = markService.findByStudentAndExam(student, exam);
            ReportCardDTO dto = buildReportCard(student, exam);
            Map<Long, Subject> ject = new HashMap<>();
            for (Marks m : marks) {
                ject.put(m.getSubject(), subjectService.findById(m.getSubject()));
            }

            model.addAttribute("reportCard", dto);
            model.addAttribute("student", s);
            model.addAttribute("grade", g);
            model.addAttribute("subject", ject);
            //
            model.addAttribute("exam", e.getName() + " - Term " + e.getTerm().getT() + ", " + e.getTerm().getY());
            model.addAttribute("marks", marks);
            model.addAttribute("totalInClass", studentService.countByGrade(auth.getBranch().getId(), grade));
            model.addAttribute("totalScore", marks.stream().mapToInt(Marks::getScore).sum());
            model.addAttribute("totalMarks", marks.size() * 100);

            model.addAttribute("totalPoints", marks.stream().mapToInt(Marks::getPoints).sum());
            model.addAttribute("overPoints", marks.size() * 8);

            model.addAttribute("averageScore",
                    String.format("%.1f%%", marks.stream().mapToInt(Marks::getScore).average().orElse(0)));
            model.addAttribute("averageLevel",
                    getLevel((int) marks.stream().mapToInt(Marks::getScore).average().orElse(0))); // Calculate
            model.addAttribute("averagePoints", marks.stream().mapToInt(Marks::getPoints).sum() / (double) marks.size()); // Calculate

        }

        model.addAttribute("selectedExam", exam);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedStudent", student);

        return "student/report-card";
    }

    @GetMapping("/bulk-reports")
    public String reportCards(
            @RequestParam(required = false) Long exam,
            @RequestParam(required = false) Long grade,
            @RequestParam(required = false) Long student,
            Model model, @AuthenticationPrincipal CustomUserDetails auth) {

        userService.getPrices(auth, model);
        model.addAttribute("exams", examService.findByBranch(auth.getBranch().getId()));
        model.addAttribute("grades", gradeService.findAll());

        if (grade != null) {
            model.addAttribute("students", studentService.findByBranchAndGrade(auth.getBranch().getId(), grade));
        }

        if (student != null && exam != null) {
            ReportCardDTO dto = buildReportCard(student, exam);
            model.addAttribute("reportCard", dto);
        }

        model.addAttribute("selectedExam", exam);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("selectedStudent", student);
        return "principal/report-cards";
    }

    private ReportCardDTO buildReportCard(Long student, Long exam) {
        Student s = studentService.findById(student);
        List<Marks> marks = markService.findByStudentAndExam(student, exam);

        ReportCardDTO dto = new ReportCardDTO();
        dto.setStudentName(s.getFirstName() + ' ' + s.getOtherName() + ' ' + s.getLastName());
        dto.setAdmNo(s.getAdmissionNumber());
        dto.setGrade(gradeService.findById(s.getGrade()).getName());
        dto.setMarks(marks.stream().map(this::toDTO).toList());
        dto.setTotal(marks.stream().mapToInt(Marks::getScore).sum());
        dto.setMaxTotal(marks.size() * 100);
        dto.setAverage(dto.getMaxTotal() > 0 ? (dto.getTotal() * 100.0 / dto.getMaxTotal()) : 0);
        return dto;
    }

    private MarkDto toDTO(Marks m) {
        MarkDto d = new MarkDto();
        d.setStudent(m.getStudent());
        d.setSubject(m.getSubject());
        d.setBranch(m.getBranch());
        d.setScore(m.getScore());
        d.setLevel(m.getLevel());
        d.setPoints(m.getPoints());
        d.setRemark(m.getRemark());
        return d;
    }

    private String getLevel(int score) {
        if (score >= 90)
            return "EE2";
        if (score >= 75)
            return "EE1";
        if (score >= 59)
            return "ME2";
        if (score >= 40)
            return "ME1";
        if (score >= 31)
            return "AE2";
        if (score >= 21)
            return "AE1";
        if (score >= 11)
            return "BE2";
        return "BE1";
    }

    private String getRemark(int score) {
        if (score >= 80)
            return "Excellent";
        if (score >= 70)
            return "Very Good";
        if (score >= 60)
            return "Good";
        if (score >= 50)
            return "Fair";
        return "Needs Improvement";
    }

    private int getPoints(int score) {
        if (score >= 90)
            return 8;
        if (score >= 75)
            return 7;
        if (score >= 59)
            return 6;
        if (score >= 40)
            return 5;
        if (score >= 31)
            return 4;
        if (score >= 21)
            return 3;
        if (score >= 11)
            return 2;
        return 1;
    }
}
