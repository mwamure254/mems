package com.mfano.mems.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.dtos.EntityDto;
import com.mfano.mems.models.Category;
import com.mfano.mems.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AuditService auditService;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public List<Category> findByBranch(Long b){
        return categoryRepository.findByBranch(b);
    }
    
    public List<Category> findByName(String key){
        return categoryRepository.findByNameContaining(key);
    }

    public Category findById(Long id){
        return categoryRepository.findById(id).orElse(null);
    }

   public String save(Long branch, Category ent, RedirectAttributes red){
         if (ent != null) {
            categoryRepository.save(ent);
            auditService.record(branch, "CREATE_CATEGORY", "SUCCESS", "Created category: " + ent.getName());
            red.addFlashAttribute("message", "Category created successfully!");
        } else {
            red.addFlashAttribute("error", "Invalid category object.");
        }
        return "redirect:/library";
    }

    public void update(Long id, Category entity, RedirectAttributes redirectAttributes, Long branch) {
        Category cat = findById(id);
        if (entity != null) {
            cat.setName(entity.getName());
            cat.setDescription(entity.getDescription());
            categoryRepository.save(cat);
            auditService.record(branch, "UPDATE_CATEGORY", "SUCCESS", "Updated category : " + cat.getName());
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Category updated successfully.");
        } else {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Category not found.");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red, Long branch) {
        Category existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            categoryRepository.save(existing);

            if (existing.isActive() == true) {
                auditService.record(branch, "activate_category", "SUCCESS", "activate cateogry " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "Category activated successfully.");
            } else {
                auditService.record(branch, "deactivate_category", "SUCCESS", "activate category " + existing.getName());
                red.addFlashAttribute(
                        "message",
                        "category deactivated successfully.");
            }
        } else {

            red.addFlashAttribute(
                    "error",
                    "Category not found.");
        }
    }

    public String delete(Long id, RedirectAttributes red, Long branch){
         Category existing = findById(id);
        if (existing != null) {
            categoryRepository.deleteById(id);
            auditService.record(branch, "delete_category", "SUCCESS", "Deleted category id =" + id);

            red.addFlashAttribute(
                    "message",
                    "Category deleted successfully.");
        } else {
            red.addFlashAttribute(
                    "error",
                    "Category not found.");
        }
        categoryRepository.deleteById(id);
        return "redirect:/library";
    }
}
