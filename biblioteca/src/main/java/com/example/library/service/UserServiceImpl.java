package com.example.library.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.example.library.repository.UserRepository;
import com.example.library.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.get();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public User updateUser(User user, Long id) {
        Optional<User> userOption = userRepository.findById(id);
        if (userOption.isEmpty()) {
            throw new NoSuchElementException();
        }
        User existingUser = userOption.get();
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        userRepository.save(existingUser);

        return existingUser;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.get();
    }

}
