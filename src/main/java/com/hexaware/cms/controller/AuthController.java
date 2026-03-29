package com.hexaware.cms.controller;

import com.hexaware.cms.dto.AuthRequest;
import com.hexaware.cms.dto.AuthResponse;
import com.hexaware.cms.security.CustomUserDetailsService;
import com.hexaware.cms.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {

        // 1. Verify email and password
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),      // ← email now
                authRequest.getPassword()
            )
        );

        // 2. Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        // 3. Get role (strip "ROLE_" prefix before putting in token)
        String role = userDetails.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "");

        // 4. Generate token
        String token = jwtUtil.generateToken(userDetails.getUsername(), role);

        // 5. Return token
        return new AuthResponse(token);
    }
}