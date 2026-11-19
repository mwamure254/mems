package com.mfano.mems.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Term;
import com.mfano.mems.auth.repositories.TermRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermService {
    private final TermRepository termRepository;
    private final AuditService auditService;

    public List<Term> findAll() {
        return termRepository.findAllByOrderByCreatedAtDesc();
    }

    public Term findById(Long id) {
        return termRepository.findById(id).orElse(null);
    }

    public List<Term> findActive() {
        return findAll().stream()
                .filter(entry -> entry.isActive() == true)
                .toList();
    }

    @Transactional
    public void save(Term term, RedirectAttributes red, Long branch) {
        if (term != null) {
            for (Term t : findActive()) {
                t.setActive(false);
                termRepository.save(term);
            }
            term.setActive(true);
            termRepository.save(term);
            auditService.record(branch, "Create_term", "SUCCESS", "Term " + term.getT() + ", " + term.getY());
            red.addFlashAttribute(
                    "message",
                    "Session created successfully.");
        } else {

            red.addFlashAttribute(
                    "error",
                    "Session not found.");
        }
    }

    public void update(Long id, Term term, RedirectAttributes redirectAttributes, Long branch) {
        Term t = findById(id);
        if (t != null) {
            t.setT(t.getT());
            t.setY(t.getY());
            t.setComments(t.getComments());
            termRepository.save(t);
            auditService.record(branch, "UPDATE_SESSION", "SUCCESS", "Updated session id: " + id);
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Session updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Session not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes redirectAttributes, Long branch) {
        Term existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            termRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "toggle_session", "SUCCESS", "Dactivate session id: " + id);
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Session activated successfully.");
            } else {
                auditService.record(branch, "toggle_session", "SUCCESS", "Dactivate session id: " + id);
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Session deactivated successfully.");
            }
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Session not found.");
        }
    }

    public void delete(Long id, RedirectAttributes redirectAttributes, Long branch) {
       Term existing = findById(id);
        if (existing != null) {
            termRepository.delete(existing);

                auditService.record(branch, "delete_session", "SUCCESS", "Deleted session id: " + id);
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Session deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Session not found.");
        }
    }

}
