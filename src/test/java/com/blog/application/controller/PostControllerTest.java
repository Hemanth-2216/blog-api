package com.blog.application.controller;

import com.blog.application.dto.PostRequest;
import com.blog.application.dto.PostResponse;
import com.blog.application.exception.GlobalExceptionHandler;
import com.blog.application.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostResponse createMockPostResponse() {
        PostResponse post = new PostResponse();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setAuthorUsername("testuser");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setCommentCount(5);
        return post;
    }

    @Test
    @DisplayName("✅ POST /posts - Create Post Successfully")
    @WithMockUser(username = "testuser")
    void testCreatePost() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("New Post");
        request.setContent("Post content");

        PostResponse response = createMockPostResponse();

        Mockito.when(postService.createPost(any(PostRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.authorUsername").value("testuser"));
    }

    @Test
    @DisplayName("✅ GET /posts - Get All Posts With Pagination")
    void testGetAllPosts() throws Exception {
        PostResponse post = createMockPostResponse();
        Page<PostResponse> page = new PageImpl<>(List.of(post));

        Mockito.when(postService.getAllPosts(any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/posts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Title"));
    }

    @Test
    @DisplayName("✅ GET /posts/{id} - Get Post by ID")
    void testGetPostById() throws Exception {
        PostResponse post = createMockPostResponse();

        Mockito.when(postService.getPostById(1L)).thenReturn(post);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    @DisplayName("✅ PUT /posts/{id} - Update Post")
    @WithMockUser(username = "testuser")
    void testUpdatePost() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        PostResponse updatedPost = createMockPostResponse();
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");

        Mockito.when(postService.updatePost(eq(1L), any(PostRequest.class), eq("testuser")))
                .thenReturn(updatedPost);

        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    @DisplayName("✅ DELETE /posts/{id} - Delete Post")
    @WithMockUser(username = "testuser")
    void testDeletePost() throws Exception {
        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(postService).deletePost(1L, "testuser");
    }

    @Test
    @DisplayName("✅ GET /posts/search - Search Posts By Keyword")
    void testSearchPosts() throws Exception {
        PostResponse post = createMockPostResponse();
        Page<PostResponse> page = new PageImpl<>(List.of(post));

        Mockito.when(postService.searchPosts(eq("Test"), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/posts/search")
                        .param("keyword", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Title"));
    }

    @Test
    @DisplayName("❌ POST /posts - Validation Fail (Blank Title)")
    @WithMockUser(username = "testuser")
    void testCreatePost_ValidationFail() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("");  // ❌ Invalid
        request.setContent("Valid Content");

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").value("Invalid input data"))
                .andExpect(jsonPath("$.validationErrors.title").value("Title must not be blank"));
    }
}
