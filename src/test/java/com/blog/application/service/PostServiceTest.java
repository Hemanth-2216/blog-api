package com.blog.application.service;

import com.blog.application.dto.PostRequest;
import com.blog.application.dto.PostResponse;
import com.blog.application.entity.Post;
import com.blog.application.entity.User;
import com.blog.application.exception.ResourceNotFoundException;
import com.blog.application.exception.UnauthorizedException;
import com.blog.application.repository.CommentRepository;
import com.blog.application.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private User mockUser;
    private Post mockPost;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("john");

        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setTitle("Test Title");
        mockPost.setContent("Test Content");
        mockPost.setAuthor(mockUser);
        mockPost.setCreatedAt(LocalDateTime.now());
        mockPost.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create Post - success")
    void testCreatePost() {
        PostRequest request = new PostRequest();
        request.setTitle("New Post");
        request.setContent("This is a new post.");

        when(userService.findByUsername("john")).thenReturn(mockUser);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(commentRepository.countByPostId(1L)).thenReturn(0L); // long literal

        PostResponse response = postService.createPost(request, "john");

        assertEquals("Test Title", response.getTitle());
        assertEquals("john", response.getAuthorUsername());
    }

    @Test
    @DisplayName("Get all posts - paginated")
    void testGetAllPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        when(postRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(mockPost)));
        when(commentRepository.countByPostId(1L)).thenReturn(2L); // long

        Page<PostResponse> result = postService.getAllPosts(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Test Title", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Get post by ID - success")
    void testGetPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(commentRepository.countByPostId(1L)).thenReturn(1L); // long

        PostResponse response = postService.getPostById(1L);

        assertEquals("Test Title", response.getTitle());
        assertEquals("john", response.getAuthorUsername());
    }

    @Test
    @DisplayName("Get post by ID - not found")
    void testGetPostById_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(1L));
    }

    @Test
    @DisplayName("Update post - success")
    void testUpdatePost_Success() {
        PostRequest request = new PostRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(commentRepository.countByPostId(1L)).thenReturn(0L); // long

        PostResponse response = postService.updatePost(1L, request, "john");

        assertEquals("Test Title", response.getTitle());
        assertEquals("john", response.getAuthorUsername());
    }

    @Test
    @DisplayName("Update post - unauthorized")
    void testUpdatePost_Unauthorized() {
        mockPost.getAuthor().setUsername("otherUser");

        PostRequest request = new PostRequest();
        request.setTitle("New Title");
        request.setContent("New Content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        assertThrows(UnauthorizedException.class, () -> postService.updatePost(1L, request, "john"));
    }

    @Test
    @DisplayName("Delete post - success")
    void testDeletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        postService.deletePost(1L, "john");

        verify(postRepository).delete(mockPost);
    }

    @Test
    @DisplayName("Delete post - unauthorized")
    void testDeletePost_Unauthorized() {
        mockPost.getAuthor().setUsername("someoneElse");

        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));

        assertThrows(UnauthorizedException.class, () -> postService.deletePost(1L, "john"));
    }

    @Test
    @DisplayName("Search posts by keyword")
    void testSearchPosts() {
        Pageable pageable = PageRequest.of(0, 10);

        when(postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase("test", pageable))
                .thenReturn(new PageImpl<>(List.of(mockPost)));
        when(commentRepository.countByPostId(1L)).thenReturn(3L); // long

        Page<PostResponse> result = postService.searchPosts("test", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Test Title", result.getContent().get(0).getTitle());
    }
}
