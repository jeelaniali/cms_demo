package com.hexaware.cms.service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import com.hexaware.cms.model.Role;
import com.hexaware.cms.dto.UserDTO;
import com.hexaware.cms.model.User;
import com.hexaware.cms.repository.UserRepository;
import com.hexaware.cms.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO dto) {
        
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

    	User user = modelMapper.map(dto, User.class);
    	
    	user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (dto.getRole() != null && dto.getRole().trim().equalsIgnoreCase("OFFICER")) {
            user.setRole(Role.OFFICER);
        } else {
            user.setRole(Role.USER);
        }

    	User savedUser = userRepository.save(user);

    	return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String loggedInEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        // If not station head, allow update only for their own profile
        if (loggedInUser.getRole() != Role.STATION_HEAD &&
            !loggedInUser.getId().equals(id)) {

            throw new RuntimeException("You can update only your own profile");
        }

        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setPan(userDTO.getPan());
        user.setAadhaar(userDTO.getAadhaar());

        User updated = userRepository.save(user);

        return modelMapper.map(updated, UserDTO.class);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }

    @Override
    public UserDTO updateProfile(String email, com.hexaware.cms.dto.UpdateProfileRequest request) {
        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));

        if (request.getName() != null) {
            loggedInUser.setName(request.getName());
        }
        if (request.getPhone() != null) {
            loggedInUser.setPhone(request.getPhone());
        }
        if (request.getAadhaar() != null) {
            loggedInUser.setAadhaar(request.getAadhaar());
        }
        if (request.getPan() != null) {
            loggedInUser.setPan(request.getPan());
        }

        User updated = userRepository.save(loggedInUser);
        return modelMapper.map(updated, UserDTO.class);
    }
}
