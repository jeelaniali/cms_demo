package com.hexaware.cms.security;

import com.hexaware.cms.model.User;
import com.hexaware.cms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Find user by EMAIL (not username)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // .name() converts enum Role.OFFICER → String "OFFICER"
        String role = "ROLE_" + user.getRole().name();  // ← .name() because role is an Enum

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),     // ← email is the login identifier
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}