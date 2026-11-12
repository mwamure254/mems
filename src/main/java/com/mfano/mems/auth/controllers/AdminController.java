package com.mfano.mems.auth.controllers;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.dtos.UserDto;
import com.mfano.mems.utils.documents.FileUpload;
import com.mfano.mems.auth.services.BranchService;
import com.mfano.mems.auth.services.GradeService;
import com.mfano.mems.auth.services.ProfileService;
import com.mfano.mems.auth.models.Branch;
import com.mfano.mems.auth.models.Grade;
import com.mfano.mems.auth.models.Profile;
import com.mfano.mems.auth.models.Role;
import com.mfano.mems.auth.models.Term;
import com.mfano.mems.auth.models.User;
import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.auth.services.RoleService;
import com.mfano.mems.auth.services.TermService;
import com.mfano.mems.auth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final BranchService storeService;
    private final ProfileService profileService;
    private final FileUpload fileUpload;
    private final GradeService gradeService;
    private final TermService termService;

    // manage /GET/* module
    @GetMapping("/{option}")
    public String manage(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (option) {
            case "dashboard":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("audits", auditService.findAll());
                dir = "admin/index";
                break;

            case "users":

                red.addAttribute("userDto", new UserDto());
                Long storeId = auth.getUser().getBranch().getId();

                if (storeId != null) {
                    // red.addAttribute("users", userService.findByBranch_Id(storeId));
                    red.addAttribute("users", userService.findAll());

                } else {
                    red.addAttribute("users", userService.findAll());
                }
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                dir = "admin/users";
                break;

            case "roles":
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("stores", storeService.findAll());
                dir = "admin/roles";
                break;

            case "audits":
                red.addAttribute("auditEntries", auditService.findAll());
                dir = "admin/audits";
                break;

            case "administration":
                red.addAttribute("branches", storeService.findAll());
                red.addAttribute("grades", gradeService.findAll());
                red.addAttribute("terms", termService.findAll());
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("branch", new Branch());
                red.addAttribute("grade", new Grade());
                red.addAttribute("term", new Term());
                dir = "security/administration";
                break;
        }

        return dir;
    }

    @PostMapping("/branches/save")
    public String save(
            @ModelAttribute Branch branch, @AuthenticationPrincipal CustomUserDetails auth, 
            RedirectAttributes redirectAttributes) {
        try {
            storeService.createBranch(branch, auth.getBranch().getId(), redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/administration";
    }

    @PostMapping("/branches/update/{id}")
    public String update(
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth, 
            @ModelAttribute Branch branch,
            RedirectAttributes redirectAttributes) {
        try {
            storeService.updateBranch(auth.getBranch().getId(), id, branch, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/branches";
    }

    @PostMapping("/branches/toggle/{id}")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            storeService.toggleActive(id, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/administration";
    }

    @PostMapping("/branches/delete/{id}")
    public String delete(@AuthenticationPrincipal CustomUserDetails auth, 
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            storeService.deleteBranch(auth.getBranch().getId(), id, redirectAttributes);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/administration";
    }

    // Role saving
    @PostMapping("/roles/save")
    public String saveRole(@ModelAttribute Role role,
            @AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes red) {
        try {
            roleService.save(role, red, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            red.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/roles";
    }

    // Role update
    @PostMapping("/roles/update/{id}")
    public String updateRole(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long id,
            @Valid Role role,
            RedirectAttributes redirectAttributes) {
        try {
            roleService.update(id, role, redirectAttributes, auth.getBranch().getId());

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Operation not successful.");
        }

        return "redirect:/admin/roles";
    }

    // manage /roles/* module
    @PostMapping("/roles/{option}/{id}")
    public String manage(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            RedirectAttributes redirectAttributes) {

        String dir = "redirect";
        switch (option) {
            case "delete":
                try {
                    roleService.delete(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                dir = "redirect:/admin/roles";
                break;

            case "toggle":
                try {
                    roleService.toggleActive(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                dir = "redirect:/admin/roles";
                break;
        }

        return dir;
    }

    // save user
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute UserDto userDto, @RequestParam("file") MultipartFile file,
            RedirectAttributes red) {
        try {
            if (file != null && !file.isEmpty()) {
                userDto.setImage(fileUpload.uploadFile(file, "profiles", red));
            }
            userService.registerUser(userDto, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/users";
    }

    // update user
    @PostMapping("/users/update/{id}")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long id, @RequestParam("file") MultipartFile file,
            @Valid UserDto userDto,
            RedirectAttributes red) {
        try {
            if (file != null && !file.isEmpty()) {
                userDto.setImage(fileUpload.uploadFile(file, "profiles", red));
            }
            userService.update(auth, id, userDto, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", "Operation not successful.");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/manage/{id}")
    public String manageProfile(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id, Model model) {
        User user = userService.findById(id);
        userService.getPrices(auth, model);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", roleService.getUserRoles(user));
        model.addAttribute("userNotRoles", roleService.getUserNotRoles(user));
        return "admin/user-roles";
    }

    // manage /user route
    @PostMapping("/users/{option}/{id}")
    public String manageGetter(@PathVariable String option,
            @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails auth,
            Model model, RedirectAttributes red) {

        User user = userService.findById(id);

        String dir = "redirect";
        switch (option) {

            case "profile":
                // Add user info to model (for Thymeleaf dashboard pages)
                userService.getPrices(auth, model);
                model.addAttribute("user", profileService.checkProfile(auth.getId()));
                dir = "admin/user-profile";
                break;

            // Resend verification link
            case "resend":
                if (user != null && !user.isActive()) {
                    userService.createAndSendToken(user, red);
                    dir = "redirect:/admin/users";
                } else {
                    red.addFlashAttribute("error", "Sorry! Failed to resend link.");
                }
                dir = "redirect:/admin/users";
                break;

            // Delete user
            case "delete":
                try {
                    userService.deleteById(id, red);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addFlashAttribute("error", "Sorry! Failed to delete user");
                }
                dir = "redirect:/admin/users";
                break;

            // Toggle user
            case "toggle":
                try {
                    userService.toggleActive(id, red);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addFlashAttribute("error", "Sorry! Failed to toggle user.");
                }
                dir = "redirect:/admin/users";
                break;
        }

        return dir;
    }

    // profile/update @PreAuthorize("isAuthenticated()")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update/{id}")
    public String userProfileUpdate(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            @ModelAttribute("profile") Profile profile, RedirectAttributes red) {
        try {
            profileService.update(id, profile, red, auth.getBranch().getId());
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", "Operation not successful.");
        }
        return "redirect:/admin/users";
    }

    // Update profile image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/update/{user}")
    public String imageUpdate(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long user,
            @RequestParam("image") MultipartFile file, RedirectAttributes red) {
        try {
            String image = "";
            if (file != null && !file.isEmpty()) {
                image = fileUpload.uploadFile(file, "profiles", red);
            }
            profileService.updateProfileImage(user, image, red);
            auditService.record(auth.getBranch().getId(), "update_image", "success",
                    auth.getUsername() + " Updated the profile image  of user id = " + user);
        } catch (IOException e) {
            e.printStackTrace();
            red.addFlashAttribute("error", "Operation not successful.");
        }

        return "redirect:/admin/users";
    }

    // Delete profile image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/delete/{user}")
    public String imageDelete(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long user,
            RedirectAttributes red) {

        try {
            profileService.deleteProfileImage(user, red);
            auditService.record(auth.getBranch().getId(), "delete_image", "success",
                    auth.getUsername() + " Deleted the profile image of user id = " + user);
        } catch (IOException e) {
            e.printStackTrace();
            red.addFlashAttribute("error", "operation not successful.");
        }

        return "redirect:/admin/users";
    }

    // Assign Remove roles
    @PostMapping("/{option}/{userId}/{roleId}")
    public String userRole(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long userId,
            @PathVariable String option,
            @PathVariable Long roleId, RedirectAttributes red) {

        switch (option) {
            case "assign-role":
                try {
                    userService.assignRoleToUser(userId, roleId, red);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addFlashAttribute("error", "Operation not successful.");
                }
                break;

            case "remove-role":
                try {
                    userService.removeRoleFromUser(userId, roleId, red);
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addFlashAttribute("error", "Operation not successful.");
                }
                break;

            default:
                red.addFlashAttribute("error", "Action failed");
                break;
        }

        return "redirect:/admin/manage/{userId}";
    }
}