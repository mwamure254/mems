package com.mfano.mems.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Subject;
import com.mfano.mems.auth.repositories.SubjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final AuditService auditService;

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }
    public Subject findById(Long id){
        return subjectRepository.findById(id).orElse(null);
    }
    
    public String save(Subject subject, RedirectAttributes red, Long branch){
         if (subject != null) {
            subjectRepository.save(subject);
            auditService.record(branch, "CREATE_GRADE", "SUCCESS", "Created subject: " + subject.getName());
            red.addFlashAttribute("message", "Subject created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid subject object.");
        }
        return "redirect:/admin/subjects";
    }

    public void update(Long id, Subject entity, RedirectAttributes redirectAttributes, Long branch) {
        Subject s = findById(id);
        if (entity != null) {
            s.setName(entity.getName());
            s.setCode(entity.getCode());
            s.setDescription(entity.getDescription());
            subjectRepository.save(s);
            auditService.record(branch, "UPDATE_SUBJECT", "SUCCESS", "Updated SUBJECT : " + s.getName());
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Subject updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Subject not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Subject existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            subjectRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "activate_subject", "SUCCESS", "activate subject " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Subject activated successfully.");
            } else {
                auditService.record(branch, "deactivate_subject", "SUCCESS", "activate subject " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Subject deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Subject not found.");
        }
    }

    public String delete(Long id, RedirectAttributes red, Long branch){
         Subject existing = findById(id);
        if (existing != null) {
            subjectRepository.deleteById(id);
            auditService.record(branch, "delete_subject", "SUCCESS", "Deleted subject id =" + id);

            red.addFlashAttribute(
                    "message",
                    "Subject deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Subject not found.");
        }
        subjectRepository.deleteById(id);
        return "redirect:/subjects";
    }
}
