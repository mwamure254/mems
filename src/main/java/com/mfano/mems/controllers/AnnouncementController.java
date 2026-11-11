package com.mfano.mems.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.mfano.mems.auth.models.Role;
import com.mfano.mems.auth.models.User;
import com.mfano.mems.auth.services.RoleService;
import com.mfano.mems.auth.services.UserService;
import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.models.Announcement;
import com.mfano.mems.services.AnnouncementService;
import com.mfano.mems.utils.documents.FileUpload;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final RoleService roleService;
    private final FileUpload fileUpload;

    @GetMapping
    public String announcements(@AuthenticationPrincipal CustomUserDetails auth, Model model) {
        User user = userService.findByEmail(auth.getUsername());
        Role admin = roleService.findByName("ADMIN");

        List<Announcement> notices;
        Map<Long, Role> audience = new HashMap<>();

        if (user.getRoles().contains(admin)) {
            notices = announcementService.getAll(user.getBranch().getId());
        } else {
            notices = announcementService.findByAudience(user.getBranch().getId(), user.getId());
        }
        for(Announcement ann : notices){
            audience.put(ann.getAudience(), roleService.findById(ann.getAudience()));
        }

        userService.getPrices(auth, model);
        model.addAttribute("announcements", notices);
        model.addAttribute("audience", audience);
        model.addAttribute("roles", roleService.findAll());
        return "security/announcements";

    }

    @PostMapping("/save")
    public String saveAnnouncement(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Announcement announcement, 
        RedirectAttributes red, @RequestParam("file") MultipartFile upload) {
        try {
            if (upload != null && !upload.isEmpty()) {
                announcement.setUpload(fileUpload.uploadFile(upload, "profiles", red));
            }
            announcementService.save(auth.getBranch().getId(), announcement, red);
        } catch (Exception e) {
            e.printStackTrace();
            red.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/announcements";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnnouncement(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id, RedirectAttributes red) {
        try {
            announcementService.delete(auth.getBranch().getId(), id, red);
        } catch (Exception e) {
            red.addFlashAttribute("error", "Something went wrong, announcement could not delete.");
        }
        return "redirect:/announcements";
    }
}
