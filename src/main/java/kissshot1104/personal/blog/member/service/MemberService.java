package kissshot1104.personal.blog.member.service;

import java.net.URL;
import java.util.UUID;
import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.global.util.s3.S3ImageUploader;
import kissshot1104.personal.blog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final S3ImageUploader s3ImageUploader;

    public void checkAuthorizedMember(final Member author, final Member requester) {
        if (author == null || requester == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (author != requester) {
            throw new AuthException(ErrorCode.UNAUTHORIZED_USER);
        }
    }

    @Transactional
    public void modifyMember(final MultipartFile image, final Member member) {
        final String originalFilename = image.getOriginalFilename();
        final String filename = UUID.randomUUID() + "_" + originalFilename;

        // S3에 파일 업로드
        s3ImageUploader.uploadImage(image, filename);

        final URL imagePath = s3ImageUploader.getImageUrl(filename);
        member.modifyProfileImagePath(imagePath.toString());
    }

    public String viewProfileImage(final Member member) {
        return member.getProfileImagePath();
    }
}
