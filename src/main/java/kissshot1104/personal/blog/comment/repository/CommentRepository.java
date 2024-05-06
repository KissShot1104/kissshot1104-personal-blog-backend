package kissshot1104.personal.blog.comment.repository;

import kissshot1104.personal.blog.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
