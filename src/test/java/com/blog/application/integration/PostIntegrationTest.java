package com.blog.application.integration;

import com.blog.application.dto.PostRequest;
import com.blog.application.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")  // 👈 Enables application-test.properties


public class PostIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;
    private Long postId;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("postUser");
        register.setEmail("post@example.com");
        register.setPassword("postpass");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    token = objectMapper.readTree(json).get("token").asText();
                });
    }

    @Test
    @DisplayName("Create, update, delete post")
    void testPostCRUD() throws Exception {
        PostRequest create = new PostRequest();
        create.setTitle("My Post");
        create.setContent("Post content");

        // Create
        mockMvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    postId = objectMapper.readTree(json).get("id").asLong();
                });

        // Update
        PostRequest update = new PostRequest();
        update.setTitle("Updated Post");
        update.setContent("Updated content");

        mockMvc.perform(put("/posts/" + postId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post"));

        // Get by ID
        mockMvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Post"));

        // Delete
        mockMvc.perform(delete("/posts/" + postId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
