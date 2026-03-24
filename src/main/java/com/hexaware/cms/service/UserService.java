package com.hexaware.cms.service;

import java.util.List;
import com.hexaware.cms.dto.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);
}