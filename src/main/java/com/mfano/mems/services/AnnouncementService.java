package com.mfano.mems.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.services.AuditService;
import com.mfano.mems.models.Announcement;
import com.mfano.mems.repositories.AnnouncementRepository;
import com.mfano.mems.utils.documents.FileUpload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final AuditService auditService;

    public void save(Long auth, Announcement announcement,
            RedirectAttributes red) {
        if (announcement != null) {
            announcement.setActive(true);
            announcementRepository.save(announcement);
            auditService.record(auth, "CREATE_ANNOUNCEMENT", "SUCCESS", "Created announcement");
            red.addFlashAttribute("message", "Announcement saved successfully.");
        } else {
            red.addFlashAttribute("error", "Something went wrong, announcement could not save.");
        }
    }

    public void delete(Long auth, Long id, RedirectAttributes red) {
        Announcement existing = findById(id);
        if (existing != null) {
            announcementRepository.delete(existing);
            auditService.record(auth, "DELETE_ANNOUNCEMENT", "SUCCESS", "Deleted announcement");
            red.addFlashAttribute("message", "Announcement deleted successfully.");
        } else {
            red.addFlashAttribute("error", "No announcement found to delete .");
        }
    }

    private Announcement findById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }

    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }// for hq user

    public List<Announcement> getAll(Long branch) {
        return announcementRepository.findAllByBranch(branch);
    }// admin of branch

    public List<Announcement> findByAudience(Long branch, Long id) {
        return announcementRepository.getAnnouncements(branch, id);
    }

    public List<Announcement> findActiveByAudience(Long branch, Long id) {
        return announcementRepository.getAnnouncements(branch, id).stream()
                .filter(entry -> entry.isActive() == true)// add filter logic
                .toList();
    }
}
