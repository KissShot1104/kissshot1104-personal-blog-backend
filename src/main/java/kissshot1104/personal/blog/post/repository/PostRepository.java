package kissshot1104.personal.blog.post.repository;


import kissshot1104.personal.blog.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
