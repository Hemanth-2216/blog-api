package com.blog.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.blog.application.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    long countByPostId(Long postId); // ✅ FIXED: changed from int to long
}
