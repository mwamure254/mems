package com.mfano.mems.controllers;

import com.mfano.mems.services.StaffService;
import java.util.HashMap;
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
import com.mfano.mems.models.Book;
import com.mfano.mems.models.Borrowing;
import com.mfano.mems.models.Staff;
import com.mfano.mems.models.Student;
import com.mfano.mems.services.BookService;
import com.mfano.mems.services.BorrowingService;
import com.mfano.mems.services.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/borrowings")
@RequiredArgsConstructor
public class BorrowingController {
    private final StaffService staffService;
    private final BorrowingService borrowingService;
    private final BookService bookService;
    private final UserService userService;
    private final StudentService studentService;

    @GetMapping("/{borrower}")
    public String borrowing(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable String borrower, Model model, RedirectAttributes red) {
        userService.getPrices(auth, model);
        Map<Long, Student> student = new HashMap<>();
        Map<Long, Staff> staff = new HashMap<>();
        Map<Long, Book> borrowed = new HashMap<>();

        model.addAttribute("books", bookService.findByBranchAndBorrowed(auth.getBranch().getId()));

        String dir = "";
        switch (borrower) {
            case "student":
                for (Borrowing br : borrowingService.findByBranch(auth.getBranch().getId())) {
                    if (br.getBook() != null) {
                        borrowed.put(br.getBook(), bookService.findById(br.getBook()));
                    }
                    if (br.getStudent() != null) {
                        student.put(br.getStudent(), studentService.findById(br.getStudent()));
                    }
                }
                try {
                    model.addAttribute("borrowings", borrowingService.studentBorrowings(auth.getBranch().getId()));
                    model.addAttribute("students", studentService.findByBranchAndActive(auth.getBranch().getId()));
                    model.addAttribute("borrower", student);
                    model.addAttribute("borrowed", borrowed);
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Operation not successful.");
                }
                dir = "library/student-books";
                break;

            case "staff":
                try {
                    model.addAttribute("borrowings", borrowingService.staffBorrowings(auth.getBranch().getId()));
                    model.addAttribute("staff", staffService.findByBranchAndActive(auth.getBranch().getId()));
                    model.addAttribute("borrower", staff);
                    model.addAttribute("borrowed", borrowed);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addAttribute("error", "Operation not successful.");
                }
                dir = "library/staff-books";
                break;
            default:
                red.addAttribute("error", "Operation not successful.");
                dir = "library/books";
                break;
        }
        return dir;
    }

    @PostMapping("/student/issue")
    public String saveStudentBorrowing(@AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Borrowing entity,
            RedirectAttributes red) {
        try {
            borrowingService.save(auth.getBranch().getId(), entity, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/borrowings/student";
    }

    @PostMapping("/staff/issue")
    public String saveStaffBorrowing(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Borrowing entity,
            RedirectAttributes red) {
        try {
            borrowingService.save(auth.getBranch().getId(), entity, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/borrowings/staff";
    }

    @PostMapping("/student/return/{id}")
    public String returnStudentBook(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            RedirectAttributes red) {
        Borrowing br = borrowingService.findById(id);
        if (br != null) {
            try {
                borrowingService.update(id, br, red, auth.getBranch().getId());
            } catch (Exception e) {
                e.printStackTrace();
                red.addFlashAttribute("error", e.getMessage());
            }
        }
        return "redirect:/borrowings/student";
    }

    @PostMapping("/staff/return/{id}")
    public String returnStaffBook(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            RedirectAttributes red) {
        Borrowing br = borrowingService.findById(id);
        if (br != null) {
            try {
                borrowingService.update(id, br, red, auth.getBranch().getId());
            } catch (Exception e) {
                e.printStackTrace();
                red.addFlashAttribute("error", e.getMessage());
            }
        }

        return "redirect:/borrowings/staff";
    }

}