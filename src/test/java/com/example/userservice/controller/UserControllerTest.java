package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean UserService service;

    @Test
    void createUser() throws Exception {
        UserDto in = new UserDto();
        in.setName("Ivan");
        in.setEmail("ivan@example.com");
        in.setAge(30);

        UserDto out = new UserDto();
        out.setId(1L);
        out.setName(in.getName());
        out.setEmail(in.getEmail());
        out.setAge(in.getAge());

        when(service.create(any())).thenReturn(out);

        mvc.perform(post("/api/users")
                .contentType("application/json")
                .content(om.writeValueAsString(in)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/users/1"))
            .andExpect(jsonPath("$.id").value(1));

        verify(service, times(1)).create(any());
    }
}
