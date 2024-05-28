package kissshot1104.personal.blog.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kissshot1104.personal.blog.global.BaseEntity;
import kissshot1104.personal.blog.member.dto.request.ModifyProfileRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(callSuper = false)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String nickName;
    private String profileImagePath;
    private String roles;

    @Builder
    private Member(final String username,
                   final String password,
                   final String nickName,
                   final String profileImagePath,
                   final String roles) {
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.profileImagePath = profileImagePath;
        this.roles = roles;
    }

    public void modifyProfileImagePath(final String imagePath) {
        this.profileImagePath = profileImagePath;
    }
}
