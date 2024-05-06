package kissshot1104.personal.blog.post_image.repository;

import kissshot1104.personal.blog.post_image.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
