package com.blog.application.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.blog.application.dto.CommentRequest;
import com.blog.application.dto.CommentResponse;
import com.blog.application.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comments management")
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping
    @Operation(summary = "Create a new comment", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request,
                                                        Authentication authentication) {
        CommentResponse response = commentService.createComment(request, authentication.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get comments by post ID")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@RequestParam Long postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Long id) {
        CommentResponse response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update comment", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,
                                                        @Valid @RequestBody CommentRequest request,
                                                        Authentication authentication) {
        CommentResponse response = commentService.updateComment(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}