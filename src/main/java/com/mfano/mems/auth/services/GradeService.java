package com.mfano.mems.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Grade;
import com.mfano.mems.auth.repositories.GradeRepository;
import com.mfano.mems.dtos.EntityDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final AuditService auditService;

    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }

    public Grade findById(Long id) {
        return gradeRepository.findById(id).orElse(null);
    }

    public void save(EntityDto grade, RedirectAttributes red, Long branch) {
        Grade g = new Grade();
        if (grade != null) {
            g.setName(grade.getEntyName());
            g.setDescription(grade.getEntyDesc());
            gradeRepository.save(g);
            auditService.record(branch, "CREATE_GRADE", "SUCCESS", "Created grade: " + g.getName());
            red.addFlashAttribute("message", "Grade created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid grade object.");
        }
    }

    public void update(Long id, EntityDto entity, RedirectAttributes redirectAttributes, Long branch) {
        Grade g = findById(id);
        if (entity != null) {
            g.setName(entity.getEntyName());
            g.setDescription(entity.getEntyDesc());
            gradeRepository.save(g);
            auditService.record(branch, "UPDATE_GRADE", "SUCCESS", "Updated grade: " + g.getName());
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Grade updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Grade not found.");
        }
    }

    // Delete Role
    public void delete(Long id, RedirectAttributes red, Long branch) {
        Grade existing = findById(id);
        if (existing != null) {
            gradeRepository.deleteById(id);
            auditService.record(branch, "delete_grade", "SUCCESS", "Deleted grade id=" + id);

            red.addFlashAttribute(
                    "message",
                    "grade deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Grade not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Grade existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            gradeRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "activate_grade", "SUCCESS", "activate grade " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Grade activated successfully.");
            } else {
                auditService.record(branch, "deactivate_grade", "SUCCESS", "activate grade " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Grade deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Grade not found.");
        }
    }

}
