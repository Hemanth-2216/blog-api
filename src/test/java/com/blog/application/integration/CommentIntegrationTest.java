package com.blog.application.integration;

import com.blog.application.dto.CommentRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;
    private Long postId;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("commentUser");
        register.setEmail("comment@example.com");
        register.setPassword("password");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    token = objectMapper.readTree(json).get("token").asText();
                });

        PostRequest post = new PostRequest();
        post.setTitle("Comment Test Post");
        post.setContent("This post is for comment testing");

        mockMvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    postId = objectMapper.readTree(json).get("id").asLong();
                });
    }

    @Test
    @DisplayName("Add and retrieve comment")
    void testCreateAndGetComment() throws Exception {
        CommentRequest comment = new CommentRequest();
        comment.setContent("This is a comment.");
        comment.setPostId(postId);

        // Create
        mockMvc.perform(post("/comments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is a comment."))
                .andExpect(jsonPath("$.postId").value(postId));

        // Get comments by postId
        mockMvc.perform(get("/comments").param("postId", postId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
