package com.mfano.mems.auth.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.dtos.UserDto;
import com.mfano.mems.utils.documents.FileUpload;

import lombok.RequiredArgsConstructor;

import com.mfano.mems.auth.models.Profile;
import com.mfano.mems.auth.models.User;
import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.auth.services.ProfileService;
import com.mfano.mems.auth.services.RoleService;
import com.mfano.mems.auth.services.UserService;

@Controller
@RequiredArgsConstructor 
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final FileUpload fileUpload;
    private String login = "redirect:/login";

    // guest user
    @GetMapping("/")
    public String redirectAfterLogin(@AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes model) {

        if (auth == null) {
            return "redirect:/login";
        }

        // Extract roles
        Set<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // If user is not assigned any role
        if (roles.isEmpty()) {
            model.addFlashAttribute("error", "Please contact the system admin for mapping.");
            return login;
        } else if (!auth.isEnabled()) {
            model.addFlashAttribute("error", "Contact the system admin for account verification.");
            return login;
        }
        // Redirect based on role priority
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (roles.contains("ROLE_PRINCIPAL")) {
            return "redirect:/principal/dashboard";
        } else if (roles.contains("ROLE_TEACHER")) {
            return "redirect:/teacher/dashboard";
        } else if (roles.contains("ROLE_BURSAR")) {
            return "redirect:/bursar/dashboard";
        } else if (roles.contains("ROLE_LIBRARY")) {
            return "redirect:/library/dashboard";
        } else {
            return "redirect:/guest/dashboard";
        }

    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("userDto", new UserDto());
        return "security/register";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            RedirectAttributes model,
            Authentication authentication) {

        // If user is already logged in → redirect to dashboard
        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof CustomUserDetails) {
            return "redirect:/dashboard";
        }

        // Logout confirmation
        if (logout != null) {
            model.addFlashAttribute("message", "You have been logged out.");
        }

        return "security/login"; // Return login view
    }

    @GetMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
        if (auth == null) {
            model.addAttribute("error", "User not authenticated, login to proceed.");
            return login;
        } else {
            userService.getPrices(auth, model);
            model.addAttribute("profile", profileService.checkProfile(auth.getId()));
            return "security/profile";
        }
    }

    // profile/update @PreAuthorize("isAuthenticated()")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update")
    public String userProfileUpdate(@AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute("profile") Profile profile,
            RedirectAttributes red) {
        User user = userService.findByEmail(auth.getUsername());
        try {
            profileService.update(user.getId(), profile, red, user.getBranch().getId());
        } catch (Exception e) {
            red.addFlashAttribute("error", "Something went wrong, try again later.");
        }
        return "redirect:/profile";
    }

    // update user image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/update/{userid}")
    public String imageUpdate(@PathVariable Long userid, @RequestParam("image") MultipartFile file,
            RedirectAttributes red, @AuthenticationPrincipal CustomUserDetails user) {
        try {
            String image = "";
            if (file != null && !file.isEmpty()) {
                image = fileUpload.uploadFile(file, "profiles", red);
            }
            profileService.updateProfileImage(userid, image, red);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }
        auditService.record(user.getBranch().getId(), "update_image", "SUCCESS", "user updated their profile image");
        red.addFlashAttribute("message", "Image updated successfully.");
        return "redirect:/profile";
    }

    // delete user image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/delete/{userid}")
    public String imageDelete(@PathVariable Long userid, RedirectAttributes red,
         @AuthenticationPrincipal CustomUserDetails user) {
        try {
            profileService.deleteProfileImage(userid, red);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }
        auditService.record(user.getBranch().getId(), "delete_image", "SUCCESS", "user deleted their profile image");
        red.addFlashAttribute("message", "Image deleted successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes model) {
        model.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/login?logout";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "") String query,
            @AuthenticationPrincipal CustomUserDetails auth, Model model) {
        userService.getPrices(auth, model);
        model.addAttribute("query", query);
        // model.addAttribute("results", searchService.search(query));

        return "security/search";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token, Model model) {
        String result = userService.validateVerificationToken(token);
        if ("valid".equals(result)) {
            model.addAttribute("message", "Email verified! You can now login.");

        } else if ("expired".equals(result)) {
            model.addAttribute("error", "Token expired. Please register again.");

        } else {
            model.addAttribute("error", "Invalid token.");
        }
        return login;
    }

    @GetMapping("/resend")
    public String resendForm() {
        return "security/resend";
    }

    @PostMapping("/resend")
    public String resendSubmit(@RequestParam("email") String email, RedirectAttributes red) {
        User user = userService.findByEmail(email);
        if (user == null) {
            red.addAttribute("error", "No account with that email.");
            return "security/resend";
        } else if (user.isActive()) {
            red.addAttribute("message", "Email already verified. You can login.");
            return login;
        } else {
            userService.createAndSendToken(user, red);
            return login;
        }
    }

    // Forgot/reset endpoints
    @GetMapping("/forgot")
    public String forgotForm() {
        return "security/forgot";
    }

    @PostMapping("/forgot")
    public String forgotSubmit(@RequestParam String email, RedirectAttributes model) {
        if (userService.findByEmail(email) == null) {
            model.addFlashAttribute("error", "No account matches the email address.");
            return "redirect:/forgot";
        }

        try {
            userService.createPasswordResetToken(email, model);
        } catch (Exception e) {
            model.addFlashAttribute("error", "Something went wrong, please try again.");
            return "redirect:/forgot";
        }
        return "redirect:/login";
    }

    // self-serve password email change
    @GetMapping("/password-reset")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        String res = userService.validatePasswordResetToken(token);
        if ("valid".equals(res)) {
            model.addAttribute("token", token);
            return "security/reset-password";
        } else if ("expired".equals(res)) {
            model.addAttribute("error", "Token expired.");
            return login;
        } else {
            model.addAttribute("error", "Invalid token.");
            return login;
        }
    }

    // self-serve password change request
    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token, @RequestParam String password, Model model) {
        var optUser = userService.getUserByPasswordResetToken(token);
        if (optUser.isEmpty()) {
            model.addAttribute("error", "Invalid token.");
            return login;
        }
        userService.changePassword(optUser.get(), password);
        model.addAttribute("message", "Password changed. You can now login.");
        return login;
    }

    // logged user change password
    // Reset user password
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reset")
    public String resetPassword(@AuthenticationPrincipal CustomUserDetails auth, @RequestParam String password,
            @RequestParam String NP,
            RedirectAttributes red) {
        if (!NP.equals(password) || NP.isEmpty() || password.isEmpty()) {
            red.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/profile";
        } else {
            try {

                userService.changePassword(auth, password, red);
            } catch (Exception e) {

                red.addFlashAttribute("error", "Failed to reset password");
            }

            return "redirect:/profile";
        }
    }
}
