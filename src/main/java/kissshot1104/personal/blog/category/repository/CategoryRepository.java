package kissshot1104.personal.blog.category.repository;

import kissshot1104.personal.blog.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
