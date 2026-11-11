package com.mfano.mems.auth.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mems.auth.models.Profile;
import com.mfano.mems.auth.repositories.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class ProfileService {
    private final ProfileRepository profileRepository;
    private Profile profile, existing;
    private final AuditService auditService;

    @Value("${app.upload-dir}")
    private String baseDirectory;
    // private String baseDirectory = "src/main/resources/static/image/profile/";

    // Get All Profiles
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    // Get Profile By Id
    public Profile findById(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    // Get Profile By Id
    public Profile findByUser(Long user) {
        return profileRepository.findByUser(user);
    }

    // Delete Profile
    public void delete(Long id) {
        profileRepository.deleteById(id);
    }

    // Save Profile
    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    // Update Profile
    public void update(Long userid, Profile profile, RedirectAttributes red, Long branch) {
        existing = findByUser(userid);

        if(existing != null){
        // update only editable fields
        existing.setFin(profile.getFin());
        existing.setLan(profile.getLan());
        existing.setOther(profile.getOther());
        existing.setAbout(profile.getAbout());
        existing.setGender(profile.getGender());
        existing.setSN(profile.getSN());
        existing.setDesignation(profile.getDesignation());
        existing.setCounty(profile.getCounty());
        existing.setAddress(profile.getAddress());
        existing.setPhone(profile.getPhone());
        existing.setTwitter(profile.getTwitter());
        existing.setFacebook(profile.getFacebook());
        existing.setInstagram(profile.getInstagram());
        existing.setLinkedin(profile.getLinkedin());

        save(existing);

        auditService.record(branch, "update_profile", "SUCCESS", "Updated profile.");
        red.addAttribute("message", "Profile updated successfully");
        }
        else {

        red.addAttribute("error", "No user account found.");
        }

    }

    // Update Profile Image
    public void updateProfileImage(Long userid, String file, RedirectAttributes red) throws IOException {
        existing = findByUser(userid);
        try {
            existing.setImage(file);
            save(existing);
            red.addFlashAttribute("message", "Profile image updated successsfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    // Update Profile Image
    public void deleteProfileImage(Long userid, RedirectAttributes red) throws IOException {
        existing = findByUser(userid);
        try {
            String image = existing.getImage();
            Path path = Path.of(baseDirectory, "profiles", image)
                    .toAbsolutePath()
                    .normalize();
            Files.delete(path);
            existing.setImage(null);
            save(existing);
            red.addFlashAttribute("message", "Profile image deleted successfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    // check profile
    public Profile checkProfile(Long userid) {
        profile = findByUser(userid);
        if (profile == null) {
            profile = new Profile();
            profile.setUser(userid);
            save(profile);
        }
        return profile;
    }

}
