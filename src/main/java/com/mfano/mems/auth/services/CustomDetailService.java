package com.mfano.mems.auth.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mfano.mems.config.CustomUserDetails;
import com.mfano.mems.auth.models.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomDetailService implements UserDetailsService {
    private final UserService userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        return new CustomUserDetails(user);
    }
}
