package kissshot1104.personal.blog.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import kissshot1104.personal.blog.category.dto.request.ModifyCategoryRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Getter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Long categoryDepth;

    public void modifyCategory(final Category parentCategory,
                               final String categoryName) {
        this.category = parentCategory;
        this.categoryName = categoryName;
    }

    public void modifyCategoryDepth(final Long categoryDepth) {
        this.categoryDepth = categoryDepth;
    }
}
