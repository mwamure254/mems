package com.mfano.mems.auth.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.dtos.UserDto;
import com.mfano.mems.auth.models.User;
import com.mfano.mems.auth.models.Role;
import com.mfano.mems.auth.models.VerificationToken;
import com.mfano.mems.auth.repositories.TokenRepository;
import com.mfano.mems.auth.repositories.UserRepository;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.utils.mail.MailService;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final ProfileService profileService;
    private final BranchService branchService;
    private final RoleService roleService;

    private final AuditService auditService;
    private final TokenRepository tokenRepository;
    private final MailService emailService;
    // private final DashboardAnalytics analyticsService;

    @Value("${app.base-url}")
    private String appBaseUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BranchService branchService,
            RoleService roleService, TokenRepository tokenRepository, MailService emailService,
            AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.branchService = branchService;
        this.roleService = roleService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    @Transactional
    public void registerUser(UserDto userDto, RedirectAttributes red) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            red.addFlashAttribute("error", "Email already in use");
        } else if (userDto.getRole() == null) {
            red.addFlashAttribute("error", "At least one role is required");
        } else if (userDto.getBranch() == null) {
            red.addFlashAttribute("error", "Branch can not be empty.");
        } else {
            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setFin(userDto.getFin());
            user.setLan(userDto.getLan());
            user.setImage(userDto.getImage());
            user.setGender(userDto.getGender());
            user.setRoles(Set.of(roleService.findById(userDto.getRole())));
            user.setBranch(branchService.findById(userDto.getBranch()));
            userRepository.save(user);
            // createAndSendToken(user);

            auditService.record(user.getBranch().getId(), "create_user", "success",
                    "Created user: " + userDto.getEmail());
            red.addFlashAttribute("message", "User created successfully!");
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void changePassword(CustomUserDetails auth, String password, RedirectAttributes red) {

        User user = findByEmail(auth.getUsername());
        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            auditService.record(user.getBranch().getId(), "reset_password", "SUCCESS",
                    "user " + user.getEmail() + "Reset password");
            red.addFlashAttribute("message", "Password reset successful");
        }
    }

    public void update(CustomUserDetails auth, Long id, UserDto userDto, RedirectAttributes red) {
        User existing = findById(id);

        if (existing != null) {
            existing.setFin(userDto.getFin());
            existing.setLan(userDto.getLan());
            existing.setGender(userDto.getGender());
            existing.setImage(userDto.getImage());

            if (userDto.getPassword() != null) {
                existing.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            existing.setBranch(branchService.findById(userDto.getBranch()));
            existing.getRoles().add(roleService.findById(userDto.getRole()));
            userRepository.save(existing);

            auditService.record(existing.getBranch().getId(), "update_user", "success",
                    "Created user: " + existing.getEmail());
            red.addFlashAttribute("message", "User updated successfully!");
        } else {
            red.addFlashAttribute("error", "User not found.");

        }
    }

    public void deleteById(Long id, RedirectAttributes red) {
        if (findById(id) != null) {
            userRepository.deleteById(id);
            auditService.record(findById(id).getBranch().getId(), "delete_user", "success", "Deleted user id = " + id);
            red.addFlashAttribute("message", "user successfully deleted");
        } else {
            red.addFlashAttribute("error", "User not found");
        }
    }

    public void toggleActive(Long id, RedirectAttributes red) {
        User existing = findById(id);
        if (existing != null) {
            existing.setActive(!Boolean.TRUE.equals(existing.isActive()));
            userRepository.save(existing);
            if (existing.isActive() == true) {
                auditService.record(existing.getBranch().getId(), "toggle_user", "SUCCESS",
                        "activate user " + existing.getEmail());
                red.addFlashAttribute(
                        "message",
                        "User activated successfully.");
            } else {
                auditService.record(existing.getBranch().getId(), "toggle_user", "SUCCESS",
                        "deactivate user " + existing.getEmail());
                red.addFlashAttribute(
                        "message",
                        "User deactivated successfully.");
            }
        } else {
            red.addFlashAttribute(
                    "error",
                    "User not found.");
        }
    }

    // Get User By Id
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByBranch_Id(Long storeId) {
        return userRepository.findByBranch_Id(storeId);
    }

    public void createAndSendToken(User user, RedirectAttributes red) {
        if (user != null) {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(24);
            VerificationToken vt = new VerificationToken();
            vt.setToken(token);
            vt.setExpiryDate(expiry);
            vt.setUser(user);
            tokenRepository.save(vt);
            String link = appBaseUrl + "/verify?token=" + token;
            String subject = "Please verify your email";
            String body = "Hi " + (user.getEmail() == null ? "" : user.getEmail()) + "\n\n"
                    + "Please click the link to verify your email:\n" + link + "\n\n"
                    + "This link will expire in 24 hours.\n\nThanks!";
            emailService.sendSimpleMessage(user.getEmail(), subject, body, user.getBranch().getId());

            red.addFlashAttribute("message", "Link resent to user");
        } else {
            red.addFlashAttribute("error", "User is not valid");
        }
    }

    public String validateVerificationToken(String token) {
        Optional<VerificationToken> opt = tokenRepository.findByToken(token);
        if (opt.isEmpty())
            return "invalid";
        VerificationToken vt = opt.get();
        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "expired";
        }
        User user = vt.getUser();
        user.setActive(true);
        userRepository.save(user);
        tokenRepository.delete(vt);
        return "valid";
    }

    // Password reset flow
    public void createPasswordResetToken(String email, RedirectAttributes red) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            red.addFlashAttribute("error", "No user with the email provided");
        } else {
            tokenRepository.deleteByUserId(user.getId());
            String token = UUID.randomUUID().toString();
            VerificationToken prt = new VerificationToken();
            prt.setToken(token);
            prt.setExpiryDate(LocalDateTime.now().plusHours(2));
            prt.setUser(user);
            tokenRepository.save(prt);
            String link = appBaseUrl + "/reset-password?token=" + token;
            String subject = "Password reset request";
            String body = "Hi " + (user.getEmail() == null ? "" : user.getEmail()) + "\n\n"
                    + "Click the link to reset your password: \n" + link + "\n\n"
                    + "This link expires in 2 hours.\n\nIf you did not request this, ignore this email.";
            emailService.sendSimpleMessage(user.getEmail(), subject, body, user.getBranch().getId());
            red.addFlashAttribute("message", "Check your email, a reset link was sent.");
        }
    }

    public String validatePasswordResetToken(String token) {
        var opt = tokenRepository.findByToken(token);
        if (opt.isEmpty())
            return "invalid";
        var prt = opt.get();
        if (prt.getExpiryDate().isBefore(LocalDateTime.now()))
            return "expired";
        return "valid";
    }

    public Optional<User> getUserByPasswordResetToken(String token) {
        return tokenRepository.findByToken(token).map(VerificationToken::getUser);
    }

    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.deleteByUserId(user.getId());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void assignRoleToUser(Long userId, Long roleId, RedirectAttributes red) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().add(role);
            userRepository.save(user);
            auditService.record(user.getBranch().getId(), "update_user",
                    "success", "Assigned user id =" + user.getEmail() + " role " + role.getName());
            red.addFlashAttribute("message", "Role assigned successful");
        } else {
            red.addFlashAttribute("error", "Action failed, please try again later.");
        }
    }

    public void removeRoleFromUser(Long userId, Long roleId, RedirectAttributes red) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        if (user != null && role != null) {
            user.getRoles().removeIf(r -> r.getId().equals(role.getId()));
            userRepository.save(user);
            auditService.record(user.getBranch().getId(), "update_user",
                    "SUCCESS", "Revoked role " + role.getName() + " from user " + user.getEmail());
            red.addFlashAttribute("message", "Role revoked successful");
        } else {
            red.addFlashAttribute("error", "Action failed, please try again later.");
        }

    }

    // Expose user and profile
    public Model getPrices(CustomUserDetails auth, Model red) {
        red.addAttribute("logActivities", auditService.findByActionContaining("USER_LOG"));

        red.addAttribute("author", findByEmail(auth.getUsername()));

        return red;
    }
}
