package com.mfano.mems.auth.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Role;
import com.mfano.mems.auth.models.User;
import com.mfano.mems.auth.repositories.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    // Get All Roles
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    // Get Role By Id
    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    // Delete Role
    public void delete(Long id, RedirectAttributes red, Long branch) {
        Role existing = findById(id);
        if (existing != null) {
            roleRepository.deleteById(id);
            auditService.record(branch, "delete_role", "SUCCESS", "Deleted role id=" + id);

            red.addFlashAttribute(
                    "message",
                    "Role deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Role not found.");
        }
    }

    // Save Role
    public void save(Role role, RedirectAttributes red, Long branch) {
        if (role != null) {
            roleRepository.save(role);
            auditService.record(branch, "CREATE_ROLE", "SUCCESS", "Created role: " + role.getName());
            red.addFlashAttribute("message", "Role created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid role object.");
        }
    }

    // Update Role
    public void update(Long id, Role role, RedirectAttributes red, Long branch) {
        if (findById(id) != null) {
            roleRepository.save(role);
            auditService.record(branch, "UPDATE_ROLE", "SUCCESS", "Created role: " + role.getName());
            red.addFlashAttribute(
                    "message",
                    "Role updated successfully.");
        } else {

            red.addFlashAttribute(
                    "error",
                    "Role not found.");
        }

    }

    public List<Role> getUserNotRoles(User user) {
        return roleRepository.getUserNotRoles(user.getId());
    }

    public Set<Role> getUserRoles(User user) {
        return user.getRoles();
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Role existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            roleRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "toggle_role", "SUCCESS", "activate role " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Role activated successfully.");
            } else {
                auditService.record(branch, "toggle_role", "SUCCESS", "activate role " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Role deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Role not found.");
        }
    }
}
