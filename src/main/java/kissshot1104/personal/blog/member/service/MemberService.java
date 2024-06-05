package kissshot1104.personal.blog.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import kissshot1104.personal.blog.global.exception.AuthException;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.member.dto.request.ModifyProfileRequest;
import kissshot1104.personal.blog.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;


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
        uploadImage(image, filename);
        final URL imagePath = s3Client.getUrl(bucketName, filename);
        member.modifyProfileImagePath(imagePath.toString());
    }

    private void uploadImage(final MultipartFile image, final String filename) {
        try {
            InputStream fileInputStream = image.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, filename, fileInputStream, metadata));
            fileInputStream.close();
        } catch (IOException io) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public String viewProfileImage(final Member member) {
        return member.getProfileImagePath();
    }
}
