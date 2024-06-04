package kissshot1104.personal.blog.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kissshot1104.personal.blog.category.entity.Category;
import kissshot1104.personal.blog.global.BaseEntity;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post.dto.request.PostModifyRequest;
import kissshot1104.personal.blog.post_image.entity.PostImage;
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
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String postPassword;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PostSecurity postSecurity;

    public void modifyPost(final PostModifyRequest postModifyRequest) {
        this.title = postModifyRequest.title();
        this.content = postModifyRequest.content();
        this.postPassword = postModifyRequest.postPassword();
        this.postSecurity = PostSecurity.checkPostSecurity(postModifyRequest.postSecurity());
    }
}
