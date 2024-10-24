package com.example.library.service;

import java.util.List;
import java.util.Optional;

import com.example.library.repository.UserRepository;
import com.example.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    private User user2;

    @BeforeEach
    public void setup(){
        user = new User();
        user.setId(1L);
        user.setEmail("myemail@gmail.com");
        user.setFirstName("My name");
        user.setLastName("my lastName");
        user.setPhoneNumber("1234567890");

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("youremail@gmail.com");
        user2.setFirstName("My name 2 ");
        user2.setLastName("my lastName 2");
        user2.setPhoneNumber("0123456789");
    }

    @Test
    public void testCreateUser() {
        given(userRepository.save(user)).willReturn(user);
        User savedUser = userService.createUser(user);
        System.out.println(savedUser);
        assertThat(savedUser).isNotNull();
    }

    @Test
    public void getAllUsers() {
        given(userRepository.findAll()).willReturn(List.of(user, user2));
        List<User> userList = userService.getAllUsers();
        assertThat(userList).isNotNull();
        assertThat(userList.size()).isGreaterThan(1);
    }

    @Test
    public void getUserById() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        User existingUser = userService.getUserById(user.getId());
        assertThat(existingUser).isNotNull();
    }

    @Test
    public void deleteUser() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        willDoNothing().given(userRepository).deleteById(user.getId());
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    public void updateUserTest() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setFirstName("My name");
        userToUpdate.setLastName("my lastName");
        userToUpdate.setPhoneNumber("1234567890");
        userToUpdate.setEmail("otherMail@gmail.com");
        User userUpdated =  userService.updateUser(userToUpdate, user.getId());
        assertThat(userUpdated.getEmail()).isEqualTo("otherMail@gmail.com");
    }

    @Test
    public void findUserByEmailTest() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        User existingUser = userService.getUserByEmail(user.getEmail());
        assertThat(existingUser).isNotNull();
    }
}
