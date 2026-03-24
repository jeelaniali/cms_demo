package com.hexaware.cms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hexaware.cms.dto.UserDTO;
import com.hexaware.cms.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Only STATION_HEAD can create users/officers
    @PreAuthorize("hasRole('STATION_HEAD')")
    @PostMapping
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    // Only STATION_HEAD can view all users
    @PreAuthorize("hasRole('STATION_HEAD')")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // USER can view their profile, STATION_HEAD can view anyone
    @PreAuthorize("hasAnyRole('USER','OFFICER','STATION_HEAD')")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // USER / OFFICER can update their profile
    // STATION_HEAD can update anyone
    @PreAuthorize("hasAnyRole('USER','OFFICER','STATION_HEAD')")
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id,
                              @Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    // Only STATION_HEAD can delete users/officers
    @PreAuthorize("hasRole('STATION_HEAD')")
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User deleted successfully";
    }
}