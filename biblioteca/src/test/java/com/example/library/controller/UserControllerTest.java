package com.example.library.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.library.model.User;
import com.example.library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("myemail@gmail.com");
        user.setFirstName("My name");
        user.setLastName("my lastName");
        user.setPhoneNumber("1234567890");
    }

    @Test
    public void saveUserTest() throws Exception{
        given(userService.createUser(any(User.class))).willReturn(user);

        ResultActions response = mockMvc.perform(post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())));
    }

    @Test
    public void getUserTest() throws Exception{

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("myemail2@gmail.com");
        user2.setFirstName("My name 2 ");
        user2.setLastName("my lastName 2");
        user2.setPhoneNumber("0123456789");

        List<User> UsersList = new ArrayList<>();
        UsersList.add(user);
        UsersList.add(user2);
        given(userService.getAllUsers()).willReturn(UsersList);

        ResultActions response = mockMvc.perform(get("/api/users/"));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(UsersList.size())));

    }

    @Test
    public void getUserByIdTest() throws Exception{
        given(userService.getUserById(user.getId())).willReturn(user);
        ResultActions response = mockMvc.perform(get("/api/users/{id}", user.getId()));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())));
    }

    @Test
    public void updateUserTest() throws Exception{
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setLastName("my lastName");
        userToUpdate.setPhoneNumber("1234567890");
        userToUpdate.setFirstName("Max");
        userToUpdate.setEmail("max@gmail.com");

        Mockito.when(userService.updateUser(Mockito.any(User.class), Mockito.any(Long.class))).thenReturn(userToUpdate);

        ResultActions response = mockMvc.perform(put("/api/users/{id}", userToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToUpdate)));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(userToUpdate.getFirstName())))
                .andExpect(jsonPath("$.email", is(userToUpdate.getEmail())));
    }

    @Test
    public void deleteUserTest() throws Exception{
        willDoNothing().given(userService).deleteUser(user.getId());
        ResultActions response = mockMvc.perform(delete("/api/users/{id}", user.getId()));
        response.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void getUserByEmailTest() throws Exception{
        given(userService.getUserByEmail(user.getEmail())).willReturn(user);
        ResultActions response = mockMvc.perform(get("/api/users/email/{email}", user.getEmail()));
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.phoneNumber", is(user.getPhoneNumber())));
    }
}
