package com.mfano.mems.services;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Book;
import com.mfano.mems.repositories.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final CategoryService categoryService;
    private final BookRepository bookRepository;
    private final AuditService auditService;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findByBranchAndBorrowed(Long id){
        return findByBranch(id).stream()
        .filter(entry -> entry.isActive() == true && entry.isBorrowed() != true)
        .toList();
    }

    public List<Book> findByBranchAndCategory(Long id, Long filter) {
        return bookRepository.findByBranchAndCategory(id, categoryService.findById(filter));
    }
    
    public List<Book> findAllByName(String key) {
        return bookRepository.findByNameContaining(key);
    }

    public List<Book> findByBranch(Long b) {
        return bookRepository.findByBranch(b);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void save(Long branch, Book entity, RedirectAttributes red) {
        if (entity != null) {
            entity.setActive(true);
            entity.setBorrowed(false);
            bookRepository.save(entity);
            auditService.record(branch, "CREATE_BOOK", "SUCCESS", "Created book: " + entity.getName());
            red.addFlashAttribute("message", "Book created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid book object.");
        }
    }

    public void update(Long id, Book entity, RedirectAttributes redirectAttributes, Long branch) {
        Book existing = findById(id);
        if (entity != null) {
            existing.setPublisher(entity.getPublisher());
            existing.setSerial(entity.getSerial());
            existing.setName(entity.getName());
            existing.setDescription(entity.getDescription());
            bookRepository.save(existing);
            auditService.record(branch, "UPDATE_BOOK", "SUCCESS", "Updated book : " + entity.getName());
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Book updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Book not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Book existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            bookRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "activate_book", "SUCCESS", "activate book " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Book activated successfully.");
            } else {
                auditService.record(branch, "deactivate_book", "SUCCESS",
                        "activate book " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Book deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Book not found.");
        }
    }

    public void delete(Long id, RedirectAttributes red, Long branch) {
        Book existing = findById(id);
        if (existing != null) {
            bookRepository.deleteById(id);
            auditService.record(branch, "delete_book", "SUCCESS", "Deleted book id =" + id);

            red.addFlashAttribute(
                    "message",
                    "Book deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Book not found.");
        }
    }
}
