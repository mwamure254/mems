package com.mfano.mems.services;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Student;
import com.mfano.mems.repositories.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findByBranch(Long id) {
        return studentRepository.findByBranch(id);
    }

    public List<Student> findByBranchAndGrade(Long id, Long grade) {
        return studentRepository.findByBranch(id).stream()
                .filter(entry -> entry.getGrade() == grade)
                .toList();
    }

    public List<Student> findByBranchActivStudent(Long id) {
        return studentRepository.findByBranch(id).stream()
                .filter(entry -> entry.isActive() == true)
                .toList();
    }

    public List<Student> findByGrade(Long id) {
        return studentRepository.findByGrade(id);
    }

    public List<Student> findByGradeActive(Long id) {
        return studentRepository.findByGrade(id).stream()
                .filter(entry -> entry.isActive() == true)
                .toList();
    }

    public Integer countByGrade(Long branch, Long grade) {
        return studentRepository.countByBranchAndGrade(branch, grade);
    }

    public List<Student> findByBranchAndActive(Long id) {
        return studentRepository.findByBranchAndActive(id, true);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public void save(Long auth, Student entity, RedirectAttributes red) {
        if (entity != null) {
            entity.setActive(true);
            studentRepository.save(entity);
            auditService.record(auth, "CREATE_STUDENT", "SUCCESS", "Created student");
            red.addFlashAttribute("message", "Student saved successfully.");
        } else {
            red.addFlashAttribute("error", "Something went wrong, student could not save.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes redirectAttributes, Long branch) {
        Student existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            studentRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "toggle_role", "SUCCESS",
                        "Activate Student " + existing.getAdmissionNumber());
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Student activated successfully.");
            } else {
                auditService.record(branch, "toggle_student", "SUCCESS",
                        "Deactivate student " + existing.getAdmissionNumber());
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Student deactivated successfully.");
            }
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Student not found.");
        }
    }

    public void update(Long id, Student entity, RedirectAttributes redirectAttributes, Long branch) {
        Student existing = findById(id);
        if (existing != null) {
            existing.setFirstName(entity.getFirstName());
            existing.setLastName(entity.getLastName());
            existing.setOtherName(entity.getOtherName());
            existing.setDob(entity.getDob());
            existing.setUpload(entity.getUpload());
            existing.setParentName(entity.getParentName());
            existing.setParentPhone(entity.getParentPhone());
            existing.setAdmissionNumber(entity.getAdmissionNumber());
            existing.setGender(entity.getGender());
            existing.setGrade(entity.getGrade());
            existing.setStream(entity.getStream());
            existing.setStatus(entity.getStatus());

            studentRepository.save(existing);
            auditService.record(branch, "UPDATE_STUDENT", "SUCCESS", "Updated student");
            redirectAttributes.addFlashAttribute("message", "Student updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No student found to update .");
        }
    }

    public void delete(Long auth, Long id, RedirectAttributes red) {
        Student existing = findById(id);
        if (existing != null) {
            studentRepository.delete(existing);
            auditService.record(auth, "DELETE_STUDENT", "SUCCESS", "Deleted student");
            red.addFlashAttribute("message", "Student deleted successfully.");
        } else {
            red.addFlashAttribute("error", "No student found to delete .");
        }
    }

    public void transfer(Long branch, Long student, RedirectAttributes red) {
        
    }

}
