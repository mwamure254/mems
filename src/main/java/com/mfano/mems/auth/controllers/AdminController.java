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
import com.mfano.mems.dtos.EntityDto;
import com.mfano.mems.dtos.UserDto;
import com.mfano.mems.utils.documents.FileUpload;
import com.mfano.mems.auth.services.BranchService;
import com.mfano.mems.auth.services.ProfileService;
import com.mfano.mems.auth.models.Profile;
import com.mfano.mems.auth.models.User;
import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.auth.services.RoleService;
import com.mfano.mems.auth.services.UserService;

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

    // manage /GET/* module
    @GetMapping("/{option}")
    public String manageAdmin(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (option) {
            case "dashboard":
                dir = "admin/index";
                break;

            case "users":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("userDto", new UserDto());
                dir = "admin/users";
                break;

            case "audits":
                red.addAttribute("auditEntries", auditService.findAll());
                dir = "admin/audits";
                break;

            case "roles":
                red.addAttribute("roles", roleService.findAll());
                dir = "admin/roles";
                break;
        }

        return dir;
    }

    // Role /gRADE saving
    @PostMapping("/roles/save")
    public String saveEntity(@ModelAttribute EntityDto ent, 
            @AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes red) {
                try {
                    roleService.save(ent, red, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    red.addAttribute("error", "Operation not successful.");
                }
                return "redirect:/admin/roles";
    }

    // Role update
    @PostMapping("/roles/update/{id}")
    public String updateEntity(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long id, 
            @ModelAttribute EntityDto role,
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
    public String manageRoles(@PathVariable String option,
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
                dir = "redirect:/admin/administration";
                break;

            case "toggle":
                try {
                    roleService.toggleActive(id, redirectAttributes, auth.getBranch().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    redirectAttributes.addAttribute("error", "Operation not allowed");
                }
                dir = "redirect:/admin/administration";
                break;
        }

        return dir;
    }

    // save user
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute UserDto userDto, @RequestParam("file") MultipartFile file,
            RedirectAttributes red, @AuthenticationPrincipal CustomUserDetails auth) {
        try {
            if (file != null && !file.isEmpty()) {
                userDto.setImage(fileUpload.uploadFile(file, "profiles", red));
            }
            userService.registerUser(auth.getBranch().getId(), userDto, red);
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
            @ModelAttribute UserDto userDto, RedirectAttributes red) {
        try {
            if (file != null && !file.isEmpty()) {
                userDto.setImage(fileUpload.uploadFile(file, "profiles", red));
            }
            userService.update(auth.getBranch().getId(), id, userDto, red);
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