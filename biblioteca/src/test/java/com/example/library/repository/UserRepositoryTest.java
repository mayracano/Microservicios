package com.example.library.repository;

import java.util.List;
import java.util.Optional;

import com.example.library.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveUserTest(){
        User user = new User();
        user.setId(2L);
        user.setEmail("myemail@gmail.com");
        user.setFirstName("My name");
        user.setLastName("my lastName");
        user.setPhoneNumber("1234567890");
        userRepository.save(user);
        Assertions.assertThat(user.getId()).isGreaterThan(1);
    }

    @Test
    public void getUserTest(){
        User user = userRepository.findById(1L).get();
        Assertions.assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    public void getListOfUsersTest(){
        List<User> users = userRepository.findAll();
        Assertions.assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void deleteUserTest(){
        userRepository.deleteById(1L);
        Optional<User> UserOptional = userRepository.findById(1L);
        Assertions.assertThat(UserOptional).isEmpty();
    }

    @Test
    public void updateUserTest() {
        User user = userRepository.findById(1L).get();
        user.setEmail("otherMail@gmail.com");
        User userUpdated =  userRepository.save(user);
        Assertions.assertThat(userUpdated.getEmail()).isEqualTo("otherMail@gmail.com");
    }

    @Test
    public void findUserByMail() {
        User user = userRepository.findByEmail("myemail@gmail.com").get();
        Assertions.assertThat(user.getId()).isEqualTo(1L);
    }
}
