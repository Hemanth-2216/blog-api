package com.blog.application.service;

import com.blog.application.dto.CommentRequest;
import com.blog.application.dto.CommentResponse;
import com.blog.application.entity.Comment;
import com.blog.application.entity.Post;
import com.blog.application.entity.User;
import com.blog.application.exception.ResourceNotFoundException;
import com.blog.application.exception.UnauthorizedException;
import com.blog.application.repository.CommentRepository;
import com.blog.application.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    private User mockUser;
    private Post mockPost;
    private Comment mockComment;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("john");

        mockPost = new Post();
        mockPost.setId(10L);
        mockPost.setAuthor(mockUser);
        mockPost.setTitle("Post Title");

        mockComment = new Comment();
        mockComment.setId(100L);
        mockComment.setPost(mockPost);
        mockComment.setAuthor(mockUser);
        mockComment.setContent("Nice article!");
        mockComment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create comment - success")
    void testCreateComment() {
        CommentRequest request = new CommentRequest();
        request.setContent("Nice article!");
        request.setPostId(10L);

        when(userService.findByUsername("john")).thenReturn(mockUser);
        when(postRepository.findById(10L)).thenReturn(Optional.of(mockPost));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponse response = commentService.createComment(request, "john");

        assertEquals("Nice article!", response.getContent());
        assertEquals("john", response.getAuthorUsername());
    }

    @Test
    @DisplayName("Get comments by postId - success")
    void testGetCommentsByPostId() {
        when(postRepository.existsById(10L)).thenReturn(true);
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(mockComment));

        List<CommentResponse> result = commentService.getCommentsByPostId(10L);

        assertEquals(1, result.size());
        assertEquals("Nice article!", result.get(0).getContent());
    }

    @Test
    @DisplayName("Get comments by postId - post not found")
    void testGetCommentsByPostId_NotFound() {
        when(postRepository.existsById(10L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByPostId(10L));
    }

    @Test
    @DisplayName("Get comment by ID - success")
    void testGetCommentById() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(mockComment));

        CommentResponse response = commentService.getCommentById(100L);

        assertEquals("Nice article!", response.getContent());
    }

    @Test
    @DisplayName("Get comment by ID - not found")
    void testGetCommentById_NotFound() {
        when(commentRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(100L));
    }

    @Test
    @DisplayName("Update comment - success")
    void testUpdateComment() {
        CommentRequest request = new CommentRequest();
        request.setContent("Updated comment");
        request.setPostId(10L);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponse response = commentService.updateComment(100L, request, "john");

        assertEquals("Nice article!", response.getContent()); // mock still returns old content
        assertEquals("john", response.getAuthorUsername());
    }

    @Test
    @DisplayName("Update comment - unauthorized")
    void testUpdateComment_Unauthorized() {
        mockComment.getAuthor().setUsername("otherUser");

        CommentRequest request = new CommentRequest();
        request.setContent("Hack comment");
        request.setPostId(10L);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(mockComment));

        assertThrows(UnauthorizedException.class, () -> commentService.updateComment(100L, request, "john"));
    }

    @Test
    @DisplayName("Delete comment - success")
    void testDeleteComment() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(mockComment));
        commentService.deleteComment(100L, "john");
        verify(commentRepository).delete(mockComment);
    }

    @Test
    @DisplayName("Delete comment - unauthorized")
    void testDeleteComment_Unauthorized() {
        mockComment.getAuthor().setUsername("someoneElse");

        when(commentRepository.findById(100L)).thenReturn(Optional.of(mockComment));

        assertThrows(UnauthorizedException.class, () -> commentService.deleteComment(100L, "john"));
    }
}
