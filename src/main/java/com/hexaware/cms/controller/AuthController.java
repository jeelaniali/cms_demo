package com.hexaware.cms.controller;

import com.hexaware.cms.dto.AuthRequest;
import com.hexaware.cms.dto.AuthResponse;
import com.hexaware.cms.dto.UserDTO;
import com.hexaware.cms.model.User;
import com.hexaware.cms.repository.UserRepository;
import com.hexaware.cms.security.CustomUserDetailsService;
import com.hexaware.cms.security.JwtUtil;
import com.hexaware.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public UserDTO register(@Valid @RequestBody UserDTO userDTO) {
        userDTO.setRole("USER");
        return userService.createUser(userDTO);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest authRequest) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
            )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String actualRole = user.getRole().name();

        String token = jwtUtil.generateToken(userDetails.getUsername(), actualRole);

        return new AuthResponse(token, actualRole, user.getName(), user.getEmail(), user.getId());
    }
}
