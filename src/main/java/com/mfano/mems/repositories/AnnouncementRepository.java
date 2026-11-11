package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfano.mems.models.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
   
    List<Announcement> findAllByBranch(Long branch);// for admin

    List<Announcement> findAll();

    @Query(value = "SELECT * FROM announcements WHERE branch_id = ?1 AND role_id IN (SELECT role_id FROM user_roles WHERE user_id = ?2)", nativeQuery = true)
    List<Announcement> getAnnouncements(Long branch, Long user);
}