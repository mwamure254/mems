package com.mfano.mems.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Exam;
import com.mfano.mems.repositories.ExamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final AuditService auditService;

    public List<Exam> findAll() {
        return examRepository.findAll();
    }

    public List<Exam> open(Long id){
        return findByBranch(id).stream()
        .filter(entry -> entry.isActive() == true)
        .toList();
    }
    
    public List<Exam> findAllByName(String key) {
        return examRepository.findByNameContaining(key);
    }

    public List<Exam> findByBranch(Long b) {
        return examRepository.findByBranch(b);
    }

    public Exam findById(Long id) {
        return examRepository.findById(id).orElse(null);
    }

    public String save(Long branch, Exam entity, RedirectAttributes red) {
        if (entity != null) {
            examRepository.save(entity);
            auditService.record(branch, "CREATE_EXAM", "SUCCESS", "Created exam: " + entity.getName());
            red.addFlashAttribute("message", "Exam created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid exam object.");
        }
        return "redirect:/exams";
    }

    public void update(Long id, Exam entity, RedirectAttributes redirectAttributes, Long branch) {
        Exam existing = findById(id);
        if (entity != null) {
            existing.setName(entity.getName());
            existing.setDescription(entity.getDescription());
            existing.setTerm(entity.getTerm());
            examRepository.save(existing);
            auditService.record(branch, "UPDATE_EXAM", "SUCCESS", "Updated exam : " + entity.getName());
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Exam updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Exam not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Exam existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            examRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "activate_exam", "SUCCESS", "activate exam " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Exam activated successfully.");
            } else {
                auditService.record(branch, "deactivate_eaxm", "SUCCESS",
                        "activate exam " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Exam deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Exam not found.");
        }
    }

    public String delete(Long id, RedirectAttributes red, Long branch) {
        Exam existing = findById(id);
        if (existing != null) {
            examRepository.deleteById(id);
            auditService.record(branch, "delete_exam", "SUCCESS", "Deleted exam id =" + id);

            red.addFlashAttribute(
                    "message",
                    "Exam deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Exam not found.");
        }
        examRepository.deleteById(id);
        return "redirect:/library";
    }
}
