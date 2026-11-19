package com.mfano.mems.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Branch;
import com.mfano.mems.auth.repositories.BranchRepository;
import com.mfano.mems.config.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final AuditService auditService;

    public List<Branch> findAll() {
        return branchRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<Branch> findAll(CustomUserDetails auth) {
        return branchRepository.findAll().stream()
        .filter(entry -> entry.getId() == auth.getBranch().getId())
        .toList();
    }

    public Branch findById(Long id) {
        return branchRepository.findById(id).orElse(null);
    }

    public Branch findByName(String name) {
        return branchRepository.findByName(name);
    }

    public void createBranch(Branch branch, Long user,
            RedirectAttributes redirectAttributes) {

        if (branchRepository.findByName(branch.getName()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Branch name already exists.");

        } else {
            branchRepository.save(branch);
            auditService.record(user, "create_branch", "SUCCESS", "Created branch " + branch.getName());
            redirectAttributes.addFlashAttribute(
                    "message", "Branch created successfully.");
        }
    }

    public void deleteBranch(Long user, Long id, RedirectAttributes red) {
        Branch existingBranch = findById(id);
        if (existingBranch != null) {
            branchRepository.deleteById(id);
            auditService.record(user, "delete_branch", "SUCCESS", "Deleted branch " + existingBranch.getName());
            red.addFlashAttribute(
                    "message", "Branch deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error", "Branch not found.");
        }
    }

    public void updateBranch(Long b, Long id, Branch br, RedirectAttributes redirectAttributes) {
        Branch branch = findById(id);
        if (branch == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Branch not found: " + id);
        } else {
            branch.setName(br.getName());
            branch.setAddress(br.getAddress());
            branch.setLatitude(br.getLatitude());
            branch.setLongitude(br.getLongitude());
            branch.setDescription(br.getDescription());
            branch.setCode(br.getCode());
            branch.setPhone(br.getPhone());
            branch.setEmail(br.getEmail());
            branch.setType(br.getType());
            branch.setDocument(br.getDocument());
            branch.setManager(br.getManager());

            branchRepository.save(branch);
            auditService.record(b, "update_branch", "SUCCESS", "Updated branch " + branch.getName());

            redirectAttributes.addFlashAttribute(
                    "message", "Branch updated successfully.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes redirectAttributes) {
        Branch existing = findById(id);
        if (existing != null) {

            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            branchRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(existing.getId(), "toggle_branch", "SUCCESS",
                        "Activated branch " + existing.getName());
                redirectAttributes.addFlashAttribute(
                        "message", "Branch unlocked successfully.");
            } else {
                auditService.record(existing.getId(), "toggle_branch", "SUCCESS",
                        "Deactivated branch " + existing.getName());
                redirectAttributes.addFlashAttribute(
                        "message", "Branch locked successfully.");
            }
        } else {

            redirectAttributes.addFlashAttribute(
                    "error", "Branch not found.");
        }
    }
}
