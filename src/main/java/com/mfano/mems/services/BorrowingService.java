package com.mfano.mems.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Borrowing;
import com.mfano.mems.repositories.BorrowingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookService bookService;
    private final AuditService auditService;

    public List<Borrowing> findAll() {
        return borrowingRepository.findAll();
    }

    public List<Borrowing> active(Long id) {
        return findByBranch(id).stream()
                .filter(entry -> entry.isActive() == true)
                .toList();
    }

    public List<Borrowing> staffBorrowings(Long id) {
        return findByBranch(id).stream()
                .filter(entry -> entry.getStaff() != null)
                .toList();
    }

    public List<Borrowing> studentBorrowings(Long id) {
        return findByBranch(id).stream()
                .filter(entry -> entry.getStudent() != null)
                .toList();
    }

    public List<Borrowing> findByBranch(Long b) {
        return borrowingRepository.findByBranch(b);
    }

    public Borrowing findById(Long id) {
        return borrowingRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Long branch, Borrowing entity, RedirectAttributes red) {
        if (entity != null) {
            entity.setActive(true);
            borrowingRepository.save(entity);
            auditService.record(branch, "CREATE_BORROWING", "SUCCESS", "Created borrowed: " + entity.getBook());
            bookService.findById(entity.getBook()).setBorrowed(true);
            red.addFlashAttribute("message", "Book borrowed successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid book object.");
        }
    }

    @Transactional
    public void update(Long id, Borrowing entity, RedirectAttributes redirectAttributes, Long branch) {
        Borrowing existing = findById(id);
        if (entity != null) {
            existing.setActive(false);
            borrowingRepository.save(existing);
            auditService.record(branch, "UPDATE_BORROWING", "SUCCESS", "Updated borrowing : " + entity.getId());
            bookService.findById(entity.getBook()).setBorrowed(false);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Record updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Record not found.");
        }
    }
}
