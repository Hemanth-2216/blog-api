package com.blog.application.controller;

import com.blog.application.dto.CommentRequest;
import com.blog.application.dto.CommentResponse;
import com.blog.application.exception.GlobalExceptionHandler;
import com.blog.application.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)  // ✅ disables Spring Security filters during test
@Import(GlobalExceptionHandler.class)      // ✅ hooks in your custom validation error format
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /comments - success with authenticated user")
    @WithMockUser(username = "testuser")
    void testCreateComment_Success() throws Exception {
        CommentRequest request = new CommentRequest("Nice post!", 1L);
        CommentResponse response = new CommentResponse(1L, "Nice post!", 1L, "testuser", LocalDateTime.now());

        Mockito.when(commentService.createComment(any(CommentRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Nice post!"))
                .andExpect(jsonPath("$.authorUsername").value("testuser"));
    }

    @Test
    @DisplayName("GET /comments?postId=x - fetch comments for post")
    void testGetCommentsByPostId() throws Exception {
        CommentResponse comment = new CommentResponse(1L, "Test comment", 1L, "user", LocalDateTime.now());
        Mockito.when(commentService.getCommentsByPostId(1L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/comments").param("postId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test comment"))
                .andExpect(jsonPath("$[0].authorUsername").value("user"));
    }

    @Test
    @DisplayName("GET /comments/{id} - fetch single comment by ID")
    void testGetCommentById() throws Exception {
        CommentResponse comment = new CommentResponse(1L, "Comment content", 2L, "user2", LocalDateTime.now());
        Mockito.when(commentService.getCommentById(1L)).thenReturn(comment);

        mockMvc.perform(get("/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Comment content"))
                .andExpect(jsonPath("$.authorUsername").value("user2"));
    }

    @Test
    @DisplayName("PUT /comments/{id} - update comment (auth user)")
    @WithMockUser(username = "testuser")
    void testUpdateComment() throws Exception {
        CommentRequest request = new CommentRequest("Updated content", 1L);
        CommentResponse response = new CommentResponse(1L, "Updated content", 1L, "testuser", LocalDateTime.now());

        Mockito.when(commentService.updateComment(eq(1L), any(CommentRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(put("/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.authorUsername").value("testuser"));
    }

    @Test
    @DisplayName("DELETE /comments/{id} - delete comment (auth user)")
    @WithMockUser(username = "testuser")
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete("/comments/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(commentService).deleteComment(1L, "testuser");
    }

    @Test
    @DisplayName("POST /comments - validation fail (missing content and postId)")
    @WithMockUser(username = "testuser")
    void testCreateComment_ValidationFail() throws Exception {
        CommentRequest invalidRequest = new CommentRequest();  // No content, no postId

        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Invalid input data"))
                .andExpect(jsonPath("$.validationErrors.content").exists())
                .andExpect(jsonPath("$.validationErrors.postId").exists());
    }
}
