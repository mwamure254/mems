package com.mfano.mems.services;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Staff;
import com.mfano.mems.repositories.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;
    private final AuditService auditService;

    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    public List<Staff> active(Long id){
        return findByBranch(id).stream()
        .filter(entry -> entry.isActive() == true)
        .toList();
    }
    
    public List<Staff> findAllByKey(String key) {
        return staffRepository.findByReferenceContaining(key);
    }

    public List<Staff> findByBranch(Long b) {
        return staffRepository.findByBranch(b);
    }

    public Staff findById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    public void save(Long branch, Staff entity, RedirectAttributes red) {
        if (entity != null) {
            staffRepository.save(entity);
            auditService.record(branch, "CREATE_STAFF", "SUCCESS", "Created staff: " + entity.getEmail());
            red.addFlashAttribute("message", "Staff created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid staff object.");
        }
    }

    public void update(Long id, Staff entity, RedirectAttributes redirectAttributes, Long branch) {
        Staff existing = findById(id);
        if (entity != null) {
            staffRepository.save(existing);
            auditService.record(branch, "UPDATE_STAFF", "SUCCESS", "Updated staff : " + entity.getEmail());
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Staff updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Staff not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Staff existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            staffRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "activate_staff", "SUCCESS", "activate staff " + existing.getEmail());
                red.addFlashAttribute(
                        "message",
                        "Staff activated successfully.");
            } else {
                auditService.record(branch, "deactivate_staff", "SUCCESS",
                        "activate staff " + existing.getEmail());
                red.addFlashAttribute(
                        "message",
                        "Staff deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Staff not found.");
        }
    }

    public void delete(Long id, RedirectAttributes red, Long branch) {
        Staff existing = findById(id);
        if (existing != null) {
            staffRepository.deleteById(id);
            auditService.record(branch, "delete_staff", "SUCCESS", "Deleted Staff id = " + id);

            red.addFlashAttribute(
                    "message",
                    "Staff deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Staff not found.");
        }
    }

    public List<Staff> findByBranchAndActive(Long id) {
        return staffRepository.findByBranchAndActive(id,true);
    }
    
}
