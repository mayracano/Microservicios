package com.example.library.service;

import java.util.List;

import com.example.library.model.User;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);

    User updateUser(User user, Long id);

    User getUserByEmail(String email);
}
