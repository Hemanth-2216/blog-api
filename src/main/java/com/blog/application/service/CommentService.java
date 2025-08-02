package com.blog.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.blog.application.dto.CommentRequest;
import com.blog.application.dto.CommentResponse;
import com.blog.application.entity.Comment;
import com.blog.application.entity.Post;
import com.blog.application.entity.User;
import com.blog.application.exception.ResourceNotFoundException;
import com.blog.application.exception.UnauthorizedException;
import com.blog.application.repository.CommentRepository;
import com.blog.application.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    
    public CommentResponse createComment(CommentRequest request, String username) {
        User author = userService.findByUsername(username);
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + request.getPostId()));
        
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setAuthor(author);
        
        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }
    
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        return convertToResponse(comment);
    }
    
    public CommentResponse updateComment(Long id, CommentRequest request, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to update this comment");
        }
        
        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return convertToResponse(updatedComment);
    }
    
    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
        
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to delete this comment");
        }
        
        commentRepository.delete(comment);
    }
    
    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setPostId(comment.getPost().getId());
        response.setAuthorUsername(comment.getAuthor().getUsername());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
