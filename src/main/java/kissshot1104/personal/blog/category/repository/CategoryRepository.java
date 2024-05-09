package kissshot1104.personal.blog.category.repository;

import java.util.List;
import kissshot1104.personal.blog.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIdNotIn(final List<Long> idList);

    List<Category> findAllByCategory(final Category category);
}
