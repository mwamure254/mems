package com.mfano.mems.auth.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.Branch;
import com.mfano.mems.auth.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByEmailAndPassword(String email, String password);

    List<User> findAllByOrderByCreatedAtDesc();

    List<User> findByBranch(Branch storeId);

}
