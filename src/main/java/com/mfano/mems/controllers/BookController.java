package com.mfano.mems.controllers;

import java.util.List;

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
import com.mfano.mems.services.BookService;
import com.mfano.mems.services.CategoryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping
    public String books(@AuthenticationPrincipal CustomUserDetails auth, @RequestParam(required = false) Long filter, Model model) {
        userService.getPrices(auth, model);

        List<Book> books = (filter == null) 
            ? bookService.findByBranch(auth.getBranch().getId()) 
            : bookService.findByBranchAndCategory(auth.getBranch().getId(), filter);

        model.addAttribute("categories", categoryService.findByBranch(auth.getBranch().getId()));
        model.addAttribute("books", books);
        return "library/books";

    }

    @PostMapping("/save")
    public String saveBook(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Book entity,
            RedirectAttributes red) {
        try {
            bookService.save(auth.getBranch().getId(), entity, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/update/{id}")
    public String updateBook(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute Book entity, RedirectAttributes redirectAttributes) {
        try {
            bookService.update(id, entity, redirectAttributes, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/books";
    }

      // manage books module
    @PostMapping("/{option}/{id}")
    public String manageBooks(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes red) {

        switch (option) {
            case "delete":
               try {
            bookService.delete(id, red, auth.getBranch().getId());
        } catch (Exception e) {
            red.addFlashAttribute("error", "Something went wrong, Book could not delete.");
        }
                break;

            case "toggle":
               try {
            bookService.toggleActive(id, red, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            red.addAttribute("error", "Operation not successful.");
        }
                break;
        }

        return "redirect:/books";
    }
}
